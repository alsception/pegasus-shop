import type { User } from "../users/User";

export interface PGSOrder {
  other: any;
  comment: string | number | Date | null | undefined;
  id: number;
  code: string;
  user?: User; // Optional since it has @JsonBackReference
  email: string;
  name: string;
  address: string;
  paymentMethod: string;
  price?: number; // BigDecimal maps to number in TS, nullable so optional
  items: PGSOrderItem[];
  created?: string; // LocalDateTime as ISO string
  modified?: string; // LocalDateTime as ISO string, nullable so optional
  status?: string;
}

export interface PGSOrderItem {
  id: number;
  // order?: PGSOrder; // Not included due to @JsonBackReference
  // Add other PGSOrderItem properties here
  quantity?: number;
  price?: number;
  productName?: string;
  // ... other fields from your PGSOrderItem entity
}