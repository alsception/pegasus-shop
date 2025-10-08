<script lang="ts">
  import type { PGSTable } from "./PGSTable";
  import OpenTableButton from "./OpenTableButton.svelte";
  export let table: PGSTable;
</script>

<div class="bg-white dark:bg-slate-900 rounded-xl shadow p-4 flex flex-col gap-2 max-w-sm">
  <div class="font-bold text-lg text-primary">Table #{table.number}</div>
  <div class="text-sm text-gray-600 dark:text-gray-400"><i class="fas fa-users mr-1"></i> <b>{table.capacity}</b></div>
  <div class="text-sm">Position: {table.position}</div>
  <div class="text-sm">Department: {table.department}</div>
  <div class="text-sm">Rayon: {table.rayon}</div>
  <OpenTableButton></OpenTableButton>
</div>