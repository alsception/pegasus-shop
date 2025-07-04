<script lang="ts">
  import { onMount } from "svelte";
  import { logout } from "../services/client";

  onMount(() => {
    logout();
    //send to /home
    window.location.href = "/";
  });
</script>

Logging out...