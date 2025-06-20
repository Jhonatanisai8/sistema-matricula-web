package com.isai.demowebregistrationsystem.controllers.admin;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.model.dtos.docente.AsignacionDocenteDTO;
import com.isai.demowebregistrationsystem.model.dtos.docente.DocenteDetalleDTO;
import com.isai.demowebregistrationsystem.model.dtos.docente.DocenteRegistroDTO;
import com.isai.demowebregistrationsystem.services.DocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/docentes")
public class AdminDocenteController {

    private final DocenteService docenteService;

    @GetMapping
    public String listarDocentes(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "persona.apellidos") String sortBy,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 Model model) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DocenteDetalleDTO> docentesPage = docenteService.listarDocentes(pageable);

        model.addAttribute("docentesPage", docentesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc"); // Para alternar el orden
        return "admin/docentes/lista_docentes";
    }

    @GetMapping("/{id}")
    public String verDetalleDocente(@PathVariable("id") Integer idDocente, Model model, RedirectAttributes redirectAttributes) {
        try {
            DocenteDetalleDTO docente = docenteService.obtenerDetalleDocente(idDocente);
            model.addAttribute("docente", docente);
            model.addAttribute("asignaciones", docenteService.obtenerAsignacionesPorDocente(idDocente));
            return "admin/docentes/detalle_docente";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/docentes";
        }
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("docenteRegistroDTO", new DocenteRegistroDTO());
        return "admin/docentes/crear_docente";
    }

    @PostMapping("/crear")
    public String crearDocente(@Valid @ModelAttribute("docenteRegistroDTO") DocenteRegistroDTO docenteRegistroDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/docentes/crear_docente";
        }
        // validamos que las contraseñas coincidan si se están estableciendo
        if (docenteRegistroDTO.getPassword() != null && !docenteRegistroDTO.getPassword().isEmpty()) {
            if (!docenteRegistroDTO.getPassword().equals(docenteRegistroDTO.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "password.mismatch", "Las contraseñas no coinciden.");
                return "admin/docentes/crear_docente";
            }
        }
        try {
            docenteService.registrarDocente(docenteRegistroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Docente registrado exitosamente!");
            return "redirect:/admin/docentes";
        } catch (IllegalArgumentException e) {
            result.reject(null, e.getMessage());
            return "admin/docentes/crear_docente";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer idDocente, Model model, RedirectAttributes redirectAttributes) {
        try {
            DocenteDetalleDTO docenteDetalle = docenteService.obtenerDetalleDocente(idDocente);
            // Convertir DocenteDetalleDTO a DocenteRegistroDTO para el formulario
            DocenteRegistroDTO docenteRegistroDTO = new DocenteRegistroDTO();
            // Mapeo de campos de Persona
            docenteRegistroDTO.setIdPersona(docenteDetalle.getIdPersona());
            docenteRegistroDTO.setDni(docenteDetalle.getDni());
            docenteRegistroDTO.setNombres(docenteDetalle.getNombresCompletos().split(" ")[0]); // Simplificado
            docenteRegistroDTO.setApellidos(docenteDetalle.getNombresCompletos().substring(docenteDetalle.getNombresCompletos().indexOf(" ") + 1)); // Simplificado
            docenteRegistroDTO.setFechaNacimiento(docenteDetalle.getFechaNacimiento());
            docenteRegistroDTO.setGenero(docenteDetalle.getGenero());
            docenteRegistroDTO.setDireccion(docenteDetalle.getDireccion());
            docenteRegistroDTO.setTelefono(docenteDetalle.getTelefono());
            docenteRegistroDTO.setEmailPersonal(docenteDetalle.getEmailPersonal());
            docenteRegistroDTO.setEstadoCivil(docenteDetalle.getEstadoCivil());
            docenteRegistroDTO.setTipoDocumento(docenteDetalle.getTipoDocumento());
            docenteRegistroDTO.setFotoUrlPersona(docenteDetalle.getFotoUrlPersona());

            // Mapeo de campos de Docente
            docenteRegistroDTO.setIdDocente(docenteDetalle.getIdDocente());
            docenteRegistroDTO.setCodigoDocente(docenteDetalle.getCodigoDocente());
            docenteRegistroDTO.setEmailInstitucional(docenteDetalle.getEmailInstitucional());
            docenteRegistroDTO.setEspecialidadPrincipal(docenteDetalle.getEspecialidadPrincipal());
            docenteRegistroDTO.setEspecialidadSecundaria(docenteDetalle.getEspecialidadSecundaria());
            docenteRegistroDTO.setTituloProfesional(docenteDetalle.getTituloProfesional());
            docenteRegistroDTO.setUniversidadEgreso(docenteDetalle.getUniversidadEgreso());
            docenteRegistroDTO.setFechaContratacion(docenteDetalle.getFechaContratacion());
            docenteRegistroDTO.setSalarioBase(docenteDetalle.getSalarioBase());
            docenteRegistroDTO.setTipoContrato(docenteDetalle.getTipoContrato());
            docenteRegistroDTO.setEstadoLaboral(docenteDetalle.getEstadoLaboral());
            docenteRegistroDTO.setAnosExperiencia(docenteDetalle.getAnosExperiencia());
            docenteRegistroDTO.setCvUrl(docenteDetalle.getCvUrl());
            docenteRegistroDTO.setCoordinador(docenteDetalle.getCoordinador());

            // Mapeo de campos de Usuario (si es relevante para el formulario de edición)
            docenteRegistroDTO.setUsername(docenteDetalle.getUsername());

            model.addAttribute("docenteRegistroDTO", docenteRegistroDTO);
            return "admin/docentes/crear_docente"; // Reutilizamos el formulario de creación
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/docentes";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarDocente(@PathVariable("id") Integer idDocente,
                                    @Valid @ModelAttribute("docenteRegistroDTO") DocenteRegistroDTO docenteRegistroDTO,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/docentes/crear_docente";
        }

        // validamos  que las contraseñas coincidan
        if (docenteRegistroDTO.getPassword() != null && !docenteRegistroDTO.getPassword().isEmpty()) {
            if (!docenteRegistroDTO.getPassword().equals(docenteRegistroDTO.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "password.mismatch", "Las contraseñas no coinciden.");
                return "admin/docentes/crear_docente";
            }
        }


        try {
            docenteService.actualizarDocente(idDocente, docenteRegistroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Docente actualizado exitosamente!");
            return "redirect:/admin/docentes/" + idDocente;
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            result.reject(null, e.getMessage());
            return "admin/docentes/crear_docente";
        }
    }

    // --- Activar/Desactivar Docente ---
    @PostMapping("/toggle-active/{id}")
    public String toggleDocenteActivo(@PathVariable("id") Integer idDocente,
                                      @RequestParam("currentStatus") Boolean currentStatus,
                                      RedirectAttributes redirectAttributes) {
        try {
            if (currentStatus) {
                docenteService.desactivarDocente(idDocente);
                redirectAttributes.addFlashAttribute("successMessage", "Docente desactivado exitosamente.");
            } else {
                docenteService.activarDocente(idDocente);
                redirectAttributes.addFlashAttribute("successMessage", "Docente activado exitosamente.");
            }
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/docentes";
    }

    // --- Asignación de Cursos ---
    @GetMapping("/asignar-cursos/{idDocente}")
    public String mostrarFormularioAsignarCursos(@PathVariable("idDocente") Integer idDocente, Model model, RedirectAttributes redirectAttributes) {
        try {
            DocenteDetalleDTO docente = docenteService.obtenerDetalleDocente(idDocente);
            model.addAttribute("docente", docente);
            System.out.println(docente.getIdDocente());
            model.addAttribute("asignacionDocenteDTO", new AsignacionDocenteDTO(idDocente, docente.getIdDocente(), null, null, null, LocalDate.now(), true, null, null));
            model.addAttribute("cursos", docenteService.listarTodosCursos());
            model.addAttribute("grados", docenteService.listarTodosGrados());
            model.addAttribute("periodosAcademicos", docenteService.listarTodosPeriodosAcademicos());
            model.addAttribute("asignacionesExistentes", docenteService.obtenerAsignacionesPorDocente(idDocente));
            return "admin/docentes/asignar_cursos";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/docentes";
        }
    }

    @PostMapping("/asignar-cursos")
    public String asignarCurso(@Valid @ModelAttribute("asignacionDocenteDTO") AsignacionDocenteDTO asignacionDocenteDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        // --- Primer punto de control: Verificación de errores de validación ---
        // Si hay errores de validación de campos del DTO (incluido idDocente si es @NotNull)
        if (result.hasErrors()) {
            // Aseguramos que el idDocente no sea nulo antes de intentar cargarlo
            // Si asignacionDocenteDTO.getIdDocente() es nulo aquí, es un error del formulario.
            // Podrías añadir un mensaje global si el idDocente es la clave del problema.
            if (asignacionDocenteDTO.getIdDocente() == null) {
                // Añade un error global si el ID del docente es el problema principal
                result.reject("global.idDocente.null", "No se pudo identificar al docente para la asignación.");
                // Si el idDocente es nulo, no podemos cargar el docente ni sus asignaciones existentes.
                // Es mejor redirigir a una página de error o al listado de docentes.
                redirectAttributes.addFlashAttribute("errorMessage", "Error: ID del docente no proporcionado. Vuelva a intentar la asignación.");
                return "redirect:/admin/docentes"; // O a una página de error
            }

            // Recuperar y agregar el objeto Docente al modelo.
            // Solo intentamos cargar el docente si su ID no es nulo.
            // Si hay otros errores de validación (curso, grado, etc.),
            // el docente debería existir y podemos cargarlo.
            try {
                DocenteDetalleDTO docente = docenteService.obtenerDetalleDocente(asignacionDocenteDTO.getIdDocente());
                model.addAttribute("docente", docente);
                // Recargar datos para los selects y la tabla de asignaciones existentes
                model.addAttribute("cursos", docenteService.listarTodosCursos());
                model.addAttribute("grados", docenteService.listarTodosGrados());
                model.addAttribute("periodosAcademicos", docenteService.listarTodosPeriodosAcademicos());
                model.addAttribute("asignacionesExistentes", docenteService.obtenerAsignacionesPorDocente(asignacionDocenteDTO.getIdDocente()));
            } catch (ResourceNotFoundException e) {
                // Si el docente no se encuentra a pesar de que el ID no es nulo,
                // es un error más grave. Podrías añadir un mensaje global aquí.
                result.reject("global.docente.notFound", "El docente especificado no fue encontrado.");
                // Redirige al listado de docentes si el docente base no existe
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/admin/docentes";
            }

            return "admin/docentes/asignar_cursos"; // Vuelve a la misma vista con errores
        }

        // --- Segundo punto de control: Lógica de negocio si la validación pasó ---
        try {
            docenteService.asignarCursoADocente(asignacionDocenteDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Curso asignado exitosamente al docente.");
            // Redirige al detalle del docente o a la página de asignaciones para ese docente
            return "redirect:/admin/docentes/asignar-cursos/" + asignacionDocenteDTO.getIdDocente();
        } catch (ResourceNotFoundException e) {
            // Errores específicos si el curso, grado, o período no se encuentran en el servicio
            redirectAttributes.addFlashAttribute("errorMessage", "Error de datos: " + e.getMessage());
            // En este caso, volvemos a la página de asignación con el ID del docente
            return "redirect:/admin/docentes/asignar-cursos/" + asignacionDocenteDTO.getIdDocente();
        } catch (IllegalArgumentException e) {
            // Otros errores de argumento ilegal desde la capa de servicio
            redirectAttributes.addFlashAttribute("errorMessage", "Error de asignación: " + e.getMessage());
            return "redirect:/admin/docentes/asignar-cursos/" + asignacionDocenteDTO.getIdDocente();
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado: " + e.getMessage());
            return "redirect:/admin/docentes/asignar-cursos/" + asignacionDocenteDTO.getIdDocente();
        }
    }

    @PostMapping("/asignar-cursos/eliminar/{idAsignacion}")
    public String eliminarAsignacion(@PathVariable("idAsignacion") Integer idAsignacion,
                                     @RequestParam("idDocente") Integer idDocente,
                                     RedirectAttributes redirectAttributes) {
        try {
            docenteService.eliminarAsignacion(idAsignacion);
            redirectAttributes.addFlashAttribute("successMessage", "Asignación eliminada exitosamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/docentes/asignar-cursos/" + idDocente;
    }
}