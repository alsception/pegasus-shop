<script lang="ts">
  import LoadingOverlay from '../utils/LoadingOverlay.svelte';
  import { register } from "../services/client";
  import HeaderLite from '../navigation/HeaderLite.svelte';

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
      error = 'ERROR: ' + err.message;
    } finally {
      loading = false;
    }
  }
</script>

<HeaderLite/>

<div class="flex items-center justify-center min-h-screen" transition:scale={{ duration: 1000, start: 0.0 }}>
  <form
    on:submit|preventDefault={handleSubmit}
    class="w-full max-w-md relative p-6 px-10 rounded-2xl bg-base-100"
  >
    <h2
      class="text-2xl font-semibold mb-8 text-center text-primary dark:text-gray-300"
    >
      Registration
    </h2>

    {#if loading}

      <LoadingOverlay/>

    {/if}

    <label for="username" class="block text-md font-medium text-secondary mb-2">
      <i class="fas fa-user text-sm text-secondary mx-2"></i>Username
    </label>
    <input
      type="text"
      bind:value={username}
      placeholder="Username"
      class="pgs-input mb-8"
      disabled={loading}
    />
    
    <label for="password" class="block text-md font-medium text-secondary mb-2">
      <i class="fas fa-lock text-sm textsecondary mx-2"></i>Password
    </label>
    <input
      type="password"
      bind:value={password}
      placeholder="Password"
      class="pgs-input mb-8"
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

      <p class="text-red-500 text-sm mt-8 text-center dark:text-red-400 bg-error-content p-2">
        {error}
      </p>

    {/if}

    <div class="mt-8">
    <label for="register" class="label">Already have an account? </label>
    <a
      href="#/login"
      class="pgs-hyperlink"
    >
      Click here to login
    </a>
    </div>
  </form>
</div>