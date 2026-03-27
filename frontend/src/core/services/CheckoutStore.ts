/***
 * Ovde cuvamo sta se nalazi u Cartu da znamo posle na Products page koje dugme sta da prikazemo
 */
import { writable } from 'svelte/store';

const CART_ITEMS_STORAGE_KEY = "pgs-cart-items-counter";
const initialTotalItems = Number(localStorage.getItem(CART_ITEMS_STORAGE_KEY)) || 0;

export const cartItemsCounter = writable<number>(initialTotalItems);

cartItemsCounter.subscribe((value) => {
    localStorage.setItem(CART_ITEMS_STORAGE_KEY, value.toString());
});