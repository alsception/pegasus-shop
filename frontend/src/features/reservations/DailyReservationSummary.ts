import type { Reservation } from "./Reservation";

export interface DailyReservationSummary {
  date: string; // ISO date format: "2025-11-15"
  totalGuests: number;
  totalMenuStandard: number;
  totalMenuGold: number;
  totalMenuPremium: number;
  totalMenuVege: number;
  totalReservations: number;
  reservations: Reservation[];
}