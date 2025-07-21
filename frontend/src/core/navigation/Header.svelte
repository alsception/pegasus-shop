<script>
  import { onMount } from "svelte";
  import PrimaryMenu from "./menu/PrimaryMenu.svelte";

  onMount(() => {
    const themeCheckbox = document.querySelector('input.theme-controller');

    const updateTheme = () => {
      const newTheme = themeCheckbox.checked ? "dark" : "light";
      document.documentElement.setAttribute("data-theme", newTheme);
      document.documentElement.classList.toggle("dark", newTheme === "dark");
      console.log("Theme switched to:", newTheme);
    };

    if (themeCheckbox) {
      const currentTheme = document.documentElement.getAttribute("data-theme") || "light";
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
<div class="navbar shadow-sm fixed bg-slate-950/84 backdrop-blur-sm z-50">
    <div class="navbar-start">
    <div class="dropdown">
      <div
        tabindex="0"
        role="button"
        class="btn btn-neutral btn-circle hover:bg-neutral-700 tooltip tooltip-right"  
        data-tip="Open Menu"
        aria-label="Open Menu"        
      >
        <i class="fas fa-bars text-xl"></i>
      </div>

      <PrimaryMenu />
    </div>
  </div>
  <div class="navbar-center">
    <a href="/#/home">
      <h1 class="text-xl sm:text-2xl md:text-3xl lg:text-4xl font-bold bg-gradient-to-r from-sky-400 via-blue-500 to-violet-600 text-transparent bg-clip-text font-mono uppercase tracking-widest yellowtail-regular">
        Pegasus
      </h1>
    </a>
  </div>
  <div class="navbar-end"> 
      <div class="" style="/*display: none;*/"> 
        <label class="swap swap-rotate mr-4 tooltip tooltip-left" data-tip="Toggle Dark Mode">
          <input id="theme-controller" type="checkbox" class="theme-controller" value="dark" />
          <span class="swap-off text-xl">☀️</span>
          <span class="swap-on text-xl">🌚</span>
        </label>
    </div>

<!-- HTML deo --><!-- 
<label class="flex items-center gap-2 cursor-pointer">
  <span class="text-sm">Dark mode</span>
  <input
    id="theme-controller"
    type="checkbox"
    class="toggle theme-controller"
    value="dark"
  />
</label> -->
  </div>
</div>