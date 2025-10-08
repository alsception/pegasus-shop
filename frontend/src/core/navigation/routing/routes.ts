import type { ComponentRegistry}    from "../menu/MenuTypes";
import { navRoutes }                from "../menu/navRoutes";//Routes are here
import Home                         from "../../Home.svelte";

import Users                        from "../../../features/users/UsersList.svelte";
import UserDetails                  from "../../../features/users/UserDetails.svelte";
import MyAccount                    from "../../../features/users/MyAccount.svelte";

import Products                     from "../../../features/products/ProductsList.svelte";
import ProductDetail                from "../../../features/products/ProductPage.svelte";
import ProductAdministration        from "../../../features/products/ProductAdministration.svelte";

import Tables                     from "../../../features/tables/Tables.svelte";

import CartDetails                  from "../../../features/cart/CartDetails.svelte";
import Checkout                     from "../../../features/cart/Checkout.svelte";
import Orders                       from "../../../features/orders/OrdersList.svelte";
import OrderDetails                 from "../../../features/orders/OrderDetails.svelte";

import Pix                          from "../../../features/pix/Pix.svelte";

import Logout                       from "../../auth/Logout.svelte";
import Register                     from "../../auth/Register.svelte";
import NotFound                     from "../error/NotFound.svelte"

// Component Registry - central place for all components
// Whenever new component is added it should be imported here and added its /url

/****************************************************
 *                                                  *
 * U navRoutes.ts isto treba dodati!                *
 *                                                  *
 *                                                  *                    
 ****************************************************/

export const components: ComponentRegistry = 
{
    "/": Home,
    "/home": Home,
    "/products": Products,
    "/products/:id": ProductDetail,
    "/products/mngmt/:id": ProductAdministration,
    "/tables": Tables,
    "/users": Users,
    "/users/:id": UserDetails,
    "/users/my-account": MyAccount,     //TODO: Dali da ostavimo /users/my-account ili, ako ne onda mora da bude secured /users endpoint da nebi user dobio nekog ko nije, osim ako nije admin
                                        // I fali za submit endpoint
                                        //Takodje kad se zatvori user details, ako je bio my account netrab nazad na users da ide nego na home.
    "/posts": null,
    "/BB_Lists": null,
    "/BB_Cards": null,
    "/cart": CartDetails,
    "/orders/:id": OrderDetails,
    "/orders": Orders,
    "/checkout": Checkout,
    "/Todo": null,
    "/Hours": null,
    "/pix": Pix,
    "/logout": Logout,
    "/register": Register,
    "*": NotFound, // catch-all
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
    
    //I dont know if these routes are found in navroutes, and I want them to be all the time
    routes["/"] = Home;
    routes["/users/my-account"] = MyAccount;
    routes["/orders/:id"] = OrderDetails;

    //Not found component: This must be last added (order matters), otherwise it catches all
    routes["*"] = NotFound; 
    
    return routes;
}