<script lang="ts">
  import { formatPrice, getFormattedPrice } from "../../utils/formatting";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { link } from "svelte-spa-router";
  import ProductPage from "./ProductPage.svelte";
  import { fly } from "svelte/transition";

  export let product;

  let productId = product.id; //Product koji cemo prikazati na modalu kada se klikne

  /* product modal */
  let showModal = false;

  function openModal() {
    showModal = true;
  }

  function closeModal() {
    showModal = false;
  }

  function handleProductClick(id: number | undefined) 
  {    
    openModal();
  }
</script>

<div 
  class="bg-white dark:bg-[linear-gradient(135deg,_#1d1d1d_0%,_#0d0d0d_100%)] dark:hover:border-primary/40
  overflow-hidden w-full shadow hover:shadow-lg transition-shadow border-1 border-primary/20 z-2
  flex flex-col h-ful rounded-t-3xl rounded-b-3xl" class:pgs-discount={product.discount>0} >
  <div class="w-full h-48 flex items-center justify-center overflow-hidden">
    {#if product.imageUrl}
      <img
        class="w-full h-full object-cover zoom"
        src={product.imageUrl}
        alt={product.name}
      />
    {:else}
    <div class="product-card-image-container" style="display: flex; align-items: center; justify-content: center; height: 150px;">
      <!-- svelte-ignore a11y_missing_attribute -->
      <img src="https://cdn.master-fb.com/gallery/Proizvodi/Hrana/Logo/e1624014329cdc45781f54f77db58e891753943002.webp" 
         style="width: 40%;opacity: 0.21;pointer-events: none;">
    </div>
      <!-- <span class="text-gray-400 dark:text-gray-500">No image available</span> -->
    {/if}
  </div>
  <div class="p-6 flex flex-col flex-1">
    <h3 class="font-semibold text-lg break-words text-primary flex items-center justify-between " title={product.name}>
      <!-- svelte-ignore a11y_click_events_have_key_events -->
      <!-- svelte-ignore a11y_no_static_element_interactions -->
      <span on:click={() => handleProductClick(product.id)} class="pgs-hyperlink">{product.name}</span>
      {#if product.discount }
        <span class="badge badge-accent text-2xl">AKCIJA</span>
      {/if}
    </h3>
    
    <p
      class="text-sm text-gray-500 dark:text-gray-500 mt-1 line-clamp-3"
      title={product.description}
      style="overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; white-space: normal;"
    >
      {product.description}
    </p>

    <div class="flex items-center justify-between mt-auto">

      {#if product.discount }

        <span class="text-xl font-bold text-primary line-through">
          { formatPrice( product.basePrice )}
        </span>   
        
        <span class="text-xl font-bold text-error ">
          {@html getFormattedPrice(product.discount)}
        </span>   

      {:else}
        
        <span class="text-xl font-bold text-amber-600 dark:text-amber-500">         
          {@html getFormattedPrice(product.basePrice)}
        </span>   

      {/if}

      <AddToCartButton {product} width="50px" />
    </div>    
  </div>
</div>

{#if showModal}
  <div class="modal modal-open pt-0" style="backdrop-filter: blur(10px);">
    <div
      class="modal-box max-h-[90vh] w-full sm:w-8/12 max-w-5xl p-0 flex flex-col bg-base-200"
      transition:fly={{ y: 50, duration: 300 }}
    >
      <!-- Fixed Header -->
      <div
        class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300"
      >
        <h3 class="font-bold text-lg">Detalji proizvoda</h3>
      </div>

      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1">
        <ProductPage productId={product.id} liteView={true}></ProductPage>
      </div>

      <!-- Fixed Footer -->
      <div
        class="sticky bottom-0 bg-base-100 z-10 px-6 py-4 border-t border-base-300"
      >
        <div class="flex justify-end gap-2">
          <button class="btn btn-secondary" on:click={closeModal}
            >Zatvori</button
          >
        </div>
      </div>
    </div>

    <!-- Glass Backdrop -->
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="modal-backdrop" on:click={closeModal}></div>
  </div>
{/if}

<style>
 .zoom {
  transition: transform 0.3s ease; /* definiše brzinu i glatkoću */
}

.zoom:hover {
  transform: scale(1.1); /* 1.1 znači povećanje od 10% */
}

.pgs-discount{
  outline: 2px solid var(--color-accent);
}
</style>