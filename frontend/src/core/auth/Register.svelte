<script lang="ts">
  import LoadingOverlay from '../utils/LoadingOverlay.svelte';
  import { register } from "../services/client";

  document.title = 'Register | Pegasus'

  let username = "";
  let password = "";
  let error = "";
  let loading = false;

  //TODO: ADD firstname,lastname, phone, email,etc...

  async function handleSubmit() 
  {
    try 
    {
      loading = true;
      await register(username, password);
      window.location.href = "/#/login";
    } catch (err: any) {
      error = err.message;
    } finally {
      loading = false;
    }
  }
</script>

<div class="flex items-center justify-center min-h-screen">
  <form
    on:submit|preventDefault={handleSubmit}
    class="w-full max-w-sm relative p-6 rounded-2xl bg-white dark:bg-slate-900"
  >
    <h2
      class="text-2xl font-semibold mb-8 text-center text-primary dark:text-gray-400"
    >
      Pegasus: Registration
    </h2>

    {#if loading}

      <LoadingOverlay/>

    {/if}

    <input
      type="text"
      bind:value={username}
      placeholder="Username"
      class="w-full input px-4 py-2 mb-4
             rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
      disabled={loading}
    />

    <input
      type="password"
      bind:value={password}
      placeholder="Password"
      class="w-full input mb-4
             rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
      disabled={loading}
    />
 
    <button
      type="submit"
      class="btn btn-primary w-full mt-2"
      disabled={loading}
    >
      Register
    </button>

    {#if error}

      <p class="text-red-500 text-sm mt-4 text-center dark:text-red-400 bg-error-content p-2">
        {error}
      </p>

    {/if}

    <div class="mt-4">
    <label for="register" class="label">Already have an account? </label>
    <a
      href="#/login"
      class="pgs-hyperlink hover:bg-secondary"
    >
      Click here to login
    </a>
    </div>
  </form>
</div>