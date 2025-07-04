<script lang="ts">
  import { type User } from "./User";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/store";
  import Login from "../../core/auth/login.svelte";
  import api from "../../core/services/client";
  import axios from "axios";
  import { toast } from "@zerodevx/svelte-toast";
  import { slide } from "svelte/transition";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let user: User | null = null;
  let ID: number | string;

  document.title = 'User details: | Pegasus'
  
  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  $: {
    if ($params?.id) {
      ID = Number($params.id);
      fetch(ID); // reactively fetch when id changes
    }
  }

    // Available user types
  const userTypes = ["ADMIN", "CUSTOMER", "EMPLOYEE", "TESTER", "OTHER"];

  //IZGLEDA DA OVDE IMAMO I USER I FORM DATA, A TREBALO BI SAMO JEDAN.

  let formData: Partial<User> = {
    role: "",
    username: "",
    firstName: "",
    lastName: "",
    active: false,
    created: new Date().toISOString(),
  };
  
  //------------------------------------------
  //TODO: done 1.5h
  //1. LOAD DATA FROM SERVER - done 
  //2. POPULATE FORM - done, roles missing
  //3. ON CLICK -> SEND DATA TO SERVER - done
  //4. --->  Process data on server - todo
  //5. PROCESS RESPONSE on client.

  //------------------------------------------

  async function fetch(id: string | number) 
  {
    loading = true;
    try 
    {
      let data = await api<User>("/users/" + id, 
      {
        method: "GET",
      });
      //samo jedan treba da ostane
      formData = data;
      user = data;
    } 
    catch (err) 
    {
      error = (err as Error).message;
    } 
    finally     
    {
      loading = false;
    }    
  }

  onMount(async () => 
  {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id) 
    {
      fetch(id);
    }

  });

  async function handleSubmit() 
  {
    try 
    {    
      await api<User>(`/users/${ID}`, { method: 'PUT', body: JSON.stringify(formData)});

      toast.push("✅ User saved");

      window.location.href = '/#/users';
    } 
    catch (err) 
    {
      alert((err as Error).message);
    } 
    finally     
    {
      loading = false;
    }    
  }

  function cancelEditing(event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement; }) 
  {
    window.location.href = "#/users"; // Putanja do početne stranice
  }

</script>

<div class="relative w-full scale-up-center-normal " transition:slide={{ delay: 36 }}>
  {#if !$auth.isAuthenticated}
    <Login />
  {:else if loading}
    <!-- <div class="min-h-screen bg-gray-50 dark:bg-gray-900 p-6">
      <p class="text-gray-600 dark:text-gray-300">Loading...</p>
    </div> -->
  {:else if error}
    <p class="text-red-600 dark:text-red-400">Error: {error}</p>
  {:else}
    <form 
      on:submit|preventDefault={handleSubmit}
      class="max-w-5xl mx-auto bg-white dark:bg-gray-800 rounded shadow p-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
    >
      <h2
        class="text-2xl font-semibold col-span-full text-gray-700 dark:text-gray-100"
      >
        {user ? "User details" : "Novi korisnik"}
      </h2>
      <div class="w-full">
        <label for="username" class="label text-sm pb-3"
          >Username</label
        >
        <input
          id="username"
          class="input input-form font-bold"
          bind:value={formData.username}
        />
      </div>
      <div class="w-full">
        <label for="password" class="label text-sm pb-3"
          >Password</label
        >
       <input
        id="password"
        type="password"
        autoComplete="new-password"
        readOnly
        on:focus={(e) => e.currentTarget.removeAttribute('readonly')}  placeholder="Enter new password"
        class="input input-form"
        bind:value={formData.password}
      />
      </div>
      <div class="w-full">
        <label for="active" class="label text-sm pb-3">Active</label
        >
        <select
          id="active"
          class="select font-mono font-bold input-form"
          on:change={(e) => formData.active = e.currentTarget.value === 'true'}
        >
          <option value="true" selected={formData.active === true}>✅  YES</option>
          <option value="false" selected={formData.active === false}>⛔  NO</option>
        </select>
      </div>
      <div class="w-full">
        <label for="firstName" class="label text-sm pb-3"
          >First Name</label
        >
        <input
          id="firstName"
          class="input input-form font-bold"
          bind:value={formData.firstName}
        />
      </div>
      <div class="w-full">
        <label for="lastName" class="label text-sm pb-3"
          >Last Name</label
        >
        <input
          id="lastName"
          class="input input-form font-bold"
          bind:value={formData.lastName}
        />
      </div>
      <div class="w-full">
        <label for="email" class="label text-sm pb-3">Email</label>
        <input
          id="email"
          type="email"
          class="input input-form font-bold"
          bind:value={formData.email}
        />
      </div>
      <div class="w-full">
        <label for="phone" class="label text-sm pb-3">Phone</label>
        <input
          id="phone"
          type="tel"
          class="input input-form font-bold"
          bind:value={formData.phone}
        />
      </div>
      <div class="w-full">
        <label for="role" class="label text-sm pb-3"> Role </label>
          <select id="type" bind:value={formData.role} class="select font-mono font-bold input-form">
            {#each userTypes as type}
              <option value={type}>{type}</option>
            {/each}
          </select>
      </div>
      <div class="col-span-full flex justify-end">
        <button type="button" on:click={cancelEditing} class="btn m-3">
            Cancel
          </button>
          
        <button type="submit" class="btn btn-primary m-3">
          {user ? "Save" : "Kreiraj korisnika"}
        </button>
      </div>
    </form>
  {/if}
</div>

<style>

/*********************************/
/* ----------------------------------------------
* Generated by Gradienty on 2025-06-02 16:00
* animation scale-up-center-normal
* ----------------------------------------
*/
  @keyframes scale-up-center-normal {0% { transform: scale(0.5); } 100% { transform: scale(1);} }

.scale-up-center-normal { 
  animation: scale-up-center-normal 0.25s ease-out 0s 1 normal both;}
</style>
