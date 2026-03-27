<script lang="ts">
  import type { Order } from "./Order";
  import { onMount } from "svelte";
  import { auth } from "../../core/services/SessionStore";
  import { get } from "svelte/store";
  import {
    formatCode,
    isNew,
    formatTime2,
    formatPrice,
    formatPriceRaw,
    getOrderCardBgClass,
  } from "../../utils/formatting";
  import axios from "axios";
  import { showErrorModal } from "../../utils/modal";
  import StatusMenu from "./StatusMenu.svelte";
  import OrderDetails from "./OrderDetails.svelte";
  import OrderButtonReady from "./OrderButtonReady.svelte";
  import OrderButtonInPreparation from "./OrderButtonInPreparation.svelte";
  import OrderButtonServed from "./OrderButtonServed.svelte";
  import ProductPage from "../products/ProductPage.svelte";
  import { fly } from "svelte/transition";

  export let order: Order;
  export let liteView = false;

  document.title = "Narudžbe | Barbacoa";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let modalOrder: Order | null = null;
  let orders: Order[] = [];
  let loading: boolean = true;
  let error: string | null = null;
  let searchTerm = "";
  let totalAmount = 0;
  let isModalOpen = false;
  let productId = 0; //Product koji cemo prikazati na modalu kada se klikne

  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  // Fetch the orders from the backend
  onMount(() => {
    try {
      const { isAuthenticated } = get(auth);

      /**
       * TODO: this is not actually working
       */

      if (!isAuthenticated) {
        error = "Session expired. Please login again.";
        return;
      } else {
        //handleSearch();
      }
    } catch (err) {
      error = err instanceof Error ? err.message : "Search failed";
    } finally {
    }
    window.addEventListener("keydown", handleKeydown);

    // Cleanup funkcija
    return () => {
      window.removeEventListener("keydown", handleKeydown);
    };
  });

  function handleFormSubmit(event: { preventDefault: () => void }) {
    event.preventDefault(); // prevent page reload
    handleSearch();
  }

  async function handleSearch() {
    const token = $auth.token;
    loading = true;

    try {
      const res = await fetch(API_BASE_URL + `/orders?search=${searchTerm}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      // Check response status and handle specific cases
      if (!res.ok) {
        if (res.status === 401) {
          console.log("Authentication failed - token may be expired");
          // Clear invalid token
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          // Redirect to login or show login modal
          // window.location.href = '/login';
          // OR: showLoginModal = true;
          // OR: goto('/login');
          throw new Error("Authentication failed");
        }
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      // Parse JSON directly - no need for JSON.parse since res.json() already does this
      const data = await res.json();

      // Update orders with the received data
      orders = data;
      totalAmount = calculateTotal(orders);
    } catch (error: any) {
      console.error("Error during orders search:", error);

      /**
       * TODO: see if this works. should display error message.__
       * ??????? štaće ovo ovde???
       */

      showErrorModal("Greška prilikom učitavanja narudžbe: " + error.message);

      // Handle 401 Unauthorized specifically
      if (error.message.includes("401")) {
        console.log("Authentication failed - token may be expired");
        // Clear invalid token
        $auth.token = null;
        // Redirect to login or show login modal
        // window.location.href = '/login';
        // OR: showLoginModal = true;
        // OR: goto('/login');
      }

      // Handle other errors appropriately (show user message, etc.)
    } finally {
      loading = false;
    }
  }

  function calculateTotal(orders: Order[]): number {
    return orders.reduce((sum, order) => {
      return sum + (order.price ?? 0);
    }, 0);
  }

  function openModal(order: Order): void {
    modalOrder = order;
  }

  function closeModal(): void {
    modalOrder = null;
    isModalOpen = false;
  }

  const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Add Bearer token if available
  axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("token"); // or getToken() if you have a helper
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });
  

  /* drugi modal */
  let showModal2 = false;

  function openModal2() {
    showModal2 = true;
  }

  function closeModal2() {
    showModal2 = false;
  }

  function handleKeydown(event: { key: string }) {
    if (event.key === "Escape") {
      closeModal2();
    }
  }

  /* treci modal */
  let showModal3 = false;

  function openModal3() {
    showModal3 = true;
  }

  function closeModal3() {
    showModal3 = false;
  }

  function handleKeydown3(event: { key: string }) {
    if (event.key === "Escape") {
      closeModal3();
    }
  }

  function handleProductClick(id: number | undefined) {
    if (id != undefined) productId = id;
    openModal3();
  }

</script>

<div
  class="rounded-xl p-2 flex flex-col gap-1 h-fit
         shadow border border-primary/20 hover:outline-primary/20 hover:outline-1
        {getOrderCardBgClass(order.status)}"
        class:card-new={isNew(order.created, 10) && order.status == "WAITING"}      
>
  <div class="flex items-center justify-between mb-1 ml-1">
    <div
      class="flex items-center gap-1 w-full"
      style="justify-content: space-between;"
    >
      <div        
        class="text-2xl font-extrabold text-primary"
        >{formatCode(order.code)}</div>      
      <span class="hidden">{order.id}</span>
      {#if order.status === 'READY' || order.status === 'SERVED'}
        <div
        class="text-sm hidden md:flex items-center text-primary/60 gap-2"
      >    
        <i class="fas fa-clock"></i>{@html formatTime2(order.created)}
      </div>
      {/if}
      {#if order.status === 'WAITING' || order.status === 'IN_PREPARATION'}
        {#if order.code.endsWith('Z')}
          <span class="badge badge-soft badge-success">
            <i class="fas fa-walking"></i>ZA VAN</span>
        {:else}
          <span class="badge badge-soft badge-info">
          <i class="fas fa-car"></i>
            DOSTAVA</span>
        {/if}
      {/if}
      {#if isNew(order.created, 15) && order.status === 'WAITING' }
        <div>
          <div
            class=" tooltip tooltip-info cursor-pointer"
            data-tip="Stiglo prije manje od 15 minuta"
          >
            <span
              class="indicator-item badge badge-accent dark:text-black dark:bg-violet-500"
              >novo</span
            >
          </div>
        </div>
      {/if}

      <StatusMenu {order} on:orderUpdateCompleted />
    </div>
  </div>

  <div class="flex items-center gap-1 text-sm text-primary mb-1 ml-1">
    <div class="flex items-center gap-1 text-sm text-primary/60 mr-2">
      <i class="fas fa-user"></i>
      <span><strong>{order.user?.username}</strong></span>
    </div>
    <div
      class="text-sm flex items-center text-primary/60 gap-2"
      class:hidden={liteView}
    >
      <i class="fas fa-clock"></i>{@html formatTime2(order.created)}
    </div>
    {#if order.status == "READY" || order.status == "SERVED"}    
      <div class="items-center gap-1 text-sm text-primary/60 mr-2 hidden md:flex">
        <i class="fas fa-euro"></i>
        <span><strong>{ formatPriceRaw(order.price)}</strong></span>
      </div>
      <div class="items-center gap-1 text-sm text-primary/60 mr-2 hidden md:flex">
        {#if order.code.endsWith('Z')}
          <z class=" tooltip tooltip-info tooltip-top" data-tip="Za van">
            <span class="badge badge-soft badge-success">
              <i class="fas fa-walking"></i></span>
            </z>
        {:else}
          <z class=" tooltip tooltip-info tooltip-top" data-tip="Dostava">
            <span class="badge badge-soft badge-info">
              <i class="fas fa-car"></i>
            </span>
          </z>  
        {/if}
      </div>
    {/if}
  </div>
  <div class="h-px bg-neutral/40 w-full mb-1"></div>

  {#if order.comment && order.comment.toString.length > -1 && (order.status != 'READY' && order.status != 'SERVED')}

  <div>
      <span
        class="indicator-item badge badge-ghost  text-primary font-bold rounded-md mt-3"
        style=""
      >
        Napomena:
      </span>
      <br />
      <div
        class=" 
          border-0 border-info text-primary p-1  py-2 px-0 rounded-lg break-words mb-4"
          class:truncate={ order.status != 'IN_PREPARATION'}
          class:max-w-xs={ order.status != 'IN_PREPARATION'}          
          title="{order?.comment}"
      >
      <div class="chat chat-start">
        <div class="chat-bubble chat-bubble-neutral text-primary whitespace-pre-wrap break-words"
            style="max-width: 100%;">
          {order.comment}
        </div>
      </div>
    </div>
  </div>
  <div class="h-px bg-neutral/40 w-full mb-2"></div>

  {/if}

  <div class="mt-2" class:hidden={liteView}>
    <ul class="flex flex-col gap-0 bg-base-300/40 rounded-t-2xl">
      {#each order.items as item}
          <li
            class="flex items-center gap-0 px-1.5 py-0.5 border-0 border-b-1 border-primary/5"
          >
            <span class="text-primary text-md font-bold"
              >{item.quantity}&nbsp;x&nbsp;</span
            >
            <!-- svelte-ignore a11y_click_events_have_key_events -->
            <!-- svelte-ignore a11y_no_static_element_interactions -->
            <span
              class="text-primary text-md pgs-hyperlink p-0 font-normal whitespace-nowrap"
              on:click={() => handleProductClick(item.productId)}
            >
              {item.product?.name}</span
            >

            <!-- ovo nam ne treba za kuhinju -->
            <div class="hidden">
              {#if item.price}
                <span class="text-xs text-gray-500 ml-auto font-mono"
                  >{formatPrice(item.price)}</span
                >
              {/if}
            </div>
          </li>
      {/each}
    </ul>
    <div
      style="align-items: end;display:grid;align-content: end; text-align: right; "
      class=" bg-base-300/40 rounded-b-2xl"
    >
      <span class="text-lg font-bold text-primary p-1 font-mono"
        >{formatPrice(order.price)}</span
      >
    </div>
  </div>

  <div class="flex gap-1 mt-1">
    <button class="btn btn-sm btn-ghost text-primary/80" on:click={openModal2}
      >Detalji</button
    >

    {#if order.status == "WAITING"}
      <OrderButtonInPreparation {order} on:orderUpdateCompleted />
    {/if}
    {#if order.status == "IN_PREPARATION"}
      <OrderButtonReady {order} on:orderUpdateCompleted />
    {/if}
    {#if order.status == "READY"}
      <OrderButtonServed {order} on:orderUpdateCompleted />
    {/if}
  </div>
</div>

<!-- ovde cemo staviti order details modal -->
{#if showModal2}
  <div class="modal modal-open pt-14" style="backdrop-filter: blur(10px);">
    <div
      class="modal-box max-h-[95vh] w-11/12 max-w-5xl p-0 flex flex-col bg-base-100"
      transition:fly={{ y: 50, duration: 200 }}
    >
      <!-- Fixed Header -->
      <div
        class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300"
      >
        <h3 class="font-bold text-lg">Detalji narudžbe</h3>
      </div>

      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1 px-3 py-2">
        <OrderDetails ID={order.id}></OrderDetails>
      </div>

      <!-- Fixed Footer -->
      <div
        class="sticky bottom-0 bg-base-100 z-10 px-6 py-4 border-t border-base-300"
      >
        <div class="flex justify-end gap-2">
          <button class="btn btn-secondary" on:click={closeModal2}
            >Zatvori</button
          >
        </div>
      </div>
    </div>

    <!-- Glass Backdrop -->
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="modal-backdrop" on:click={closeModal2}></div>
  </div>
{/if}

{#if showModal3}
  <div class="modal modal-open pt-0" style="backdrop-filter: blur(10px);">
    <div
      class="modal-box max-h-[90vh] w-full sm:w-8/12 max-w-5xl p-0 flex flex-col bg-base-200"
      transition:fly={{ y: 50, duration: 300 }}
    >
      <!-- Fixed Header -->
      <div
        class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300"
      >
        <h3 class="font-bold text-lg">Detalji proizvoda</h3>
      </div>

      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1">
        <ProductPage {productId} liteView={true}></ProductPage>
      </div>

      <!-- Fixed Footer -->
      <div
        class="sticky bottom-0 bg-base-100 z-10 px-6 py-4 border-t border-base-300"
      >
        <div class="flex justify-end gap-2">
          <button class="btn btn-secondary" on:click={closeModal3}
            >Zatvori</button
          >
        </div>
      </div>
    </div>

    <!-- Glass Backdrop -->
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="modal-backdrop" on:click={closeModal3}></div>
  </div>
{/if}


<style>
  .card-new {
    border: 2px solid 2px solid #6933ff;
    animation: pulse 6s infinite;
  }

  @keyframes pulse {
    0%,
    100% {
      box-shadow: 0 0 0 0 rgba(111, 0, 255, 0.7);
    }
    50% {
      box-shadow: 0 0 0 21px rgba(0, 4, 255, 0);
    }
  }
  .bg-wait {
    background-color: #acab5e45;
  }
  .bg-inprep {
    background-color: #5e86ac45;
  }
  .bg-ready {
    background-color: #6bac5e45;
  }
</style>
