<script lang="ts">
  import { login } from "../services/client";

  let username = "";
  let password = "";
  let error = "";
  let loading = false;

  async function handleLogin() 
  {
    try 
    {
      loading = true;
      await login(username, password);
      window.location.href = "/#/home";
    } catch (err: any) {
      error = err.message;
    } finally {
      loading = false;
    }
  }
</script>

<div class="flex items-center justify-center min-h-screen">
  <form
    on:submit|preventDefault={handleLogin}
    class="w-full max-w-sm relative p-6 rounded-2xl shadow-md bg-white dark:bg-slate-900"
  >
    <h2
      class="text-2xl font-semibold mb-8 text-center text-primary dark:text-gray-400"
    >
      Login to Pegasus
    </h2>

    {#if loading}

      <!-- Overlay loading animation -->
      <div
        class="absolute inset-0 bg-white/66 dark:bg-black/66 flex flex-col justify-center items-center z-10 rounded-2xl"
      >
        <span
          class="loading loading-infinity mb-2 text-blue-500"
          style="width: 4rem; height: 4rem;"
        ></span>
      </div>

    {/if}

    <input
      type="text"
      bind:value={username}
      placeholder="Username"
      class="w-full input px-4 py-2 mb-4
             rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
      disabled={loading}
    />

    <input
      type="password"
      bind:value={password}
      placeholder="Password"
      class="w-full input mb-4
             rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
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

      <p class="text-red-500 text-sm mt-4 text-center dark:text-red-400">
        ERROR: {error}
      </p>

    {/if}

  </form>
</div>