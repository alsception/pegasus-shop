<script lang="ts">
  import { onMount } from "svelte";
  import { auth } from "../../core/services/SessionStore";
  import { get } from "svelte/store";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import type { Reservation } from "./Reservation";
  import type { DailyReservationSummary } from "./DailyReservationSummary";
  import InlineEdit from "./InlineEdit.svelte";
  import NewReservationModal from "./NewReservationModal.svelte";
  import { link } from "svelte-spa-router";
  import Chart from "./Chart.svelte";

  let showCreateModal = false;
  let totalReservations = 0;

  document.title = "Rezervacije | Pegasus";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  // Authentication
  let isAuthenticated = false;

  // Search parameters
  let searchDateFrom = "2025-11-01";
  let searchDateTo = "2025-11-30";
  let searchName = "";

  // Results
  let dailyReservations: DailyReservationSummary[] = [];
  let loading = false;
  let error: string | null = null;
  let isBlockView = false;
  let selectedreservation: DailyReservationSummary = {
    date: "2025-11-15", // ISO date format: "2025-11-15"
  totalGuests: 0,
  totalMenuStandard:0,
  totalMenuGold: 0,
  totalMenuPremium:0,
  totalMenuVege:0,
  totalReservations: 0,
    reservations: []
  };

  function toggleView() {
    isBlockView = !isBlockView;
  }

  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  onMount(async () => {
    try {
      const { isAuthenticated: authStatus } = get(auth);
      isAuthenticated = authStatus;

      if (!isAuthenticated) {
        error = "Session expired. Please login again.";
        return;
      }

      handleSearch();
    } catch (err) {
      error = err instanceof Error ? err.message : "Unknown error";
    }
  });
  /* 
  function handleFormSubmit(event: { preventDefault: () => void }) {
    event.preventDefault();
    handleSearch();
  } */

  async function handleSearch() {
    loading = true;
    error = null;

    const token = $auth.token;

    try {
      const params = new URLSearchParams();
      if (searchDateFrom) params.append("dateFrom", searchDateFrom);
      if (searchDateTo) params.append("dateTo", searchDateTo);
      if (searchName.trim()) params.append("name", searchName.trim());

      const url = `${API_BASE_URL}/reservations/search?${params.toString()}`;

      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          throw new Error("Authentication failed");
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      dailyReservations = await response.json();
      // način 1: pomoću reduce()
      totalReservations = dailyReservations.reduce(
        (sum, day) => sum + day.reservations.length,
        0
      );
      selectedreservation = dailyReservations[0];

      console.log("TOTAL : " + totalReservations); // 4
    } catch (err: any) {
      if (err.message === "Authentication failed") {
        error = "Session expired. Please log in again.";
        $auth.token = null;
      } else {
        error = err instanceof Error ? err.message : "An error occurred";
      }
      dailyReservations = [];
    } finally {
      loading = false;
    }
  }

  function resetSearch() {
    searchDateFrom = "";
    searchDateTo = "";
    searchName = "";
    dailyReservations = [];
    error = null;
  }

  function formatDate(dateString: string): string {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString("hr-HR");
  }

  function getTotalMenus(reservation: Reservation): number {
    return (
      (reservation.menuStandard || 0) +
      (reservation.menuGold || 0) +
      (reservation.menuPremium || 0) +
      (reservation.menuVege || 0) +
      (reservation.menuX || 0)
    );
  }

  function formatTime(timeString: string) {
    if (!timeString) return "—";

    const [hours, minutes] = timeString.split(":");
    return `${hours.padStart(2, "0")}:${minutes.padStart(2, "0")}`;
  }

  export function getDanUNedeljiFromString(dateStr: string): string 
  {
    const date = new Date(dateStr);

    const dani: string[] = [
      "nedelja",
      "ponedeljak",
      "utorak",
      "sreda",
      "četvrtak",
      "petak",
      "subota"
    ];

    return dani[date.getDay()];
  }

  function openCreateModal() 
  {
    showCreateModal = true;
  }


  function showInfoModal( day : DailyReservationSummary): void {
    const contentEl = document.getElementById("modal-content");
    const dialogEl = document.getElementById("modal") as HTMLDialogElement;

    if (contentEl) {
     
      selectedreservation = day;
    }

    if (dialogEl) {
      dialogEl.showModal();
    }
  }

  const data = [12, 19, 3, 5, 2, 15];
  const max = Math.max(...data);
  /*   const formatDate = (iso: string) => {
    try {
    const d = new Date(iso);
    return d.toLocaleDateString(undefined, { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });
    } catch (e) {
    return iso;
    }
    }; */
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else if error}
  <ErrorDiv {error} />
{:else}
  <div class="">
    <!-- Search Form -->
    <div class="w-full flex justify-center px-1">
  <div class="w-full /*max-w-4xl*/ p-4 bg-base-200 rounded-lg mb-1"  style="min-width: fit-content">
    <form class="flex flex-col sm:flex-row items-center gap-3 w-full">
      <!-- Ime -->
       <div class="flex flex-col px-2">
        <label for="searchName" class="text-sm font-medium text-secondary mb-1">
          Ime
        </label>
        <input
          id="searchName"
          type="text"
          class="input pgs-input"
          bind:value={searchName}
          placeholder="Upiši ime za pretragu..."
          on:keypress={(e) => e.key === "Enter" && handleSearch()}
        />
      </div>      

      <!-- Datum od -->
      <div class="flex flex-col px-2">
        <label for="searchDateFrom" class="text-sm font-medium text-secondary mb-1">
          Datum od
        </label>
        <input
          id="searchDateFrom"
          type="date"
          class="input pgs-input max-w-[9rem]"
          bind:value={searchDateFrom}
          placeholder="Datum od"
        />
      </div>

      <!-- Datum do -->
      <div class="flex flex-col px-2">
        <label for="searchDateTo" class="text-sm font-medium text-secondary mb-1">
          Datum do
        </label>
        <input
        id="searchDateTo"
        type="date"
        class="input pgs-input max-w-[9rem]"
        bind:value={searchDateTo}
        placeholder="Datum do"
      />
      </div>      

      <!-- Dugmad -->
      <button
        type="button"
        class="btn btn-dash"
        on:click={handleSearch}
        disabled={loading}
      >
        <i class="fas fa-search"></i>
        {loading ? "Tražim..." : "Traži   "}
      </button>
      <button
        type="button"
        class="btn btn-dash"
        on:click={resetSearch}
        disabled={loading}
      >
        Resetuj
      </button>
      <button
        on:click={openCreateModal}
        class="btn btn-dash"
      >       
          <i class="fas fa-calendar-plus"></i>
          Nova rezervacija
      </button>
      <button 
                  type="button"

        class="btn btn-dash" on:click={toggleView}>
        
        <i class="fas fa-th-large"></i>
        Prikaz
      </button>

    </form>
  </div>
</div>


    {#if loading}
      <LoadingOverlay />
    {/if}

    <!-- Results -->
    {#if !loading && dailyReservations.length > 0}
      <div class="results-info pl-6 pb-6">
        <h3>Pronađeno rezervacija: {totalReservations}</h3>
      </div>

    <!-- Updated target code with styling applied from source (Tailwind/DaisyUI) -->
    <!-- NOTE: Only styling copied. No structure or logic was changed. -->

      {#if isBlockView}
  <div class="/*reservations-list*/ grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 2xl:grid-cols-4 gap-4 md:gap-16 p-2">
    <!-- tvoj postojeći kartični prikaz ostaje isti -->
      {#each dailyReservations as summary}
        <div class="max-w-md mx-auto">
          <div class="card-lg bg-base-200 shadow p-4 rounded-xl flex flex-col gap-2 h-fit">
            <div class="card-body p-0">
              <div class="flex items-start justify-between mb-2">
                <div class="w-full flex items-center gap-2" style="justify-content: space-between;">
                  
                  <div class="flex flex-col gap-2 w-full ml-2" style="justify-content: space-between;">
                  
                    <span class="text-xl font-extrabold text-primary">
                      <a use:link href="/reservations/{summary.date}" class="pgs-hyperlink">{formatDate(summary.date)}</a>
                    </span>
                    <span class="text-md font-bold text-primary/70">{getDanUNedeljiFromString(summary.date)}</span>
                    
                    <!-- <span class="badge badge-soft badge-{getOrderStatusColor(order.status)} font-mono badge-lg ml-auto" style="text-transform: uppercase;">
                      {order.status}
                    </span> -->
                  </div>
                  
                  <div class="text-md py-2 text-primary flex items-center gap-4 ">
                    <div class="flex items-center gap-2 text-sm text-primary mr-4">
                      <div class="tooltip tooltip-info" data-tip="Broj rezervacija">
                        <i class="fas fa-address-book"></i>
                      </div>                      
                      <span class="text-xl"><strong>{summary.totalReservations}</strong></span>
                    </div>  
                    <div class="flex items-center gap-2 text-sm text-primary mr-4">                      
                      <div class="tooltip tooltip-info" data-tip="Ukupno gostiju">
                        <i class="fas fa-users"></i>
                      </div>                      
                      <span class="text-xl"><strong>{summary.totalGuests}</strong></span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="grid grid-cols-4 gap-x-8 gap-y-2 my-3 mx-4 mr-0" style="width: 100%;">
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Standard">
                    <i class="fas fa-utensils text-sky-500"></i>
                    <span class="font-bold">{summary.totalMenuStandard}</span>
                  </div> 
                </div>
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Gold">
                    <i class="fas fa-crown text-amber-400"></i>
                    <span class="font-semibold">{summary.totalMenuGold}</span>
                  </div> 
                </div>
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Premium">
                    <i class="fas fa-gem text-violet-500"></i>
                    <span class="font-semibold">{summary.totalMenuPremium}</span>
                  </div> 
                </div>
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Vege">
                    <i class="fas fa-leaf text-success"></i>
                    <span class="font-semibold">{summary.totalMenuVege}</span>
                  </div> 
                </div>
              </div>
              <div class="divider my-2"></div>
              <div class="space-y-0 /*max-h-48*/ overflow-auto">
                {#if summary.reservations.length === 0}
                  <div class="text-sm text-neutral">Nema rezervacija</div>
                {:else}
                  <table class="w-full border-collapse">
                  <thead>
                    <tr class="bg-base-200 border-b border-base-300">
                      <th class="text-xs font-medium text-secondary text-left p-2">Vrijeme</th>
                      <th class="text-xs font-medium text-secondary text-left p-2">Ime</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Gosti</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Standard</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Gold</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Premium</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Vege</th>
                    </tr>
                  </thead>
                  <tbody>
                    {#each summary.reservations as r, i (r.id ?? i)}
                      <tr class="border-b border-base-300 hover:bg-base-200">

                        <td class="text-xs p-2">
                          <InlineEdit bind:value={r.vreme} type="time" />
                        </td>

                        <td class="text-xs p-2">
                          <InlineEdit bind:value={r.ime} />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.brojGostiju} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuStandard} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuGold} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuPremium} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuVege} type="number" />
                        </td>

                      </tr>
                    {/each}

                  </tbody>
                </table>
                {/if}
              </div>
            </div>
          </div>
        </div>
      {/each}
    <!-- tvoj postojeći kartični prikaz ostaje isti -->
  </div>
{:else}
  <!-- OVDE IDE NOVI TABELARNI PRIKAZ -->
  <div class="max-w-[2048px] w-full overflow-x-auto rounded-lg align-middle text-center mx-auto mt-6 flex justify-center">
    <table class="table table-zebra divide-y divide-accent w-auto">
      <thead class="bg-base-200 border-b-1 border-secondary/30">
        <tr class="h-12">
          <th class="pgs-th">Datum</th>
          <th class="pgs-th">Dan</th>
          <th class="pgs-th">Rezervacije</th>
          <th class="pgs-th">Gosti</th>
          <th class="pgs-th">Standard</th>
          <th class="pgs-th">Gold</th>
          <th class="pgs-th">Premium</th>
          <th class="pgs-th">Vege</th>
        </tr>
      </thead>
      <tbody>
        {#each dailyReservations as day}
          <tr class="bg-base-100 hover:bg-blue-600/10">
            <td class="pgs-td">
              <!-- use:link href="/reservations/{day.date}" -->
              <button  type="button"
              on:click={showInfoModal(day)}
             class="pgs-hyperlink">{formatDate(day.date)} 
            
          </button>
            </td>
            <td class="pgs-td">{getDanUNedeljiFromString(day.date)}</td>
            <td class="pgs-td-num">{day.totalReservations}</td>
            <td class="pgs-td-num">{day.totalGuests}</td>
            <td class="pgs-td-num">{day.totalMenuStandard}</td>
            <td class="pgs-td-num">{day.totalMenuGold}</td>
            <td class="pgs-td-num">{day.totalMenuPremium}</td>
            <td class="pgs-td-num">{day.totalMenuVege}</td>
          </tr>
        {/each}
        <!-- Ukupno red -->
        <tr class="font-bold border-t-1 border-secondary/30 bg-base-100">
          <td class="pgs-td" colspan="2">UKUPNO</td>
          <td class="pgs-td-num">{dailyReservations.reduce((sum, day) => sum + day.totalReservations, 0)}</td>
          <td class="pgs-td-num">{dailyReservations.reduce((sum, day) => sum + day.totalGuests, 0)}</td>
          <td class="pgs-td-num">{dailyReservations.reduce((sum, day) => sum + day.totalMenuStandard, 0)}</td>
          <td class="pgs-td-num">{dailyReservations.reduce((sum, day) => sum + day.totalMenuGold, 0)}</td>
          <td class="pgs-td-num">{dailyReservations.reduce((sum, day) => sum + day.totalMenuPremium, 0)}</td>
          <td class="pgs-td-num">{dailyReservations.reduce((sum, day) => sum + day.totalMenuVege, 0)}</td>
        </tr>
      </tbody>
    </table>
  </div>
{/if}

    {:else if !loading && searchDateFrom === "" && searchDateTo === "" && searchName === ""}
      <div class="no-search">
        Unesite datum ili ime za pretragu rezervacija.
      </div>
    {:else if !loading && dailyReservations.length === 0 && (searchDateFrom || searchDateTo || searchName)}
      <div class="no-results">
        Nema pronađenih rezervacija za zadati kriterijum.
      </div>
    {/if}
  </div>
{/if}

<NewReservationModal
    isOpen={showCreateModal}
    on:close={() => {showCreateModal = false;}}
  />



<!-- Info Modal -->
<dialog id="modal" class="modal modal-bottom sm:modal-middle w-full">
  <div class="modal-box" style="min-width: min-content;">
    <h3 class="text-lg font-bold" id="modal-title"></h3>
    <div class="py-4" id="modal-content">

      <div class="max-w-md mx-auto">
          <div class="card-lg bg-base-200 shadow p-4 rounded-xl flex flex-col gap-2 h-fit">
            <div class="card-body p-0">
              <div class="flex items-start justify-between mb-2">
                <div class="w-full flex items-center gap-2" style="justify-content: space-between;">
                  
                  <div class="flex flex-col gap-2 w-full ml-2" style="justify-content: space-between;">
                  
                    <span class="text-xl font-extrabold text-primary">
                      <a use:link href="/reservations/{selectedreservation.date}" class="pgs-hyperlink">{formatDate(selectedreservation.date)}</a>
                    </span>
                    <span class="text-md font-bold text-primary/70">{getDanUNedeljiFromString(selectedreservation.date)}</span>
                    
                    <!-- <span class="badge badge-soft badge-{getOrderStatusColor(order.status)} font-mono badge-lg ml-auto" style="text-transform: uppercase;">
                      {order.status}
                    </span> -->
                  </div>
                  
                  <div class="text-md py-2 text-primary flex items-center gap-4 ">
                    <div class="flex items-center gap-2 text-sm text-primary mr-4">
                      <div class="tooltip tooltip-info" data-tip="Broj rezervacija">
                        <i class="fas fa-address-book"></i>
                      </div>                      
                      <span class="text-xl"><strong>{selectedreservation.totalReservations}</strong></span>
                    </div>  
                    <div class="flex items-center gap-2 text-sm text-primary mr-4">                      
                      <div class="tooltip tooltip-info" data-tip="Ukupno gostiju">
                        <i class="fas fa-users"></i>
                      </div>                      
                      <span class="text-xl"><strong>{selectedreservation.totalGuests}</strong></span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="grid grid-cols-4 gap-x-8 gap-y-2 my-3 mx-4 mr-0" style="width: 100%;">
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Standard">
                    <i class="fas fa-utensils text-sky-500"></i>
                    <span class="font-bold">{selectedreservation.totalMenuStandard}</span>
                  </div> 
                </div>
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Gold">
                    <i class="fas fa-crown text-amber-400"></i>
                    <span class="font-semibold">{selectedreservation.totalMenuGold}</span>
                  </div> 
                </div>
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Premium">
                    <i class="fas fa-gem text-violet-500"></i>
                    <span class="font-semibold">{selectedreservation.totalMenuPremium}</span>
                  </div> 
                </div>
                <div class="flex gap-2">
                  <div class="tooltip tooltip-info" data-tip="Vege">
                    <i class="fas fa-leaf text-success"></i>
                    <span class="font-semibold">{selectedreservation.totalMenuVege}</span>
                  </div> 
                </div>
              </div>
              <div class="divider my-2"></div>
              <div class="space-y-0 /*max-h-48*/ overflow-auto">
                {#if selectedreservation.reservations.length === 0}
                  <div class="text-sm text-neutral">Nema rezervacija</div>
                {:else}
                  <table class="w-full border-collapse">
                  <thead>
                    <tr class="bg-base-200 border-b border-base-300">
                      <th class="text-xs font-medium text-secondary text-left p-2">Vrijeme</th>
                      <th class="text-xs font-medium text-secondary text-left p-2">Ime</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Gosti</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Standard</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Gold</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Premium</th>
                      <th class="text-xs font-medium text-secondary text-center p-2">Vege</th>
                    </tr>
                  </thead>
                  <tbody>
                    {#each selectedreservation.reservations as r, i (r.id ?? i)}
                      <tr class="border-b border-base-300 hover:bg-base-200">

                        <td class="text-xs p-2">
                          <InlineEdit bind:value={r.vreme} type="time" />
                        </td>

                        <td class="text-xs p-2">
                          <InlineEdit bind:value={r.ime} />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.brojGostiju} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuStandard} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuGold} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuPremium} type="number" />
                        </td>

                        <td class="text-xs text-center p-2">
                          <InlineEdit bind:value={r.menuVege} type="number" />
                        </td>

                      </tr>
                    {/each}

                  </tbody>
                </table>
                {/if}
              </div>
            </div>
          </div>
        </div>
      </div>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>


<svg viewBox="0 0 300 100" class="w-full h-32">
  <polyline
    points={data.map((val, i) => `${i * 50},${100 - (val/max * 80)}`).join(' ')}
    fill="none"
    stroke="#00ff9f"
    stroke-width="2"
  />
</svg>


<Chart {dailyReservations} />

<style>
 
  .results-info,
  .no-search,
  .no-results {
    text-align: center;
    padding: 40px;
    font-size: 1.1rem;
    color: #666;
  }

  .btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .no-search,
  .no-results {
    text-align: center;
    padding: 40px;
    color: #666;
    font-size: 1.1rem;
  }

  .card-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

</style>
