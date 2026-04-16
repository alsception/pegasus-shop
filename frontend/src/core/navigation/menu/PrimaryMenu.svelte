<script lang="ts">
  import { link } from "svelte-spa-router";
  import { navRoutes } from "./navRoutes";
  import { getCurrentUsername } from "../../services/SessionStore";
  import { getCurrentRole } from "../../services/SessionStore";
  import type { NavRoutesMap } from "./MenuTypes";
  import { cartTotalCounter } from "../../services/CheckoutStore";
  import { formatPrice } from "../../../utils/formatting";

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
  let navItems = getNavigationItems();

  // Čim se promeni broj u Košarici, ažuriraj navigaciju
  // TODO: Ovde bi zapravo trebalo da se prikaže kolko kešovine a ne kolko proizvoda
  $: {
    const total = $cartTotalCounter;
    const price = formatPrice(total);

    navItems = navItems.map(n => 
      n.href === '#/cart' 
        ? { ...n, label: total > 0 ? `<b>Košarica (<span class="text-info">${price}</span>)</b>` : 'Košarica' } 
        : n
    );
  }

  // Funkcija koja zatvara meni micanjem fokusa s trenutno aktivnog elementa, gazda tako trazio
  const closeMenu = () => {
    if (typeof document !== 'undefined') {
      (document.activeElement as HTMLElement)?.blur();
    }
  };
</script>

<ul
  class="menu menu-md dropdown-content  w-52 p-2 
         bg-base-200/70 dark:bg-zinc-950/84 border-1 border-zinc-500/50 dark:border-zinc-500/20 
         rounded shadow  fixed z-[9999] pointer-events-auto"         
  style="min-width: 280px;
        position:fixed; top: 48px; left:1px;
        backdrop-filter: blur(12px) !important; 
        -webkit-backdrop-filter: blur(16px) !important;"        
>
  <li class="flex  px-3 py-2 rounded-md">
    <div class="inline-flex gap-1">
      <a
        class="text-primary pgs-hyperlink inline-flex items-center max-w-[100px] whitespace-nowrap overflow-hidden text-ellipsis"
        href="#/users/my-account"
        on:click={closeMenu}
        title={getCurrentUsername()}
      >
        <i class="fas fa-user mr-1"></i>
        <span class="">{getCurrentUsername()}</span>
      </a>
      
      <span class="badge badge-info badge-outline badge-xs">
        { role !== 'CUSTOMER' ? role : '' }
      </span>
    </div>
  </li>
  <li class=""></li>

  {#each navItems as item}
    <li class="w-full">
      {#if !item.disabled}
        {#if item.href === "#/logout"}
          <a
            use:link
            href={item.disabled ? "#" : item.href}
            class="flex items-center px-3 py-2 rounded-md cursor-pointer
                    hover:bg-primary/15 text-error text-sm"
                    on:click={closeMenu}
          >
            <i class="fas fa-{item.icon} w-5 mr-2"></i>
            <span class="text-error" style="width: 150px;">{@html item.label}</span>
          </a>
        {:else}
          <a
            use:link
            href={item.disabled ? "#" : item.href}
            class="flex items-center px-3 py-2 rounded-md cursor-pointer
                    hover:bg-primary/15 text-primary/70 text-sm"
                    on:click={closeMenu}
          >
            <i class="fas fa-{item.icon} w-5 mr-2"></i>
            <span class="text-primary/80" style="width: 150px;">{@html item.label}</span>
          </a>
        {/if}
      {:else}
        <div
          class="flex items-center px-3 py-2 rounded-md pointer-events-none
              hover:text-zinc-400 dark:hover:text-zinc-500
              text-base "
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="text-zinc-400 dark:text-zinc-400">{@html item.label}</span>
        </div>
      {/if}
    </li>
  {/each}
</ul>