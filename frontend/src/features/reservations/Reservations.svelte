<script lang="ts">
  import { onMount } from "svelte";
  import { get } from "svelte/store";
  import { auth } from "../../core/services/SessionStore";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import ReservationCard from "./ReservationCard.svelte";
  import Login from "../../core/auth/Login.svelte";
  import type { Reservation } from "./Reservation.ts";
  import NewReservation from "./NewReservation.svelte";

  document.title = "Rezervacije | Pegasus";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  // State variables
  let isAuthenticated = false;
  let reservations: Reservation[] = [];
  let loading = false;
  let error: string | null = null;
  let page = 0;
  let size = 20;
  let totalReservations = 0;
  let totalPages = 0;

  // Auth subscription
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  // Fetch reservations on mount
  onMount(() => {
    fetchReservations();
  });

  async function fetchReservations() {
    loading = true;
    error = null;
    try {
      const token = $auth.token;
      const res = await fetch(
        API_BASE_URL + `/reservations?page=${page}&size=${size}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (!res.ok) throw new Error(`Fetch error: ${res.status}`);
      const data = await res.json();
      reservations = data.reservations ?? data; // support both paginated and plain
      totalReservations = data.totalCount ?? reservations.length; 
      totalPages = data.totalPages ?? 1;
    } catch (err: any) {
      error = err.message || "Unknown error";
    } finally {
      loading = false;
    }
  }

  function nextPage() {
    if (page + 1 < totalPages) {
      page += 1;
      fetchReservations();
    }
  }
  function prevPage() {
    if (page > 0) {
      page -= 1;
      fetchReservations();
    }
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else if error}
  <ErrorDiv {error} />
{:else}
  <div class="w-full flex justify-center px-4">
    <div class="w-full max-w-[1800px] p-4 bg-transparent rounded-lg">
      <!-- Add controls here if needed (search, filters, etc.) -->

      <!-- Pagination Controls: hidden -->
      <div class="flex flex-col sm:flex-row justify-between items-center mt-4 gap-2 hidden">
        <div class="flex gap-2 items-center">
          <button class="btn btn-outline" on:click={prevPage} disabled={page === 0}>⬅️ Prev</button>
          <button class="btn btn-outline" on:click={nextPage} disabled={page + 1 >= totalPages}>Next ➡️</button>
        </div>
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Page <b>{page + 1}</b> of <b>{totalPages}</b> | Total reservations: <b>{totalReservations}</b>
        </div>
      </div>
    </div>
  </div>
    <NewReservation />

  <div id="results" class="w-full max-w-4xl mx-auto mt-6"></div>

  {#if loading}
    <LoadingOverlay />
  {/if}

  {#if reservations.length === 0 && !loading}
    Nema nadjenih rezervacija
  {:else}
    <div
      class="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-5 gap-8 p-4"
    >
      {#each reservations as reservation, i}
        <ReservationCard {reservation} />
       {/each}
    </div>
  {/if}
{/if}