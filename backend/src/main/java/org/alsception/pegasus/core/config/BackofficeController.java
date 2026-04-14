package org.alsception.pegasus.core.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BackofficeController {

    // Hvata tačno /backoffice ili /backoffice/, mora da ucitamo index.html (nemoze automatski), pa onda dalje ide svelte
    @GetMapping({"/backoffice", "/backoffice/"})
    public String index() {
        return "forward:/backoffice/index.html";
    }

    // Hvata sve ostale pod-rute (npr. /backoffice/users) i vraca na index
    @GetMapping("/backoffice/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/backoffice/index.html";
    }
}