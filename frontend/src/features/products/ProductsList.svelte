<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { get } from "svelte/store";
  import { auth } from "../../core/services/SessionStore";
  import { formatDate } from "../../utils/formatting";
  import { formatActive } from "../../utils/formatting";
  import { formatCommentInfo } from "../../utils/formatting";
  import type { Product } from "./Product";
  import Modal from "./ProductModal.svelte";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import AddToCartButton from "./AddToCartButton.svelte";

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
        API_BASE_URL + `/products/p?search=${searchTerm}&page=${page}&size=${size}`,
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
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}
  <!-- <button class="nb-button default">Default</button>
  <button class="nb-button orange">Orange</button>
  <button class="nb-button blue">Blue</button>
  <button class="nb-button green">Green</button>

  <button class="nb-button default rounded">Default</button>
  <button class="nb-button orange rounded">Orange</button>

  <div class="nb-dialog">
    <div class="nb-dialog-header">Dialog Header</div>
    <div class="nb-dialog-body">...</div>
    <div class="nb-dialog-footer">
      <button class="nb-button default">Cancel</button>
      <button class="nb-button green">Save</button>
    </div>
  </div> -->
{#if error}
    <ErrorDiv {error} />
  {:else}
  <div class="w-full flex justify-center px-4">
    <div class="w-full max-w-4xl p-4 bg-transparent rounded-lg">
      <form
        on:submit|preventDefault={handleFormSubmit}
        class="flex flex-col sm:flex-row items-center gap-3"
      >
        <input
          type="text"
          bind:value={searchTerm}
          placeholder="Search products..."
          class="nb-input default"
        />
        <!-- Search Button -->
        <button type="submit" class="nb-button default">
          <i class="fas fa-search"></i>
          Search
        </button>

        <!-- Toggle View Button -->
        <button on:click={toggleView} 
        class="nb-button default"
        >
          <i class="fas fa-th-list"></i>
          Grid view / List view
        </button>

        <!-- Create product Button -->
        <button
          on:click={() => alert("not implemented yet")}
          class="nb-button default"
        >
          <i class="fas fa-plus"></i>
          Create new product
        </button>
      </form>
      <!-- Pagination Controls & Info -->
      <div class="flex flex-col sm:flex-row justify-between items-center mt-4 gap-2">
        <div class="flex gap-2 items-center">
          <button class="nb-button default" title="Previous page" on:click={prevPage} disabled={page === 0}>⬅️</button>
          <button class="nb-button default" title="Next page" on:click={nextPage} disabled={page + 1 >= totalPages}>➡️</button>
        </div>
        <div class="text-sm text-gray-600 dark:text-gray-400">
          <span>
            Page <b>{page + 1}</b> of <b>{totalPages}</b>
            &nbsp;|&nbsp;
            Total products: <b>{totalProducts}</b>
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
        class="max-w-[2048px] w-full overflow-x-auto rounded-lg align-middle text-center mx-auto"
      >
        {#if products.length === 0}
          no products found :/
        {:else}
          <table class="nb-table">
            <thead class="bg-base-200">
              <!-- todo: try applying this but only to darkmode
          <table class="w-full border-separate border-spacing-0">
          <thead class="[border-bottom:1px_solid_theme(colors.accent)] [box-shadow:0_4px_12px_#00ffff] z-10 relative"> 
          Also this, very nice effect lol: <table class="table table-zebra min-w-full divide-y divide-accent [filter:drop-shadow(0_0_4px_#00ffff)]" >
          -->

              <tr class="h-12">
                <th class="pgs-th">Name</th>
                <th class="pgs-th">Code</th>
                <th class="pgs-th">Description</th>
                <th class="pgs-th">Category</th>
                <th class="pgs-th">Brand</th>
                <th class="pgs-th">Price</th>
                <th class="pgs-th">Currency</th>
                <th class="pgs-th">Shipping</th>
                <th class="pgs-th">Tax</th>
                <th class="pgs-th">Discount</th>
                <th class="pgs-th">Stock</th>
                <th class="pgs-th">Comment</th>
                <th class="pgs-th">Other</th>
                <th class="pgs-th">created</th>
                <th class="pgs-th">modified</th>
                <th class="pgs-th">Active</th>
                <th class="pgs-th">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              {#each products as product, i}
                <tr class="bg-base-100 pgs-tr">
                  <td class="pgs-td">
                    <a
                      use:link
                      href="/products/{product.id}"
                      class="text-primary pgs-hyperlink">{product.name}</a
                    >
                  </td>
                  <td class="pgs-td">{product.code}</td>
                  <td class="pgs-td">{product.description}</td>
                  <td class="pgs-td">{product.category}</td>
                  <td class="pgs-td">{product.brand}</td>
                  <td class="pgs-td-num"> {product.basePrice}</td>
                  <td class="pgs-td-fmc">{product.baseCurrency}</td>
                  <td class="pgs-td-num">{product.shippingCost}</td>
                  <td class="pgs-td-num">{product.taxAmount}</td>
                  <td class="pgs-td-num">{product.discount}</td>
                  <td class="pgs-td-num">{product.stockQuantity}</td>
                  <td class="text-center"
                    >{@html formatCommentInfo(product.comment)}</td
                  >
                  <td class="pgs-td">{product.other}</td>
                  <td class="pgs-td font-mono">
                    {@html formatDate(product.created, "new", 15)}
                  </td>
                  <td class="pgs-td font-mono">
                    {@html formatDate(
                      product.modified,
                      "Changed less then 10 minutes ago",
                      15
                    )}
                  </td>

                  <td class="pgs-td font-mono"
                    >{formatActive(product.active)}</td
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
                          ><i
                            class="fas fa-pen text-gray-500 hover:text-sky-400 cursor-pointer"
                          ></i></a
                        >
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
                        </div></button
                      >
                    </div>
                  </td>
                </tr>
              {/each}
              <tr class="bg-base-100">
                <td colspan="18" class="pgs-td text-left font-mono font-bold">
                  Showing <b>{products.length}</b> product(s) on this page.<br>
                  Total products found: <b>{totalProducts}</b> | Total pages: <b>{totalPages}</b>
                </td>
              </tr>
            </tbody>
          </table>
        {/if}
      </div>
    {:else if products.length === 0 && !loading}
      no products found :/
    {:else}
      <!-- <div class="nb-card">
          <img src="..." class="nb-card-img">
          <div class="nb-card-content">
              <h4 class="nb-card-title">Card Title</h4>
              <p class="nb-card-text">...</p>
              <div class="nb-card-actions">
                  <button class="nb-button">Learn More</button>
              </div>
          </div>
       </div> -->
      <div
        class="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-8 p-4"
      >
        {#each products as product, i}
          <div class="nb-card">
            {#if product.imageUrl}
              <img
                src={product.imageUrl}
                alt={product.name}
                class="nb-card-img"
              />
            {:else}
              <span class="text-gray-400 dark:text-gray-500"
                >No image available</span
              >
            {/if}
            <div class="nb-card-content">
              <h4 class="nb-card-title pgs-hyperlink w-fit">{product.name}</h4>
              <p class="nb-card-text">
                <span
                  class="product-detail text-xl price font-bold"
                  
                  class:text-gray-400={product.active === false ||
                    product.stockQuantity === null}
                  class:dark:text-400-600={product.active === false ||
                    product.stockQuantity === null}>€</span
                >

                <span
                  class="product-detail text-3xl price font-bold"
                  
                  class:text-gray-400={product.active === false ||
                    product.stockQuantity === null}
                  class:dark:text-gray-400={product.active === false ||
                    product.stockQuantity === null}>{product.priceEur}</span
                >

                <span
                  class="product-detail text-xl price font-bold"
                  
                  class:text-gray-400={product.active === false ||
                    product.stockQuantity === null}
                  class:dark:text-gray-400={product.active === false ||
                    product.stockQuantity === null}>, 00</span
                >
              </p>
              <div class="flex">
                <div class="">
                  <div class="">{product.description}</div>
                  <br>
                  <div class="">{product.stockQuantity} left</div>
                </div>
                <div class="w-1/2 flex justify-center">
                  <div class="nb-card-actions">
                    <AddToCartButton {product} width="96px" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!--   <div
            class="nb-card"
          >
            <div
              class="w-full h-48 bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden rounded-md mb-4"
            >
              {#if product.imageUrl}
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  class="nb-card-img"
                />
              {:else}
                <span class="text-gray-400 dark:text-gray-500"
                  >No image available</span
                >
              {/if}
            </div>

            <div class="nb-card-title">
              <h1 class="text-xl font-semibold text-primary">
                <a use:link href="/products/{product.id}" class="pgs-hyperlink"
                  >{product.name}</a
                >
              </h1>
            </div>

            <div
              class="nb-card-text"
            >              
              <p class="product-detail font-thin text-gray-500">
                {product.description}
              </p>
              <div class="flex">
                <div class="nb-card-actions">
                  <h2>
                    <span
                      class="product-detail text-xl price font-bold"
                      class:text-green-600={product.active !== false && product.stockQuantity !== null}
                      class:dark:text-green-400={product.active !== false && product.stockQuantity !== null}
                      class:text-gray-400={product.active === false || product.stockQuantity === null}
                      class:dark:text-400-600={product.active === false || product.stockQuantity === null}
                    >€</span>

                    <span
                      class="product-detail text-3xl price font-bold"
                      class:text-green-600={product.active !== false && product.stockQuantity !== null}
                      class:dark:text-green-400={product.active !== false && product.stockQuantity !== null}
                      class:text-gray-400={product.active === false || product.stockQuantity === null}
                      class:dark:text-gray-400={product.active === false || product.stockQuantity === null}
                    >{product.priceEur}</span>

                    <span
                      class="product-detail text-xl price font-bold"
                      class:text-green-600={product.active !== false && product.stockQuantity !== null}
                      class:dark:text-green-400={product.active !== false && product.stockQuantity !== null}
                      class:text-gray-400={product.active === false || product.stockQuantity === null}
                      class:dark:text-gray-400={product.active === false || product.stockQuantity === null}
                    >, 00</span>
                </div>
                <div class="w-1/2 flex justify-center">
                  
                  <AddToCartButton
                    product={ product } 
                    width="96px"
                  />
                      
                </div>
              </div>
            </div>
          </div> -->
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
</style>
