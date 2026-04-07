<script lang="ts">
  import axios from "axios";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { params, link } from "svelte-spa-router";
  import api from "../../core/services/client";
  import EmptyImg1 from "../../assets/img/empty-amico-1.svg";
  import EmptyImg2 from "../../assets/img/empty-amico.svg";
  import EmptyImg3 from "../../assets/img/empty-bro-1.svg";
  import EmptyImg4 from "../../assets/img/empty-bro.svg";
  import EmptyImg5 from "../../assets/img/empty-pana.svg";
  import type { Order } from "../orders/Order";
  import { loadStripe, type Stripe, type StripeElements } from '@stripe/stripe-js';
  import { formatPrice } from "../../utils/formatting";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
  const STRIPE_PUBLIC_KEY = import.meta.env.VITE_STRIPE_PUBLIC_KEY;

  document.title = "Checkout | Barbacoa";

  let ID: number | string;
  let orderNumber: number | string;
  let loading: boolean = false;
  let error: string | null | any = null;
  let order: Order | null | undefined = null;
  let clientSecret: any = null;
  let stripe: Stripe | null = null;
  let elements: StripeElements | null = null;
  let isLoading: boolean = false;
  let errorMessage: string = "";

  $: {
    if ( $params?.id )
    {
      ID = Number($params.id);
      if( ID!=0 ) 
      {        
        fetchOrderAndCreatePaymentIntent(ID); // reactively fetch when id changes, if id=0 backend will create new
      }
    }
  }   

  const emptyImages = [EmptyImg1, EmptyImg2, EmptyImg3, EmptyImg4, EmptyImg5];

  // Get a random image (TODO: see if this has sense, to load many images at once )
  const randomImage =
    emptyImages[Math.floor(Math.random() * emptyImages.length)];


  //AXIOS DEf
  const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Add Bearer token if available
  axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("token"); // or getToken() if you have a helper
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  /**
   * Prvo ucitavamo order, jer nam treba 
   */
  async function fetchOrderAndCreatePaymentIntent(id: string | number) 
  {    
    loading = true;

    try 
    {
      let data = await api<Order>("/orders/" + id, {
        method: "GET",
      });
      
      error = null;
      order = data;

      await createPaymentIntent();

      initStripe();      
    } 
    catch (err) 
    {
      error = err instanceof Error ? err.message : "Greška prilikom učitavanja narudžbe";
    } 
    finally 
    {
      loading = false;
    }
  }

  /**
   * Ova funkcija ucitava Stripe, treba mu public key koji se nalazi u .env i stripe dashboardu
   */
  async function initStripe()
  {
    stripe = await loadStripe(STRIPE_PUBLIC_KEY);
    
    if (stripe) 
    {
      // 2. Kreiranje elemenata sa clientSecret-om
      elements = stripe.elements({ clientSecret });
      
      // 3. Kreiranje i montiranje Payment Elementa
      const paymentElement = elements.create('payment');
      paymentElement.mount('#payment-element');
    }
  }

  /**
   * Ova funkcija salje na backend koji ce ucitati order i izracunati i vratiti client secret
   */
  async function createPaymentIntent() 
  {
    const paymentUrl = "/payments/create-payment-intent"
    isLoading = true;

    const paymentData = {
        orderId: ID 
    };

    try 
    {
      let result = await api<any>(paymentUrl, {
        method: "POST",
        body: JSON.stringify(paymentData) // Ovo pakuje podatke za Javu
      });

      clientSecret = result.clientSecret;
    } 
    catch (err) 
    {
      console.error(error);
      error = err;
    } 
    finally 
    {
     isLoading = false;   
    }
  }

  async function handleSubmit() 
  {
    if (!stripe || !elements) return;

    isLoading = true;

    // 4. Potvrda plaćanja
    const { error } = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: `${window.location.origin}/completion?order=`+order?.code,
      },
    });

    // Ako dođe do greške (npr. odbijena kartica), execution se nastavlja ovde
    if (error) 
    {
      errorMessage = error.message ?? "Došlo je do greške prilikom plaćanja.";
    }
    else 
    {
      //Ovde se n desava nista, Stripe preusmerava na novu stranicu
    }
    
    isLoading = false;
  }
</script>

<div class="w-full max-w-4xl mx-auto p-2 sm:p-4">

  {#if !$auth.isAuthenticated}
    <Login />
  {:else}
    
  {#if loading}
      
    <LoadingOverlay />

    {:else if error}
      
      <ErrorDiv {error} />

    {:else}

      <div
        class="text-primary mx-auto bg-base-200 dark:bg-black lg:dark:bg-base-200 mt-6 sm:mt-10 w-full max-w-2xl"
        style="transform: none"
      >
        <div
          class="p-0 bg-primary/10 text-primary-content/80 dark:text-primary/80 rounded-t-md"
        >
          <h2 class="text-primary text-lg sm:text-2xl font-bold p-1 text-center h-14 pt-3">
            Plaćanje
          </h2>
        </div>

        {#if order}

        <div class="page-container bg-base-200 dark:bg-black">
          <div class="w-full max-w-3xl card bg-base-200 dark:bg-black">
              <div class="pt-0 px-0 flex justify-end items-end mb-4">
                  <div class="text-lg sm:text-xl text-primary/70 mr-6 mb-0.5">
                    Iznos:
                  </div>
                  <div class="text-xl sm:text-2xl font-bold font-mono text-primary mb-0">
                    { formatPrice(order.price) }
                  </div>
                </div>  

              <form on:submit|preventDefault={handleSubmit}>
                  <div id="payment-element"></div>
              
                  <button disabled={!stripe || isLoading}>
                    {#if isLoading}
                      <span class="loading loading-spinner loading-xs"></span>
                    {:else}
                      Plati
                    {/if}
                  </button>
                  
                  {#if errorMessage}
                      <p class="error">{errorMessage}</p>
                  {/if}
              </form>
          </div>
        </div>

        {:else}

            <p class="text-center text-gray-500 py-8 px-4">
              Nepoznata narudžba <a
                use:link
                href="/products"
                class="pgs-hyperlink">Dodaj proizvod</a
              >
            </p>
            <div class="flex justify-center pb-4">
              <img src={randomImage} alt="Cart empty" class="max-w-xs" />
            </div>
            
          {/if}

      </div>
    {/if}
  {/if}
</div>

<style>
  .error { color: red; margin-top: 10px; }
  button { margin-top: 20px; padding: 10px; cursor: pointer; }
  button:disabled { opacity: 0.5; cursor: not-allowed; }
  /* Centriranje na celom ekranu */
  .page-container {
      display: flex;
      justify-content: center; /* Horizontalno centriranje */
      align-items: center;     /* Vertikalno centriranje */
  }
  .card {
    width: 100%;
  }
  button {
      width: 100%;
      margin-top: 20px;
      padding: 12px;
      background-color: #635bff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: bold;
  }

  button:disabled {
      opacity: 0.5;
  }

  .error {
      color: #df1b41;
      margin-top: 10px;
  }
</style>