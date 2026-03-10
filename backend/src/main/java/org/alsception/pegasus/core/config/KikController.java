package org.alsception.pegasus.core.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KikController {

    //za sad nekoristimo

    // Ova ruta hvata točno "/kik" ili "/kik/" i šalje na index.html
    @GetMapping("/kik")
    public String index() {
        return "forward:/kik/index.html";
    }

    // Ako koristiš Svelte-spa-router, ovo pomaže da refresh na pod-rutama radi
    @GetMapping("/kik/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/kik/index.html";
    }
}