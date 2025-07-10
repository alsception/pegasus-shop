<script lang="ts">
  import { link } from "svelte-spa-router";
  import { navRoutes } from "./navRoutes";

  // Function to get navigation items (for menu display)
  function getNavigationItems() {
    return Object.values(navRoutes).map((route) => ({
      label: route.label,
      icon: route.icon,
      href: route.href,
      inProgress: route.inProgress,
      tags: route.tags,
      disabled: route.disabled,
      hidden: !!route.hidden,
    }));
  }

  //This object is used for displaying menu
  const navItems = getNavigationItems();
</script>

<ul
  class="menu menu-sm dropdown-content /*bg-base-100 dark:bg-slate-800*/ rounded-box mt-3 w-52 p-2 shadow scale-in-ver-top" style="background-color: var(--color-base-100);"
>
  {#each navItems as item}
    <li>
      {#if !item.disabled}
        <a
          use:link
          href={item.disabled ? "#" : item.href}
          class="flex items-center px-3 py-2 rounded-md transition-colors cursor-pointer
                  hover:bg-[#0f172a] hover:text-blue-400 
                 text-base"
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="dark:text-gray-400">{item.label}</span>
        </a>
      {:else}
        <div
          class="flex items-center px-3 py-2 rounded-md transition-colors pointer-events-none 
              hover:text-gray-400 dark:hover:text-gray-500 
              text-base"
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="text-gray-400 dark:text-gray-400 ">{item.label}</span>
        </div>
      {/if}
    </li>
  {/each}
</ul>
