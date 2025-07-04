<script lang="ts">

import { logout } from "../services/client"

</script>

<div class="relative">
    <button
      id="dropdownBtn"
      class="text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white focus:outline-none text-xl"
    >
      ⋮
    </button>
    <div
      id="dropdownMenu"
      class="hidden absolute right-0 mt-2 w-40 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-md shadow-lg z-40"
    >
      <a
        href="#"
        class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
        >Profile</a
      >
      <a
        href="#"
        class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
        >Settings</a
      >
      <a
        href="#"
        on:click={async () => logout()}
        class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-800 dark:text-red-400"
      >Logout</a
      >
    </div>
  </div>