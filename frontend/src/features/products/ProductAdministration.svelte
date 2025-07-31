<script lang="ts">
  import { type Product } from "./Product";
  import { onMount } from "svelte";
  import { params, push } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { formatDateTime } from "../../utils/formatting";
  import { showSuccessToast } from "../../core/utils/toaster";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let product: Product | null = null;
  let ID: number | string;
  const codeLength = 15;

  document.title = "Product details: | Pegasus";

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  $: {
    if ($params?.id) {
      ID = Number($params.id);
      fetch(ID); // reactively fetch when id changes
    }
  }

  // Available product types
  const userTypes = [
    "ADMIN",
    "CUSTOMER",
    "VENDOR",
    "EMPLOYEE",
    "TESTER",
    "USER",
    "OTHER",
  ];

  //IZGLEDA DA OVDE IMAMO I PRODUCT I FORM DATA, A TREBALO BI SAMO JEDAN.

  let formData: Partial<Product> = {};

  async function fetch(id: string | number) {
    loading = true;
    try {
      let data = await api<Product>("/products/" + id, {
        method: "GET",
      });
      //samo jedan treba da ostane
      formData = data;
      product = data;
    } catch (err) {
      error = (err as Error).message;
    } finally {
      loading = false;
    }
  }

  onMount(async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id) {
      fetch(id);
    }
  });

  async function handleSubmit() {
    try {
      await api<Product>(`/products/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Saved");
      push('/products?listView=true');
      //window.location.href = "/#/products";
      fetch(ID);
    } catch (err) {
      alert((err as Error).message);
    } finally {
      loading = false;
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === "Enter") {
      event.preventDefault();
      handleSubmit();
    }
  }

  function cancelEditing(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }
  ) {
    window.location.href = "#/products"; // Putanja do početne stranice
  }

  function toggleActive() {}
</script>

<div class="relative w-full h-full scale-up-center-normal">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else if loading}
    <LoadingOverlay />
  {:else if error}
    <ErrorDiv {error} />
  {:else}
    <!-- FIX TODO
Non-interactive element `<form>` should not be assigned mouse or keyboard event listeners
https://svelte.dev/e/a11y_no_noninteractive_element_interactionssvelte(a11y_no_non -->

    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
    <form
      on:submit|preventDefault={handleSubmit}
      on:keydown={handleKeydown}
      id="productForm"
      class="max-w-7xl mx-auto bg-base-100 rounded-lg p-8 w-full space-y-8"
    >
      <!-- Header Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-1">
          <h3 class="text-3xl font-semibold text-primary">Product details</h3>

          <div
            id="loadingMessage"
            style="display: none;"
            class="text-2xl font-semibold text-gray-700 dark:text-gray-100 flex items-center gap-2 mt-4"
          >
            <span class="loading loading-dots loading-xs"></span>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- General Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">General</h3>
          <p class="text-secondary text-sm mt-2">
            Basic product information and details
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="code"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Code</label
                >
                <input
                  id="code"
                  type="text"
                  class="pgs-input"
                  maxlength={codeLength}
                  size={codeLength}
                  bind:value={formData.code}
                />
              </div>

              <div class="w-full">
                <label
                  for="productname"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Name</label
                >
                <input
                  id="productname"
                  class="pgs-input"
                  bind:value={formData.name}
                />
              </div>

              <div class="w-full md:col-span-2">
                <label
                  for="description"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Description</label
                >
                 <textarea
                  id="description"
                  class="pgs-input resize-vertical"
                  bind:value={formData.description}
                  rows="4"
                ></textarea>               
              </div>

              <div class="w-full">
                <label
                  for="active"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Status</label
                >
                <div class="flex items-center space-x-3 pt-2">
                  <input
                    type="checkbox"
                    bind:checked={formData.active}
                    class="toggle ring-2 ring-primary bg-black text-red-600 checked:text-green-600"
                  />
                  <p class="font-mono text-primary">
                    {#if formData.active}
                      ACTIVE <i
                        class="fa fa-check text-green-600"
                        aria-hidden="true"
                      ></i>
                    {:else}
                      DISABLED <i
                        class="fa fa-ban text-red-600"
                        aria-hidden="true"
                      ></i>
                    {/if}
                  </p>
                </div>
                <p class="text-secondary text-sm mt-2">
                  Disabled products will not be possible to buy, but they will
                  show in results
                </p>
              </div>
              <div class="w-full">
                <div class="lg:col-span-2">
                  <div class="rounded-lg p-6">
                    <div class="flex flex-col">
                      <!-- Metadata -->
                      <div
                        class="flex flex-col sm:flex-row flex-wrap gap-x-10 gap-y-2 text-md text-secondary"
                      >
                        <span
                          class="flex items-center gap-2 min-w-[200px] text-sm"
                        >
                          <i class="fas fa-calendar-plus text-gray-400"></i>
                          Created:
                          <span class="font-mono"
                            >{formatDateTime(formData.created)}</span
                          >
                        </span>
                        <span
                          class="flex items-center gap-2 min-w-[200px] text-sm"
                        >
                          <i class="fas fa-edit text-gray-400"></i>
                          Modified:
                          <span class="font-mono"
                            >{formatDateTime(formData.modified)}</span
                          >
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Metadata -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Images</h3>
          <p class="text-secondary text-sm mt-2">Product pictures links</p>
        </div>
        <div class="w-full md:col-span-2">
          <label
            for="imglink"
            class="block text-sm font-medium text-gray-700 mb-2"
          >
            <i class="fas fa-image text-xs text-gray-400 mr-2"></i>Image link
          </label>
          <input
            id="imglink"
            class="pgs-input"
            bind:value={formData.imageUrl}
          />
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Pricing Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Pricing</h3>
          <p class="text-secondary text-sm mt-2">
            Product pricing and cost information
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="price"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Price</label
                >
                <input
                  id="price"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.basePrice}
                />
              </div>

              <div class="w-full">
                <label
                  for="currency"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Currency</label
                >
                <input
                  id="currency"
                  type="text"
                  class="pgs-input"
                  bind:value={formData.baseCurrency}
                />
              </div>

              <div class="w-full">
                <label
                  for="shipping_cost"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Shipping Cost</label
                >
                <input
                  id="shipping_cost"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.shippingCost}
                />
              </div>

              <div class="w-full">
                <label
                  for="tax_amount"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Tax Amount</label
                >
                <input
                  id="tax_amount"
                  class="pgs-input bg-gray-100"
                  disabled
                  step=".01"
                  bind:value={formData.taxAmount}
                />
              </div>

              <div class="w-full">
                <label
                  for="tax_percent"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Tax Percent</label
                >
                <input
                  id="tax_percent"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.taxPercent}
                />
              </div>

              <div class="w-full">
                <label
                  for="discount"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Discount</label
                >
                <input
                  id="discount"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.discount}
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Stock Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Stock</h3>
          <p class="text-secondary text-sm mt-2">
            Inventory and availability information
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="stock_quantity"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Stock Quantity</label
                >
                <input
                  id="stock_quantity"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.stockQuantity}
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Other Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Other</h3>
          <p class="text-secondary text-sm mt-2">
            Additional information and comments
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-1 gap-12">
              <div class="w-full">
                <label
                  for="comment"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Comment</label
                >
                <textarea
                  id="comment"
                  class="pgs-input resize-vertical"
                  bind:value={formData.comment}
                  rows="4"
                ></textarea>
              </div>

              <div class="w-full">
                <label
                  for="other"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Other</label
                >
                <input
                  id="other"
                  type="text"
                  class="pgs-input"
                  bind:value={formData.other}
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <div class="grid grid-cols-1">
        <!-- <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-primary">
        Actions
      </h3>
      <p class="text-secondary text-sm mt-2">
        Save the changes or close without saving
      </p>
    </div> -->
        <div class="flex justify-end gap-3 pt-4">
          <button
            type="button"
            on:click={cancelEditing}
            class="btn btn-outline btn- m-3"
          >
            Close
          </button>
          <button type="submit" class="btn btn-primary m-3"> Save </button>
        </div>
      </div>
    </form>
  {/if}
</div>

<style>
  .acc-disabled {
    background-color: #f8f8f8 !important;
    /*darkmode?*/
  }
  .toggle {
    background-color: rgb(57, 57, 36);
  }
</style>
