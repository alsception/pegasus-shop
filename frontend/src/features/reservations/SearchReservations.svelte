<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import { get } from "svelte/store";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import type { Reservation } from "./Reservation";
  import type { DailyReservationSummary } from "./DailyReservationSummary";
  import { slide } from "svelte/transition";
  import { formatCommentInfo } from "../../utils/formatting";

  // Keep track of which reservation is open (use id if available, otherwise index)
  let open: number | null = null;

  // Toggle open state
  const toggle = (key: number | null) => (open = open === key ? null : key);

  let totalReservations = 0;

  /*   interface Reservation {
    id: number;
    dan: string; 
    vreme: string;
    ime: string;
    email: string;
    telefon: string;
    brojGostiju: number;
    menuStandard: number;
    menuGold: number;
    menuPremium: number;
    menuVege: number;
    menuX: number;
    poslano: boolean;
    potvrdjeno: boolean;
    dogovorio: string;
    napomena: string;
    rucak: boolean;
    vecera: boolean;
    vazno: boolean;
    otkazano: boolean;
    created?: string;
    updated?: string;
    createdBy?: string;
    updatedBy?: string;
  } */

  document.title = "Reservations | Pegasus";

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
  <div class="container">
    <!-- Search Form -->

    <!-- Search Form -->
    <div class="search-form bg-base-200">
      <h2>Pretraga Rezervacija</h2>
      <div class="form-row">
        <div class="form-group">
          <label for="searchDateFrom">Datum od:</label>
          <input
            id="searchDateFrom"
            type="date"
            class="pgs-input"
            bind:value={searchDateFrom}
            placeholder="Izaberite datum"
          />
        </div>

        <div class="form-group">
          <label for="searchDateTo">Datum do:</label>
          <input
            id="searchDateTo"
            type="date"
            class="pgs-input"
            bind:value={searchDateTo}
            placeholder="Izaberite datum"
          />
        </div>

        <div class="form-group">
          <label for="searchName">Ime:</label>
          <input
            id="searchName"
            type="text"
            class="pgs-input"
            bind:value={searchName}
            on:keypress={(e) => e.key === "Enter" && handleSearch()}
          />
        </div>
      </div>

      <div class="button-group">
        <button
          class="btn btn-primary w-[100px] mr-4"
          on:click={handleSearch}
          disabled={loading}
        >
          {loading ? "Tražim..." : "Traži"}
        </button>
        <button
          class="btn btn-secondary w-[100px]"
          on:click={resetSearch}
          disabled={loading}
        >
          Resetuj
        </button>
      </div>
    </div>

    {#if loading}
      <LoadingOverlay />
    {/if}

    <!-- Results -->
    {#if !loading && dailyReservations.length > 0}
      <div class="results-header">
        <h2>Pronađeno rezervacija: {totalReservations}</h2>
      </div>

      <div class="reservations-list">
        {#each dailyReservations as summary}
          <!-- Simple Tailwind + DaisyUI card that looks calendar-like -->
          <div class="max-w-md mx-auto">
            <div class="card bg-base-100 shadow-md">
              <div class="card-body">
                <div class="flex items-start justify-between">
                  <div class="w-full">
                    <div class="flex items-center justify-between">
                      <h3 class="text-lg font-semibold">
                        {formatDate(summary.date)}
                      </h3>
                      <label
                        class="swap swap-rotate tooltip tooltip-info tooltip-left"
                        data-tip="Dodaj novu rezervaciju"
                      >
                        <button
                          on:click={openCreateModal}
                          class="btn btn-dash"
                          data-tip="Dodaj novu rezervaciju"
                          aria-label="Dodaj novu rezervaciju"
                        >
                          <i class="fas fa-plus text-xl"></i>
                        </button>
                      </label>
                    </div>

                    <div class="text-md py-2">
                      Rezervacije: <b>{summary.totalReservations}</b>
                      &nbsp;&nbsp; • &nbsp;&nbsp; Ukupno gostiju:
                      <b>{summary.totalGuests}</b>
                    </div>
                    <div
                      class="grid grid-cols-2 gap-x-8 gap-y-1 my-3 mx-4"
                      style="width: 90%;"
                    >
                      <div class="flex justify-between">
                        <span>🍽️ Standard:</span>
                        <span class="font-bold"
                          >{summary.totalMenuStandard}</span
                        >
                      </div>
                      <div class="flex justify-between">
                        <span>🟨 Gold:</span>
                        <span class="font-semibold"
                          >{summary.totalMenuGold}</span
                        >
                      </div>
                      <div class="flex justify-between">
                        <span>💎 Premium:</span>
                        <span class="font-semibold"
                          >{summary.totalMenuPremium}</span
                        >
                      </div>
                      <div class="flex justify-between">
                        <span>☘️ Vege:</span>
                        <span class="font-semibold"
                          >{summary.totalMenuVege}</span
                        >
                      </div>
                    </div>
                  </div>
                </div>

                <div class="divider my-2"></div>

                <div class="space-y-2 max-h-48 overflow-auto">
                  {#if summary.reservations.length === 0}
                    <div class="text-sm text-neutral">Nema rezervacija</div>
                  {:else}
                    <!-- Header (jednom iznad svih redova) -->
                    <div
                      class="grid grid-cols-[60px_1fr_50px_50px_50px_50px_50px] gap-2 p-2 bg-base-200 font-semibold text-xs"
                    >
                      <div>Vreme</div>
                      <div>Ime</div>
                      <div class="text-center">👤</div>
                      <div class="text-center">🍽️</div>
                      <div class="text-center">🟨</div>
                      <div class="text-center">💎</div>
                      <div class="text-center">☘️</div>
                    </div>
                    {#each summary.reservations as r, i (r.id ?? i)}
                      <!-- Red rezervacije -->
                      <div class="rounded-md overflow-hidden">
                        <button
                          class="grid grid-cols-[60px_1fr_50px_50px_50px_50px_50px] gap-2 p-2 hover:bg-base-200 w-full text-left items-center"
                          on:click={() => toggle(r.id ?? i)}
                          aria-expanded={open === (r.id ?? i)}
                        >
                          <div class="text-xs font-medium">
                            {formatTime(r.vreme)}
                          </div>
                          <div class="font-semibold">
                            {r.ime ?? "Anonimno"}&nbsp;{@html formatCommentInfo(
                              r.napomena
                            )}
                          </div>
                          <div class="text-xs text-center">
                            {r.brojGostiju ?? 0}
                          </div>
                          <div class="text-xs text-center">
                            {r.menuStandard ?? 0}
                          </div>
                          <div class="text-xs text-center">
                            {r.menuGold ?? 0}
                          </div>
                          <div class="text-xs text-center">
                            {r.menuPremium ?? 0}
                          </div>
                          <div class="text-xs text-center">
                            {r.menuVege ?? 0}
                          </div>
                        </button>
                        {#if open === (r.id ?? i)}
                          <div
                            in:slide
                            out:slide
                            class="p-2 text-sm bg-base-100 space-y-1"
                          >
                            <div>
                              <strong>Napomena:</strong>
                              {r.napomena ?? "—"}
                            </div>
                            <div class="mt-2 text-xs text-neutral">
                              Rezervacija ID: {r.id ?? i}
                            </div>
                          </div>
                        {/if}
                      </div>
                    {/each}
                  {/if}
                </div>
              </div>
            </div>
          </div>

          <!-- start comment -->
          <!-- <div class="reservation-card" class:otkazano={reservation.otkazano} class:vazno={reservation.vazno}>
              <div class="card-header">
                <div class="card-title">
                  <h3>{reservation.ime}</h3>
                  <div class="badges">
                    {#if reservation.potvrdjeno}
                      <span class="badge badge-success">Potvrđeno</span>
                    {/if}
                    {#if reservation.otkazano}
                      <span class="badge badge-danger">Otkazano</span>
                    {/if}
                    {#if reservation.vazno}
                      <span class="badge badge-warning">Važno</span>
                    {/if}
                  </div>
                </div>
                <div class="card-id">ID: {reservation.id}</div>
              </div>
              
              <div class="card-body">
                <div class="info-row">
                  <div class="info-item">
                    <strong>📅 {formatDate(reservation.dan)}</strong>
                  </div>
                  <div class="info-item">
                    <strong>🕐 {reservation.vreme} </strong>
                  </div>
                  <div class="info-item">
                    <strong>👥 Gosti:</strong> {reservation.brojGostiju}
                  </div>
                </div>
                
                <div class="info-row">
                  <div class="info-item">
                    <strong>📧 Email:</strong> {reservation.email}
                  </div>
                  <div class="info-item">
                    <strong>📞 Telefon:</strong> {reservation.telefon}
                  </div>
                </div>

                {#if getTotalMenus(reservation) > 0}
                  <div class="menu-section">
                    <strong>🍽️ Meniji:</strong>
                    <div class="menu-items">
                      {#if reservation.menuStandard > 0}
                        <span class="menu-badge">Standard: {reservation.menuStandard}</span>
                      {/if}
                      {#if reservation.menuGold > 0}
                        <span class="menu-badge">Gold: {reservation.menuGold}</span>
                      {/if}
                      {#if reservation.menuPremium > 0}
                        <span class="menu-badge">Premium: {reservation.menuPremium}</span>
                      {/if}
                      {#if reservation.menuVege > 0}
                        <span class="menu-badge">Vege: {reservation.menuVege}</span>
                      {/if}
                      {#if reservation.menuX > 0}
                        <span class="menu-badge">X: {reservation.menuX}</span>
                      {/if}
                    </div>
                  </div>
                {/if}

                <div class="info-row">
                  <div class="info-item">
                    {#if reservation.rucak}
                      <span class="tag">🌞 Ručak</span>
                    {/if}
                    {#if reservation.vecera}
                      <span class="tag">🌙 Večera</span>
                    {/if}
                  </div>
                </div>

                {#if reservation.dogovorio}
                  <div class="info-item">
                    <strong>Dogovorio:</strong> {reservation.dogovorio}
                  </div>
                {/if}

                {#if reservation.napomena}
                  <div class="info-item note">
                    <strong>📝 Napomena:</strong> {reservation.napomena}
                  </div>
                {/if}
              </div>
            </div> -->
          <!-- end comment -->
        {/each}
      </div>
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

<style>
  .container {
    width: 100%;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
      Ubuntu, Cantarell, sans-serif;
  }

  h1 {
    color: #333;
    margin-bottom: 30px;
    font-size: 2rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
  }

  label {
    font-weight: 600;
    margin-bottom: 8px;
    color: #555;
  }

  .button-group {
    display: flex;
    gap: 10px;
    margin-top: 24px;
  }

  .no-search,
  .no-results {
    text-align: center;
    padding: 40px;
    color: #666;
    font-size: 1.1rem;
  }

  .results-header {
    margin: 30px 0 20px 0;
  }

  .results-header h2 {
    color: #333;
    font-size: 1.5rem;
  }

  .reservations-list {
    display: grid;
    gap: 20px;
    grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    width: 100%;
  }

  @media (max-width: 768px) {
    .container {
      padding: 10px;
    }

    .info-row {
      grid-template-columns: 1fr;
    }

    .button-group {
      flex-direction: column;
      width: 100%;
    }
  }

  /*  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  } */

  h2 {
    margin-bottom: 30px;
    font-size: 1.5rem;
  }

  .search-form {
    padding: 25px;
    border-radius: 8px;
    margin-bottom: 30px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    max-width: 1496px;
  }

  .form-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
  }

  label {
    font-weight: 600;
    margin-bottom: 8px;
    color: #555;
  }

  /* t */

  .button-group {
    display: flex;
    gap: 10px;
  }

  .btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .btn-primary {
    background-color: #007bff;
    color: white;
  }

  .btn-primary:hover:not(:disabled) {
    background-color: #0056b3;
  }

  .btn-secondary {
    background-color: #6c757d;
    color: white;
  }

  .btn-secondary:hover:not(:disabled) {
    background-color: #545b62;
  }

  .error-message {
    background-color: #f8d7da;
    color: #721c24;
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 20px;
    border: 1px solid #f5c6cb;
  }

  .loading {
    text-align: center;
    padding: 40px;
    font-size: 1.2rem;
    color: #666;
  }

  .no-search,
  .no-results {
    text-align: center;
    padding: 40px;
    color: #666;
    font-size: 1.1rem;
  }

  .results-header {
    margin-bottom: 20px;
  }

  .results-header h2 {
    color: #333;
    font-size: 1.5rem;
  }

  .card-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  @media (max-width: 768px) {
    .container {
      padding: 10px;
    }

    .form-row {
      grid-template-columns: 1fr;
    }

    .info-row {
      grid-template-columns: 1fr;
    }

    .button-group {
      flex-direction: column;
    }

    .btn {
      width: 100%;
    }
  }
  .card {
    width: 350px;
    margin-left: 0px;
    margin-right: 0px;
  }
</style>
