<script lang="ts">
  import { onMount } from "svelte";

  //TODO: ovo sad mora da se ucita sa servera, i da moze da se promeni i posalje na server
  // i da se razlikuje admin da moze da vidi i promeni a musterija ne da promeni, samo da vidi
  //i nevidi se dobro kad je false na mobilnom


  onMount(() => 
  {
    //IMPORTANT! Ovde postavljamo DAISYUI temu na dark ako je sistem dark, u suprotnom ce da bude pola-pola :/

    // Funkcija koja menja temu na <html> elementu
    const applyTheme = (isDark: boolean) => {
        const theme = isDark ? "dark" : "light";
        document.documentElement.setAttribute("data-theme", theme);
        document.documentElement.classList.toggle("dark", isDark);        
    };

    // 1. Proveri temu odmah pri učitavanju stranice
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    applyTheme(mediaQuery.matches);

    // 2. Dodaj listener koji prati promenu u sistemu "uživo"
    const handler = (e: MediaQueryListEvent) => applyTheme(e.matches);
    
    // Moderni browseri koriste addEventListener, stariji addListener
    if (mediaQuery.addEventListener) {
        mediaQuery.addEventListener("change", handler);
    } else {
        mediaQuery.addListener(handler);
    }

    // Cleanup: obriši listener kada se komponenta uništi (Svelte praksa)
    return () => {
        if (mediaQuery.removeEventListener) {
            mediaQuery.removeEventListener("change", handler);
        } else {
            mediaQuery.removeListener(handler);
        }
    };
  });

</script>


<div class="navbar shadow-sm fixed bg-zinc-950/84 z-50 backdrop-blur-lg">
  <div class="navbar-start ">
    <div class="ml-16">
      <a href="/#/home">
      <img src="/white_barbacoa.png" alt="Barbacoa logo" title="Barbacoa" style="max-width:149px;">
      </a>
    </div>
  </div>
  <div class="navbar-center">
    
  </div>
  <div class="navbar-end">      
  </div>
</div>

<style>
  .navbar{
    height: 3rem;
    min-height: 3rem;
  }
</style>