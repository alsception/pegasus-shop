<script lang="ts">
  import type { PGSTable } from "./PGSTable";
  import OpenTableButton from "./OpenTableButton.svelte";
  export let table: PGSTable;



  function getBgClass(table: PGSTable): string 
  {   

    if(table.occupied === true)
    {
      return "bg-orange-500/30"
    }
    else
    {
      return "bg-green-500/30";
    }    

  }

</script>

<div class="m-2
   bg-base-200 {getBgClass(table)}
   rounded-xl shadow p-8
   grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6 border-- border-red-400">

   <div class=" flex flex-col gap-2 w-full items-center justify-center border-- border-fuchsia-500">
      
      <div class="font-bold text-6xl text-primary mr-auto border-- border-blue-300">
         {table.number}
      </div>

      <div>
      </div>

      <div class="flex flex-col items-start mr-auto mb-4 border-- border-green-400">
         <div class="text-md text-primary/60">
            <i class="fas fa-users mr-1"></i> <b>{table.capacity}</b>
         </div>
         <div class="text-md uppercase font-semibold text-primary/60">
            {table.department}
         </div>
      </div>

      <div class="ml-auto">
      </div>

   </div>

   <div class="flex flex-col justify-between items-center h-full min-h-[150px] border-- border-amber-200 p-2">
      <div class="w-full flex justify-center">
         {#if table.reserved}
         <span class="badge badge-error badge-xl bg-error/20 text-error">
         REZERVIRANO
         </span>
         {/if}
      </div>
      <div class="w-full flex justify-end">
         <OpenTableButton {table}/>
      </div>
   </div>
</div>