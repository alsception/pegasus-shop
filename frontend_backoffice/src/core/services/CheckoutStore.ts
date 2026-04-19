/***
 * Ovde cuvamo sta se nalazi u Cartu da znamo posle na Products page koje dugme sta da prikazemo
 */
import { writable } from 'svelte/store';

const CART_ITEMS_STORAGE_KEY = "pgs-cart-items-counter";
const initialTotalItems = Number(localStorage.getItem(CART_ITEMS_STORAGE_KEY)) || 0;

const CART_TOTAL_KEY = "pgs-cart-total-counter";
const initialCartTotal = Number(localStorage.getItem(CART_TOTAL_KEY)) || 0;

const CART_TIMESTAMP_KEY = "pgs-cart-timestamp";
const initialCartTimestamp = Number(localStorage.getItem(CART_TIMESTAMP_KEY)) || 0;

///////////////////////////////////////////////////////////////////////////////

export const cartTotalCounter = writable<number>(initialCartTotal);

cartTotalCounter.subscribe((value) => {
    localStorage.setItem(CART_TOTAL_KEY, value.toString());
});

//////////////////////////////////////////////////////////////////////////////

export const cartItemsCounter = writable<number>(initialTotalItems);

cartItemsCounter.subscribe((value) => {
    localStorage.setItem(CART_ITEMS_STORAGE_KEY, value.toString());
});

//////////////////////////////////////////////////////////////////////////////

export const cartTimestamp = writable<number>(initialCartTimestamp);

cartTimestamp.subscribe((value) => {
    localStorage.setItem(CART_TIMESTAMP_KEY, value.toString());
});