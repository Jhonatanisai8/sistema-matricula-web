package com.isai.demowebregistrationsystem.controllers.apoderado;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/apoderado/layaout")
public class ApoderadoPanelController {

    @GetMapping
    public String index() {
        return "layouts/apoderado-layout";
    }

}
