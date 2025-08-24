<script lang="ts">
  import AddToCartButton from "./AddToCartButton.svelte";
  import { link } from "svelte-spa-router";
  export let product;
</script>

<div class="bg-white dark:bg-slate-900 rounded-xl overflow-hidden flex flex-col max-w-[384px] shadow hover:shadow-lg transition-shadow">
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
    <p class="text-sm text-gray-500 dark:text-gray-500 mt-1">
      <i class="fas fa-key text-gray-400"></i>
      {product.code}
    </p>
    <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
        <i class="fas fa-tag text-gray-400"></i>
        <span class="text-primary"> {product.category} </span>
        <span class="text-sm text-gray-600 dark:text-gray-500 pl-4 text-right">
        {product.stockQuantity} left
        </span>
    </p>   
    <p
      class="text-sm text-gray-500 dark:text-gray-500 mt-1 line-clamp-3"
      title={product.description}
      style="overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; white-space: normal;"
    >
      {product.description}
    </p>
    <div class="flex items-center justify-between mt-4">
      <span class="text-2xl font-bold text-primary">
        € {product.priceEur}
      </span>      
      <AddToCartButton {product} width="135px" />
    </div>    
  </div>
</div>