<script lang="ts">
  import errorImg from "../../../assets/img/503-error-man.svg";

  export let error: string | null = null;

  console.log("hello error div");
  console.log(error);

  const isFetchError = (error?.toLowerCase().includes("fetch") || error?.toLowerCase().includes("network")) ?? false;
</script>

<div
  class="flex flex-col justify-center items-center h-full text-2xl mt-4 text-center"
>
  {#if isFetchError}
    <img src={errorImg} alt="Fetch error illustration" class="max-w-128" />
    <!-- <div class="nb-marquee" style="width: 768px;">
      <div class="nb-marquee-content"> -->
        <h1>⛔ Error: {error} ⛔</h1>
      <!-- </div>
    </div> -->
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
