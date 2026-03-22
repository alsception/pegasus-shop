<script lang="ts">
  import { type PGSArtikal } from "./Artikal";
  import { onMount } from "svelte";
  import { params, push } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { formatDateTime } from "../../utils/formatting";
  import { showInfoToast, showSuccessToast } from "../../core/utils/toaster";
  import { showErrorModal, showErrorModalWithTitle } from "../../utils/modal";

  let loading: boolean = false;
  let error: string | null = null;
  let ID: number | string;
  const codeLength = 15;

  document.title = "Artikli | Barbacoa";

  $: {
    if ($params?.id) {
      ID = Number($params.id);
      if(ID!=0) fetch(ID); // reactively fetch when id changes, if id=0 backend will create new
    }
  }  

  let formData: Partial<PGSArtikal> = {};

  async function fetch(id: string | number) 
  {
    loading = true;
    try 
    {
      let data = await api<PGSArtikal>("/artikli/" + id, 
      {
        method: "GET",
      });
      formData = data;
    } 
    catch (err) 
    {
      console.error(err)
      error = (err as Error).message;
    } 
    finally 
    {
      loading = false;
    }
  }

  onMount(async () => 
  {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id) 
    {
      fetch(id);
    }
  });

  async function handleSubmit() 
  {
    try 
    {
      await api<PGSArtikal>(`/artikli/${ID}`, 
      {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Sačuvano");
      push('/artikli');
      fetch(ID);
    } 
    catch (err: any) 
    {
      showErrorModal(err.message);
    } 
    finally 
    {
      loading = false;
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === "Enter") {
      event.preventDefault();
      handleSubmit();
    }
  }

  function cancelEditing(event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }) 
  {
    window.location.href = "#/artikli"; // Putanja do početne stranice
  }

  function deleteDialog()
  {
    if (confirm("Jeste li sigurni?") == true) 
    {
      deleteItem();      
    } 
  }

  async function executeDelete() 
  {
    return api("/artikli/"+ID, {
      method: "DELETE",
    });
  }

  async function deleteItem() 
  {
    try 
    {
      await executeDelete();
      //We just assume its deleted if no error happens...
      showInfoToast('Obrisano',0);
      push('/artikli');      
    } 
    catch (error) 
    {
      showErrorModalWithTitle("Greška prilikom brisanja artikla", error);
    }
  }

</script>

<div class="relative w-full h-full scale-up-center-normal">

  {#if !$auth.isAuthenticated}

    <Login />

  {:else if loading}

    <LoadingOverlay />

  {:else if error}

    <ErrorDiv {error} />

  {:else}
    <!-- FIX TODO
    Non-interactive element `<form>` should not be assigned mouse or keyboard event listeners
    https://svelte.dev/e/a11y_no_noninteractive_element_interactionssvelte(a11y_no_non -->

    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
    <form
    on:submit|preventDefault={handleSubmit}
    on:keydown={handleKeydown}
    id="ArtikalForm"
    class="max-w-[100rem] mx-auto bg-base-200 rounded-lg p-8 w-full space-y-8"
  >
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
      <div>
        <h3 class="text-3xl font-semibold text-primary">Artikal</h3>
        <div id="loadingMessage" style="display: none;" class="mt-2">
          <span class="loading loading-dots loading-xs"></span>
        </div>
      </div>
      <div class="flex gap-3">
        <button type="button" on:click={cancelEditing} class="btn btn-outline">
          <i class="fas fa-arrow-left text-primary/60"></i> Nazad</button>
        <button type="button" on:click={deleteDialog} class="btn btn-outline hover:text-error group">
          <i class="fas fa-trash text-primary/60 group-hover:text-error text-xs"></i> Obriši</button>
        <button type="submit" class="btn btn-primary px-8">
          <i class="far fa-save text-primary-content"></i> Spremi</button>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      
      <div class="lg:col-span-7 space-y-8">
        
        <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
          <div class="mb-6">
            <h3 class="text-xl font-semibold text-primary">Osnovne informacije</h3>
            <p class="text-secondary text-sm uppercase tracking-wider">Identifikacija i kategorizacija</p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="md:col-span-2">
              <label for="Artikalname" class="block text-sm font-medium text-secondary mb-2">Ime</label>
              <input id="Artikalname" class="pgs-input w-full" bind:value={formData.name} />
            </div>

            <div>
              <label for="code" class="block text-sm font-medium text-secondary mb-2">Barcode</label>
              <input id="code" type="text" class="pgs-input w-full" maxlength={codeLength} bind:value={formData.barcode} />
            </div>

            <div>
              <label for="department" class="block text-sm font-medium text-secondary mb-2">Kategorija</label>
              <select id="department" class="pgs-input w-full" bind:value={formData.kategorijaId}>
                <option value={1}>Alkoholna pića</option>
                <option value={2}>Bezalkoholna pića</option>
                <option value={3}>Pivo</option>
                <option value={4}>Rakije i likeri</option>
                <option value={5}>Vino</option>
              </select>
            </div>

            <div class="md:col-span-1">
              <label for="proizvodjac" class="block text-sm font-medium text-secondary mb-2">Proizvođač</label>
              <input id="proizvodjac" class="pgs-input w-full" bind:value={formData.proizvodjac} />
            </div>

            <div>
              <label for="dept_select" class="block text-sm font-medium text-secondary mb-2">Odjel</label>
              <select id="dept_select" class="pgs-input w-full" bind:value={formData.odjel}>
                <option value={1}>Kuhinja</option>
                <option value={2}>Bar</option>
              </select>
            </div>

            <div class="md:col-span-2">
              <label for="description" class="block text-sm font-medium text-secondary mb-2">Opis</label>
              <textarea id="description" class="pgs-input w-full resize-vertical" bind:value={formData.opis} rows="4"></textarea>
            </div>
          </div>
        </div>

        <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
          <h3 class="text-xl font-semibold text-primary mb-4">Ostalo</h3>
          <label for="comment" class="block text-sm font-medium text-secondary mb-2">Napomena</label>
          <textarea id="comment" class="pgs-input w-full resize-vertical" bind:value={formData.napomena} rows="3"></textarea>
        </div>
      </div>

      <div class="lg:col-span-5 space-y-8">
        
        <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
          <div class="mb-6 border-b border-neutral/10 pb-2">
            <h3 class="text-xl font-semibold text-primary">Cijene i Porez</h3>
          </div>
          
          <div class="space-y-4">
            <div>
              <label for="currency1" class="block text-sm text-secondary mb-1">Prodajna cijena</label>
              <input id="currency1" type="number" step="0.01" class="pgs-input w-full font-bold text-lg" bind:value={formData.price1} />
            </div>

          <div class="grid grid-cols-1 gap-4 pt-2">
            <div class="flex flex-col lg:flex-row gap-2">
              <div class="flex-1">
                <label for="currency2" class="block text-sm font-medium text-secondary mb-1">Nabavna cijena 1</label>
                <input id="currency2" type="number" step="0.01" 
                  class="pgs-input" style="min-width: fit-content;"
                  bind:value={formData.price2} />
              </div>
              <div class="flex-[2]">
                <label for="supplier1" class="block text-sm font-medium text-secondary mb-1">Dobavljač 1</label>
                <input id="supplier1" type="text" 
                style="min-width: fit-content;"
                class="pgs-input w-fit" 
                bind:value={formData.dobavljac1} />
              </div>
            </div>

            <div class="flex flex-col sm:flex-row gap-2">
              <div class="flex-1">
                <label for="currency3" class="block text-sm font-medium text-secondary mb-1">Nabavna cijena 2</label>
                <input id="currency3" type="number" step="0.01" 
                class="pgs-input w-fit" style="min-width: fit-content;"
                bind:value={formData.price3} />
              </div>
              <div class="flex-[2]">
                <label for="supplier2" class="block text-sm font-medium text-secondary mb-1">Dobavljač 2</label>
                <input id="supplier2" type="text" class="pgs-input w-full" bind:value={formData.dobavljac2} />
              </div>
            </div>

            <div class="flex flex-col sm:flex-row gap-2">
              <div class="flex-1">
                <label for="currency4" class="block text-sm font-medium text-secondary mb-1">Nabavna cijena 3</label>
                <input id="currency4" type="number" step="0.01" 
                class="pgs-input w-fit" style="min-width: fit-content;"
                bind:value={formData.price4} />
              </div>
              <div class="flex-[2]">
                <label for="supplier3" class="block text-sm font-medium text-secondary mb-1">Dobavljač 3</label>
                <input id="supplier3" type="text" class="pgs-input w-full" bind:value={formData.dobavljac3} />
              </div>
            </div>
          </div>

            <div class="grid grid-cols-1 gap-4 border-t border-neutral/10 pt-4 mt-4">
              <div>
                <label for="tax_percent" class="block text-sm font-medium text-secondary mb-1">Porez %</label>
                <input id="tax_percent" type="number" step="0.01" class="pgs-input w-full" bind:value={formData.porez} />
              </div>
              <div>
                <label for="discount" class="block text-sm font-medium text-secondary mb-1">Porezna grupa</label>
                <input id="discount" type="number" class="pgs-input w-full" bind:value={formData.poreznaGrupa} />
              </div>
            </div>
          </div>
        </div>

        <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
          <h3 class="text-xl font-semibold text-primary mb-4">Skladište</h3>
          <div class="space-y-4">
            <div>
              <label for="stock_quantity" class="block text-sm font-medium text-secondary mb-2">Stok količina</label>
              <input id="stock_quantity" type="number" class="pgs-input w-full" bind:value={formData.stok} />
            </div>
           
          </div>
        </div>

      

      </div>
        <div class="p-4 text-[14px] text-secondary flex flex-col lg:flex-row lg:items-center lg:justify-between gap-2 lg:gap-10
                    w-max">
          <div class="flex items-center gap-2">
            <i class="fas fa-calendar-plus text-gray-400"></i> 
            <span>Upisano: <span class="font-mono">{formatDateTime(formData.created)}</span></span>
          </div>
          
          <div class="flex items-center gap-2">
            <i class="fas fa-edit text-gray-400"></i> 
            <span>Izmijenjeno: <span class="font-mono">{formatDateTime(formData.updated)}</span></span>
          </div>
        </div>
    </div>
  </form>
  {/if}
</div>

<style>
  
</style>
