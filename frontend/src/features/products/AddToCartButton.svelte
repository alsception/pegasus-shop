<script lang="ts">
  /**
   * We will pass parameters to this button:
   * 1. product: The product to be added to the cart.
   * 2. addToCart: A function to call when the button is clicked.
   * 3. loading: A boolean to indicate if the loading is in progress to disable button.
   */

  import { addToCart } from "./ProductService";
  import { addToCartLoading } from "./ProductService";

  export let product;
  export let width;
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
{:else if $addToCartLoading === product.id}
  <button
    class="btn blue"
    style="width: {width}"
    disabled
    aria-label="Adding to cart"
  >
    <span class=""></span>
    <span class="loading loading-dots loading-xs"></span>
  </button>
{:else}
<!-- background-color:#FFEB3B; -->
  <button
    class="btn btn-secondary"
    style="width: {width}; "
    aria-label="Dodaj"
    on:click={() => addToCart(product.id)}
  >
       <i class="fa fa-plus" aria-hidden="true"></i>
  </button>
{/if}