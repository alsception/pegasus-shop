<script lang="ts">
  import { createEventDispatcher, onMount } from "svelte";
  import { fade } from "svelte/transition";
  import type { FPGSUser } from "./FPGSUser";
  import { toast } from "@zerodevx/svelte-toast";
  import api from "../../core/services/client";

  export let isOpen = false;

  const dispatch = createEventDispatcher();

  let newUser: FPGSUser;

  function resetUser() {
    newUser = {
      id: 0,
      role: "",
      username: "",
      firstName: "",
      lastName: "",
      active: true,
      created: null,
      modified: null,
      password: null,
      email: null,
      phone: null,
    };
  }

  // Available user types
  const userTypes = ["ADMIN", "CUSTOMER", "VENDOR", "EMPLOYEE", "TESTER", "USER", "OTHER"];

  function closeModal() {
    isOpen = false;
    dispatch("close");
  }

  async function createUser(user: FPGSUser) {
    return api<FPGSUser>("/users", {
      method: "POST",
      body: JSON.stringify(user),
    });
  }

  function submitForm() 
  {
    submit(newUser);
    closeModal();
    resetUser();
  }

  async function submit(newUser: FPGSUser) 
  {
    try 
    {
      await createUser(newUser);
      //We just assume its created if no error happened
      toast.push('✅ User created');
      dispatch('close');//Ovo dispacuje event pa parent da zna da pokrene search
    } catch (error) {
      console.error("Error creating user:", error);
    }
  }

  onMount(() => {
    resetUser();
  });
</script>

<!-- Modal backdrop -->
{#if isOpen}
  <div
    class="fixed inset-0 bg-black bg-opacity-50 z-40 flex items-center justify-center p-4 glassdrop"
    
    on:keydown|self={(e) =>
      (e.key === "Escape" || e.key === "Enter") && closeModal()}
    tabindex="0"
    role="button"
    aria-label="Close modal"
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
          New User
        </h2>
        <button
          class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
          aria-label="Close modal"
          on:click={closeModal}
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <form on:submit|preventDefault={submitForm} class="p-4">
        <fieldset
          class="fieldset bg-base-200 border-base-300 rounded-box w-full border p-4"
        >
          <label class="label" for="username">Username</label>
          <input
            type="text"
            class="pgs-input mb-2"
            placeholder="username"
            bind:value={newUser.username}
            required
          />

          <label class="label" for="password">Password</label>
          <input
            type="password"
            class="pgs-input mb-2"
            placeholder="password"
            bind:value={newUser.password}
            required
          />

          <label class="label" for="firstName">First name</label>
          <input
            type="text"
            class="pgs-input mb-2"
            placeholder="First name"
            bind:value={newUser.firstName}
          />

          <label class="label" for="lastName">Last name</label>
          <input
            type="text"
            class="pgs-input mb-2"
            placeholder="Last name"
            bind:value={newUser.lastName}
          />

          <label class="label" for="email">email</label>
          <input
            type="text"
            class="pgs-input mb-2"
            placeholder="email"
            bind:value={newUser.email}
          />

          <label class="label" for="phone">phone</label>
          <input
            type="text"
            class="pgs-input mb-2"
            placeholder="phone"
            bind:value={newUser.phone}
          />

          <label for="role" class="label"> Role </label>
          <select id="type" bind:value={newUser.role} class="pgs-input mb-2">
            {#each userTypes as type}
              <option value={type}>{type}</option>
            {/each}
          </select>

          <label for="active" class="flex items-center gap-2">
            <input
              id="active"
              type="checkbox"
              class="checkbox"
              bind:checked={newUser.active}
            />
            Active
          </label>
        </fieldset>

        <div class="mt-6 flex justify-end space-x-3">          
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
