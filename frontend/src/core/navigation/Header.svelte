<script>
  import { onMount } from "svelte";
  import PrimaryMenu from "./menu/PrimaryMenu.svelte";
  import NotificationsInfo from "./NotificationsInfo.svelte";

  onMount(() => {
    const themeCheckbox = document.querySelector("input.theme-controller");

    const updateTheme = () => {
      const newTheme = themeCheckbox.checked ? "dark" : "light";
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
</script>

<!-- 
 Should we have this (bg-slate-950/84 backdrop-blur-sm) glassy effect or not?
 Sure it looks nice, but is it necesary/usefull and non blocking?
 We'll leave it, for now...
  -->

<div class="navbar shadow-sm fixed bg-zinc-950/84 backdrop-blur-lg z-9000">
  <div class="navbar-start">
    <div class="pl-16">
      <a href="/#/home">
        <h1
          class="text-md sm:text-md md:text-2xl lg:text-2xl font-bold bg-gradient-to-tr from-slate-400 via-blue-500 to-zinc-600
        text-transparent bg-clip-text font-mono uppercase tracking-widest"
        >
          Pegasus
        </h1>
      </a>
    </div>
  </div>
  <div class="navbar-center">
    
  </div>
<div class="navbar-end">
  <div class="flex items-center gap-4">
    <div class="p-2">
      <label
        class="swap swap-rotate tooltip tooltip-info tooltip-left text-gray-500 cursor-pointer"
        data-tip="Dark Mode"
      >
        <input
          id="theme-controller"
          type="checkbox"
          class="theme-controller"
          value="dark"
        />
        <span class="swap-off text-xl">
          <i class="fas fa-sun text-md"></i>
        </span>
        <span class="swap-on text-xl">
          <i class="fas fa-moon text-md"></i>
        </span>
      </label>
    </div>
    
    <div class="">
      <div
        class="tooltip tooltip-info tooltip-left text-gray-500 cursor-pointer"
        data-tip="Poruke"
      >
        <span class="text-xl">
          <i class="fas fa-envelope text-md"></i>
        </span>
      </div>
    </div>
    
    <div class="dropdown">
      <div
        tabindex="0"
        role="button"
        class="btn btn-ghost btn-circle hover:bg-neutral-900 text-gray-500 text-xl tooltip tooltip-info tooltip-left"
        data-tip="Notifications"
        aria-label="Notifications"
      >
        <i class="fas fa-bell text-md"></i>
      </div>

      <NotificationsInfo />
    </div>
  </div>
</div>

</div>

<div class="dropdown"
        style="    
              z-index: 9999;
              position: fixed;
              top: 12px;
              left: 8px;"
>
      <div
        tabindex="0"
        role="button"
        class="btn btn-ghost btn-circle text-gray-500 hover:bg-neutral-900"
      >
        <i class="fas fa-bars text-xl"></i>
        <PrimaryMenu />
      </div>
    </div>
