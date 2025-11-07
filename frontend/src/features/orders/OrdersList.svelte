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
  import { showInfoModal } from "../../utils/modal";

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
  let isBlockView = true;

  function toggleView() {
    isBlockView = !isBlockView;
  }

  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });


  // Fetch the orders from the backend
  onMount(async () => 
  {
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
    } 
    catch (error: any) 
    {
        console.error('Error during search:', error);


        /**
         * TODO: see if this works. should display error message.
         */

        showInfoModal(error.message);
        
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
    
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(price);
  }

  function getOrderStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case "PAID":
      case "DELIVERED":
        return "success";
      case "PENDING":
      case "REFUNDED":
        return "warning";
      case "RETURNED":
        return "error";
      case "SHIPPED":
        return "info";
      case "PROCESSING":
        return "accent";
      case "CREATED":
        return "info";
      case "CANCELLED":
      default:
        return "secondary";
    }
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

<!-- Add toggle button to switch views -->
<div class="flex justify-end mb-4">
  <button class="btn btn-secondary" on:click={toggleView}>
    <i class="fas fa-th-large"></i>
    {#if isBlockView}
      Table view
    {:else}
      Card view
    {/if}
  </button>
</div>

<!-- Show each item in the order card (Block view) -->
{#if isBlockView}
  <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 2xl:grid-cols-5 gap-16 p-16">
    {#each orders as order}
      <div class="bg-white dark:bg-slate-900 rounded-xl shadow p-4 flex flex-col gap-2">
        <!-- Nicer card header -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center gap-2">
            <span class="text-xl font-extrabold text-primary">#{formatCode(order.code)}</span>
            <span class="badge badge-soft badge-{getOrderStatusColor(order.status)} font-mono badge-lg" style="text-transform: uppercase;">
              {order.status}
            </span>
          </div>
        </div>
        <div class="flex items-center gap-2 text-sm text-primary mb-1">
          <i class="fas fa-user"></i>
          <span>Konobar: <strong>{order.user?.username}</strong></span>
        </div>
       
        <div class="text-sm">{@html formatCommentInfo(order.comment)}</div>
        <div class="text-sm">⏰ {@html formatDate(order.created,'Novo',5)}</div>
        <!-- Nicer items list -->
        <div class="mt-2 bg-base-100">
          <ul class="flex flex-col gap-0">
            {#each order.items as item}
              <li class="flex items-center gap-0 p-2 rounded bg-base-100 dark:bg-base-100 border-1 border-base-300">
                <span class="font-bold text-primary">{item.quantity} x </span>&nbsp;
                <span class="font-bold text-primary"> {item.product?.name ?? item.name}</span>
                {#if item.price}
                  <span class="text-xs text-gray-500 ml-auto">{formatPrice(item.price)}</span>
                {/if}
              </li>
            {/each}
          </ul>
                    <span class="text-lg font-bold text-primary p-2">{formatPrice(order.price)}</span>

        </div>
        <div class="flex gap-2 mt-2">
          <a class="btn btn-sm btn-ghost" use:link href="/orders/{order.id}">Details</a>
          <button class="btn btn-sm btn-ghost text-red-700" on:click={()=>deleteDialog(order.id, 'Are you sure you want to delete this order? This action cannot be undone!')}>
            Delete
          </button>
        </div>
      </div>
    {/each}
  </div>
{:else}
  <!-- Table view (existing code) -->
  <div class="max-w-[2048px] w-full overflow-x-auto rounded-lg align-middle text-center mx-auto">
    <div class="nb-table-container">
      <div class="nb-table-header text-left">Orders</div>
      <table class="table table-zebra min-w-full divide-y divide-accent " >
      <thead class="bg-base-200">
        <tr class="h-12">
          <th class="pgs-th">code</th>
          <th class="pgs-th">Amount</th>
          <th class="pgs-th">Status</th>
          <th class="pgs-th">user</th>
          <th class="pgs-th">Items</th>
          <th class="pgs-th">Comment</th>          
          <th class="pgs-th">created</th>       
          <th class="pgs-th">Actions</th>          
        </tr>
      </thead>
      <tbody>
        {#each orders as order, i}
          <tr class="bg-base-100  outline-1 outline-transparent hover:outline-blue-500 hover:bg-blue-600/15">
            <td class="pgs-td">
              <a use:link href="/orders/{order.id}" class="pgs-hyperlink">{formatCode(order.code)}</a>
            </td>
            <td class="pgs-td-num font-mono font-bold">{formatPrice(order.price)}</td>
            <td class="text-center">
              {#if order.status}
                <span class="badge badge-soft badge-{getOrderStatusColor(order.status)} font-mono badge-sm" style="text-transform: uppercase;">
                  {order.status}
                </span>
              {/if}
            </td>
            <td class="pgs-td">{order.user?.username}</td>
            <td class="pgs-td-num font-mono">{order.items.length}</td>
            <td class="text-center">{@html formatCommentInfo(order.comment)}</td>            
            <td class="pgs-td font-mono">
              {@html formatDate(order.created,'New - created less than 30 minutes ago',30)}
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
    <div class="nb-table-footer text-left bg-secondary" style="background-color: var(--color-base-100);">
      Total orders found:
      <span class="font-bold"> {orders.length}</span>
    <br>
    <br>
      Total amount: <span class="font-bold">{formatPrice(totalAmount)}</span>
    </div>
    </div>
  </div>
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

<style>
  .badge {
/*   background-color: transparent !important;
 */}

</style>