package com.isai.demowebregistrationsystem.controllers.login;

import com.isai.demowebregistrationsystem.model.dtos.RegistroApoderadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.RegistroDocenteDTO;
import com.isai.demowebregistrationsystem.model.dtos.RegistroEstudianteDTO;
import com.isai.demowebregistrationsystem.model.dtos.RegistroUsuarioDTO;
import com.isai.demowebregistrationsystem.model.enums.Rol;
import com.isai.demowebregistrationsystem.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registro")
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Usuario o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Has cerrado sesión exitosamente.");
        }
        return "auth/login";
    }

    @GetMapping("")
    public String showRegistroSelectionForm(Model model) {
        model.addAttribute("roles", Arrays.asList(Rol.DOCENTE, Rol.ESTUDIANTE, Rol.APODERADO, Rol.ADMIN));
        return "auth/registro_seleccion_rol";
    }

    @GetMapping("/{rol}")
    public String showSpecificRegistrationForm(@PathVariable String rol, Model model, RedirectAttributes redirectAttributes) {
        try {
            Rol selectedRol = Rol.valueOf(rol.toUpperCase());
            switch (selectedRol) {
                case DOCENTE -> model.addAttribute("registroDTO", new RegistroDocenteDTO());
                case ESTUDIANTE -> model.addAttribute("registroDTO", new RegistroEstudianteDTO());
                case APODERADO -> model.addAttribute("registroDTO", new RegistroApoderadoDTO());
                case ADMIN -> model.addAttribute("registroDTO", new RegistroUsuarioDTO());
                default -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Rol de registro no válido.");
                    return "redirect:/registro";
                }
            }

            model.addAttribute("selectedRol", selectedRol); // ← clave para acceder a descripcion en la vista
            model.addAttribute("generos", Arrays.asList("MASCULINO", "FEMENINO", "OTRO"));
            model.addAttribute("tiposDocumento", Arrays.asList("DNI", "PASAPORTE", "CARNET_EXTRANJERIA"));
            model.addAttribute("estadosCivil", Arrays.asList("SOLTERO", "CASADO", "DIVORCIADO", "VIUDO"));

            return "auth/registro_form_dinamico";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rol de registro no válido.");
            return "redirect:/registro";
        }
    }

    @PostMapping("/guardar")
    public String registrarUsuario(@ModelAttribute("registroDTO") @Valid RegistroUsuarioDTO registroDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registroDTO", "Las contraseñas no coinciden.");
        }

        if (result.hasErrors()) {
            model.addAttribute("selectedRol", registroDTO.getRol()); // ← pasamos el Enum, no el String
            model.addAttribute("generos", Arrays.asList("MASCULINO", "FEMENINO", "OTRO"));
            model.addAttribute("tiposDocumento", Arrays.asList("DNI", "PASAPORTE", "CARNET_EXTRANJERIA"));
            model.addAttribute("estadosCivil", Arrays.asList("SOLTERO", "CASADO", "DIVORCIADO", "VIUDO"));
            return "auth/registro_form_dinamico";
        }

        try {
            usuarioService.registrarNuevoUsuario(registroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("nombre de usuario")) {
                result.rejectValue("username", "error.registroDTO", e.getMessage());
            } else if (e.getMessage().contains("DNI")) {
                result.rejectValue("dni", "error.registroDTO", e.getMessage());
            } else {
                model.addAttribute("errorMessage", e.getMessage());
            }
            model.addAttribute("selectedRol", registroDTO.getRol());
            model.addAttribute("generos", Arrays.asList("MASCULINO", "FEMENINO", "OTRO"));
            model.addAttribute("tiposDocumento", Arrays.asList("DNI", "PASAPORTE", "CARNET_EXTRANJERIA"));
            model.addAttribute("estadosCivil", Arrays.asList("SOLTERO", "CASADO", "DIVORCIADO", "VIUDO"));
            return "auth/registro_form_dinamico";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard-admin";
    }

    @GetMapping("/docente/dashboard")
    public String docenteDashboard() {
        return "layouts/profesor-layout";
    }

    @GetMapping("/estudiante/dashboard")
    public String estudianteDashboard() {
        return "dashboards/estudiante_dashboard";
    }

    @GetMapping("/apoderado/dashboard")
    public String apoderadoDashboard() {
        return "dashboards/apoderado_dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access_denied";
    }
}
