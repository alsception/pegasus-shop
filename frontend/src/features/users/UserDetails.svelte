<script lang="ts">
  import { type User } from "./User";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/store";
  import Login from "../../core/auth/login.svelte";
  import api from "../../core/services/client";
  import { toast } from "@zerodevx/svelte-toast";

  let isAuthenticated = false;
  let loading: boolean = true;
  let error: string | null = null;
  let user: User | null = null;
  let ID: number | string;

  document.title = 'User details: | Pegasus'
  
  //Authenticacion
  $: isAuthenticated = $auth.isAuthenticated;

  $: {
    if ($params?.id) {
      const parsedId = Number($params.id);
      // We only permit valid numbers to be called.
      if (!isNaN(parsedId)) {
        ID = parsedId;
        fetch(ID); // reactively fetch when id changes
      } else {
        error = "Invalid user ID.";
        user = null;
        formData = {};
      }
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
    active: null,
    created: new Date().toISOString(),
  };

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
      error = null;
    } 
    catch (err) 
    {
      processError(err);
    } 
    finally     
    {
      loading = false;
    }    
  }

  function processError(err: any) 
  {
    user = null;
    formData = {};
    error = (err as Error)?.message || "User not found or an unknown error occurred. ERR_80";
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
    window.location.href = "#/users"; // Back to users
  }

</script>

<div class="relative w-full scale-up-center-normal">

  {#if !$auth.isAuthenticated}

    <Login />

  {:else if error}

    <div class="flex justify-center items-center h-64 text-red-500 text-2xl mt-4 text-center dark:text-red-400">
      <h3>Error: {error}</h3>
    </div>
    
  {:else}

   {#if loading} 
    <!-- Overlay loading animation -->      
    <div
        class="absolute inset-0 bg-white/33 dark:bg-black/33 flex flex-col justify-center items-center z-10 rounded-2xl max-w-5xl mx-auto"
      >
        <span
          class="loading loading-infinity mb-2 text-blue-500"
          style="width: 4rem; height: 4rem;"
        ></span>
      </div> 

    {/if}

    <form 
      on:submit|preventDefault={handleSubmit}
      class="max-w-5xl mx-auto bg-white dark:bg-gray-800 rounded shadow p-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
    >
      <h2
        class="text-2xl font-semibold col-span-full text-gray-700 dark:text-gray-100"
      >
        {"User details"}
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
          bind:value={formData.active}
        >
          <option value="" disabled selected={formData.active === undefined || formData.active === null}>-- Select --</option>
          <option value={true}>✅  YES</option>
          <option value={false}>⛔  NO</option>
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
           Save
        </button>
      </div>
    </form>
  {/if}
</div>