<script lang="ts">
  import axios from "axios";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { auth, getCurrentUsername } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { push } from "svelte-spa-router";
  import { brojStola } from './../../core/services/CheckoutStore';

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  document.title = "Checkout | Pegasus";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let stol = "";
  let user = getCurrentUsername();
  let comment = "";

  const stolovi = [
    "DOSTAVA",
    "ZA VAN",
    "001",
    "100",
    "101",
    "102",
    "110",
    "120",
    "121",    
    "129",
  ];

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  async function submitForm() 
  {
    const url = API_BASE_URL + "/cart/checkout";
    const payload = {
      stol: $brojStola,//ovde mora dollar sign kad ga koristimo...
      user,
      comment
    };

    try 
    {
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
    } catch (err: any) {
      console.error("kect", err);
      
      // Proveravamo da li server uopšte vratio odgovor (response)
      if (err.response && err.response.data) 
      {
        // Ovde hvatamo tvoj "message" iz JSON-a koji je poslao Java backend
        error = "ERROR: "+err.response.data.message;
        
        // Možeš dodati i specifičan toast za ovu poruku
        //showErrorToast(error); 
      } else {
        // Ako je greška mreže ili nešto drugo (npr. timeout)
        error = (err as Error).message;
      }
    } 
    finally
    {
      loading = false;
    }
  }

  function cancel(event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }) 
  {
    window.location.href = "#/cart";
  }
</script>

<div class="w-full max-w-4xl mx-auto p-2 sm:p-4">

  {#if !$auth.isAuthenticated}

    <Login />

  {:else}  

    {#if loading}

      <LoadingOverlay/>
     
    {/if}

      {#if error}
  
      <ErrorDiv {error} />

      {:else}

    <div class="text-primary mx-auto bg-base-200 mt-6 sm:mt-10 w-full max-w-2xl" style="transform: none">
    <!-- Header Section -->
     <div class="p-0 bg-accent text-primary/80 rounded-t">
        <h2 class="text-lg sm:text-2xl font-bold p-1 text-center">Checkout</h2>
      </div>
      

  <!-- Full-width underline -->
<!--   <div class="h-px bg-neutral w-full"></div>
 -->
  <form class="p-12 w-full" on:submit|preventDefault={submitForm}>
    <!-- Contact Information Section -->
    <div class="grid grid-cols-1 mb-16 w-full">
      <div class="lg:col-span-2 ">
        <div class="">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12">

            <div class="w-full md:col-span-2">
              <label for="user" class="block text-md font-medium text-gray-700 mb-2">
                <i class="fas fa-user text-md text-primary/60 mr-2"></i>Konobar
              </label>
              <input
                id="user"
                class="pgs-input font-mono input-ghost font-bold"
                style="border: none;"
                placeholder=""
                disabled
                bind:value={user}
              />
            </div>

            <div class="w-full md:col-span-2">
             
              <div class="w-full">
                <label for="stol" class="block text-md font-medium text-gray-700 mb-2">
                <i class="fas fa-chair text-md text-primary/60 mr-2"></i>Stol
              </label>
                <select
                  id="stol"
                  bind:value={$brojStola}
                  class="pgs-input font-mono font-bold"
                  style="border: none;"
                >
                  {#each stolovi as sto}
                    <option value={sto}>{sto}</option>
                  {/each}
                </select>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>  

    <!-- Other Section -->
    <div class="grid grid-cols-1 mb-6">
      
    <div class="lg:col-span-2">
      <div class="rounded-lg">
        <div class="">
          <div class="w-full">
            <label for="comment" class="block text-md font-medium text-gray-700 mb-2">Napomena </label>
            <textarea
              id="comment"
              class="pgs-input resize-vertical"
              bind:value={comment}
              rows="4"
              style="border: none;"
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
    <!--   <div class="lg:col-span-1">
        <h3 class="text-2xl font-semibold text-accent ">
          Actions
        </h3>
        <p class="text-secondary text-sm mt-2">
          Confirm your order or go back to continue shopping
        </p>
      </div> -->
      <div class="flex justify-end gap-3 pt-4">
        <button type="button" on:click={cancel} class="btn btn-neutral text-primary/80 mr-12">
             <i class="fa fa-arrow-left text-md cursor-pointer"> </i>Nazad
        </button>
        <button type="submit" class="btn btn-primary bg-success">
             <i class="fa fa-check text-md cursor-pointer"> </i>Potvrdi
        </button>
      </div>
    </div>
  </form>
</div>
  {/if}
  {/if}
</div>
