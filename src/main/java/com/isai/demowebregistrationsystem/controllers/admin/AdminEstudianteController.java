package com.isai.demowebregistrationsystem.controllers.admin;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.exceptions.ValidationException;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteDetalleDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteListadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteRegistroDTO;
import com.isai.demowebregistrationsystem.services.EstudianteService;
import com.isai.demowebregistrationsystem.services.MatriculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/crear")
    public String mostrarFormularioCrearEstudiante(Model model) {
        model.addAttribute("estudiante", new EstudianteRegistroDTO());
        cargarDatosFormularioEstudiante(model); // Cargar listas para selects
        return "admin/estudiantes/crear_estudiante";
    }

    private void cargarDatosFormularioEstudiante(Model model) {
        model.addAttribute("apoderados", estudianteService.obtenerApoderadosDisponibles());
        model.addAttribute("grados", estudianteService.obtenerGradosDisponibles());
        model.addAttribute("estadosCiviles", estudianteService.getEstadosCiviles());
        model.addAttribute("generos", estudianteService.getGeneros());
        model.addAttribute("tiposDocumento", estudianteService.getTiposDocumento());
        model.addAttribute("tiposSangre", estudianteService.getTiposSangre());
        model.addAttribute("gradosAnteriores", estudianteService.getGradosAnteriores());
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarEstudiante(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EstudianteRegistroDTO estudianteDTO = estudianteService.obtenerEstudianteParaEdicion(id);
            model.addAttribute("estudiante", estudianteDTO);
            cargarDatosFormularioEstudiante(model);
            return "admin/estudiantes/crear_estudiante";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/estudiantes/lista";
        }
    }

    @PostMapping("/guardar")
    public String guardarEstudiante(@Valid @ModelAttribute("estudiante") EstudianteRegistroDTO estudianteDTO,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            cargarDatosFormularioEstudiante(model);
            model.addAttribute("errorMessage", "Por favor, corrige los errores en el formulario.");
            return "admin/estudiantes/crear_estudiante";
        }
        try {
            estudianteService.guardarEstudiante(estudianteDTO);
            String message = (estudianteDTO.getIdEstudiante() == null) ? "Estudiante creado exitosamente." : "Estudiante actualizado exitosamente.";
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/admin/estudiantes/lista";
        } catch (ValidationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            cargarDatosFormularioEstudiante(model);
            return "admin/estudiantes/crear_estudiante";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/estudiantes/lista";
        }
    }


}
