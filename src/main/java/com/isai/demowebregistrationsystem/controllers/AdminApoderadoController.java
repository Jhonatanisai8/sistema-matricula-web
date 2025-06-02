package com.isai.demowebregistrationsystem.controllers;

import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.ApoderadoRegistroDTO;
import com.isai.demowebregistrationsystem.services.impl.ApoderadoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

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

    // Método para mostrar el formulario de registro (GET)
    @GetMapping("/registro")
    public String mostrarFormularioRegistroApoderado(Model model) {
        // Si hay un apoderadoRegistroDTO como flash attribute (debido a un error previo), se usará.
        // Si no, se crea uno nuevo.
        if (!model.containsAttribute("apoderadoRegistroDTO")) {
            model.addAttribute("apoderadoRegistroDTO", new ApoderadoRegistroDTO());
        }

        // para el campo genero
        model.addAttribute("generos", List.of("Masculino", "Femenino"));
        model.addAttribute("parentescos", List.of("Padre", "Madre", "Abuelo(a)", "Tío(a)", "Hermano(a)", "Otro"));
        model.addAttribute("roles", List.of("APODERADO"));

        return "admin/registro-apoderado";
    }

    @PostMapping("/registro")
    public String registrarApoderado(@Valid @ModelAttribute("apoderadoRegistroDTO") ApoderadoRegistroDTO apoderadoDTO,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {

        System.out.println("--- DENTRO DEL MÉTODO POST /admin/apoderados/registro ---");
        System.out.println("DTO recibido: " + apoderadoDTO.getDni() + ", " + apoderadoDTO.getNombres() + ", " + apoderadoDTO.getUsername()); // Imprime algunos campos para verificar que llegan
        System.out.println("BindingResult tiene errores: " + result.hasErrors());

        if (result.hasErrors()) {
            System.out.println("--- ERRORES DE VALIDACIÓN DETECTADOS ---");
            result.getAllErrors().forEach(error -> System.out.println("Error: " + error.getDefaultMessage() + " en campo: " + (error instanceof org.springframework.validation.FieldError ? ((org.springframework.validation.FieldError) error).getField() : "global")));
            // ... (resto de tu código de redirección para errores)
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apoderadoRegistroDTO", result);
            redirectAttributes.addFlashAttribute("apoderadoRegistroDTO", apoderadoDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, corrige los errores en el formulario.");
            return "redirect:/admin/apoderados/registro";
        }

        try {
            System.out.println("--- INTENTANDO REGISTRAR APODERADO EN EL SERVICIO ---");
            apoderadoServiceImpl.registrarApoderado(apoderadoDTO);
            System.out.println("--- APODERADO REGISTRADO EXITOSAMENTE (supuestamente) ---");
            redirectAttributes.addFlashAttribute("successMessage", "Apoderado registrado exitosamente.");
            return "redirect:/admin/apoderados";
        } catch (IllegalArgumentException e) {
            System.err.println("--- ERROR DE LÓGICA DE NEGOCIO: " + e.getMessage() + " ---");
            // ... (resto de tu código de redirección para errores de negocio)
            if (e.getMessage().contains("DNI")) {
                result.rejectValue("dni", "duplicate.dni", e.getMessage());
            } else if (e.getMessage().contains("Email Personal")) {
                result.rejectValue("emailPersonal", "duplicate.email", e.getMessage());
            } else if (e.getMessage().contains("nombre de usuario")) {
                result.rejectValue("username", "duplicate.username", e.getMessage());
            } else {
                result.rejectValue(null, null, e.getMessage());
            }

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apoderadoRegistroDTO", result);
            redirectAttributes.addFlashAttribute("apoderadoRegistroDTO", apoderadoDTO);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/apoderados/registro";
        } catch (Exception e) {
            System.err.println("--- ERROR INESPERADO AL REGISTRAR: " + e.getMessage() + " ---");
            e.printStackTrace(); // Imprime el stack trace completo para errores inesperados
            // ... (resto de tu código de redirección para errores inesperados)
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al registrar el apoderado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("apoderadoRegistroDTO", apoderadoDTO);
            return "redirect:/admin/apoderados/registro";
        }
    }

    // Método para mostrar el formulario de edición (GET)
    @GetMapping("/editar/{idApoderado}")
    public String mostrarFormularioEdicionApoderado(@PathVariable Integer idApoderado, Model model, RedirectAttributes redirectAttributes) {
        try {
            ApoderadoRegistroDTO apoderadoDTO = apoderadoServiceImpl.obtenerApoderadoParaEditar(idApoderado);
            model.addAttribute("apoderadoRegistroDTO", apoderadoDTO);

            model.addAttribute("generos", List.of("Masculino", "Femenino"));
            model.addAttribute("parentescos", List.of("Padre", "Madre", "Abuelo(a)", "Tío(a)", "Hermano(a)", "Otro"));
            model.addAttribute("roles", List.of("APODERADO")); // Opcional, ya que no se muestra

            return "admin/editar-apoderado";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/apoderados";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar los datos del apoderado para edición: " + e.getMessage());
            return "redirect:/admin/apoderados";
        }
    }

    // Método para procesar el envío del formulario de edición (POST)
    @PostMapping("/editar")
    public String actualizarApoderado(@Valid @ModelAttribute("apoderadoRegistroDTO") ApoderadoRegistroDTO apoderadoDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apoderadoRegistroDTO", result);
            redirectAttributes.addFlashAttribute("apoderadoRegistroDTO", apoderadoDTO); // Para precargar los datos
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, corrige los errores en el formulario.");
            return "redirect:/admin/apoderados/editar/" + apoderadoDTO.getIdApoderado();
        }

        try {
            apoderadoServiceImpl.actualizarApoderado(apoderadoDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Apoderado actualizado exitosamente.");
            return "redirect:/admin/apoderados"; // Redirige a la lista después de actualizar
        } catch (IllegalArgumentException e) {
            // errores de dupliucado de dni, email y username
            if (e.getMessage().contains("DNI")) {
                result.rejectValue("dni", "duplicate.dni", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                result.rejectValue("emailPersonal", "duplicate.email", e.getMessage());
            } else if (e.getMessage().contains("nombre de usuario")) {
                result.rejectValue("username", "duplicate.username", e.getMessage());
            } else {
                result.rejectValue(null, null, e.getMessage()); // error global si no coincide
            }

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.apoderadoRegistroDTO", result);
            redirectAttributes.addFlashAttribute("apoderadoRegistroDTO", apoderadoDTO);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/apoderados/editar/" + apoderadoDTO.getIdApoderado();
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
            return "redirect:/admin/apoderados";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al actualizar el apoderado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("apoderadoRegistroDTO", apoderadoDTO);
            return "redirect:/admin/apoderados/editar/" + apoderadoDTO.getIdApoderado();
        }
    }

}
