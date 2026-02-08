import type { FPGSUser } from "../../features/users/FPGSUser";
import type { Product } from "../products/Product";

export interface Order {
  other: any;
  comment: string | number | Date | null | undefined;
  id: number;
  code: string;
  user?: FPGSUser; // Optional since it has @JsonBackReference
  stol?: string;
  email: string;
  name: string;
  address: string;
  paymentMethod: string;
  paymentStatus: string;
  currency: string;
  price?: number; // BigDecimal maps to number in TS, nullable so optional
  items: OrderItem[];
  created?: string; // LocalDateTime as ISO string
  modified?: string | null; // LocalDateTime as ISO string, nullable so optional
  status?: string;
}

export interface OrderItem {
  id: number;
  // order?: PGSOrder; // Not included due to @JsonBackReference
  // Add other PGSOrderItem properties here
  quantity?: number;
  price?: number;
  productName?: string;
  product?: Product;
  // ... other fields from your PGSOrderItem entity
}