<script lang="ts">
  import { onMount } from "svelte";
  import { logout } from "../services/client";
  import { push } from "svelte-spa-router";

  onMount(() => {
    logout();
    //send to /home
    push("/");
    window.location.reload();
  });
</script>