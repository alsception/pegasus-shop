<script lang="ts">
  import { onMount } from "svelte";
  import { link, params } from "svelte-spa-router";
  import { auth, isAdmin } from "../../core/services/SessionStore";
  import type { Product } from "./Product";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";

  document.title = "Product details | Pegasus";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  let productId: number;
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
  <div class="product-card dark:product-card-dark">
    <div
      class="w-full /*h-48*/ bg-base-100 flex items-center justify-center overflow-hidden rounded-md mb-4"
    >
      {#if product.imageUrl}
        <img
          src={product.imageUrl}
          alt={product.name}
          class="object-cover w-full h-full"
        />
      {:else}
        <span class="text-gray-400 dark:text-gray-500">No image available</span>
      {/if}
    </div>
    <h1>
      {product.name}
      {#if isAdminView}
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
    <p class="product-detail">Code:</p>
    <p>{product.code}</p>
    <p class="product-detail">Name:</p>
    <p>{product.name}</p>
    <p class="product-detail">Description:</p>
    <p>{product.description}</p>
    <p class="product-detail">Price:</p>
    <p>€{product.priceEur}</p>
    <p class="product-detail">Category:</p>
    <p>{product.category}</p>
    <p class="product-detail">Stock Quantity:</p>
    <p>{product.stockQuantity}</p>
    <p class="product-detail">Comment:</p>
    <p>{product.comment}</p>
    <p class="product-detail">Available:</p>
    <p>{product.active === true ? "YES ✅" : "NO ⛔"}</p>

    <div class="w-full flex mr-0 mt-6">
      <button type="button" on:click={close} class=" btn btn-ghost mr-5">
      <i class="fa fa-close"></i>
      Close
      </button>
      <AddToCartButton {product} width="135px" />
    </div>
  </div>
{/if}

<style>
  p {
    min-height: 24px;
    color: var(--color-base-content);
  }
  .product-card {
    max-width: 768px; /*not too wide for now*/
    margin: 2rem auto;
    padding: 2rem;
    border-radius: 12px;
    /*     box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
 */
    background-color: var(--color-base-200);
  }

  .product-card h1 {
    margin-bottom: 1rem;
    font-size: 1.8rem;
    color: #333;
  }

  .product-detail {
    margin: 0.5rem 0;
    font-size: 1rem;
    font-weight: bold;
    color: var(--color-base-content);
    margin-top: 16px;
    margin-bottom: 0px;
  }
</style>
