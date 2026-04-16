<script lang="ts">

/**
   * We will pass parameters to this button:
   * 1. product: The product to be added to the cart.
   * 2. addToCart: A function to call when the button is clicked.
   * 3. loadingItems: A set of productIds to indicate if the loading is in progress to disable button.
   * 4. addedItems: set of product ids to show that was loaded
   */

  import { addedItems, addToCart, loadingItems, ProductService } from "./ProductService";

  export let product;
  export let width;

  const lottieAnimationUrl = '/lottie/check2.lottie';//or check2.lottie or success3

</script>

{#if product.active === false || product.stock === 0}

  <button
    class="btn default bg-gray-600 button-fx w-full flex items-center justify-center"
    style="width: {width}"
    disabled
    aria-label="Nedostupno"
  >
    <span class="">Nedostupno</span>
  </button>

{:else if $loadingItems.has(product.id)}  

  <button
    class="btn btn-ghost bg-base-100 dark:bg-primary-content/80"
    style="width: {width}; height: {width};"
    aria-label="Dodaj u košaricu"
  >
    <span class="loading loading-spinner loading-xl bg-primary/70"></span>
  </button>

{:else if $addedItems[product.id] > 0}  

    <div class="flex items-center bg-base-100 dark:bg-primary-content/80 rounded-lg border-0 overflow-hidden h-full shadow-sm cursor-pointer pulsing2"
    style="height: {width};border-radius: 1000px; padding:4px;">
        <button 
            class="px-3 bg-base-300/30 hover:bg-base-300/60 text-info font-bold transition-colors cursor-pointer text-3xl rounded-full"
            on:click={() => ProductService.updateQuantity(product.id, -1)}
        >
            -
        </button>
        
        <span class="px-2 font-bold min-w-[20px] text-center text-2xl text-info">
            {$addedItems[product.id]}
        </span>
        
        <button 
            class="px-3 bg-base-300/30 hover:bg-base-300/60 text-info font-bold transition-colors cursor-pointer text-3xl rounded-full"
            on:click={() => ProductService.updateQuantity(product.id, 1)}
        >
            +
        </button>
    </div>

{:else}

  <button
    class="btn btn-ghost bg-base-100 hover:bg-base-300 dark:bg-primary-content/80 dark:hover:border-2 border-accent/20 group"
    style="width: {width}; height: {width};/*transition-delay: 0.3s;*/"
    aria-label="Dodaj u košaricu"
    title="Dodaj u košaricu"
    on:click={() => addToCart(product.id)}
  >
    <i class="fa fa-plus text-primary/40 group-hover:text-primary text-xl" aria-hidden="true"></i>
  </button>
{/if}

<style>
  .lottie-container {
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: visible; /* Osigurava da ništa ne seče animaciju */
    /* 9999px garantuje oblik pilule bez obzira na širinu */
    border-radius: 9999px !important;
  }

  .lottie-container :global(dotlottie-player),
  .lottie-container :global(canvas),
  .lottie-container :global(svg) {
    /* Ovo ovde sluzi da se poveca animacija bez da se poveca dugme */
    transform: scale(1.5);
    transform-origin: center;
  }
  button {
    /* Centriranje sadržaja */
    display: flex;
    align-items: center;
    justify-content: center;
  }

.pulsing2 {
  /* Osnovni stilovi */
  box-shadow: rgba(0, 26, 255, 0.357) 0px 0px 0px 10.3387px;
  border: 2px solid #6933ff79; /* Popravio sam tvoj dupli '2px solid' */
  
  /* Animacija: ime | trajanje | timing-function | broj ponavljanja | fill-mode */
  animation: pulse2 0.7s ease-out 1 forwards;
}
@keyframes pulse2 {
  0% {
    box-shadow: 0 0 0 0 rgba(0, 17, 255, 0.7);
  }
  70% {
    /* Senka se raširila, ali je još uvek malo vidljiva */
    box-shadow: 0 0 0 28px rgba(0, 4, 255, 0);
  }
  100% {
    /* Na kraju je potpuno nevidljiva i ostaje na nuli do novog kruga */
    box-shadow: 0 0 0 0 rgba(0, 4, 255, 0);
  }
}
</style> 
