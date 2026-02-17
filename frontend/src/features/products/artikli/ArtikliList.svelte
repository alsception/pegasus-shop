<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { get } from "svelte/store";
  import { auth, getCurrentRole, isAdmin } from "../../../core/services/SessionStore";
  import { formatDate, formatPrice } from "../../../utils/formatting";
  import { formatActive } from "../../../utils/formatting";
  import { formatCommentInfo } from "../../../utils/formatting";
  import type { PGSArtikal } from "./Artikal";
  import Login from "../../../core/auth/Login.svelte";
  import LoadingOverlay from "../../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../../core/navigation/error/ErrorDiv.svelte";

  document.title = "Artikli | Pegasus";
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  //DEFINITIONS
  let isAuthenticated = false;
  let artikli: PGSArtikal[] = [];
  let loading: boolean = false;
  let error: string | null = null;
  let isListView = false;
  let isAdminView = true;
  let isDark = true;
  let page = 0;
  let size = 20; // Products per page
  let totalProducts = 0; // If backend returns total count
  let totalPages = 0; // Total pages from backend
  let selectedCategory: any;

  // AUTHENTICATION
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  function checkDarkmode() {
    //Ovo proverava dali je darkmode classa dodeljena na body
    //Jer treba zbog nekih elemenata koji se drugacije prikazuju

    const check = () => (isDark = document.body.classList.contains("dark"));
    check();

    const observer = new MutationObserver(check);
    observer.observe(document.body, {
      attributes: true,
      attributeFilter: ["class"],
    });

    return () => observer.disconnect();
  }

  /**
   * Apparently, the query param ?listview=true is not accessible in the window.location.search
   * but in window.location.hash (spa logic), so we need to use this ai slop to make it work...
   */
  function checkListViewParam() {
    // Support hash-based routing: extract query params from hash if present
    let search = window.location.search;
    if (!search && window.location.hash.includes("?")) {
      search = window.location.hash.substring(
        window.location.hash.indexOf("?")
      );
    }
    const params = new URLSearchParams(search);
    // Use lowercase 'listview' for consistency
    let listViewParam = params.get("listview");
    if (listViewParam !== null) {
      isListView = listViewParam === "true";
    }
    listViewParam = params.get("listView");
    if (listViewParam !== null) {
      isListView = listViewParam === "true";
    }
  }

  // Fetch the products from the backend
  onMount(async () => {
    checkDarkmode();
    checkListViewParam();

    try {
      const { isAuthenticated } = get(auth);
      isAdminView = isAdmin();
      /**
       * TODO: this is not actually working
       */

      if (!isAuthenticated) {
        error = "Session expired. Please login again.";
        // goto('/login?return=' + encodeURIComponent(currentPath));
        return;
      } else {
        handleSearch();
      }
    } catch (err) {
      error = err instanceof Error ? err.message : "Search failed";
    }
  });

  let searchTerm = "";

  function handleFormSubmit(event: { preventDefault: () => void }) {
    event.preventDefault(); // prevent page reload
    page = 0; // Reset to first page on new search
    handleSearch();
  }

  async function handleSearch() {
    const token = $auth.token;
    loading = true;
    try 
    {

      let kategorijaId = "";

      if(searchTerm!=null)
      {
        switch (searchTerm.toLowerCase()) {
        case 'pivo':
        case 'vopi':
        case 'vops':
          kategorijaId = '3';
          break;
        case 'rakije':
        case 'likeri':
        case 'rakije i likeri':
          kategorijaId = '4';
          break;  
        case 'vino':
          kategorijaId = '5';
          break;
        case 'bezalkoholna pića':
          kategorijaId = '2';
          break;
        case 'alkoholna pića':
          kategorijaId = '1';
          break;  
        default:
          // Opciono: šta se dešava ako se ništa ne poklopi
          kategorijaId = ''; 
      }
      }

      const res = await fetch(
        API_BASE_URL +
          `/artikli?search=${searchTerm}&kategorijaId=${kategorijaId}&size=${size}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!res.ok) {
        if (res.status === 401) {
          localStorage.removeItem("token");
          auth.set({ token: null, isAuthenticated: false });
          throw new Error("Authentication failed");
        }
        throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
      }

      const data = await res.json();

      // Use the paginated response structure
      artikli = data;
      totalProducts = data.totalCount;
      totalPages = data.totalPages;
    } catch (err: any) {
      console.error(error);
      if (err.message.includes("401")) {
        $auth.token = null;
      } else {
        error = err instanceof Error ? err.message : "Unknown error";
      }
    } finally {
      loading = false;
    }
  }

  function nextPage() {
    if ((page + 1) * size < totalProducts) {
      page += 1;
      handleSearch();
    }
  }

  function prevPage() {
    if (page > 0) {
      page -= 1;
      handleSearch();
    }
  }
/* 
  function closeModal(): void {
    modalProduct = null;
  } */

  function toggleView() {
    isListView = !isListView;
  }

  function handleCategorySelect(category: string): void 
  {
    console.log('selected category: '+category);
  }

  function getArtikalColor(kategorija: string | undefined): string 
  {
    if(kategorija == undefined)
    {
      return "secondary ";
    }    
    else
    {
      switch (kategorija.toLowerCase()) 
      {
        case "vino":
          return "error ";

        case "bezalkoholna pića":
          return "success ";

        case "alkoholna pića":
          return "info ";

        case "pivo":
          return "warning ";
        
        case "rakije i likeri":
          return "accent ";

        default:
          return "secondary "; // ili neka podrazumevana boja
      }
    }
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}  
  {#if error}
    <ErrorDiv {error} />
  {:else}
      
    <div class="w-full flex justify-center px-4">
      <div class="w-full max-w-4xl p-4 bg-base-200 rounded-lg">
        <form
          on:submit|preventDefault={handleFormSubmit}
          class="flex flex-col gap-3"
        >
          <!-- Gornji red: Input i Traži dugme -->
          <div class="flex gap-2">
            <input
              type="text"
              bind:value={searchTerm}
              placeholder="Traži artikal..."
              class="input input-primary dark:input-info border-2 flex-1"
            />
            <button type="submit" class="btn btn-dash">
              <i class="fas fa-search"></i>
              <span class="hidden sm:inline ml-1">Traži</span>
            </button>
          </div>

          <!-- Donji red: Kategorije, Pagination i View toggle -->
          <div class="flex gap-2 items-center">
            
           <!--  <div class="flex-1">
              <ProductCategories bind:selectedCategory onSelect={handleCategorySelect} />
            </div>
 -->
          {#if getCurrentRole() === 'ADMIN'}
            <button
              type="button"
              on:click={() => window.location.href = '#/products/mngmt/0'}
              class="btn btn-dash flex-1 lg:flex-none whitespace-nowrap"
            >       
              <i class="fas fa-plus"></i>
              Dodaj novi
            </button>
      <!--       <a
  href="/novi"
  class="btn btn-dash flex-1 lg:flex-none whitespace-nowrap"
>
  <i class="fas fa-plus"></i>
  Dodaj novi
</a> -->
            {/if}
            
            <div class="text-xs sm:text-sm text-gray-600 dark:text-gray-400 whitespace-nowrap">
              <span class="md:inline"> | Ukupno nadjeno artikala: <b>{artikli.length}</b></span>
            </div>
            
          </div>
        </form>
      </div>
    </div>

    <div id="results" class="w-full max-w-4xl mx-auto mt-16"></div>
 
    {#if loading}
      <LoadingOverlay />
    {/if}

      <div
        class="w-full max-w-[1480px] overflow-x-auto rounded-lg align-middle mx-auto"
      >
        <table class="table table-zebra min-w-[1200px] divide-y divide-accent">
          <thead class="bg-[#10273c]">
            <tr class="h-12">
              <th class="pgs-th-l">Artikal</th>
              <th class="pgs-th-l">Kategorija</th>
              <th class="pgs-th-r">Cijena 1</th>
              <th class="pgs-th-r">Cijena 2</th>
              <th class="pgs-th-r">Cijena 3</th>
              <th class="pgs-th-r">Cijena 4</th>
              <th class="pgs-th-l"></th><!-- prazno za komentar -->
              <th class="pgs-th-l">Datum upisa</th>
              <th class="pgs-th-l">Datum zadnje izmjene</th>
              <th class="pgs-th"></th><!-- Akcije -->
            </tr>
          </thead>
          <tbody class="">
            {#each artikli as artikal, i}
              <tr class="bg-base-200/80 outline-1 outline-transparent /*hover:outline-blue-500*/ hover:bg-base-300/70">            
                <td class="pgs-td whitespace-nowrap">
                  <a
                    use:link
                    href="/artikli/{artikal.id}"
                    class="text-primary pgs-hyperlink">{artikal.name}</a
                  >
                </td>
                <td class="pgs-td">
                  <span class="badge badge-soft badge-{getArtikalColor(artikal.kategorija?.name)} badge-{getArtikalColor(artikal.kategorija?.name)} font-mono badge-sm" style="text-transform: uppercase;">
                    {artikal.kategorija?.name}
                  </span>
                </td>
                <td class="pgs-td-num">{formatPrice(artikal.price1)}</td>
                <td class="pgs-td-num">{formatPrice(artikal.price2)}</td>
                <td class="pgs-td-num">{formatPrice(artikal.price3)}</td>
                <td class="pgs-td-num">{formatPrice(artikal.price4)}</td>
                <td class="text-center"
                  >{@html formatCommentInfo(artikal.comment)}</td
                >
                <td class="pgs-td font-mono whitespace-nowrap"
                  >{@html formatDate(artikal.created, "new", 15)}</td
                >
                <td class="pgs-td font-mono whitespace-nowrap"
                  >{@html formatDate(artikal.updated, "new", 15)}</td
                >
              
                <td class="px-2">
                  <div
                    class="flex justify-center items-center gap-2"
                    style="font-size: 14px;"
                  >
                    <div class="tooltip tooltip-info" data-tip="Edit">
                      <a
                        class="px-4"
                        aria-label="Edit"
                        use:link
                        href="/products/mngmt/{artikal.id}"
                      >
                        <i
                          class="fas fa-pen text-gray-500 hover:text-sky-400 cursor-pointer"
                        ></i>
                      </a>
                    </div>
                    <button
                      class="px-4"
                      aria-label="Delete"
                      on:click={() => deleteDialog(artikal.id)}
                    >
                      <div class="tooltip tooltip-info" data-tip="Delete">
                        <i
                          class="fas fa-times-circle text-gray-500 hover:text-red-400 cursor-pointer"
                        ></i>
                      </div>
                    </button>
                  </div>
                </td>
              </tr>
            {/each}
            <!-- <tr class="bg-base-200">
              <td colspan="18" class="pgs-td font-mono h-[64px]">
                Ukupno <b>{artikli.length}</b> artikala na ovoj stranica.<br />
                <b>{totalPages}</b>
              </td>
            </tr> -->
          </tbody>
        </table>
      </div>
  
  {/if}
 
{/if}

<style>
  .pgs-th {
    color: white;
  }
</style>
