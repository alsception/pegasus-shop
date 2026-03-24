import axios from "axios";
import { showErrorToast } from "../../core/utils/toaster";
import { writable } from "svelte/store";
import { cartItemsCounter } from './../../core/services/CheckoutStore';


const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// workaround to make this reactivee to show loading spinner
export const addedItems = writable(new Set());
export const loadingItems = writable(new Set());

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

  //add product id to set of ids
  loadingItems.update(prevSet => {
      prevSet.add(productId);
      return prevSet; 
    });

  try {
    await axiosInstance.post<{ message: string }>(
      "/cart/add",
      null,
      {
        params: {
          productId,
        },
      }
    );
    // We assume success if no error happened
    // No more toasts. we will show lottie animation in button
    loadingItems.update(prevSet => {
      prevSet.delete(productId);
      return prevSet; 
    });

    addedItems.update(prevSet => {
        prevSet.add(productId);
        return prevSet; 
    });

    cartItemsCounter.update(n => n + 1);
  }
  catch (error: any) 
  {
    processError(error);
  } 
  finally 
  {
    loadingItems.update(prevSet => {
      prevSet.delete(productId);
      return prevSet; 
    });
  }
}

export function resetCartItems() 
{
  //console.log('reseting cart items');//TODO: CUDNO PONASANJE, ako nema ovog console.loga izlgeda kao da neresetuje
  addedItems.set(new Set());
  loadingItems.set(new Set());
  cartItemsCounter.update(n => 0);
}

export function processError(error: any) 
{
  // Extract message from error response
  let errorMessage = "Error adding product to cart: ";

  if (error.response && error.response.data) 
  {
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