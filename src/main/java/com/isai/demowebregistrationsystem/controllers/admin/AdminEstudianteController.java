package com.isai.demowebregistrationsystem.controllers.admin;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteDetalleDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteListadoDTO;
import com.isai.demowebregistrationsystem.services.EstudianteService;
import com.isai.demowebregistrationsystem.services.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/admin/estudiantes")
public class AdminEstudianteController {

    private final EstudianteService estudianteService;

    private final MatriculaService matriculaService;

    @GetMapping("/lista")
    public String listarEstudiantes(
            Model model,
            @RequestParam(name = "success", required = false) String successMessage,
            @RequestParam(name = "error", required = false) String errorMessage,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "activo", required = false) Boolean activo,
            @PageableDefault(size = 10, sort = "persona.apellidos", direction = Sort.Direction.ASC) Pageable pageable) {


        String sortField = pageable.getSort().stream().findFirst().map(Sort.Order::getProperty).orElse(null);
        String sortDirection = pageable.getSort().stream().findFirst().map(order -> order.getDirection().name()).orElse(null);

        Page<EstudianteListadoDTO> estudiantesPage = estudianteService.listarEstudiantesPaginado(keyword, activo, pageable);

        model.addAttribute("estudiantesPage", estudiantesPage);


        model.addAttribute("currentPage", estudiantesPage.getNumber());
        model.addAttribute("totalPages", estudiantesPage.getTotalPages());
        model.addAttribute("totalItems", estudiantesPage.getTotalElements());
        model.addAttribute("pageSize", estudiantesPage.getSize());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection != null ? sortDirection.toLowerCase() : null);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activo", activo != null ? String.valueOf(activo) : "");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "admin/estudiantes/lista_estudiantes";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalleEstudiante(@PathVariable("id") Integer id, Model model,
                                       @RequestParam(name = "success", required = false) String successMessage,
                                       @RequestParam(name = "error", required = false) String errorMessage,
                                       RedirectAttributes redirectAttributes) { // Added RedirectAttributes
        try {
            EstudianteDetalleDTO estudiante = estudianteService.obtenerEstudianteDetalle(id);
            model.addAttribute("estudiante", estudiante);
            if (successMessage != null) {
                model.addAttribute("successMessage", successMessage);
            }
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
            }
            return "admin/estudiantes/detalle_estudiante";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/estudiantes/lista";
        }
    }
}
