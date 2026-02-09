<script lang="ts">
  import type { PGSTable } from "./PGSTable";
  import { brojStola } from './../../core/services/CheckoutStore';
  import { push } from 'svelte-spa-router';

  /**
   * //TODO: ovde treba videti dali se sto vec koristi prvo pre otvaranja
   */

  export let isAvailable = true;
  export let table: PGSTable;

  let width = "116px;"
  
  function handleClick() 
  {
    // Postavljaš vrijednost u store (ovo automatski sprema u localStorage)
    brojStola.set(table.number);

    // Navigacija na drugu stranicu
    push('/products');
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
  <button
    class="btn btn-primary"
    style="width: {width};"
    on:click={handleClick}    
  >
    <span data-text="Otvori" class="font-mono">       
       <i class="fa fa-plus" aria-hidden="true"></i> Otvori</span>
    <div class="scan-line"></div>
  </button>
{/if}