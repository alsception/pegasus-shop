<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { get } from "svelte/store";
  import { auth, isAdmin } from "../../core/services/SessionStore";
  import { formatDate } from "../../utils/formatting";
  import { formatActive } from "../../utils/formatting";
  import { formatCommentInfo } from "../../utils/formatting";
  import type { Product } from "./Product";
  import Modal from "./ProductModal.svelte";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import ProductCard from "./ProductCard.svelte";
  import ProductCategories from "./ProductCategories.svelte";

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

  async function handleSearch() {
    const token = $auth.token;
    loading = true;
    try {
      const res = await fetch(
        API_BASE_URL +
          `/products/p?search=${searchTerm}&page=${page}&size=${size}`,
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

      // Use the paginated response structure
      products = data.products;
      totalProducts = data.totalCount;
      totalPages = data.totalPages;
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
    <div class="w-full flex justify-center px-4">
      <div class="w-full max-w-[1800px] p-4 bg-transparent rounded-lg">
        <form
          on:submit|preventDefault={handleFormSubmit}
          class="flex flex-col sm:flex-row items-center gap-3"
        >
          <input
            type="text"
            bind:value={searchTerm}
            placeholder="Traži proizvod..."
            class="input input-bordered input-lg w-full"
            style="font-size: 1.5rem; /*height: 3.5rem;*/ max-width: 100%;"
          />
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-search"></i>
            Traži
          </button>
          <ProductCategories bind:selectedCategory onSelect={handleCategorySelect} />
          
          <button on:click={toggleView} class="btn btn-secondary">
            <i class="fas fa-th-list"></i>
            Grid view / List view
          </button>
          
        </form>
        <!-- Pagination Controls & Info -->
        <div
          class="flex flex-col sm:flex-row justify-between items-center mt-4 gap-2"
        >
          <div class="flex gap-2 items-center">
            <button
              class="btn btn-outline"
              title="Previous page"
              on:click={prevPage}
              disabled={page === 0}>⬅️ Prev</button
            >
            <button
              class="btn btn-outline"
              title="Next page"
              on:click={nextPage}
              disabled={page + 1 >= totalPages}>Next ➡️</button
            >
          </div>
          <div class="text-sm text-gray-600 dark:text-gray-400">
            <span>
              Page <b>{page + 1}</b> of <b>{totalPages}</b>
              &nbsp;|&nbsp; Total products: <b>{totalProducts}</b>
            </span>
          </div>
        </div>
      </div>
    </div>

    <div id="results" class="w-full max-w-4xl mx-auto mt-6"></div>

    {#if loading}
      <LoadingOverlay />
    {/if}

    {#if isListView && isAdminView}
      <div
        class="w-full max-w-[1780px] overflow-x-auto rounded-lg align-middle mx-auto"
      >
        <table class="table table-zebra min-w-[1200px] divide-y divide-accent">
          <thead class="bg-[#10273c] border-2 border-primary/10">
            <tr class="h-12">
              <th class="pgs-th">Proizvod</th>
              <th class="pgs-th">Brand</th>
              <th class="pgs-th">Price</th>
              <th class="pgs-th">Actions</th>
            </tr>
          </thead>
          <tbody class="">
            {#each products as product, i}
              <tr class="bg-base-100 pgs-tr border-2 border-primary/10">
                <td class="pgs-td whitespace-nowrap">
                  <a
                    use:link
                    href="/products/{product.id}"
                    class="text-primary pgs-hyperlink">{product.name}</a
                  >
                </td>
                <td class="pgs-td">{product.category}</td>
                <td class="pgs-td">{product.brand}</td>
                <td class="pgs-td-num">{product.basePrice}</td>
                <td class="text-center"
                  >{@html formatCommentInfo(product.comment)}</td
                >
                <td class="pgs-td">{product.other}</td>
                <td class="pgs-td font-mono whitespace-nowrap"
                  >{@html formatDate(product.created, "new", 15)}</td
                >
              
                <td class="px-2">
                  <div
                    class="flex justify-center items-center gap-2"
                    style="font-size: 14px;"
                  >
                    <div class="tooltip tooltip-info" data-tip="Edit">
                      <a
                        class="px-4"
                        aria-label="Edit"
                        use:link
                        href="/products/mngmt/{product.id}"
                      >
                        <i
                          class="fas fa-pen text-gray-500 hover:text-sky-400 cursor-pointer"
                        ></i>
                      </a>
                    </div>
                    <button
                      class="px-4"
                      aria-label="Delete"
                      on:click={() => deleteDialog(product.id)}
                    >
                      <div class="tooltip tooltip-info" data-tip="Delete">
                        <i
                          class="fas fa-times-circle text-gray-500 hover:text-red-400 cursor-pointer"
                        ></i>
                      </div>
                    </button>
                  </div>
                </td>
              </tr>
            {/each}
            <tr class="bg-base-200 border-2 border-primary/10">
              <td colspan="18" class="pgs-td font-mono h-[64px]">
                Showing <b>{products.length}</b> product(s) on this page.<br />
                Total products found: <b>{totalProducts}</b> | Total pages:
                <b>{totalPages}</b>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    {:else if products.length === 0 && !loading}
      no products found :/
    {:else}
      <div
        class="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-5 gap-8 p-4"
      >
        {#each products as product, i}
          <ProductCard {product} />
        {/each}
      </div>
    {/if}
  {/if}
  {#if modalProduct}
    <Modal
      showModal={true}
      title="Reviews"
      data={modalProduct.reviews}
      on:close={closeModal}
    />
  {/if}
{/if}

<style>
  .pgs-th {
    color: white;
  }
</style>
