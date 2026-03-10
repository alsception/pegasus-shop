<script lang="ts">
  import type { PGSTable } from "./PGSTable";
  import { brojStola } from './../../core/services/CheckoutStore';
  import { link, push } from 'svelte-spa-router';
  import ProductsListBarbacoa from "../products/ProductsListBarbacoa.svelte";

  /**
   * //TODO: ovde treba videti dali se sto vec koristi prvo pre otvaranja
   */

  export let isAvailable = true;
  export let table: PGSTable;

  let width = "60px;"
  
  function handleClick() 
  {
    // Postavljaš vrijednost u store (ovo automatski sprema u localStorage)
    brojStola.set(table.number);

    openModal2();
  }

  let showModal2 = false;
  
  function openModal2() {
    showModal2 = true;
  }
  
  function closeModal2() {
    showModal2 = false;
  }

  //todo: neradi
  function handleKeydown(event: { key: string; }) 
  {

    if (event.key === 'Escape') {
      closeModal2();
    }
  }

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
<!-- svelte-ignore a11y_consider_explicit_label -->
<button
  class="btn btn-circle btn-ghost bg-base-100 flex items-center justify-center"
  on:click={handleClick}
>
  <span data-text="Dodaj" class="font-mono flex items-center justify-center ml-1.5">
    <i class="fa fa-plus" aria-hidden="true"></i>
  </span>
  <div class="scan-line"></div>
</button>
    {#if showModal2}
    <div class="modal modal-open pt-10 backdrop-blur-sm">
    <div class="modal-box w-full max-w-none h-[96vh] p-0 flex flex-col rounded-none">
      
      <!-- Fixed Header -->
     <!--  <div class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300">
        <h3 class="font-bold text-lg">Odaberi proizvod</h3>
      </div> -->
      
      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1 /*px-1 py-1*/">
        <ProductsListBarbacoa hideButtonDalje={true}></ProductsListBarbacoa>
      </div>
      
      <!-- Fixed Footer -->
    <div class="sticky bottom-0 z-10 px-6 py-4 border-t border-base-300 bg-base-300">
      <div class="flex justify-between items-center">
        <button class="btn btn-secondary" on:click={closeModal2}>
          Zatvori
        </button>

        <a use:link href="/cart" class="btn btn-success">
          Dalje
        </a>
      </div>
    </div>
      
    </div>
    
    <!-- Glass Backdrop -->
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="modal-backdrop"
      on:click={closeModal2}
    ></div>
  </div>
  {/if}
{/if}