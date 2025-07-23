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

  document.title = "Account details | Pegasus";

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
      document.title = formData.username+" | Account details "+" | Pegasus";
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
        class="absolute inset-0 dark:bg-black/33 flex flex-col justify-center items-center z-10 rounded-2xl max-w-5xl mx-auto"
      >
        <span
          class="loading loading-infinity mb-2 text-blue-500"
          style="width: 4rem; height: 4rem;"
        ></span>
      </div>
    {/if}

<!-- TODO: 
FIX THIS PROBLEM:
Non-interactive element `<form>` should not be assigned mouse or keyboard event listeners
https://svelte.dev/e/a11y_no_noninteractive_element_interactionssvelte(a11y_no_noninteractive_element_interactions)
A space-separated list of the classes of the element. Classes allows CSS and JavaScript to select and access specific elements via the class selectors or functions like the method Document.getElementsByClassName().

MDN Reference -->

<!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
<form
  on:submit|preventDefault={handleSubmit}
  on:keydown={handleKeydown}
  id="userForm"
  class="max-w-7xl mx-auto bg-base-100 rounded-lg p-8 w-full space-y-8"
>
  <!-- Header Section -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
    <div class="lg:col-span-1">
      <h2 class="text-4xl font-semibold text-accent yellowtail-regular">
        Account details
      </h2>

      <div
        id="loadingMessage"
        style="display: none;"
        class="text-2xl font-semibold text-gray-700 dark:text-gray-100 flex items-center gap-2 mt-4"
      >
        <span class="loading loading-dots loading-xs"></span>
      </div>
    </div>    
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <!-- Basics Section -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Basics
      </h3>
      <p class="text-secondary text-sm mt-2">
        Essential user credentials and permissions
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class="">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div class="w-full">
            <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fas fa-user text-xs text-gray-400 mr-2"></i>Username
            </label>
            <input
              id="username"
              class="pgs-input-form"
              bind:value={formData.username}
            />
          </div>
          
          <div class="w-full">
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Password</label>
            <input
              id="password"
              type="password"
              autoComplete="new-password"
              readOnly
              on:focus={(e) => e.currentTarget.removeAttribute("readonly")}
              placeholder="Enter new password"
              class="pgs-input-form"
              bind:value={formData.password}
            />
          </div>

          <div class="w-full">
            <label for="role" class="block text-sm font-medium text-gray-700 mb-2">Role</label>
            <select
              id="role"
              bind:value={formData.role}
              class="pgs-input-form font-mono"
            >
              {#each userTypes as type}
                <option value={type}>{type}</option>
              {/each}
            </select>
          </div>

          <div class="w-full">
            <label for="active" class="block text-sm font-medium text-gray-700 mb-2">Status</label>
            <div class="flex items-center space-x-3 pt-2">
              <input
                type="checkbox"
                bind:checked={formData.active}
                class="toggle ring-2 ring-primary bg-base-100 text-red-600 checked:text-green-600"
              />
              <p class="font-mono font-bold text-primary">
                {#if formData.active}
                  ACTIVE <i class="fa fa-check text-green-600" aria-hidden="true"></i>
                {:else}
                  DISABLED <i class="fa fa-ban text-red-600" aria-hidden="true"></i>
                {/if}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <!-- Profile Section -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Profile
      </h3>
      <p class="text-secondary text-sm mt-2">
        Personal information and organization details
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class="rounded-lg ">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div class="w-full">
            <label for="firstName" class="block text-sm font-medium text-gray-700 mb-2">First Name</label>
            <input
              id="firstName"
              class="pgs-input-form"
              bind:value={formData.firstName}
            />
          </div>
          
          <div class="w-full">
            <label for="lastName" class="block text-sm font-medium text-gray-700 mb-2">Last Name</label>
            <input
              id="lastName"
              class="pgs-input-form"
              bind:value={formData.lastName}
            />
          </div>

          <div class="w-full">
            <label for="dob" class="block text-sm font-medium text-gray-700 mb-2">Date of Birth</label>
            <input
              id="dob"
              type="date"
              class="pgs-input-form"
              bind:value={formData.dob}
            />
          </div>

          <div class="w-full">
            <label for="organization" class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fas fa-building text-xs text-gray-400 mr-2"></i>Organization
            </label>
            <input
              id="organization"
              type="text"
              class="pgs-input-form"
              bind:value={formData.organization}
            />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <!-- Contact Section -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Contact
      </h3>
      <p class="text-secondary text-sm mt-2">
        Communication and contact information
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class="rounded-lg">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div class="w-full">  
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fas fa-envelope text-xs text-gray-400 mr-2"></i>Email
            </label>
            <input
              id="email"
              type="email"
              class="pgs-input-form"
              bind:value={formData.email}
            />
          </div>
          
          <div class="w-full">
            <label for="phone" class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fas fa-phone text-xs text-gray-400 mr-2"></i>Phone
            </label>
            <input
              id="phone"
              type="tel"
              class="pgs-input-form"
              bind:value={formData.phone}
            />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <!-- Address Section
   
  TODO: we dont save this yet to database. Address should be only one big textarea
  
  -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16 ">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Address
      </h3>
      <p class="text-secondary text-sm mt-2">
        Physical location and address details 
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class="rounded-lg">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div class="w-full">
            <label for="addressName" class="block text-sm font-medium text-gray-700 mb-2">Name</label>
            <input
              id="addressName"
              type="text"
              class="pgs-input-form"
              bind:value={formData.addressName}
            />
          </div>
          
          <div class="w-full">
            <label for="street" class="block text-sm font-medium text-gray-700 mb-2">Street</label>
            <input
              id="street"
              type="text"
              class="pgs-input-form"
              bind:value={formData.street}
            />
          </div>

          <div class="w-full">
            <label for="town" class="block text-sm font-medium text-gray-700 mb-2">Town</label>
            <input
              id="town"
              type="text"
              class="pgs-input-form"
              bind:value={formData.town}
            />
          </div>

          <div class="w-full">
            <label for="state" class="block text-sm font-medium text-gray-700 mb-2">State</label>
            <input
              id="state"
              type="text"
              class="pgs-input-form"
              bind:value={formData.state}
            />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <!-- Other Section -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Other
      </h3>
      <p class="text-secondary text-sm mt-2">
        Additional notes and comments
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class=" rounded-lg">
        <div class="w-full">
          <label for="comment" class="block text-sm font-medium text-gray-700 mb-2">Comment</label>
          <textarea
            id="comment"
            class="pgs-input-form resize-vertical"
            bind:value={formData.comment}
            rows="4"
          ></textarea>
        </div>
      </div>
    </div>
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <!-- Metadata -->
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Metadata
      </h3>
      <p class="text-secondary text-sm mt-2">
        Account creation date and time of last change
      </p>
    </div>
    <div class="lg:col-span-2">
      <div class=" rounded-lg p-6 ">
        <div class="flex flex-col ">
          <!-- Metadata -->
          <div class="flex flex-col sm:flex-row flex-wrap gap-x-10 gap-y-2 text-md text-secondary">
            <span class="flex items-center gap-2 min-w-[200px] text-sm">
              <i class="fas fa-calendar-plus text-gray-400"></i>
              Created: <span class="font-mono">{formatDateTime(formData.created)}</span>
            </span>
            <span class="flex items-center gap-2 min-w-[200px] text-sm">
              <i class="fas fa-edit text-gray-400"></i>
              Modified: <span class="font-mono">{formatDateTime(formData.modified)}</span>
            </span>
          </div>          
        </div>
      </div>
    </div>
  </div>

  <!-- Full-width underline -->
  <div class="h-px bg-neutral w-full"></div>

  <div class="grid grid-cols-1 lg:grid-cols-2">
    <div class="lg:col-span-1">
      <h3 class="text-2xl font-semibold text-accent yellowtail-regular">
        Actions
      </h3>
      <p class="text-secondary text-sm mt-2">
        Save the changes or close without saving
      </p>
    </div>
    <div class="flex justify-end gap-3 pt-4 ">
      <button type="button" on:click={cancelEditing} class="btn btn-outline btn- m-3"> Close </button> 
      <button type="submit" class="btn btn-primary m-3"> Save </button> 
    </div>
  </div>
</form>
  {/if}
</div>