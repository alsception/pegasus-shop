import type { NavRoutesMap } from "./MenuTypes";

/***
 * This object is used for generating main menu
 */

export const navRoutes: NavRoutesMap = 
{
    "/home": {
      label: "Home",
      icon: "home",
      href: "#/home",
      component: "/home",
    },
    "/users": {
      label: "Users",
      icon: "users",
      href: "#/users",
      component: "/users",
      componentDetails: "/users/:id",
    },
    "/products": {
      label: "Products",
      icon: "box",
      href: "#/products",
      component: "/products",
      componentDetails: "/products/:id",
    },
    "/products/mngmt/:id": {
      label: "Products administration",
      icon: "box",
      href: "#/products?listView=true",
      component: null,
      componentDetails: "/products/mngmt/:id",
    },
    "/orders": {
      label: "Orders",
      icon: "truck",
      href: "#/orders",
      component: null,
      disabled: false,
    },
    "/cart": {
      label: "My cart",
      icon: "shopping-basket",
      href: "#/cart",
      component: null,
      disabled: false,
    },
    "/checkout": {
      label: "Checkout",
      icon: "credit-card",
      href: "#/checkout",
      component: null,
      disabled: false,
      hidden: true,
    },   
    "/statistics": {
      label: "Statistics",
      icon: "bar-chart",
      href: "#/statistics",
      component: null,
      disabled: true,
    },
    "/catalogs": {
      label: "Catalogs",
      icon: "file",
      href: "",
      component: null,
      disabled: true,
    },    
    "/pix": {
      label: "Pictures",
      icon: "image",
      href: "#/pix",
      component: null,
      disabled: false,
      hidden: false,
    },  
    "/logout": {
      label: "Logout",
      icon: "right-from-bracket",
      href: "#/logout",
      component: "Logout",
      disabled: false,
      hidden: false,
    },
  };