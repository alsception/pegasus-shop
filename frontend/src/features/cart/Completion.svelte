<script lang="ts">
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { onMount } from "svelte";
  import { DotLottieSvelte } from "@lottiefiles/dotlottie-svelte";

  const lottieAnimationUrl = "/lottie/success1.lottie"; //or check2.lottie or success3

  document.title = "Checkout | Barbacoa";

  let loading: boolean = false;
  let error: string | null | any = null;
  let orderNumber:any;
  let success = false;

  let statusMessage = "";

  onMount(() => 
  {
    //Parametri svi dolaze od Stripea preko redirecta
    const hash = window.location.hash; 
    const queryString = hash.includes('?') ? hash.split('?')[1] : '';
    const params = new URLSearchParams(queryString);
    const redirectStatus = params.get('redirect_status');
    orderNumber = params.get('order');

    //TODO: ovde bi mogli da ucitamo narudzbu ili da stavimo link za detalje i da prikazemo musterija da vidi sta je kupio i da prati status

    switch (redirectStatus) 
    {
      //TODO: dali stvarno imamo sve ove osim success? Jer stripe nece ni radi redirect u ostalim slucajevima.
      case "succeeded":
        success = true;
        statusMessage = "Uplata uspješna! Vaša narudžba broj <b>"+orderNumber+"</b> uskoro stiže kod vas.";
        //TODO 2: A sta ako order nezavrsava sa T? i nije dostava nego Za van
        break;
      case "processing":
        statusMessage = "Uplata se još obrađuje. Javićemo vam čim prođe.";
        break;
      case "requires_payment_method":
        statusMessage = "Uplata nije uspjela. Pokušajte ponovo.";
        break;
      default:
        statusMessage = "";
        break;
    }
    // CLEAN URL (sklanjamo sve query parametre
    window.history.replaceState({}, document.title, window.location.origin); 
  });

</script>

<div class="w-full max-w-4xl mx-auto p-2 sm:p-4">

  {#if !$auth.isAuthenticated}

    <Login />

  {:else}
    
  {#if loading}
      
    <LoadingOverlay />

    {:else if error}
      
      <ErrorDiv {error} />

    {:else}

      <div
        class="text-primary mx-auto bg-base-200 dark:bg-black lg:dark:bg-base-200 mt-6 sm:mt-10 w-full max-w-2xl"
        style="transform: none"
      >
        <div
          class="p-0 bg-primary/10 text-primary-content/80 dark:text-primary/80 rounded-t-md"
        >
          <h2 class="text-primary text-lg sm:text-2xl font-bold p-1 text-center h-14 pt-3">
            Narudžba {orderNumber}
          </h2>
        </div>

        <div class="page-container bg-base-200 dark:bg-black p-14">
          
          <div class="w-full max-w-3xl bg-base-200 dark:bg-black">

            <h2>{@html statusMessage}</h2>
              
            {#if success} 
            
              <div class="lottie-container bg-transparent relative inset-0  h-40 w-full"    
              >
              <DotLottieSvelte
                src={lottieAnimationUrl}
                loop={false}
                autoplay={true}
                speed={1}
              />
              </div>
            {/if} 
            
            <a href="/" class="pgs-hyperlink">Nazad na početnu</a>
          </div>
        </div>
      </div>
    {/if}
  {/if}
</div>

<style>
    .page-container {
        display: flex;
        justify-content: center;
        align-items: center;
    }
</style>