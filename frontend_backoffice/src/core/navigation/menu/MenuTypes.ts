/**
 * Predlozi poboljšanja:

    Zameni any tipove ako možeš:

        component: unknown je sigurniji nego any

        Ako koristiš Svelte: component: typeof SvelteComponent

        Ako koristiš React: component: React.ComponentType<any>

    componentDetails?: any – ako znaš čemu tačno služi, definiši interfejs.

    routes?: Record<string, SubRoute> – ovo je u redu ako string mora biti dinamički ključ (npr. "products/:id").

    Dodaj id ako ti treba za mapiranje ruta.
 */

export type Component = any // Replace with your actual component type

// Type for the entire navRoutes object
export type NavRoutesMap = Record<string, NavRoute>

export type Tag = {
  text: string
  class: string
}

export type SubRoute = {
  component: any
  [key: string]: any
}

export type NavRoute = {
  label: string
  icon: string
  href: string
  componentDetails?: any
  component: any // The component to render
  inProgress?: boolean
  tags?: Tag[]
  routes?: Record<string, SubRoute> // For sub-routes like /products/:id
  disabled?: boolean
  hidden?: boolean //TODO: dis doesnt actually work. Currently we hide for disabled
  admin?: boolean
  customer?: boolean
  default?: boolean
}

export type ComponentRegistry = Record<string, Component>
