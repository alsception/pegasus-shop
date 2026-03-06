<script lang="ts">
  import type { PGSTable } from "./PGSTable";
  import OpenTableButton from "./OpenTableButton.svelte";
  import { fly } from "svelte/transition";
  export let table: PGSTable;

  function getBgClass(table: PGSTable): string {
    if (table.occupied === true) {
      return "bg-red-400/80 dark:bg-red-900/80 ";
    } else {
      return "bg-green-400/80 dark:bg-green-900/70";
    }
  }
</script>

<div
  class="m-2
   bg-base-200 {getBgClass(table)}
   rounded-xl shadow p-2
   grid grid-cols-1 md:grid-cols-1 lg:grid-cols-1 gap-1
 hover:outline-blue-500 hover:outline-2
   "
>
  <div class=" flex flex-col gap-2 w-full items-center justify-center">
    <div class="font-bold text-6xl text-primary mr-auto">
      {table.number}
    </div>
  </div>

  <div class="flex flex-col justify-between items-center h-full p-0">
    <div class="w-full flex justify-center">
      {#if table.reserved}
        <span
          class="badge badge-error badge-lg bg-yellow-300 text-black font-bold"
          style="position: relative; top:-6rem;"
        >
          REZERVIRANO
        </span>
      {/if}
    </div>
    <div class="w-full flex justify-end">
      <OpenTableButton {table} />
    </div>
  </div>
</div>
