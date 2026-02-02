<script lang="ts">
  import axios from "axios";
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

    let notifications = [
    {
        icon: '',
        label: ''
    },
/*
    {
        icon: 'check',
        label: 'Order 234 is ready'
    },
    {
        icon: 'plus',
        label: 'New order from X'
    },
    {
        icon: 'spoon',
        label: 'Order 2 is in preparation'
    },*/
    ]

    const axiosInstance = axios.create({
        baseURL: API_BASE_URL,
        headers: {
        'Content-Type': 'application/json',
        },
    });

    // Add Bearer token if available
    axiosInstance.interceptors.request.use((config) => {
        const token = localStorage.getItem('token'); // or getToken() if you have a helper
        if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });

    onMount(() => 
    {
        console.log('mounted');
        //fetchNotifications();
    });

    function fetchNotifications()
    {
        console.log('fetching notfs')
        axiosInstance.get(`/notifications/`)
            .then(response => {
                processSuccess(response);
            })
            .catch(error => {
                processError(error);
            });
    };

    function processSuccess(response: any)
    {
        if (response.data && response.data.notifications) 
        {
            console.log(response);
            notifications = response.data
        }
    }

    /**
     * @param {any} error
     */
    function processError(error: any) 
    {
        console.log('Error fetching notifications',error);
        notifications = [];
    }
</script>

<ul
  class="menu menu-sm dropdown-content  w-52 p-2 scale-in-ver-top 
         bg-base-100 dark:bg-slate-950
         rounded shadow "
  style="min-width: 300px; left: -270px;"
>
    {#if notifications.length == 0}
  <li class="flex  px-3 py-2 rounded-md">
    <div class="inline-flex gap-1">
     No new notifications
    </div>
  </li>
  <li class=""></li>
{:else}
   {#each notifications as item}
    <li class="w-full border-y border-base-300 ">
        <div
          class="flex items-center px-3 py-2 rounded-md cursor-pointer
                  hover:bg-[#0f172a] hover:text-blue-400
                 text-primary text-sm"
        >
          <i class="fas fa-{item.icon} w-5 mr-2"></i>
          <span class="dark:text-gray-400 hover:text-blue-400" style="width: 150px;">{item.label}</span>
    </div>
   
    </li>
{/each}
{/if}
</ul>
