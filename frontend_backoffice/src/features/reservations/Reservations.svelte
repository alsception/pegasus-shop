<script lang="ts">
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import SearchReservationsForm from "./SearchReservationsForm.svelte";

  document.title = "Rezervacije | Barbacoa";

  // State variables
  let isAuthenticated = false;
  let error: string | null = null;

  // Auth subscription
  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });

</script>

{#if !$auth.isAuthenticated}

  <Login />

{:else}

  <SearchReservationsForm/>  

{/if}