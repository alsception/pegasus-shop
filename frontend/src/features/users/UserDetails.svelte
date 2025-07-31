<script lang="ts">
  import { type FPGSUser } from "./FPGSUser";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { formatDateTime } from "../../utils/formatting";

  export let endpoint: string | null = null; // Optional if used in a route
  let resolvedEndpoint: string;

  let loading: boolean = false;
  let error: string | null = null;
  let ID: number | string;

  document.title = "Account details | Pegasus";

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
    console.log('hello')
     if (endpoint) {
      // Embedded mode — endpoint is passed in directly
      console.log(endpoint)
      resolvedEndpoint = endpoint;
    }  if ($params?.id) {
      console.log('params?')
      const parsedId = Number($params.id);
      if (!isNaN(parsedId)) {
        resolvedEndpoint = `/users/${parsedId}`;
        ID = parsedId;
      } else {
        error = "Invalid ID in route.";
        resolvedEndpoint = '/users/my-account';
      }
    }else{
      resolvedEndpoint = '/users/my-account';
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
    "CUSTOMER",
    "VENDOR",
    "EMPLOYEE",
    "TESTER",
    "USER",
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

  async function fetch(id: string | number) {
    startLoadingAnimation();

    try {
      let data = await api<FPGSUser>(resolvedEndpoint, {
        method: "GET",
      });

      formData = data;
      document.title = formData.username + " | Account details " + " | Pegasus";
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

  function processError(err: any) {
    formData = {};
    error =
      (err as Error)?.message ||
      "User not found or an unknown error occurred. ERR_80";
  }
/* 
  onMount(async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id) {
      fetch(id);
    }
  }); */


//**
// PITAJ BOGA STA SE OVDE VRACA STRING SA JSONOMO...
// 
// */

async function handleSubmit() {
    try {
        loading = true;
        if (formData.dob) formData.dob += "T00:00"; //hack :)
        
        const response = await api<FPGSUser>(`/users/${ID}`, {
            method: "PUT",
            body: JSON.stringify(formData),
        });
        
        console.log(1)
        console.log(response)
        if (!response.ok) {
          console.log(2)
            const errorData = await response.json();
            console.log(errorData);
            // Pass the parsed error object directly
            showErrorInModal(errorData);
            return;
        }
        console.log(3)
        showSuccessToast("Saved");
        fetch(ID);
    } catch (err) {
      console.log(4)
        // fallback if fetch itself fails (e.g. network error)
        showErrorInModal({ message: (err as Error).message });
    } finally {
      console.log(5)
        loading = false;
    }
}

function showErrorInModal(error: any): void {
    console.log(error);
    const contentEl = document.getElementById("modal-content");
    const dialogEl = document.getElementById("modal") as HTMLDialogElement;
    
    if (contentEl) {
        let errorMessage = "An error occurred";
        
        if (typeof error === 'string') {
            // Handle case where error is a string like "Error: {json...}"
            if (error.startsWith('Error: {')) {
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

  function openModal() {
    const dialog = document.getElementById("modal") as HTMLDialogElement;
    dialog.showModal();
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
      class="max-w-7xl mx-auto bg-base-100 rounded-lg p-8 w-full space-y-8"
    >
      <!-- Header Section -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-1">
          <h2 class="text-4xl font-semibold text-primary">Account details</h2>

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
          <h3 class="text-2xl font-semibold text-primary">Basics</h3>
          <p class="text-secondary text-sm mt-2">
            Essential user credentials and permissions
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="username"
                  class="block text-sm font-medium text-gray-700 mb-2"
                >
                  <i class="fas fa-user text-xs text-gray-400 mr-2"></i>Username
                </label>
                <input
                  id="username"
                  class="pgs-input"
                  bind:value={formData.username}
                />
              </div>

              <div class="w-full">
                <label
                  for="password"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Password</label
                >
                <input
                  id="password"
                  type="password"
                  autoComplete="new-password"
                  readOnly
                  on:focus={(e) => e.currentTarget.removeAttribute("readonly")}
                  placeholder="Enter new password"
                  class="pgs-input"
                  bind:value={formData.password}
                />
              </div>

              <div class="w-full">
                <label
                  for="role"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Role</label
                >
                <select
                  id="role"
                  bind:value={formData.role}
                  class="pgs-input font-mono"
                >
                  {#each userTypes as type}
                    <option value={type}>{type}</option>
                  {/each}
                </select>
              </div>

              <div class="w-full">
                <label
                  for="active"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Status</label
                >
                <div class="flex items-center space-x-3 pt-2">
                  <input
                    type="checkbox"
                    bind:checked={formData.active}
                    class="toggle ring-2 ring-primary bg-base-100 text-red-600 checked:text-green-600"
                  />
                  <p class="font-mono font-bold text-primary">
                    {#if formData.active}
                      ACTIVE <i
                        class="fa fa-check text-green-600"
                        aria-hidden="true"
                      ></i>
                    {:else}
                      DISABLED <i
                        class="fa fa-ban text-red-600"
                        aria-hidden="true"
                      ></i>
                    {/if}
                  </p>
                </div>
              </div>
              <div class="w-full">
                <div class="lg:col-span-2">
                  <div class="rounded-lg p-6">
                    <div class="flex flex-col">
                      <!-- Metadata -->
                      <div
                        class="flex flex-col sm:flex-row flex-wrap gap-x-10 gap-y-2 text-md text-secondary"
                      >
                        <span
                          class="flex items-center gap-2 min-w-[200px] text-sm"
                        >
                          <i class="fas fa-calendar-plus text-gray-400"></i>
                          Created:
                          <span class="font-mono"
                            >{formatDateTime(formData.created)}</span
                          >
                        </span>
                        <span
                          class="flex items-center gap-2 min-w-[200px] text-sm"
                        >
                          <i class="fas fa-edit text-gray-400"></i>
                          Modified:
                          <span class="font-mono"
                            >{formatDateTime(formData.modified)}</span
                          >
                        </span>
                      </div>
                    </div>
                  </div>
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
          <h3 class="text-2xl font-semibold text-primary">Profile</h3>
          <p class="text-secondary text-sm mt-2">
            Personal information and organization details
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="firstName"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >First Name</label
                >
                <input
                  id="firstName"
                  class="pgs-input"
                  bind:value={formData.firstName}
                />
              </div>

              <div class="w-full">
                <label
                  for="lastName"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Last Name</label
                >
                <input
                  id="lastName"
                  class="pgs-input"
                  bind:value={formData.lastName}
                />
              </div>

              <div class="w-full">
                <label
                  for="dob"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Date of Birth</label
                >
                <input
                  id="dob"
                  type="date"
                  class="pgs-input"
                  bind:value={formData.dob}
                />
              </div>

              <div class="w-full">
                <label
                  for="organization"
                  class="block text-sm font-medium text-gray-700 mb-2"
                >
                  <i class="fas fa-building text-xs text-gray-400 mr-2"
                  ></i>Organization
                </label>
                <input
                  id="organization"
                  type="text"
                  class="pgs-input"
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
          <h3 class="text-2xl font-semibold text-primary">Contact</h3>
          <p class="text-secondary text-sm mt-2">
            Communication and contact information
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="email"
                  class="block text-sm font-medium text-gray-700 mb-2"
                >
                  <i class="fas fa-envelope text-xs text-gray-400 mr-2"
                  ></i>Email
                </label>
                <input
                  id="email"
                  type="email"
                  class="pgs-input"
                  bind:value={formData.email}
                />
              </div>

              <div class="w-full">
                <label
                  for="phone"
                  class="block text-sm font-medium text-gray-700 mb-2"
                >
                  <i class="fas fa-phone text-xs text-gray-400 mr-2"></i>Phone
                </label>
                <input
                  id="phone"
                  type="tel"
                  class="pgs-input"
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
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
        <div class="lg:col-span-1">
          <h3 class="text-2xl font-semibold text-primary">Address</h3>
          <p class="text-secondary text-sm mt-2">
            Physical location and address details
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class="rounded-lg">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
              <div class="w-full">
                <label
                  for="addressName"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Name</label
                >
                <input
                  id="addressName"
                  type="text"
                  class="pgs-input"
                  bind:value={formData.addressName}
                />
              </div>

              <div class="w-full">
                <label
                  for="street"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Street</label
                >
                <input
                  id="street"
                  type="text"
                  class="pgs-input"
                  bind:value={formData.street}
                />
              </div>

              <div class="w-full">
                <label
                  for="town"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >Town</label
                >
                <input
                  id="town"
                  type="text"
                  class="pgs-input"
                  bind:value={formData.town}
                />
              </div>

              <div class="w-full">
                <label
                  for="state"
                  class="block text-sm font-medium text-gray-700 mb-2"
                  >State</label
                >
                <input
                  id="state"
                  type="text"
                  class="pgs-input"
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
          <h3 class="text-2xl font-semibold text-primary">Other</h3>
          <p class="text-secondary text-sm mt-2">
            Additional notes and comments
          </p>
        </div>
        <div class="lg:col-span-2">
          <div class=" rounded-lg">
            <div class="w-full">
              <label
                for="comment"
                class="block text-sm font-medium text-gray-700 mb-2"
                >Comment</label
              >
              <textarea
                id="comment"
                class="pgs-input resize-vertical"
                bind:value={formData.comment}
                rows="4"
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <!-- Full-width underline -->
      <div class="h-px bg-neutral w-full"></div>

      <div class="grid grid-cols-1">
        <div class="flex justify-end gap-3 pt-4">
          <button
            type="button"
            on:click={cancelEditing}
            class="btn btn-outline btn- m-3"
          >
            Close
          </button>
          <button type="submit" class="btn btn-primary m-3"> Save </button>
        </div>
      </div>
    </form>
  {/if}
</div>
<!-- The button to open modal -->
<!-- <label for="my_modal_6" class="btn">open modal</label>
 -->
<!-- Put this part before </body> tag -->
<!-- <input type="checkbox" id="my_modal_6" class="modal-toggle" />
<div class="modal" role="dialog">
  <div class="modal-box">
    <h3 class="text-lg font-bold">Hello!</h3>
    <p class="py-4">This modal works with a hidden checkbox!</p>
    <div class="modal-action">
      <label for="my_modal_6" class="btn">Close!</label>
    </div>
  </div>
</div>
 -->
<!-- Open the modal using ID.showModal() method -->
<!-- <button class="btn" on:click={openModal}>open modal</button>
 --><dialog id="modal" class="modal modal-bottom sm:modal-middle">
  <div class="modal-box">
    <h3 class="text-lg font-bold" id="modal-title">Error</h3>
    <p class="py-4" id="modal-content"></p>
    <div class="modal-action">
      <form method="dialog">
        <!-- if there is a button in form, it will close the modal -->
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>
