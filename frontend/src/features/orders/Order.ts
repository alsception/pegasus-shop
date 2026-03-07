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
  created?: string  | null; // LocalDateTime as ISO string
  modified?: string | null; // LocalDateTime as ISO string, nullable so optional
  upripremiAt?: string | null;
  spremnoAt?: string | null;
  servedAt?: string | null;
  status?: string;
  taxPercent?: number | string;
  discount?: number | string;
}

export interface OrderItem {
  id: number;
  // order?: PGSOrder; // Not included due to @JsonBackReference
  // Add other PGSOrderItem properties here
  quantity: number;
  price?: number;
  productName?: string;
  product: Product;
  productId: number;
  // ... other fields from your PGSOrderItem entity
}