<script lang="ts">
  import axios from "axios";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { auth, getCurrentUsername } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { push } from "svelte-spa-router";
  import { brojStola } from "./../../core/services/CheckoutStore";
  import { resetCartItems } from "../products/ProductService";
  import { fly } from "svelte/transition";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  document.title = "Checkout | Pegasus";

  let loading: boolean = false;
  let error: string | null = null;
  let user = getCurrentUsername();
  let address = "";
  let phone = "";
  //let comment = "";

  async function submitForm() {
    const url = API_BASE_URL + "/cart/checkout";
    const payload = {
      user,
      address,
      phone,
    };

    try {
      loading = true;
      const response = await axios.post(url, payload, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
          "Content-Type": "application/json",
        },
      });

      showSuccessToast("Narudžba uspješno poslata!");
      resetCartItems(); //Mora da resetujemo cart items da neprikazuje kao dodate proizvode
      push("/orders?v=1");//orders page otvorimo na narudzbe na cekanju
    } catch (err: any) {
      console.error("kect", err);

      // Proveravamo da li server uopšte vratio odgovor (response)
      if (err.response && err.response.data) {
        // Ovde hvatamo tvoj "message" iz JSON-a koji je poslao Java backend
        error = "ERROR: " + err.response.data.message;
      } else {
        // Ako je greška mreže ili nešto drugo (npr. timeout)
        error = (err as Error).message;
      }
    } finally {
      loading = false;
    }
  }

  function cancel(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }
  ) {
    window.location.href = "#/cart";
  }
</script>

<div class="w-full max-w-4xl mx-auto p-2 sm:p-4">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else}
    {#if loading}
      <LoadingOverlay />
    {/if}

    {#if error}
      <ErrorDiv {error} />
    {:else}
      <div
        class="text-primary mx-auto bg-base-200 dark:bg-black lg:dark:bg-base-200 mt-6 sm:mt-10 w-full max-w-2xl"
        style="transform: none"
        transition:fly={{ y: -50, duration: 300 }}
      >
        <div
          class="p-0 bg-base-100 text-primary-content/80 dark:text-primary/80 border-2 border-secondary/20 rounded-t-md"
        >
          <h2 class="text-primary text-lg sm:text-2xl font-bold p-1 text-center h-14 pt-3">
            Checkout
          </h2>
        </div>

        <form class="p-12 w-full border-2 border-secondary/20 " on:submit|preventDefault={submitForm}>
          <div class="grid grid-cols-1 mb-16 w-full">
            <div class="lg:col-span-2">
              <div class="">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
                  
                  <div class="w-full md:col-span-2">
                    <label
                      for="phone"
                      class="block text-md font-medium text-primary/80 mb-2"
                    >
                      <i class="fas fa-phone text-sm text-primary/60 mr-2"
                      ></i>Telefon
                    </label>
                    <input
                      id="phone"
                      class="pgs-input font-mono input-ghost font-bold"
                      placeholder=""
                      required                      
                      bind:value={phone}
                    />
                  </div>               
                </div>
              </div>
            </div>
          </div>

          <!-- Address Section -->
          <div class="grid grid-cols-1 mb-6">
            <div class="lg:col-span-2">
              <div class="rounded-lg">
                <div class="">
                  <div class="w-full">
                    <label
                      for="address"
                      class="block text-md font-medium text-primary/80 mb-2"
                      ><i class="fas fa-house text-sm text-primary/60 mr-2"
                      ></i>Adresa
                    </label>
                    <textarea
                      id="address"
                      class="pgs-input resize-vertical"
                      bind:value={address}
                      rows="8"
                      style="border: none;"
                      maxlength="255"
                    ></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Full-width underline -->
          <div class="h-px bg-neutral w-full"></div>

          <!-- Actions Section -->
          <div class="grid grid-cols-1 mt-6">
            <div class="flex justify-between gap-3 pt-4">
              <button
                type="button"
                on:click={cancel}
                class="btn btn-neutral text-primary/80 mr-12"
              >
                <i class="fa fa-arrow-left text-md cursor-pointer"> </i>Nazad
              </button>
              <button type="submit" class="btn btn-primary bg-primary">
                <i class="fa fa-check text-md cursor-pointer"> </i>Potvrdi
              </button>
            </div>
          </div>
        </form>
      </div>
    {/if}
  {/if}
</div>