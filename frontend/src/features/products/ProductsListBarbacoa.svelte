<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { get } from "svelte/store";
  import { auth, getCurrentRole, isAdmin } from "../../core/services/SessionStore";
  import { formatPrice } from "../../utils/formatting";
  import type { Product } from "./Product";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import ProductCard from "./ProductCard.svelte";
  import ProductCategories from "./ProductCategories.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";
  import { slide } from "svelte/transition";

  document.title = "Products | Pegasus";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let modalProduct: Product | null = null;
  let products: Product[] = [];
  let loading: boolean = false;
  let error: string | null = null;
  let isListView = false;
  let isAdminView = true;
  let isDark = true;
  let page = 0;
  let size = 20; // Products per page
  let totalProducts = 0; // If backend returns total count
  let totalPages = 0; // Total pages from backend
  let selectedCategory: any;

  const kategorije = ['🧀 Predjela','🍔 Glavna jela','🍟 Prilozi','🍨 Desert','Piće 🍷']

  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  function checkDarkmode() {
    //Ovo proverava dali je darkmode classa dodeljena na body
    //Jer treba zbog nekih elemenata koji se drugacije prikazuju

    const check = () => (isDark = document.body.classList.contains("dark"));
    check();

    const observer = new MutationObserver(check);
    observer.observe(document.body, {
      attributes: true,
      attributeFilter: ["class"],
    });

    return () => observer.disconnect();
  }

  /**
   * Apparently, the query param ?listview=true is not accessible in the window.location.search
   * but in window.location.hash (spa logic), so we need to use this ai slop to make it work...
   */
  function checkListViewParam() {
    // Support hash-based routing: extract query params from hash if present
    let search = window.location.search;
    if (!search && window.location.hash.includes("?")) {
      search = window.location.hash.substring(
        window.location.hash.indexOf("?")
      );
    }
    const params = new URLSearchParams(search);
    // Use lowercase 'listview' for consistency
    let listViewParam = params.get("listview");
    if (listViewParam !== null) {
      isListView = listViewParam === "true";
    }
    listViewParam = params.get("listView");
    if (listViewParam !== null) {
      isListView = listViewParam === "true";
    }
  }

  // Fetch the products from the backend
  onMount(async () => {
    checkDarkmode();
    checkListViewParam();

    try {
      const { isAuthenticated } = get(auth);
      isAdminView = isAdmin();
      /**
       * TODO: this is not actually working
       */

      if (!isAuthenticated) {
        error = "Session expired. Please login again.";
        // goto('/login?return=' + encodeURIComponent(currentPath));
        return;
      } else {
        handleSearch();
      }
    } catch (err) {
      error = err instanceof Error ? err.message : "Search failed";
    }
  });

  let searchTerm = "";

  function handleFormSubmit(event: { preventDefault: () => void }) {
    event.preventDefault(); // prevent page reload
    page = 0; // Reset to first page on new search
    handleSearch();
  }
//barbacoa
          //
  async function handleSearch() {
    const token = $auth.token;
    loading = true;
    try {
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

      if (!res.ok) {
        if (res.status === 401) {
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          throw new Error("Authentication failed");
        }
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      const data = await res.json();

      console.log(data);

      // Use the paginated response structure
      products = data;
      totalProducts = products.length;
    } catch (err: any) {
      console.error(error);
      if (err.message.includes("401")) {
        $auth.token = null;
      } else {
        error = err instanceof Error ? err.message : "Unknown error";
      }
    } finally {
      loading = false;
    }
  }

  function nextPage() {
    if ((page + 1) * size < totalProducts) {
      page += 1;
      handleSearch();
    }
  }

  function prevPage() {
    if (page > 0) {
      page -= 1;
      handleSearch();
    }
  }

  function closeModal(): void {
    modalProduct = null;
  }

  function toggleView() {
    isListView = !isListView;
  }

  function handleCategorySelect(category: string): void 
  {
    console.log('selected category: '+category);
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}  
  {#if error}
    <ErrorDiv {error} />
  {:else}
      
    <div class="w-full flex justify-center p-0 fixed z-3 bg-transparent"
      style="
        position: fixed;
       /*  top: -16px;
        left: -23px; */
    ">
      <div class="w-full max-w-4xl p-4 bg-base-200/90 rounded-lg border-2 border-primary/20 backdrop-blur-lg m-6 mb-8">
        <form
          on:submit|preventDefault={handleFormSubmit}
          class="flex flex-col gap-3 "
        >
          <!-- Gornji red: Input i Traži dugme -->
          <div class="flex gap-2">
            <input
              type="text"
              bind:value={searchTerm}
              placeholder="Traži proizvod..."
              class="input /*input-primary dark:*/  input-info border-2 flex-1"
            />
            <button type="submit" class="btn btn-dash">
              <i class="fas fa-search"></i>
              <span class="hidden sm:inline ml-1">Traži</span>
            </button>
          </div>

          <!-- Donji red: Kategorije, Pagination i View toggle -->
          <div class="flex gap-2 items-center ">
            
            <div class="flex-1 hidden">
              <ProductCategories bind:selectedCategory onSelect={handleCategorySelect} />
            </div>

          {#if getCurrentRole() === 'ADMIN'}
            <button
              type="button"
              on:click={() => window.location.href = '#/products/mngmt/0'}
              class="btn btn-dash flex-1 lg:flex-none whitespace-nowrap hidden"
            >       
              <i class="fas fa-plus"></i>
              Dodaj novi
            </button>
            {/if}
            
            <div class="text-xs sm:text-sm text-gray-600 dark:text-gray-400 whitespace-nowrap">
               <span class="md:inline"> | Nadjeno: <b>{products.length} </b> |</span>

                {#each kategorije as k}
                  <span class="badge badge-neutral font-mono badge-lg cursor-pointer
                            hover:bg-zinc-400/40 hover:outline-1 hover:outline-blue-600
                            m-2" 
                        style="text-transform: uppercase;">
                      {k}
                  </span>
                {/each}

            </div>
            
            <button type="button" on:click={toggleView} class="btn btn-dash whitespace-nowrap">
              <i class="fas fa-th-list"></i>
           .
            </button>
          </div>
        </form>
      </div>
    </div>

    <div id="results" class="w-full max-w-4xl mx-auto mt-16 pt-20"></div>

    {#if loading}
      <LoadingOverlay />
    {/if}

    {#if isListView && isAdminView}
   <div class="w-full max-w-[382px] mx-auto p-0">
  <div class="w-full overflow-x-auto">
    <table class="table  w-full min-w-[382px] ">
          <tbody class="">
            {#each products as product, i}
              <tr class="bg-base-200 outline-1 outline-transparent /*hover:outline-blue-500*/ hover:bg-base-300/70 border-b border-b-base-300">            
                <td class="pgs-td whitespace-nowrap p-0">
                     <div class="">
                     <h3 class="font-semibold text-lg max-w-[382px;] truncate text-primary" title={product.name}>
      <a use:link href="/products/{product.id}" class="pgs-hyperlink">{product.name}</a>
    </h3>
    <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
    </p>   
    <p
      class="text-sm text-primary/50 mt-1 line-clamp-3 max-w-[80%;]"
      title={product.description}
      style="overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; white-space: normal;"
    >
      {product.description}
    </p>
    <table class="w-full">
      <tbody>
        <tr>
         <td class="pgs-td text-left text-4xl "><h3 class="font-semibold text-lg truncate text-primary/50" >€ {formatPrice(product.basePrice)}</h3></td>
               
                <td class="p-0">
                  <div
                    class="flex justify-end items-end gap-2"
                    style="font-size: 14px;"
                  >
                    <div class="tooltip tooltip-info" data-tip="Dodaj">
                     <AddToCartButton {product} width="40px" />
                    </div>
                   
                  </div>
                </td>
                </tr>
      </tbody>
      
    </table>
                  </div>
                </td>

               
              </tr>
            {/each}
            <tr class="bg-base-200">
              <td colspan="18" class="pgs-td font-mono h-[64px]">
                Showing <b>{products.length}</b> product(s) on this page.<br />
              </td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>
    {:else if !products && !loading}
      no products found :/
    {:else}
    <!-- 
    todo: ako je standalone onda neka bude 4 cols, ako je u modal dialogu onda mozda 3
      -->
      <div
        class="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 4xl:grid-cols-5 gap-8 p-4"
        style="justify-items: center;"        
      >
        {#each products as product, i}
          <ProductCard {product} />
        {/each}
      </div>
    {/if}
  {/if}

{/if}

