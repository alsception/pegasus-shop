<script>
    export let product;
    export let addToCart;
    export let addToCartLoading = false;
  </script>
  
  {#if addToCartLoading}
    <div class="flex justify-center items-center px-4 py-2 my-3">
      <div class="spinner w-6 h-6 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin"></div>
    </div>
  {:else}
    <button 
      class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 my-3 rounded-lg shadow transition duration-200"
      on:click={() => addToCart(product.id)}
    >
      <i class="fas fa-cart-plus"></i>
      Add to Cart
    </button>
  {/if}
  
  <style>
    /* Optional: Custom spinner styling if needed */
    .spinner {
      border-top-color: skyblue;
      animation: spin 1s linear infinite;
    }
  
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  </style>