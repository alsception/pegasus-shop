<script lang="ts">
  import { onMount } from "svelte";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import axios from "axios";
  import type { Cart } from "./Cart";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import EmptyImg1 from "../../assets/img/empty-amico-1.svg"; 
  import EmptyImg2 from "../../assets/img/empty-amico.svg"; 
  import EmptyImg3 from "../../assets/img/empty-bro-1.svg"; 
  import EmptyImg4 from "../../assets/img/empty-bro.svg"; 
  import EmptyImg5 from "../../assets/img/empty-pana.svg"; 
  import { link } from "svelte-spa-router";

  const emptyImages = [EmptyImg1, EmptyImg2, EmptyImg3, EmptyImg4, EmptyImg5];

  // Get a random image (TODO: see if this has sense, to load many images at once )
  const randomImage = emptyImages[Math.floor(Math.random() * emptyImages.length)]; 

  document.title = 'Cart | Pegasus'
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  let isAuthenticated = false;
  let loading: boolean = false;
  let cart: Cart | null = null;
  let error: any = null;


  /********************************************************************
   * TODO FIX 0 ERROR
   * *
   * ************************************************************
  */

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
      const response = await axiosInstance.get<Cart>('cart');
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
  
  async function updateCart(item: any) {
    if (!cart || !item) return;
    loading = true;
    error = null;
    try {
      const response = axiosInstance.put('/cart/update', null, {
        params: {
          productId: item.product.id,
          quantity: item.quantity
        }
      });
      cart = (await response).data.cart;
    } catch (err) {
      error = (err instanceof Error ? err.message : "Unknown error");
    } finally {
      loading = false;
    }
  }

  function handleQuantityChange(event: Event, item: any) {
    const input = event.target as HTMLInputElement;
    item.quantity = Number(input.value);
    updateCart(item);
  }

  async function deleteCartItem(productId: number) {
    if (!cart) return;
    loading = true;
    error = null;
    try {
      await axiosInstance.delete('/cart/delete', {
        params: {
          productId
        }
      });
      await loadCart();
    } catch (err) {
      error = (err instanceof Error ? err.message : "Unknown error");
    } finally {
      loading = false;
    }
  }
  
  function cancel(event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }) 
  {
    window.location.href = "#/products";
  }
</script>

<div class="w-full max-w-4xl mx-auto p-4">

{#if !$auth.isAuthenticated}

  <Login />

{:else}

  {#if error}

    <ErrorDiv {error} />
    
  {:else}  

    {#if loading}

      <LoadingOverlay/>
  
    {/if}
  
    {#if cart}

    <div class="nb-card mx-auto  bg-base-100 /*rounded-2xl*/ mt-6 sm:mt-10 " style="min-width: 568px; transform: none">
      <h2 class="text-xl sm:text-2xl font-bold mb-4 text-center pt-4">Pegasus Shop - My Cart</h2>
      
      <div class="font-mono">

        {#if cart.items && cart.items.length > 0}

        <div class="p-4 sm:p-6">
          <div class="divide-y divide-gray-200 dark:divide-slate-700">
            
            {#each cart.items as item (item.id)}

              <div class="py-4 grid grid-cols-3 gap-2 items-center">
                <div class="col-span-2 flex flex-col gap-2">
                  
                  <div class="flex items-center gap-4">
                      <div class="flex items-center rounded-md overflow-hidden">
                        <button 
                          type="button" 
                          class="btn btn-ghost"
                          on:click={() => {
                            if (item.quantity > 1) {
                              item.quantity = item.quantity - 1;
                              updateCart(item);
                            }
                          }}
                          aria-label="Ukloni"
                        >
                          -
                        </button>
                        <input 
                          id="myNumber" 
                          value={item.quantity}
                          min="1"
                          class="w-12 h-8 text-center"
                          on:change={(e) => handleQuantityChange(e, item)}
                        />
                        <button 
                          type="button" 
                          class="btn btn-ghost"
                          on:click={() => {
                            if (item.quantity < 100) {
                              item.quantity = item.quantity + 1;
                              updateCart(item);
                            }
                          }}
                          aria-label="Dodaj"
                        >
                          +
                        </button>
                      </div>
                                        <p class="text-base sm:text-md font-medium truncate">{item.product.name}</p>

                  </div>
                      

                  <!-- Move delete button below -->
                  <div class="flex items-center">
                    <div class="tooltip tooltip-info" data-tip="Delete">
                      <button
                        type="button"
                        class="btn primary"
                        aria-label="Delete"
                        on:click={() => deleteCartItem(item.product.id)}
                      >
                        <i class="fa fa-trash text-md text-gray-500 hover:text-red-400 cursor-pointer"></i>
                      </button>
                    </div>
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
          <div class="space-y-4 mt-6 divide-y divide-gray-200 dark:divide-slate-700">
        
                      <div class="divide-y divide-gray-200 dark:divide-slate-700"></div>

            <div class="pt-4 flex justify-between items-center">
              <p class="text-xl sm:text-md font-semibold">Total:</p>
              <p class="text-md sm:text-xl font-bold">{formatPrice(cart.totalPrice || 0)}</p>
            </div>
        </div>
        <br>
        </div>
        <div class="col-span-full flex p-4 justify-end bg-secondary/10">
          <button type="button" on:click={cancel} class=" m-3 btn btn-secondary">
            Close
          </button>
          <a href="#/checkout" class=" m-3 btn btn-primary"> Confirm</a>
        </div>

        {:else}
          <p class="text-center text-gray-500 py-4">Your cart is empty. Add some <a use:link href="/products" class="pgs-hyperlink">products</a>
            <img src="{randomImage}" alt="Cart empty" />
          </p>
        {/if}       
        
      </div>
    </div>    
    
    {/if}  
  {/if}  
  {/if}  

</div>

<style>
  /**need to remove this*/
  button:focus {
  outline: none;
  box-shadow: none;
  border-color: none;
}
</style>