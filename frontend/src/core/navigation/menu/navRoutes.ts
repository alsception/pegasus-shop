import type { NavRoutesMap } from "./MenuTypes";

/***
 * This object is used for generating main menu
 */

export const navRoutes: NavRoutesMap = 
{
    // home mora da stoji ovde ali ga necemo prikazati u meniju
    "/home": {
      label: "Početak",
      icon: "home",
      href: "#/home",
      component: "/home",
      admin: true,
      customer: false,
      default: false,
    },     
    "/products": {
      label: "Jelovnik",
      icon: "book",
      href: "#/products",
      component: "/products",
      componentDetails: "/products/:id",
      admin: true,
      customer: true,
      default: true,
    },   
   
    "/cart": {
      label: "Košarica",
      icon: "shopping-basket",
      href: "#/cart",
      component: null,
      disabled: false,
      admin: true,
      customer: true,
    },
    "/checkout": {
      label: "Checkout",
      icon: "credit-card",
      href: "#/checkout",
      component: null,
      disabled: false,
      hidden: true,
      admin: true,
      customer: true,
    },   
    "/orders": {
      label: "Narudžbe",
      icon: "truck",
      href: "#/orders",
      component: null,
      disabled: false,
      admin: true,
      customer: true,
    },
    "/logout": {
      label: "Odjavi se ",
      icon: "right-from-bracket",
      href: "#/logout",
      component: "Logout",
      disabled: false,
      hidden: false,
      admin: true,
      customer: false,
      default: true,
    },
  };