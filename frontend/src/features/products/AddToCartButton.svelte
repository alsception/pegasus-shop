<script lang="ts">
  /**
   * We will pass parameters to this button:
   * 1. product: The product to be added to the cart.
   * 2. addToCart: A function to call when the button is clicked.
   * 3. loadingItems: A set of productIds to indicate if the loading is in progress to disable button.
   * 4. addedItems: set of product ids to show that was loaded
   */

  import { addedItems, addToCart, loadingItems } from "./ProductService";
  import { DotLottieSvelte } from "@lottiefiles/dotlottie-svelte";

  export let product;
  export let width;

  const lottieAnimationUrl = '/lottie/check2.lottie';//or check2.lottie or success3

</script>

{#if product.active === false || product.stock === 0}

  <button
    class="btn default bg-gray-600 button-fx w-full flex items-center justify-center"
    style="width: {width}"
    disabled
    aria-label="Product not available"
  >
    <span class="">Not available</span>
  </button>

{:else if $loadingItems.has(product.id)}  

  <button
    class="btn btn-secondary"
    style="width: {width}; height: {width};"
    aria-label="Dodaj"
  >
    <span class="loading loading-spinner loading-xl bg-primary/70"></span>
  </button>

{:else if $addedItems.has(product.id)}  

  <button on:click={() => addToCart(product.id)}>
    <div
      class="lottie-container bg-secondary"
      style="width: {width}; height: {width};"
    >
      <DotLottieSvelte
        src={lottieAnimationUrl}
        loop={false}
        autoplay={true}
        speed={2}
      />
    </div>
  </button>

{:else}

  <button
    class="btn btn-secondary"
    style="width: {width}; height: {width};transition-delay: 0.3s;"
    aria-label="Dodaj"
    on:click={() => addToCart(product.id)}
  >
    <i class="fa fa-plus text-warning text-xl" aria-hidden="true"></i>
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
</style>
