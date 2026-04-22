<script lang="ts">
  import LoadingOverlay from '../utils/LoadingOverlay.svelte';
  import { register } from "../services/client";
  import HeaderLite from '../navigation/HeaderLite.svelte';

  document.title = 'Register | Pegasus'

  let username = "";
  let password = "";
  let error: string | null = null;
  let loading = false;
  let successMsg: string | null = null;

  //TODO: ADD firstname,lastname, phone, email,etc...

  async function handleSubmit() 
  {
    try 
    {
      loading = true;
      let response = await register(username, password);
      successMsg = response;
      error = null;
    } 
    catch (err: any) 
    {
      error = 'ERROR: ' + err.message;
    } finally {
      loading = false;
    }
  }
</script>

<!-- <HeaderLite/>
 -->
<div class="flex items-center justify-center h-[80vh]">
  <form
    on:submit|preventDefault={handleSubmit}
    class="w-full max-w-md relative p-6 px-10 rounded-2xl bg-base-100 border border-primary/10"
  >
    <h2
      class="text-2xl font-semibold mb-8 text-center text-primary dark:text-gray-300"
    >
      Registracija
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
      placeholder=""
      class="pgs-input mb-8 outline outline-primary/30"
      disabled={loading}
    />
    
    <label for="password" class="block text-md font-medium text-secondary mb-2">
      <i class="fas fa-lock text-sm textsecondary mx-2"></i>Password
    </label>
    <input
      type="password"
      bind:value={password}
      placeholder=""
      class="pgs-input mb-8 outline outline-primary/30"
      disabled={loading}
    />
 
    <button
      type="submit"
      class="btn btn-primary w-full mt-2"
      disabled={loading}
    >
      Registriraj se
    </button>

    {#if error}

      <p class="text-red-500 text-sm mt-8 text-center dark:text-red-400 bg-error-content p-2">
        {error}
      </p>

    {/if}

    {#if successMsg}

      <p class="text-green-500 text-sm mt-8 text-center dark:text-green-400 bg-success-content p-2">
        {successMsg}
      </p>

    {/if}

    <div class="mt-8">
    <label for="register" class="label">Već imate račun?</label>
    <a
      href="#/login"
      class="pgs-hyperlink"
    >
       Prijavite se ovdje. 
    </a>
    </div>
  </form>
</div>