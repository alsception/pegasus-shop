<script lang="ts">
  import { onMount } from "svelte";
  import { get } from "svelte/store";
  import { auth } from "../../core/services/SessionStore";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import ReservationCard from "./ReservationCard.svelte";
  import Login from "../../core/auth/Login.svelte";
  import type { Reservation } from "./Reservation";
  import NewReservation from "./NewReservation.svelte";
  import SearchForm from "./SearchReservations.svelte";
  import NewReservationModal from "./NewReservationModal.svelte";

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
  let showCreateModal = false;


  // Auth subscription
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  // Fetch reservations on mount
  onMount(() => {
    //fetchReservations();
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
  
  function openCreateModal() 
  {
    showCreateModal = true;
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else if error}
  <ErrorDiv {error} />
{:else}

  <button
    on:click={openCreateModal}
    class="btn btn-dash"
  >       
      <i class="fas fa-plus"></i><i class="fas fa-table"></i>
      Nova rezervacija
  </button>

  <SearchForm/>
  
{/if}

<NewReservationModal
    isOpen={showCreateModal}
    on:close={() => {showCreateModal = false;}}
  />