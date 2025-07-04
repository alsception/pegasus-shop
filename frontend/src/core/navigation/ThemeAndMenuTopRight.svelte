<script>
  import SecondaryMenu from "./SecondaryMenu.svelte";
  export let isDark;
</script>
        
<div class="flex items-center gap-4">
    <button
      id="themeToggle"
      class="text-gray-600 dark:text-gray-300 hover:text-black dark:hover:text-white text-sm px-3 py-1 rounded flex items-center gap-2 bg-slate-800 hover:bg-slate-600 cursor-pointer"
      aria-label="Darkmode"
      title="Darkmode"
    >        
      {#if isDark}
        🌚
      {:else}
        🌞
      {/if}        
    </button>
    <!-- Dropdown -->
     <SecondaryMenu/> 
  </div>