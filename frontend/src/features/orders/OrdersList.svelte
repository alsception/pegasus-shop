<script lang="ts">
  import type { PGSOrder } from "./Order";
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../../core/services/store";  
  import { get } from "svelte/store";
  import { formatCode, formatDate, formatCommentInfo } from "../../lib/utils";
  import { showSuccessToast, showErrorToast } from '../../core/toaster';
  import axios from 'axios';
  import Login from "../../core/auth/login.svelte";
  import LoadingOverlay from "../../core/LoadingOverlay.svelte";
  import ErrorDiv from "../users/ErrorDiv.svelte";

  document.title = 'Orders | Pegasus'

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let modalOrder: PGSOrder | null = null;
  let orders: PGSOrder[] = [];
  let loading: boolean = true;
  let error: string | null = null;
  let isDark = false;
  let searchTerm = "";


  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });


  function checkDarkmode()
  {

    //Ovo proverava dali je darkmode classa dodeljena na body
    //Jer treba zbog nekih elemenata koji se drugacije prikazuju

    const check = () => isDark = document.body.classList.contains('dark');
    check();

    const observer = new MutationObserver(check);
    observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });

    return () => observer.disconnect();
  }


  // Fetch the orders from the backend
  onMount(async () => {
    checkDarkmode();

    try {
      const { isAuthenticated } = get(auth);

      /**
       * TODO: this is not actually working
       */

      if (!isAuthenticated) {
        error = "Session expired. Please login again.";
        return;
      } else {
        handleSearch();
      }
    } catch (err) {
      error = err instanceof Error ? err.message : "Search failed";
    } finally {

    }
  });

  function handleFormSubmit(event: { preventDefault: () => void }) 
  {
    event.preventDefault(); // prevent page reload
    handleSearch();
  }

 async function handleSearch() 
 {    
    const token = $auth.token;
    loading = true;
    
    try {        

        const res = await fetch(API_BASE_URL + `/orders?search=${searchTerm}`, 
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });        
        
        // Check response status and handle specific cases
        if (!res.ok) {
            if (res.status === 401) {
                console.log('Authentication failed - token may be expired');
                // Clear invalid token
                localStorage.removeItem('token');
                auth.set({ token: null, isAuthenticated: false });
                // Redirect to login or show login modal
                // window.location.href = '/login';
                // OR: showLoginModal = true;
                // OR: goto('/login');
                throw new Error('Authentication failed');
            }
            throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
        }
        
        // Parse JSON directly - no need for JSON.parse since res.json() already does this
        const data = await res.json();
        
        // Update orders with the received data
        orders = data;
        
    } catch (error: any) 
    {
        console.error('Error during search:', error);
        
        // Handle 401 Unauthorized specifically
        if (error.message.includes('401')) {
            console.log('Authentication failed - token may be expired');
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

  function openModal(order: PGSOrder): void {
    modalOrder = order;
  }

  function closeModal(): void {
    modalOrder = null;
  }

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
          // Remove the deleted order from the list
          orders = orders.filter(order => order.id !== orderId);
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

{#if !$auth.isAuthenticated}
  <Login />
{:else}



<div
    class="w-full max-w-4xl mx-auto p-4 bg-white dark:bg-slate-900 rounded-lg"
  >

    <form
      on:submit|preventDefault={handleFormSubmit}
      class="flex flex-wrap items-center gap-3"
    >
      <input
        type="text"
        bind:value={searchTerm}
        placeholder="Search orders..."
        class="input input-primary"
      />
      <!-- Search Button -->
      <button
        type="submit"
        class="btn"
      >
        <i class="fas fa-search"></i>
        Search
      </button>


      <!-- Create order Button -->
      <button
        on:click={() => alert('not implemented yet')}
        class="btn"
      >       
          <i class="fas fa-plus"></i>
          Create new order
      </button>
    </form>
</div>

<div id="results" class="w-full max-w-4xl mx-auto mt-6"></div>

  {#if loading}

  <LoadingOverlay/>

  {:else if error}
    
  <ErrorDiv {error} />
    
  {:else}

    {#if (orders.length === 0)}
        
      <div class="flex justify-center items-center h-64">
        <h3 class="text-gray-500 dark:text-gray-400">No orders found.</h3>
      </div>

    {:else}

      <div class="max-w-[2048px] w-full overflow-x-auto rounded-lg align-middle text-center mx-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700 /*table*/">
          <thead class="bg-gray-800 dark:bg-slate-800">
            <tr class="h-12">
              <th class="pgs-th">id</th>
              <th class="pgs-th">code</th>
              <th class="pgs-th">user</th>
              <th class="pgs-th">Email</th>
              <th class="pgs-th">Payment Method</th>
              <th class="pgs-th">Amount</th>
              <th class="pgs-th">Items</th>
              <th class="pgs-th">Comment</th>          
              <th class="pgs-th">created</th>       
              <th class="pgs-th">Status</th>
              <th class="pgs-th">Actions</th>          
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            {#each orders as order, i}
              <tr class="{i % 2 === 0 ? 'bg-white dark:bg-gray-800' : 'bg-gray-50 dark:bg-gray-900'} hover:bg-gray-100 dark:hover:bg-gray-600 pgs-tr-hov">
                <td class="pgs-td">
                  <a use:link href="/orders/{order.id}" class="pgs-hyperlink">{order.id}</a>
                </td>
                <td class="pgs-td font-mono">{formatCode(order.code)}</td>
                <td class="pgs-td">{order.user?.username}</td>
                <td class="pgs-td">{order.email}</td>
                <td class="pgs-td">
                  <div class="badge badge-neutral">{order.paymentMethod}</div>
              </td>
                <td class="pgs-td-num font-mono">{order.price}</td>
                <td class="pgs-td-num font-mono">{order.items.length}</td>
                <td class="text-center">{@html formatCommentInfo(order.comment)}</td>            
                <td class="pgs-td font-mono">
                  {@html formatDate(order.created,'New - created less than 15 minutes ago',15)}
                </td>
                <td class="text-center"><div class="badge badge-primary">{order.status}</div></td>        


                <td class=" justify-center">
                  <div class="tooltip" data-tip="Edit"><a class="px-4" aria-label="Edit" use:link href="/orders/mngmt/{order.id}"><i class="fas fa-pen text-gray-500 hover:text-sky-400 cursor-pointer"></i></a></div>
                  <button class="px-4" aria-label="Delete" on:click={()=>deleteDialog(order.id, 'Are you sure you want to delete this order? This action cannot be undone!')}>
                    <div class="tooltip" data-tip="Delete">
                    <i class="fas fa-times-circle text-gray-500 hover:text-red-400 cursor-pointer"></i>
                  </div></button>
                </td>      
              </tr>
            {/each}
            <tr class="bg-white dark:bg-slate-900"> 
              <td colspan="18" class="pgs-td text-left font-mono font-bold">Total orders found: {orders.length}</td>  
            </tr>
          </tbody>
        </table>
      </div>
      
    {/if}
  {/if}

  <!-- ovde cemo staviti order details (items) -->
  {#if modalOrder}
    <Modal
      showModal={true}
      title="Reviews"
      data={modalOrder.reviews}
      on:close={closeModal}
    />
  {/if}
{/if}