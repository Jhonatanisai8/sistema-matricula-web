package com.isai.demowebregistrationsystem.controllers.estudiante;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/estudiante/layaout")
public class EstudiantePanelController {

    @GetMapping
    public String index() {
        return "layouts/student-layout";
    }

}
