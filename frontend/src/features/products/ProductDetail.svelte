<script lang="ts">
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/store"; 
  import type { Product } from "./Product";
  import LoadingOverlay from "../../core/LoadingOverlay.svelte";
  import ErrorDiv from "../users/ErrorDiv.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";

  document.title = 'Products | Pegasus'
  
  let productId: number;
  let product: Product | null = null;
  let loading = true;
  let error: string | null = null;

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  $: {
    if ($params?.id) {
      productId = Number($params.id);
      fetchProduct(productId); // reactively fetch when id changes
    }
  }

  // JWT token
  const token = $auth.token;

  async function fetchProduct(id: number) {
    loading = true;
    try {
      const res = await fetch(API_BASE_URL+`/products/${id}`, {
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

  // Optional: You can still call this on mount if you want to double-sure it triggers
  onMount(() => {
    if (productId) {
      fetchProduct(productId);
    }
  });
</script>

  {#if loading}

  <LoadingOverlay/>

  {:else if error}
  
  <ErrorDiv {error} />

  {:else if !product}
  
    <div class="flex justify-center items-center h-64">
      <h3>Product not found.</h3>
    </div>

  {:else}

    <div class="product-card">
      <div
              class="w-full h-48 bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden rounded-md mb-4"
            >
              {#if product.imageUrl}
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  class="object-cover w-full h-full"
                />
              {:else}
                <span class="text-gray-400 dark:text-gray-500"
                  >No image available</span
                >
              {/if}
            </div>
      <h1>{product.name}</h1>
      <p class="product-detail"><strong>Code:</strong> {product.code}</p>
      <p class="product-detail">
        <strong>Description:</strong>
        {product.description}
      </p>
      <p class="product-detail price">EUR: €{product.priceEur}</p>
      <p class="product-detail price">USD: ${product.priceUsd}</p>
      <p class="product-detail"><strong>Category:</strong> {product.category}</p>
      <p class="product-detail"><strong>Tax:</strong> {product.tax}</p>
      <p class="product-detail"><strong>Tax Group:</strong> {product.taxGroup}</p>
      <div class="w-full flex mr-0">
      <AddToCartButton
        product={ product } 
      />
      </div>
                      
  </div>

  {/if}

<style>
  .product-card {
    max-width: 500px;
    margin: 2rem auto;
    padding: 2rem;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    font-family: 'Courier New', Courier, monospace;
  }

  .product-card h1 {
    margin-bottom: 1rem;
    font-size: 1.8rem;
    color: #333;
  }

  .product-detail {
    margin: 0.5rem 0;
    font-size: 1rem;
  }

  .product-detail strong {
    color: #666;
  }

  .price {
    margin-top: 1rem;
    font-weight: bold;
  }
</style>
