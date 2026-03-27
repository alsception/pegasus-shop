<script lang="ts">
  import axios from "axios";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { auth, getCurrentUsername } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { link, push } from "svelte-spa-router";
  import { ProductService } from "../products/ProductService";
  import { slide } from "svelte/transition";
  import { onMount } from "svelte";
  import type { Cart } from "./Cart";
  import api from "../../core/services/client";
  import type { FPGSUser } from "../users/FPGSUser";
  import EmptyImg1 from "../../assets/img/empty-amico-1.svg";
  import EmptyImg2 from "../../assets/img/empty-amico.svg";
  import EmptyImg3 from "../../assets/img/empty-bro-1.svg";
  import EmptyImg4 from "../../assets/img/empty-bro.svg";
  import EmptyImg5 from "../../assets/img/empty-pana.svg";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  document.title = "Checkout | Barbacoa";

  let cart: Cart | null = null;
  let loading: boolean = false;
  let error: string | null = null;
  let user = getCurrentUsername();
  let address = "";
  let phone = "";
  let comment = "";
  let takeAway: boolean = false; 
  let userData: Partial<FPGSUser> = {
    role: "",
    username: "",
    firstName: "",
    lastName: "",
    active: null,
    created: new Date().toISOString(),
    modified: null,
    comment: "",
  };

  const emptyImages = [EmptyImg1, EmptyImg2, EmptyImg3, EmptyImg4, EmptyImg5];

  // Get a random image (TODO: see if this has sense, to load many images at once )
  const randomImage =
    emptyImages[Math.floor(Math.random() * emptyImages.length)];


  //AXIOS DEf
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

  onMount(async () => {
    loadUser();
    await loadCart();
  });

  async function loadCart() 
  {
    loading = true;
    try 
    {
      const response = await axiosInstance.get<Cart>("cart");
      cart = response.data;
    } 
    catch (err) 
    {
      error = err instanceof Error ? err.message : "Unknown error";
    } 
    finally 
    {
      loading = false;
    }
  }

  async function loadUser() 
  {
    try 
    {
      let data = await api<FPGSUser>("/users/my-account",{
        method: "GET",
      });
      userData = data;
      if(userData.address) address = userData.address;
      if(userData.phone) phone = userData.phone;

    } 
    catch (err) 
    {
      console.error(err);
    } 
    finally 
    {
      
    }
  }

  async function submitForm() {
    const url = API_BASE_URL + "/cart/checkout";
    const payload = {
      user,
      address,
      phone,
      comment,
      takeAway
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
      ProductService.clearCart(); //Mora da resetujemo cart items da neprikazuje kao dodate proizvode
      push("/home");//orders page otvorimo na narudzbe na cekanju
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

  // Ovo se izvršava kad god se takeAway promeni
  $: {
    if (takeAway) 
    {
      //Korisnik preuzima lično, brišem adresu iz memorije...
      address = ""; // Automatski resetuješ polje kad god se čekira checkbox
    }
  }
</script>

<div class="w-full max-w-4xl mx-auto p-2 sm:p-4">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else}
    
  {#if loading}
      
    <LoadingOverlay />

    {:else if error}
      
      <ErrorDiv {error} />

    {:else}
      <div
        class="text-primary mx-auto bg-base-200 dark:bg-black lg:dark:bg-base-200 mt-6 sm:mt-10 w-full max-w-2xl"
        style="transform: none"
      >
        <div
          class="p-0 bg-base-100 text-primary-content/80 dark:text-primary/80 border-2 border-secondary/20 rounded-t-md"
        >
          <h2 class="text-primary text-lg sm:text-2xl font-bold p-1 text-center h-14 pt-3">
            Checkout
          </h2>
        </div>

         {#if cart && cart.items && cart.items.length > 0}

        <form class="p-6 sm:p-12 sm:pb-6 w-full border-2 border-secondary/20 border-t-0" 
          on:submit|preventDefault={submitForm}>
          <div class="grid grid-cols-1 mb-6 w-full">
            <div class="lg:col-span-2">
              <div class="">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
                  
                  <div class="w-full md:col-span-2">
                    <label
                      for="phone"
                      class="block text-md font-medium text-primary/80 mb-2"
                    >
                      <i class="fas fa-phone text-sm text-primary/60 mr-2"
                      ></i>Telefon<span class="text-error ml-2">*</span>
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
                  {#if !takeAway}
                  <div class="w-full" transition:slide={{ duration: 300 }}>
                    <label
                      for="address"
                      class="block text-md font-medium text-primary/80 mb-2"
                    >
                      <i class="fas fa-house text-sm text-primary/60 mr-2"></i>
                      Adresa za dostavu<span class="text-error ml-2">*</span>
                    </label>
                    
                    <textarea
                      id="address"
                      class="pgs-input resize-vertical"
                      bind:value={address}
                      rows="4"
                      style="border: none;"
                      maxlength="255"
                      required={!takeAway} 
                    ></textarea>
                  </div>
                {/if}
                  <label class="fieldset-legend w-fit cursor-pointer hover:text-info">
                    <input 
                      type="checkbox" 
                      bind:checked={takeAway} 
                      class="checkbox border" 
                    />
                  Preuzimanje u restoranu (15-25 minuta)
                </label>
                </div>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 mb-6">
            <div class="lg:col-span-2">
              <div class="rounded-lg">
                <div class="">
                  <div class="w-full">
                    <label
                      for="comment"
                      class="block text-md font-medium text-primary/80 mb-2"
                      ><i class="fas fa-comment text-sm text-primary/60 mr-2"
                      ></i>Napomena
                    </label>
                    <textarea
                      id="comment"
                      class="pgs-input resize-vertical"
                      bind:value={comment}
                      rows="4"
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
                class="btn btn-ghost text-primary/80 mr-12"
              >
                <i class="fa fa-arrow-left text-md cursor-pointer"> </i>Nazad
              </button>
              <button type="submit" class="btn btn-primary bg-primary text-accent">
                <i class="fa fa-check text-md cursor-pointer"> </i>Potvrdi
              </button>
            </div>
          </div>
        </form>

        {:else}

            <p class="text-center text-gray-500 py-8 px-4">
              Košarica je prazna. <a
                use:link
                href="/products"
                class="pgs-hyperlink">Dodaj proizvod</a
              >
            </p>
            <div class="flex justify-center pb-4">
              <img src={randomImage} alt="Cart empty" class="max-w-xs" />
            </div>
            
          {/if}

      </div>
    {/if}
  {/if}
</div>