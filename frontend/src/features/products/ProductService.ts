import axios from "axios";
import { showErrorToast } from "../../core/utils/toaster";
import { writable } from "svelte/store";
import { cartItemsCounter } from './../../core/services/CheckoutStore';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const STORAGE_KEY = 'addedItemsMap';

/**
 * UPOZORENJE: Sledi ai slop -.-
 * 
 * TODO:
 * 1. Treba da se resetuju svi objekti kad je Checkout. [done]
 * 2. Treba backend za reduce [done]
 * 3. Treba da se sinhronizuju objekti kad se menja iz Checkouta
 * 
 */

// 1. Učitavanje (Map id -> quantity)
const saved = typeof window !== 'undefined' ? localStorage.getItem(STORAGE_KEY) : null;
const initialData: Record<number, number> = saved ? JSON.parse(saved) : {};

export const addedItems = writable<Record<number, number>>(initialData);
export const loadingItems = writable(new Set<number>());

/**
 * Need to set axios and interceptor below to ensure that request is authorized with Bearer token
 */
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 2. Automatsko čuvanje i ažuriranje globalnog brojača
addedItems.subscribe((items) => {
    if (typeof window !== 'undefined') {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
        // Ažuriraj totalni broj u CheckoutStore
        const total = Object.values(items).reduce((acc, curr) => acc + curr, 0);
        cartItemsCounter.set(total);
    }
});

export const ProductService = 
{
  async updateQuantity(productId: number, delta: number) 
  {
    loadingItems.update(s => { s.add(productId); return s; });    

    try 
    {      
      let response = await axiosInstance.post<{qt: any; message: string }>
      (
        ( delta > 0 ) ? "/cart/add" : "/cart/reduce",
        null,
        {
          params: {
            productId,
          },
        }
      );   

      let newQt = response.data.qt;      

      addedItems.update(items => {
          const currentQty = items[productId] || 0;
          const newQty = newQt;
          
          if (newQty <= 0) {
              delete items[productId];
          } else {
              items[productId] = newQty;
          }
          return { ...items };
      });
    } 
    catch (error: any) 
    {
      // Ako server vrati error (npr. 400, 422, 500)
      if (error.response && error.response.data) 
      {        
        const errorMessage = error.response.data.message;
        showErrorToast(errorMessage);        
      } 
      else 
      {
        // U slučaju mrežne greške ili ako server ne odgovori
        showErrorToast(error.message);
      }
    }
    finally 
    {
        loadingItems.update(s => { s.delete(productId); return s; });
    }
  },

  clearCart() 
  {
    // 1. Resetuj mapu dodatih artikala na prazan objekat
    addedItems.set({});

    // 2. Resetuj globalni brojač na 0
    cartItemsCounter.set(0);

    // 3. Obriši podatke iz localStorage
    if (typeof window !== 'undefined') {
        localStorage.removeItem('addedItemsMap');
        localStorage.removeItem('pgs-cart-items-counter');
    }
  }
};

export async function addToCart(productId: number) {
    await ProductService.updateQuantity(productId, 1);
}

// Add Bearer token if available
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token"); // or getToken() if you have a helper
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

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