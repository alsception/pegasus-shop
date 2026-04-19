<script lang="ts">
  import { onMount } from "svelte";
  import { createEventDispatcher } from "svelte";
  import type { Reservation } from "./Reservation";
  import api from "../../core/services/client";
  import Reservations from "./Reservations.svelte";
  import { showSuccessToast } from "../../core/utils/toaster";


  const dispatch = createEventDispatcher();

  let reservation: Reservation;
  reservation = {
        id: 0,
        dan: "",
        vreme: "",
        ime: "",
        email: "",
        telefon: "",
        brojGostiju: 1,
        menuStandard: 0,
        menuGold: 0,
        menuPremium: 0,
        menuVege: 0,
        menuX: 0,
        poslano: false,
        potvrdjeno: false,
        dogovorio: "",
        napomena: "",
        rucak: false,
        vecera: false,
        vazno: false,
        otkazano: false,
      };


      onMount(() => {
        console.log('Mounted new reservation component');
    });


   async function createReservation(reservation: Reservation) {
    return api<Reservation>("/reservations", {
      method: "POST",
      body: JSON.stringify(reservation),
    });
  }

  function closeModal() {
/*     isOpen = false;
 */    dispatch("close");
  }

  async function handleSubmit() {
    try {
      const response = await createReservation(reservation)

      /* if (!response.ok) {
        throw new Error("Failed to save reservation");
      }

      const result = await response.json(); */
/*       dispatch("created", result);
 */      showSuccessToast("Spremljeno");
      
      // Optionally reset the form
      reservation = {
        id: 0,
        dan: "",
        vreme: "",
        ime: "",
        email: "",
        telefon: "",
        brojGostiju: 1,
        menuStandard: 0,
        menuGold: 0,
        menuPremium: 0,
        menuVege: 0,
        menuX: 0,
        poslano: false,
        potvrdjeno: false,
        dogovorio: "",
        napomena: "",
        rucak: false,
        vecera: false,
        vazno: false,
        otkazano: false,
      };

    } catch (error) {
      console.error(error);
      alert("Error creating reservation");
    }
    dispatch('submit', reservation);
  }
</script>

<div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
  
  <div class="bg-base-100 shadow-2xl rounded-2xl w-full max-w-[100rem] max-h-[90vh] flex flex-col overflow-hidden">
    
    <div class="overflow-y-auto p-2 md:p-4 custom-scrollbar">

<form
  on:submit|preventDefault={handleSubmit}
  class="max-w-[100rem] mx-auto bg-base-200 rounded-lg p-8 w-full space-y-8"
>
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h3 class="text-3xl font-semibold text-primary">Rezervacija</h3>
      <div id="loadingMessage" style="display: none;" class="mt-2">
        <span class="loading loading-dots loading-xs"></span>
      </div>
    </div>
    <div class="flex gap-3">
      <button type="button" on:click={closeModal} class="btn btn-outline">
        <i class="fas fa-arrow-left text-primary/60"></i> Nazad
      </button>
      <button type="submit" class="btn btn-primary px-8">
        <i class="far fa-save text-primary-content"></i> Spremi
      </button>
    </div>
  </div>

  <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
    <div class="lg:col-span-7 space-y-8">
      
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6">
          <h3 class="text-xl font-semibold text-primary">Termin</h3>
          <p class="text-secondary text-sm uppercase tracking-wider">Vreme i detalji dolaska</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-user text-xs text-gray-400 mr-1"></i> Ime <span class="text-error">*</span>
            </label>
            <input 
              type="text" 
              class="pgs-input w-full" 
              bind:value={reservation.ime} 
              required 
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-calendar text-xs text-gray-400 mr-1"></i> Datum <span class="text-error">*</span>
            </label>
            <input 
              type="date" 
              class="pgs-input w-full" 
              bind:value={reservation.dan}
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-clock text-xs text-gray-400 mr-1"></i> Vreme
            </label>
            <input 
              type="time" 
              class="pgs-input w-full" 
              bind:value={reservation.vreme}
            />
          </div>
        </div>
      </div>

      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6">
          <h3 class="text-xl font-semibold text-primary">Gosti</h3>
        </div>
        <div class="space-y-4">
          <label class="block text-sm font-medium text-secondary">
            Broj osoba <span class="text-error">*</span>
          </label>
          <div class="flex items-center gap-6 bg-base-100 p-4 rounded-lg border border-neutral/20">
            <input 
              type="range" 
              min="1" 
              max="50" 
              class="range range-primary flex-1" 
              bind:value={reservation.brojGostiju}
            />
            <div class="badge badge-lg badge-primary font-mono font-bold text-lg h-10 w-16">
              {reservation.brojGostiju}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="lg:col-span-5 space-y-8">
      
      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <div class="mb-6 border-b border-neutral/10 pb-2">
          <h3 class="text-xl font-semibold text-primary">Kontakt</h3>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-envelope text-xs text-gray-400 mr-1"></i> Email
            </label>
            <input 
              type="email" 
              class="pgs-input w-full" 
              bind:value={reservation.email}
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-secondary mb-2">
              <i class="fas fa-phone text-xs text-gray-400 mr-1"></i> Telefon
            </label>
            <input 
              type="tel" 
              class="pgs-input w-full" 
              bind:value={reservation.telefon}
            />
          </div>
        </div>
      </div>

      <div class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20">
        <h3 class="text-xl font-semibold text-primary mb-6">Dodatno</h3>
        
        <div class="mb-6">
          <label class="label cursor-pointer bg-base-100 rounded-lg p-4 border border-neutral/20 hover:bg-base-300 transition-colors" class:border-warning={reservation.vazno}>
            <span class="label-text flex items-center gap-2 font-medium">
              <i class="fas fa-exclamation-triangle text-warning"></i>
              Označi kao važno
            </span>
            <input type="checkbox" class="checkbox checkbox-warning" bind:checked={reservation.vazno} />
          </label>
        </div>

        <label class="block text-sm font-medium text-secondary mb-2">Napomena</label>
        <textarea 
          class="pgs-input w-full resize-vertical" 
          bind:value={reservation.napomena}
          rows="4"
        ></textarea>
      </div>
    </div>
  </div>
</form>

</div>
  </div>
</div>
<style>
  /* Opciono: Lepši skrolbar za moderne browsere */
  .custom-scrollbar::-webkit-scrollbar {
    width: 8px;
  }
  .custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb {
    background: #88888844;
    border-radius: 10px;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #88888888;
  }
</style>