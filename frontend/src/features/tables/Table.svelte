<script lang="ts">
  import type { PGSTable } from "./PGSTable";
  import OpenTableButton from "./OpenTableButton.svelte";
  export let table: PGSTable;



  function getBgClass(table: PGSTable): string 
  {   

    if(table.occupied === true)
    {
      return "bg-orange-700/80 dark:bg-orange-900/80 "
    }
    else
    {
      return "bg-green-700/80 dark:bg-green-700/80";
    }    

  }

</script>

<div class="m-2
   bg-base-200 {getBgClass(table)}
   rounded-xl shadow p-8
   grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6
 hover:outline-blue-500 hover:outline-2
   ">

   <div class=" flex flex-col gap-2 w-full items-center justify-center ">
      
      <div class="font-bold text-6xl text-primary mr-auto">
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

   <div class="flex flex-col justify-between items-center h-full min-h-[150px] p-2">
      <div class="w-full flex justify-center">
         {#if table.reserved}
         <span class="badge badge-error badge-lg bg-yellow-300 text-black font-bold "
               style="position: relative; top:-2rem;">
         REZERVIRANO
         </span>
         {/if}
      </div>
      <div class="w-full flex justify-end">
         <OpenTableButton {table}/>
      </div>
   </div>
</div>

