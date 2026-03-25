<script lang="ts">
  import { type Order } from "./Order";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import {
    formatPrice,
    formattedTime,
    getOrderStatusColor,
    getOrderStatusLabel,
  } from "../../utils/formatting";
  import { showSuccessToast } from "../../core/utils/toaster";
  import L from 'leaflet';
  import type { Map as LeafletMap } from 'leaflet'; // Uvezi tip da se ne meša sa JS Map
  import 'leaflet-routing-machine';

  document.title = "Narudžba | Barbacoa";

  export let ID: number | string;

  let loading: boolean = false;
  let error: string | null = null;
  let rows = 2;
  let map: LeafletMap; // Ovde definišemo varijablu
  let mapContainer: HTMLElement;
  let marker: any;
  let routingControl: any;

  let formData: Partial<Order> = {
    code: "",
    status: "",
    paymentMethod: "",
    taxPercent: 0,
    discount: 0,
    currency: "",
    comment: "",
    created: null,
    modified: null,
    servedAt: null,
    upripremiAt: null,
    spremnoAt: null,
    address: "",
    phone: ""
  };

  $: {
    if ($params?.id) 
    {
      ID = Number($params.id);
      fetchOrder(ID); // reactively fetch when id changes
    }
  }

  onMount( async () =>
  {
    fetchOrder(ID);
    // inicijalizacija mape (Leaflet mora u onMount jer mu treba DOM)
    initMap();
  });

  async function initMap() 
  {    
    map = L.map(mapContainer).setView([45.35439056, 14.3615457], 16); // Barbacoa adresa (ex furinac)
    /*** Zoom level
      1 je ceo svet (vidiš kontinente).
      13 je  nivo grada.
      18-20 je maksimalni zum. Na 20 bukvalno vidiš krov zgrade ili pojedinačno drvo u dvorištu.
    */
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(map);    
  }

  // Umesto window.drawRoute, napravi običnu funkciju
  async function searchAddressAndDrawRoute(address: string | undefined) {
    if (!address || address.length < 3) return;

    try {
        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`;
        const response = await fetch(url);
        const data = await response.json() as any[];

        if (data.length > 0) {
            const { lat, lon } = data[0];
            const targetLat = parseFloat(lat);
            const targetLon = parseFloat(lon);

            // 1. Pomeri mapu na lokaciju kupca
            map.setView([targetLat, targetLon], 17);

            // 2. Nacrtaj rutu od restorana do te lokacije
            drawRoute(targetLat, targetLon);
            
            // 3. (Opciono) Ako želiš i marker na kući kupca
            if (marker) {
                marker.setLatLng([targetLat, targetLon]);
            } else {
                marker = L.marker([targetLat, targetLon]).addTo(map);
            }
        }
    } catch (error) {
        console.error("Greška pri pretrazi ili crtanju rute:", error);
    }
}

function drawRoute(endLat: number, endLon: number) {
    // Tvoj fiksni start (Restoran)
    const startLat = 45.35439056;
    const startLon = 14.3615457;

    if (!map) return;

    if (routingControl) {
        map.removeControl(routingControl);
    }

    routingControl = (L as any).Routing.control({
        waypoints: [
            L.latLng(startLat, startLon),
            L.latLng(endLat, endLon)
        ],
        lineOptions: {
            styles: [{ color: '#3b82f6', weight: 6 }]
        } as any,
        routeWhileDragging: true,
        addWaypoints: false,
        show: true // tekstualni panel s uputstvima
    }).addTo(map);
}

  async function fetchOrder(id: string | number) 
  {
    startLoadingAnimation();

    try 
    {
      let data = await api<Order>("/orders/" + id, {
        method: "GET",
      });

      formData = data;
      document.title = `Narudžba ${formData.code} | Barbacoa`;
      error = null;
      if(formData.comment && formData.comment.toString.length > -1 )
        rows = 5;
      
      if(formData.address){
        searchAddressAndDrawRoute(formData.address);
      }
    } 
    catch (err) 
    {
      processError(err);
    } 
    finally 
    {
      removeLoadingAnimation();
    }
  }

  function processError(err: any) 
  {
    formData = {};
    error =
      (err as Error)?.message ||
      "Order not found or an unknown error occurred. ERR_90";
  }

  async function handleSubmit() 
  {
    try 
    {
      loading = true;
      const response = await api<Order>(`/orders/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Order saved");
      fetchOrder(ID);
    } 
    catch (err) 
    {
      console.log(err);
      showErrorInModal({ message: (err as Error).message });
    } 
    finally 
    {
      loading = false;
    }
  }

  function showErrorInModal(error: any): void 
  {
    //Note to myself: moguce da nam vecina ovoga zapravo netreba
    const contentEl = document.getElementById("modal-content");
    const dialogEl = document.getElementById("modal") as HTMLDialogElement;

    if (contentEl) {
      let errorMessage = "An error occurred";

      if (typeof error === "string") {
        if (error.startsWith("Error: {")) {
          try {
            const jsonPart = error.substring(7);
            const parsedError = JSON.parse(jsonPart);
            errorMessage = parsedError.message || errorMessage;
          } catch (e) {
            errorMessage = error;
          }
        } else {
          errorMessage = error;
        }
      } else if (error && error.message) {
        errorMessage = error.message;
      }

      contentEl.textContent = errorMessage;
    }

    if (dialogEl) {
      dialogEl.showModal();
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === "Enter") {
      event.preventDefault();
      handleSubmit();
    }
  }

  function cancelEditing() {
    window.location.href = "#/orders";
  }

  const inputSkeletons =
    "#orderForm input, #orderForm select, #orderForm textarea";

  function startLoadingAnimation(): void {
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.add("skeleton");
      input.disabled = true;
    });
  }

  function removeLoadingAnimation(): void {
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "none";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.remove("skeleton");
      input.disabled = false;
    });
  }
  
 
</script>

<div class="relative w-full scale-up-center-normal">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else if error}
    <div class="wrapper">
      <div class="max-w-[520px] wrap-anywhere">
        <ErrorDiv {error} />
      </div>
    </div>
  {:else}
    {#if loading}
      <div class="fixed inset-0 z-10 flex items-center justify-center">
        <div
          class="rounded-2xl max-w-5xl w-full mx-auto flex flex-col items-center"
        >
          <span
            class="loading loading-infinity mb-2 text-blue-500"
            style="width: 4rem; height: 4rem;"
          ></span>
        </div>
      </div>
    {/if}

    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
    <form
      on:submit|preventDefault={handleSubmit}
      on:keydown={handleKeydown}
      id="orderForm"
      class="max-w-7xl mx-auto bg-base-100 rounded-lg p-2 w-full space-y-8"
    >
      <!-- Order Information Section -->
      <div
        class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-4 pb-4 items-start border-b-2 border-secondary/10"
      >
        <div>
          <h4 class="font-mono text-2xl">#{formData.code}</h4>
        </div>

        <div class="flex gap-6">
          <h6 class="font-mono text-md font-semibold">
            <i class="fas fa-user text-primary/60">&nbsp;</i>
            {formData.user?.username}
          </h6>
          <h6 class="font-mono text-md font-semibold">
            <i class="fas fa-chair text-primary/60">&nbsp;</i>
            {formData.stol}
          </h6>
        </div>

        <div class="absolute top-0 right-0">
          {#if formData.status}
            <span
              class="badge badge-soft badge-{getOrderStatusColor(
                formData.status
              )} font-mono badge-lg uppercase font-bold"
            >
              {getOrderStatusLabel(formData.status)}
            </span>
          {/if}
        </div>
      </div>

      <div class="w-full p-0 m-0 pb-4 mb-4 border-b-2 border-secondary/10">
        <div class="rounded-lg p-">
          <ul
            class="steps steps-vertical lg:steps-horizontal w-full text-primary/80"
          >
            <li class="step step-secondary">
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-edit"></i>Primljeno</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.created)}</span
                >
              </span>
            </li>
            <li
              class="step"
              class:step-secondary={formData.upripremiAt != null}
            >
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-fire"></i>U pripremi</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.upripremiAt)}</span
                >
              </span>
            </li>
            <li class="step" class:step-secondary={formData.spremnoAt != null}>
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-check"></i>Spremno</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.spremnoAt)}</span
                >
              </span>
            </li>
            <li
              class="step"
              class:step-secondary={formData.servedAt != null}
            >
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-check-double"></i>Dostavljeno</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.servedAt)}</span
                >
              </span>
            </li>
          </ul>
        </div>
      </div>

      <!-- Order Items Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-4">
        <div class="lg:col-span-2">
          <div class="space-y-4">
            {#if formData.items && formData.items.length > 0}
              <div class="divide-y divide-primary/10">
                {#each formData.items as item, index (item.id || index)}
                  <div
                    class="py-1 grid grid-cols-1 sm:grid-cols-3 gap-0 items-center"
                  >
                    <div class="sm:col-span-2 flex flex-col gap-0.5">
                      <div class="flex items-center gap-1">
                        <p
                          class="text-sm sm:text-base font-medium text-primary/80 px-1"
                        >
                          {item.quantity} x {item.product.name}
                          {#if item.quantity > 1}
                            <span class="text-xs text-primary/40"> ({formatPrice(item.product.basePrice)}) </span>
                          {/if}
                        </p>
                      </div>
                    </div>

                    <div class="text-right pt-0 sm:pt-0 relative mt-[0.14rem]">
                      <span
                        class="text-sm sm:text-base text-primary/80 font-mono"
                      >
                        {formatPrice(item.quantity * item.product.basePrice)}
                      </span>
                    </div>
                  </div>
                {/each}
                <div
                  style="align-items: end;display:grid;align-content: end; text-align: right; "
                  class="p-0 mr-0"
                >
                  <span
                    class="text-2xl font-bold text-primary p0 pt-2 font-mono"
                    >{formatPrice(formData.price)}</span
                  >
                </div>
              </div>
            {:else}
              <div class="text-center py-8 text-gray-500">
                <i class="fas fa-box-open text-4xl mb-4"></i>
                <p>No items in this order</p>
              </div>
            {/if}
          </div>
        </div>
      </div>

      {#if (formData.comment)}
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-1 mb-1">
        <div class="lg:col-span-2">
          <div class="w-full">
            <label for="notes" class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fas fa-comment text-primary/60">&nbsp;</i>
              Napomena
            </label>
            <textarea
              id="notes"
              class="pgs-input resize-vertical rounded-md"
              style="background-color: var(--color-base-200);"
              bind:value={formData.comment}
              rows="{formData.comment ? 4 : 2}"
            ></textarea>
          </div>
        </div>
      </div>
      {/if}

      <div class="grid grid-cols-1 md:grid-cols-1 gap-4 mb-4">
        <div>
          <label for="phone" class="block text-sm font-medium text-gray-700 mb-2">
            <i class="fas fa-phone text-primary/60">&nbsp;</i>Telefon
          </label>
          <input
            id="phone"
            type="tel"
            class="pgs-input rounded-md w-full"
            style="background-color: var(--color-base-200);"
            bind:value={formData.phone}
          />
        </div>
        <div>
          <label for="address" class="block text-sm font-medium text-gray-700 mb-2">
            <i class="fas fa-home text-primary/60">&nbsp;</i>Adresa
          </label>
          <textarea
            id="address"
            class="pgs-input resize-vertical rounded-md w-full"
            style="background-color: var(--color-base-200);"
            bind:value={formData.address}
            rows="{formData.address ? 3 : 2}"
          ></textarea>
        </div>        
      </div>
      
    <button 
      type="button"      
      on:click={() => searchAddressAndDrawRoute(formData.address)}
      class="btn btn-ghost btn-md m-0.5"
    >
      <i class="fas fa-location-arrow text-primary/60">&nbsp;</i>Nađi put
    </button>
    
      <div bind:this={mapContainer} class="w-full h-164 rounded-md shadow-inner"></div>

      <div class="grid grid-cols-1 hidden">
        <div class="flex justify-end gap-3 pt-4">
          <button
            type="button"
            on:click={cancelEditing}
            class="btn btn-outline btn-lg m-3"
          >
            Close
          </button>
          <button type="submit" class="btn btn-primary btn-lg m-3">
            Save
          </button>
        </div>
      </div>
    </form>
  {/if}
</div>

<!-- Error Modal -->
<dialog id="modal" class="modal modal-bottom sm:modal-middle w-full">
  <div class="modal-box" style="min-width: min-content;">
    <h3 class="text-lg font-bold text-red-700" id="modal-title">Error</h3>
    <p class="py-4 text-red-600" id="modal-content">
      An error occurred while processing your request.
    </p>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>

<style>
  .wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 70vh;
    text-align: center;
    padding: 2rem;
    background-color: transparent;
  }
</style>
