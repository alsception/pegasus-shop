export interface Reservation {
  id: number;
  dan: string; 
  vreme: string;
  ime: string;
  email: string;
  telefon: string;
  brojGostiju: number;
  menuStandard: number;
  menuGold: number;
  menuPremium: number;
  menuVege: number;
  menuX: number;
  poslano: boolean;
  potvrdjeno: boolean;
  dogovorio: string;
  napomena: string;
  rucak: boolean;
  vecera: boolean;
  vazno: boolean;
  otkazano: boolean;
  created?: string;
  updated?: string;
  createdBy?: string;
  updatedBy?: string;
}
