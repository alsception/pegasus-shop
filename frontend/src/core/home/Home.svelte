<script lang="ts">
  import { onMount } from "svelte";
  import { auth, getCurrentRole } from "../services/SessionStore";
  import InfoBlocks from "./InfoBlocks.svelte";
  import Login from "../auth/Login.svelte";
  import OrdersList from "../../features/orders/OrdersList.svelte";
  import ProductsListBarbacoa from "../../features/products/ProductsListBarbacoa.svelte";

  document.title = "Pegasus";

  type Item = {
    title: string;
    description: string;
    icon: string;
    emoji: string;
    href: string;
    color: string;
    admin: boolean;
    customer: boolean;
    default: boolean;
    id: number;
  };

  const allItems: Item[] = [
    {
      id: 1,
      emoji: "📔",
      title: "Jelovnik",
      description: "Pregledaj proizvode",
      icon: "box--",
      href: "/products",
      color: "green",
      default: true,
      admin: true,
      customer: true,
    },
    {
      id: 6,
      emoji: "🪑",
      title: "Stolovi",
      description: "Pregled i upravljanje stolovima",
      icon: "--table",
      href: "/tables",
      color: "yellow",
      default: false,
      admin: false,
      customer: false,
    },
    {
      id: 4,
      emoji: "📝",
      title: "Narudžbe",
      description: "Pregled i upravljanje narudžbama",
      icon: "truck--",
      href: "/orders",
      color: "red",
      default: false,
      admin: true,
      customer: true,
    },
    {
      id: 3,
      emoji: "🧺",
      title: "Košarica",
      description: "Pregledaj proizvode u košarici",
      icon: "", //"shopping-basket",
      href: "/cart",
      color: "orange",
      default: false,
      admin: true,
      customer: true,
    },
    {
      id: 2,
      emoji: "📦",
      title: "Artikli",
      description: "Upravljanje artiklima",
      icon: "box--",
      href: "/artikli",
      color: "yellow",
      default: false,
      admin: true,
      customer: false,
    },
    {
      id: 0,
      emoji: "👤",
      title: "Korisnici",
      description: "Upravljanje korisnicima",
      icon: "users--",
      href: "/users",
      color: "blue",
      default: false,
      admin: true,
      customer: false,
    },
    {
      id: 8,
      emoji: "📦",
      title: "Upr. proizv.",
      description: "Upravljanje proizvodima",
      icon: "users--",
      href: "/products-mngmt",
      color: "blue",
      default: false,
      admin: true,
      customer: false,
    },
    {
      id: 5,
      emoji: "📅",
      title: "Rezervacije",
      description: "Upravljanje rezervacijama",
      icon: "truck--",
      href: "/reservations",
      color: "red",
      default: false,
      admin: false,
      customer: false,
    },
    {
      id: 6,
      emoji: "📈",
      title: "Statistike",
      description: "",
      icon: "",
      href: "/stats",
      color: "",
      default: false,
      admin: true,
      customer: false,
    },
    {
      id: 7,
      emoji: "🖼️",
      title: "Galerija",
      description:
        "Pregled i upravljanje galerijom slika. Milijarde besplatnih slika putem Unsplash API",
      icon: "//image",
      href: "/pix",
      color: "violet",
      default: false,
      admin: false,
      customer: false,
    },
  ];

  const role = getCurrentRole();

  const getItemsByRole = (role: "ADMIN" | "CUSTOMER" | "EMPLOYEE"): Item[] => {
    return allItems.filter((route) => {
      if (route.default) return true;

      if (role === "ADMIN" || role === "EMPLOYEE") return route.admin === true;
      if (role === "CUSTOMER") return route.customer === true;

      return false;
    });
  };

  let items = getItemsByRole(role);

  async function addItemsWithDelay(
    sourceItems: Item[],
    addFn: (item: Item) => void,
    delay = 0 //za sad bez ovoga
  ) {
    for (const item of sourceItems) {
      addFn(item);
      await new Promise((resolve) => setTimeout(resolve, delay));
    }
  }
  let displayedItems: Item[] = [];

  onMount(() => {
    addItemsWithDelay(
      items,
      (item) => (displayedItems = [...displayedItems, item])
    );
  });
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else if getCurrentRole() === "ADMIN"}
  <InfoBlocks />

  <div
    class="pt-8 sm:pt-0 mx-0 px-0 sm:gap-0
    grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-7 menu-container"
  >
    {#each displayedItems as item}
      <a
        href="#{item.href}"
        class="m-2 sm:m-1 shadow-sm hover:shadow-xl max-h-[170px]"
      >
        <div
          class="menu-card card-8 flex-col bg-base-100 dark:bg-[linear-gradient(135deg,_#1d1d1d_0%,_#0d0d0d_100%)]"
        >
          <div
            class="card-content hover:bg-info/20 dark:hover:bg-primary/10 flex flex-col items-center justify-center text-primary/70"
          >
            <span class="icon">{item.emoji}</span>
            <h2 class="title">{item.title}</h2>
          </div>
          <span class="arrow">→</span>
        </div>
      </a>
    {/each}
  </div>

  <div class="mt-6 mx-2">
    <OrdersList liteView={true} />
  </div>

{:else if getCurrentRole() === "CUSTOMER"}

  <div class="mt-0 mx-0">
    <ProductsListBarbacoa />
  </div>
  
{/if}

<style>
  @keyframes wave {
    0%,
    100% {
      transform: translateX(0);
    }
    50% {
      transform: translateX(-50px);
    }
  }
  @keyframes slide {
    0% {
      transform: translateX(-20px) translateY(-20px);
    }
    100% {
      transform: translateX(20px) translateY(20px);
    }
  }

  @keyframes float {
    0%,
    100% {
      transform: translate(0, 0) scale(1);
    }
    33% {
      transform: translate(30px, -20px) scale(1.1);
    }
    66% {
      transform: translate(-20px, 30px) scale(0.9);
    }
  }
  /*********************************/
  /* ----------------------------------------------
* Generated by Gradienty on 2025-06-02 16:00
* animation scale-up-center-normal
* ----------------------------------------
*/ /* 
  @keyframes scale-up-center-normal {
    0% {
      transform: scale(0.5);
    }
    100% {
      transform: scale(1);
    }
  } */

  .menu-container {
    display: grid;
    grid-template-columns: revert-layer !important;
    /*max-width: 1200px;*/
    width: 100%;
  }

  .menu-card {
    /* background: var(--color-base-100); */
    min-width: none;
    position: relative;
    overflow: hidden;
    cursor: pointer;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    height: -webkit-fill-available;
    align-content: center;
  }

  .menu-card::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0;
  }

  .menu-card:hover::before {
    opacity: 1;
  }

  .card-content {
    position: relative;
    z-index: 1;
  }

  .menu-card:hover .card-content {
    color: var(--color-primary);
  }

  .icon {
    font-size: 48px;
    margin-bottom: 20px;
  }

  .title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 10px;
    letter-spacing: -0.5px;
  }

  .arrow {
    position: absolute;
    bottom: 20px;
    right: 20px;
    font-size: 24px;
    opacity: 0;
    transform: translateX(-10px);
    transition: all 0.4s ease;
  }

  .menu-card:hover .arrow {
    opacity: 1;
    transform: translateX(0);
  }

  @media (max-width: 768px) {
    .menu-container {
      grid-template-columns: 1fr;
    }
  }
</style>
