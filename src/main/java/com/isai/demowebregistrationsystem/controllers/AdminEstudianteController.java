package com.isai.demowebregistrationsystem.controllers;

import com.isai.demowebregistrationsystem.model.entities.Estudiante;
import com.isai.demowebregistrationsystem.services.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// AdminEstudianteController.java
@Controller
@RequestMapping("/admin/estudiantes")
@RequiredArgsConstructor
public class AdminEstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("idEstudiante").descending());
        Page<Estudiante> estudiantes = estudianteService.listarEstudiantes(filtro, pageable);

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("filtro", filtro);

        // Agregamos los datos para la paginación
        model.addAttribute("paginaActual", estudiantes.getNumber());
        model.addAttribute("totalPaginas", estudiantes.getTotalPages());
        model.addAttribute("tamanioPagina", estudiantes.getSize());

        return "admin/lista-estudiantes";
    }


    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        return "admin/lista-estudiantes";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Estudiante estudiante, RedirectAttributes attrs) {
        estudianteService.registrar(estudiante);
        attrs.addFlashAttribute("mensaje", "Estudiante registrado correctamente.");
        return "redirect:/admin/estudiantes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Estudiante estudiante = estudianteService.obtenerPorId(id);
        model.addAttribute("estudiante", estudiante);
        return "admin/estudiantes/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Estudiante estudiante, RedirectAttributes attrs) {
        estudianteService.actualizar(id, estudiante);
        attrs.addFlashAttribute("mensaje", "Estudiante actualizado correctamente.");
        return "redirect:/admin/estudiantes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes attrs) {
        estudianteService.eliminar(id);
        attrs.addFlashAttribute("mensaje", "Estudiante eliminado correctamente.");
        return "redirect:/admin/estudiantes";
    }
}
