<script lang="ts">
  import { onMount } from "svelte";
  import { getCurrentRole } from "../services/SessionStore";

  type Item = {
    title: string;
    description: string;
    color: string;
    amount: number;
  };

  const items: Item[] = [
   /*  {
      title: "Ukupno narudžbi danas",
      description: "Zbroj primljenih narudžbi + narudžbe u pripremi + spremljene narudžbe",
      color: "blue",
      amount: 127
    },     */
    
    {
      title: "Narudžbe na čekanju",
      description: "Narudžbe koje čekaju pripremu",
      color: "yellow",
      amount: 8
    },    
    {
      title: "Narudžbe u pripremi",
      color: "orange",
      description: "Narudžbe koje se trenutno pripremaju",
      amount: 23
    },    
    {
      title: "Pripremljeno narudžbi",
      description: "Narudžbe spremne za preuzimanje",
      color: "green",
      amount: 96
    },    
    {
      title: "Ukupan prihod danas",
      description: "Trenutno naplaćeno",
      color: "purple",
      amount: 3847.50
    },    
  ];

  const role = getCurrentRole();

</script>

<div class="stats-container">
  {#each items as item, index}
  <!-- style="--delay: {index * 0.1}s" -->
    <div class="stat-card {item.color}">
      <div class="card-top">
        <div class="icon-wrapper {item.color}">
          {#if item.color === 'blue'}
            📊
          {:else if item.color === 'orange'}
            🔥
          {:else if item.color === 'yellow'}
            ⏳
          {:else if item.color === 'green'}
            ✅
          {:else if item.color === 'purple'}
            💰
          {/if}
        </div>
        <h3 class="card-title">{item.title}</h3>
      </div>
      
      <div class="card-amount">
        {#if item.title.includes("prihod")}
          <span class="value">{item.amount.toFixed(2)}</span>
          <span class="currency">€</span>
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
      #4CAF50 0% 80%,      /* Restoran - 30% */
      #2196F3 30% 55%,     /* Dostava - 25% */
      #FFC107 55% 80%,     /* Wolt - 25% */
      #FF5722 80% 100%     /* Takeaway - 20% */
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
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    padding: 2rem 1rem;
/*     max-width: 1400px;
 */    margin: 0 auto;
    margin-top: -3rem;
  }

  .stat-card {
    background: var(--color-base-200);
    color: var(--color-primary);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, 0.1);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    animation: slideIn 0.5s ease-out var(--delay) both;
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

  .stat-card:hover {
    transform: translateY(-8px) scale(1.02);
    border-color: rgba(255, 255, 255, 0.2);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
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
    background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);
  }

  .card-glow.orange {
    background: radial-gradient(circle, rgba(249, 115, 22, 0.15) 0%, transparent 70%);
  }

  .card-glow.yellow {
    background: radial-gradient(circle, rgba(234, 179, 8, 0.15) 0%, transparent 70%);
  }

  .card-glow.green {
    background: radial-gradient(circle, rgba(34, 197, 94, 0.15) 0%, transparent 70%);
  }

  .card-glow.purple {
    background: radial-gradient(circle, rgba(168, 85, 247, 0.15) 0%, transparent 70%);
  }

  .stat-card:hover .card-glow {
    opacity: 1;
  }

  .card-top {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1.5rem;
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
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(59, 130, 246, 0.1));
  }

  .icon-wrapper.orange {
    background: linear-gradient(135deg, rgba(249, 115, 22, 0.2), rgba(249, 115, 22, 0.1));
  }

  .icon-wrapper.yellow {
    background: linear-gradient(135deg, rgba(234, 179, 8, 0.2), rgba(234, 179, 8, 0.1));
  }

  .icon-wrapper.green {
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.2), rgba(34, 197, 94, 0.1));
  }

  .icon-wrapper.purple {
    background: linear-gradient(135deg, rgba(168, 85, 247, 0.2), rgba(168, 85, 247, 0.1));
  }

  .stat-card:hover .icon-wrapper {
    transform: scale(1.1) rotate(5deg);
  }

  .card-title {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    color: var(--color-primary);
    flex: 1;
  }

  .card-amount {
    margin: 1.5rem 0;
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end; /* ← dodaj ovo */
  }

  .card-amount .value {
    font-size: 3rem;
    font-weight: 800;
    color: #fff;
    line-height: 1;
    letter-spacing: -1px;
  }

  .card-amount .currency {
    font-size: 1.5rem;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.7);
  }

  .stat-card {
    padding: 1rem 2rem 0rem 2rem;
  }

  .stat-card.blue .value {
    background: linear-gradient(135deg, #3b82f6, #60a5fa);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .stat-card.orange .value {
    background: linear-gradient(135deg, #f97316, #fb923c);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .stat-card.yellow .value {
    background: linear-gradient(135deg, #eab308, #fbbf24);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .stat-card.green .value {
    background: linear-gradient(135deg, #22c55e, #4ade80);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .stat-card.purple .value {
    background: linear-gradient(135deg, #a855f7, #c084fc);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .card-footer {
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }

  @media (max-width: 640px) {
    .stats-container {
      grid-template-columns: 1fr;
      padding: 1rem;
    }

    .card-amount .value {
      font-size: 2.5rem;
    }
  }
</style>