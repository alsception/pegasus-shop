<script lang="ts">
  import UserDetails from './UserDetails.svelte';

  /**
   * TODO IMPORTANT ⚠️
   * 
   * Ovo nije baš dobro što se tiče sigurnosti, trebalo bi odvojiti user edit form od my-account forme,
   * i obratiti pažnju na to koja se polja šalju
   * A ima i bug. (Mozda je do sortiranja usera)
   * */

</script>

<UserDetails endpoint="/users/my-account" />