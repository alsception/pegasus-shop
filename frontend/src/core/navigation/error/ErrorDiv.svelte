<script lang="ts">
  import errorImg from "../../../assets/img/503-error-man.svg";

  export let error: string | null = null;

  console.error(error);

  const isErr500 = error?.toLowerCase().includes("500") ?? false;
  const isFetchError = (error?.toLowerCase().includes("fetch") || error?.toLowerCase().includes("network")) ?? false;
</script>

<div
  class="flex flex-col justify-center items-center h-full text-2xl mt-4 text-center"
>
  {#if isErr500}
    <img src={errorImg} alt="Fetch error illustration" class="max-w-128" />
    <h1>⛔ Error: {error} ⛔</h1>
    <h2 style="/*position: relative; top: -70px;*/" class="text-primary mt-4">
      Server error, sorry :(
    </h2>
  {:else if isFetchError}
    <img src={errorImg} alt="Fetch error illustration" class="max-w-128" />
    <h1>⛔ Error: {error} ⛔</h1>
    <h2 style="/*position: relative; top: -70px;*/" class="text-primary mt-4">
      Looks like our server is unavailable or you are offline.
    </h2>
  {:else}
    <h1>⛔ Error: {error} ⛔</h1>
  {/if}

</div>

<style>
  h1 {
    font-size: 2rem;
    font-weight: 900;
    color: #111;
    background: #ffdd00;
    padding: 0.5rem 1.5rem;
    border: 3px solid #111;
    margin: 1rem 0;
    box-shadow: 6px 6px 0 #111;
  }
</style>
