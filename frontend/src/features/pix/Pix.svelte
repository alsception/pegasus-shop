<script lang="ts">
  import { onMount } from "svelte";
  import { type UnsplashPhoto } from "./UnsplashPhoto";

  let query = "random";
  let page = 1;
  let perPage = 10;
  let maxResults = 0;
  let numResults = 0;
  let totalPages = 0;
  let ratelimitremaining: string;
  let ratelimit = 50;
  let isListView = false;
  let isLoading = false;
  let fetchTime = "";
  let photos: UnsplashPhoto[];

  // Split the array into 4 columns
  let columns: UnsplashPhoto[][] = [];

  //TODO: try /me

  function searchBackgrounds(query: string) {
    try {
      /* todo: this should not be in code
      alternative &client_id=2-W_8P7fziKPjfsP5s7XbdniZNSMViqv_zj6mG_-N-c */
      const start = performance.now();
      isLoading = true;
      fetch(
        `https://api.unsplash.com/search/photos?` +
          `query=` +
          query +
          `&per_page=` +
          perPage +
          `&page=` +
          page,
        {
          method: "GET",
          headers: {
            Authorization: `Client-ID ${import.meta.env.VITE_UNSPLASH_CLIENT_ID}`,
            "Content-Type": "application/json",
          },
        }
      )
        .then((response) => {
          fetchTime = ((performance.now() - start) / 1000).toFixed(2);

          // Ispis svih zaglavlja
          for (let [key, value] of response.headers.entries()) {
            if (key === "x-ratelimit-remaining") {
              ratelimitremaining = value;
            }
          }

          // Ako želite dalje raditi sa sadržajem odgovora
          return response.json(); // ili response.text(), response.blob() itd.
        })
        .then((data) => {
          photos = data.results;
          totalPages = data.total_pages;
          numResults = data.results.length;
          columns = [[], [], [], []];
          photos.forEach((photo, index) => {
            columns[index % 4].push(photo);
          });
        })
        .catch((err) => {
          console.error("Error:", err);
        })
        .finally(() => {
          isLoading = false;
        });
    } catch (err) {
    } finally {
    }
  }

  onMount(async () => {
    try {
      searchBackgrounds(query);
    } catch (err) {
      console.log(err);
    } finally {
    }
  });

  function toggleView() {
    isListView = !isListView;
  }

  function formatHumanDate(dateString: string | number | Date) {
    const date = new Date(dateString);
    return (
      /*date.toDateString()*/ date.toLocaleDateString("sr-RS") +
      " " +
      date.toTimeString().split(" ")[0]
    );
  }
  function formatHumanDateYo(dateString: string | number | Date) {
    const date = new Date(dateString);
    return date.getFullYear();
  }

  function nextPage() {
    if (page < totalPages) {
      page += 1;
      searchBackgrounds(query);
    }
  }

  function prevPage() {
    if (page > 1) {
      page -= 1;
      searchBackgrounds(query);
    }
  }
</script>

<div
  class="w-full max-w-4xl mx-auto p-4 bg-white dark:bg-slate-900 rounded-lg min-w-[1024px]"
>
  <form
    on:submit|preventDefault={() => searchBackgrounds(query)}
    class="flex flex-wrap items-center gap-3"
  >
    <input
      type="text"
      bind:value={query}
      placeholder="Search pictures..."
      class="input input-accent"
    />
    <button type="submit" class="btn btn-primary">
      {#if isLoading}
        <i class="fas fa-spinner fa-spin"></i>
        Loading
      {:else}
        <i class="fas fa-search"></i>
        &nbsp;Search
      {/if}
    </button>
   
    <label for="perPage" class="font-extralight">Results per page</label>
    <input
      type="number"
      min="1"
      max="30"
      bind:value={perPage}
      placeholder="Per page"
      class="input w-20"
    />

    <button class="btn" on:click={toggleView}>
      <i class="fas fa-th-list"></i>
      {#if isListView}
        List view
      {:else}
        Grid view
      {/if}
    </button>
  </form>

  {#if photos !== null}
    

  <div class="flex items-center gap-2 mt-4">
    <button class="btn btn-ghost text-4xl" on:click={prevPage} disabled={page <= 1} aria-label="Previous page">
      ←
    </button>
    <span class="text-sm text-gray-600 dark:text-gray-400">
      Page <b><input
        type="number"
        min="1"
        max={totalPages}
        bind:value={page}
        placeholder="Page"
        class="input w-16 text-center"
      /></b> of <b>{totalPages}</b>
    </span>
    <button class="btn btn-ghost text-4xl" on:click={nextPage} disabled={page >= totalPages} aria-label="Next page">
      →
    </button>
  </div>
<div class="mt-4 text-sm text-gray-400 dark:text-gray-300">
      {numResults} results fetched in {fetchTime} seconds, page {page} of {totalPages}
      &nbsp; &nbsp; &nbsp; Rate limit remaining: {ratelimitremaining} of {ratelimit}
    </div>
  {/if}

</div>

{#if isListView}
  <!-- flex or grid
 -->
  <div
    class=" grid-cols-1 items-center justify-center min-h-screen gap-6 p-4 flex w-fit"
  >
    {#each photos as photo}
      <div
        class="bg-white dark:bg-slate-900 rounded-xl overflow-hidden flex flex-col max-w-[384px]"
      >
        <img
          class="w-full object-cover"
          src={photo.urls.regular}
          alt={photo.alt_description}
        />
        <div class="p-4">
          <h3
            class="font-semibold text-lg truncate"
            title={photo.description ?? photo.alt_description}
          >
            {photo.description ?? photo.alt_description}
          </h3>
          <p class="text-sm text-gray-600 dark:text-gray-400 mt-1">
            By {photo.user.name}
          </p>
          <p class="text-sm text-gray-500 dark:text-gray-500 mt-1">
            ❤️ {photo.likes}
          </p>
          <p class="text-sm text-gray-400 dark:text-gray-600 mt-1">
            {photo.created_at}
          </p>

          <a
            href={photo.urls.full}
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-300 underline">full</a
          >
          <a
            href={photo.urls.raw}
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-300 underline">raw</a
          >
          <a
            href={photo.urls.regular}
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-300 underline">regular</a
          >
          <a
            href={photo.urls.small}
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-300 underline">small</a
          >
          <a
            href={photo.urls.small_s3}
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-300 underline">small_s3</a
          >
          <a
            href={photo.urls.thumb}
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-300 underline">thumb</a
          >
        </div>
      </div>
    {/each}
  </div>
{:else}
  <div
    class="flex justify-center gap-4 p-4 max-w-[1568px] images-container-center"
  >
    {#each columns as column}
      <div class="flex-1 space-y-4">
        {#each column as photo}
          <div
            class="bg-white dark:bg-slate-900 rounded-xl overflow-hidden"
          >
            <img
              class="w-full object-cover"
              src={photo.urls.small}
              alt={photo.alt_description}
            />
            <div class="p-4 max-w-[384px]">
              <h3
                class="font-semibold text-lg truncate"
                title={photo.description ?? photo.alt_description}
              >
                {photo.description ?? photo.alt_description}
              </h3>
              <p
                class="text-sm text-gray-600 mt-1"
                title={formatHumanDate(photo.created_at)}
              >
                By {photo.user.name}, {formatHumanDateYo(photo.created_at)}
              </p>
              <p class="text-sm text-gray-500 mt-1">❤️ {photo.likes}</p>
              <p class="text-sm text-gray-500 mt-1">links:</p>

              <div class="tooltip tooltip-info" data-tip={photo.urls.full}>
                <a
                  href={photo.urls.full}
                  target="_blank"
                  rel="noopener noreferrer"
                  class="text-blue-400 hover:text-blue-300 underline">full</a
                >
              </div>

              <a
                href={photo.urls.raw}
                target="_blank"
                rel="noopener noreferrer"
                class="text-blue-400 hover:text-blue-300 underline">raw</a
              >
              <a
                href={photo.urls.regular}
                target="_blank"
                rel="noopener noreferrer"
                class="text-blue-400 hover:text-blue-300 underline">regular</a
              >
              <a
                href={photo.urls.small}
                target="_blank"
                rel="noopener noreferrer"
                class="text-blue-400 hover:text-blue-300 underline">small</a
              >
              <a
                href={photo.urls.small_s3}
                target="_blank"
                rel="noopener noreferrer"
                class="text-blue-400 hover:text-blue-300 underline">small_s3</a
              >
              <a
                href={photo.urls.thumb}
                target="_blank"
                rel="noopener noreferrer"
                class="text-blue-400 hover:text-blue-300 underline">thumb</a
              >
            </div>
          </div>
        {/each}
      </div>
    {/each}
  </div>
{/if}

<style>
  img {
    max-width: 100%;
    border-radius: 4px;
  }

  .images-container-center {
    max-width: 2000px;
    margin: 0 auto; /* Centers horizontally */
    padding: 20px; /* Optional: add some padding */

    /* Your existing grid/flex properties */
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 16px;
  }
  a {
    color: gray;
    text-decoration: none;
    font-size: medium;
  }
  a:hover {
    color: cornflowerblue;
  }
</style>
