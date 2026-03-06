<script lang="ts">
  import { login } from "../services/client";
  import LoadingOverlay from "../utils/LoadingOverlay.svelte";
  import { push } from "svelte-spa-router";
  import HeaderLite from "../navigation/HeaderLite.svelte";
  import { type UnsplashPhoto } from "../../features/pix/UnsplashPhoto";

  document.title = "Log in | Pegasus";

  let username = "";
  let password = "";
  let error = "";
  let loading = false;
  let photo: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  //TODO: ovde cemo staviti razne slike da se ucitavaju i menjaju
  photo.urls.regular =
    //"https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHw0fHxiYXJiYWN1ZXxlbnwwfHx8fDE3NzI3MTUwMTh8MA&ixlib=rb-4.1.0&q=85"
  //  "https://images.unsplash.com/photo-1606131731446-5568d87113aa?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwyfHxidXJnZXJzfGVufDB8fHx8MTc3MjcxNDA0NHww&ixlib=rb-4.1.0&q=85";
  "https://images.unsplash.com/photo-1607013251379-e6eecfffe234?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHw1fHxidXJnZXJzfGVufDB8fHx8MTc3MjcxNDA0NHww&ixlib=rb-4.1.0&q=85"

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
    } finally 
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
      <img
        class="w-full object-cover bg"
        src={photo.urls.regular}
        alt={photo.alt_description}
      />
    </div>

    <form
      on:submit|preventDefault={handleLogin}
      class="w-full max-w-md relative p-6 px-10 m-[5px] rounded-2xl bg-base-200 border-[#2A2A2A] bg-zinc-50/24 backdrop-blur-lg border border-primary/14"
    >
      {#if loading}
        <LoadingOverlay />
      {/if}

      <label for="username" class="block text-md font-semibold mb-2 text-primary">
        <i class="fas fa-user text-sm mx-2"></i>Username
      </label>
      <input
        type="text"
        bind:value={username}
        class="pgs-input mb-8 outline outline-primary/30"
        disabled={loading}
      />

      <label for="password" class="block text-md font-semibold mb-2 text-primary">
        <i class="fas fa-lock text-sm  mx-2"></i>Password
      </label>
      <input
        type="password"
        bind:value={password}
        class="pgs-input mb-8 outline outline-primary/30"
        disabled={loading}
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

      <div class="mt-8 hidden">
        <label for="register" class="label">Don't have account?</label>
        <a href="#/register" class="pgs-hyperlink"> Click here to register </a>
      </div>
    </form>
  </div>
</div>

<style>
  .bg {
    position: fixed;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    z-index: -1;
  }
</style>