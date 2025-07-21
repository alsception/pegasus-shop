<script lang="ts">
  import { type FPGSUser } from "./FPGSUser";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import ErrorDiv from "../utils/ErrorDiv.svelte";
  import { showSuccessToast } from "../utils/toaster";
  import { formatDateTime } from "../../utils/formatting";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let ID: number | string;

  document.title = "User details | Pegasus";

  /**
  *   TODO: Pitanje kakve date formate koristimo ovde, moguce da su pogresni
 * 
  * 
    */

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
        formData = {};
      }
    }
  }

  // Available user types
  const userTypes = ["ADMIN", "CUSTOMER", "VENDOR", "EMPLOYEE", "TESTER", "USER", "OTHER"];

  let formData: Partial<FPGSUser> = {
    role: "",
    username: "",
    firstName: "",
    lastName: "",
    active: null,
    created: new Date().toISOString(),
    modified: null,
    dob: null,
    organization: '',
    comment: ''
  };

  async function fetch(id: string | number) 
  {
    startLoadingAnimation();

    try {
      let data = await api<FPGSUser>("/users/" + id, {
        method: "GET",
      });
      
      formData = data;
      document.title = formData.username+" - User details "+" | Pegasus";
      error = null;
      formData.created = data.created;
      // Convert created date to datetime-local format
      if (data.created) {
        const date = new Date(data.created);
        formData.created = date.toISOString().slice(0, 16); // YYYY-MM-DDTHH:mm
      }
      if (data.modified) {
        const date = new Date(data.modified);
        formData.modified = date.toISOString().slice(0, 16); // YYYY-MM-DDTHH:mm
      }
      if (data.dob) {
        const date = new Date(data.dob);
        formData.dob = date.toISOString().slice(0, 10); // YYYY-MM-DDTHH:mm
      }

    } catch (err) {
      processError(err);
    } finally {
      removeLoadingAnimation();
    }
  }

  function processError(err: any) 
  {
    formData = {};
    error =
      (err as Error)?.message ||
      "User not found or an unknown error occurred. ERR_80";
  }

  onMount(async () => 
  {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id) {
      fetch(id);
    }
  });

  async function handleSubmit() 
  {
    try 
    {
      loading = true;         

      if(formData.dob)formData.dob += 'T00:00';//hack :)

      await api<FPGSUser>(`/users/${ID}`, 
      {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      showSuccessToast("Saved");

      fetch(ID);
      
    } catch (err){
      alert((err as Error).message);
    } finally {
      loading = false;
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === 'Enter') {
      event.preventDefault();
      handleSubmit();
    }
  }

  function cancelEditing(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }) 
  {
    window.location.href = "#/users"; // Back to users
  }

  const inputSkeletons = '#userForm input, #userForm select, #userForm textarea';

  //Instead of loading spinner, we will show skeletons
  function startLoadingAnimation(): void {
    // Remove display:none from #loadingMessage element
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.add("skeleton");
      input.disabled = true;
    });
  }

  function removeLoadingAnimation(): void {
    // Add display:none to #loadingMessage element
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "none";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.remove("skeleton");
      input.disabled = false;
    });
  }
</script>

<div class="relative w-full scale-up-center-normal">
  {#if !$auth.isAuthenticated}
    <Login />
  {:else if error}
    <ErrorDiv {error} />
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
      on:keydown={handleKeydown}
      id="userForm"
      class="max-w-5xl mx-auto bg-base-200 rounded p-6 pb-0 pt-0 px-0 w-full shadow-2xl "
    >
      <!-- Full-width header  -->
      <div
        class="w-full mb-6 pt-4 pb-4 px-6 bg-base-100" 
      >
        <div class="flex items-center gap-2">
          <h2 class="text-4xl font-semibold text-primary yellowtail-regular">
            User details
          </h2>
          <div
            id="loadingMessage"
            style="display: none;"
            class="text-2xl font-semibold text-gray-700 dark:text-gray-100 flex items-center gap-2"
          >
            <span class="loading loading-dots loading-xs"></span>
          </div>
        </div>
      </div>

      <!-- Form content grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 px-8 gap-6 pb-8 w-full ">
        <div class="w-full">
          <label for="username" class="label label-secondary text-sm text-gray-500 pb-3">Username</label>
          <label class="label label-secondary input input-form">
            <i class="fas fa-user text-xs text-gray-400"></i>
            <input
              id="username"
              class=" font-bold text-primary"
              bind:value={formData.username}
            />
          </label>
        </div>
        <div class="w-full">
          <label for="password" class="label label-secondary text-sm pb-3 text-gray-500">Password</label>
          <input
            id="password"
            type="password"
            autoComplete="new-password"
            readOnly
            on:focus={(e) => e.currentTarget.removeAttribute("readonly")}
            placeholder="Enter new password"
            class="input input-form text-primary"
            bind:value={formData.password}
          />
        </div>

        <div class="w-full">
          <label for="active" class="label label-secondary text-sm pb-3 text-gray-500">Status</label>
          <div class="w-full flex items-center space-x-2"> 
            <input
              type="checkbox"
              bind:checked="{formData.active}"
              class="toggle border-base bg-base-100 text-red-600 border-slate-500 checked:text-green-600"
            />            
            <p class="font-mono font-bold text-primary ml-4">
              {#if formData.active}
                ACTIVE <i class="fa fa-check" aria-hidden="true"></i>
              {:else}               
                DISABLED <i class="fa fa-ban" aria-hidden="true"></i>
              {/if}
            </p>
          </div>
        </div>

        <div class="col-span-full"><!-- HACK --></div>

        <div class="w-full">
          <label for="role" class="label label-secondary text-sm pb-3 text-gray-500"> Role </label>
          <select
            id="type"
            bind:value={formData.role}
            class="select font-mono font-bold input-form text-primary"
          >
            {#each userTypes as type}
              <option value={type}>{type}</option>
            {/each}
          </select>
        </div>
        <div class="w-full">
          <label for="dob" class="label label-secondary text-sm pb-3 text-gray-500">
            Day of birth
          </label>
          <input
            id="dob"
            type="date"
            class="input input-form font-bold text-primary"
            bind:value={formData.dob}
          />
        </div>   
        <div class="w-full">
          <label for="firstName" class="label label-secondary text-sm pb-3 text-gray-500">First Name</label>
          <input
            id="firstName"
            class="input input-form font-bold text-primary"
            bind:value={formData.firstName}
          />
        </div>
        <div class="w-full">
          <label for="lastName" class="label label-secondary text-sm pb-3 text-gray-500">Last Name</label>
          <input
            id="lastName"
            class="input input-form font-bold text-primary"
            bind:value={formData.lastName}
          />
        </div>
        <div class="w-full"> 
          <i class="fas fa-envelope text-xs text-gray-400"></i>         
          <label for="email" class="label label-secondary text-sm pb-3 text-gray-500">Email</label>
          <input
            id="email"
            type="email"
            class="input input-form font-bold text-primary"
            bind:value={formData.email}
          />
        </div>
        <div class="w-full">
          <i class="fas fa-phone text-xs text-gray-400"></i>
          <label for="phone" class="label label-secondary text-sm pb-3 text-gray-500">Phone</label>
          <input
            id="phone"
            type="tel"
            class="input input-form font-bold text-primary"
            bind:value={formData.phone}
          />
        </div>
        <div class="w-full lg:col-span-2">
          <i class="fas fa-building text-xs text-gray-400"></i>
          <label for="organization" class="label label-secondary text-sm pb-3 text-gray-500">Organization</label>
          <input
            id="organization"
            type="text"
            class="input input-form font-bold text-primary"
            bind:value={formData.organization}
          />
        </div>       


        <div class="w-full col-span-full">
          <label for="comment" class="label text-gray-500 text-sm pb-3">Comment</label>
          <textarea
            id="comment"
            class="input input-form font-bold text-primary"
            bind:value={formData.comment}
            style="height: 100px;"
          ></textarea>
        </div>
      </div>

      <div
        class="w-full px-2 py-2 bg-base-100"
      >
     <div class="w-full px-4 py-0 rounded-lg border border-base-300 ">
      <div class="flex items-center justify-between"> 
        <div class="flex items-center justify-between text-xs text-gray-600 font-mono bg-base-100 p-2 rounded-md">
          <div class="flex gap-6 items-center"> 
            <span class="flex items-center gap-2 info-block"> 
              <i class="fas fa-calendar-plus text-gray-400"></i> Created: {formatDateTime(formData.created)} 
            </span> 
            <span class="flex items-center gap-2 info-block"> 
              <i class="fas fa-edit text-gray-400"></i> Modified: {formatDateTime(formData.modified)} 
            </span> 
          </div>
        </div>
        <div class="flex items-center justify-end gap-2"> 
          <button type="button" on:click={cancelEditing} class="btn btn-outline m-3"> Close </button> 
          <button type="submit" class="btn btn-primary m-3"> Save </button> 
        </div>
      </div>
    </div>
    </form>
  {/if}
</div>

<style>
  .info-block:hover{
    color: rgb(0, 255, 88) !important;
  }
  input:hover,select:hover,textarea:hover{
    border: 1px solid var(--color-accent);
    transition: 50ms ease;
  }
  .toggle{
    transition: 200ms ease !important;
  }
</style>