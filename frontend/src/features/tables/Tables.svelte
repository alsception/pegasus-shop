<script lang="ts">
  import { onMount } from "svelte";
  import { get } from "svelte/store";
  import { auth } from "../../core/services/SessionStore";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import TableCard from "./Table.svelte";
  import Login from "../../core/auth/Login.svelte";
  import type { PGSTable } from "./PGSTable";

  document.title = "Tables | Pegasus";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  // State variables
  let isAuthenticated = false;
  let tables: PGSTable[] = [];
  let loading = false;
  let error: string | null = null;
  let page = 0;
  let size = 20;
  let totalTables = 0;
  let totalPages = 0;

  // Auth subscription
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  // Fetch tables on mount
  onMount(() => {
    fetchTables();
  });

  async function fetchTables() {
    loading = true;
    error = null;
    try {
      const token = $auth.token;
      const res = await fetch(
        API_BASE_URL + `/tables?page=${page}&size=${size}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (!res.ok) throw new Error(`Fetch error: ${res.status}`);
      const data = await res.json();
      tables = data.tables ?? data; // support both paginated and plain
      totalTables = data.totalCount ?? tables.length;
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
      fetchTables();
    }
  }
  function prevPage() {
    if (page > 0) {
      page -= 1;
      fetchTables();
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
          Page <b>{page + 1}</b> of <b>{totalPages}</b> | Total tables: <b>{totalTables}</b>
        </div>
      </div>
    </div>
  </div>

  <div id="results" class="w-full max-w-4xl mx-auto mt-6"></div>

  {#if loading}
    <LoadingOverlay />
  {/if}

  {#if tables.length === 0 && !loading}
    No tables found :/
  {:else}
    <div
      class="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-5 gap-8 p-4"
    >
      {#each tables as table, i}
        <TableCard {table} />
      {/each}
    </div>
  {/if}
{/if}

<style>
  .pgs-th {
    color: white;
  }
</style>
