<script lang="ts">
  import { onMount } from "svelte";
  import { link, params } from "svelte-spa-router";
  import { auth, isAdmin } from "../../core/services/SessionStore";
  import type { Product } from "./Product";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { formatPrice } from "../../utils/formatting";

  document.title = "Product details | Pegasus";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  export let productId: number;
  export let liteView = false;
  let product: Product | null = null;
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
    } catch (err) {
      error = (err as Error).message;
    } finally {
      loading = false;
    }
  }

  function close(event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }) 
  {
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
  <div class="product-card dark:product-card-dark" class:border-2={!liteView} class:border-zinc-600={!liteView}>
    <div
      class="w-full flex items-center justify-center overflow-hidden rounded-md mb-4"
    >
      {#if product.imageUrl}
        <img
          src={product.imageUrl}
          alt={product.name}
          class="object-contain w-full h-max-[600px]"
        />
      {:else}
        <span class="text-gray-400 dark:text-gray-500">No image available</span>
      {/if}
    </div>
    <h1 class="text-primary text-2xl">
      {product.name}
      {#if !liteView}
        <a
          class="text-gray-400 hover:text-blue-300 text-md"
          use:link
          href="#/products/mngmt/{product.id}"
          title="Edit"
          aria-label="Edit"
        >
          <i class="fas fa-pen"></i>
        </a>
      {/if}
    </h1>
    <div class="flex gap-2"></div>
    
    {#if product.description}
      <p>{product.description}</p>
    {/if}

    <h3 class="product-detail"
      style="font-size: x-large;">{formatPrice( product.basePrice   )}</h3>
    <p></p>
   
    {#if !liteView && isAdminView}
      <div class="w-full flex mr-0 mt-6 mb-6">
        <button type="button" on:click={close} class=" btn btn-ghost mr-5">
        Zatvori
        </button>
        <div class="ml-auto">
          <AddToCartButton {product} width="75px" />
        </div>
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
    max-width: 650px; /*not too wide for now*/
    margin: 2rem auto;
    margin-top: 0;
    margin-bottom: 0;
    padding: 2rem;
    padding-top: 0;
    padding-bottom: 0;
    border-radius: 12px;
    background-color: var(--color-base-200);
  }

  .product-card h1 {
    margin-bottom: 1rem;
    font-size: 1.8rem;
  }

  .product-detail {
    margin: 0.5rem 0;
    font-size: 1rem;
    font-weight: bold;
    color: var(--color-base-content);
    margin-top: 16px;
    margin-bottom: 0px;
  }

  img{
    object-fit: contain;
    max-height: 350px !important; /**trenutno je ovoliko velicina slike*/
  }
</style>
