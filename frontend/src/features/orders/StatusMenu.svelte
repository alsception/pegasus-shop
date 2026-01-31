<script lang="ts">
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import { showErrorToast, showSuccessToast } from "../../core/utils/toaster";
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
      label: "⏳ NA ČEKANJU",
      status: "WAITING"
    },
    { 
      label: "👨‍🍳 U PRIPREMI",
      status: "IN_PREPARATION" 
    },
    { 
      label: "✅ SPREMNO",
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
      showSuccessToast("Sačuvano");
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
  
</script>
{#if loading}
  <LoadingOverlay />
{/if}
<details class="dropdown bg-transparent" bind:open={dropdownOpen}>
  <summary
    class="btn btn-ghost btn-circle hover:bg-neutral-700/10 tooltip tooltip-info tooltip-top"
    data-tip="Uredi stanje"
  >
    ⚙️☰
  </summary>

  <ul
    class="menu menu-sm dropdown-content mt-3 w-22 p-0 scale-in-ver-top
            bg-base-100
            rounded-md shadow
            border border-gray-600"
    style="min-width: 205px;left: -170px"
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
        <!--                   hover:bg-[#405075] hover:text-blue-400 -->
<!--           <i class="fas fa-{item.label} w-5 mr-2"></i>
 -->          <span class="badge badge badge-{getOrderStatusColor(item.status)} font-mono badge-lg ml-auto" style="text-transform: uppercase;">
                {item.label /*+/* getOrderStatusLabel(item.status)*/}
              </span>
          <!-- <span
            class="text-white hover:text-blue-400"
            style="width: 150px;color:white">{item.label}</span
          > -->
      </button>
      </li>
      {/if}
    {/each}
  </ul>
</details>
