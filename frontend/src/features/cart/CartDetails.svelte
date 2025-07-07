<script lang="ts">
  import { onMount } from "svelte";
  import { auth } from "../../core/services/store";
  import Login from "../../core/auth/login.svelte";
  import axios from "axios";
  import type { PGSCart } from "./Cart";

  document.title = 'Cart | Pegasus'
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  let isAuthenticated = false;
  let loading: boolean = false;
  let cart: PGSCart | null = null;
  let error: any = null;

  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  //AXIOS DEf
  const axiosInstance = axios.create(
  {
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

  onMount(async () => {
    await loadCart();
  });

  async function loadCart() 
  {
    loading = true;
    try{
      const response = await axiosInstance.get<PGSCart>('cart');
      cart = response.data;
    } catch (err) {
      error = (err instanceof Error ? err.message : "Unknown error");
    } finally {
      loading = false;
    }
  };  

  function formatPrice(price: number): string {
    return new Intl.NumberFormat('de-DE', {
      style: 'currency',
      currency: 'EUR',
    }).format(price);
  }

  function goToCheckout() {
    window.location.href = "/checkout"; // Putanja do početne stranice
  }
  
  function updateCart(item: any){
    //Send to server actually
    /* console.log(item);
      if (!cart || !cart.items) return;

    const index = cart.items.findIndex(i => i.id === item.id);
    if (index !== -1) {
      cart.items[index] = item;
    } */
  }
</script>

<div class="w-full max-w-4xl mx-auto p-4">

{#if !$auth.isAuthenticated}

  <Login />

{:else}

  {#if loading}

    <!-- Overlay loading animation -->
    <div
      class="absolute inset-0 flex flex-col justify-center items-center z-10 rounded-2xl"
    >
      <span
        class="loading loading-infinity mb-2 text-blue-500"
        style="width: 4rem; height: 4rem;"
      ></span>
    </div>

  {:else if error}

    <div class="flex justify-center items-center h-64 text-red-500 text-2xl mt-4 text-center dark:text-red-400">
      <h3>Error: {error}</h3>
    </div>
    
  {:else}  

    {#if cart}

    <div class=" mx-auto p-4 sm:p-6 bg-white dark:bg-slate-900 rounded-2xl shadow-md mt-6 sm:mt-10">
      <h2 class="text-xl sm:text-2xl font-bold mb-4 text-center">Pegasus Shop - My Cart</h2>
      
      <div class="font-mono">

        {#if cart.items && cart.items.length > 0}

          <div class="divide-y divide-gray-200 dark:divide-slate-700">
            
            {#each cart.items as item (item.id)}

              <div class="py-4 grid grid-cols-3 gap-2 items-center">
                <div class="col-span-2">
                  <p class="text-base sm:text-lg font-medium truncate">{item.product.name}</p>
                  <p class="text-xs sm:text-sm text-gray-500">Quantity:   
                    <input
                    type="number"
                    class="input input-ghost w-20 validator"
                    required
                    placeholder="Type a number between 1 to 10"
                    min="0"
                    max="100"
                    on:change={() => updateCart(item)}
                    on:input={() => updateCart(item)}
                    bind:value={item.quantity}
                  />

                  <p class="validator-hint">Must be between be 1 to 10</p>
<!--                     <input type="number" id="myNumber" value="{item.quantity}"/>                    
 -->                 
                  <div class="relative flex items-center hidden">
                    <button 
                      type="button" 
                      class="w-8 h-8 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-l-md border border-gray-300 text-gray-700 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      -
                    </button>
                    <!-- type="number"  -->
                    <input 
                      
                      id="myNumber" 
                      value="{item.quantity}"
                      min="1"
                      class="w-12 h-8 text-center appearance-none border border-x-0 border-gray-300 bg-white text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 spin-button-none"
                    />
                    <button 
                      type="button" 
                      class="w-8 h-8 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-r-md border border-gray-300 text-gray-700 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      +
                    </button>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-xs sm:text-sm text-gray-500 mb-1">
                    {item.quantity} x {formatPrice(item.product.basePrice)}
                  </div>
                  <span class="text-sm sm:text-base font-semibold">
                    {formatPrice(item.quantity * item.product.basePrice)}
                  </span>
                </div>
              </div>
            {/each}
          </div>
        {:else}
          <p class="text-center text-gray-500 py-4">Your cart is empty</p>
        {/if}
        
        <div class="space-y-4 mt-6 divide-y divide-gray-200 dark:divide-slate-700">
          <div class="pt-4 flex justify-between items-center">
            <p class="text-base sm:text-lg font-semibold">Subtotal:</p>
            <p class="text-lg sm:text-xl font-bold">{formatPrice(cart.subtotal || 0)}</p>
          </div>
          
          <div class="pt-4 flex justify-between items-center">
            <p class="text-base sm:text-lg font-semibold">Tax:</p>
            <p class="text-lg sm:text-xl font-bold">{formatPrice(cart.tax || 0)}</p>
          </div>
          
          <div class="pt-4 flex justify-between items-center">
            <p class="text-base sm:text-lg font-semibold">Shipping:</p>
            <p class="text-lg sm:text-xl font-bold">{formatPrice(cart.shipping || 0)}</p>
          </div>
          
          <div class="pt-4 flex justify-between items-center">
            <p class="text-base sm:text-lg font-semibold">Total:</p>
            <p class="text-lg sm:text-xl font-bold">{formatPrice(cart.totalPrice || 0)}</p>
          </div>
        </div>
        <br>
        <div class="w-full text-center">
          <a href="#/checkout" class="btn btn-primary"> Proceed to Checkout</a>
        </div>
      </div>
    </div>    

    {:else}
      <p>Nothing to see here</p>
    {/if}  
  {/if}  
{/if}

</div>