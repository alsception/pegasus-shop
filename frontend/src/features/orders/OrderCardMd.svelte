<script lang="ts">
  import type { Order } from "./Order";
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";  
  import { get } from "svelte/store";
  import { formatCode, formatTime, formatCommentInfo, isNew, formatTime2, formatPrice } from "../../utils/formatting";
  import { showSuccessToast, showErrorToast } from '../../core/utils/toaster';
  import axios from 'axios';
  import { showInfoModal } from "../../utils/modal";
  import StatusMenu from "./StatusMenu.svelte";
  import OrderModal from "./OrderModal.svelte";
  import OrderDetails from "./OrderDetails.svelte";
  import { fade } from "svelte/transition";

  export let order: Order;
  export let liteView = false;

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
  let isModalOpen = false;

  function openDetailsModal() 
  {
    isModalOpen = true;
    modalOrder = order;
  }

  function toggleView() {
    isBlockView = !isBlockView;
  }

  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });


  // Fetch the orders from the backend
  onMount(() => 
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
        //handleSearch();
      }
    } catch (err) {
      error = err instanceof Error ? err.message : "Search failed";
    } finally {

    }
    window.addEventListener('keydown', handleKeydown);
    
    // Cleanup funkcija
    return () => {
      window.removeEventListener('keydown', handleKeydown);
    };
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
         * TODO: see if this works. should display error message.__
         * ??????? štaće ovo ovde???
         */

        showInfoModal("Greška prilikom učitavanja narudžbi: "+error.message);
        
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
    isModalOpen = false;
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

  function getOrderStatusColor(status: string | null | undefined): string {
    switch (status?.toUpperCase()) {
      case "READY":
      case "DELIVERED":
        return "success";
      case "CANCELLED":
      case "REFUNDED":
        return "warning";
      case "RETURNED":
        return "error";
      case "IN_PREPARATION":
        return "info";
      case "WAITING":
        return "warning";
      default:
        return "secondary";
    }
  }

  function getOrderStatusLabel(status: string | null | undefined): string {
    switch (status?.toUpperCase()) {
      case "READY":      
        return "SPREMNO";
      case "DELIVERED":
        return "SERVIRANO"
      case "CANCELLED":
      case "REFUNDED":
        return "warning";
      case "RETURNED":
        return "error";
      case "IN_PREPARATION":
        return "U PRIPREMI";
      case "WAITING":
        return "NA ČEKANJU";
      default:
        return "-";
    }
  }

  function getBgClass(status: string | null | undefined): string 
  {
    /**
     * Tip narudžbe	Boja pozadine (Dark Mode)	Boja teksta/border-a	Opis
    Žuta (Na čekanju)	bg-yellow-900/20	text-yellow-500/80	Boja ćilibara, suptilna i topla.
    Plava (U obradi)	bg-blue-900/20	text-blue-500/80	Tamna teget-plava, smirena.
    Zelena (Završeno)	bg-emerald-900/20	text-emerald-500/80	Duboka šumska zelena, odmara oči.
    */

    switch (status?.toUpperCase()) 
    {

      default:
        return "bg-base-200 ";

     /* case "WAITING":
        return "bg-[#FEBB0036]"//"bg-yellow-700/30";//"bg-[#525214]";

      case "IN_PREPARATION":
        //return "bg-[#0077FF3A]"/* blue-900/30"/;
        return "bg-base-200 "

      case "READY":
      case "DELIVERED":
        return "bg-emerald-800/30";

      case "CANCELLED":
      case "REFUNDED":
        return "warning";

      case "RETURNED":
        return "error";*/

      
    }
  }

  function getBgStyle(status: string | null | undefined): string 
  {
    /**
     * Tip narudžbe	Boja pozadine (Dark Mode)	Boja teksta/border-a	Opis
    Žuta (Na čekanju)	bg-yellow-900/20	text-yellow-500/80	Boja ćilibara, suptilna i topla.
    Plava (U obradi)	bg-blue-900/20	text-blue-500/80	Tamna teget-plava, smirena.
    Zelena (Završeno)	bg-emerald-900/20	text-emerald-500/80	Duboka šumska zelena, odmara oči.
    */

    switch (status?.toUpperCase()) 
    {
      case "WAITING":
        return "";

      case "IN_PREPARATION":
        return "";

      case "READY":
      case "DELIVERED":
        return "";

      default:
        return "secondary";
    }
  }

  /* drugi modal */
  let showModal2 = false;
  
  function openModal2() {
    showModal2 = true;
  }
  
  function closeModal2() {
    showModal2 = false;
  }


  function handleKeydown(event: { key: string; }) {
    if (event.key === 'Escape') {
      closeModal2();
    }
  }

</script>

 <!-- class:bg-inprep={order.status == 'IN_PREPARATION'}
    class:bg-wait={order.status == 'WAITING'}
    class:bg-ready={order.status == 'READY'}
    
    
    
    READY COLOR: color-mix(in oklab, oklch(0.6 0.23 142.37) 30%, transparent)
    WAITING COLOR: color-mix(in oklab, oklch(0.89 0.18 91.93) 30%, transparent)
    
    
    TODO:
    Probaj ovu boju za waiting: #fdc700

    
    -->

 <div class="rounded-xl shadow p-4 flex flex-col gap-2 h-fit w-fit {liteView ? 'w-full' : 'w-fit min-w-[21rem]'} {getBgClass(order.status)}" 
    class:card-new={isNew(order.created,10) && order.status == 'WAITING'}
   
    >
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center gap-2 w-full" style="justify-content: space-between;">
      
              <a use:link href="/orders/{order.id}"
              class="text-3xl font-extrabold text-primary pgs-hyperlink">{formatCode(order.code)}</a>
      
              <!-- <span class="badge badge-soft badge-{getOrderStatusColor(order.status)} font-mono badge-md ml-auto" style="text-transform: uppercase;">
                {getOrderStatusLabel(order.status)}
              </span> -->

              {#if isNew(order.created,10)}
                <div>
                  <div class=" tooltip tooltip-info cursor-pointer" data-tip="Stiglo prije manje od 10 minuta">
                    <span class="indicator-item badge badge-accent dark:text-black dark:bg-violet-500">novo</span>
                  </div>
                </div>
              {/if}

              <StatusMenu {order} on:orderUpdateCompleted/>

            </div>
          </div>

          <div class="flex items-center gap-2 text-sm text-primary mb-1 ">
            <div class="flex items-center gap-2 text-sm text-primary/60 mr-4">
              <i class="fas fa-user"></i>
              <span><strong>{order.user?.username}</strong></span>
            </div>        
            <div class="flex items-center gap-2 text-sm text-primary/60 mr-4">
              <i class="fas fa-chair"></i>              
              <span><strong>{order.stol ? order.stol : "-"}</strong></span>
            </div> 
            <div class="text-sm flex items-center text-primary/60 gap-2" class:hidden={liteView}>
              <i class="fas fa-clock"></i>{@html formatTime2(order.created)}
            </div>  
          </div>
          
          {#if (order.status != 'READY') && (order.comment && order.comment.toString.length > -1)}
            <div>
              <span class="indicator-item badge badge-info text-primary bg-black text-yellow-200 rounded-md" style="">
                Napomena
              </span>
              <br>
              <div class=" bg-base-300/66 dark:bg-gray-800 border-1 border-primary/30 dark:border-gray-800 text-primary p-1 font-bold py-2 px-4 rounded-md">          
                {order.comment}
              </div>
            </div>
          {/if}

          <div class="mt-2 " class:hidden={liteView}>
            <ul class="flex flex-col gap-0 bg-base-300">
              {#each order.items as item}
                <li class="flex items-center gap-0 p-2 border-0">
                  <span class="text-primary text-md font-semibold font">{item.quantity} x </span>&nbsp;
                  <span class="text-primary text-md font-semibold font"> {item.product?.name}</span>
                  {#if item.price}
                    <span class="text-xs text-gray-500 ml-auto font-mono">{formatPrice(item.price)}</span>
                  {/if}
                </li>
              {/each}
            </ul>
              <div style="align-items: end;display:grid;align-content: end; text-align: right; "
                    class=" bg-base-300">
                <span class="text-lg font-bold text-primary p-2 font-mono">{formatPrice(order.price)}</span>
              </div>
          </div>

          <div class="flex gap-2 mt-2">
            <!-- use:link href="/orders/{order.id} -->
            <button class="btn btn-sm btn-ghost text-primary/80"
              on:click={openModal2}>Detalji</button>

            <button class="btn btn-sm btn-ghost"  aria-label="Obriši" 
              on:click={()=>deleteDialog(order.id, 'Are you sure you want to delete this order? This action cannot be undone!')}>
                <z
                class=" tooltip tooltip-info tooltip-top"
                data-tip="Obriši"
              >
                <i class="fas fa-trash text-primary/60" aria-label="Obriši"></i>  
                      </z>
              
            </button>
            {#if order.status == 'IN_PREPARATION'}
            <button class="btn btn-sm btn-success text-primary-content"
              on:click={alert('hello')}><i class="fa fa-check text-md cursor-pointer"></i>Spremno</button>
            {/if}  
            {#if order.status == 'WAITING'}
            <button class="btn btn-sm btn-info text-primary-content"
              on:click={alert('hello')}><i class="fa fa-fire text-md cursor-pointer"></i>U pripremi</button>
            {/if}  
          </div>
          
        </div>


      <!-- ovde cemo staviti order details modal -->
   <!--  <OrderModal
      isOpen={isModalOpen}
      order={modalOrder}
      on:close={closeModal}
    />
 -->


<!-- Modal -->
{#if showModal2}
  <div class="modal modal-open  pt-10" style="backdrop-filter: blur(10px);">
  <div class="modal-box max-h-[90vh] w-11/12 max-w-5xl p-0 flex flex-col bg-base-200">
    
    <!-- Fixed Header -->
    <div class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300">
      <h3 class="font-bold text-lg">Detalji narudžbe</h3>
    </div>
    
    <!-- Scrollable Content -->
    <div class="overflow-y-auto flex-1 px-6 py-4">
      <OrderDetails ID={order.id}></OrderDetails>
    </div>
    
    <!-- Fixed Footer -->
    <div class="sticky bottom-0 bg-base-100 z-10 px-6 py-4 border-t border-base-300">
      <div class="flex justify-end gap-2">
        <button class="btn" on:click={closeModal2}>Zatvori</button>
      </div>
    </div>
    
  </div>
  
  <!-- Glass Backdrop -->
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <div class="modal-backdrop"
    on:click={closeModal2}
  ></div>
</div>
{/if}

<style>
  .badge {
/*   background-color: transparent !important;
 */}

.card-new {
  border: 2px solid 2px solid #6933ff;
/*   border-radius: 22px;
 */  /*padding: 20px;*/
  animation: pulse 6s infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(111, 0, 255, 0.7);
  }
  50% {
    box-shadow: 0 0 0 21px rgba(0, 4, 255, 0);
  }
}
.bg-wait{
  background-color: #acab5e45;
}
.bg-inprep{
  background-color: #5e86ac45;
}
.bg-ready{
  background-color: #6bac5e45;
}
</style>
