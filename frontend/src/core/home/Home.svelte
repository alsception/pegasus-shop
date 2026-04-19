<script lang="ts">
  import { auth } from "../services/SessionStore";
  import Login from "../auth/Login.svelte";
  import ProductsListBarbacoa from "../../features/products/ProductsListBarbacoa.svelte";

  document.title = "Barbacoa";
</script>

{#if !$auth.isAuthenticated}

  <Login />

{:else}

  <div class="mt-0 mx-0">
    <ProductsListBarbacoa />
  </div>
  
{/if}
