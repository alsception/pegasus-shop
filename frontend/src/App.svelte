<script lang="ts">
  //Svelte imports
  import { get } from "svelte/store";
  import { onMount } from "svelte";
  import Router, { push } from "svelte-spa-router";

  //Our imports - core
  import { auth, getCurrentRole } from "./core/services/SessionStore";
  import Login from "./core/auth/Login.svelte";
  import Register from "./core/auth/Register.svelte";
  import Header from "./core/navigation/Header.svelte";
  import { generateRoutes } from "./core/navigation/routing/routes";
  import { SvelteToast } from "@zerodevx/svelte-toast";
  import InfoModal from "./core/utils/ErrorModal.svelte";

  document.title = 'Pegasus'

  export const unauthenticatedRoutes = {
    '/login': Login,
    '/register': Register,
    /*'/products': ProductsListBarbacoa, mozda jednog dana?*/
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
      const params = new URLSearchParams(window.location.search);
      let page = params.get('page');
      if (page === 'completion') 
      {
        // Ovo treba za placanje karticom, taraba problem
        const clientSecret = params.get('payment_intent_client_secret');
        const query = window.location.search; // preserve Stripe params
        window.location.replace(`/#/completion${query}`);
      }
    } 
    catch (err) 
    {
       console.error(err);
    } 
  });

  const routes = generateRoutes();

</script>

{#if !$auth.isAuthenticated}
  <Router routes={unauthenticatedRoutes} />
{:else}
<Header/>
  <main class="flex-1 overflow-auto main-content w-full mt-14 p-0 sm:p-6">
    <Router {routes} />
  </main>
{/if}

<InfoModal />
<SvelteToast />