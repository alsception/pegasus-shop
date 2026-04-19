<script lang="ts">
  import { login } from "../services/client";
  import LoadingOverlay from "../utils/LoadingOverlay.svelte";
  import { push } from "svelte-spa-router";
  import HeaderLite from "../navigation/HeaderLite.svelte";

  document.title = "Log in | Barbacoa Backoffice";

  let username = "";
  let password = "";
  let error = "";
  let loading = false;

  async function handleLogin() 
  {
    try 
    {
      loading = true;
      await login(username, password);
      push("/home");
    } 
    catch (err: any) 
    {
      error = "ERROR: " + err.message;
    } 
    finally 
    {
      loading = false;
    }
  }
</script>

<HeaderLite />

<div class="flex items-center justify-center min-h-screen relative">
  <div
    class=" rounded-lg /*animate-pulse*/ relative z-10 border-0 bg-opacity-100"
    style="box-shadow: none;"
  >
    <div class="">
    </div>

    <form
      on:submit|preventDefault={handleLogin}
      class="w-full max-w-md relative p-6 px-10 pb-7 m-[5px] rounded-2xl bg-base-content/10 bg-gradient-to-br 
                from-gray-50/40 via-base-200/40 to-zinc-100/40 
                dark:from-gray-900/40 dark:via-base-200/40 dark:to-zinc-800/40 border 
                border-base-content/20 backdrop-blur-lg /*border border-primary/14*/"
    >
      {#if loading}
        <LoadingOverlay />
      {/if}

      <label
        for="username"
        class="block text-md font-semibold mb-2 text-zinc-700 dark:text-zinc-300"
      >
        <i class="fas fa-user text-sm mx-2"></i>Username
      </label>
      <input
        id="username"
        type="text"
        bind:value={username}
        class="pgs-input mb-8 outline outline-primary/30"
        disabled={loading}
        name="username" autocomplete="username"
      />

      <label
        for="password"
        class="block text-md font-semibold mb-2 text-zinc-700 dark:text-zinc-300"
      >
        <i class="fas fa-lock text-sm mx-2"></i>Password
      </label>
      <input
        id="password"
        type="password"
        bind:value={password}
        class="pgs-input mb-8 outline outline-primary/30"
        disabled={loading}
        name="password" 
        autocomplete="current-password"
      />

      <button
        type="submit"
        class="btn w-full mt-2 btn-secondary font-semibold text-lg"
        disabled={loading}
      >
        Login
      </button>

      {#if error}
        <p
          class="text-red-500 text-sm mt-8 text-center dark:text-red-400 bg-error-content/70 p-2"
        >
          {error}
        </p>
      {/if}
    </form>
  </div>
</div>