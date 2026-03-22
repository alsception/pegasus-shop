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

  document.title = "Product details: | Barbacoa";

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  $: {
    if ($params?.id) {
      ID = Number($params.id);
      if(ID!=0) fetch(ID); // reactively fetch when id changes, if id=0 backend will create new
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
    window.location.href = "#/products-mngmt"; // Putanja do početne stranice
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
  class="max-w-[100rem] mx-auto bg-base-100 border border-primary/10 rounded-lg p-8 w-full space-y-8"
>
  <!-- Header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h3 class="text-3xl font-semibold text-primary">Proizvod</h3>
      <div id="loadingMessage" style="display: none;" class="mt-2">
        <span class="loading loading-dots loading-xs"></span>
      </div>
    </div>
    <div class="flex gap-3">
      <button type="button" on:click={cancelEditing} class="btn btn-outline">
        <i class="fas fa-arrow-left text-primary/60"></i> Nazad
      </button>
      <button type="button" on:click={() => deleteDialog()} class="btn btn-outline hover:text-error group">
        <i class="fas fa-trash text-primary/60 group-hover:text-error text-xs"></i> Obriši
      </button>
      <button type="submit" class="btn btn-primary px-8">
        <i class="far fa-save text-primary-content"></i> Spremi
      </button>
    </div>
  </div>

  <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
    <!-- Left column -->
    <div class="lg:col-span-7 space-y-8">

      <!-- Osnovno -->
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6">
          <h3 class="text-xl font-semibold text-primary">Osnovno</h3>
          <p class="text-secondary text-sm uppercase tracking-wider">Osnovne informacije o proizvodu</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label for="code" class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-barcode text-xs text-gray-400 mr-1"></i> Šifra
            </label>
            <input id="code" type="text" class="pgs-input w-full" maxlength={codeLength} bind:value={formData.code} />
          </div>

          <div>
            <label for="productname" class="block text-sm font-medium text-secondary mb-2">Naziv</label>
            <input id="productname" class="pgs-input w-full" bind:value={formData.name} />
          </div>

          <div class="md:col-span-2">
            <label for="description" class="block text-sm font-medium text-secondary mb-2">Opis</label>
            <textarea id="description" class="pgs-input w-full resize-vertical" bind:value={formData.description} rows="4"></textarea>
          </div>

          <div>
            <label for="active" class="block text-sm font-medium text-secondary mb-2">Status</label>
            <div class="flex items-center space-x-3 pt-2">
              <input
                type="checkbox"
                bind:checked={formData.active}
                class="text-sm toggle ring-2 ring-primary bg-base-100 text-gray-600 checked:text-green-600"
              />
              <p class="font-mono font-bold text-primary/80">
                {#if formData.active}
                  <span class="text-green-600 text-lg">AKTIVAN</span>
                {:else}
                  <span class="text-gray-600 text-lg">NEAKTIVAN</span>
                {/if}
              </p>
            </div>
            <p class="text-secondary text-xs mt-2">Neaktivni proizvodi neće biti dostupni za narudžbu</p>
          </div>
        </div>
      </div>

      <!-- Cijene -->
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6">
          <h3 class="text-xl font-semibold text-primary">Cijene</h3>
          <p class="text-secondary text-sm uppercase tracking-wider">Cijena, porez i popust</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label for="price" class="block text-sm font-medium text-secondary mb-2">Cijena</label>
            <input id="price" type="number" class="pgs-input w-full" bind:value={formData.basePrice} />
          </div>

          <div>
            <label for="currency" class="block text-sm font-medium text-secondary mb-2">Valuta</label>
            <input id="currency" type="text" class="pgs-input w-full" bind:value={formData.baseCurrency} />
          </div>

          <div>
            <label for="shipping_cost" class="block text-sm font-medium text-secondary mb-2">Troškovi dostave</label>
            <input id="shipping_cost" type="number" class="pgs-input w-full" bind:value={formData.shippingCost} />
          </div>

          <div>
            <label for="tax_percent" class="block text-sm font-medium text-secondary mb-2">PDV %</label>
            <input id="tax_percent" type="number" class="pgs-input w-full" bind:value={formData.taxPercent} />
          </div>

          <div>
            <label for="tax_amount" class="block text-sm font-medium text-secondary mb-2">Iznos PDV-a</label>
            <input id="tax_amount" class="pgs-input w-full bg-base-300 opacity-60" disabled bind:value={formData.taxAmount} />
          </div>

          <div>
            <label for="discount" class="block text-sm font-medium text-secondary mb-2">Popust</label>
            <input id="discount" type="number" class="pgs-input w-full" bind:value={formData.discount} />
          </div>
        </div>
      </div>

    </div>

    <!-- Right column -->
    <div class="lg:col-span-5 space-y-8">

      <!-- Slika -->
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6 border-b border-neutral/10 pb-2">
          <h3 class="text-xl font-semibold text-primary">Slika</h3>
        </div>
        <div>
        <div class="w-full h-96  bg-gray-100 dark:bg-black flex items-center justify-center overflow-hidden">
            {#if formData.imageUrl}
              <img
                class="w-full h-full object-cover"
                src={formData.imageUrl}
                alt={formData.name}
              />
            {:else}
              <span class="text-gray-400 dark:text-gray-500">No image available</span>
            {/if}
          </div>
          <label for="imglink" class="block text-sm font-medium text-secondary mb-2">
            <i class="fas fa-image text-xs text-gray-400 mr-1"></i> Link slike
          </label>
          <input id="imglink" class="pgs-input w-full" bind:value={formData.imageUrl} />
        </div>
      </div>

      <!-- Zalihe -->
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6 border-b border-neutral/10 pb-2">
          <h3 class="text-xl font-semibold text-primary">Zalihe</h3>
        </div>
        <div class="space-y-4">
          <div>
            <label for="stock_quantity" class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-boxes text-xs text-gray-400 mr-1"></i> Količina na stanju
            </label>
            <input id="stock_quantity" type="number" class="pgs-input w-full" bind:value={formData.stockQuantity} />
          </div>
          <div class="mt-6">
            <label for="department" class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-door-open text-xs text-gray-400 mr-1"></i> Odjel
            </label>
            <select id="department" class="pgs-input w-full font-mono" bind:value={formData.department}>
              <option value={1}>Kuhinja</option>
              <option value={2}>Bar</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Ostalo -->
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <h3 class="text-xl font-semibold text-primary mb-6">Ostalo</h3>
        <label for="comment" class="block text-sm font-medium text-secondary mb-2">Napomena</label>
        <textarea id="comment" class="pgs-input w-full resize-vertical" bind:value={formData.comment} rows="6"></textarea>
      </div>

    </div>

    <!-- Footer timestamps -->
    <div class="p-4 text-[14px] text-secondary flex flex-col lg:flex-row lg:items-center lg:justify-between gap-2 lg:gap-10 w-max">
      <div class="flex items-center gap-2">
        <i class="fas fa-calendar-plus text-gray-400"></i>
        <span>Upisano: <span class="font-mono">{formatDateTime(formData.created)}</span></span>
      </div>
      <div class="flex items-center gap-2">
        <i class="fas fa-edit text-gray-400"></i>
        <span>Izmijenjeno: <span class="font-mono">{formatDateTime(formData.modified)}</span></span>
      </div>
    </div>
  </div>
</form>
  {/if}
</div>

<style>
  .toggle {
    background-color: rgb(57, 57, 36);
  }
</style>
