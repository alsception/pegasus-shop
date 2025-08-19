<script lang="ts">



/***
 * TODO: skontati kako ovo funkckionense :), i commit i remove comment
 * 
 * 2. add some line effects  kad se upali i login
 * 
 * 3. register kad predje da bude isto ovako kao kocka da se rotira???
 * 
 * 
 * 
 * 
*/


  import LoadingOverlay from "../utils/LoadingOverlay.svelte";
  import { login } from "../services/client";
  import LoginTest from "./LoginTest.svelte";
  import { scale } from "svelte/transition";
  import { push } from "svelte-spa-router";

  document.title = "Log in | Pegasus";

  let username = "";
  let password = "";
  let error = "";
  let loading = false;

  async function handleLogin() {
    try {
      loading = true;
      await login(username, password);

      //LLM PROMPT: I WANT TRANSITION HERE SAME AS IN FORM
      push("/home");
    } catch (err: any) {
      error = 'ERROR: ' + err.message;
    } finally {
      loading = false;
    }
  }
</script>

<LoginTest/>
 
<div class="flex items-center justify-center min-h-screen relative" >
  <div class="p-6 border-2 border-cyan-400 rounded-lg /*animate-pulse*/ shadow-[0_0_10px_cyan,0_0_20px_cyan,0_0_40px_cyan] relative z-10 bg-black bg-opacity-100" style="box-shadow: none;"  transition:scale={{ duration: 700, start: 0.5 }}>

    <h1 class="text-4xl mb-4 text-center font-bold bg-gradient-to-r from-sky-400 via-blue-500 to-violet-600 text-transparent bg-clip-text font-mono uppercase tracking-widest yellowtail-regular">
      Pegasus
    </h1>
    
   <div class="w-[112%] ml-[-6%] h-1 bg-transparent mb-4 " transition:scale={{ duration: 200, start: 0.5 }}>
      <div class="w-full h-full bg-cyan-400 /*animate-pulse*/ shadow-[0_0_10px_cyan,0_0_20px_cyan,0_0_40px_cyan]"></div>
    </div>

    <form
      on:submit|preventDefault={handleLogin}
      
      class="w-full max-w-md relative p-6 px-10 m-[5px] rounded-2xl bg-black bg-opacity-100"
      style="background-color: rgba(0, 0, 0, 1) !important;"
      
    >
      {#if loading}
        <LoadingOverlay />
      {/if}

      <label for="username" class="block text-md font-medium text-secondary mb-2">
        <i class="fas fa-user text-sm text-secondary mx-2"></i>Username
      </label>
      <input
        type="text"
        bind:value={username}
        placeholder="Username"
        class="pgs-input mb-8"
        disabled={loading}
      />

      <label for="password" class="block text-md font-medium text-secondary mb-2">
        <i class="fas fa-lock text-sm textsecondary mx-2"></i>Password
      </label>
      <input
        type="password"
        bind:value={password}
        placeholder="Password"
        class="pgs-input mb-8"
        disabled={loading}
      />

      <button
        type="submit"
        class="btn btn-primary w-full mt-2"
        disabled={loading}
      >
        Login
      </button>

      {#if error}
        <p
          class="text-red-500 text-sm mt-8 text-center dark:text-red-400 bg-error-content p-2"
        >
          {error}
        </p>
      {/if}

      <div class="mt-8">
        <label for="register" class="label">Don't have account?</label>
        <a href="#/register" class="pgs-hyperlink"> Click here to register </a>
      </div>
    </form>
  </div>
  
</div>