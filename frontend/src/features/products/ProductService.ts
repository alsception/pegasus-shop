import axios from "axios";
import { showErrorToast, showSuccessToast } from "../../core/toaster";
import { writable } from "svelte/store";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// workaround to make this reactivee to show loading spinner
export let addToCartLoading = writable(0); // or null

/**
 * Need to set axios and interceptor below to ensure that request is authorized with Bearer token
 */
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

export async function addToCart(productId: number): Promise<void> 
{
  addToCartLoading.set(productId);
  try {
    const response = await axiosInstance.post<{ message: string }>(
      "/cart/add",
      null,
      {
        params: {
          productId,
        },
      }
    );
    //We assume success if no error happened
    processSuccess(response);
  } catch (error: any) {
    processError(error);
  } finally {
    addToCartLoading.set(0);
  }
}

export function processSuccess(response: any) {
  // Display success message
  if (response.data && response.data.message) {
    showSuccessToast(response.data.message);
  }
}

export function processError(error: any) {
  // Extract message from error response
  let errorMessage = "Error adding product to cart: ";

  if (error.response && error.response.data) {
    // error.response.data.message should contain message
    if (error.response.data.message) {
      errorMessage += error.response.data.message;
    } else if (typeof error.response.data === "string") {
      errorMessage += error.response.data;
    }
  } else if (error.message) {
    errorMessage = error.message;
  }

  showErrorToast(errorMessage);
}