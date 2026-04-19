<script lang="ts">
  import { createEventDispatcher, onMount } from "svelte";
  import { fade } from "svelte/transition";
  import { toast } from "@zerodevx/svelte-toast";
  import api from "../../core/services/client";
  import type { FPGSUser } from "../users/FPGSUser";
  import type { Order } from "./Order";
  import OrderDetails from "./OrderDetails.svelte";

  export let isOpen = false;
  export let order: Order | null;

  const dispatch = createEventDispatcher();

  function closeModal() {
    isOpen = false;
    dispatch("close");
  }

  function submitForm() 
  {
    closeModal();
  }

  async function submit(newUser: FPGSUser) 
  {
    try 
    {

      //We just assume its created if no error happened
      toast.push('✅ User created');
      dispatch('close');//Ovo dispacuje event pa parent da zna da pokrene search
    } catch (error) {
      console.error("Error creating user:", error);
    }
  }

  onMount(() => {

  });
</script>

<!-- Modal backdrop -->
{#if isOpen}
  <div
    class="fixed inset-0 bg-black bg-opacity-50 z-40 flex items-center justify-center p-4 glassdrop h-7"
    
    on:keydown|self={(e) =>
      (e.key === "Escape" || e.key === "Enter") && closeModal()}
    tabindex="0"
    role="button"
    aria-label="Close modal"
    id="m-backdrop"
    transition:fade={{ duration: 200 }}
  >
    <div
      class="bg-white dark:bg-gray-800 rounded-lg max-w-md w-full z-50 overflow-auto "
      transition:fade={{ duration: 200 }}
    >
      <div
        class="flex justify-between items-center p-4 border-b border-gray-200 dark:border-gray-700"
      >
        <h2 class="text-xl font-semibold text-gray-800 dark:text-white">
          Order details
        </h2>
        <button
          class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
          aria-label="Close modal"
          on:click={closeModal}
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <form on:submit|preventDefault={submitForm} class="p-0">
        <fieldset
          class="fieldset bg-base-100 rounded-box w-full p-4"
        >
        <div style="width: 40%; height: fit-content; overflow: scroll;" >
        <OrderDetails></OrderDetails>
        </div>
        </fieldset>

        <div class="mt-6 flex justify-end space-x-3 p-4">          
          <button type="button" on:click={closeModal} class="btn">
            Cancel
          </button>
          <button type="submit" class="btn btn-primary"> Save </button>
        </div>
      </form>
    </div>
  </div>
{/if}

<style>
  .bg-black {
    background-color: #00000070;
  }
  .glassdrop{
    backdrop-filter: blur(10px);
  }
</style>
