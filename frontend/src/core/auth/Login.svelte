<script lang="ts">
  import { login } from "../services/client";
  import LoadingOverlay from "../utils/LoadingOverlay.svelte";
  import { push } from "svelte-spa-router";
  import HeaderLite from "../navigation/HeaderLite.svelte";
  import { type UnsplashPhoto } from "../../features/pix/UnsplashPhoto";
  import { onMount } from "svelte";
  import { fade } from "svelte/transition";

  document.title = "Log in | Barbacoa";

  let username = "";
  let password = "";
  let error = "";
  let loading = false;

  let photo1: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  let photo2: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  let photo3: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  let photo4: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  let photo5: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  let photo6: UnsplashPhoto = {
    urls: {
      regular: "https://...",
      // ostala polja...
    },
  } as UnsplashPhoto;

  //TODO: ovde cemo staviti razne slike da se ucitavaju i menjaju
  photo1.urls.regular =
    "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHw0fHxiYXJiYWN1ZXxlbnwwfHx8fDE3NzI3MTUwMTh8MA&ixlib=rb-4.1.0&q=85";

  photo2.urls.regular =
    "https://images.unsplash.com/photo-1606131731446-5568d87113aa?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwyfHxidXJnZXJzfGVufDB8fHx8MTc3MjcxNDA0NHww&ixlib=rb-4.1.0&q=85";

  photo3.urls.regular =
    "https://images.unsplash.com/photo-1607013251379-e6eecfffe234?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHw1fHxidXJnZXJzfGVufDB8fHx8MTc3MjcxNDA0NHww&ixlib=rb-4.1.0&q=85";

  photo4.urls.regular = "https://images.unsplash.com/photo-1558030137-a56c1b004fa3?ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwyMHx8c3RlYWt8ZW58MHx8fHwxNzc2MTc3NDQ1fDA&ixlib=rb-4.1.0"
  photo5.urls.regular = "https://images.unsplash.com/photo-1504973960431-1c467e159aa4?ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwxNHx8c3RlYWt8ZW58MHx8fHwxNzc2MTc3NDQ1fDA&ixlib=rb-4.1.0"
  photo6.urls.regular = "https://images.unsplash.com/photo-1558030089-02acba3c214e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHw1fHxzdGVha3xlbnwwfHx8fDE3NzYxNzczOTJ8MA&ixlib=rb-4.1.0&q=80&w=1080"
  

  /*
  //todo:mozda jos ovo:

  https://images.unsplash.com/photo-1614119068601-483274e9dcb7?ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwzMXx8c3RlYWt8ZW58MHx8fHwxNzc2MTc3Nzk5fDA&ixlib=rb-4.1.0
  https://images.unsplash.com/photo-1615937662601-4724eceda00f?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwyN3x8c3RlYWt8ZW58MHx8fHwxNzc2MTc3NzU0fDA&ixlib=rb-4.1.0&q=85
  https://images.unsplash.com/photo-1615937691194-97dbd3f3dc29?crop=entropy&cs=srgb&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwyNnx8c3RlYWt8ZW58MHx8fHwxNzc2MTc3NzU0fDA&ixlib=rb-4.1.0&q=85
  https://images.unsplash.com/photo-1565299524732-d2149c7eabf5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3NDQzNzd8MHwxfHNlYXJjaHwxN3x8c3RlYWt8ZW58MHx8fHwxNzc2MTc4MDYzfDA&ixlib=rb-4.1.0&q=80&w=1080
  */



  const photos = [photo1, photo2, photo3, photo4, photo5, photo6];
  let index = Math.floor(Math.random() * photos.length);

  // Reaktivna deklaracija - ovo je ispravno za Svelte 4
  $: photo = photos[index];//random da nebude uvek ista slicica

  onMount(() => {
    const interval = setInterval(() => {
      // Inkrementacija indexa pokreće reaktivnost
      index = (index + 1) % photos.length;

    }, 1000 * 8);

    return () => clearInterval(interval);
  });

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

<!-- <HeaderLite />
 -->
<div class="flex items-center justify-center h-[80vh] relative">
  <div
    class=" rounded-lg /*animate-pulse*/ relative z-10 border-0 bg-opacity-100"
    style="box-shadow: none;"
  >
    <div class="">
    
      {#key photo}
        <img
          in:fade={{ duration: 800 }}
          class="w-full object-cover bg ken-burns-image"
          src={photo.urls.regular}
          alt={photo.alt_description}
        />
      {/key}
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
        class="block text-md font-semibold mb-2 text-primary"
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
        class="block text-md font-semibold mb-2 text-primary"
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

      <div class="mt-8">
        <label for="register" class="label text-primary/70">Nemate nalog?</label>
        <a href="#/register" class="hover:text-blue-800 dark:hover:text-sky-500 text-primary/90 font-bold">&nbsp; Registrirajte se &nbsp;</a>
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

  /* Definisanje CSS animacije */
  @keyframes kenBurns {
    0% {
      transform: scale(1); /* Početna veličina */
    }
    100% {
      transform: scale(1.15); /* Krajnja veličina (približeno 15%) */
    }
  }

  .ken-burns-image {
    /* Vežemo animaciju: ime, trajanje, način (linear), beskonačno */
    /* Trajanje animacije treba da bude isto ili malo duže od intervala smene slika */
    animation: kenBurns 8s linear infinite;

    /* Osigurava da se zumiranje vrši iz centra */
    transform-origin: center center;
  }
</style>
