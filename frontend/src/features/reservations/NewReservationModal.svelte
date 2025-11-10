<script lang="ts">
  import { createEventDispatcher, onDestroy, onMount } from "svelte";
  import { fade } from "svelte/transition";
  import { toast } from "@zerodevx/svelte-toast";
  import api from "../../core/services/client";
  import NewReservation from "./NewReservation.svelte";

  export let isOpen = false;

  const dispatch = createEventDispatcher();

  function closeModal() {
    isOpen = false;
    dispatch("close");
  }

  // Disable scroll when modal is open
  onMount(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    }
  });

  // Watch for changes
  $: {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }

  onDestroy(() => {
    document.body.style.overflow = '';
  });

  function submitForm() 
  {
    closeModal();
  }

</script>

<!-- Modal backdrop -->
{#if isOpen}
  <div
    class="fixed inset-0 bg-black bg-opacity-50 z-40 flex items-center justify-center p-4 glassdrop"
    
    on:keydown|self={(e) =>
      (e.key === "Escape" || e.key === "Enter") && closeModal()}
    tabindex="0"
    role="button"
    aria-label="Zatvori"
    id="m-backdrop"
    transition:fade={{ duration: 200 }}
  >
    <div
      class="bg-white dark:bg-gray-800 rounded-lg max-w-md w-full z-50"
      transition:fade={{ duration: 200 }}
    >
      <div
        class="flex justify-between items-center p-4 border-b border-gray-200 dark:border-gray-700"
      >
        <h2 class="text-xl font-semibold text-gray-800 dark:text-white">
          Nova rezervacija
        </h2>
        <button
          class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
          aria-label="Zatvori"
          on:click={closeModal}
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <NewReservation/>

      <!-- <form on:submit|preventDefault={submitForm} class="p-4">
            <div class="mt-6 flex justify-end space-x-3">          
          <button type="button" on:click={closeModal} class="btn">
            Cancel
          </button>
          <button type="submit" class="btn btn-primary"> Save </button>
        </div>
      </form> -->
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
