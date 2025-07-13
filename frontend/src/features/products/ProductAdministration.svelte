<script lang="ts">
  import { type Product } from "./Product";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/store";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import { toast } from "@zerodevx/svelte-toast";
  import LoadingOverlay from "../../core/LoadingOverlay.svelte";
  import ErrorDiv from "../users/ErrorDiv.svelte";

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
  const productTypes = ["ADMIN", "CUSTOMER", "EMPLOYEE", "TESTER", "OTHER"];

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

      toast.push("✅ Product saved");

      window.location.href = "/#/products";
    } catch (err) {
      alert((err as Error).message);
    } finally {
      loading = false;
    }
  }

  function cancelEditing(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }
  ) {
    window.location.href = "#/products"; // Putanja do početne stranice
  }
</script>

<div class="relative w-full h-full scale-up-center-normal">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else if loading}
    <LoadingOverlay />
  {:else if error}
     <ErrorDiv {error} />
  {:else}
    <form
      on:submit|preventDefault={handleSubmit}
      class="max-w-5xl mx-auto bg-white dark:bg-gray-800 rounded p-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
    >
      <h2
        class="text-2xl font-semibold col-span-full text-gray-700 dark:text-gray-100 py-5"
      >
        Product administration
      </h2>

      <h3
        class="text-xl font-semibold col-span-full text-gray-700 dark:text-gray-100 py-5"
      >
        General
      </h3>

      <div class="w-full">
        <label for="code" class="label text-sm pb-3">Code</label>
        <input
          id="code"
          type="text"
          class="input input-form font-bold"
          maxlength={codeLength}
          size={codeLength}
          bind:value={formData.code}
        />
      </div>

      <div class="w-full">
        <label for="productname" class="label text-sm pb-3">Name</label>
        <input
          id="productname"
          class="input input-form font-bold"
          bind:value={formData.name}
        />
      </div>

      <!-- LINE 2 -->

      <div class="w-full">
        <label for="description" class="label text-sm pb-3">Description</label>
        <input
          id="description"
          type="text"
          class="input input-form font-bold"
          bind:value={formData.description}
        />
      </div>

      <div class="w-full">
        <label for="created" class="label text-sm pb-3">Created</label>
        <input
          id="created"
          type="datetime"
          disabled
          class="input input-form font-bold"
          bind:value={formData.created}
        />
      </div>

      <!-- ln 3 -->

      <div class="w-full">
        <label for="modified" class="label text-sm pb-3">Modified</label>
        <input
          id="modified"
          type="datetime"
          disabled
          class="input input-form font-bold"
          bind:value={formData.modified}
        />
      </div>

      <div class="w-full">
        <label for="imglink" class="label text-sm pb-3">Image link</label>
        <input
          id="imglink"
          class="input input-form font-bold"
          bind:value={formData.imageUrl}
        />
      </div>

      <h3
        class="text-xl font-semibold col-span-full text-gray-700 dark:text-gray-100 py-5"
      >
        Pricing
      </h3>

      <div class="w-full">
        <label for="price" class="label text-sm pb-3">Price</label>
        <input
          id="price"
          type="number"
          class="input input-form font-bold"
          bind:value={formData.basePrice}
        />
      </div>

      <div class="w-full">
        <label for="currency" class="label text-sm pb-3">Currency</label>
        <input
          id="currency"
          type="text"
          class="input input-form font-bold"
          bind:value={formData.baseCurrency}
        />
      </div>

      <!-- ln 4 -->

      <div class="w-full">
        <label for="shipping_cost" class="label text-sm pb-3"
          >Shipping_cost</label
        >
        <input
          id="shipping_cost"
          type="number"
          class="input input-form font-bold"
          bind:value={formData.shippingCost}
        />
      </div>

      <div class="w-full">
        <label for="tax_amount" class="label text-sm pb-3">Tax amount</label>
        <input
          id="tax_amount"
          class="input input-form font-bold bg-gray-100"
          disabled
          step=".01"
          bind:value={formData.taxAmount}
        />
      </div>

      <div class="w-full">
        <label for="tax_percent" class="label text-sm pb-3">Tax percent</label>
        <input
          id="tax_percent"
          type="number"
          class="input input-form font-bold"
          bind:value={formData.taxPercent}
        />
      </div>

      <div class="w-full">
        <label for="discount" class="label text-sm pb-3">Discount</label>
        <input
          id="discount"
          type="number"
          class="input input-form font-bold"
          bind:value={formData.discount}
        />
      </div>

      <h3
        class="text-xl font-semibold col-span-full text-gray-700 dark:text-gray-100 py-5"
      >
        Stock
      </h3>

      <div class="w-full">
        <label for="stock_quantity" class="label text-sm pb-3"
          >Stock quantity</label
        >
        <input
          id="stock_quantity"
          type="number"
          class="input input-form font-bold"
          bind:value={formData.stockQuantity}
        />
      </div>

      <div class="w-full">
        <label for="active" class="label text-sm pb-3">Active (available)</label
        >
        <select
          id="active"
          class="select font-mono font-bold input-form"
          on:change={(e) =>
            (formData.active = e.currentTarget.value === "true")}
        >
          <option value="true" selected={formData.active === true}
            >✅ YES</option
          >
          <option value="false" selected={formData.active === false}
            >⛔ NO</option
          >
        </select>
      </div>

      <h3
        class="text-xl font-semibold col-span-full text-gray-700 dark:text-gray-100 py-5"
      >
        Other
      </h3>

      <div class="w-full">
        <label for="comment" class="label text-sm pb-3">Comment</label>
        <textarea
          id="comment"
          class="input input-form font-bold"
          bind:value={formData.comment}
        ></textarea>
      </div>

      <div class="w-full">
        <label for="other" class="label text-sm pb-3">Other</label>
        <input
          id="other"
          type="text"
          class="input input-form font-bold"
          bind:value={formData.other}
        />
      </div>

      <div class="col-span-full flex justify-end">
        <button type="button" on:click={cancelEditing} class="btn m-3">
          Cancel
        </button>
        <button type="submit" class="btn btn-primary m-3"> Save </button>
      </div>
    </form>
  {/if}
</div>
