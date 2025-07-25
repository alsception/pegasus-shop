<script lang="ts">
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../../core/services/SessionStore";
  import { get } from "svelte/store";
  import { formatCommentInfo, formatDate } from "../../utils/formatting";
  import { formatActive } from "../../utils/formatting";
  import { logout } from "../../core/services/client";
  import type { FPGSUser } from "./FPGSUser";
  import Login from "../../core/auth/Login.svelte";
  import api from "../../core/services/client";
  import LoadingOverlay from "../../core/utils/LoadingOverlay.svelte";
  import ErrorDiv from '../utils/ErrorDiv.svelte';
  import NewUserModal from "./NewUserModal.svelte";
  import { showErrorToastLongDuration, showErrorToastNoExp } from "../utils/toaster";


  let isAuthenticated = false;  
  let loading: boolean = false;
  let error: string | null = null;
  let users: FPGSUser[] = [];  
  let showCreateModal = false;
  let sortKey = '';
  let sortAsc = true;
  let searchTerm = '';

  document.title = 'Users | Pegasus'

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  $: auth.subscribe((value) => {
    isAuthenticated = value.isAuthenticated;
  });  

  // Headers needs to be reactive because they change when sorting
  $: usernameHeader = sortKey === 'username'
    ? sortAsc ? 'USERNAME ▼' : 'USERNAME ▲'
    : 'USERNAME&nbsp;&nbsp;';

  $: roleHeader = sortKey === 'role'
    ? sortAsc ? 'ROLE ▼' : 'ROLE ▲'
    : 'ROLE&nbsp;&nbsp;';

  $: firstNameHeader = sortKey === 'firstName'
    ? sortAsc ? 'FIRST NAME ▼' : 'FIRST NAME ▲'
    : 'FIRST NAME&nbsp;&nbsp;';

  $: lastNameHeader = sortKey === 'lastName'
    ? sortAsc ? 'LAST NAME ▼' : 'LAST NAME ▲'
    : 'LAST NAME&nbsp;&nbsp;';

  $: createdHeader = sortKey === 'created'
    ? sortAsc ? 'CREATED ▼' : 'CREATED ▲'
    : 'CREATED&nbsp;&nbsp;';

  $: modifiedHeader = sortKey === 'modified'
    ? sortAsc ? 'MODIFIED ▼' : 'MODIFIED ▲'
    : 'MODIFIED&nbsp;&nbsp;';

  $: activeHeader = sortKey === 'active'
    ? sortAsc ? 'STATUS ▼' : 'STATUS ▲'
    : 'STATUS&nbsp;&nbsp;';

  
  // Fetch the users from the backend
  onMount(async () => {
    try {
      const { isAuthenticated: authStatus } = get(auth);
      isAuthenticated = authStatus;
      //TODO: if is not authenticated, then dont do search      
      handleSearch(true);
    } catch (err) {
      error = err instanceof Error ? err.message : "Unknown error";
    } finally {
      loading = false;
    }
  });

  function handleFormSubmit(event: { preventDefault: () => void }) 
  {
    event.preventDefault(); // prevent page reload
    handleSearch(false);
  }

  async function handleSearch(showLoading: boolean) 
  {
    //TODO: Currently we search for new users when modal is closed
    // We should not search if new user has not been created
    
    if(showLoading) loading = true;
    error = null; // Reset error message
    
    const token = $auth.token;

    await fetch(API_BASE_URL+`/users?search=${searchTerm}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })
      .then((res) => {
        if (res.status === 401) {
          throw new Error("Unauthorized");
        }
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
          users = data; 
      })
      .catch((err) => {
        if (err.message === "Unauthorized") {
          // 👇 Show error message to user
          alert("Session expired. Please log in again.");
          logout();

          // Optional: Redirect to login or clear token
          // window.location.href = '/login';
        } else {
          error = err instanceof Error ? err.message : "Unknown error";
        }
      })
      .finally(() => {
        loading = false;
      });
  }
  
  function getClass(dateStr: string | number | Date | null | undefined): string 
  {
    if (dateStr == null) return '';

    const date = new Date(dateStr);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInMinutes = diffInMs / (1000 * 60);

    if (diffInMinutes < 10) 
    {
      return `cornsilk`;
    }
    return '';
  }

  function sortBy(key: keyof FPGSUser) 
  {
    users = [...users].sort((a, b) => {
      const valA = String(a[key]);
      const valB = String(b[key]);
      return sortAsc ? valA.localeCompare(valB) : valB.localeCompare(valA);
    });

    sortKey = key;
    sortAsc = !sortAsc;
  }

  function openCreateModal() 
  {
    showCreateModal = true;
  }

  function getUserColor(userRole: string): string 
  {
    switch (userRole.toUpperCase()) 
    {
      case "ADMIN":
        return " bg-info/70 text-success ml-4 ";
      case "CUSTOMER":
        return "success text-success/60 ";
      case "EMPLOYEE":
        return "accent ";
      //Most should be covered by these 3
      case "TESTER":
        return "error ";
      case "VENDOR":
        return "warning ";
      case "OTHER":
        return "secondary ";
      default:
        return "secondary "; // ili neka podrazumevana boja
    }
  }

  function deleteDialog(id: number)
  {
    if (confirm("Are you sure? This action cannot be undone.") == true) 
    {
      deleteUser(id);      
    } 
  }

  async function executeDelete(id: number) 
  {
    return api("/users/"+id, {
      method: "DELETE",
    });
  }

  async function deleteUser(id: number) 
  {
    try 
    {
      await executeDelete(id);
      //We just assume its created if no error happens...
      handleSearch(false);      
    } catch (error) {
      //TODO: process this err msg to show network error failed to fetch, and json msg error from server
      showErrorToastLongDuration("Error deleting user: " + error);
    }
  }


  function handleCheckboxChange(event: Event, i: any) 
  {

		const target = event.target as HTMLInputElement;
		let isChecked = target.checked;
    //TODO: BUG - when ordering, selected rows are not updated
		if (isChecked) 
    {			
      const trElement = document.getElementById(`tr-${i}`); 
      //add class bg-blue-100 dark:bg-slate-700
      if (trElement) 
      {
        trElement.classList.add("selected");
      }
		} 
    else 
    {
      const trElement = document.getElementById(`tr-${i}`);
      if (trElement) 
      {
        trElement.classList.remove("selected");
      }
		}
	}

  function isActiveHeader(name: keyof FPGSUser) 
  {
    return sortKey === name;
  }
</script>

{#if !$auth.isAuthenticated}
  <Login />
{:else}
<div class="w-full flex justify-center px-4">
  <div class="w-full max-w-4xl p-4 bg-transparent rounded-lg">
    <form
      on:submit|preventDefault={handleFormSubmit}
      class="flex flex-col sm:flex-row items-center gap-3"
    >
      <input
        type="text"
        bind:value={searchTerm}
        placeholder="Search users..."
        class="input input-primary border-2"
        />
      <button type="submit" class="btn btn-dash">
        <i class="fas fa-search"></i>
          Search
      </button>
     
      <button
        on:click={openCreateModal}
        class="btn btn-dash"
      >       
          <i class="fas fa-plus"></i><i class="fas fa-user"></i>
          Create new user
      </button>
    </form>
  </div>
  </div>

  <div id="results" style="margin: 48px;"></div>

  {#if loading}
      
  <LoadingOverlay/>

  {:else if error}

  <ErrorDiv {error} />

  {:else}

  {#if (users.length === 0)}
        
      <div class="flex justify-center items-center h-64">
        <h3 class="text-gray-500 dark:text-gray-400">No users found.</h3>
      </div>

    {:else}

  <div class="max-w-[1568px] overflow-x-auto rounded-lg align-middle mx-auto">
          <table class="table table-zebra min-w-full divide-y divide-accent" >
      <thead class="bg-base-200">
        <tr class="h-12">
          <th class="pgs-th">
          </th>
          <th class="pgs-th cursor-pointer"  
            title="Click to sort by"            
            class:pgs-gradient-text={isActiveHeader('username')}
            class:dark\:bg-slate-700={isActiveHeader('username')} 
            on:click={() => sortBy('username')}>
            {@html usernameHeader}          
          </th>

          <th class="pgs-th cursor-pointer"  
            title="Click to sort by" 
            class:pgs-gradient-text={isActiveHeader('role')}
            class:dark\:bg-slate-700={isActiveHeader('role')} 
            on:click={() => sortBy('role')}>
            {@html roleHeader}            
          </th>

          <th class="pgs-th cursor-pointer"  
            title="Click to sort by"
            class:pgs-gradient-text={isActiveHeader('firstName')}
            class:dark\:bg-slate-700={isActiveHeader('firstName')} 
            on:click={() => sortBy('firstName')}>{@html firstNameHeader}            
          </th>

          <th class="pgs-th cursor-pointer"  
            title="Click to sort by"
            class:pgs-gradient-text={isActiveHeader('lastName')}
            class:dark\:bg-slate-700={isActiveHeader('lastName')} 
            on:click={() => sortBy('lastName')}>{@html lastNameHeader}            
          </th>
                    <th class="pgs-th">
                      
                    </th>

          <th class="pgs-th cursor-pointer"  
            title="Click to sort by"
            class:pgs-gradient-text={isActiveHeader('created')}
            class:dark\:bg-slate-700={isActiveHeader('created')} 
            on:click={() => sortBy('created')}>{@html createdHeader}            
          </th>

          <th class="pgs-th cursor-pointer"  
            title="Click to sort by"
            class:pgs-gradient-text={isActiveHeader('modified')}
            class:dark\:bg-slate-900={isActiveHeader('modified')} 
            on:click={() => sortBy('modified')}>{@html modifiedHeader}            
          </th>

          <th class="pgs-th cursor-pointer"  
            title="Click to sort by"
            class:pgs-gradient-text={isActiveHeader('active')}
            on:click={() => sortBy('active')}>{@html activeHeader}            
          </th>
          <th class="pgs-th">ACTIONS</th>
        </tr>
      </thead>
      <tbody class="">

        {#each users as user, i}     
            <tr id="tr-{i}" class="{ getClass(user.modified) + ' bg-base-100  outline-1 outline-transparent hover:outline-blue-500 hover:bg-blue-600/15'} ">            
            
              <td class="justify-center pgs-td-center px-4 ">
                <input type="checkbox" class="checkbox checkbox-accent checkbox-xs" 	on:change={(event) => handleCheckboxChange(event, i)} />
              </td>
              <td class="pgs-td">
                  <a use:link href="#/users/{user.id}" class="text-gray-500  pgs-hyperlink dark:text-gray-100 text-primary font-bold block max-w-[200px] truncate">{user.username}</a>
              </td> 
              <td>
                <span class="badge badge-outline badge-{getUserColor(user.role)} badge-{getUserColor(user.role)} font-mono font-semibold" style="text-transform: uppercase;">
                  {user.role}
                </span>
              </td>     
              <td class="pgs-td"><span class="block max-w-[200px] truncate" title="{user.firstName}">{user.firstName}</span></td>
              <td class="pgs-td"><span class="block max-w-[200px] truncate" title="{user.lastName}">{user.lastName}</span></td>
              <td class="text-center"
                  >{@html formatCommentInfo(user.comment)}</td
                >
              <td class="pgs-td font-mono justify-right">
                {@html formatDate(user.created,'new',15)}
              </td>
              <td class="pgs-td font-mono pgs-td-num ">
                {@html formatDate(user.modified,'Changed less then 15 minutes ago',15)}
              </td>
              <td class="pgs-td font-mono"> {formatActive(user.active)}</td>      
              <td class="px-2">
                <div class="flex justify-center items-center gap-2" style="font-size: 14px;">
                  <div class="tooltip tooltip-info" data-tip="Edit">
                    <a class="px-2" aria-label="Edit" use:link href="#/users/{user.id}">
                      <i class="fas fa-pen text-gray-500 hover:text-sky-400 cursor-pointer"></i>
                    </a>
                  </div>
                  <div class="tooltip tooltip-info" data-tip="Delete">
                    <button class="px-2" aria-label="Delete" on:click={() => deleteDialog(user.id)}>
                      <i class="fas fa-times-circle text-gray-500 hover:text-red-400 cursor-pointer"></i>
                    </button>
                  </div>
                </div>
              </td>
            </tr>          
        {/each}
        <tr class="bg-base-200"> 
          <td colspan="10" class="pgs-td font-mono">Total users found: {users.length}</td>  
        </tr>  
      </tbody>
    </table>
  </div>
  {/if}

  <NewUserModal
    isOpen={showCreateModal}
    on:close={() => {showCreateModal = false; handleSearch(false)}
    }
  />
  {/if}
{/if}

<style> 
.selected {
  background-color: #cfe2ff; /* light blue */
} 
.selected:hover {
  background-color: #cfe2ffd5; /* slightly different light blue */
} 

/* 
@media (prefers-color-scheme: dark) {
  .pgs-table-tr {
    @apply bg-linear-to-b from-gray-900 to-gray-950 text-gray-200;
  }
}
.dark .pgs-table-tr {
  @apply bg-linear-to-b from-gray-900 to-gray-950 text-gray-200;
}
 */

.pgs-table-tr{

}
.pgs-table-tr:hover{
  
}
</style>
