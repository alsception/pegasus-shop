<script lang="ts">
  import { type FPGSUser } from "./FPGSUser";
  import { onMount } from "svelte";
  import { params, push } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { formatDateTime } from "../../utils/formatting";
  import Pix from "../pix/Pix.svelte";
  import { showErrorModalWithTitle } from "../../utils/modal";

  export let endpoint: string | null = null; // Optional if used in a route
  let resolvedEndpoint: string;

  let loading: boolean = false;
  let error: string | null = null;
  let ID: number | string;

  document.title = "Account details | Barbacoa";

  /**
   *   TODO: Pitanje kakve date formate koristimo ovde, moguce da su pogresni
   *
   *   TODO 2: Logika ispod za switch id/my-account je sacuvaj boze i pojavljuje se error.
   *          Pitanje dali radi submit
   *
   *  I fali endpoint za submit
   *
   *
   */

  $: {
    if (endpoint) {
      // Embedded mode — endpoint is passed in directly
      resolvedEndpoint = endpoint;
    }
    if ($params?.id) {
      const parsedId = Number($params.id);
      if (!isNaN(parsedId)) {
        resolvedEndpoint = `/users/${parsedId}`;
        ID = parsedId;
      } else {
        error = "Invalid ID in route.";
        resolvedEndpoint = "/users/my-account";
      }
    } else {
      resolvedEndpoint = "/users/my-account";
    }
  }

  $: if (resolvedEndpoint) {
    fetch(resolvedEndpoint);
  }

  /* 
   

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
 */
  // Available user types
  const userTypes = [
    "ADMIN",
    "WAITER",
    "KITCHEN",
    "EMPLOYEE",
    "TESTER",
    "OTHER",
  ];

  let formData: Partial<FPGSUser> = {
    role: "",
    username: "",
    firstName: "",
    lastName: "",
    active: null,
    created: new Date().toISOString(),
    modified: null,
    dob: null,
    organization: "",
    comment: "",
  };

  async function fetch(id: string | number) 
  {
    startLoadingAnimation();

    try {
      let data = await api<FPGSUser>(resolvedEndpoint, {
        method: "GET",
      });

      formData = data;
      document.title = formData.username + " | Account details " + " | Barbacoa";
      error = null;
      formData.created = data.created;
      // Convert created date to datetime-local format
      // Treba biti oprezan s ovim
      if (data.dob) {
        const date = new Date(data.dob);
        formData.dob = data.dob.substring(0, 10);
      }
    } catch (err) {
      processError(err);
    } finally {
      removeLoadingAnimation();
    }
  }

  function processError(err: any) {
    formData = {};
    error =
      (err as Error)?.message ||
      "User not found or an unknown error occurred. ERR_80";
  }

  async function handleSubmit() {
    try {
      loading = true;
      if (formData.dob) formData.dob += "T00:00"; //hack :)

      const response = await api<FPGSUser>(`/users/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      //Ce da baci exception api ako se desi greska

      showSuccessToast("Sačuvano");
      push("/home"); //TODO: AKO je my-account onda netreba ovo
    } catch (err) {
      // fallback if fetch itself fails (e.g. network error)
      showErrorInModal({ message: (err as Error).message });
    } finally {
      loading = false;
    }
  }

  function showErrorInModal(error: any): void {
    console.error(error);
    const contentEl = document.getElementById("modal-content");
    const dialogEl = document.getElementById("modal") as HTMLDialogElement;

    if (contentEl) {
      let errorMessage = "An error occurred";

      if (typeof error === "string") {
        // Handle case where error is a string like "Error: {json...}"
        if (error.startsWith("Error: {")) {
          try {
            const jsonPart = error.substring(7); // Remove "Error: " prefix
            const parsedError = JSON.parse(jsonPart);
            errorMessage = parsedError.message || errorMessage;
          } catch (e) {
            errorMessage = error; // Fallback to original string
          }
        } else {
          errorMessage = error;
        }
      } else if (error && error.message) {
        // Handle case where error is already an object
        errorMessage = error.message;
      }

      contentEl.textContent = errorMessage;
    }

    if (dialogEl) {
      dialogEl.showModal();
    }
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === "Enter") {
      event.preventDefault();
      handleSubmit();
    }
  }

  function cancelEditing(
    event: MouseEvent & { currentTarget: EventTarget & HTMLButtonElement }
  ) {
    window.location.href = "#/users"; // Back to users
  }

  const inputSkeletons =
    "#userForm input, #userForm select, #userForm textarea";

  //Instead of loading spinner, we will show skeletons
  function startLoadingAnimation(): void 
  { 
    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.add("skeleton");
      input.disabled = true;
    });
  }

  function removeLoadingAnimation(): void 
  {
    const inputs = document.querySelectorAll<HTMLInputElement>(inputSkeletons);
    inputs.forEach((input) => {
      input.classList.remove("skeleton");
      input.disabled = false;
    });
  }

  function openModal() {
    const dialog = document.getElementById("modal") as HTMLDialogElement;
    dialog.showModal();
  }

  function openPixModal() {
    const dialog = document.getElementById("modal-pix") as HTMLDialogElement;
    dialog.showModal();
  }

  function deleteDialog() {
    if (confirm("Jeste li sigurni?") == true) {
      deleteUser(ID);
    }
  }

  async function executeDelete(id: number | string) {
    return api("/users/" + id, {
      method: "DELETE",
    });
  }

  async function deleteUser(id: number | string) {
    try {
      await executeDelete(id);
      //We just assume its deleted if no error happens...
      push('/users');
    } catch (error) {
      //TODO: process this err msg to show network error failed to fetch, and json msg error from server
      showErrorModalWithTitle("Error deleting user", error);
    }
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
      <div class="fixed inset-0 z-10 flex items-center justify-center">
        <div
          class="rounded-2xl max-w-5xl w-full mx-auto flex flex-col items-center"
        >
          <span
            class="loading loading-infinity mb-2 text-blue-500"
            style="width: 4rem; height: 4rem;"
          ></span>
        </div>
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
      class="max-w-[100rem] mx-auto bg-base-200 rounded-lg p-8 w-full space-y-8"
    >
      <!-- Header -->
      <div
        class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4"
      >
        <div>
          <h3 class="text-3xl font-semibold text-primary">Korisnik</h3>&nbsp;
          <div id="loadingMessage" style="display: none;" class="mt-2">
            <span class="loading loading-dots loading-xs"></span>
          </div>
        </div>
        <div class="flex gap-3">
          <button
            type="button"
            on:click={cancelEditing}
            class="btn btn-outline"
          >
            <i class="fas fa-arrow-left text-primary/60"></i> Nazad
          </button>
          <button
            type="button"
            on:click={() => deleteDialog()}
            class="btn btn-outline hover:text-error group"
          >
            <i
              class="fas fa-trash text-primary/60 group-hover:text-error text-xs"
            ></i> Obriši
          </button>
          <button type="submit" class="btn btn-primary px-8">
            <i class="far fa-save text-primary-content"></i> Spremi
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <!-- Left column -->
        <div class="lg:col-span-7 space-y-8">
          <!-- Basics -->
          <div
            class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20"
          >
            <div class="mb-6">
              <h3 class="text-xl font-semibold text-primary">Osnovno</h3>
              <p class="text-secondary text-sm uppercase tracking-wider">
                Kredencijali i pristup
              </p>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label
                  for="username"
                  class="block text-sm font-medium text-secondary mb-2"
                >
                  <i class="fas fa-user text-xs text-gray-400 mr-1"></i> Korisničko
                  ime
                </label>
                <input
                  id="username"
                  class="pgs-input w-full"
                  bind:value={formData.username}
                />
              </div>

              <div>
                <label
                  for="password"
                  class="block text-sm font-medium text-secondary mb-2"
                  >Lozinka</label
                >
                <input
                  id="password"
                  type="password"
                  autoComplete="new-password"
                  readOnly
                  on:focus={(e) => e.currentTarget.removeAttribute("readonly")}
                  placeholder="Unesi novu lozinku"
                  class="pgs-input w-full"
                  bind:value={formData.password}
                />
              </div>

              <div>
                <label
                  for="role"
                  class="block text-sm font-medium text-secondary mb-2"
                  >Uloga</label
                >
                <select
                  id="role"
                  bind:value={formData.role}
                  class="pgs-input w-full font-mono"
                >
                  {#each userTypes as type}
                    <option value={type}>{type}</option>
                  {/each}
                </select>
              </div>

              <div>
                <label
                  for="active"
                  class="block text-sm font-medium text-secondary mb-2"
                  >Status</label
                >
                <div class="flex items-center space-x-3 pt-2">
                  <input
                    type="checkbox"
                    bind:checked={formData.active}
                    class="text-sm toggle ring-2 ring-primary bg-base-100 text-red-600 checked:text-green-600"
                  />
                  <p class="font-mono font-bold text-primary/80">
                    {#if formData.active}
                      <span class="text-green-600 text-lg">AKTIVAN</span>
                    {:else}
                      <span class="text-red-600 text-lg">NEAKTIVAN</span>
                    {/if}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Profile -->
          <div
            class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20"
          >
            <div class="mb-6">
              <h3 class="text-xl font-semibold text-primary">Profil</h3>
              <p class="text-secondary text-sm uppercase tracking-wider">
                Lične informacije
              </p>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label
                  for="firstName"
                  class="block text-sm font-medium text-secondary mb-2"
                  >Ime</label
                >
                <input
                  id="firstName"
                  class="pgs-input w-full"
                  bind:value={formData.firstName}
                />
              </div>

              <div>
                <label
                  for="lastName"
                  class="block text-sm font-medium text-secondary mb-2"
                  >Prezime</label
                >
                <input
                  id="lastName"
                  class="pgs-input w-full"
                  bind:value={formData.lastName}
                />
              </div>

              <div>
                <label
                  for="dob"
                  class="block text-sm font-medium text-secondary mb-2"
                  >Datum rođenja</label
                >
                <input
                  id="dob"
                  type="date"
                  class="pgs-input w-full"
                  bind:value={formData.dob}
                />
              </div>

              <div>
                <label
                  for="organization"
                  class="block text-sm font-medium text-secondary mb-2"
                >
                  <i class="fas fa-building text-xs text-gray-400 mr-1"></i> Organizacija
                </label>
                <input
                  id="organization"
                  type="text"
                  class="pgs-input w-full"
                  bind:value={formData.organization}
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Right column -->
        <div class="lg:col-span-5 space-y-8">
          <!-- Contact -->
          <div
            class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20"
          >
            <div class="mb-6 border-b border-neutral/10 pb-2">
              <h3 class="text-xl font-semibold text-primary">Kontakt</h3>
            </div>
            <div class="space-y-4">
              <div>
                <label
                  for="email"
                  class="block text-sm font-medium text-secondary mb-2"
                >
                  <i class="fas fa-envelope text-xs text-gray-400 mr-1"></i> Email
                </label>
                <input
                  id="email"
                  type="email"
                  class="pgs-input w-full"
                  bind:value={formData.email}
                />
              </div>
              <div class="mt-6">
                <label
                  for="phone"
                  class="block text-sm font-medium text-secondary mb-2"
                >
                  <i class="fas fa-phone text-xs text-gray-400 mr-1"></i> Telefon
                </label>
                <input
                  id="phone"
                  type="tel"
                  class="pgs-input w-full"
                  bind:value={formData.phone}
                />
              </div>
            </div>
          </div>

          <!-- Ostalo / Comment -->
          <div
            class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20"
          >
            <h3 class="text-xl font-semibold text-primary mb-6">Ostalo</h3>
            <label
              for="comment"
              class="block text-sm font-medium text-secondary mb-2"
              >Napomena</label
            >
            <textarea
              id="comment"
              class="pgs-input w-full resize-vertical"
              bind:value={formData.comment}
              rows="6"
            ></textarea>
          </div>
        </div>

        <!-- Footer timestamps -->
        <div
          class="p-4 text-[14px] text-secondary flex flex-col lg:flex-row lg:items-center lg:justify-between gap-2 lg:gap-10 w-max"
        >
          <div class="flex items-center gap-2">
            <i class="fas fa-calendar-plus text-gray-400"></i>
            <span
              >Upisano: <span class="font-mono"
                >{formatDateTime(formData.created)}</span
              ></span
            >
          </div>
          <div class="flex items-center gap-2">
            <i class="fas fa-edit text-gray-400"></i>
            <span
              >Izmijenjeno: <span class="font-mono"
                >{formatDateTime(formData.modified)}</span
              ></span
            >
          </div>
        </div>
      </div>
    </form>
  {/if}
</div>
<!-- TODO: WE NEED SEPARATE MODAL FOR INFO/ERROR MESSAGE AND PROFILE PIX -->

<dialog id="modal" class="modal modal-bottom sm:modal-middle w-full">
  <div
    class="modal-box bg-red-200 text-red-700"
    style="min-width: min-content;"
  >
    <h3 class="text-lg font-bold" id="modal-title">Error</h3>
    <p class="py-4" id="modal-content"></p>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>
