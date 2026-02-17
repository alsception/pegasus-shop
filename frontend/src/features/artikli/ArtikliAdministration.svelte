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
  import { showSuccessToast } from "../../core/utils/toaster";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
/*   let artikal: PGSArtikal | null = null;
 */  let ID: number | string;
  const codeLength = 15;

  document.title = "Artikli | Pegasus";

  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

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
    try {
      let data = await api<PGSArtikal>("/artikli/" + id, 
      {
        method: "GET",
      });
      //samo jedan treba da ostane
      formData = data;
/*       artikal = data;
 */    } catch (err) {
      error = (err as Error).message;
    } finally {
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

  async function handleSubmit() {
    try {
      await api<PGSArtikal>(`/artikli/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Sačuvano");
      push('/artikli');
      //window.location.href = "/#/Artikals";
      fetch(ID);
    } catch (err) {
      alert((err as Error).message);
    } finally {
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
      class="max-w-7xl mx-auto bg-base-200 rounded-lg p-8 w-full space-y-8"
    >
      <!-- Header Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-1">
          <h3 class="text-3xl font-semibold text-primary">Artikal</h3>

          <div
            id="loadingMessage"
            style="display: none;"
            class="text-2xl font-semibold text-gray-700 dark:text-gray-100 flex items-center gap-2 mt-4"
          >
            <span class="loading loading-dots loading-xs"></span>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- General Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Osnovno</h3>
          <p class="text-secondary text-sm mt-2">
              Osnovne informacije o artiklu
            </p>
        </div>
        <div class="lg:col-span-2">
          <div class="">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full md:col-span-2">
                <label
                  for="Artikalname"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Ime</label
                >
                <input
                  id="Artikalname"
                  class="pgs-input"
                  bind:value={formData.name}
                />
              </div>

              <div class="w-full">
                <label
                  for="code"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Barcode</label
                >
                <input
                  id="code"
                  type="text"
                  class="pgs-input"
                  maxlength={codeLength}
                  size={codeLength}
                  bind:value={formData.barcode}
                />
              </div>

              <div class="w-full">
                <label
                  for="department"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Kategorija</label
                >
                <select
                  id="department"
                  class="pgs-input"
                  bind:value={formData.kategorijaId}
                >
                  <option value={1}>Alkoholna pića</option>
                  <option value={2}>Bezalkoholna pića</option>
                  <option value={3}>Pivo</option>
                  <option value={4}>Rakije i likeri</option>
                  <option value={5}>Vino</option>
                </select>
              </div>

              <div class="w-full md:col-span-2">
                <label
                  for="proizvodjac"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Proizvođač</label
                >
                <input
                  id="proizvodjac"
                  class="pgs-input"
                  bind:value={formData.proizvodjac}
                />
              </div>

              <div class="w-full md:col-span-2">
                <label
                  for="description"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Opis</label
                >
                 <textarea
                  id="description"
                  class="pgs-input resize-vertical"
                  bind:value={formData.opis}
                  rows="4"
                ></textarea>               
              </div>

              <div class="w-full">
                <div class="lg:col-span-2">
                  <div class="rounded-lg p-6">
                    <div class="flex flex-col">
                      <!-- Metadata -->
                      <div
                        class="flex flex-col sm:flex-row flex-wrap gap-x-10 gap-y-2 text-md text-secondary"
                      >
                        <span
                          class="flex items-center gap-2 min-w-[200px] text-sm"
                        >
                          <i class="fas fa-calendar-plus text-gray-400"></i>
                          Datum upisa:
                          <span class="font-mono"
                            >{formatDateTime(formData.created)}</span
                          >
                        </span>
                        <span
                          class="flex items-center gap-2 min-w-[200px] text-sm"
                        >
                          <i class="fas fa-edit text-gray-400"></i>
                          Datum zadnje izmjene:
                          <span class="font-mono"
                            >{formatDateTime(formData.updated)}</span
                          >
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Pricing Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Cijene</h3>
          <p class="text-secondary text-sm mt-2">
            Informacije o cijenama i porezu
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="price"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Prodajna cijena</label
                >
                <input
                  id="currency1"
                  type="number"
                  step="0.01"
                  min="0"
                  class="pgs-input"
                  bind:value={formData.price1}
                />
              </div>

              <div class="w-full">
                <label
                  for="currency"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Nabavna cijena 1</label
                >
               <input
                id="currency2"
                type="number"
                step="0.01"
                min="0"
                class="pgs-input"
                bind:value={formData.price2}
              />
              </div>

              <div class="w-full">
                <label
                  for="shipping_cost"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Nabavna cijena 2</label
                >
                <input
                  id="currency3"
                  type="number"
                  step="0.01"
                  min="0"
                  class="pgs-input"
                  bind:value={formData.price3}
                />
              </div>

              <div class="w-full">
                <label
                  for="tax_amount"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Nabavna cijena 3</label
                >
                <input
                  id="currency4"
                  type="number"
                  step="0.01"
                  min="0"
                  class="pgs-input"
                  bind:value={formData.price4}
                />
              </div>

              <div class="w-full hidden">
                <label
                  for="currency5"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Cijena 5</label
                >
                <input
                  id="currency5"
                  type="number"
                  step="0.01"
                  min="0"
                  class="pgs-input"
                  bind:value={formData.price5}
                />
              </div>

              <div class="w-full hidden">
                <label
                  for="price6"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Cijena 6</label
                >
                <input
                  id="currency6"
                  type="number"
                  step="0.01"
                  min="0"
                  class="pgs-input"
                  bind:value={formData.price6}
                />
              </div>
              <div class="w-full">
                <label
                  for="porez"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Porez</label
                >
                <input
                  id="tax_percent"
                  type="number"
                  step="0.01"
                  min="0"
                  class="pgs-input"
                  bind:value={formData.porez}
                />
              </div>

              <div class="w-full">
                <label
                  for="discount"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Porezna grupa</label
                >
                <input
                  id="discount"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.poreznaGrupa}
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>
        
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Stok</h3>
          <p class="text-secondary text-sm mt-2">
            Inventar i količina na skladištu
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              
              <div class="w-full">
                <label
                  for="stock_quantity"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Stok količina</label
                >
                <input
                  id="stock_quantity"
                  type="number"
                  class="pgs-input"
                  bind:value={formData.stok}
                />
              </div>

              <div class="w-full">
                <label
                  for="department"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Odjel</label
                >
                <select
                  id="department"
                  class="pgs-input"
                  bind:value={formData.odjel}
                >
                  <option value={1}>Kuhinja</option>
                  <option value={2}>Bar</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <!-- Other Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Ostalo</h3>
          <p class="text-secondary text-sm mt-2">
            Ostale informacije i napomena
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-1 gap-12">
              <div class="w-full">
                <label
                  for="comment"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Napomena</label
                >
                <textarea
                  id="comment"
                  class="pgs-input resize-vertical"
                  bind:value={formData.napomena}
                  rows="4"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <div class="grid grid-cols-1">     
        <div class="flex justify-end gap-3 pt-4">
          <button
            type="button"
            on:click={cancelEditing}
            class="btn btn-outline btn- m-3"
          >
            Zatvori
          </button>
          <button type="submit" class="btn btn-primary m-3"> Spremi </button>
        </div>
      </div>
    </form>
  {/if}
</div>

<style>
  
</style>
