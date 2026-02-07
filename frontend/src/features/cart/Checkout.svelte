<script lang="ts">
  import axios from "axios";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { push } from "svelte-spa-router";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  document.title = "Checkout | Pegasus";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let stol = "";
  let user = "";
  let comment = "";

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  async function submitForm() {
    const url = API_BASE_URL + "/cart/checkout";
    const payload = {
      stol,
      user,
      comment
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
      showSuccessToast("Narudžba uspješno poslata!");
      push('/orders');
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

<div class="feat-container">

  {#if !$auth.isAuthenticated}

    <Login />

  {:else}
  

    {#if loading}

      <LoadingOverlay/>
     
    {/if}

      {#if error}
  
      <ErrorDiv {error} />

      {:else}
<div class="nb-card max-w-7xl mx-auto bg-base-100 rounded-lg p-8 w-full space-y-8 " style="width: revert; max-width:1280px; ">
  <!-- Header Section -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
    <div class="lg:col-span-1">
      <h2 class="text-4xl font-semibold text-primary ">
        Checkout
      </h2>

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

  <form class="space-y-8" on:submit|preventDefault={submitForm}>
    <!-- Contact Information Section -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
      <div class="lg:col-span-1">
        <h3 class="text-2xl font-semibold text-primary ">
          Contact Information
        </h3>
        <p class="text-secondary text-sm mt-2">
          Your personal details for order processing
        </p>
      </div>
      <div class="lg:col-span-2 ">
        <div class="">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12">

            <div class="w-full md:col-span-2">
              <label for="user" class="block text-sm font-medium text-gray-700 mb-2">
                <i class="fas fa-user text-xs text-gray-400 mr-2"></i>Konobar
              </label>
              <input
                id="user"
                class="nb-input default font-mono"
                placeholder=""
                bind:value={user}
              />
            </div>

            <div class="w-full md:col-span-2">
              <label for="stol" class="block text-sm font-medium text-gray-700 mb-2">
                <i class="fas fa-chair text-xs text-gray-400 mr-2"></i>Stol
              </label>
              <input
                id="stol"
                class="nb-input default font-mono"
                placeholder=""
                bind:value={stol}
              />
            </div>

          </div>
        </div>
      </div>
    </div>

  

    <!-- Other Section -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
      <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-primary">
        Note
      </h3>
      <p class="text-secondary text-sm mt-2">
        Additional information
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class="rounded-lg">
        <div class="">
          <div class="w-full">
            <label for="comment" class="block text-sm font-medium text-gray-700 mb-2">Comment (optional)</label>
            <textarea
              id="comment"
              class="nb-textarea resize-vertical"
              bind:value={comment}
              rows="4"
            ></textarea>
          </div>          
        </div>
      </div>
    </div>
  </div>

    <!-- Full-width underline -->
    <div class="h-px bg-neutral w-full"></div>

    <!-- Actions Section -->
    <div class="grid grid-cols-1">
    <!--   <div class="lg:col-span-1">
        <h3 class="text-2xl font-semibold text-accent ">
          Actions
        </h3>
        <p class="text-secondary text-sm mt-2">
          Confirm your order or go back to continue shopping
        </p>
      </div> -->
      <div class="flex justify-end gap-3 pt-4">
        <button type="button" on:click={cancel} class="nb-button default mr-8">
          Cancel
        </button>
        <button type="submit" class="nb-button blue">
          Confirm
        </button>
      </div>
    </div>
  </form>
</div>
  {/if}
  {/if}
</div>
<style>
  .nb-card:hover {
  transform: none;
}
.nb-card:hover {
/*   box-shadow: reset;
 */}
</style>