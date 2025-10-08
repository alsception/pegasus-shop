<script lang="ts">
  /**
   * //TODO: ovde treba videti dali se sto vec koristi prvo pre otvaranja
   */

  //import { addToCart } from "./ProductService";
  //import { addToCartLoading } from "./ProductService";

  //export let product;
  //export let width;

  let isAvailable = true;
  let width = "136px;"
</script>



{#if !isAvailable}
  <button
    class="btn default bg-gray-600 button-fx w-full flex items-center justify-center"
    style="width: {width}"
    disabled
    aria-label="Table not available"
  >
    <span class="">Not available</span>
  </button>


<!-- {:else if $addToCartLoading === product.id}
  <button
    class="btn blue"
    style="width: {width}"
    disabled
    aria-label="Adding to cart"
  >
    <span class="">Adding</span>
    <span class="loading loading-dots loading-xs"></span>
  </button> -->
{:else}
  <button
    class="btn bg-green-400"
    style="width: {width}"
    
  >
    <span data-text="Otvori" class="text-black/80">       
       <i class="fa fa-plus" aria-hidden="true"></i> Otvori</span>
    <div class="scan-line"></div>
  </button>
{/if}