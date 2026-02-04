<script lang="ts">
    import axios from "axios";
    import { onMount, onDestroy } from 'svelte';
    import { link } from "svelte-spa-router";
    import { auth } from "../services/SessionStore";
    import { showSuccessToast } from "../utils/toaster";

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

    let notifications = [
    {
        icon: '',
        title: '',
        text: ''
    }];
 
    let notificationsMap = new Map();
    let interval: any;

    onMount(() => 
    {
        console.log('Component mounted');
        // 1. Prvo učitavanje odmah
        fetchNotifications();

        // 2. Fetchuj na svakih 5 sekundi
        interval = setInterval(() => {
            fetchNotifications();
        }, 5000);
    });

    onDestroy(() => 
    {
        // Obavezno čišćenje intervala da ne curi memorija
        if (interval) clearInterval(interval);
    });

    async function fetchNotifications() 
    {
        const token = $auth.token;
        if (!token) return;

        try {
            const response = await fetch(API_BASE_URL + `/notifications`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const data = await response.json();
                processNotifications(data);
            }
        } catch (error) {
            console.error("Fetch error:", error);
        }
    }

    function processNotifications(data: any[]) {
        notifications = data; // Update-ujemo listu za UI

        data.forEach((notification: any) => {
            // 3. Ako se pojavi nova koja nije u mapi
            if (!notificationsMap.has(notification.id)) 
            {
                
                // Dodajemo je u mapu da je ne bismo opet prikazali
                notificationsMap.set(notification.id, notification);

                // Prikaži toast (samo ako ovo nije prvo učitavanje, opciono)
                // Ako želiš da izbegneš rafalnu paljbu tostova na samom startu,
                // možeš dodati proveru: if (notificationsMap.size > data.length)
                showSuccessToast(notification.text || notification.title || 'New notification');
            }
        });
    }
</script>

<ul
  class="menu menu-sm dropdown-content  w-52 p-2 scale-in-ver-top 
         bg-base-100 dark:bg-gray-900
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
        <li class="w-full border-t border-primary/10">
            <div class="flex items-center px-3 py-2 rounded-md cursor-pointer
                        hover:bg-[#0f172a] hover:text-blue-400
                        text-primary text-sm"
            >
                <i class="fas fa-{item.icon} w-5 mr-3"></i>

                <div class="flex flex-col">
                <p class="font-bold dark:text-gray-400 hover:text-blue-400">
                    {item.title}
                </p>
                <p class="text-xs dark:text-gray-500 hover:text-blue-300">
                    {item.text}
                </p>
                </div>
            </div>
        </li>
    {/each}
{/if}
</ul>
