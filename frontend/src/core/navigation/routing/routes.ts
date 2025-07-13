import type { ComponentRegistry} from "../menu/MenuTypes";
import { navRoutes } from "../menu/navRoutes";//Routes are here
import Home from "../../Home.svelte";
import Users from "../../../features/users/UsersList.svelte";
import UserDetails from "../../../features/users/UserDetails.svelte";
import Products from "../../../features/products/ProductsList.svelte";
import Orders from "../../../features/orders/OrdersList.svelte";
import ProductDetail from "../../../features/products/ProductDetail.svelte";
import ProductAdministration from "../../../features/products/ProductAdministration.svelte";
import CartDetails from "../../../features/cart/CartDetails.svelte";
import Checkout from "../../../features/cart/Checkout.svelte";
import Pix from "../../../features/pix/Pix.svelte";
import Logout from "../../auth/Logout.svelte";
import Register from "../../auth/Register.svelte";

// Component Registry - central place for all components
// Whenever new component is added it should be imported here and added its /url
export const components: ComponentRegistry = 
{
    "/home": Home,
    "/": Home,
    "/products": Products,
    "/products/:id": ProductDetail,
    "/products/mngmt/:id": ProductAdministration,
    "/users": Users,
    "/users/:id": UserDetails,
    "/posts": null,
    "/BB_Lists": null,
    "/BB_Cards": null,
    "/cart": CartDetails,
    "/orders": Orders,
    "/checkout": Checkout,
    "/Todo": null,
    "/Hours": null,
    "/pix": Pix,
    "/logout": Logout,
    "/register": Register
};

// Helper function to generate flat routes for router configuration
// Return object is used for generating routes and displaying components
export function generateRoutes() 
{
    const routes: Record<string, any> = {};

    Object.entries(navRoutes).forEach(([path, routeInfo]) => 
    {       
        if (components[path]) 
        {
            routes[path] = components[path];
        }

        if (routeInfo.componentDetails && components[routeInfo.componentDetails]) 
        {
            routes[routeInfo.componentDetails] = components[routeInfo.componentDetails];
        }
    });
    return routes;
}