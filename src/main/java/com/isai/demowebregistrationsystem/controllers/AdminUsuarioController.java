package com.isai.demowebregistrationsystem.controllers;

import com.isai.demowebregistrationsystem.model.dtos.UsuarioDTO;
import com.isai.demowebregistrationsystem.services.impl.UsuarioServiceImpl;
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
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioServiceImpl usuarioServiceImpl;

    @GetMapping
    public String listarUsuarios(@RequestParam(value = "terminoBusqueda", required = false) String terminoBusqueda, Model model) {
        List<UsuarioDTO> usuarioDTOS;
        if (terminoBusqueda != null && !terminoBusqueda.trim().isEmpty()) {
            usuarioDTOS = usuarioServiceImpl.buscarUsuarios(terminoBusqueda);
            model.addAttribute("terminoBusqueda", terminoBusqueda);
        } else {
            usuarioDTOS = usuarioServiceImpl.listarUsuarios();
        }
        model.addAttribute("usuarios", usuarioDTOS);
        return "admin/lista-usuarios";
    }


    @GetMapping("/editar/{idUsuario}")
    public String mostrarFormularioEdicionUsuario(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        // ... (código sin cambios) ...
        try {
            UsuarioDTO usuarioDTO = usuarioServiceImpl.obtenerUsuarioParaEdicion(idUsuario);
            model.addAttribute("usuarioDTO", usuarioDTO);
            model.addAttribute("roles", List.of("ADMIN", "APODERADO", "PROFESOR"));
            return "admin/editar-usuario";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar los datos del usuario para edición: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/editar")
    public String actualizarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioDTO usuarioDTO,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        // ... (código sin cambios) ...
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuarioDTO", result);
            redirectAttributes.addFlashAttribute("usuarioDTO", usuarioDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, corrige los errores en el formulario.");
            return "redirect:/admin/usuarios/editar/" + usuarioDTO.getIdUsuario();
        }

        try {
            usuarioServiceImpl.actualizarUsuario(usuarioDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado exitosamente.");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("nombre de usuario")) {
                result.rejectValue("userName", "duplicate.username", e.getMessage());
            } else if (e.getMessage().contains("contraseña")) {
                result.rejectValue("password", "invalid.password", e.getMessage());
            } else {
                result.rejectValue(null, null, e.getMessage());
            }

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuarioDTO", result);
            redirectAttributes.addFlashAttribute("usuarioDTO", usuarioDTO);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/usuarios/editar/" + usuarioDTO.getIdUsuario();
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al actualizar el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("usuarioDTO", usuarioDTO);
            return "redirect:/admin/usuarios/editar/" + usuarioDTO.getIdUsuario();
        }
    }
}