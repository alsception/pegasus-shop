<script lang="ts">
  import type { Order } from "./Order";
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";  
  import { get } from "svelte/store";
  import { formatCode, formatDate, formatCommentInfo } from "../../utils/formatting";
  import { showSuccessToast, showErrorToast } from '../../core/utils/toaster';
  import axios from 'axios';
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";

  document.title = 'Orders | Pegasus'

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let modalOrder: Order | null = null;
  let orders: Order[] = [];
  let loading: boolean = true;
  let error: string | null = null;
  let isDark = false;
  let searchTerm = "";
  let totalAmount = 0;


  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

/* 
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
 */

  // Fetch the orders from the backend
  onMount(async () => {
/*     checkDarkmode();
 */
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
        totalAmount = calculateTotal(orders);
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



  function formatPrice(price: number | undefined): string {

    if (price === undefined || price === null) 
    {
      return "€ 0,00";
    }
    
    return new Intl.NumberFormat('de-DE', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(price);
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}

<div class="w-full flex justify-center px-4">
  <div class="w-full max-w-4xl p-4 bg-base-200 rounded-lg">
    <form
      on:submit|preventDefault={handleFormSubmit}
      class="flex flex-col sm:flex-row items-center gap-3"
    >
      <input
        type="text"
        bind:value={searchTerm}
        placeholder="Search orders..."
        class="nb-input default"
      />
      <!-- Search Button -->
      <button
        type="submit" 
        class="nb-button default"
      >
        <i class="fas fa-search"></i>
        Search
      </button>

      <!-- Create order Button -->
      <button
        on:click={() => alert('not implemented yet')}
        class="nb-button default"
      >       
          <i class="fas fa-plus"></i>
          Create new order
      </button>
    </form>
</div>
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
        <div class="nb-table-container">
          <div class="nb-table-header text-left">Orders</div>
          <table class="table table-zebra min-w-full divide-y divide-accent " >
          <thead class="bg-base-200">
            <tr class="h-12">
              <th class="pgs-th">Amount</th>
              <th class="pgs-th">code</th>
              <th class="pgs-th">user</th>
              <th class="pgs-th">Email</th>
              <th class="pgs-th">Items</th>
              <th class="pgs-th">Comment</th>          
              <th class="pgs-th">created</th>       
              <th class="pgs-th">Status</th>
              <th class="pgs-th">Actions</th>          
            </tr>
          </thead>
          <tbody>
            {#each orders as order, i}
              <tr class="bg-base-100  outline-1 outline-transparent hover:outline-blue-500 hover:bg-blue-600/15">
                
                <td class="pgs-td-num font-mono font-bold">{formatPrice(order.price)}</td>
                <td class="pgs-td">
                  <a use:link href="/orders/{order.id}" class="pgs-hyperlink">{formatCode(order.code)}</a>
                </td>
                <td class="pgs-td">{order.user?.username}</td>
                <td class="pgs-td">{order.email}</td>
                <td class="pgs-td-num font-mono">{order.items.length}</td>
                <td class="text-center">{@html formatCommentInfo(order.comment)}</td>            
                <td class="pgs-td font-mono">
                  {@html formatDate(order.created,'New - created less than 30 minutes ago',30)}
                </td>
                <td class="text-center">
                 {#if (order.status !== '' && order.status !== null)}  
                  <div class="badge badge-primary">{order.status}</div>
                  {/if}
                </td>     
                <td class=" justify-center">
                  <div class="tooltip tooltip-info" data-tip="Edit"><a class="px-4" aria-label="Edit" use:link href="/orders/mngmt/{order.id}"><i class="fas fa-pen text-gray-500 hover:text-sky-400 cursor-pointer"></i></a></div>
                  <button class="px-4" aria-label="Delete" on:click={()=>deleteDialog(order.id, 'Are you sure you want to delete this order? This action cannot be undone!')}>
                    <div class="tooltip tooltip-info" data-tip="Delete">
                    <i class="fas fa-times-circle text-gray-500 hover:text-red-400 cursor-pointer"></i>
                  </div></button>
                </td>      
              </tr>
            {/each}           
          </tbody>
          </table>
        <div class="nb-table-footer  text-left">
          Total orders found:
          <span class="font-bold"> {orders.length}</span>
        <br>
        <br>
          Total amount: <span class="font-bold">{formatPrice(totalAmount)}</span>
        </div>
        </div>
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

<style>
  .badge {
  background-color: transparent !important;
}

</style>