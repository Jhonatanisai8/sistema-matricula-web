package com.isai.demowebregistrationsystem.controllers.admin;

import com.isai.demowebregistrationsystem.model.dtos.DocenteDTO;
import com.isai.demowebregistrationsystem.model.dtos.DocenteRegistroDTO;
import com.isai.demowebregistrationsystem.services.DocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/docentes")
public class AdminDocenteController {

    private final DocenteService docenteService;

    // Método para mostrar el formulario de registro de docente
    @GetMapping("/registro")
    public String mostrarFormularioRegistroDocente(Model model) {
        model.addAttribute("docenteRegistroDTO", new DocenteRegistroDTO());
        // Listas para dropdowns/radios
        model.addAttribute("generos", List.of("Masculino", "Femenino"));
        model.addAttribute("tiposDocumento", List.of("DNI", "Carnet Extranjería", "Pasaporte"));
        model.addAttribute("estadosCivil", List.of("Soltero", "Casado", "Divorciado", "Viudo"));
        model.addAttribute("tiposContrato", List.of("Temporal", "Indefinido", "Por Horas", "Practicante"));
        model.addAttribute("estadosLaboral", List.of("Activo", "Licencia", "Suspendido", "Inactivo"));

        return "admin/registro-docente"; // Vista del formulario
    }

    // Método para procesar el envío del formulario de registro de docente
    @PostMapping("/registro")
    public String registrarDocente(@Valid @ModelAttribute("docenteRegistroDTO") DocenteRegistroDTO docenteRegistroDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (result.hasErrors()) {
            // Si hay errores de validación, recargar las listas de opciones
            model.addAttribute("generos", List.of("Masculino", "Femenino"));
            model.addAttribute("tiposDocumento", List.of("DNI", "Carnet Extranjería", "Pasaporte"));
            model.addAttribute("estadosCivil", List.of("Soltero", "Casado", "Divorciado", "Viudo"));
            model.addAttribute("tiposContrato", List.of("Temporal", "Indefinido", "Por Horas", "Practicante"));
            model.addAttribute("estadosLaboral", List.of("Activo", "Licencia", "Suspendido", "Inactivo"));
            return "admin/registro-docente"; // Vuelve al formulario con errores
        }

        try {
            DocenteDTO docenteGuardado = docenteService.registrarDocente(docenteRegistroDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Docente registrado exitosamente. Usuario: " + docenteGuardado.getUsername() +
                            ", Contraseña: " + docenteGuardado.getPasswordGenerada());
            // Opcional: Podrías redirigir a una página de confirmación con los datos del usuario/contraseña
            return "redirect:/admin/docentes/registro";
        } catch (IllegalArgumentException e) {
            // Manejar errores de lógica de negocio (DNI o código duplicado)
            if (e.getMessage().contains("DNI")) {
                result.rejectValue("dni", "duplicate.dni", e.getMessage());
            } else if (e.getMessage().contains("Código de Docente")) {
                result.rejectValue("codigoDocente", "duplicate.codigoDocente", e.getMessage());
            } else {
                result.rejectValue(null, null, e.getMessage()); // Error global
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Recargar listas para el formulario
            model.addAttribute("generos", List.of("Masculino", "Femenino", "Otro"));
            model.addAttribute("tiposDocumento", List.of("DNI", "Carnet Extranjería", "Pasaporte"));
            model.addAttribute("estadosCivil", List.of("Soltero", "Casado", "Divorciado", "Viudo"));
            model.addAttribute("tiposContrato", List.of("Temporal", "Indefinido", "Por Horas", "Practicante"));
            model.addAttribute("estadosLaboral", List.of("Activo", "Licencia", "Suspendido", "Inactivo"));
            return "admin/registro-docente"; // Vuelve al formulario con el mensaje de error
        } catch (IllegalStateException e) {
            // Manejar errores de generación de usuario (si el username no se pudo hacer único)
            result.rejectValue(null, null, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Recargar listas para el formulario
            model.addAttribute("generos", List.of("Masculino", "Femenino", "Otro"));
            model.addAttribute("tiposDocumento", List.of("DNI", "Carnet Extranjería", "Pasaporte"));
            model.addAttribute("estadosCivil", List.of("Soltero", "Casado", "Divorciado", "Viudo"));
            model.addAttribute("tiposContrato", List.of("Temporal", "Indefinido", "Por Horas", "Practicante"));
            model.addAttribute("estadosLaboral", List.of("Activo", "Licencia", "Suspendido", "Inactivo"));
            return "admin/registro-docente";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al registrar el docente: " + e.getMessage());
            // Recargar listas para el formulario
            model.addAttribute("generos", List.of("Masculino", "Femenino", "Otro"));
            model.addAttribute("tiposDocumento", List.of("DNI", "Carnet Extranjería", "Pasaporte"));
            model.addAttribute("estadosCivil", List.of("Soltero", "Casado", "Divorciado", "Viudo"));
            model.addAttribute("tiposContrato", List.of("Temporal", "Indefinido", "Por Horas", "Practicante"));
            model.addAttribute("estadosLaboral", List.of("Activo", "Licencia", "Suspendido", "Inactivo"));
            return "admin/registro-docente";
        }
    }

    // Método para mostrar la lista de docentes
    @GetMapping
    public String listarDocentes(Model model) {
        List<DocenteDTO> docentes = docenteService.listarDocentes();
        model.addAttribute("docentes", docentes);
        return "admin/lista-docentes"; // Vista de la tabla de docentes
    }
}