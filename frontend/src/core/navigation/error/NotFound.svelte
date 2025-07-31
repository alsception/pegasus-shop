<script>
  // no logic needed
    import errorimg from '../../../assets/img/404-error-landscape.gif'; 
    import errorPng from '../../../assets/img/404-error-landscape.png'; 

//

</script>

<style>
  /* :global(body) {
    margin: 0;
    background: #f4f4f4;
  } */

  .wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 80vh;
    text-align: center;
    padding: 2rem;
    background-color: transparent;
  }

  h1 {
    font-size: 4rem;
    font-weight: 900;
    color: #111;
    background: #ffdd00;
    padding: 0.5rem 1.5rem;
    border: 3px solid #111;
    margin: 1rem 0;
    box-shadow: 6px 6px 0 #111;
  }

  p {
    font-size: 2.3rem;
    color: var(--color-primary);
    font-weight: bold;
    margin: 1rem 0;
/*     background-color: #ffdd00;
 */    padding-left: 1rem;
    padding-right: 1rem;
  }

  
</style>

<div class="wrapper">
  <img src="{errorimg}" alt="404 - Page not found" />
  <h1 style="position: relative; top: -70px;">Page not found.</h1>
  <!-- 🙉 Our team of highly trained personell is fixing it right now.🖳🔨🐒 😎  -->
</div>
