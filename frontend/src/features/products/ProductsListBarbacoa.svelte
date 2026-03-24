<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { get } from "svelte/store";
  import {
    auth,
    getCurrentRole,
    isAdmin,
  } from "../../core/services/SessionStore";
  import { formatPrice } from "../../utils/formatting";
  import type { Product } from "./Product";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import ProductCard from "./ProductCard.svelte";
  import ProductCategories from "./ProductCategories.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { fly } from "svelte/transition";
  import ProductPage from "./ProductPage.svelte";

  document.title = "Jelovnik | Barbacoa";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let modalProduct: Product | null = null;
  let products: Product[] = [];
  let filteredProducts: Product[] = [];
  let loading: boolean = false;
  let error: string | null = null;
  let isListView = false;
  let isLiteListView = false;
  let isAdminView = true;
  let isDark = true;
  let page = 0;
  let size = 20; 
  let totalProducts = 0; 
  let totalPages = 0; 
  let selectedCategory: number;
  let productId = 0; //Product koji cemo prikazati na modalu kada se klikne
  export let hideButtonDalje = false;

  /* const kategorije = [
    { id: 1, ime: "🧀", title: "Predjela" },
    { id: 2, ime: "🥩", title: "Glavna jela" },
    { id: 3, ime: "🥗", title: "Prilozi" },
    { id: 4, ime: "🍰", title: "Desert" },
    { id: 5, ime: "🍺", title: "Piće" },
    { id: 6, ime: "❤️", title: "Omiljeno" },
  ]; */

  // AUTHENTICATION
  $: auth.subscribe((value) => 
  {
    isAuthenticated = value.isAuthenticated;
  });

  function checkDarkmode() 
  {
    const check = () => (isDark = document.body.classList.contains("dark"));
    check();

    const observer = new MutationObserver(check);
    observer.observe(document.body, {
      attributes: true,
      attributeFilter: ["class"],
    });

    return () => observer.disconnect();
  }

  function checkListViewParam() 
  {
    let search = window.location.search;
    if (!search && window.location.hash.includes("?")) {
      search = window.location.hash.substring(
        window.location.hash.indexOf("?")
      );
    }
    const params = new URLSearchParams(search);
    let listViewParam = params.get("listview") || params.get("listView");
    if (listViewParam !== null) {
      isListView = listViewParam === "true";
    }
  }

  onMount(async () => 
  {
    checkDarkmode();
    checkListViewParam();

    try 
    {
      const { isAuthenticated } = get(auth);
      isAdminView = isAdmin();

      if (!isAuthenticated) 
      {
        error = "Session expired. Please login again.";
        return;
      } 
      else 
      {
        handleSearch();
      }
    } 
    catch (err) 
    {
      error = err instanceof Error ? err.message : "Search failed";
    }
  });

  let searchTerm = "";
  let searchTimeout: ReturnType<typeof setTimeout>;

  // Funkcija koja reaguje na kucanje
  function handleInput() 
  {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => 
    {
      page = 0; 
      handleSearch();
    }, 300); // Čeka 300ms pauze u kucanju pre nego što pozove API
  }

  function handleFormSubmit(event: { preventDefault: () => void }) 
  {
    event.preventDefault(); 
    clearTimeout(searchTimeout); // Prekini planirani debounced poziv ako se stisne Enter
    page = 0; 
    handleSearch();
  }

  async function handleSearch() 
  {
    const token = $auth.token;
    loading = true;
    try 
    {
      const res = await fetch(
        API_BASE_URL +
          `/products?search=${searchTerm}&page=${page}&size=${size}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!res.ok) 
      {
        if (res.status === 401) 
        {
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          throw new Error("Authentication failed");
        }
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      const data = await res.json();
      products = data;
      filteredProducts = data;
      totalProducts = products.length;
    } 
    catch (err: any) 
    {
      console.error(error);
      if (err.message.includes("401")) 
      {
        $auth.token = null;
      } 
      else 
      {
        error = err instanceof Error ? err.message : "Unknown error";
      }
    } 
    finally 
    {
      loading = false;
    }
  }

  function toggleView() 
  {
    if(isListView)
    {
      if(!isLiteListView)
      {
        isLiteListView = true;
      }
      else
      {
        isListView = false;
        isLiteListView = false;
      }
    }
    else
    {
      isListView = true;
    }
  }

  function handleCategorySelect(category: number): void {
    filteredProducts = products.filter((p) => p.category == category);
  }

  function clickChange(id: any): any {
    if (selectedCategory != id) {
      selectedCategory = id;

      if (selectedCategory === 6) {
        filteredProducts = products.filter((p) => p.favourite === true);
      } else {
        filteredProducts = products.filter((p) => p.category == id);
      }
    } else {
      selectedCategory = 0;
      filteredProducts = products;
    }

    document
      .getElementById("products-container")
      ?.scrollIntoView({ behavior: "smooth", block: "nearest" });
    document
      .getElementById("products-list-container")
      ?.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

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
    if (id != undefined) productId = id;
    openModal();
  }

</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else if error}
  <ErrorDiv {error} />
{:else}
  <div
    class="w-[50%] left-[25%] flex justify-center p-0 fixed z-9003 bg-transparent top-[-0.1rem]"
    style="justify-self: center;"
  >
    <div
      class="w-full lg:max-w-4xl lg:p-4 lg:m-6 lg:mb-4 lg:mt-0.5 bg-base-200/80 lg:rounded-lg border-1 border-primary/20 backdrop-blur-lg lg:pb-[5px]
              max-lg:fixed max-lg:top-0 max-lg:left-0 max-lg:p-[10px] max-lg:pb-0 fixed top-[-16.5] lg:-top-16.5 left-0.5 [width:-webkit-fill-available] 
              lg:static lg:w-auto md:min-w-[680px] justify-self-center"
    >
      <form
        on:submit|preventDefault={handleFormSubmit}
        class="flex flex-col gap-1"
        id="brb-prod-l-f"
      >
        <div class="flex gap-1 mb-4">
          <input
            id="i-search"
            type="text"
            bind:value={searchTerm}
            on:input={handleInput}
            placeholder="Traži proizvod..."
            class="input border-2 flex-1 w-max ml-10"
          />
          <button type="submit" class="btn btn-dash">
            <i class="fas fa-search"></i>
            <span class="hidden sm:inline ml-1">Traži</span>
          </button>

          <div class=" p-0">
            <ProductCategories
              bind:selectedCategory
              onSelect={handleCategorySelect}
            />
          </div>

          <button
            type="button"
            on:click={toggleView}
            class="btn btn-dash whitespace-nowrap"
          >
            {#if !isListView}
              <i class="fas fa-image"></i>
            {:else}
              <i class="fas fa-list"></i>
            {/if}
          </button>
        </div>
      </form>
    </div>
  </div>

  <div id="results" class="w-full max-w-4xl mx-auto mt-22 pt-2"></div>

  {#if loading}
    <LoadingOverlay />
  {/if}

  {#if isListView}
    <div id="products-list-container" class="w-full max-w-[382px] mx-auto p-0">
      <div class="w-full overflow-x-auto">
        <table class="table w-full min-w-[382px]">
          <tbody>
            {#each filteredProducts as product, i}
              <tr class="bg-base-200 outline-1 outline-transparent hover:bg-base-300/70 border-b border-b-base-300">
                <td class="pgs-td whitespace-nowrap p-0">
                  <div class="">
                    <h3 class="font-semibold text-lg max-w-[382px;] truncate text-primary" title={product.name}>
                      <!-- svelte-ignore a11y_click_events_have_key_events -->
                      <!-- svelte-ignore a11y_no_static_element_interactions -->
                      <span on:click={() => handleProductClick(product.id)}
                       class="pgs-hyperlink">{product.name}</span>
                    </h3>
                    {#if isLiteListView}
                      <p class="text-sm text-primary/50 mt-1 line-clamp-3 max-w-[80%;]" title={product.description} style="overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; white-space: normal;">
                        {product.description}
                      </p>  
                    {/if}
                    <table class="w-full">
                      <tbody>
                        <tr>
                          <td class="pgs-td text-left text-4xl">
                            <h3 class="font-semibold text-lg truncate text-primary/50">
                              € {formatPrice(product.basePrice)}
                            </h3>
                          </td>
                          <td class="p-0">
                            <div class="flex justify-end items-end gap-2" style="font-size: 14px;">
                              <AddToCartButton {product} width="40px" />
                            </div>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
      {#if !hideButtonDalje}
      {@render btnNext()}
    {/if}
    </div>
  {:else if !products && !loading}
    no products found :/
  {:else}
    <div id="products-container" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 xl:grid-cols-4 4xl:grid-cols-5 gap-8 p-4 mb-20 mt-[-4rem] sm:mt-[-6rem]" 
      style="justify-items: center;">
      {#each filteredProducts as product, i}
        <ProductCard {product} />
      {/each}
    </div>
    {#if !hideButtonDalje}
      {@render btnNext()}
    {/if}
  {/if}
{/if}

{#snippet btnNext()}
  <div class="fixed top-[92%] left-[75%] md:left-[84%] lg:left-[92%] z-[9000]">
    <a class="btn btn-lg btn-primary border border-primary-content/40" use:link href="/cart">
      Naruči<i class="fas fa-arrow-right text-primary-content/60"></i>
    </a>
  </div>
{/snippet}

{#if showModal}
  <div class="modal pt-0" style="backdrop-filter: blur(10px);">
    <div
      class="modal-box max-h-[90vh] w-full sm:w-8/12 max-w-5xl p-0 flex flex-col bg-base-200"
      transition:fly={{ y: 50, duration: 300 }}
    >
      <!-- Fixed Header -->
      <div
        class="sticky top-0 bg-base-100 z-10 px-6 py-4 border-b border-base-300"
      >
        <h3 class="font-bold text-lg hidden">Detalji proizvoda</h3>
      </div>

      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1">
        <ProductPage {productId} liteView={true}></ProductPage>
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
  .active {
    background-color: color-mix(in srgb, var(--color-info) 40%, transparent);
  }
  .neon-btn {
  position: relative;
  padding: 15px 30px;
  background: #000; /* Boja unutrašnjosti dugmeta */
  color: #fff;
  /* font-family: sans-serif; */
  text-transform: uppercase;
  letter-spacing: 2px;
  border: none;
  cursor: pointer;
  overflow: hidden; /* Ključno: da sakrije ostatak animacije */
  /* border-radius: 8px; */
}

/* "Zmija" koja kruži */
.neon-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: conic-gradient(
    transparent, 
    #ae00ff, /* Boja neona */
    transparent 30%
  );
  animation: rotate 3s linear infinite;
}

/* Overlay koji pravi efekat tanke ivice */
.neon-btn::after {
  content: '';
  position: absolute;
  inset: 2px; /* Debljina ivice (što manji broj, tanja ivica) */
  background: #000;
  border-radius: 6px;
  z-index: 0;
}

/* Tekst mora biti iznad svega */
.neon-btn span {
  position: relative;
  z-index: 1;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Dodatni glow efekat na hover */
.neon-btn:hover {
  box-shadow: 0 0 20px #ff0055;
  transition: 0.3s;
}
</style>