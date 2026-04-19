<script lang="ts">
  import { onMount } from 'svelte';
  import OrderDetails from './OrderDetails.svelte';

  let isLoading: boolean = false;
    const urlParams = new URLSearchParams(window.location.search);
    const id = window.location.pathname.split('/').pop();
  onMount(async () => {
   
  });

</script>

<div class="w-full md:w-2/5">
        <OrderDetails ID={id}></OrderDetails>
</div>

