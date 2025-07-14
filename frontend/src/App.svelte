<script lang="ts">
  //Svelte imports
  import { get } from "svelte/store";
  import { onMount } from "svelte";
  import Router from "svelte-spa-router";

  //Our imports - core
  import { auth } from "./core/services/SessionStore";
  import Login from "./core/auth/Login.svelte";
  import Register from "./core/auth/Register.svelte";
  import Header from "./core/navigation/Header.svelte";
  import { generateRoutes } from "./core/navigation/routing/routes";
  import { SvelteToast } from "@zerodevx/svelte-toast";

  export const unauthenticatedRoutes = {
    '/login': Login,
    '/register': Register,
    '*': Login  // fallback route
  };

  //Definitions
  let isAuthenticated = false;
  let isDark = false;

  //Authenticacion
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  onMount(async () => 
  {
    try 
    {
      const { isAuthenticated: authStatus } = get(auth);
      isAuthenticated = authStatus;
    } catch (err) {
       console.log(err);
    } 
  });

  const routes = generateRoutes();

</script>


{#if !$auth.isAuthenticated}
  <Router routes={unauthenticatedRoutes} />
{:else}
<Header/>
  <main class="flex-1 overflow-auto main-content w-full mt-24 p-0 sm:p-6" style="padding-bottom: 64px !important;">
    <Router {routes} />
  </main>
{/if}

<SvelteToast />