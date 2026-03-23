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

  document.title = "Order details | Barbacoa";

  let loading: boolean = false;
  let error: string | null = null;
  export let ID: number | string;
  let rows = 2;

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
    discount: 0,
    currency: "",
    comment: "",
    created: null,
    modified: null,
    servedAt: null,
    upripremiAt: null,
    spremnoAt: null,
  };

  async function fetch(id: string | number) 
  {
    startLoadingAnimation();

    try 
    {
      let data = await api<Order>("/orders/" + id, {
        method: "GET",
      });

      formData = data;
      document.title = `Narudžba ${formData.code} | Pegasus`;
      error = null;
      if(formData.comment && formData.comment.toString.length > -1 )
        rows = 5;
    } 
    catch (err) 
    {
      processError(err);
    } 
    finally 
    {
      removeLoadingAnimation();
    }
  }

  function processError(err: any) 
  {
    formData = {};
    error =
      (err as Error)?.message ||
      "Order not found or an unknown error occurred. ERR_90";
  }

  async function handleSubmit() 
  {
    try 
    {
      loading = true;
      const response = await api<Order>(`/orders/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Order saved");
      fetch(ID);
    } 
    catch (err) 
    {
      console.log(err);
      showErrorInModal({ message: (err as Error).message });
    } 
    finally 
    {
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
      <div
        class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-4 pb-4 items-start border-b-2 border-secondary/10"
      >
        <div>
          <h4 class="font-mono text-2xl">#{formData.code}</h4>
        </div>

        <div class="flex gap-6">
          <h6 class="font-mono text-md font-semibold">
            <i class="fas fa-user text-primary/60">&nbsp;</i>
            {formData.user?.username}
          </h6>
          <h6 class="font-mono text-md font-semibold">
            <i class="fas fa-chair text-primary/60">&nbsp;</i>
            {formData.stol}
          </h6>
        </div>

        <div class="absolute top-0 right-0">
          {#if formData.status}
            <span
              class="badge badge-soft badge-{getOrderStatusColor(
                formData.status
              )} font-mono badge-lg uppercase font-bold"
            >
              {getOrderStatusLabel(formData.status)}
            </span>
          {/if}
        </div>
      </div>

      <div class="w-full p-0 m-0 pb-4 mb-4 border-b-2 border-secondary/10">
        <div class="rounded-lg p-">
          <ul
            class="steps steps-vertical lg:steps-horizontal w-full text-primary/80"
          >
            <li class="step step-secondary">
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-edit"></i>Primljeno</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.created)}</span
                >
              </span>
            </li>
            <li
              class="step"
              class:step-secondary={formData.upripremiAt != null}
            >
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-fire"></i>U pripremi</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.upripremiAt)}</span
                >
              </span>
            </li>
            <li class="step" class:step-secondary={formData.spremnoAt != null}>
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-check"></i>Spremno</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.spremnoAt)}</span
                >
              </span>
            </li>
            <li
              class="step"
              class:step-secondary={formData.servedAt != null}
            >
              <span class="flex flex-row lg:flex-col gap-1 items-center">
                <span class="m-0 badge min-w-[110px]"
                  ><i class="fas fa-check-double"></i>Dostavljeno</span
                >
                <span class="font-mono font-bold badge-md badge badge-ghost"
                  >{formattedTime(formData.servedAt)}</span
                >
              </span>
            </li>
          </ul>
        </div>
      </div>

      <!-- Order Items Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-4">
        <div class="lg:col-span-2">
          <div class="space-y-4">
            {#if formData.items && formData.items.length > 0}
              <div class="divide-y divide-primary/10">
                {#each formData.items as item, index (item.id || index)}
                  <div
                    class="py-1 grid grid-cols-1 sm:grid-cols-3 gap-0 items-center"
                  >
                    <div class="sm:col-span-2 flex flex-col gap-0.5">
                      <div class="flex items-center gap-1">
                        <p
                          class="text-sm sm:text-base font-medium text-primary/80 px-1"
                        >
                          {item.quantity} x {item.product.name}
                          {#if item.quantity > 1}
                            <span class="text-xs text-primary/40"> ({formatPrice(item.product.basePrice)}) </span>
                          {/if}
                        </p>
                      </div>
                    </div>

                    <div class="text-right pt-0 sm:pt-0 relative mt-[0.14rem]">
                      <span
                        class="text-sm sm:text-base text-primary/80 font-mono"
                      >
                        {formatPrice(item.quantity * item.product.basePrice)}
                      </span>
                    </div>
                  </div>
                {/each}
                <div
                  style="align-items: end;display:grid;align-content: end; text-align: right; "
                  class="p-0 mr-0"
                >
                  <span
                    class="text-2xl font-bold text-primary p0 pt-2 font-mono"
                    >{formatPrice(formData.price)}</span
                  >
                </div>
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

      <!-- Notes Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-1 mb-1">
        <div class="lg:col-span-2">
          <div class="w-full">
            <label
              for="notes"
              class="block text-sm font-medium text-gray-700 mb-2"
            >
              Napomena</label
            >
            <!-- TODO: staviti ovde da bude veci rows ako ima comment -->
            <textarea
              id="notes"
              class="pgs-input resize-vertical rounded-md"
              style="background-color: var(--color-base-200);"
              bind:value={formData.comment}
              rows="{rows}"
            ></textarea>
          </div>
        </div>
      </div>

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
    align-items: center;
    justify-content: center;
    min-height: 70vh;
    text-align: center;
    padding: 2rem;
    background-color: transparent;
  }
</style>
