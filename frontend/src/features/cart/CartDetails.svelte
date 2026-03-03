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
  import { formatPrice } from "../../utils/formatting";

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
<div class="w-full max-w-4xl mx-auto p-2 sm:p-4">

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

    <div class="text-primary mx-auto bg-base-200 mt-6 sm:mt-10 w-full max-w-2xl" style="transform: none">
      <div class="p-0 bg-accent text-primary-content/80 dark:text-primary/80 rounded-t">
        <h2 class="text-lg sm:text-2xl font-bold p-1 text-center">Košarica 1/2</h2>
      </div>
      
      <div class="font-mono">

        {#if cart.items && cart.items.length > 0}

        <div class="p-2 sm:p-2">
          <div class="divide-y divide-primary/10">
            
            {#each cart.items as item (item.id)}

              <div class="py-1 grid grid-cols-1 sm:grid-cols-3 gap-0 items-center">
                
                <div class="sm:col-span-2 flex flex-col gap-0.5">
                  
                  <div class="flex items-center gap-1">

                    <div class="flex items-center rounded-md overflow-hidden border border-primary/10 shrink-0 h-6">
                      <button 
                        type="button" 
                        class="btn btn- btn-sm text-primary px-3 bg-base-100 rounded-none"
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
                        class="w-12 h-8 text-center text-primary bg-transparent border-0"
                        on:change={(e) => handleQuantityChange(e, item)}
                      />
                      <button 
                        type="button" 
                        class="btn btn-ghost btn-sm text-primary px-3 bg-base-100 rounded-none"
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

                    <p class="text-sm sm:text-base font-medium text-primary px-1">{item.product.name} ({formatPrice(item.product.basePrice)})</p>
                  </div>
                      
                  <div class="flex items-center">
                    <div class="tooltip tooltip-info" data-tip="Delete">
                      <button
                        type="button"
                        class="btn btn-ghost btn-sm text-error h-2"
                        aria-label="Delete"
                        on:click={() => deleteCartItem(item.product.id)}
                      >
                        <i class="fa fa-remove text-md cursor-pointer"> </i>
                       <span class="pt-1"> Ukloni</span>
                      </button>
                    </div>
                  </div>
                </div>

                <div class="text-right sm:text-right pt-0 sm:pt-0 relative -top-6 h-0  sm:top-0"
                  >
                  <span class="text-sm sm:text-base font-semibold text-primary">
                    {formatPrice(item.quantity * item.product.basePrice)}
                  </span>
                </div>
              </div>
            {/each}
          </div>

          <div class="space-y-4 mt-4 divide-y divide-primary/20">
            <div class="divide-y divide-gray-200 dark:divide-slate-700"></div>

            <div class="pt-4 px-20 flex justify-between items-center">
              <p class="text-lg sm:text-xl font-semibold text-primary">Ukupno:</p>
              <p class="text-xl sm:text-2xl font-bold text-primary">{formatPrice(cart.totalPrice || 0)}</p>
            </div>
          </div>
          <br>
        </div>

        <div class="flex flex-row gap-2 p-8 justify-between bg-base-300/25">
          <button type="button" on:click={cancel} class="btn btn-neutral text-primary/80 mr-12">
             <i class="fa fa-arrow-left text-md cursor-pointer"> </i>Zatvori
          </button>
          <a href="#/checkout" class="btn btn-primary bg-success">
             <i class="fa fa-check text-md cursor-pointer"> </i>Potvrdi</a>
        </div>

        {:else}

          <p class="text-center text-gray-500 py-8 px-4">
            Košarica je prazna.  <a use:link href="/products" class="pgs-hyperlink">Dodaj proizvod</a>
          </p>
          <div class="flex justify-center pb-4">
            <img src="{randomImage}" alt="Cart empty" class="max-w-xs" />
          </div>
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