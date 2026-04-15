import type { ComponentRegistry}    from "../menu/MenuTypes";
import { navRoutes }                from "../menu/navRoutes";//Routes are here
import Home                         from "../../home/Home.svelte";
import Stats                         from "../../home/Stats.svelte";

import Users                        from "../../../features/users/UsersList.svelte";
import UserDetails                  from "../../../features/users/UserDetails.svelte";
import MyAccount                    from "../../../features/users/MyAccount.svelte";

import Artikli                      from "../../../features/artikli/ArtikliList.svelte";
import ArtikliAdministration        from "../../../features/artikli/ArtikliAdministration.svelte";
import Products                     from "../../../features/products/ProductsListBarbacoa.svelte";
import ProductDetail                from "../../../features/products/ProductPage.svelte";
import ProductsAdminList            from "../../../features/products/InventoryList.svelte";
import ProductAdministration        from "../../../features/products/ProductAdministration.svelte";

import Tables                       from "../../../features/tables/Tables.svelte";
import Reservations                 from "../../../features/reservations/Reservations.svelte";

import CartDetails                  from "../../../features/cart/CartDetails.svelte";
import CheckoutDelivery             from "../../../features/cart/CheckoutDelivery.svelte";
import CheckoutPayment              from "../../../features/cart/CheckoutPayment.svelte";
import Orders                       from "../../../features/orders/OrdersList.svelte";
import OrderDetails                 from "../../../features/orders/OrderDetails.svelte";

import Pix                          from "../../../features/pix/Pix.svelte";

import Logout                       from "../../auth/Logout.svelte";
import Register                     from "../../auth/Register.svelte";
import NotFound                     from "../error/NotFound.svelte"
import StripeCheckout               from "../../../features/stripe/StripeCheckout.svelte";
import Completition                 from "../../../features/cart/Completion.svelte";
import OrderPage from "../../../features/orders/OrderPage.svelte";

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
    "/stats": Stats,
    "/products": Products,
    "/products/:id": ProductDetail,
    "/inventory": ProductsAdminList,    
    "/inventory/:id": ProductAdministration,
    "/tables": Tables,
    "/reservations": Reservations,
    "/users": Users,
    "/users/:id": UserDetails,
    "/users/my-account": MyAccount,     //TODO: Dali da ostavimo /users/my-account ili, ako ne onda mora da bude secured /users endpoint da nebi user dobio nekog ko nije, osim ako nije admin
                                        // I fali za submit endpoint
                                        //Takodje kad se zatvori user details, ako je bio my account netrab nazad na users da ide nego na home.
    "/posts": null,
    "/BB_Lists": null,
    "/BB_Cards": null,
    "/cart": CartDetails,
    "/orders/:id": OrderPage,
    "/orders": Orders,
    "/checkout": CheckoutDelivery,
    "/pay/:id": CheckoutPayment,
    "/completion": Completition,
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
    routes["/stats"] = Stats;
    routes["/artikli"] = Artikli;
    routes["/artikli/:id"] = ArtikliAdministration;
    routes["/users/my-account"] = MyAccount;
    routes["/orders/:id"] = OrderDetails;
    routes["/inventory"] = ProductsAdminList;
    routes["/pay/:id"] = CheckoutPayment;
    routes["/stripe-checkout"] = StripeCheckout;
    routes["/completion"] = Completition;

    //Not found component: This must be last added (order matters), otherwise it catches all
    routes["*"] = NotFound; 
    
    return routes;
}
