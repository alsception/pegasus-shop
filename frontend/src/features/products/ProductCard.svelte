<script lang="ts">
  import { formatPrice, getFormattedPrice } from "../../utils/formatting";
  import AddToCartButton from "./AddToCartButton.svelte";
  import ProductPage from "./ProductPage.svelte";
  import type { Product } from "./Product";

  export let product: Product;

  export let zoomPix = false;

  let productId = product.id; //Product koji cemo prikazati na modalu kada se klikne

  /* product modal */
  let showModal = false;

  function openModal() {
    showModal = true;
  }

  function closeModal() {
    showModal = false;
    showHeader();
  }

  function handleProductClick(id: number | undefined) 
  {    
    openModal();
    hideHeader();
  }

  function hideHeader(){
    // Pronađi element (npr. navbar po ID-u ili klasi)
    let element = document.querySelector('.navbar');

    // Provjeri postoji li element prije dodavanja klase
    if (element) {
      element.classList.add('hidden'); // Dodaje klasu
    }

    element = document.querySelector('.btn-nxt-cntr');

    // Provjeri postoji li element prije dodavanja klase
    if (element) {
      element.classList.add('hidden'); // Dodaje klasu
    }

    element = document.querySelector('.prod-form-cntr');

    // Provjeri postoji li element prije dodavanja klase
    if (element) {
      element.classList.add('hidden'); // Dodaje klasu
    }

    // Dohvaća točno ove elemente
    /* const elementi = document.querySelectorAll('.navbar, .btn-nxt-cntr, #btn-nxt-cntr');

    elementi.forEach((el) => {
      (el as HTMLElement).classList.toggle('hidden');
    }); */
    
  }

  function showHeader(){
    // Pronađi element (npr. navbar po ID-u ili klasi)
    let element = document.querySelector('.navbar');

    // Provjeri postoji li element prije dodavanja klase
    if (element) {
      element.classList.remove('hidden'); // Dodaje klasu
    }

    element = document.querySelector('.btn-nxt-cntr');

    // Provjeri postoji li element prije dodavanja klase
    if (element) {
      element.classList.remove('hidden'); // Dodaje klasu
    }

    element = document.querySelector('.prod-form-cntr');

    // Provjeri postoji li element prije dodavanja klase
    if (element) {
      element.classList.remove('hidden'); // Dodaje klasu
    }

    // Dohvaća točno ove elemente
    /* const elementi = document.querySelectorAll('.navbar, .btn-nxt-cntr, #btn-nxt-cntr');

    elementi.forEach((el) => {
      (el as HTMLElement).classList.toggle('hidden');
    }); */
    
  }

  if(product.name == 'Kulen') product.isNew = true;//TODO: ovo skloniti za deploy/live i dodati polje u entitet

  //ovo nam je trebalo za import
  function isFresh(item: Product)
  {
    if (item.modified) {
      // Proveri da li je tip "string"
      if (typeof item.modified === 'string') {
          const danas = "2026-04-23";
          
          if (item.modified.startsWith(danas)) {
              return true;
          }
      } else if (item.modified instanceof Date) {
          // Ako je Date objekat, pretvori ga u string prvo
          if (item.modified.toISOString().startsWith("2026-04-23")) {
              return true
          }
      }
    }
    return false;
  }


  function handleKeydown(event: { key: string }) {
    if (event.key === "Escape") {
      closeModal();
    }
  }
</script>

<svelte:window on:keydown={handleKeydown} />

<div 
  class="bg-white overflow-hidden w-full /*z-2*/ flex flex-col h-full rounded-xl md:rounded-2xl" 
  class:pgs-discount={product.discount>0} 
  class:pgs-new={product.isNew} 
  >
  <div class="w-full h-48 flex items-center justify-center overflow-hidden">
    {#if product.imageUrl}
      <img
        class="w-full h-full object-none cursor-pointer zoom"
        class:object-none={zoomPix}
        class:object-cover={!zoomPix}
        src={product.imageUrl}
        alt={product.name}
        on:click={() => handleProductClick(product.id)}
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
    <h3 class="font-semibold text-lg break-words text-warning flex items-center justify-between ">
    
      <!-- svelte-ignore a11y_click_events_have_key_events -->
      <!-- svelte-ignore a11y_no_static_element_interactions -->
      <span on:click={() => handleProductClick(product.id)} class="pgs-hyperlink text-amber-700 dark:text-amber-500">{product.name}</span>
      <span class="hidden">{product.id}</span>
      
      {#if product.discount }
        <span class="badge badge-accent text-3xl p-4 rotate-12">AKCIJA</span>
      {/if}
      {#if product.isNew }
        <span class="badge badge-info text-2xl p-4 /*rotate-348*/">NOVO</span>
      {/if}
    </h3>
    
    {#if false}
      <!-- jednog lepog dana mozda stavimo ocene -->
      <p>⭐⭐⭐⭐⭐</p>
    {/if}
    

    <p
      class="text-sm text-gray-500 dark:text-gray-500 mt-1 line-clamp-3"
      title={product.description}
      style="overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; white-space: normal;"
    >
      {product.description}
    </p>

    <div class="flex items-center justify-between mt-auto">

      {#if product.discount }

        <div class="flex">
          <span class="text-xl font-bold text-primary line-through">
            { formatPrice( product.basePrice )}
          </span>   
          
          <span class="text-xl font-bold text-success ml-4">
            {@html getFormattedPrice(product.discount)}
          </span>   
        </div>
        

      {:else}
      <!-- text-amber-600 dark:text-amber-500 -->        
       <span class="text-xl font-bold text-primary/80 dark:text-primary ">         
          {@html getFormattedPrice(product.basePrice)}
        </span>   

      {/if}

      <AddToCartButton {product} width="50px" full={false}/>
    </div>    
  </div>
</div>

{#if showModal}
  <div class="modal modal-open pt-0 sm:mt-0 " style="backdrop-filter: blur(10px);">
    <div
      class="modal-box max-h-[100vh] sm:h-full max-w-[1900px] sm:w-full w-auto p-0 flex flex-col bg-base-200"
      
    >
      <!-- Fixed Header -->
      <div
        class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300"
      >
        <div class="flex justify-end">
          <h3 class="font-bold text-lg cursor-pointer" on:click={closeModal}>X</h3>
        </div>
      </div>

      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1">
      <!-- Zasto ovde opet ucitavamo s interneta kad imamo proizvod?
       TODO: Ovde staviti ceo proizvod da bude brze,ali da se nista nepokvari -->
        <ProductPage productId={product.id} liteView={true}></ProductPage>
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
  transition: transform 0.5s ease; /* definiše brzinu i glatkoću */
}

.zoom:hover {
  transform: scale(1.15); /* 1.1 znači povećanje od 10% */
}

.pgs-discount{
  outline: 4px solid var(--color-accent);
  background-color: oklch(50.131% 0.28782 284.203 / 0.07);

}

.pgs-new{
  outline: 4px solid var(--color-info);
  background-color: oklch(0.54 0.25 262.88 / 0.11);
}
</style>