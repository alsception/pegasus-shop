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
      guest:true
    },
    "/logout": {
      label: "Odjavi se ",
      icon: "right-from-bracket",
      href: "#/logout",
      component: "Logout",
      disabled: false,
      hidden: false,
      admin: true,
      customer: true,
      default: false,
      guest: false
    },
    "/login": {
      label: "Prijavi se ",
      icon: "user",
      href: "#/login",
      component: "Login",
      disabled: false,
      hidden: false,
      admin: false,
      customer: false,
      default: false,
      guest: true
    },
    "/register": {
      label: "Registriraj se ",
      icon: "right-to-bracket",
      href: "#/register",
      component: "Register",
      disabled: false,
      hidden: true,
      admin: false,
      customer: false,
      default: false,
      guest: true
    },
  };