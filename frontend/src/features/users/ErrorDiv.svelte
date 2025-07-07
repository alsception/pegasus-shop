<script lang="ts">
  export let error: string | null = null;
</script>

<div
  class="flex justify-center items-center h-64 text-red-500 text-2xl mt-4 text-center dark:text-red-400"
>
  <h3>Error: {error}</h3>
</div>
