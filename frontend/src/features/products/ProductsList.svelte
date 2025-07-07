<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import Modal from "./ProductModal.svelte";
  import { auth } from "../../core/services/store";
  import Login from "../../core/auth/login.svelte";
  import { get } from "svelte/store";
  import type { Product } from "./Product";
  import axios from "axios";
  import { formatDate } from "../../lib/utils";
  import { formatActive } from "../../lib/utils";
  import { formatCommentInfo } from "../../lib/utils";
  import { showSuccessToast, showErrorToast } from "../../core/toaster";
  import LoadingOverlay from "../../core/LoadingOverlay.svelte";
  import ErrorDiv from "../users/ErrorDiv.svelte";

  document.title = "Products | Pegasus";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  const fallbackImage =
    "https://images.unsplash.com/photo-1503602642458-232111445657?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHw0fHxwcm9kdWN0fGVufDB8fHx8MTc0ODAwODQyN3ww&ixlib=rb-4.1.0&q=80&w=200";

  //DEFINITIONS
  let isAuthenticated = false;
  let modalProduct: Product | null = null;
  let products: Product[] = [];
  let loading: boolean = false;
  let addToCartLoading: number | null = null; // store productId being added
  let error: string | null = null;
  let isListView = false;
  let isAdminView = true;
  let isDark = true;

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
        loading = true;
        handleSearch();
      }
    } catch (err) {
      error = err instanceof Error ? err.message : "Search failed";
    } 
  });

  let searchTerm = "";

  function handleFormSubmit(event: { preventDefault: () => void }) {
    event.preventDefault(); // prevent page reload
    handleSearch();
  }

  async function handleSearch() {
    const token = $auth.token;
    loading = true;
    try {
      const res = await fetch(API_BASE_URL + `/products?search=${searchTerm}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      // Check response status and handle specific cases
      if (!res.ok) {
        if (res.status === 401) {
          console.log("Authentication failed - token may be expired");
          // Clear invalid token
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          // Redirect to login or show login modal
          // window.location.href = '/login';
          // OR: showLoginModal = true;
          // OR: goto('/login');
          throw new Error("Authentication failed");
        }
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      // Parse JSON directly - no need for JSON.parse since res.json() already does this
      const data = await res.json();

      // Update products with the received data
      products = data;
    } catch (error: any) {

      // Handle 401 Unauthorized specifically
      if (error.message.includes("401")) {
        console.log("Authentication failed - token may be expired");
        // Clear invalid token
        $auth.token = null;
        // Redirect to login or show login modal
        // window.location.href = '/login';
        // OR: showLoginModal = true;
        // OR: goto('/login');
      }

      // Handle other errors appropriately (show user message, etc.)
    } finally {
      loading = false;
    }
  }

  function closeModal(): void {
    modalProduct = null;
  }

  function toggleView() {
    isListView = !isListView;
  }

  const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Add Bearer token if available
  axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("token"); // or getToken() if you have a helper
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  async function addToCart(productId: number): Promise<void> {
    addToCartLoading = productId;
    try {
      const response = await axiosInstance.post<{ message: string }>(
        "/cart/add",
        null,
        {
          params: {
            productId,
          },
        }
      );
      //We assume success if no error happened
      processSuccess(response);
    } catch (error: any) {
      processError(error);
    } finally {
      addToCartLoading = null;
    }
  }

  function processSuccess(response: any) {
    // Display success message
    if (response.data && response.data.message) {
      showSuccessToast(response.data.message);
    }
  }

  function processError(error: any) {
    // Extract message from error response
    let errorMessage = "Error adding product to cart: ";

    if (error.response && error.response.data) {
      // error.response.data.message should contain message
      if (error.response.data.message) {
        errorMessage += error.response.data.message;
      } else if (typeof error.response.data === "string") {
        errorMessage += error.response.data;
      }
    } else if (error.message) {
      errorMessage = error.message;
    }

    showErrorToast(errorMessage);
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}
  <div
    class="w-full max-w-4xl p-4 bg-white dark:bg-slate-900 rounded-lg shadow-md dark:shadow-gray-900/30 mx-4 sm:mx-0"
  >
    <form
      on:submit|preventDefault={handleFormSubmit}
      class="flex flex-wrap items-center gap-3"
    >
      <input
        type="text"
        bind:value={searchTerm}
        placeholder="Search products..."
        class="input input-primary"
      />
      <!-- Search Button -->
      <button type="submit" class="btn">
        <i class="fas fa-search"></i>
        Search
      </button>

      <!-- Toggle View Button -->
      <button on:click={toggleView} class="btn">
        <i class="fas fa-th-list"></i>
        Grid view / List view
      </button>

      <!-- Create product Button -->
      <button on:click={() => alert("not implemented yet")} class="btn">
        <i class="fas fa-plus"></i>
        Create new product
      </button>
    </form>
  </div>

  <div id="results" class="w-full max-w-4xl mx-auto mt-6"></div>

  {#if loading}
    
  <LoadingOverlay/>

  {:else if error}

  <ErrorDiv {error} />

  {:else}

    {#if isListView && isAdminView}
      <div
        class="max-w-[2048px] w-full overflow-x-auto shadow-md rounded-lg align-middle text-center mx-auto"
      >
        <table
          class="min-w-full divide-y divide-gray-200 dark:divide-gray-700 /*table*/"
        >
          <thead class="bg-gray-800 dark:bg-slate-800">
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
          <tbody
            class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
          >
            {#each products as product, i}
              <tr
                class="{i % 2 === 0
                  ? 'bg-white dark:bg-gray-800'
                  : 'bg-gray-50 dark:bg-gray-900'} hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors pgs-tr-hov"
              >
                <td class="pgs-td">
                  <a
                    use:link
                    href="/products/{product.id}"
                    class="pgs-hyperlink">{product.name}</a
                  >
                </td>
                <td class="pgs-td">{product.code}</td>
                <td class="pgs-td">{product.description}</td>
                <td class="pgs-td">{product.category}</td>
                <td class="pgs-td">{product.brand}</td>
                <td class="pgs-td-num">
                  {product.basePrice}</td
                >
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

                <td class="pgs-td font-mono">{formatActive(product.active)}</td>

                <td class=" justify-center">
                  <div class="tooltip" data-tip="Edit">
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
                    <div class="tooltip" data-tip="Delete">
                      <i
                        class="fas fa-times-circle text-gray-500 hover:text-red-400 cursor-pointer"
                      ></i>
                    </div></button
                  >
                </td>
              </tr>
            {/each}
            <tr class="bg-white dark:bg-slate-900">
              <td colspan="18" class="pgs-td text-left font-mono font-bold"
                >Total products found: {products.length}</td
              >
            </tr>
          </tbody>
        </table>
      </div>
    {:else}
      <div
        class="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-5 gap-8 p-4"
      >
        <!-- transition:fade={{ delay: i * 100 }}   -->
        {#each products as product, i}
          <div
            class="scale-up-center-normal product-card glow bg-white dark:bg-slate-900 shadow-lg rounded-xl overflow-hidden p-5 transition-all duration-300 hover:shadow-2xl min-w-[196px] max-w-[384px] scale-up-center-normal
                  transition-all duration-200 hover:-translate-y-1"
          >
            <!-- Product Image -->
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

            <!-- Product Header -->
            <div class="product-card-header mb-4">
              <h1 class="text-xl font-semibold text-gray-800 dark:text-white">
                <a use:link href="/products/{product.id}" class="pgs-hyperlink"
                  >{product.name}</a
                >
              </h1>
            </div>

            <!-- Product Body -->
            <div
              class="product-card-body space-y-2 text-gray-700 dark:text-gray-300"
            >
              <p class="product-detail font-thin text-zinc-500">
                {product.code}
              </p>
              <p class="product-detail font-thin text-gray-500">
                {product.description}
              </p>
              <div class="flex">
                <div class="w-1/2 flex justify-center">
                  <h2>
                    <span
                      class="product-detail text-xl price font-bold text-green-600 dark:text-green-400"
                      >€</span
                    >
                    <span
                      class="product-detail text-3xl price font-bold text-green-600 dark:text-green-400"
                      >{product.priceEur}</span
                    >
                    <span
                      class="product-detail text-xl price font-bold text-green-600 dark:text-green-400"
                      >, 00</span
                    >
                  </h2>
                </div>
                <div class="w-1/2 flex justify-center">
                  
                        {#if addToCartLoading === product.id}

                          <button
                            class="btn bg-amber-300 hover:bg-amber-400 p-2 rounded-full button-fx w-full flex items-center justify-center"
                            style="width: 96px;"
                            disabled
                            aria-label="Adding to cart"
                          >                            
                            <span class="">Adding</span>
                            <span class="loading loading-dots loading-xs"></span>
                          </button>

                        {:else}

                          <button
                            class="cursor-pointer text-black bg-amber-300 hover:bg-amber-400 p-2 rounded-full button-fx btn w-full flex items-center justify-center"
                            style="width: 96px;"
                            on:click={() => addToCart(product.id)}
                          >
                            <span data-text="Add to cart">Add to cart</span>
                            <div class="scan-line"></div>
                          </button>

                        {/if}
                      
                </div>
              </div>
            </div>
          </div>
        {/each}
      </div>
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
{/if}
