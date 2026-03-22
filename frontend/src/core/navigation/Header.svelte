<script lang="ts">
  import { onMount } from "svelte";
  import PrimaryMenu from "./menu/PrimaryMenu.svelte";
  import NotificationsInfo from "./NotificationsInfo.svelte";
  import CartDetails from "../../features/cart/CartDetails.svelte";
  import { fly } from "svelte/transition";

  onMount(() => {
    const themeCheckbox = document.querySelector("input.theme-controller");

    const updateTheme = () => {
      const newTheme = themeCheckbox?.checked ? "dark" : "light";
      document.documentElement.setAttribute("data-theme", newTheme);
      document.documentElement.classList.toggle("dark", newTheme === "dark");
      console.log("Theme switched to:", newTheme);
    };

    if (themeCheckbox) {
      const currentTheme =
        document.documentElement.getAttribute("data-theme") || "light";
      themeCheckbox.checked = currentTheme === "dark";
      updateTheme();
      themeCheckbox.addEventListener("change", updateTheme);
    }
  });

  function resetNotificationsInfo() {
    console.log("reseting..");
    /*  document.getElementById('notifications-icon')?.classList.remove('text-error');
    document.getElementById('notifications-indicator')?.classList.add('hidden');
    document.getElementById('notifications-indicator').textContent = ''; */
  }

  let showModal = false;

  function handleModal(){
    showModal = !showModal
  }

  function openModal() {
    showModal = true;
  }

  function closeModal() {
    showModal = false;
  }

  function handleKeydown(event: { key: string }) {
    if (event.key === "Escape") {
      closeModal();
    }
  }
</script>

<!-- 
 Should we have this (bg-slate-950/84 backdrop-blur-sm) glassy effect or not?
 Sure it looks nice, but is it necesary/usefull and non blocking?
 We'll leave it, for now...
  -->

<div class="navbar shadow-sm fixed bg-[#0d0d0d]/84 bg-zinc-950/84 backdrop-blur-lg z-9000">
  <div class="navbar-start">
    <div class="pl-16">
      <a href="/#/home">
        <!-- <h1
          class="text-md sm:text-md md:text-2xl lg:text-2xl font-bold bg-gradient-to-tr from-slate-400 via-orange-500 to-zinc-600
        text-transparent bg-clip-text font-mono uppercase tracking-widest"
        >
          Barbacoa
        </h1> -->

        <img src="/white_barbacoa.png" alt="Barbacoa logo" title="Barbacoa" style="max-width:149px;">


      </a>
    </div>
  </div>
  <div class="navbar-center"></div>
<div class="navbar-end">
  <div class="flex items-center gap-6">

    <label
      class="swap swap-rotate text-gray-500 cursor-pointer hidden"
      data-tip="Dark Mode"
    >
      <input
        id="theme-controller"
        type="checkbox"
        class="theme-controller"
        value="dark"
      />
      <span class="swap-off text-xl">
        <i class="fas fa-sun text-sm md:text-lg"></i>
      </span>
      <span class="swap-on text-xl">
        <i class="fas fa-moon text-sm md:text-lg"></i>
      </span>
    </label>

    <div
      class="text-gray-500 cursor-pointer hidden"
      data-tip="Poruke"
    >
      <span class="text-xl">
        <i class="fas fa-envelope text-sm md:text-lg"></i>
      </span>
    </div>

    <div class="dropdown dropdown-hover dropdown-bottom dropdown-left hidden">
      <div tabindex="0" role="button" class="btn m-1">Kosarica</div>
      <ul tabindex="-1" class="dropdown-content menu bg-base-100 rounded-box z-1 w-52 p-2 shadow-sm">
            <li> 
              <div class="max-h-[90vh] w-11/12 max-w-5xl p-0 flex flex-col bg-base-100 bg-transparent">        
                <!-- TODO: OVDE CEMO STAVITI CARTLITE -->
                <CartDetails />
       </div>
            </li>
          </ul>
        </div>

    <button
      tabindex="0"
      class="btn btn-ghost p-0 hover:bg-primary/10 text-gray-500 text-xl"

      data-tip="Cart"
      aria-label="Cart"
      on:click={handleModal}
    >
      <i class="fas fa-shopping-basket text-sm md:text-lg"></i>
    </button>

    <div class="dropdown">
      <button
        tabindex="0"
        class="btn btn-ghost p-0 hover:bg-primary/10 text-gray-500 text-xl"
        data-tip="Notifications"
        aria-label="Notifications"
        on:click={() => resetNotificationsInfo()}
      >
        <i id="notifications-icon" class="fas fa-bell text-sm md:text-lg"></i>
        <span
          id="notifications-indicator"
          class="text-sm text-zinc-50 bg-error px-1 hidden"
          style="position: relative;top: -0.5rem;left: -0.5rem;">12</span
        >
      </button>

      <NotificationsInfo />
    </div>

  </div>
</div>
</div>

<div
  class="dropdown"
  style="    
            z-index: 9999;
            position: fixed;
            top: 0.8rem;
            left: 8px;"
>
  <div
    tabindex="0"
    role="button"
    class="btn btn-ghost btn-circle text-gray-500 hover:bg-primary/10"
  >
    <i class="fas fa-bars text-xl"></i>
  </div>
  <PrimaryMenu />
</div>

{#if showModal}
  <div class="modal modal-open pt-10" style="backdrop-filter: blur(10px);">
    <div
      class="modal-box max-h-[90vh] w-11/12 max-w-5xl p-0 flex flex-col bg-base-100 bg-transparent"
      transition:fly={{ y: 50, duration: 300 }}
    >
      <!-- Fixed Header -->
      <div
        class="sticky top-0 z-10 px-6 py-4 border-b border-base-300"
      >
        <!-- <h3 class="font-bold text-lg">Košarica</h3> -->
      </div>

      <!-- Scrollable Content -->
      <div class="overflow-y-auto flex-1 px-3 py-2">
        <CartDetails />
      </div>

      <!-- Fixed Footer -->
      <div
        class="sticky bottom-0 bg-base-100 z-10 px-6 py-4 border-t border-base-300 hidden"
      >
        <div class="flex justify-end gap-2">
          <button class="btn btn-secondary" on:click={closeModal}
            >Zatvori</button
          >
        </div>
      </div>
    </div>

    <!-- Glass Backdrop -->
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="modal-backdrop" on:click={closeModal}></div>
  </div>
{/if}

<style>
  .navbar {
    height: 4rem;
    min-height: 3rem;
  }
</style>
