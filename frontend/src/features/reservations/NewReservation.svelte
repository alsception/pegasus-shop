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

<div class="w-full h-full" >

  <div class="bg-base-100 shadow-2xl">
    <div class="card-body">
      <!-- Header -->
      <!-- <div class="text-center mb-6">
        <h2 class="text-2xl font-bold text-primary">
          Nova Rezervacija
        </h2>
      </div> -->

      <form on:submit|preventDefault={handleSubmit} class="space-y-6">
        
        <!-- Date & Time Section -->
        <div class="bg-base-200 rounded-lg p-6">
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-control md:col-span-2">
              <label class="label">
                <span class="label-text font-semibold">👤 Ime</span>
                <span class="label-text-alt text-error">*obavezno</span>
              </label>
              <input 
                type="text" 
                class="pgs-input w-full mt-2" 
                bind:value={reservation.ime} 
                required 
              />
            </div>
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">📅 Datum</span>
                <span class="label-text-alt text-error">*obavezno</span>
              </label>
              <input 
                type="date" 
                class="pgs-input w-full mt-2" 
                bind:value={reservation.dan}
                required
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">🕐 Vreme</span>
              </label>
              <input 
                type="time" 
                class="pgs-input w-full mt-2" 
                bind:value={reservation.vreme}
              />
            </div>
          </div>
        </div>

        <!-- Guest Information Section -->
        <div class="bg-base-200 rounded-lg p-6">
        
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">📧 Email</span>
              </label>
              <input 
                type="email" 
                class="pgs-input w-full mt-2" 
                bind:value={reservation.email}
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">📞 Telefon</span>
              </label>
              <input 
                type="tel" 
                class="pgs-input w-full mt-2" 
                bind:value={reservation.telefon}
              />
            </div>

            <div class="form-control md:col-span-2">
              <label class="label">
                <span class="label-text font-semibold">👥 Broj Gostiju</span>
                <span class="label-text-alt text-error">*obavezno</span>
              </label>
              <div class="flex items-center gap-4">
                <input 
                  type="range" 
                  min="1" 
                  max="50" 
                  class="range range-primary flex-1" 
                  bind:value={reservation.brojGostiju}
                />
                <div class="badge badge-lg badge-primary font-bold text-lg w-16">
                  {reservation.brojGostiju}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Menu Selection Section -->
        <div class="bg-base-200 rounded-lg p-6 hidden">
          <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
            <i class="fas fa-utensils text-accent"></i>
            Izbor Menija
          </h3>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow">
              <div class="card-body p-4">
                <h4 class="card-title text-base">🍽️ Standard Menu</h4>
                <input 
                  type="number" 
                  class="input input-bordered w-full" 
                  bind:value={reservation.menuStandard} 
                  min="0"
                  placeholder="0"
                />
              </div>
            </div>

            <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow">
              <div class="card-body p-4">
                <h4 class="card-title text-base">⭐ Gold Menu</h4>
                <input 
                  type="number" 
                  class="input input-bordered input-warning w-full" 
                  bind:value={reservation.menuGold} 
                  min="0"
                  placeholder="0"
                />
              </div>
            </div>

            <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow">
              <div class="card-body p-4">
                <h4 class="card-title text-base">💎 Premium Menu</h4>
                <input 
                  type="number" 
                  class="input input-bordered input-secondary w-full" 
                  bind:value={reservation.menuPremium} 
                  min="0"
                  placeholder="0"
                />
              </div>
            </div>

            <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow">
              <div class="card-body p-4">
                <h4 class="card-title text-base">🥗 Vegetarian Menu</h4>
                <input 
                  type="number" 
                  class="input input-bordered input-success w-full" 
                  bind:value={reservation.menuVege} 
                  min="0"
                  placeholder="0"
                />
              </div>
            </div>

            <div class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow sm:col-span-2 lg:col-span-2">
              <div class="card-body p-4">
                <h4 class="card-title text-base">🌟 X Menu (Special)</h4>
                <input 
                  type="number" 
                  class="input input-bordered input-accent w-full" 
                  bind:value={reservation.menuX} 
                  min="0"
                  placeholder="0"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Meal Type & Options Section -->
        <div class="bg-base-200 rounded-lg p-6 hidden">
         

          <!-- Status Options -->
          <div>
            <label class="label">
              <span class="label-text font-semibold">Status</span>
            </label>
            <div class="grid grid-cols-2 gap-3">
              <label class="label cursor-pointer bg-base-100 rounded-lg p-4 hover:bg-base-300 transition-colors border-2" class:border-warning={reservation.vazno}>
                <span class="label-text flex items-center gap-2">
                  <i class="fas fa-exclamation-triangle text-warning"></i>
                  Važno
                </span>
                <input type="checkbox" class="checkbox checkbox-warning" bind:checked={reservation.vazno} />
              </label>

            </div>
          </div>
        </div>

        <!-- Notes Section -->
        <div class="bg-base-200 rounded-lg p-6">
           
          <div class="form-control">
           <label
                  for="napomena"
                  class="label-text font-semibold mb-20"
                  >Napomena</label
                >
            <textarea 
              class="textarea pgs-input resize-vertical" 
              bind:value={reservation.napomena}
            ></textarea>
          </div>
        </div>

        <!-- Submit Button -->
        <div class="mt-6 flex justify-end space-x-3">          
          <button type="button" on:click={closeModal} class="btn">
            Otkaži
          </button>
          <button type="submit" class="btn btn-primary"> Spremi </button>
        </div>

<!-- 
        <div class="flex justify-end gap-3 pt-4">
          <button type="button" class="btn btn-ghost btn-lg">
            <i class="fas fa-times"></i>
            Otkaži
          </button>
          <button type="submit" class="btn btn-primary btn-lg">
            <i class="fas fa-check"></i>
            Spremi
          </button>
        </div> -->

      </form>
    </div>
  </div>
</div>

<style>
  :global(body) {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  }
    .modal {
  /*   background: white;
    border-radius: 12px;
    padding: 2rem; */
    max-width: 500px;
    width: 100%;
    max-height: 90vh; /* ograničava visinu na 90% visine ekrana */
    overflow-y: auto; /* omogućava vertikalni scroll unutar modala */
}
</style>