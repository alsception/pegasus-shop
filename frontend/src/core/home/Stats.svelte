<script lang="ts">
  import { onMount } from "svelte";
  import { getCurrentRole } from "../services/SessionStore";
  import InfoBlocks from "./InfoBlocks.svelte";
  import Charts from "./Charts.svelte";
  import api from "../../core/services/client";
  import InfoModal from "../utils/InfoModal.svelte";
  import { showInfoModal } from "../../utils/modal";
  import LoadingOverlay from "../utils/LoadingOverlay.svelte";


  document.title = "Pegasus";

  let isLoading = false;

  const syncUrl = '/barbacoa/sync';

  /**
   * TODO ovde verovatno treba obrisati 90% koda lol
   *
   */

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
      emoji: "🍔",
      title: "Jelovnik",
      description: "Pregledaj proizvode",
      icon: "box--",
      href: "/products?listView=false",
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
      admin: true,
      customer: true,
    },
    {
      id: 2,
      emoji: "🥩",
      title: "Artikli",
      description: "Upravljanje artiklima",
      icon: "box--",
      href: "/products?listView=true",
      color: "yellow",
      default: false,
      admin: true,
      customer: false,
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
      id: 5,
      emoji: "📅",
      title: "Rezervacije",
      description: "Upravljanje rezervacijama",
      icon: "truck--",
      href: "/reservations",
      color: "red",
      default: false,
      admin: true,
      customer: true,
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
      id: 7,
      emoji: "🖼️",
      title: "Galerija",
      description: "Pregled i upravljanje galerijom slika. Milijarde besplatnih slika putem Unsplash API",
      icon: "//image",
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

  async function syncData() {
  isLoading = true;
    let syncMessage;
    try 
    {
      let result = await api<any>(syncUrl, {
        method: "POST",
      });

      syncMessage = await result; // ili result.message ako je objekat
      console.log(syncMessage)
      
    } catch (error) {
      console.log(error);
      syncMessage = 'Greška pri sinhronizaciji';
    } finally {
     isLoading = false;

      showInfoModal(syncMessage['message']);
    }
  }
</script>
      
<InfoBlocks/>

<Charts/> 

  {#if isLoading}

      <LoadingOverlay/>
  
    {/if}

<div
  class="max-w-9xl mx-auto px-6 py-10
  grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 scale-up-center-normal menu-container"
>
<div class="flex flex-row gap-3 w-full md:w-auto">
        <button
          type="submit" 
          onclick={syncData}
          class="btn btn-primary flex-1 md:flex-none whitespace-nowrap"
        >
          <i class="fas fa-sync"></i>
          Importuj jelovnik
        </button>
</div>
  <!-- {#each displayedItems as item}


    <a
      href="#{item.href}"
      class=""
    >
   <div class="menu-card card-8">
            <div class="card-content">
                <span class="icon">{item.emoji}</span>
                 <h2 class="title">{item.title}</h2>
                <p class="description"> {item.description}</p>
            </div>
            <span class="arrow">→</span>
        </div>
    </a>


  {/each} -->
</div>

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

@keyframes rotation_9018 {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}



 .menu-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(270px, 1fr));
            gap: 20px;
            max-width: 1200px;
            width: 100%;
        }




        @media (max-width: 768px) {
            .menu-container {
                grid-template-columns: 1fr;
            }
        }
</style>
