package com.isai.demowebregistrationsystem.controllers.profesor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/profesor/layaout")
public class ProfesorPanelController {

    @GetMapping
    public String index() {
        return "layouts/profesor-layout";
    }

}
