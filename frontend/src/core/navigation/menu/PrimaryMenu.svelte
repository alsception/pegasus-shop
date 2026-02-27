<script lang="ts">
  import { link } from "svelte-spa-router";
  import { navRoutes } from "./navRoutes";
  import { getCurrentUsername } from "../../services/SessionStore";
  import { getCurrentRole } from "../../services/SessionStore";
  import type { NavRoutesMap } from "./MenuTypes";

  const role = getCurrentRole();  

  //Filter nav items by role
  const getRoutesByRole = (role: 'ADMIN' | 'CUSTOMER') => {
    return Object.entries(navRoutes)
      .filter(([_, route]) => {
        if(route.default) return true;
        if (role === 'ADMIN') return route.admin === true;
        if (role === 'CUSTOMER') return route.customer === true;
        return false;
      })
      .reduce((acc, [path, route]) => {
        acc[path] = route;
        return acc;
      }, {} as NavRoutesMap);
  };

  const filteredRoutes = getRoutesByRole(role);

  // Function to get navigation items (for menu display)
  function getNavigationItems() {
    return Object.values(filteredRoutes).map((route) => ({
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

<!-- TODO BUG TO FIX: dark:bgslate does not work if theme is switched from theme swithcer but work from browsers -->
<!--    position: absolute;
        top: 270px;
        left: 120px; -->
<ul
  class="menu menu-md dropdown-content  w-52 p-2 
         bg-base-200/70 dark:bg-zinc-950/84 border-2 border-zinc-500/50 dark:border-zinc-500/20 
         rounded shadow  fixed z-[9999] pointer-events-auto

"
  style="min-width: 280px;
        position:fixed; top: 64px; left:1px;
        backdrop-filter: blur(12px) !important; 
        -webkit-backdrop-filter: blur(16px) !important;
     
  
"
>
  <li class="flex  px-3 py-2 rounded-md">
    <div class="inline-flex gap-1">
      <a
        class="text-primary pgs-hyperlink inline-flex items-center max-w-[100px] whitespace-nowrap overflow-hidden text-ellipsis"
        href="#/users/my-account"
        title={getCurrentUsername()}
      >
        <i class="fas fa-user mr-1"></i>
        <span class="">{getCurrentUsername()}</span>
      </a>
      |
      <span class="badge badge-accent badge-outline badge-xs">
        {getCurrentRole()}
      </span>
    </div>
  </li>
  <li class=""></li>

  {#each navItems as item}
    <li class="w-full">
      {#if !item.disabled}
        <a
          use:link
          href={item.disabled ? "#" : item.href}
          class="flex items-center px-3 py-2 rounded-md cursor-pointer
                  hover:bg-primary/15 text-primary/70 text-sm"
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="text-primary/80" style="width: 150px;">{item.label}</span>
        </a>
      {:else}
        <div
          class="flex items-center px-3 py-2 rounded-md pointer-events-none
              hover:text-zinc-400 dark:hover:text-zinc-500
              text-base "
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="text-zinc-400 dark:text-zinc-400">{item.label}</span>
        </div>
      {/if}
    </li>
  {/each}
</ul>
