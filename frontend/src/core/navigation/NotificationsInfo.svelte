<script lang="ts">
  import axios from "axios";
  import { onMount } from "svelte";
  import { link } from "svelte-spa-router";
  import { auth } from "../services/SessionStore";
  import { showSuccessToast } from "../utils/toaster";

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

    let notifications = [
    {
        icon: '',
        text: ''
    },
/*
    {
        icon: 'check',
        text: 'Order 234 is ready'
    },
    {
        icon: 'plus',
        text: 'New order from X'
    },
    {
        icon: 'spoon',
        text: 'Order 2 is in preparation'
    },*/
    ]
    onMount(() => 
    {
        console.log('mounted');
        fetchNotifications();
    });

    async function fetchNotifications()
    {
        console.log('fetching notfs')
        const token = $auth.token;
        
        fetch(API_BASE_URL + `/notifications`, 
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        })
        .then(response => response.json()) // <-- Parse JSON prvo
        .then(data => 
        {
            processSuccess(data);
        })
        .catch(error => 
        {
            processError(error);
        });     
    }

    function processSuccess(data: any)
    {
        console.log(data);
        notifications = data;
        
        // Samo za nepročitane
        data
            .filter((n: any) => !n.read)
            .forEach((notification: any) => {
                showSuccessToast(notification.text || notification.title || 'New notification');
            });
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
          <span class="dark:text-gray-400 hover:text-blue-400" style="width: 150px;">{item.text}</span>
    </div>
   
    </li>
{/each}
{/if}
</ul>
