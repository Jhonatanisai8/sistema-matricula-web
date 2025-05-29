package com.isai.demowebregistrationsystem.controllers;

import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.services.impl.ApoderadoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/apoderados")
public class AdminApoderadoController {
    private final ApoderadoServiceImpl apoderadoServiceImpl;

  /*
    @GetMapping
    public String listarApoderados(Model model) {
        List<ApoderadoDTO> apoderados = apoderadoServiceImpl.listarApoderados();
        model.addAttribute("apoderados", apoderados);
        return "admin/lista-apoderados"; // This view will be src/main/resources/templates/admin/lista-apoderados.html
    }
   */

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String listarApoderados(
            @RequestParam(value = "terminoBusqueda", required = false) String terminoBusqueda, Model model) {
        //listamos todos los apoderados
        List<ApoderadoDTO> apoderadoDTOS = apoderadoServiceImpl.listarApoderados();
        if (terminoBusqueda != null && !terminoBusqueda.trim().isEmpty()) {
            apoderadoDTOS = apoderadoServiceImpl.buscarApoderados(terminoBusqueda);
            model.addAttribute("terminoBusqueda", terminoBusqueda);
        } else {
            apoderadoDTOS = apoderadoServiceImpl.listarApoderados();
        }
        model.addAttribute("apoderados", apoderadoDTOS);
        return "admin/lista-apoderados";
    }
}
