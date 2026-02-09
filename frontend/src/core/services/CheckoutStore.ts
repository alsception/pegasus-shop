/***
 * Prilikom checkouta kad se otvori stol, ovde cemo sacuvati podatke o stolu
 */
import { writable } from 'svelte/store';

const STORAGE_KEY = "pgs-broj-stola";

// Dohvaćamo početnu vrijednost iz storage-a (ako postoji)
const pocetnaVrijednost = String(localStorage.getItem(STORAGE_KEY)) || "";

export const brojStola = writable<String>(pocetnaVrijednost);

// Pretplata: svaki put kad se store promijeni, spremi u localStorage
brojStola.subscribe((value) => {
    localStorage.setItem(STORAGE_KEY, value.toString());
});