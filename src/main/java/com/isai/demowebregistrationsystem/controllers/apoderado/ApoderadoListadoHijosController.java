package com.isai.demowebregistrationsystem.controllers.apoderado;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.model.dtos.apoderado.EstudianteListaApoderadoDTO;
import com.isai.demowebregistrationsystem.services.ApoderadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(path = "/apoderado")
public class ApoderadoListadoHijosController {

    private final ApoderadoService apoderadoService;

    public ApoderadoListadoHijosController(ApoderadoService apoderadoService) {
        this.apoderadoService = apoderadoService;
    }

    @GetMapping("/mis_hijos")
    public String showMisHijos(
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            String usernameApoderado = principal.getName();
            List<EstudianteListaApoderadoDTO> hijos = apoderadoService.obtenerHijosDelApoderado(usernameApoderado);
            model.addAttribute("hijos", hijos);
            return "apoderado/mis_hijos";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Apoderado no encontrado. Por favor, intente de nuevo.");
            return "redirect:/logout";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar la lista de hijos: " + e.getMessage());
            return "redirect:/apoderado/dashboard"; // Redirigir al dashboard con error
        }
    }
}
