<script lang="ts">
  import { DotLottieSvelte } from "@lottiefiles/dotlottie-svelte";
  import { showErrorToast } from "../../core/utils/toaster";
  import type { Order } from "./Order";
  import axios from "axios";
  import { createEventDispatcher, onDestroy } from "svelte";

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  export let order: Order;

  let loading = false;
  let loadingDone = false;
  const lottieAnimationUrl = "/lottie/success1.lottie"; //or check2.lottie or success3

  const dispatch = createEventDispatcher();

  const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Add Bearer token if available
  axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("token"); // or getToken() if you have a helper
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  async function updateOrderState(id: any, status: any) {
    if (!id || !status) return;
    loading = true;

    try {
      const response = axiosInstance.put("/orders/update-status/" + id, null, {
        params: {
          status: status,
        },
      });
      await response;
      loadingDone = true;
      setTimeout(() => {
        dispatch("orderUpdateCompleted", { id, status });
      }, 2000); //sacekamo dve sekunde
    } catch (err) {
      showErrorToast("Greška");
    } finally {
      loading = false;
    }
  }

  onDestroy(() => {
    loadingDone = false;
  });
</script>

<div
  class="ml-auto w-24 mr-2 overflow-hidden relative h-12 flex items-center justify-center"
>
  {#if loadingDone}
    <div
      class="lottie-container bg-transparent absolute inset-0 w-full h-full"
      
    >
      <DotLottieSvelte
        src={lottieAnimationUrl}
        loop={false}
        autoplay={true}
        speed={1.5}
      />
    </div>
  {:else}
    <button
      class="btn btn-sm btn-success text-info-content w-full"
      on:click={(e) => {
        e.preventDefault();
        updateOrderState(order.id, "READY");
      }}
    >
      {#if loading}
        <span class="loading loading-dots loading-xs"></span>
      {:else}
        SPREMNO
      {/if}
    </button>
  {/if}
</div>

<style>
  .lottie-container {
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: visible; /* Osigurava da ništa ne seče animaciju */
    /* 9999px garantuje oblik pilule bez obzira na širinu */
    border-radius: 9999px !important;
  }
</style>
