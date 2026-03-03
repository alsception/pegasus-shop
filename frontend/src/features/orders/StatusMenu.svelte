<script lang="ts">
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import { showErrorToast, showInfoToast, showSuccessToast } from "../../core/utils/toaster";
  import { getOrderStatusColor, getOrderStatusLabel } from "../../utils/formatting";
  import type { Order } from "./Order";
  import axios from "axios";
  import { createEventDispatcher } from 'svelte';
  
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  export let order: Order;

  let loading = false;

  const dispatch = createEventDispatcher();

  //This object is used for displaying menu
  const statusItems = [
    { 
      label: "NA ČEKANJU",
      status: "WAITING"
    },
    { 
      label: "U PRIPREMI",
      status: "IN_PREPARATION" 
    },
    { 
      label: "SPREMNO",
      status: "READY" 
    },
  ];

  const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Add Bearer token if available
  axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token'); // or getToken() if you have a helper
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  async function updateOrderState(id: any, status: any) 
  {
    if (!id || !status) return;
    loading = true;
    
    try 
    {
      const response = axiosInstance.put('/orders/update-status/'+id, null, {
        params: {
          status: status
        }
      });
      (await response);
      //showInfoToast("Sačuvano",3);
      closeDropdown();
      dispatch('orderUpdateCompleted', { id, status }); // Šalje event

    } 
    catch (err) 
    {
      showErrorToast("Greška")
    } 
    finally 
    {
       loading = false;
    }
  }

  let dropdownOpen = false;
  
  function closeDropdown() {
    dropdownOpen = false;
  }


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
      axiosInstance.delete(`/orders/${orderId}`)
        .then(response => {
          processSuccess(response);
          // e sad . ovde bi treblo refresh content na ekranu... todo
        })
        .catch(error => {
          processError(error);
        });
    }
  };

  function processError(error: any) 
  {
    // Extract message from error response
    let errorMessage = 'Error: ';

    if (error.response && error.response.data) 
    {
        // error.response.data.message should contain message
        if (error.response.data.message) 
        {
            errorMessage += error.response.data.message;
        } 
        else if (typeof error.response.data === 'string') 
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

  
</script>
{#if loading}
  <LoadingOverlay />
{/if}
<details class="dropdown" bind:open={dropdownOpen}>
  <summary
    class="btn btn-ghost btn-circle hover:bg-neutral-700/10 tooltip tooltip-info tooltip-top bg-transparent"
    data-tip="Uredi stanje"
  >
    ☰
  </summary>

  <ul
    class="menu menu-sm dropdown-content mt-3 w-22 p-0 scale-in-ver-top
            bg-base-200
            rounded-md shadow
            border border-gray-600 "
    style="min-width: 205px;left: -170px; z-index: 9000;"
  >
    {#each statusItems as item}
      {#if order.status != item.status}
        <li class="w-full p-2 border-t border-b border-gray-500/40">
          <button
          on:click={(e) => {
            e.preventDefault();
            updateOrderState(order.id, item.status);
          }}


  class="flex items-center px-3 py-2 rounded-md cursor-pointer
                    font-semibold font-stretch-150% badge"
            style="font-size: small;"
          >
            <span class="badge badge-{getOrderStatusColor(item.status)} font-mono badge-lg ml-auto" style="text-transform: uppercase;">
                  {item.label /*+/* getOrderStatusLabel(item.status)*/}
                </span>
          
        </button>
        </li>
        {/if}
    {/each}
    <button class="btn btn-sm btn-ghost"  aria-label="Obriši" 
              on:click={()=>deleteDialog(order.id, 'Are you sure you want to delete this order? This action cannot be undone!')}>
                <z
                class=" tooltip tooltip-info tooltip-top"
                data-tip="Obriši"
              >
                <i class="fas fa-trash text-error" aria-label="Obriši"></i>  
                </z>       Obriši       
            </button>
  </ul>
</details>
