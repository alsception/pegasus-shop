<script lang="ts">
  import { link } from "svelte-spa-router";
  import { navRoutes } from "./navRoutes";
  import { getCurrentUsername } from "../../services/SessionStore";
  import { getCurrentRole } from "../../services/SessionStore";

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

  //CSS: here we need shadow
</script>

<ul
  class="menu menu-sm dropdown-content shadow rounded-box mt-3 w-52 p-2 scale-in-ver-top bg-base-100"
>
  <li class="flex items-center px-3 py-2 rounded-md">
    <div class="inline-flex items-center gap-1">
      <a
        class="pgs-hyperlink inline-flex items-center max-w-[100px] whitespace-nowrap overflow-hidden text-ellipsis"
        href="#/my-account"
        title={getCurrentUsername()}
      >
        <i class="fas fa-user mr-1"></i>
        <span>{getCurrentUsername()}</span>
      </a>
      |
      <span class="badge badge-accent badge-outline badge-xs">
        {getCurrentRole()}
      </span>
    </div>
  </li>
  <li class=""></li>

  {#each navItems as item}
    <li>
      {#if !item.disabled}
        <a
          use:link
          href={item.disabled ? "#" : item.href}
          class="flex items-center px-3 py-2 rounded-md cursor-pointer
                  hover:bg-[#0f172a] hover:text-blue-400
                 text-primary text-sm"
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="dark:text-gray-400">{item.label}</span>
        </a>
      {:else}
        <div
          class="flex items-center px-3 py-2 rounded-md pointer-events-none
              hover:text-gray-400 dark:hover:text-gray-500
              text-base"
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="text-gray-400 dark:text-gray-400">{item.label}</span>
        </div>
      {/if}
    </li>
  {/each}
</ul>
