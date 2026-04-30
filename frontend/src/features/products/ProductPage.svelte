<script lang="ts">
  import { onMount } from "svelte";
  import { link, params } from "svelte-spa-router";
  import { auth, isAdmin } from "../../core/services/SessionStore";
  import type { Product } from "./Product";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { formatPrice, getFormattedPrice } from "../../utils/formatting";
  import AddToCartButtonBig from "./AddToCartButtonBig.svelte";

  document.title = "Barbacoa";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  export let productId: number;
  export let liteView = false;
  export let product: Product | null = null;
  let loading = true;
  let error: string | null = null;
  let isAdminView = false;

  $: {
    if ($params?.id) {
      productId = Number($params.id);
      fetchProduct(productId); // reactively fetch when id changes
    }
  }

  const token = $auth.token;

  async function fetchProduct(id: number) {
    loading = true;
    try {
      const res = await fetch(API_BASE_URL + `/products/${id}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error("Failed to fetch product");

      product = await res.json();

      document.title = product?.name + " | Barbacoa";
    } catch (err) {
      error = (err as Error).message;
    } finally {
      loading = false;
    }
  }

  function close(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }
  ) {
    window.location.href = "#/products";
  }

  // Optional: You can still call this on mount if you want to double-sure it triggers
  onMount(() => {
    isAdminView = isAdmin();

    if (productId) {
      fetchProduct(productId);
    }
  });
</script>

{#if loading}
  <LoadingOverlay />
{:else if error}
  <ErrorDiv {error} />
{:else if !product}
  <div class="flex justify-center items-center h-64">
    <h3>Product not found.</h3>
  </div>
{:else}
  <div
    class="product-card dark:product-card-dark p-0 sm:px-12"
    class:border-2={!liteView}
    class:border-zinc-600={!liteView}
  >
    <div
      class="w-full sm:h-[80vh] flex items-center justify-center overflow-hidden rounded-md mb-4 border-1 border-primary/20"
    >
      {#if product.imageUrl}
      <!-- TODO: DALI DA ZOOMIRAMO ILI OBJECT-COVER? -->
        <img src={product.imageUrl} alt={product.name} class=" object-cover" />
      {:else}
        <span class="text-gray-400 dark:text-gray-500">No image available</span>
      {/if}
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 items-stretch p-4 sm:p-0">
      <div class="w-full mt-2">
        <h3 class="inline-block text-amber-700 dark:text-amber-500 text-2xl sm:text-3xl mr-2 sm:mr-0 mb-4 sm:mb-0">
          {product.name}
          <!-- ... link ... -->
        </h3>
        {#if product.discount}
          <h3 class="inline-block text-md sm:ml-4 line-through">
            {@html getFormattedPrice(product.basePrice)}
          </h3>
          <h3 class="inline-block text-xl font-bold text-success ml-4">
            {@html getFormattedPrice(product.discount)}
          </h3>
        {:else}
          <h3 class="inline-block text-xl font-bold ml-0 sm:ml-4 ">
            {@html getFormattedPrice(product.basePrice)}
          </h3>
        {/if}

        <div class="flex items-center justify-between mt-auto">
          <div class=" ">
            {#if product.description}
              <p class="mt-4 mb-4 text-secondary/40">{product.description}</p>
            {/if}
          </div>
        </div>
      </div>

      <div class="w-full mb-7 sm:mt-2 mt-16">
        <div class="max-w-[28rem] ml-auto flex justify-center sm:justify-end">
          <AddToCartButtonBig {product} width="250px" full={true} />
        </div>
      </div>
    </div>
    {#if !liteView && isAdminView}
      <div class="w-full flex mr-0 mt-6 mb-6">
        <button type="button" on:click={close} class=" btn btn-ghost mr-5">
          Zatvori
        </button>
      </div>
    {/if}
  </div>
{/if}

<style>
  p {
    min-height: 24px;
    color: var(--color-base-content);
  }
  .product-card {    
    background-color: var(--color-base-200);
  }
</style>
