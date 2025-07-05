<script lang="ts">
  //Svelte imports
  import { get } from "svelte/store";
  import { onMount } from "svelte";
  import Router from "svelte-spa-router";

  //Our imports - core
  import { auth } from "./core/services/store";
  import Login from "./core/auth/login.svelte";

  import { SvelteToast } from "@zerodevx/svelte-toast";
  import { generateRoutes } from "./core/navigation/routes";
  import Header from "./core/navigation/Header.svelte";

  //Definitions
  let loading: boolean = true;
  let isAuthenticated = false;
  let isDark = false;
  let isSideBarOpen = false;
  let showBackground = true;

  //Unsplash params
  let backgroundUrl = ""
  const bg2 = '';
  let backgrounds: { urls: { full: string } }[];
  let imgIndex = 0;
  let imgQueryDay = "sky";
  let imgQueryNight = "neon-night";
  let imgQuery = imgQueryDay;

  //Authenticacion
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  /**routing end**********************************************/

  document.addEventListener("keydown", (event) => {
    if (event.ctrlKey && event.key === "k") {
      const el = document.getElementById("background-info");
      el?.classList.toggle("hidden");
      event.preventDefault(); // optional: prevent default browser behavior
    }
  });

  function processDayTime() {
    const now = new Date();
    if (isNightTime(now)) {
      toggleTheme();
    }
  }

  function isNightTime(date = new Date()) {
    const hours = date.getHours();
    return hours >= 19 || hours < 6;
  }

  onMount(async () => 
  {
    try 
    {
      const { isAuthenticated: authStatus } = get(auth);
      isAuthenticated = authStatus;

      /*processDayTime()*/

    } catch (err) {
       console.log(err);
    } 
  });

  function toggleTheme() {
    //TODO: FIX, we dont need double variable.
    isDark = !isDark;
    document.body.classList.toggle("dark", isDark);
  }

  $: themeEmoji = isDark ? "🌙" : "☀️";

  function extractUsernameFromToken(token: string | null) {
    // Split the token into parts
    if (token !== null) {
      const parts = token.split(".");

      if (parts.length !== 3) {
        throw new Error("Invalid JWT token format");
      }

      // Get the payload (middle part)
      const payload = parts[1];

      // Base64 decode the payload
      // Note: Need to add padding and handle URL-safe base64
      const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split("")
          .map(function (c) {
            return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
          })
          .join("")
      );

      // Parse the JSON
      const data = JSON.parse(jsonPayload);

      // Look for common username fields
      return data.username || data.sub || data.email || null;
    }
    return "";
  }

  const routes = generateRoutes();

</script>

{#if !$auth.isAuthenticated}

  <Login />

{:else}

  <Header/>

  <main class="flex-1 overflow-auto main-content p-6 w-full mt-24">
    <!-- Router to render dynamic pages -->
    <Router {routes} />
  </main>

{/if}

<SvelteToast />