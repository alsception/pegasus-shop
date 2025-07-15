import type { Product } from "../products/Product";
import type { FPGSUser } from "../../core/users/FPGSUser";

export interface Cart 
{
    id: number;
    totalPrice: number;
    subTotalPrice?: number;
    totalProductTypes: number;//TODO: refactor to totalProducts
    totalItems: number;
    items?: CartItem[];
    user?: FPGSUser;
    shippingCost?: number;
    taxAmount?: number;
    taxPercent?: number;
    created: Date;
    modified: Date;

}

interface CartItem 
{
    id: number;    
    quantity: number;
    product: Product;
}