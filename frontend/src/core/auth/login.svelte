<script lang="ts">
  import { login } from "../services/client";

  let username = "";
  let password = "";
  let error = "";

  async function handleLogin() {
  try {
    await login(username, password);
    window.location.href = '/#/home';
    window.location.reload(); // <--- osvežava stranicu
    /*Mora zbog:TypeError: document.getElementById(...) is null
    prepareUI App.svelte:423
    App App.svelte:256
    TypeError: document.getElementById(...) is null
    prepareUI App.svelte:423
    App App.svelte:256
    */
    //eventualno     push('/home'); // preusmerava bez reload-a.import { push } from 'svelte-spa-router';
  } catch (err: any) {
    error = err.message;
  }
}
</script>

<div class="flex items-center justify-center min-h-screen /*bg-gray-100*/ /*dark:*/ bg-slate-950 p-4">
  <form 
    on:submit|preventDefault={handleLogin} 
    class="w-full max-w-sm /*bg-white*/ /*dark:*/ bg-slate-800 p-6 rounded-2xl shadow-md"
  >
    <h2 class="text-2xl font-semibold mb-6 text-center /*text-gray-800 dark:*/ text-gray-100">
      Pegasus Login
    </h2>

    <input 
    type="text"
    bind:value={username}
    placeholder="Username"
    class="w-full px-4 py-2 mb-4 border border-gray-700 
           bg-amber-100 text-black placeholder-gray-500 
           rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
  />
  
    <input 
      type="password" 
      bind:value={password} 
      placeholder="Password"
      class="w-full px-4 py-2 mb-4 border border-gray-700 
           bg-amber-100 text-black placeholder-gray-500 
           rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
    />

    <button 
      type="submit"
      class="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold 
             py-2 px-4 rounded-lg transition cursor-pointer"
    >
      Login
    </button>

    {#if error}
      <p class="text-red-500 text-sm mt-4 text-center dark:text-red-400">
        {error}
      </p>
    {/if}
  </form>
</div>