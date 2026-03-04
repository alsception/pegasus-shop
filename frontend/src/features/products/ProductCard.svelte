<script lang="ts">
  import { formatPrice } from "../../utils/formatting";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { link } from "svelte-spa-router";
  export let product;
</script>

<div 
  class="bg-white dark:bg-slate-900 rounded-xl overflow-hidden flex flex-col 
  w-full
  shadow hover:shadow-lg transition-shadow
  border-1 border-primary/20 z-2">
  <div class="w-full h-48 bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden">
    {#if product.imageUrl}
      <img
        class="w-full h-full object-cover"
        src={product.imageUrl}
        alt={product.name}
      />
    {:else}
      <span class="text-gray-400 dark:text-gray-500">No image available</span>
    {/if}
  </div>
  <div class="p-6">
    <h3 class="font-semibold text-lg truncate text-primary" title={product.name}>
      <a use:link href="/products/{product.id}" class="pgs-hyperlink">{product.name}</a>
    </h3>
   <!--  <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
        <i class="fas fa-tag text-gray-400"></i>
        <span class="text-primary"> {product.category} </span>        
    </p>    -->
    <p
      class="text-sm text-gray-500 dark:text-gray-500 mt-1 line-clamp-3"
      title={product.description}
      style="overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; white-space: normal;"
    >
      {product.description}
    </p>
    <div class="flex items-center justify-between mt-4">

      {#if product.discount }

        <span class="text-xl font-bold text-primary line-through">
          { formatPrice( product.basePrice )}
        </span>   
        
        <span class="text-2xl font-bold text-success">
          { formatPrice( product.discount )}
        </span>   

      {:else}
        
        <span class="text-xl font-bold text-primary">
          { formatPrice( product.basePrice )}
        </span>   

      {/if}


      <AddToCartButton {product} width="50px" />
    </div>    
  </div>
</div>