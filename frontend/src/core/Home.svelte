<script lang="ts">
  import { onMount } from "svelte";
  import { getCurrentRole } from "./services/SessionStore";

  type Item = {
    title: string;
    description: string;
    icon: string;
    href: string;
    color: string;
    admin: boolean;
    customer: boolean;
    default: boolean;
  };

  const allItems: Item[] = [
    {
      title: "Users",
      description: "Manage and view users",
      icon: "users",
      href: "/users",
      color: "blue",
      default: false,
      admin: true,
      customer: false,
    },
    {
      title: "Shop",
      description: "Browse products",
      icon: "box",
      href: "/products?listView=false",
      color: "green",
      default: true,
      admin: true,
      customer: true,
    },
    {
      title: "Products administration",
      description: "Manage store items",
      icon: "box",
      href: "/products?listView=true",
      color: "yellow",
      default: false,
      admin: true,
      customer: false,
    },
    {
      title: "My Cart",
      description: "View, update or delete items in your cart",
      icon: "shopping-basket",
      href: "/cart",
      color: "orange",
      default: false,
      admin: true,
      customer: true,
    },
    {
      title: "Orders",
      description: "View and manage orders",
      icon: "truck",
      href: "/orders",
      color: "red",
      default: false,
      admin: true,
      customer: true,
    },
    {
      title: "Gallery",
      description:
        "View and manage picture gallery. Billions of free pictures, by Unsplash API",
      icon: "image",
      href: "/pix",
      color: "violet",
      default: true,
      admin: true,
      customer: true,
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
    delay = 60
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

  function getHoverBorderClass(color: string): string {
    if (color === "orange" || color === "yellow")
      return `hover:border-${color}-300`;
    else return `hover:border-${color}-600`;
  }
</script>

<div
  class="max-w-7xl mx-auto px-6 py-10
  grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 scale-up-center-normal"
>
  <!-- ${getHoverBorderClass(item.color)} -->
  {#each displayedItems as item}
    <a
      href="#{item.href}"
      class={`group block p-6 bg-base-200 dark:bg-slate-950 
             transition-all duration-200 
            hover:bg-accent/5 border-3 border-base-100  
            text-primary dark:text-accent home-link
            hover:text-white dark:hover:bg-black
            hover:bg-linear-to-br hover:from-blue-600 hover:to-violet-600 nb-card
            `}
    >
      <div
        class="text-4xl font-semibold scale-up-center-normal text-primary/90 menu-block group-hover:text-white mb-4"
      >
        <i class="fas fa-{item.icon} w-5 mr-2"></i>
      </div>

      <h2 class="text-2xl text-accent font-semibold group-hover:text-white">
        {item.title}
      </h2>

      <p class="mt-2 text-base dark:text-violet-400 text-primary/66 group-hover:text-white">
        {item.description}
      </p>
    </a>
  {/each}
</div>
<!-- 

        <button class="nb-button default">Default</button>
        <button class="nb-button orange">Orange</button>
        <button class="nb-button blue">Blue</button>
        <button class="nb-button green">Green</button>
        

        <button class="nb-button default rounded">Default</button>
        <button class="nb-button orange rounded">Orange</button>
                      


        <input class="nb-input default" placeholder="Default" />
        <input class="nb-input orange" placeholder="Orange" />
        <input class="nb-input blue" placeholder="Blue" />
        <input class="nb-input green" placeholder="Green" />
                      


       <div class="nb-marquee green">
          <div class="nb-marquee-content">
             <span>Item 1</span>
             <span>Item 2</span>
          </div>
       </div>
                      


       <div class="nb-card">
          <img src="..." class="nb-card-img">
          <div class="nb-card-content">
              <h4 class="nb-card-title">Card Title</h4>
              <p class="nb-card-text">...</p>
              <div class="nb-card-actions">
                  <button class="nb-button">Learn More</button>
              </div>
          </div>
       </div> -->
                        

<style>
  /*********************************/
  /* ----------------------------------------------
* Generated by Gradienty on 2025-06-02 16:00
* animation scale-up-center-normal
* ----------------------------------------
*/
  @keyframes scale-up-center-normal {
    0% {
      transform: scale(0.5);
    }
    100% {
      transform: scale(1);
    }
  }

  .scale-up-center-normal {
    animation: scale-up-center-normal 0.25s ease-out 0s 1 normal both;
  }
  .menu-block {
    display: inline-block; /* needed for transform to work properly */
    transition: transform 0.2s ease;
  }
  .menu-block:hover {
    transform: scale(2.1); /* 10% bigger */
  }
  /* .home-link:hover h2 {
    @apply text-blue-500;
  } */
  .gblock {
    background-color: #003153 !important;
    background-image: linear-gradient(315deg, #062840 0%, #000 74%) !important;
  }
  .gradient {
 

  }
</style>
