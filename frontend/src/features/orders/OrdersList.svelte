<script lang="ts">
  import OrderCardMd from "./OrderCardMd.svelte";
  import type { Order } from "./Order";
  import { onDestroy, onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import { get } from "svelte/store";
  import {
    formatCode,
    formatCommentInfo,
    formatDate,
    formatPrice,
    formatTime,
    formatTime2,
    getOrderStatusColor,
    getOrderStatusLabel,
  } from "../../utils/formatting";
  import { showSuccessToast, showErrorToast } from "../../core/utils/toaster";
  import axios from "axios";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import { showErrorModal } from "../../utils/modal";
  import { fade, fly } from "svelte/transition";
  import { flip } from "svelte/animate";

  /* ******************************************

TODO:

[done] Novo da bude uokvireno jer ovo sad badge smeta i NOTIFICATION!!!!(new Order, status updated, from-to)
i failed to fetch kad ugasim server js da stane 
i LITE APP!!, I MOZDA i WS.....



***************************************************************** */

  document.title = "Narudžbe | Barbacoa";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let orders: Order[] = [];
  let ordersNaCekanju: Order[] = [];
  let ordersUpripremi: Order[] = [];
  let ordersSpremni: Order[] = [];
  let ordersOstalo: Order[] = [];
  let loading: boolean = true;
  let error: string | null = null;
  let searchTerm = "";
  let totalAmount = 0;
  let isBlockView = false;
  let intervalId: string | number | NodeJS.Timeout | undefined;
  let mojParametar = null;
  const REFRESH_INTERVAL = 5000; // 5 sekundi
  const autoRefresh = true;
  export let liteView = true;

  let isCheckedWait = false;
  let isCheckedInPrep = true;
  let isCheckedReady = false;
  let isCheckedOther = false;

  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  function toggleView() 
  {
    isBlockView = !isBlockView;
  }

  // AUTHENTICATION
  $: auth.subscribe((value) => 
  {
    isAuthenticated = value.isAuthenticated;
  });

  // Fetch the orders from the backend
  onMount(async () => 
  {
    if (liteView) isBlockView = false;

    try 
    {
      
      if (!isAuthenticated) 
      {
        error = "Session expired. Please login again.";
        return;
      } 
      else 
      {

        // Support hash-based routing: extract query params from hash if present
        // drugacije nemoze
        let search = window.location.search;
        if (!search && window.location.hash.includes("?")) 
        {
          search = window.location.hash.substring(
            window.location.hash.indexOf("?")
          );
        }
        const params = new URLSearchParams(search);

        //stolovi: todo: moramo smisliti nesto da razlikujemo stolove od broja narudzbe
        let tableParam = params.get("t");

        if (tableParam !== null) 
        {
          searchTerm = /*'T:'+*/tableParam;
        }

        //Views: na cekanju, u pripremi, spremno
        let viewParam = params.get("v");

        if (viewParam !== null) 
        {
          
          if(viewParam == '1')
          {
            isCheckedWait = true;
          }
          else if(viewParam == '2')
          {
            isCheckedInPrep = true;
          }
          else if(viewParam == '3')
          {
            isCheckedReady = true;
          }
        }        

        // Prvo učitavanje
        await handleSearch(true);

        if (autoRefresh) 
        {
          // Automatsko osvežavanje
          intervalId = setInterval(async () => 
          {
            await handleSearch(false);
          }, REFRESH_INTERVAL);
        }
      }
    } 
    catch (err) 
    {
      error = err instanceof Error ? err.message : "Search failed";
    } 
    
  });

  onDestroy(() => 
  {
    // Cleanup - obavezno!
    if (intervalId) 
    {
      clearInterval(intervalId);
    }
  });

  function handleFormSubmit(event: { preventDefault: () => void }) 
  {
    event.preventDefault(); // prevent page reload
    handleSearch(true);
  }

  async function handleSearch(showLoading: boolean) 
  {
    if(!$auth.isAuthenticated)
    {
      clearInterval(intervalId);
    }
    else
    {
      const token = $auth.token;
      if (showLoading) loading = true;

      try 
      {
        const res = await fetch(API_BASE_URL + `/orders?search=${searchTerm}`, 
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        // Check response status and handle specific cases
        if (!res.ok) 
        {
          if (res.status === 401) 
          {
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

        if( liteView || !isBlockView ) orders = data.reverse();


        ordersNaCekanju = orders.filter((o) => o.status === 'WAITING');
        ordersUpripremi = orders.filter((o) => o.status === 'IN_PREPARATION');
        ordersSpremni = orders.filter((o) => o.status === "READY" || o.status === "SERVED");
        ordersOstalo = orders.filter((o) => (
          o.status !== "READY" 
           && o.status !== "SERVED"
           && o.status !== "WAITING"
           && o.status !== "IN_PREPARATION"
        ));
        totalAmount = calculateTotal(orders);
      } 
      catch (error: any) 
      {
        console.error("Error during search:", error);

        /**
         * TODO: see if this works. should display error message.
         * ako je failed to fetch, server je nedostupan.
         */

        showErrorModal("Greška prilikom učitavanja narudžbi: " + error.message);
        clearInterval(intervalId);
        console.log('asdasd');
        // Handle 401 Unauthorized specifically
        if (error.message.includes("401")) 
        {
          console.log("Authentication failed - token may be expired");
          // Clear invalid token
          $auth.token = null;
        }

      } 
      finally 
      {
        if (showLoading) loading = false;
      }
    }         
  }

  function calculateTotal(orders: Order[]): number 
  {
    return orders.reduce((sum, order) => 
    {
      return sum + (order.price ?? 0);
    }, 0);
  }

  const axiosInstance = axios.create(
  {
    baseURL: API_BASE_URL,
    headers: 
    {
      "Content-Type": "application/json",
    },
  });

  // Add Bearer token if available
  axiosInstance.interceptors.request.use((config) => 
  {
    const token = localStorage.getItem("token"); 
    if (token) 
    {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  function processSuccess(response: any) 
  {
    // Display success message
    if (response.data && response.data.message) 
    {
      console.log(response);
      showSuccessToast(response.data.message);
    }
  }

  function deleteDialog(orderId: number, confirmMsg: string) 
  {
    if (confirm(confirmMsg)) 
    {
      axiosInstance
        .delete(`/orders/${orderId}`)
        .then((response) => {
          processSuccess(response);
          // Remove the deleted order from the list
          orders = orders.filter((order) => order.id !== orderId);
        })
        .catch((error) => {
          processError(error);
        });
    }
  }

  function processError(error: any) 
  {
    // Extract message from error response
    let errorMessage = "Error: ";

    if (error.response && error.response.data) 
    {
      // error.response.data.message should contain message
      if (error.response.data.message) 
      {
        errorMessage += error.response.data.message;
      } 
      else if (typeof error.response.data === "string") 
      {
        errorMessage += error.response.data;
      }
    } 
    else if (error.message) 
    {
      errorMessage = error.message;
    }

    showErrorToast(errorMessage);
  }

  /**
   * Kad zavrsi ajax poziv iz OrderCardMd>StatusMenu, onda ce se ovde pozvati ova funkcija i osveziti sadrzaj na ekranu
   */
  function handleOrderUpdateCompleted(event: { detail: any }) 
  {
    handleSearch(true);
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}
  {#if !liteView}
    <div class="w-full flex justify-center">
      <div class="w-full /*max-w-4xl*/ p-4 bg-base-200 rounded-lg mb-1 pb-2">
        <form
          on:submit|preventDefault={handleFormSubmit}
          class="flex flex-col md:flex-row items-center gap-3"
        >
          <input
            type="text"
            bind:value={searchTerm}
            placeholder="Upiši broj narudžbe ili stola"
            class="input border-2 w-full md:flex-1 max-w-md"
          />

          <div class="flex flex-row gap-3 w-full md:w-auto">
            <button
              type="submit"
              class="btn btn-dash flex-1 md:flex-none whitespace-nowrap"
            >
              <i class="fas fa-search"></i>
              Traži
            </button>

            <button
              type="button"
              class="btn btn-dash flex-1 md:flex-none whitespace-nowrap"
              on:click={toggleView}
            >
              <i class="fas fa-th-large"></i>
              Prikaz
            </button>

          </div>
        </form>
      </div>
    </div>
  {:else}
    <a use:link href="/orders"><h3 class="font-bold font-mono text-2xl mt-14 mb-6 text-primary/80">Zadnje narudžbe</h3></a>
  {/if}

  {#if loading}
    <LoadingOverlay />
  {/if}

  <!-- Show each item in the order card (Block view) -->
  {#if false}
    <!-- name of each tab group should be unique -->
    <div class="tabs tabs-box rounded-lg">     

      <input
        type="radio"
        name="my_tabs_6"
        class="tab font-bold text-primary"
        aria-label="NA ČEKANJU ({ ordersNaCekanju.length })"
        checked={isCheckedWait}
      />
      <div class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-6 h-full"
        >
        {@render ordersWait()}
      </div>

      <input
        type="radio"
        name="my_tabs_6"
        class="tab font-bold text-primary"
        aria-label="U PRIPREMI ({ ordersUpripremi.length })"
        checked={isCheckedInPrep}
      />
      <div
        class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-6 h-full"
      >
        {@render ordersInprep()}
      </div>

      <input
        type="radio"
        name="my_tabs_6"
        class="tab font-bold text-primary"
        aria-label="SPREMNO ({ ordersSpremni.length })"
        checked={isCheckedReady}
      />
      <div     
        class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-6 h-full"
      >
        {@render ordersReady()}
      </div>

      <input
        type="radio"
        name="my_tabs_6"
        class="tab font-bold text-primary"
        aria-label="OSTALO ({ ordersOstalo.length })"
        checked={isCheckedOther}
      />
      <div     
        class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-6 h-full"
      >
        {@render ordersOther()}
      </div>
    </div>


  {:else}
    <!-- Table view: -->
    {#if orders.length > 0}
    <div
      class="max-w-[2048px] w-full overflow-x-auto rounded-lg align-middle text-center mx-auto"
    >
      <div class="">
        <table class="table table-zebra min-w-full divide-y divide-accent">
          <thead class="bg-base-300">
            <tr class="h-12">
              <th class="pgs-th">Broj</th>
              <th class="pgs-th">Dostava<br>/ za van</th>
              <th class="pgs-th">Status</th>
              <th class="pgs-th"></th>
              <th class="pgs-th">Primljeno</th>
              <th class="pgs-th">U pripremi</th>
              <th class="pgs-th">Spremno</th>
              <th class="pgs-th">Dostavljeno</th>
              <th class="pgs-th-r">Ukupno<br>stavki</th>
              <th class="pgs-th-r">Iznos</th>
              <th class="pgs-th-l">Način<br>plaćanja</th>
              {#if !liteView}
                <th class="pgs-th"></th>
              {/if}
            </tr>
          </thead>
          <tbody>
            {#each orders as order, i}
              <tr
                class={`tr-highlight ${i % 2 === 1 ? 'bg-base-200/30' : 'bg-base-200/60'}`}
                transition:fly={{ y: -50, duration: 300 }}
              >
                <td class="pgs-td">
                  <a use:link href="/orders/{order.id}" class="pgs-hyperlink"
                    >{formatCode(order.code)}</a
                  >
                </td>                
                <td class="pgs-td font-mono p-2">
                  <div class="flex items-center justify-center gap-1 text-sm text-primary/60 w-full">
                    
                    {#if order.code.endsWith('T')}
                      <div class="tooltip tooltip-info tooltip-top flex items-center" data-tip="Za van">
                        <span class="badge badge-soft badge-success flex items-center justify-center">
                          <i class="fas fa-walking"></i>
                        </span>
                      </div>
                    {:else}
                      <div class="tooltip tooltip-info tooltip-top flex items-center" data-tip="Dostava">
                        <span class="badge badge-soft badge-info flex items-center justify-center">
                          <i class="fas fa-car"></i>
                        </span>
                      </div>  
                    {/if}
                  </div>
                </td>
                <td class="text-center">
                  {#if order.status}
                    <span
                      class="badge badge-soft badge-{getOrderStatusColor(
                        order.status
                      )} font-mono badge-sm whitespace-nowrap"
                      style="text-transform: uppercase;"
                    >
                      {@html getOrderStatusLabel(order.status) }
                    </span>
                  {/if}
                </td>
                <td class="text-center"
                  >{@html formatCommentInfo(order.comment)}</td
                >
                <td class="pgs-td font-mono">
                  {@html formatTime(order.created, "novo", 15)}
                  <!-- {@html formatTime2(order.created)} -->
                </td>
                <td class="pgs-td font-mono">
                  {@html formatTime2(order.upripremiAt)}
                </td>
                <td class="pgs-td font-mono">
                  {@html formatTime2(order.spremnoAt)}
                </td>
                <td class="pgs-td font-mono">
                  {@html formatTime2(order.servedAt)}
                </td>
                <td class="pgs-td-num font-mono">{order.items.length}</td>
                <td class="pgs-td-num font-mono font-bold text-right"
                  >{formatPrice(order.price)}</td
                >
                <td class="pgs-td font-mono text-center">
                  {#if order.paymentMethod == '1'}
                    <div class="tooltip tooltip-info tooltip-top inline-flex" data-tip="Gotovina">
                      <span class="badge badge-soft badge-warning flex items-center justify-center">
                        <i class="fas fa-coins"></i>
                      </span>
                    </div>
                  {:else if order.paymentMethod == '2'}
                    <div class="tooltip tooltip-info tooltip-top inline-flex" data-tip="Kartica">
                      <span class="badge badge-soft badge-accent flex items-center justify-center">
                        <i class="fas fa-credit-card"></i>
                      </span>
                    </div>
                  {:else}
                    <span
                      class="badge badge-soft badge-secondary font-mono badge-sm whitespace-nowrap"
                      style="text-transform: uppercase;"
                    >OSTALO</span>
                  {/if}
                </td>
                {#if !liteView}
                  <td class=" justify-center">
                    <div class="tooltip tooltip-info group" data-tip="Edit">
                      <a
                        class="px-4"
                        aria-label="Edit"
                        use:link
                        href="/orders/mngmt/{order.id}"
                      >
                        <i
                          class="fas fa-pen text-gray-500 group-hover:text-sky-400 cursor-pointer"
                        ></i></a
                      >
                    </div>
                    <button
                      class="px-4 group"
                      aria-label="Delete"
                      on:click={() =>
                        deleteDialog(
                          order.id,
                          "Are you sure you want to delete this order? This action cannot be undone!"
                        )}
                    >
                      <div class="tooltip tooltip-info" data-tip="Delete">
                        <i
                          class="fas fa-times-circle text-gray-500 group-hover:text-red-400 cursor-pointer"
                        ></i>
                      </div>
                    </button>
                  </td>
                {/if}
              </tr>
            {/each}
          </tbody>
        </table>
        {#if !liteView}
          <div
            class="nb-table-footer text-left bg-secondary w-full"
            style="background-color: var(--color-base-200);"
          >
            Ukupan promet: <span
              class="ml-4 font-bold font-mono text-2xl text-primary"
              >{formatPrice(totalAmount)}</span
            >
            <br />
            Ukupno narudžbi:
            <span class="font-bold text-xl text-primary"> {orders.length}</span>
          </div>
        {/if}
      </div>
    </div>
    {:else}
    Nema narudžbi
    {/if}

  {/if}
{/if}

<div style="display: none;">
  NEED THIS HERE OTHERWISE TAILWIND OR DAISY DOESNT LOAD CSS CLASSES
  <div class="badge badge-soft badge-primary">Primary</div>
  <div class="badge badge-soft badge-secondary">Secondary</div>
  <div class="badge badge-soft badge-accent">Accent</div>
  <div class="badge badge-soft badge-info">Info</div>
  <div class="badge badge-soft badge-success">Success</div>
  <div class="badge badge-soft badge-warning">Warning</div>
  <div class="badge badge-soft badge-error">Error</div>
</div>

{#snippet ordersWait()}
  <div
    class="columns-1 sm:columns-2 lg:columns-3 xl:columns-4 2xl:columns-5 gap-6"
  >
    {#each ordersNaCekanju as order (order.id)}
    <div 
      class="break-inside-avoid mb-8 w-full"
      animate:flip={{ duration: 200 }}
      transition:fade
      >
      <OrderCardMd
        {order}
        liteView={false}
        on:orderUpdateCompleted={handleOrderUpdateCompleted}
      ></OrderCardMd>
      </div>
    {/each}
  </div>
{/snippet}

{#snippet ordersInprep()}
  <div
    class="columns-1 sm:columns-2 lg:columns-3 xl:columns-4 2xl:columns-5 gap-6"
  >
    {#each ordersUpripremi as order (order.id)}
      <div
        class="break-inside-avoid mb-8 w-full"
        animate:flip={{ duration: 400 }}
        transition:fade
      >
        <OrderCardMd
          {order}
          on:orderUpdateCompleted={handleOrderUpdateCompleted}
        />
      </div>
    {/each}
  </div>
{/snippet}

{#snippet ordersReady()}
  <!-- TODO: ovde mora fix na mali ekran nevidi se dobro -->
  <div class="grid grid-cols-2 lg:flex lg:flex-wrap gap-4">
    {#each ordersSpremni as order (order.id)}
      <div
        class="w-full lg:flex-grow lg:min-w-[200px] lg:max-w-[280px] mt-2"
        animate:flip={{ duration: 400 }}
        transition:fade
      >
        <OrderCardMd
          {order}
          liteView={true}
          on:orderUpdateCompleted={handleOrderUpdateCompleted}
        ></OrderCardMd>
      </div>
    {/each}
  </div>
{/snippet}

{#snippet ordersOther()}
  <div
    class="columns-1 sm:columns-2 lg:columns-3 xl:columns-4 2xl:columns-5 gap-6"
  >
    {#each ordersOstalo as order (order.id)}
      <div
        class="break-inside-avoid mb-8 w-full"
        animate:flip={{ duration: 400 }}
        transition:fade
      >
        <OrderCardMd
          {order}
          on:orderUpdateCompleted={handleOrderUpdateCompleted}
        />
      </div>
    {/each}
  </div>
{/snippet}

<style>
  .pgs-orders-wait-cntr {
    min-height: 100vh;
  }

  .pgs-orders-inprep-cntr {
    min-height: 100vh;
  }

  .tab {
    border-radius: 4px;
  }
  
</style>
