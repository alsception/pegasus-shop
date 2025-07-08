<script>
/**
 * We will pass parameters to this button:
 * 1. product: The product to be added to the cart.
 * 2. addToCart: A function to call when the button is clicked.
 * 3. loading: A boolean to indicate if the loding is in progress to disable button.
 * 
*/
  export let product;
//  export let addToCart(product.id);
/*   export let loading = false;
 */  import { addToCart } from "./ProductService";
  import { addToCartLoading } from "./ProductService";


</script>

{#if $addToCartLoading === product.id}
  <button
    class="btn bg-amber-300 hover:bg-amber-400 p-2 rounded-full button-fx w-full flex items-center justify-center"
    style="width: 128px;"
    disabled
    aria-label="Adding to cart"
  >
    <span class="">Adding</span>
    <span class="loading loading-dots loading-xs"></span>
  </button>
{:else}
  <button
    class="cursor-pointer text-black bg-amber-300 hover:bg-amber-400 p-2 rounded-full button-fx btn w-full flex items-center justify-center"
    style="width: 128px;"
    on:click={() => addToCart(product.id)}
  >
    <span data-text="Add to cart">Add to cart</span>
    <div class="scan-line"></div>
  </button>
{/if}
