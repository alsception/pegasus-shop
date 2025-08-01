<script lang="ts">
  import { type Order } from "./Order";
  import { onMount } from "svelte";
  import { params, push } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { formatDateTime } from "../../utils/formatting";
  import { showSuccessToast } from "../../core/utils/toaster";

  document.title = "Order details | Pegasus";

  let loading: boolean = false;
  let error: string | null = null;
  let ID: number | string;

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  $: {
    if ($params?.id) {
      ID = Number($params.id);
      fetch(ID); // reactively fetch when id changes
    }
  }

  // Available order statuses
  const orderStatuses = [
    "CREATED",  
    "PENDING",
    "CONFIRMED",
    "PROCESSING",
    "SHIPPED",
    "DELIVERED",
    "CANCELLED",
    "REFUNDED",
    "RETURNED",
  ];

  // Available payment statuses
  const paymentStatuses = ["PENDING", "PAID", "FAILED", "REFUNDED", "PARTIAL"];

  // Available payment methods
  const paymentMethods = [
    "CREDIT_CARD",
    "DEBIT_CARD",
    "PAYPAL",
    "BANK_TRANSFER",
    "CASH_ON_DELIVERY",
    "CRYPTOCURRENCY",
    "OTHER",
  ];

  let formData: Partial<Order> = {
    code: "",
    status: "PENDING",
    paymentStatus: "PENDING",
    paymentMethod: "",
    taxAmount: 0,
    taxPercent: 0,
    shippingCost: 0,
    discount: 0,
    totalAmount: 0,
    currency: "EUR",
    customerName: "",
    customerEmail: "",
    customerPhone: "",
    shippingAddress: "",
    billingAddress: "",
    trackingNumber: "",
    notes: "",
    created: new Date().toISOString(),
    modified: null,
    shippedDate: null,
    deliveredDate: null,
  };

  async function fetch(id: string | number) {
    startLoadingAnimation();

    try {
      let data = await api<Order>('/orders/'+id, {
        method: "GET",
      });

      formData = data;
      console.log(data);
      document.title = `Order ${formData.code} | Pegasus`;
      error = null;

      // Convert dates to datetime-local format for form inputs
      if (data.created) {
        const date = new Date(data.created);
        formData.created = date.toISOString().slice(0, 16);
      }
      if (data.modified) {
        const date = new Date(data.modified);
        formData.modified = date.toISOString().slice(0, 16);
      }
      /* if (data.shippedDate) {
        const date = new Date(data.shippedDate);
        formData.shippedDate = date.toISOString().slice(0, 16);
      }
      if (data.deliveredDate) {
        const date = new Date(data.deliveredDate);
        formData.deliveredDate = date.toISOString().slice(0, 16);
      } */
    } catch (err) {
      processError(err);
    } finally {
      removeLoadingAnimation();
    }
  }

  function processError(err: any) {
    formData = {};
    error =
      (err as Error)?.message ||
      "Order not found or an unknown error occurred. ERR_90";
  }

  async function handleSubmit() {
    try {
      loading = true;

      const response = await api<Order>(`/orders/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      /* if (!response.ok) {
        const errorData = await response.json();
        showErrorInModal(errorData);
        return;
      }

      showSuccessToast("Order updated successfully");
      fetch(resolvedEndpoint); */
    } catch (err) {
      showErrorInModal({ message: (err as Error).message });
    } finally {
      loading = false;
    }
  }

  function showErrorInModal(error: any): void {
    const contentEl = document.getElementById("modal-content");
    const dialogEl = document.getElementById("modal") as HTMLDialogElement;

    if (contentEl) {
      let errorMessage = "An error occurred";

      if (typeof error === "string") {
        if (error.startsWith("Error: {")) {
          try {
            const jsonPart = error.substring(7);
            const parsedError = JSON.parse(jsonPart);
            errorMessage = parsedError.message || errorMessage;
          } catch (e) {
            errorMessage = error;
          }
        } else {
          errorMessage = error;
        }
      } else if (error && error.message) {
        errorMessage = error.message;
      }

      contentEl.textContent = errorMessage;
    }

    if (dialogEl) {
      dialogEl.showModal();
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === "Enter") {
      event.preventDefault();
      handleSubmit();
    }
  }

  function cancelEditing() {
    window.location.href = "#/orders";
  }

  function formatPrice(price: number): string {
    return new Intl.NumberFormat("de-DE", {
      style: "currency",
      currency: formData.currency || "EUR",
    }).format(price || 0);
  }

  const inputSkeletons =
    "#orderForm input, #orderForm select, #orderForm textarea";

  function startLoadingAnimation(): void {
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.add("skeleton");
      input.disabled = true;
    });
  }

  function removeLoadingAnimation(): void {
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "none";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.remove("skeleton");
      input.disabled = false;
    });
  }
</script>

<div class="relative w-full scale-up-center-normal">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else if error}
    <ErrorDiv {error} />
  {:else}
    {#if loading}
      <div class="fixed inset-0 z-10 flex items-center justify-center">
        <div
          class="rounded-2xl max-w-5xl w-full mx-auto flex flex-col items-center"
        >
          <span
            class="loading loading-infinity mb-2 text-blue-500"
            style="width: 4rem; height: 4rem;"
          ></span>
        </div>
      </div>
    {/if}

    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
    <form
      on:submit|preventDefault={handleSubmit}
      on:keydown={handleKeydown}
      id="orderForm"
      class="max-w-7xl mx-auto bg-base-100 rounded-lg p-8 w-full space-y-8"
    >
      <!-- Header Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-1">
          <h2 class="text-4xl font-semibold text-primary">Order details</h2>

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

      <!-- Order Information Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Order Information</h3>
          <p class="text-secondary text-sm mt-2">
            Basic order details and status
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div class="w-full">
              <label
                for="code"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-hashtag text-xs text-gray-400 mr-2"></i>Order
                Number
              </label>
              <input
                id="code"
                class="pgs-input font-mono"
                bind:value={formData.code}
              />
            </div>

            <div class="w-full">
              <label
                for="status"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Order Status</label
              >
              <select
                id="status"
                bind:value={formData.status}
                class="pgs-input font-mono"
              >
                {#each orderStatuses as status}
                  <option value={status}>{status}</option>
                {/each}
              </select>
            </div>

            <div class="w-full">
              <label
                for="paymentStatus"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Payment Status</label
              >
              <select
                id="paymentStatus"
                bind:value={formData.paymentStatus}
                class="pgs-input font-mono"
              >
                {#each paymentStatuses as status}
                  <option value={status}>{status}</option>
                {/each}
              </select>
            </div>

            <div class="w-full">
              <label
                for="paymentMethod"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Payment Method</label
              >
              <select
                id="paymentMethod"
                bind:value={formData.paymentMethod}
                class="pgs-input font-mono"
              >
                <option value="">Select method</option>
                {#each paymentMethods as method}
                  <option value={method}>{method}</option>
                {/each}
              </select>
            </div>

            <div class="w-full">
              <label
                for="trackingNumber"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-truck text-xs text-gray-400 mr-2"></i>Tracking
                Number
              </label>
              <input
                id="trackingNumber"
                class="pgs-input font-mono"
                bind:value={formData.trackingNumber}
              />
            </div>

            <div class="w-full">
              <div class="rounded-lg p-6">
                <div class="flex flex-col">
                  <div
                    class="flex flex-col sm:flex-row flex-wrap gap-x-10 gap-y-2 text-md text-secondary"
                  >
                    <span class="flex items-center gap-2 min-w-[200px] text-sm">
                      <i class="fas fa-calendar-plus text-gray-400"></i>
                      Created:
                      <span class="font-mono"
                        >{formatDateTime(formData.created)}</span
                      >
                    </span>
                    <span class="flex items-center gap-2 min-w-[200px] text-sm">
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

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Customer Information Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">
            Customer Information
          </h3>
          <p class="text-secondary text-sm mt-2">Customer contact details</p>
        </div>
        <div class="lg:col-span-2">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div class="w-full">
              <label
                for="customerName"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-user text-xs text-gray-400 mr-2"></i>Customer
                Name
              </label>
              <input
                id="customerName"
                class="pgs-input"
                bind:value={formData.customerName}
              />
            </div>

            <div class="w-full">
              <label
                for="customerEmail"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-envelope text-xs text-gray-400 mr-2"></i>Email
              </label>
              <input
                id="customerEmail"
                type="email"
                class="pgs-input"
                bind:value={formData.customerEmail}
              />
            </div>

            <div class="w-full">
              <label
                for="customerPhone"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-phone text-xs text-gray-400 mr-2"></i>Phone
              </label>
              <input
                id="customerPhone"
                type="tel"
                class="pgs-input"
                bind:value={formData.customerPhone}
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Address Information Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">
            Address Information
          </h3>
          <p class="text-secondary text-sm mt-2">
            Shipping and billing addresses
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div class="w-full">
              <label
                for="shippingAddress"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-shipping-fast text-xs text-gray-400 mr-2"
                ></i>Shipping Address
              </label>
              <textarea
                id="shippingAddress"
                class="pgs-input resize-vertical"
                bind:value={formData.shippingAddress}
                rows="4"
              ></textarea>
            </div>

            <div class="w-full">
              <label
                for="billingAddress"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-file-invoice text-xs text-gray-400 mr-2"
                ></i>Billing Address
              </label>
              <textarea
                id="billingAddress"
                class="pgs-input resize-vertical"
                bind:value={formData.billingAddress}
                rows="4"
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Order Items Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Order Items</h3>
          <p class="text-secondary text-sm mt-2">
            Products included in this order
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="space-y-4">
            {#if formData.items && formData.items.length > 0}
              <div class="divide-y divide-gray-200 dark:divide-slate-700">
                {#each formData.items as item, index (item.id || index)}
                  <div class="py-4 grid grid-cols-1 md:grid-cols-4 gap-4 items-center">
                    <div class="md:col-span-2">
                      <p class="text-base font-medium">{item.product?.name || item.productName || 'Product'}</p>
                      <p class="text-sm text-gray-500">Code: {item.product?.code || item.productCode || 'N/A'}</p>
                    </div>
                    <div class="text-center">
                      <label class="block text-xs text-gray-500 mb-1">Quantity</label>
                      <input
                        type="number"
                        min="1"
                        class="pgs-input w-20 text-center"
                        bind:value={item.quantity}
                      />
                    </div>
                    <div class="text-right">
                      <p class="text-sm text-gray-500">
                        {item.quantity} × {formatPrice(item.unitPrice || item.product?.basePrice || 0)}
                      </p>
                      <p class="text-base font-semibold">
                        {formatPrice((item.quantity || 0) * (item.unitPrice || item.product?.basePrice || 0))}
                      </p>
                    </div>
                  </div>
                {/each}
              </div>
            {:else}
              <div class="text-center py-8 text-gray-500">
                <i class="fas fa-box-open text-4xl mb-4"></i>
                <p>No items in this order</p>
              </div>
            {/if}
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Pricing Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Pricing</h3>
          <p class="text-secondary text-sm mt-2">
            Order amounts and pricing breakdown
          </p>
        </div>
        <div class="lg:col-span-2">
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
                step="0.01"
                class="pgs-input"
                bind:value={formData.price}
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
                class="pgs-input font-mono"
                bind:value={formData.currency}
              />
            </div>

            <div class="w-full">
              <label
                for="taxPercent"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Tax Percent (%)</label
              >
              <input
                id="taxPercent"
                type="number"
                step="0.01"
                class="pgs-input"
                bind:value={formData.taxPercent}
              />
            </div>

            <div class="w-full">
              <label
                for="taxAmount"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Tax Amount</label
              >
              <input
                id="taxAmount"
                type="number"
                step="0.01"
                class="pgs-input"
                bind:value={formData.taxAmount}
              />
            </div>

            <div class="w-full">
              <label
                for="shippingCost"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Shipping Cost</label
              >
              <input
                id="shippingCost"
                type="number"
                step="0.01"
                class="pgs-input"
                bind:value={formData.shippingCost}
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
                step="0.01"
                class="pgs-input"
                bind:value={formData.discount}
              />
            </div>

            <div class="w-full md:col-span-2">
              <label
                for="totalAmount"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Total Amount</label
              >
              <input
                id="totalAmount"
                type="number"
                step="0.01"
                class="pgs-input font-bold text-lg"
                bind:value={formData.totalAmount}
              />
              <p class="text-sm text-secondary mt-1">
                Formatted: {formatPrice(formData.totalAmount || 0)}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Dates Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Important Dates</h3>
          <p class="text-secondary text-sm mt-2">
            Shipping and delivery tracking
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div class="w-full">
              <label
                for="shippedDate"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-shipping-fast text-xs text-gray-400 mr-2"
                ></i>Shipped Date
              </label>
              <input
                id="shippedDate"
                type="datetime-local"
                class="pgs-input"
                bind:value={formData.shippedDate}
              />
            </div>

            <div class="w-full">
              <label
                for="deliveredDate"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                <i class="fas fa-box text-xs text-gray-400 mr-2"></i>Delivered
                Date
              </label>
              <input
                id="deliveredDate"
                type="datetime-local"
                class="pgs-input"
                bind:value={formData.deliveredDate}
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Notes Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Notes</h3>
          <p class="text-secondary text-sm mt-2">
            Additional order notes and comments
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="w-full">
            <label
              for="notes"
              class="block text-sm font-medium text-gray-700 mb-2"
              >Customer Notes</label
            >
            <textarea
              id="comment"
              class="pgs-input resize-vertical"
              bind:value={formData.comment}
              rows="4"
            ></textarea>
          </div>
        </div>
        <br>
        <div class="lg:col-span-2">
          <div class="w-full">
            <label
              for="notes"
              class="block text-sm font-medium text-gray-700 mb-2"
              >Order Notes</label
            >
            <textarea
              id="notes"
              class="pgs-input resize-vertical"
              bind:value={formData.notes}
              rows="4"
            ></textarea>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <div class="grid grid-cols-1">
        <div class="flex justify-end gap-3 pt-4">
          <button
            type="button"
            on:click={cancelEditing}
            class="btn btn-outline btn-lg m-3"
          >
            Close
          </button>
          <button type="submit" class="btn btn-primary btn-lg m-3">
            Save
          </button>
        </div>
      </div>
    </form>
  {/if}
</div>

<!-- Error Modal -->
<dialog id="modal" class="modal modal-bottom sm:modal-middle w-full">
  <div class="modal-box" style="min-width: min-content;">
    <h3 class="text-lg font-bold" id="modal-title">Error</h3>
    <p class="py-4" id="modal-content">
      An error occurred while processing your request.
    </p>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>
