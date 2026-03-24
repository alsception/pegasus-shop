/***
 * Prilikom checkouta kad se otvori stol, ovde cemo sacuvati podatke o stolu
 * 
 * 2026/03/24 nemamo vise stolove, ali cemo sacuvati podatke o cartu
 */
import { writable } from 'svelte/store';

//1. Table logic 
const STORAGE_KEY = "pgs-broj-stola";

// Dohvaćamo početnu vrijednost iz storage-a (ako postoji)
const pocetnaVrijednost = String(localStorage.getItem(STORAGE_KEY)) || "";

export const brojStola = writable<String>(pocetnaVrijednost);

// Pretplata: svaki put kad se store promijeni, spremi u localStorage
brojStola.subscribe((value) => {
    localStorage.setItem(STORAGE_KEY, value.toString());
});

//2. Cart counter logic: TODO: Umesto da nestane kad se refreshuje trebalo bi ucitati Cart sa servera.
const CART_ITEMS_STORAGE_KEY = "pgs-cart-items";
const pocetnaVrijednostCart = Number(localStorage.getItem(CART_ITEMS_STORAGE_KEY)) || 0;
export const cartItemsCounter = writable<number>(pocetnaVrijednostCart);
cartItemsCounter.subscribe((value) => {
    localStorage.setItem(STORAGE_KEY, value.toString());
});
