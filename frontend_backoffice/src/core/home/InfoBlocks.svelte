<script lang="ts">
  import { onMount } from "svelte";
  import { auth, getCurrentRole } from "../services/SessionStore";
  import type { Order } from "../../features/orders/Order";
  import { link } from "svelte-spa-router";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
  const token = $auth.token;

  let orders: Order[] = [];
  let ordersNaCekanju: Order[] = [];
  let ordersUpripremi: Order[] = [];
  let ordersSpremni: Order[] = [];
  let totalAmount = 0;

  type Item = {
    title: string;
    description: string;
    color: string;
    amount: number;
  };

  const items: Item[] = 
  [
    {
      title: "Narudžbe na čekanju",
      description: "Narudžbe koje čekaju pripremu",
      color: "gray",
      amount: 0,
    },
    {
      title: "Narudžbe u pripremi",
      color: "gray",
      description: "Narudžbe koje se trenutno pripremaju",
      amount: 0,
    },
    {
      title: "Pripremljeno narudžbi",
      description: "Narudžbe spremne za preuzimanje",
      color: "gray",
      amount: 0,
    },
    {
      title: "Ukupan promet danas",
      description: "Trenutno naplaćeno",
      color: "gray",
      amount: 0,
    },
  ];

  const role = getCurrentRole();

  onMount(() => {
    loadOrders();
  });

  async function loadOrders() {
    const token = $auth.token;

    try {
      const res = await fetch(API_BASE_URL + `/orders`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      // Check response status and handle specific cases
      if (!res.ok) {
        if (res.status === 401) {
          console.log("Authentication failed - token may be expired");
          // Clear invalid token
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          // Redirect to login or show login modal
          // window.location.href = '/login';
          // OR: showLoginModal = true;
          // OR: goto('/login');
          throw new Error("Authentication failed");
        }
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      // Parse JSON directly - no need for JSON.parse since res.json() already does this
      const data = await res.json();

      // Update orders with the received data
      orders = data;

      orders = data.reverse();

      ordersNaCekanju = orders.filter((o) => o.status === "WAITING");
      ordersUpripremi = orders.filter((o) => o.status === "IN_PREPARATION");
      ordersSpremni = orders.filter(
        (o) => o.status === "READY" || o.status === "SERVED"
      );
      totalAmount = calculateTotal(orders);

      items[0].amount = ordersNaCekanju.length;
      items[1].amount = ordersUpripremi.length;
      items[2].amount = ordersSpremni.length;
      items[3].amount = totalAmount;
    } catch (error: any) {
      console.error("Error loading orders:", error);

      /**
       * TODO: see if this works. should display error message.
       * ako je failed to fetch, server je nedostupan.
       */

      // Handle 401 Unauthorized specifically
      if (error.message.includes("401")) {
        console.log("Authentication failed - token may be expired");
        // Clear invalid token
        $auth.token = null;
        // Redirect to login or show login modal
        // window.location.href = '/login';
        // OR: showLoginModal = true;
        // OR: goto('/login');
      }

      // Handle other errors appropriately (show user message, etc.)
    }
  }

  function calculateTotal(orders: Order[]): number {
    return orders.reduce((sum, order) => {
      return sum + (order.price ?? 0);
    }, 0);
  }
</script>

<div class="stats-container hidden sm:grid">
  {#each items as item, index}
    <!-- style="--delay: {index * 0.1}s" -->
     <a use:link href="/stats" class="cursor-pointer">
    <div class="stat-card rounded-xl p-2 flex flex-col gap-1 h-fit
         shadow border border-primary/20 hover:outline-primary/20 hover:outline-1
        bg-gradient-to-br border border-zinc-200  dark:border-0 from-slate-100 dark:from-slate-900 via-green-100 dark:via-green-950  to-emerald-100 dark:to-emerald-900  s-FD2xrYezMqqb">
      <div class="card-top">
        <h3 class="card-title text-primary/70">{item.title}</h3>
      </div>

      <div class="card-amount text-primary/70 mb-1">
        {#if item.title.includes("promet")}
          <span class="value">€ {item.amount.toFixed(2)}</span>
        {:else}
          <span class="value">{item.amount}</span>
        {/if}
      </div>

      {#if item.description}
        <!--   <div class="card-footer">
          <p>{item.description}</p>
        </div> -->
      {/if}

      <div class="card-glow {item.color}"></div>
    </div>
    </a>
  {/each}
</div>

<style>
  .chart-container {
    display: flex;
    align-items: center;
    font-family: sans-serif;
    gap: 20px;
  }

  .pie-chart {
    width: 200px;
    height: 200px;
    border-radius: 50%;
    /* Postotke podešavaš ovdje: */
    background: conic-gradient(
      #4caf50 0% 80%,
      /* Restoran - 30% */ #2196f3 30% 55%,
      /* Dostava - 25% */ #ffc107 55% 80%,
      /* Wolt - 25% */ #ff5722 80% 100% /* Takeaway - 20% */
    );
  }

  .legend {
    list-style: none;
    padding: 0;
  }

  .dot {
    width: 12px;
    height: 12px;
    border-radius: 2px;
    margin-right: 8px;
  }

  .stats-container {
    grid-template-columns: repeat(auto-fit, minmax(214px, 1fr));
    gap: 1.5rem;
    padding: 3rem 0.1rem;

    margin: 0 auto;
    margin-top: -3rem;
    /*  */
  }

  .stat-card {
    background: var(--color-base-200);
    color: var(--color-primary);
    backdrop-filter: blur(10px);
    /*     border-radius: 20px;
 */
    padding: 2rem;
    position: relative;
    overflow: hidden;
    /* border: 1px solid rgba(255, 255, 255, 0.1); */
    /*     transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
 */ /* animation: slideIn 0.5s ease-out var(--delay) both; */
  }

  @keyframes slideIn {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .card-glow {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    opacity: 0;
    transition: opacity 0.4s ease;
    pointer-events: none;
  }

  .card-glow.blue {
    background: radial-gradient(
      circle,
      rgba(59, 130, 246, 0.15) 0%,
      transparent 70%
    );
  }

  .card-glow.orange {
    background: radial-gradient(
      circle,
      rgba(249, 115, 22, 0.15) 0%,
      transparent 70%
    );
  }

  .card-glow.yellow {
    background: radial-gradient(
      circle,
      rgba(234, 179, 8, 0.15) 0%,
      transparent 70%
    );
  }

  .card-glow.green {
    background: radial-gradient(
      circle,
      rgba(34, 197, 94, 0.15) 0%,
      transparent 70%
    );
  }

  .card-glow.purple {
    background: radial-gradient(
      circle,
      rgba(168, 85, 247, 0.15) 0%,
      transparent 70%
    );
  }

  .stat-card .card-glow {
    opacity: 1;
  }
  .custom-pattern-1 {
    background:
        32px 32px/calc(2*32px) calc(2*32px) conic-gradient(at calc(500%/6) 50%,#E4E4ED 25%,#0000 0),0 0/calc(2*32px) calc(2*32px) conic-gradient(at calc(500%/6) 50%,#E4E4ED 25%,#0000 0),
        32px 32px/calc(2*32px) calc(2*32px) conic-gradient(at calc(200%/3) 50%,#1AE5D6 25%,#0000 0),0 0/calc(2*32px) calc(2*32px) conic-gradient(at calc(200%/3) 50%,#1AE5D6 25%,#0000 0),
        repeating-conic-gradient(#1A8FE5 0 25%,#0000 0 50%) 0 0/calc(2*32px) calc(2*32px),
        linear-gradient(#1A8FE5 calc(100%/3),#1AE5D6 0 calc(200%/3),#E4E4ED 0)
        0 0/32px 32px;
  }
  .card-top {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .icon-wrapper {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    transition: transform 0.3s ease;
  }

  .icon-wrapper.blue {
    background: linear-gradient(
      135deg,
      rgba(59, 130, 246, 0.2),
      rgba(59, 130, 246, 0.1)
    );
  }

  .icon-wrapper.orange {
    background: linear-gradient(
      135deg,
      rgba(249, 115, 22, 0.2),
      rgba(249, 115, 22, 0.1)
    );
  }

  .icon-wrapper.yellow {
    background: linear-gradient(
      135deg,
      rgba(234, 179, 8, 0.2),
      rgba(234, 179, 8, 0.1)
    );
  }

  .icon-wrapper.green {
    background: linear-gradient(
      135deg,
      rgba(34, 197, 94, 0.2),
      rgba(34, 197, 94, 0.1)
    );
  }

  .icon-wrapper.purple {
    background: linear-gradient(
      135deg,
      rgba(168, 85, 247, 0.2),
      rgba(168, 85, 247, 0.1)
    );
  }

  .stat-card:hover .icon-wrapper {
    transform: scale(1.1) rotate(5deg);
  }

  .card-title {
    margin: 0;
    font-size: 1rem;
    font-weight: 500;
    flex: 1;
  }

  .card-amount {
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end; /* ← dodaj ovo */
  }

  .card-amount .value {
    font-size: 2rem;
    font-weight: 800;
    line-height: 1;
    letter-spacing: -1px;
  }

  .stat-card {
    padding: 1rem 2rem 0rem 2rem;
  }

  .card-footer {
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }

  @media (max-width: 640px) {
    .stats-container {
      grid-template-columns: 1fr;
      padding: 1rem;
      margin-top: 0;
    }

    .card-amount .value {
      font-size: 2.5rem;
    }
  }
</style>
