<script lang="ts">
  import { type User } from "./User";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import { toast } from "@zerodevx/svelte-toast";
  import ErrorDiv from "./ErrorDiv.svelte";

  let isAuthenticated = false;
  let loading: boolean = false;
  let error: string | null = null;
  let user: User | null = null;
  let ID: number | string;

  document.title = "User details: | Pegasus";

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

  async function fetch(id: string | number) {
    startLoadingAnimation();

    try {
      let data = await api<User>("/users/" + id, {
        method: "GET",
      });
      //samo jedan treba da ostane
      formData = data;
      user = data;
      error = null;
      formData.created = data.created;

      // Convert created date to datetime-local format
      if (data.created) {
        const date = new Date(data.created);
        formData.created = date.toISOString().slice(0, 16); // YYYY-MM-DDTHH:mm
      }

    } catch (err) {
      processError(err);
    } finally {
      removeLoadingAnimation();
    }
  }

  function processError(err: any) {
    user = null;
    formData = {};
    error =
      (err as Error)?.message ||
      "User not found or an unknown error occurred. ERR_80";
  }

  onMount(async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id) {
      fetch(id);
    }
  });

  async function handleSubmit() {
    try {
      loading = true;

      await api<User>(`/users/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      toast.push("✅ User saved");

      window.location.href = "/#/users";
    } catch (err) {
      alert((err as Error).message);
    } finally {
      loading = false;
    }
  }

  function cancelEditing(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }
  ) {
    window.location.href = "#/users"; // Back to users
  }

  //Instead of loading spinner, we will show skeletons
  function startLoadingAnimation(): void {
    // Remove display:none from #loadingMessage element
    const loadingMessage = document.getElementById("loadingMessage");
    if (loadingMessage) {
      loadingMessage.style.display = "";
    }

    const inputs = document.querySelectorAll<HTMLInputElement>(
      "#userForm input, #userForm select"
    );
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

    const inputs = document.querySelectorAll<HTMLInputElement>(
      "#userForm input, #userForm select"
    );
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
      id="userForm"
      class="max-w-5xl mx-auto bg-base-200 rounded p-6 pb-0 pt-0 px-0 w-full shadow-2xl "
    >
      <!-- Full-width header  -->
      <div
        class="w-full mb-6 pt-4 pb-4 px-6 bg-secondary"
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
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 px-8 gap-6 pb-8 w-full ">
        <div class="w-full">
          <label for="username" class="label label-secondary text-sm pb-3">Username</label>

          <label class="label label-secondary input input-form">
            <svg
              class="h-[1em] opacity-50"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
            >
              <g
                stroke-linejoin="round"
                stroke-linecap="round"
                stroke-width="2.5"
                fill="none"
                stroke="currentColor"
              >
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </g>
            </svg>
            <input
              id="username"
              class=" font-bold text-primary"
              bind:value={formData.username}
            />
          </label>
        </div>
        <div class="w-full">
          <label for="password" class="label label-secondary text-sm pb-3">Password</label>
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
          <label for="active" class="label label-secondary text-sm pb-3">Active</label>
          <select
            id="active"
            class="select font-mono font-bold input-form text-primary"
            bind:value={formData.active}
          >
            <option
              value=""
              disabled
              selected={formData.active === undefined ||
                formData.active === null}>-- Select --</option
            >
            <option value={true}>✅ YES</option>
            <option value={false}>⛔ NO</option>
          </select>
        </div>
        <div class="w-full">
          <label for="firstName" class="label label-secondary text-sm pb-3">First Name</label>
          <input
            id="firstName"
            class="input input-form font-bold text-primary"
            bind:value={formData.firstName}
          />
        </div>
        <div class="w-full">
          <label for="lastName" class="label label-secondary text-sm pb-3">Last Name</label>
          <input
            id="lastName"
            class="input input-form font-bold text-primary"
            bind:value={formData.lastName}
          />
        </div>
        <div class="w-full">
          <label for="email" class="label label-secondary text-sm pb-3">Email</label>
          <input
            id="email"
            type="email"
            class="input input-form font-bold text-primary"
            bind:value={formData.email}
          />
        </div>
        <div class="w-full">
          <label for="phone" class="label label-secondary text-sm pb-3">Phone</label>
          <input
            id="phone"
            type="tel"
            class="input input-form font-bold text-primary"
            bind:value={formData.phone}
          />
        </div>
        <div class="w-full">
          <label for="role" class="label label-secondary text-sm pb-3"> Role </label>
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
          <label for="created" class="label label-secondary text-sm pb-3"> Created </label>
          <input
            id="created"
            type="datetime-local"
            class="input input-form font-bold text-primary"
            bind:value={formData.created}
            disabled
            readonly
          />
        </div>
      </div>

      <div
        class="w-full pt-4 pb-4 px-4 bg-base-100"
      >
        <div class="flex items-center justify-end gap-2">
          <button type="button" on:click={cancelEditing} class="btn m-3">
            Cancel
          </button>
          <button type="submit" class="btn btn-primary m-3"> Save </button>
        </div>
      </div>
    </form>
  {/if}
</div>