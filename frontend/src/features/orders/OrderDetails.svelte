<script lang="ts">
  import { type Order } from "./Order";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import {
    formatPrice,
    formattedTime,
    getOrderStatusColor,
    getOrderStatusLabel,
  } from "../../utils/formatting";
  import { showSuccessToast } from "../../core/utils/toaster";

  document.title = "Order details | Pegasus";

  let loading: boolean = false;
  let error: string | null = null;
  export let ID: number | string;

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  $: {
    if ($params?.id) {
      ID = Number($params.id);
      fetch(ID); // reactively fetch when id changes
    }
  }

  onMount(() => {
    fetch(ID);
  });

  // Available order statuses
  const orderStatuses = [
    "WAITING",
    "IN_PREPARATION",
    "READY",
    "DELIVERED",
    //---------
    "CANCELLED",
    "RETURNED",
  ];

  // Available payment statuses
  const paymentStatuses = ["PENDING", "PAID", "FAILED", "REFUNDED", "PARTIAL"];

  // Available payment methods
  const paymentMethods = ["CASH", "CARD", "CRYPTOCURRENCY", "OTHER"];

  let formData: Partial<Order> = {
    code: "",
    status: "",
    paymentMethod: "",
    taxPercent: 0,
    shippingCost: 0,
    discount: 0,
    totalAmount: 0,
    currency: "",
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
    upripremiAt: null,
    spremnoAt: null,
  };

  async function fetch(id: string | number) {
    startLoadingAnimation();

    try {
      let data = await api<Order>("/orders/" + id, {
        method: "GET",
      });

      formData = data;
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
      console.log("sending data", formData);
      const response = await api<Order>(`/orders/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Order saved");
      fetch(ID);
    } catch (err) {
      console.log(err);
      showErrorInModal({ message: (err as Error).message });
    } finally {
      loading = false;
    }
  }

  function showErrorInModal(error: any): void {
    //Note to myself: moguce da nam vecina ovoga zapravo netreba
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
    <div class="wrapper">
      <div class="max-w-[520px] wrap-anywhere">
        <ErrorDiv {error} />
      </div>
    </div>
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
      class="max-w-7xl mx-auto bg-base-100 rounded-lg p-2 w-full space-y-8"
    >
      <!-- Order Information Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-10 items-start">

  <div>
    <h4 class="font-mono text-2xl">{formData.code}</h4>
  </div>

  

  <div class="lg:text-right">
    {#if formData.status}
      <span
        class="badge badge-soft badge-{getOrderStatusColor(formData.status)} font-mono badge-lg uppercase"
      >
      {getOrderStatusLabel(formData.status)}
      </span>
    {/if}
  </div>

</div>
<div>
    <h6 class="font-mono text-xl"><i class="fas fa-user text-primary/40">&nbsp;</i>{formData.user?.username}</h6>
  </div>

      <div class="w-full">
        <div class="rounded-lg p-">
          <ul class="steps steps-vertical lg:steps-horizontal w-full">
            <li class="step step-secondary">Primljeno: <br>{formattedTime(formData.created)}</li>
            <li class="step" class:step-secondary={formData.upripremiAt != null}>U pripremi: <br>{formattedTime(formData.upripremiAt)}</li>
            <li class="step" class:step-secondary={formData.spremnoAt != null}>Spremno: <br>{formattedTime(formData.spremnoAt)}</li>
            <li class="step" class:step-secondary={formData.deliveredAt != null}>Servirano: <br>{formattedTime(formData.deliveredAt)}</li>
          </ul>
          
          <div class="flex hidden">


            <div
              class="flex flex-col sm:flex-row flex-wrap gap-x-10 gap-y-2 text-md text-secondary"
            >
              <span
                class="flex items-center gap-2 min-w-[100px] text-md badge badge-warning badge-lg"
              >
                <i class="fas fa-edit"></i>
                <span class="font-mono font-bold"
                  >{formattedTime(formData.created)}</span
                >
              </span>
              {#if formData.upripremiAt != null}
                <span
                  class="flex items-center gap-2 min-w-[100px] text-md badge badge-info badge-lg"
                >
                  <i class="fas fa-fire"></i>
                  <span class="font-mono font-bold"
                    >{formattedTime(formData.upripremiAt)}</span
                  >
                </span>
              {/if}

              {#if formData.spremnoAt != null}
                <span
                  class="flex items-center gap-2 min-w-[100px] text-md badge badge-success badge-lg"
                >
                  <i class="fas fa-check"></i>
                  <span class="font-mono font-bold"
                    >{formattedTime(formData.spremnoAt)}</span
                  >
                </span>
              {/if}
            </div>
          </div>
        </div>
      </div>
      <div class="h-px bg-neutral w-full"></div>

      <!-- Order Items Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-4">
        <div class="lg:col-span-2">
          <div class="space-y-4">
            {#if formData.items && formData.items.length > 0}
              <div class="divide-y divide-gray-200 dark:divide-slate-700">
                {#each formData.items as item, index (item.id || index)}
                  <div
                    class="py-1 grid grid-cols-1 sm:grid-cols-3 gap-0 items-center"
                  >
                    <div class="sm:col-span-2 flex flex-col gap-0.5">
                      <div class="flex items-center gap-1">
                        <div
                          class="flex items-center rounded-md overflow-hidden border border-primary/10 shrink-0 h-6"
                        ></div>

                        <p
                          class="text-sm sm:text-base font-medium text-primary px-1"
                        >
                          {item.quantity} x {item.product.name} ({formatPrice(
                            item.product.basePrice
                          )})
                        </p>
                      </div>
                    </div>

                    <div
                      class="text-right sm:text-right pt-0 sm:pt-0 relative -top-6 h-0 sm:top-0"
                    >
                      <span class="text-sm sm:text-base text-primary font-mono">
                        {formatPrice(item.quantity * item.product.basePrice)}
                      </span>
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
      <!--       <div class="h-px bg-neutral w-full"></div>
 -->
      <!-- Notes Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-1 mb-1">
        <!--  <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Notes</h3>
          <p class="text-secondary text-sm mt-2">
            Additional order notes and comments
          </p>
        </div> -->

        <div class="lg:col-span-2">
          <div class="w-full">
            <label
              for="notes"
              class="block text-sm font-medium text-gray-700 mb-2"
            >
              Napomena</label
            >
            <textarea
              id="notes"
              class="pgs-input resize-vertical rounded-md"
              style="background-color: var(--color-base-200);"
              bind:value={formData.comment}
              rows="4"
            ></textarea>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <!-- <div class="h-px bg-neutral w-full hidden"></div> -->

      <div class="grid grid-cols-1 hidden">
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
    <h3 class="text-lg font-bold text-red-700" id="modal-title">Error</h3>
    <p class="py-4 text-red-600" id="modal-content">
      An error occurred while processing your request.
    </p>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>

<style>
  .wrapper {
    display: flex;
    /*     flex-direction: column;
 */
    align-items: center;
    justify-content: center;
    min-height: 70vh;
    text-align: center;
    padding: 2rem;
    background-color: transparent;
  }
</style>
