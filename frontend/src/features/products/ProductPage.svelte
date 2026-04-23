<script lang="ts">
  import { onMount } from "svelte";
  import { link, params } from "svelte-spa-router";
  import { auth, isAdmin } from "../../core/services/SessionStore";
  import type { Product } from "./Product";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { formatPrice, getFormattedPrice } from "../../utils/formatting";

  document.title = "Barbacoa";
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
    class="product-card dark:product-card-dark"
    class:border-2={!liteView}
    class:border-zinc-600={!liteView}
  >
    <div
      class="w-full flex items-center justify-center overflow-hidden rounded-md mb-4"
    >
      {#if product.imageUrl}
        <img
          src={product.imageUrl}
          alt={product.name}
          class="w-full h-full object-cover"
        />
        <div class="carousel rounded-box w-64 hidden">
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1559703248-dcaaec9fab78.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1565098772267-60af42b81ef2.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1572635148818-ef6fd45eb394.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1494253109108-2e30c049369b.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1550258987-190a2d41a8ba.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1559181567-c3190ca9959b.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
          <div class="carousel-item w-full">
            <img
              src="https://img.daisyui.com/images/stock/photo-1601004890684-d8cbf643f5f2.webp"
              class="w-full"
              alt="Tailwind CSS Carousel component" />
          </div>
        </div>

        <figure class="hover-gallery max-w-60 hidden">
          <img src="https://img.daisyui.com/images/stock/daisyui-hat-1.webp" />
          <img src="https://img.daisyui.com/images/stock/daisyui-hat-2.webp" />
          <img src="https://img.daisyui.com/images/stock/daisyui-hat-3.webp" />
          <img src="https://img.daisyui.com/images/stock/daisyui-hat-4.webp" />
        </figure> 
      {:else}
        <span class="text-gray-400 dark:text-gray-500">No image available</span>
      {/if}
    </div>
<div class="grid grid-cols-1 md:grid-cols-4 gap-6 items-stretch">
  
  <div class=" mt-2">
    <h2 class="text-primary text-3xl">
      {product.name}
      {#if !liteView}
        <a class="text-gray-400 hover:text-blue-300 text-md" use:link href="#/inventory/{product.id}">
          <i class="fas fa-pen"></i>
        </a>
      {/if}
    </h2>

  <div class=" ">
    {#if product.description}
      <p class="mt-4 mb-4 text-secondary/40">{product.description}</p>
    {/if}
  </div>
  </div>

  <div class=" p-4">
    <h3 class=" text-xl font-bold">
      {@html getFormattedPrice(product.basePrice)}
    </h3>
  </div>



  <div class=" p-4">
    <p class="mb-5">⭐⭐⭐⭐⭐</p>
  </div>

<div><AddToCartButton {product} width="50px" full={true}/></div>
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

</style>
