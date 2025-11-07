import type { Rezervacija } from "./Reservation";

export interface Dan {
  id?: number;
  datum: string; // LocalDateTime kao ISO string ili samo yyyy-MM-dd
  naziv: string; // ako želiš ime dana, npr. “Subota”, može biti opcionalno
  boja: string;
  rezervacije: Rezervacija[];

  created?: string;
  updated?: string;
  createdBy?: string;
  updatedBy?: string;
}
