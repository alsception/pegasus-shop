<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { get } from "svelte/store";
  import { auth, getCurrentRole, isAdmin } from "../../core/services/SessionStore";
  import { formatDate, formatPrice } from "../../utils/formatting";
  import { formatCommentInfo } from "../../utils/formatting";
  import type { PGSArtikal } from "./Artikal";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import api from "../../core/services/client";
  import { showErrorModalWithTitle } from "../../utils/modal";


  document.title = "Artikli | Pegasus";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  let isAuthenticated = false;
  let artikli: PGSArtikal[] = [];
  let loading: boolean = false;
  let error: string | null = null;
  let isListView = false;
  let isAdminView = true;
  let isDark = true;
  let page = 0;
  let size = 20;
  let totalProducts = 0;
  let totalPages = 0;

  // Sort state
  let sortKey = '';
  let sortAsc = true;

  // Reactive sort headers
  $: nameHeader = sortKey === 'name'
    ? sortAsc ? 'ARTIKAL ▼' : 'ARTIKAL ▲'
    : 'ARTIKAL&nbsp;&nbsp;';

  $: kategorijaHeader = sortKey === 'kategorija'
    ? sortAsc ? 'KATEGORIJA ▼' : 'KATEGORIJA ▲'
    : 'KATEGORIJA&nbsp;&nbsp;';

  $: price1Header = sortKey === 'price1'
    ? sortAsc ? 'Prodajna<br>cijena ▼' : 'Prodajna<br>cijena ▲'
    : 'Prodajna<br>cijena&nbsp;&nbsp;';

  $: price2Header = sortKey === 'price2'
    ? sortAsc ? 'Nabavna<br>cijena 1 ▼' : 'Nabavna<br>cijena 1 ▲'
    : 'Nabavna<br>cijena 1&nbsp;&nbsp;';

  $: price3Header = sortKey === 'price3'
    ? sortAsc ? 'Nabavna<br>cijena 2 ▼' : 'Nabavna<br>cijena 2 ▲'
    : 'Nabavna<br>cijena 2&nbsp;&nbsp;';

  $: price4Header = sortKey === 'price4'
    ? sortAsc ? 'Nabavna<br>cijena 3 ▼' : 'Nabavna<br>cijena 3 ▲'
    : 'Nabavna<br>cijena 3&nbsp;&nbsp;';

  $: stokHeader = sortKey === 'stok'
    ? sortAsc ? 'Stok ▼' : 'Stok ▲'
    : 'Stok&nbsp;&nbsp;';

  $: createdHeader = sortKey === 'created'
    ? sortAsc ? 'DATUM<br>UPISA ▼' : 'DATUM<br>UPISA ▲'
    : 'DATUM<br>UPISA&nbsp;&nbsp;';

  $: updatedHeader = sortKey === 'updated'
    ? sortAsc ? 'ZADNJA<br>IZMJENA ▼' : 'ZADNJA<br>IZMJENA ▲'
    : 'ZADNJA<br>IZMJENA&nbsp;&nbsp;';

  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

  function isActiveHeader(name: string) {
    return sortKey === name;
  }

  function sortBy(key: keyof PGSArtikal) {
    if (sortKey === key) {
      sortAsc = !sortAsc;
    } else {
      sortKey = key;
      sortAsc = true;
    }

    artikli = [...artikli].sort((a, b) => {
      let valA: string;
      let valB: string;

      // Specijalan slucaj za nested objekat kategorija
      if (key === 'kategorija') {
        valA = a.kategorija?.name ?? '';
        valB = b.kategorija?.name ?? '';
      } else {
        valA = String(a[key] ?? '');
        valB = String(b[key] ?? '');
      }

      // Numericki sort za price polja
      if (['price1', 'price2', 'price3', 'price4'].includes(key as string)) {
        const numA = parseFloat(valA) || 0;
        const numB = parseFloat(valB) || 0;
        return sortAsc ? numA - numB : numB - numA;
      }

      return sortAsc ? valA.localeCompare(valB) : valB.localeCompare(valA);
    });
  }

  function checkDarkmode() {
    const check = () => (isDark = document.body.classList.contains("dark"));
    check();
    const observer = new MutationObserver(check);
    observer.observe(document.body, { attributes: true, attributeFilter: ["class"] });
    return () => observer.disconnect();
  }

  function checkListViewParam() {
    let search = window.location.search;
    if (!search && window.location.hash.includes("?")) {
      search = window.location.hash.substring(window.location.hash.indexOf("?"));
    }
    const params = new URLSearchParams(search);
    let listViewParam = params.get("listview");
    if (listViewParam !== null) isListView = listViewParam === "true";
    listViewParam = params.get("listView");
    if (listViewParam !== null) isListView = listViewParam === "true";
  }

  onMount(async () => {
    checkDarkmode();
    checkListViewParam();
    try {
      const { isAuthenticated } = get(auth);
      isAdminView = isAdmin();
      if (!isAuthenticated) {
        error = "Session expired. Please login again.";
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
    event.preventDefault();
    page = 0;
    handleSearch();
  }

  async function handleSearch() {
    const token = $auth.token;
    loading = true;
    try {
      let kategorijaId = "";
      if (searchTerm != null) {
        switch (searchTerm.toLowerCase()) {
          case 'pivo': case 'vopi': case 'vops': kategorijaId = '3'; break;
          case 'rakije': case 'likeri': case 'rakije i likeri': kategorijaId = '4'; break;
          case 'vino': kategorijaId = '5'; break;
          case 'bezalkoholna pića': kategorijaId = '2'; break;
          case 'alkoholna pića': kategorijaId = '1'; break;
          default: kategorijaId = '';
        }
      }

      const res = await fetch(
        API_BASE_URL + `/artikli?search=${searchTerm}&kategorijaId=${kategorijaId}&size=${size}`,
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
      artikli = data;
      totalProducts = data.totalCount;
      totalPages = data.totalPages;

      // Reset sorta pri svakom novom searchu, za sad netreba
      /* sortKey = '';
      sortAsc = true; */

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

  function toggleView() {
    isListView = !isListView;
  }

  function handleCategorySelect(category: string): void {
    console.log('selected category: ' + category);
  }

  function getArtikalColor(kategorija: string | undefined): string 
  {
    if (kategorija == undefined) return "secondary ";
    switch (kategorija.toLowerCase()) 
    {
      case "vino": return "error ";
      case "bezalkoholna pića": return "success ";
      case "alkoholna pića": return "info ";
      case "pivo": return "warning ";
      case "rakije i likeri": return "accent ";
      default: return "secondary ";
    }
  }

  function getArtikalLabel(kategorija: string | undefined): string 
  {
    if (kategorija == undefined) return " ";
    switch (kategorija.toLowerCase()) 
    {      
      case "bezalkoholna pića": return "bezalk. pića";
      default: return kategorija.toUpperCase();
    }
  }

  function isMax(artikal: PGSArtikal, price: number)
  {
    if(price == null || price==0) 
      return false;
    
    return (
        
        price >= artikal.price2 &&
        price >= artikal.price3 &&
        price >= artikal.price4)      
  }

  function isMin(artikal: PGSArtikal, price: number)
  {
    if(price == null || price==0) 
      return false;

    return (
      
      price <= artikal.price2 &&
      price <= artikal.price3 &&
      price <= artikal.price4)      
  }

  function getClass(artikal: PGSArtikal, price: number)
  {
    if( isMin(artikal,price) )
    {
      return "success font-bold";
    }
    else if( isMax(artikal,price) )
    {
      return "error ";
    }
    else
    {
      return ""
    }

  }

  function deleteDialog(id: number)
  {
    if (confirm("Jeste li sigurni?") == true) 
    {
      deleteItem(id);      
    } 
  }

  async function executeDelete(id: number) 
  {
    return api("/artikli/"+id, {
      method: "DELETE",
    });
  }

  async function deleteItem(id: number) 
  {
    try 
    {
      await executeDelete(id);
      //We just assume its deleted if no error happens...
      handleSearch();      
    } 
    catch (error) 
    {
      showErrorModalWithTitle("Greška prilikom brisanja artikla", error);
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
        <form on:submit|preventDefault={handleFormSubmit} class="flex flex-col gap-3">
          <div class="flex gap-2">
            <input
              type="text"
              bind:value={searchTerm}
              placeholder="Traži artikal..."
              class="input input-secondary border-2 flex-1"
            />
            <button type="submit" class="btn btn-dash">
              <i class="fas fa-search"></i>
              <span class="hidden sm:inline ml-1">Traži</span>
            </button>
          </div>

          <div class="flex gap-2 items-center">
            {#if getCurrentRole() === 'ADMIN'}
              <button
                type="button"
                on:click={() => window.location.href = '#/artikli/0'}
                class="btn btn-dash flex-1 lg:flex-none whitespace-nowrap"
              >
                <i class="fas fa-plus"></i>
                Dodaj novi
              </button>
            {/if}

            <div class="text-xs sm:text-sm text-gray-600 dark:text-gray-400 whitespace-nowrap">
              <span class="md:inline"> | Ukupno nadjeno artikala: <b>{artikli.length}</b></span>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div id="results" class="w-full max-w-4xl mx-auto mt-4"></div>

    {#if loading}
      <LoadingOverlay />
    {/if}

    <div class="w-full max-w-[1580px] overflow-x-auto rounded-lg align-middle mx-auto">
      <table class="table table-zebra min-w-[1200px] divide-y divide-accent">
        <thead class="bg-primary/10 sticky top-0">
          <tr class="h-12 sticky top-0 z-20 bg-base-100">
            <th class="pgs-th-l cursor-pointer sticky top-0 z-20 bg-base-200"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('name')}
              class:dark\:bg-slate-700={isActiveHeader('name')} 
              on:click={() => sortBy('name')}>
              {@html nameHeader}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('kategorija')}
              on:click={() => sortBy('kategorija')}>
              {@html kategorijaHeader}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('price1')}
              on:click={() => sortBy('price1')}>
              {@html price1Header}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('price2')}
              on:click={() => sortBy('price2')}>
              {@html price2Header}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('price3')}
              on:click={() => sortBy('price3')}>
              {@html price3Header}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('price4')}
              on:click={() => sortBy('price4')}>
              {@html price4Header}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('stok')}
              on:click={() => sortBy('stok')}>
              {@html stokHeader}
            </th>
            <th class="pgs-th-l"></th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('created')}
              on:click={() => sortBy('created')}>
              {@html createdHeader}
            </th>
            <th class="pgs-th-l cursor-pointer"
              title="Klikni za sortiranje"
              class:pgs-gradient-text={isActiveHeader('updated')}
              on:click={() => sortBy('updated')}>
              {@html updatedHeader}
            </th>
            <th class="pgs-th"></th>
          </tr>
        </thead>
        <tbody>
          {#each artikli as artikal, i}
            <tr class="bg-base-200/60 tr-highlight">
              <td class="pgs-td whitespace-nowrap">
                <a use:link href="/artikli/{artikal.id}" class=" pgs-hyperlink text-primary/70">{artikal.name}</a>
              </td>
              <td class="pgs-td">
                <span class="badge badge-soft badge-{getArtikalColor(artikal.kategorija?.name)} font-mono badge-md" style="text-transform: uppercase;">
                  {getArtikalLabel(artikal.kategorija?.name)}
                </span>
              </td>
              <td class="">{formatPrice(artikal.price1)}</td>
              <td class="text-{getClass(artikal,artikal.price2)} font-mono ">{formatPrice(artikal.price2)}</td>
              <td class="text-{getClass(artikal,artikal.price3)} font-mono ">{formatPrice(artikal.price3)}</td>
              <td class="text-{getClass(artikal,artikal.price4)} font-mono ">{formatPrice(artikal.price4)}</td>
              <td class="text-">{artikal.stok}</td>
              <td class="text-center">{@html formatCommentInfo(artikal.napomena)}</td>
              <td class="pgs-td font-mono whitespace-nowrap">{@html formatDate(artikal.created, "new", 15)}</td>
              <td class="pgs-td font-mono whitespace-nowrap">{@html formatDate(artikal.updated, "new", 15)}</td>
              <td class="px-2">
                <div class="flex justify-center items-center gap-2" style="font-size: 14px;">
                  <div class="tooltip tooltip-info group" data-tip="Edit">
                    <a class="px-4" aria-label="Edit" use:link href="/artikli/{artikal.id}">
                      <i class="fas fa-pen text-gray-500 group-hover:text-sky-400 cursor-pointer"></i>
                    </a>
                  </div>
                  <button class="px-4 group" aria-label="Delete" on:click={() => deleteDialog(artikal.id)}>
                    <div class="tooltip tooltip-info" data-tip="Delete">
                      <i class="fas fa-times-circle text-gray-500 group-hover:text-red-400 cursor-pointer"></i>
                    </div>
                  </button>
                </div>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>

  {/if}
{/if}

<style>
  .pgs-th {
    color: white;
  }
  .badge-warning{
    color: rgb(155, 104, 10); /*mora fix jer se nevidi dobro*/
  }
 
</style>