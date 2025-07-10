<script lang="ts">
  import axios from "axios";
  import { showSuccessToast } from "../../core/toaster";
  import { auth } from "../../core/services/store";
  import Login from "../../core/auth/login.svelte";
  import LoadingOverlay from "../../core/LoadingOverlay.svelte";
  import ErrorDiv from "../users/ErrorDiv.svelte";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  document.title = "Checkout | Pegasus";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let name = "";
  let email = "";
  let phone = "";
  let address = "";
  let paymentMethod = "bank_transfer";

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  async function submitForm() {
    const url = API_BASE_URL + "/cart/checkout";
    const payload = {
      email,
      name,
      address,
      phone,
      paymentMethod,
    };

    try {
      loading = true;
      const response = await axios.post(url, payload, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
          "Content-Type": "application/json",
        },
      });

      //todo: show success message -> go to order details page. we dont have order details page yet.
      showSuccessToast("Order placed successfully!");
    } catch (err) {
      error = (err as Error).message;
    } finally {
      loading = false;
    }
  }

  function cancel(event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }) 
  {
    window.location.href = "#/cart";
  }
</script>

<div class="w-full max-w-4xl mx-auto p-4">

  {#if !$auth.isAuthenticated}

    <Login />

  {:else}
  

    {#if loading}

      <LoadingOverlay/>
     
    {/if}

     {#if error}
  
      <ErrorDiv {error} />

    {:else}

    <div
      class=" mx-auto p-4 sm:p-6 rounded-2xl mt-6 sm:mt-10" style="border: 1px solid gray; background-color: var(--color-base-200);" 
    >
      <h2
        class="text-xl font-semibold col-span-full text-gray-700 dark:text-gray-100 mb-7"
      >
        Checkout
      </h2>

      <form class="space-y-5" on:submit|preventDefault={submitForm}>

        <div class="w-full">
          <label for="Name" class="label text-sm pb-3">Name</label>
          <input
            id="name"
            class="input input-form font-bold"
            placeholder="Name"
            bind:value={name}
          />
        </div>

        <div class="w-full">
          <label for="email" class="label text-sm pb-3">e-mail</label>
          <input
            id="email"
            class="input input-form font-bold"
            placeholder="e-mail"
            bind:value={email}
          />
        </div>

        <div class="w-full">
          <label for="phone" class="label text-sm pb-3">phone</label>
          <input
            id="phone"
            class="input input-form font-bold"
            placeholder="phone"
            bind:value={phone}
          />
        </div>

        <div class="w-full">
          <label for="address" class="label text-sm pb-3">Address</label>
          <textarea
            id="address"
            class="input input-form font-bold"
            rows="3"
            placeholder="Street and number, city, postal code, country"
            bind:value={address}
          ></textarea>
        </div>

        <div class="w-full">
          <fieldset class="fieldset">
            <legend class="label text-sm pb-3">Payment Method:</legend>
            <select
              id="payment"
              bind:value={paymentMethod}
              required
              class="select input input-form font-bold"
            >
              <option value="bank_transfer" selected
                >Bank Transfer / invoice</option
              >
              <option value="credit_card" disabled>Credit Card</option>
              <option value="paypal" disabled>PayPal</option>
              <option value="crypto" disabled>Crypto / Bitcoin</option>
            </select>
          </fieldset>
        </div>

        <div class="col-span-full flex justify-end">
          <button type="button" on:click={cancel} class="btn m-3">
            Cancel
          </button>
          <button type="submit" class="btn btn-primary m-3"> Confirm </button>
        </div>

      </form>
    </div>
  {/if}
  {/if}
</div>