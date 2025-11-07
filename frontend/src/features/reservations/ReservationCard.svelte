<script lang="ts">
  import type { Reservation } from "./Reservation";

  // Import your types if needed
  export let reservation: Reservation;

</script>

<div class="card bg-base-100 shadow-xl">
  <div class="card-body">
    <h2 class="card-title">
      {reservation.ime}
      {#if reservation.vazno}
        <div class="badge badge-error ml-2">VAŽNO</div>
      {/if}
      {#if reservation.otkazano}
        <div class="badge badge-secondary ml-2">OTKAZANO</div>
      {/if}
    </h2>
    <p class="text-sm text-gray-500">
      ID: {reservation.id} • Datum: {reservation.dan} • Vreme: {reservation.vreme}
    </p>

    <div class="flex flex-col gap-1 mt-2">
      <div><b>Ime:</b> {reservation.ime}</div>
      <div><b>Email:</b> {reservation.email}</div>
      <div><b>Telefon:</b> {reservation.telefon}</div>
      <div><b>Broj gostiju:</b> {reservation.brojGostiju}</div>
      <div class="flex flex-wrap gap-2 mt-2">
        <div class="badge badge-outline">Standard: {reservation.menuStandard}</div>
        <div class="badge badge-outline">Gold: {reservation.menuGold}</div>
        <div class="badge badge-outline">Premium: {reservation.menuPremium}</div>
        <div class="badge badge-outline">Vege: {reservation.menuVege}</div>
      </div>
      <div class="mt-2">
        <b>Tip:</b>
        {#if reservation.rucak}<span class="badge badge-success ml-1">Ručak</span>{/if}
        {#if reservation.vecera}<span class="badge badge-info ml-1">Večera</span>{/if}
      </div>
      <div class="mt-2">
        <b>Status:</b>
        {#if reservation.poslano}<span class="badge badge-primary ml-1">Poslano</span>{/if}
        {#if reservation.potvrdjeno}<span class="badge badge-accent ml-1">Potvrđeno</span>{/if}
      </div>
      {#if reservation.dogovorio}
        <div><b>Dogovoreno sa:</b> {reservation.dogovorio}</div>
      {/if}
      {#if reservation.napomena}
        <div class="mt-2"><b>Napomena:</b> {reservation.napomena}</div>
      {/if}
    </div>

    <div class="divider"></div>

    <div class="text-xs text-gray-400">
      <div>Created: {reservation.created} by {reservation.createdBy}</div>
      <div>Last updated: {reservation.updated} by {reservation.updatedBy}</div>
    </div>

    <div class="card-actions justify-end mt-4">
      <button class="btn btn-sm btn-primary">Edit</button>
      <button class="btn btn-sm btn-error">Delete</button>
    </div>
  </div>
</div>
