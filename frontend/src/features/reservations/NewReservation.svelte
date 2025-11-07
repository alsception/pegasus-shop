<script lang="ts">
  import { onMount } from "svelte";
  import { createEventDispatcher } from "svelte";
  import type { Reservation } from "./Reservation";
  import api from "../../core/services/client";
  import Reservations from "./Reservations.svelte";


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
console.log('hello');
  });


   async function createReservation(reservation: Reservation) {
    return api<Reservation>("/reservations", {
      method: "POST",
      body: JSON.stringify(reservation),
    });
  }

  async function handleSubmit() {
    try {
      const response = await createReservation(reservation)

      /* if (!response.ok) {
        throw new Error("Failed to save reservation");
      }

      const result = await response.json(); */
/*       dispatch("created", result);
 */      alert("Reservation created successfully!");
      
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
  }
</script>

<div class="container mx-auto p-4">
  <div class="card bg-base-100 shadow-xl max-w-2xl mx-auto">
    <div class="card-body">
      <h2 class="card-title text-2xl mb-2 text-center">Create New Reservation</h2>

      <form on:submit|preventDefault={handleSubmit} class="space-y-4">

        <!-- Date -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Date (dan)</span>
          </label>
          <input type="date" class="input input-bordered w-full" bind:value={reservation.dan} />
        </div>

        <!-- Time -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Time (vreme)</span>
          </label>
          <input type="time" class="input input-bordered w-full" bind:value={reservation.vreme} />
        </div>

        <!-- Name -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Name (ime)</span>
          </label>
          <input type="text" class="input input-bordered w-full" bind:value={reservation.ime} required />
        </div>

        <!-- Email -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Email</span>
          </label>
          <input type="email" class="input input-bordered w-full" bind:value={reservation.email} />
        </div>

        <!-- Phone -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Phone (telefon)</span>
          </label>
          <input type="text" class="input input-bordered w-full" bind:value={reservation.telefon} />
        </div>

        <!-- Number of guests -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Number of guests</span>
          </label>
          <input type="number" class="input input-bordered w-full" bind:value={reservation.brojGostiju} min="1" required />
        </div>

        <!-- Menus -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="form-control">
            <label class="label"><span class="label-text">Standard Menu</span></label>
            <input type="number" class="input input-bordered w-full" bind:value={reservation.menuStandard} min="0" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">Gold Menu</span></label>
            <input type="number" class="input input-bordered w-full" bind:value={reservation.menuGold} min="0" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">Premium Menu</span></label>
            <input type="number" class="input input-bordered w-full" bind:value={reservation.menuPremium} min="0" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text">Vegetarian Menu</span></label>
            <input type="number" class="input input-bordered w-full" bind:value={reservation.menuVege} min="0" />
          </div>

          <div class="form-control sm:col-span-2">
            <label class="label"><span class="label-text">X Menu</span></label>
            <input type="number" class="input input-bordered w-full" bind:value={reservation.menuX} min="0" />
          </div>
        </div>

        <!-- Notes -->
        <div class="form-control">
          <label class="label">
            <span class="label-text">Notes (napomena)</span>
          </label>
          <textarea class="textarea textarea-bordered w-full" bind:value={reservation.napomena}></textarea>
        </div>

        <!-- Toggles -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-2">
          <label class="label cursor-pointer flex items-center justify-between bg-base-200 rounded-lg p-2">
            <span class="label-text">Lunch (rucak)</span>
            <input type="checkbox" class="toggle" bind:checked={reservation.rucak} />
          </label>

          <label class="label cursor-pointer flex items-center justify-between bg-base-200 rounded-lg p-2">
            <span class="label-text">Dinner (vecera)</span>
            <input type="checkbox" class="toggle" bind:checked={reservation.vecera} />
          </label>

          <label class="label cursor-pointer flex items-center justify-between bg-base-200 rounded-lg p-2">
            <span class="label-text">Important (vazno)</span>
            <input type="checkbox" class="checkbox" bind:checked={reservation.vazno} />
          </label>

          <label class="label cursor-pointer flex items-center justify-between bg-base-200 rounded-lg p-2">
            <span class="label-text">Cancelled (otkazano)</span>
            <input type="checkbox" class="checkbox" bind:checked={reservation.otkazano} />
          </label>
        </div>

        <!-- Submit -->
        <button type="submit" class="btn btn-primary w-full mt-4">Create Reservation</button>

      </form>
    </div>
  </div>
</div>
