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

  onMount(async () => {
    try {
      prepareUI();
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

  function nextBackground() {
    if (backgrounds) backgroundUrl = backgrounds[imgIndex++].urls.full;
  }

  function loadBackgrounds() {
    if (showBackground == true) {
      loading = true;
      const token = $auth.token;
      try {
        /* todo: this should not be in code
        alternative &client_id=... */
        fetch(`https://api.unsplash.com/search/photos?query=` + imgQuery, {
          method: "GET",
          headers: 
          {
            Authorization: `Client-ID ${import.meta.env.VITE_UNSPLASH_CLIENT_ID}`,
            "Content-Type": "application/json",
          },
        })
          .then((res) => res.json())
          .then((data) => {
            console.log(data);
            backgrounds = data.results;
          })
          .catch((err) => {
            console.error("Error:", err);
          })
          .finally(() => {
            loading = false; 
          });
      } catch (err) {
      } finally {
        loading = false;
      }
    }
  }

  const routes = generateRoutes();

  ////////////////////////////////////////////NEW LOOK START
  // Default states
  let darkMode = false;

  function prepareUI() {
    const html = document.documentElement;

    const sidebar = document.getElementById("sidebar")!;
    const overlay = document.getElementById("overlay")!;
    const themeIcon = document.getElementById("themeIcon")!;
    const welcomeCard = document.getElementById("welcomeCard")!;
    const registrationForm = document.getElementById("registrationForm")!;
    const showFormLink = document.getElementById("showFormLink")!;
    const closeForm = document.getElementById("closeForm")!;
    const togglePassword = document.getElementById("togglePassword")!;
    const passwordInput = document.getElementById(
      "password"
    )! as HTMLInputElement;
    const dropdownBtn = document.getElementById("dropdownBtn")!;
    const dropdownMenu = document.getElementById("dropdownMenu")!;
    const form = document.querySelector("form")!;

    const isMobile = window.innerWidth < 640;
    if (isMobile) {
      isSideBarOpen = false;
      sidebar.classList.add("-translate-x-full");
    }

    function updateTheme() {
      html.classList.toggle("dark", darkMode);
      //themeIcon.className = darkMode ? "fas fa-moon" : "fas fa-sun";do we actually use this?
      //do we want body or html?

      isDark = !isDark;
      document.body.classList.toggle("dark", isDark);
    }

    function updateSidebar() {
      if (isSideBarOpen) {
        sidebar.classList.remove("-translate-x-full");
        overlay.classList.remove("hidden");
      } else {
        sidebar.classList.add("-translate-x-full");
        overlay.classList.add("hidden");
      }
    }

    function showRegistration() {
      welcomeCard.classList.add("hidden");
      registrationForm.classList.remove("hidden");

      if (window.innerWidth < 640) {
        isSideBarOpen = false;
        updateSidebar();
      }
    }

    function hideRegistration() {
      welcomeCard.classList.remove("hidden");
      registrationForm.classList.add("hidden");
    }

    document.getElementById("themeToggle")!.addEventListener("click", () => {
      darkMode = !darkMode;
      updateTheme();
    });

    document.getElementById("sidebarToggle")!.addEventListener("click", () => {
      isSideBarOpen = !isSideBarOpen;
      updateSidebar();
    });

    if (showFormLink)
      showFormLink.addEventListener("click", (e) => {
        e.preventDefault();
        showRegistration();
      });

    if (closeForm)
      closeForm.addEventListener("click", () => {
        hideRegistration();
      });

    if (togglePassword)
      togglePassword.addEventListener("click", () => {
        if (passwordInput.type === "password") {
          passwordInput.type = "text";
          togglePassword.innerHTML = '<i class="fas fa-eye-slash"></i>';
        } else {
          passwordInput.type = "password";
          togglePassword.innerHTML = '<i class="fas fa-eye"></i>';
        }
      });

    if (form)
      form.addEventListener("submit", (e) => {
        e.preventDefault();

        const formData = {
          firstName: (document.getElementById("fname") as HTMLInputElement)
            .value,
          lastName: (document.getElementById("lname") as HTMLInputElement)
            .value,
          username: (document.getElementById("username") as HTMLInputElement)
            .value,
          password: passwordInput.value,
        };

        console.log("Form submitted:", formData);
        alert("Registration submitted successfully!");
        hideRegistration();
      });

    if (overlay)
      overlay.addEventListener("click", () => {
        isSideBarOpen = false;
        updateSidebar();
      });

    window.addEventListener("resize", () => {
      if (window.innerWidth < 640 && isSideBarOpen) {
        overlay.classList.remove("hidden");
      } else if (window.innerWidth >= 640) {
        overlay.classList.add("hidden");
      }
    });

    dropdownBtn.addEventListener("click", () => {
      dropdownMenu.classList.toggle("hidden");
    });

    window.addEventListener("click", (e) => {
      if (
        !dropdownBtn.contains(e.target as Node) &&
        !dropdownMenu.contains(e.target as Node)
      ) {
        dropdownMenu.classList.add("hidden");
      }
    });
  }

  function toggleSidebar() {
    isSideBarOpen = !isSideBarOpen;

    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("overlay");

    if (sidebar) {
      sidebar.classList.toggle("hidden", !isSideBarOpen);
    }

    if (overlay) {
      overlay.classList.toggle("hidden", isSideBarOpen);
    }
  }
</script>
{#if !$auth.isAuthenticated}
  <Login />
{:else}
  {#if showBackground}
    <div
      class="unsplash-background"
      style={`background-image: url('${backgroundUrl}');`}
    ></div>
  {/if}

  <Header/>

  <main class="flex-1 overflow-auto main-content p-6 w-full mt-24">
    <!-- Router to render dynamic pages -->
    <Router {routes} />
  </main>
{/if}

<SvelteToast />

<style>
  .unsplash-background {
    position: fixed;
    top: 0;
    left: 0;
    z-index: -1;
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-attachment: fixed;
  }
</style>
