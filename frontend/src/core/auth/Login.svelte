<script lang="ts">
  import { login } from "../services/client";
  import LoadingOverlay from "../utils/LoadingOverlay.svelte";
  import LoginAnimation from "./LoginAnimation.svelte";
  import { scale } from "svelte/transition";
  import { push } from "svelte-spa-router";
  import HeaderLite from "../navigation/HeaderLite.svelte";

  document.title = "Log in | Pegasus";

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
      error = 'ERROR: ' + err.message;
    } 
    finally 
    {
      loading = false;
    }
  }
</script>

<HeaderLite/>

<!-- <LoginAnimation/>
 --> 
<div class="flex items-center justify-center min-h-screen relative" >
  <div class=" rounded-lg /*animate-pulse*/ relative z-10 border-0 bg-opacity-100 " style="box-shadow: none;"  transition:scale={{ duration: 700, start: 0.5 }}>


    <form
      on:submit|preventDefault={handleLogin}
      
      class="w-full max-w-md relative p-6 px-10 m-[5px] rounded-2xl bg-[#b7b7b7] bg-opacity-100 border-[#2A2A2A]"
      
      
    >
      {#if loading}
        <LoadingOverlay />
      {/if}

      <label for="username" class="block text-md font-medium text-zinc-900  mb-2">
        <i class="fas fa-user text-sm text-zinc-900  mx-2"></i>Username
      </label>
      <input
        type="text"
        bind:value={username}
        class="pgs-input mb-8"
        disabled={loading}
      />

      <label for="password" class="block text-md font-medium text-zinc-900  mb-2">
        <i class="fas fa-lock text-sm text-zinc-900 mx-2"></i>Password
      </label>
      <input
        type="password"
        bind:value={password}
        class="pgs-input mb-8"
        disabled={loading}
      />

      <button
        type="submit"
        class="btn  w-full mt-2 bg-gray-900"
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

      <div class="mt-8 hidden">
        <label for="register" class="label">Don't have account?</label>
        <a href="#/register" class="pgs-hyperlink"> Click here to register </a>
      </div>
    </form>
  </div>
  
</div>