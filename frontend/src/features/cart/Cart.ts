import type { Product } from "../products/Product";
import type { User } from "../users/User";

export interface Cart 
{
    id: number;
    totalPrice: number;
    totalProductTypes: number;//TODO: refactor to totalProducts
    totalItems: number;
    items?: CartItem[];
    user?: User;
}

interface CartItem 
{
    id: number;    
    quantity: number;
    product: Product;
}