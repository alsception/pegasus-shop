<script lang="ts">
  import { type FPGSUser } from "./FPGSUser";
  import { onMount } from "svelte";
  import { link, params, push } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import ErrorDiv from "../../core/navigation/error/ErrorDiv.svelte";
  import { showSuccessToast } from "../../core/utils/toaster";
  import { formatCode, formatCommentInfo, formatDate2, formatDateTime, formatPrice, formatTime, formatTime2, getOrderStatusColor, getOrderStatusLabel } from "../../utils/formatting";
  import { showErrorModal, showErrorModalWithTitle } from "../../utils/modal";
  import type { Order } from "../orders/Order";
  import { fly } from "svelte/transition";

  export let endpoint: string | null = null; // Optional if used in a route

  let resolvedEndpoint: string;

  let loading: boolean = false;
  let loadingOrders: boolean = false;
  let error: string | null = null;
  let ID: number | string | undefined;
  let orders: Order[] = [];
  let ordersNaCekanju: Order[] = [];
  let ordersUpripremi: Order[] = [];
  let ordersSpremni: Order[] = [];
  let ordersOstalo: Order[] = [];


  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;


  document.title = "Account details | Barbacoa";

  /**
   *   TODO: Pitanje kakve date formate koristimo ovde, moguce da su pogresni
   *
   *   TODO 2: Logika ispod za switch id/my-account je sacuvaj boze i pojavljuje se error.
   *          Pitanje dali radi submit
   *
   *  I fali endpoint za submit
   *
   * 27/3/26 submit radi ali sad ima novi bug kad se udje iz userlist :D
   */

    onMount(() => {
      //console.log('mounted');
    });

  $: {
    if (endpoint) 
    {
      // Embedded mode — endpoint is passed in directly
      resolvedEndpoint = endpoint;
    }
    if ($params?.id)//ovo ce biti true cak i ako je /my-account 
    {
      const parsedId = Number($params.id);
      if (!isNaN(parsedId)) 
      {
        resolvedEndpoint = `/users/${parsedId}`;
        ID = parsedId;
      } 
      else 
      {
        resolvedEndpoint = "/users/my-account";
      }
    } 
    else 
    {
      resolvedEndpoint = "/users/my-account";
    }
  }

  $: if (resolvedEndpoint) 
  {
    fetchUser();
    
  }

  let isCheckedDetails = true;
  let isCheckedCart = false;
  let isCheckedOrders = false;
 
  // Available user types
  const userTypes = [
    "ADMIN",
    "CUSTOMER",
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
    comment: "",
  };

  async function fetchUser() 
  {
    startLoadingAnimation();
    try 
    {
      let data = await api<FPGSUser>(resolvedEndpoint,{
        method: "GET",
      });
      formData = data;
      ID = formData.id;//Ako je my-acc id cemo dobiti tek kad se ucita user.
      document.title = formData.username + " | Detalji računa " + " | Barbacoa";
      error = null;
      formData.created = data.created;   
      
      //If user is loaded, load orders
      //TODO: start loading only when checked true;
      fetchOrders(true);
    } 
    catch (err) 
    {
      processError(err);
    } 
    finally 
    {
      removeLoadingAnimation();
    }

  }

  async function fetchOrders(showLoading: boolean) 
  {
      const token = $auth.token;
      if (showLoading) loadingOrders = true;

      try 
      {
        const res = await fetch(API_BASE_URL + `/orders/user/${ID}`, 
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        // Check response status and handle specific cases
        if (!res.ok) 
        {
          if (res.status === 401) 
          {
            console.log("Authentication failed - token may be expired");
            // Clear invalid token
            localStorage.removeItem("token");
            auth.set({ token: null, isAuthenticated: false });
            // Redirect to login or show login modal
            // window.location.href = '/login';
            // OR: showLoginModal = true;
            // OR: goto('/login');
            throw new Error("Authentication failed");
          }
          throw new Error(`Fetch error: ${res.status} - ${res.statusText}`);
        }

        // Parse JSON directly - no need for JSON.parse since res.json() already does this
        const data = await res.json();

        // Update orders with the received data
        orders = data;

        console.log(orders)

        orders = data.reverse();

        let ordersDropped = orders.filter((o) => o.code === null);
        if( ordersDropped.length > 0)
        {
          console.warn("Dropped %s orders with code null", ordersDropped.length);
          //ovo bi trebalo poslati na db log TODO
        }
          
        orders = orders.filter((o) => o.code !== null);

        ordersNaCekanju = orders.filter((o) => o.status === 'WAITING');
        ordersUpripremi = orders.filter((o) => o.status === 'IN_PREPARATION');
        ordersSpremni = orders.filter((o) => o.status === "READY" || o.status === "SERVED");
        ordersOstalo = orders.filter((o) => (
          o.status !== "READY" 
           && o.status !== "SERVED"
           && o.status !== "WAITING"
           && o.status !== "IN_PREPARATION"
        ));
        //totalAmount = calculateTotal(orders);
      } 
      catch (error: any) 
      {
        console.error("Error during search:", error);

        /**
         * TODO: see if this works. should display error message.
         * ako je failed to fetch, server je nedostupan.
         */

        showErrorModal("Greška prilikom učitavanja narudžbi: " + error.message);
        // Handle 401 Unauthorized specifically
        if (error.message.includes("401")) 
        {
          console.log("Authentication failed - token may be expired");
          // Clear invalid token
          $auth.token = null;
        }

      } 
      finally 
      {
        if (showLoading) loadingOrders = false;
      }
         
  }

  function processError(err: any) 
  {
    formData = {};
    error =
      (err as Error)?.message ||
      "User not found or an unknown error occurred. ERR_80";
  }

  async function handleSubmit() 
  {
    try 
    {
      console.log(ID);
      loading = true;

      const response = await api<FPGSUser>(`/users/${ID}`, {
        method: "PUT",
        body: JSON.stringify(formData),
      });

      //Ce da baci exception api ako se desi greska

      showSuccessToast("Sačuvano");
      push("/home"); //TODO: AKO je my-account onda netreba ovo
    } 
    catch (err) 
    {
      // fallback if fetch itself fails (e.g. network error)
      showErrorInModal({ message: (err as Error).message });
    } 
    finally 
    {
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

  function deleteDialog() {
    if (confirm("Jeste li sigurni?") == true) {
      deleteUser(ID);
    }
  }

  async function executeDelete(id: number | string | undefined) {
    return api("/users/" + id, {
      method: "DELETE",
    });
  }

  async function deleteUser(id: number | string | undefined) {
    try {
      await executeDelete(id);
      //We just assume its deleted if no error happens...
      push('/users');
    } catch (error) {
      //TODO: process this err msg to show network error failed to fetch, and json msg error from server
      showErrorModalWithTitle("Error deleting user", error);
    }
  }

  function shouldHide() {
    //Nije bas najelegantnije resenje, ali za sad moramo da sakrijemo neka polje
    return resolvedEndpoint == "/users/my-account"
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


       <!-- name of each tab group should be unique -->
    <div class="tabs tabs-box rounded-xs">     

      <input
        type="radio"
        name="my_tabs_6"
        class="tab text-primary text-xl"
        aria-label="Detalji računa"
        checked={isCheckedDetails}
      />
      <div class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-0 h-full"
        >
        {@render userForm()}
      </div>

      <input
        type="radio"
        name="my_tabs_6"
        class="tab text-primary text-xl"
        aria-label="Košarica"
        checked={isCheckedCart}
      />
      <div
        class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-0 h-full"
      >
        {@render userCart()} 
      </div>

      <input
        type="radio"
        name="my_tabs_6"
        class="tab text-primary text-xl"
        aria-label="Narudžbe"
        checked={isCheckedOrders}
      />
      <div     
        class="tab-content bg-base-300 dark:bg-[#0a0a0a] border-base-300 p-0 h-full"
      >
        {@render userOrders()}
      </div>

    </div>

    {#snippet userForm()}

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
      class=" mx-auto bg-base-200 rounded-lg p-8 w-full space-y-8"
    >
      <!-- Header -->
      <div
        class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4"
      >
        <div>
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
                  ><i class="fas fa-lock text-xs text-gray-400 mr-1"></i> Lozinka</label
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

              <div class:hidden={shouldHide()}>
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

              <div class:hidden={shouldHide()}>
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

          <!-- Adresa -->
          <div
            class="bg-base-200 p-6 rounded-xl shadow-sm border border-neutral/20"
          >
            <h3 class="text-xl font-semibold text-primary mb-6">Adresa</h3>
            <label
              for="address"
              class="block text-sm font-medium text-secondary mb-2"
              ><i class="fas fa-home text-xs text-gray-400 mr-1"></i> Adresa za dostavu</label
            >
            <textarea
              id="address"
              class="pgs-input w-full resize-vertical"
              bind:value={formData.address}
              rows="6"
            ></textarea>
          </div>

          <!-- Ostalo / Comment -->
          <!-- <div
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
          </div> -->

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
  
    {/snippet}

    {#snippet userOrders()}
    {#if loadingOrders}
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
    <!-- Table view: -->
    {#if orders.length > 0}
    <div
      class="max-w-[2048px] w-full overflow-x-auto rounded-lg align-middle text-center mx-auto"
    >
      <div class="">
        <table class="table table-zebra min-w-full divide-y divide-accent">
          <thead class="bg-base-300">
            <tr class="h-12">
              <th class="pgs-th">Broj</th>
              <th class="pgs-th">Korisnik</th>
              <th class="pgs-th">Dostava<br>za van</th>
              <th class="pgs-th">Status</th>
              <th class="pgs-th">Komentar</th>
              <th class="pgs-th">Datum</th>
              <th class="pgs-th">Primljeno</th>
              <th class="pgs-th">U pripremi</th>
              <th class="pgs-th">Spremno</th>
              <th class="pgs-th-r">Ukupno<br>stavki</th>
              <th class="pgs-th-r">Iznos</th>
              <th class="pgs-th-l">Način<br>plaćanja</th>
                <th class="pgs-th"></th>
            </tr>
          </thead>
          <tbody>
            {#each orders as order, i}
              <tr
                class={`tr-highlight ${i % 2 === 1 ? 'bg-base-200/30' : 'bg-base-200/60'}`}
                transition:fly={{ y: -50, duration: 300 }}
              >
                <td class="pgs-td">
                  <a use:link href="/orders/{order.id}" class="pgs-hyperlink"
                    >{formatCode(order.code)}</a
                  >
                </td>                
                <td class="pgs-td font-mono font-bold">{order.user?.username}</td>
                <td class="pgs-td font-mono p-2">
                  <div class="flex items-center justify-center gap-1 text-sm text-primary/60 w-full">
                    
                    {#if order.code.endsWith('T')}
                      <div class="tooltip tooltip-info tooltip-top flex items-center" data-tip="Za van">
                        <span class="badge badge-soft badge-success flex items-center justify-center">
                          <i class="fas fa-walking"></i>
                        </span>
                      </div>
                    {:else}
                      <div class="tooltip tooltip-info tooltip-top flex items-center" data-tip="Dostava">
                        <span class="badge badge-soft badge-info flex items-center justify-center">
                          <i class="fas fa-car"></i>
                        </span>
                      </div>  
                    {/if}
                  </div>
                </td>
                <td class="text-center">
                  {#if order.status}
                    <span
                      class="badge badge-soft badge-{getOrderStatusColor(
                        order.status
                      )} font-mono badge-sm whitespace-nowrap"
                      style="text-transform: uppercase;"
                    >
                      { getOrderStatusLabel(order.status) }
                    </span>
                  {/if}
                </td>
                <td class="text-center"
                  >{@html formatCommentInfo(order.comment)}</td
                >
                <td class="pgs-td font-mono">
                  {@html formatDate2(order.created)}
                  <!-- {@html formatTime2(order.created)} -->
                </td>
                <td class="pgs-td font-mono">
                  {@html formatTime(order.created, "novo", 15)}
                  <!-- {@html formatTime2(order.created)} -->
                </td>
                <td class="pgs-td font-mono">
                  {@html formatTime2(order.upripremiAt)}
                </td>
                <td class="pgs-td font-mono">
                  {@html formatTime2(order.spremnoAt)}
                </td>
                <td class="pgs-td-num font-mono">{order.items.length}</td>
                <td class="pgs-td-num font-mono font-bold text-right"
                  >{formatPrice(order.price)}</td
                >
                <td class="pgs-td font-mono text-center">
                  {#if order.paymentMethod == '1'}
                    <div class="tooltip tooltip-info tooltip-top inline-flex" data-tip="Gotovina">
                      <span class="badge badge-soft badge-warning flex items-center justify-center">
                        <i class="fas fa-coins"></i>
                      </span>
                    </div>
                  {:else if order.paymentMethod == '2'}
                    <div class="tooltip tooltip-info tooltip-top inline-flex" data-tip="Kartica">
                      <span class="badge badge-soft badge-accent flex items-center justify-center">
                        <i class="fas fa-credit-card"></i>
                      </span>
                    </div>
                  {:else}
                    <span
                      class="badge badge-soft badge-secondary font-mono badge-sm whitespace-nowrap"
                      style="text-transform: uppercase;"
                    >OSTALO</span>
                  {/if}
                </td>
                  <td class=" justify-center">
                    <div class="tooltip tooltip-info group" data-tip="Edit">
                      <a
                        class="px-4"
                        aria-label="Edit"
                        use:link
                        href="/orders/mngmt/{order.id}"
                      >
                        <i
                          class="fas fa-pen text-gray-500 group-hover:text-sky-400 cursor-pointer"
                        ></i></a
                      >
                    </div>
                    <button
                      class="px-4 group"
                      aria-label="Delete"
                      on:click={() =>
                        deleteDialog(
                          order.id,
                          "Are you sure you want to delete this order? This action cannot be undone!"
                        )}
                    >
                      <div class="tooltip tooltip-info" data-tip="Delete">
                        <i
                          class="fas fa-times-circle text-gray-500 group-hover:text-red-400 cursor-pointer"
                        ></i>
                      </div>
                    </button>
                  </td>
              </tr>
            {/each}
          </tbody>
        </table>
          <div
            class="nb-table-footer text-left bg-secondary w-full"
            style="background-color: var(--color-base-200);"
          >
            Ukupan promet: <span
              class="ml-4 font-bold font-mono text-2xl text-primary"
              >{formatPrice(0)}</span
            >
            <br />
            Ukupno narudžbi:
            <span class="font-bold text-xl text-primary"> {orders.length}</span>
          </div>
      </div>
    </div>
    {:else}
    {#if !loadingOrders}
    <p class="p-6">Nema narudžbi</p>
    {/if}
    {/if}
    {/snippet}

    {#snippet userCart()}
    .
    {/snippet}

  {/if}
</div>
<!-- TODO: WE NEED SEPARATE MODAL FOR INFO/ERROR MESSAGE AND PROFILE PIX -->

<dialog id="modal" class="modal modal-bottom sm:modal-middle w-full">
  <div
    class="modal-box bg-base-200 text-red-700"
    style="min-width: min-content;"
  >
    <h3 class="text-lg font-bold" id="modal-title">Error</h3>
    <p class="p-4 bg-error/10" id="modal-content"></p>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>
