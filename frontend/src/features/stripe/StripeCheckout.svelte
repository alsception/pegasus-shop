<script lang="ts">
  import { onMount } from 'svelte';
  import { loadStripe, type Stripe, type StripeElements } from '@stripe/stripe-js';

  // Ovo dobijaš od Jave (npr. preko props-a ili stora)
  export let clientSecret: string = 'pi_3THQ7zQdWEpRyYG230k9oGBd_secret_33pyAVsjcWEfcAR6JvqamSwqv';

  let stripe: Stripe | null = null;
  let elements: StripeElements | null = null;
  let errorMessage: string = "";
  let isLoading: boolean = false;

  onMount(async () => {
    // 1. Inicijalizacija
    stripe = await loadStripe('pk_test_51TGoKKQdWEpRyYG2bsUlvdtvZLd5hugwSPwkj3UPTpfqRgwhLJOWWu5zocfQqxd9BpXJ9IWfCFWe0D78wrIZ4ORF00fGwQnY5c');
    
    if (stripe) {
      // 2. Kreiranje elemenata sa clientSecret-om
      elements = stripe.elements({ clientSecret });
      
      // 3. Kreiranje i montiranje Payment Elementa
      const paymentElement = elements.create('payment');
      paymentElement.mount('#payment-element');
    }
  });

  async function handleSubmit() {
    if (!stripe || !elements) return;

    isLoading = true;

    // 4. Potvrda plaćanja
    const { error } = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: `${window.location.origin}/completion`,
      },
    });

    // Ako dođe do greške (npr. odbijena kartica), execution se nastavlja ovde
    if (error) {
      errorMessage = error.message ?? "Došlo je do greške prilikom plaćanja.";
    }
    
    isLoading = false;
  }
</script>

<div class="page-container bg-base-200 dark:bg-black">
    <div class="w-full max-w-3xl card bg-base-200 dark:bg-black">
        <form on:submit|preventDefault={handleSubmit}>
            <div id="payment-element"></div>
        
            <button disabled={!stripe || isLoading}>
                {isLoading ? "Obrađujem..." : "Plati"}
            </button>
            
            {#if errorMessage}
                <p class="error">{errorMessage}</p>
            {/if}
        </form>
    </div>
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
        min-height: 100vh;       /* Uzima celu visinu ekrana */
        background-color: #f9f9f9; /* Opciona boja pozadine */
    }

    /* Opcioni stil za samu formu da izgleda lepše */
    .card {
        
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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