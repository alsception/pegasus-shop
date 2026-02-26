<script lang="ts">
  import errorImg from "../../../assets/img/503-error-man.svg";

  export let error: string | null = null;

  const isErr500 = error?.toLowerCase().includes("500") ?? false;
  const isFetchError = (error?.toLowerCase().includes("fetch") || error?.toLowerCase().includes("network")) ?? false;
  const is404 = (error?.toLowerCase().includes("404") || error?.toLowerCase().includes("NOT FOUND")) ?? false;

</script>

<div
  class="flex flex-col justify-center items-center h-full text-2xl mt-4 text-center wrap-anywhere"
>
  {#if isErr500}
  
    <img src={errorImg} alt="Fetch error illustration" class="max-w-128" />
    
    <p class="text-red-500 text-xl mt-8 text-center dark:text-red-400 bg-error-content p-2">
        {error}
    </p>

  {:else if isFetchError}
    
    <p class="text-red-500 text-xl mt-8 text-center dark:text-red-400 bg-error-content p-2">
        {error}
    </p>

  {:else if is404}
    
    <p class="text-primary/80 text-3xl mt-8 text-centerp-2">
        {error}
    </p>
    <p class="text-primary/60 text-xl mt-8 text-centerp-2">
        Traženi resurs nije pronađen
    </p>

  {:else}

    <p class="text-red-500 text-xl mt-8 text-center dark:text-red-400 bg-error-content p-2">
        {error}
    </p>

  {/if}

</div>