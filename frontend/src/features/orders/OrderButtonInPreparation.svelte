<script lang="ts">
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import { showErrorToast, showInfoToast } from "../../core/utils/toaster";
  import type { Order } from "./Order";
  import axios from "axios";
  import { createEventDispatcher } from 'svelte';
  
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  export let order: Order;

  let loading = false;

  const dispatch = createEventDispatcher();

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
  
</script>

{#if loading}

  <LoadingOverlay />

{/if}
 
<button 
class="btn btn-sm btn-info text-primary-content ml-auto"
    on:click={(e) => {
          e.preventDefault();
          updateOrderState(order.id, 'IN_PREPARATION');
        }}>
    <i class="fa fa-fire text-md cursor-pointer"></i>PRIHVATI</button>