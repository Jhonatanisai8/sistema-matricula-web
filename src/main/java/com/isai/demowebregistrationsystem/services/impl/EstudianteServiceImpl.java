package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.exceptions.ValidationException;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteDetalleDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteListadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.estudiantes.EstudianteRegistroDTO;
import com.isai.demowebregistrationsystem.model.dtos.opciones.ApoderadoOptionDTO;
import com.isai.demowebregistrationsystem.model.dtos.opciones.GradoOptionDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import com.isai.demowebregistrationsystem.model.entities.Estudiante;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.model.enums.Rol;
import com.isai.demowebregistrationsystem.repositorys.*;
import com.isai.demowebregistrationsystem.services.EstudianteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl
        implements EstudianteService {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final PersonaRepository personaRepository;
    private final AlmacenArchivoImpl almacenArchivoImpl;
    private final ApoderadoRepository apoderadoRepository;
    private final GradoRepository gradoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional(readOnly = true)
    public Page<EstudianteListadoDTO> listarEstudiantesPaginado(String keyword, Boolean activo, Pageable pageable) {
        Specification<Estudiante> spec = Specification.where(null);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerCaseKeyword = keyword.toLowerCase();
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("codigoEstudiante")), "%" + lowerCaseKeyword + "%"),
                    cb.like(cb.lower(root.get("persona").get("dni")), "%" + lowerCaseKeyword + "%"),
                    cb.like(cb.lower(root.get("persona").get("nombres")), "%" + lowerCaseKeyword + "%"),
                    cb.like(cb.lower(root.get("persona").get("apellidos")), "%" + lowerCaseKeyword + "%")
            ));
        }

        if (activo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("persona").get("activo"), activo));
        }

        return estudianteRepository.findAll(spec, pageable)
                .map(this::convertirAEstudianteListadoDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteDetalleDTO obtenerEstudianteDetalle(Integer idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + idEstudiante));
        return convertirAEstudianteDetalleDTO(estudiante);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteRegistroDTO obtenerEstudianteParaEdicion(Integer idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + idEstudiante));
        return convertirAEstudianteRegistroDTO(estudiante);
    }


    @Override
    @Transactional
    public void guardarEstudiante(EstudianteRegistroDTO dto) {
        Optional<Persona> existingPersonaByDni = personaRepository.findByDni(dto.getDni());
        if (existingPersonaByDni.isPresent() && !existingPersonaByDni.get().getIdPersona().equals(dto.getIdPersona())) {
            throw new ValidationException("Ya existe una persona con el DNI: " + dto.getDni());
        }

        if (dto.getCodigoEstudiante() != null && !dto.getCodigoEstudiante().isEmpty()) {
            Optional<Estudiante> existingEstudianteByCodigo = estudianteRepository.findByCodigoEstudiante(dto.getCodigoEstudiante());
            if (existingEstudianteByCodigo.isPresent() && !existingEstudianteByCodigo.get().getIdEstudiante().equals(dto.getIdEstudiante())) {
                throw new ValidationException("Ya existe un estudiante con el código: " + dto.getCodigoEstudiante());
            }
        }


        Persona persona;
        Estudiante estudiante;

        if (dto.getIdPersona() != null) { // Es una edición
            persona = personaRepository.findById(dto.getIdPersona())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada para el ID: " + dto.getIdPersona()));
            estudiante = estudianteRepository.findById(dto.getIdEstudiante())
                    .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado para el ID: " + dto.getIdEstudiante()));
        } else {
            persona = new Persona();
            estudiante = new Estudiante();
            persona.setFechaRegistro(LocalDateTime.now());
            if (dto.getCodigoEstudiante() == null || dto.getCodigoEstudiante().isEmpty()) {
                estudiante.setCodigoEstudiante(generarCodigoEstudiante()); // Implementar este método
            }
        }

        persona.setNombres(dto.getNombres());
        persona.setApellidos(dto.getApellidos());
        persona.setDni(dto.getDni());
        persona.setDireccion(dto.getDireccion());
        persona.setEmailPersonal(dto.getEmailPersonal());
        persona.setEstadoCivil(dto.getEstadoCivil());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setFotoUrl(dto.getFotoUrl());
        persona.setGenero(dto.getGenero());
        persona.setTelefono(dto.getTelefono());
        persona.setTipoDocumento(dto.getTipoDocumento());
        persona.setActivo(dto.getPersonaActivo());
        persona.setFechaActualizacion(LocalDateTime.now());

        String ruta = "";
        if (dto.getFoto() != null
                && !dto.getFoto().isEmpty()) {
            ruta = almacenArchivoImpl.almacenarArchivo(dto.getFoto());
            persona.setFotoUrl(ruta);
        }
        personaRepository.save(persona);

        estudiante.setPersona(persona);
        estudiante.setCodigoEstudiante(dto.getCodigoEstudiante() != null && !dto.getCodigoEstudiante().isEmpty() ? dto.getCodigoEstudiante() : estudiante.getCodigoEstudiante()); // Si viene en DTO, úsalo; si no, mantén el generado.
        estudiante.setEmailEducativo(dto.getEmailEducativo());
        estudiante.setInstitucionProcedencia(dto.getInstitucionProcedencia());
        estudiante.setGradoAnterior(dto.getGradoAnterior());
        estudiante.setSeguroEscolar(dto.getSeguroEscolar());
        estudiante.setAlergias(dto.getAlergias());
        estudiante.setObservacionesMedicas(dto.getObservacionesMedicas());
        estudiante.setContactoEmergencia(dto.getContactoEmergencia());
        estudiante.setTelefonoEmergencia(dto.getTelefonoEmergencia());
        estudiante.setTipoSangre(dto.getTipoSangre());

        if (dto.getIdApoderado() != null) {
            Apoderado apoderado = apoderadoRepository.findById(dto.getIdApoderado())
                    .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado con ID: " + dto.getIdApoderado()));
            estudiante.setApoderadoPrincipal(apoderado);
        } else {
            estudiante.setApoderadoPrincipal(null);
        }

        Usuario usuario;
        if (dto.getIdUsuario() != null) {
            usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            Optional<Usuario> existingUserByUsername = usuarioRepository.findByUserName(dto.getUserName());
            if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getIdUsuario().equals(dto.getIdUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario '" + dto.getUserName() + "' ya está en uso.");
            }
        } else {
            if (usuarioRepository.existsByUserName(dto.getUserName())) {
                throw new IllegalArgumentException("El nombre de usuario '" + dto.getUserName() + "' ya está en uso.");
            }
            usuario = new Usuario();
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setActivo(true);
            usuario.setIntentosFallidos(0);
            usuario.setRol(Rol.ESTUDIANTE);
            usuario.setPersona(persona);
        }

        usuario.setUserName(dto.getNombres().substring(0, 5).concat(dto.getNombres().substring(5, 7)));
        usuario.setPasswordHash(passwordEncoder.encode(dto.getEmailPersonal()));
        estudianteRepository.save(estudiante);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarEstudiante(Integer idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + idEstudiante));
        Persona persona = estudiante.getPersona();
        persona.setActivo(false);
        persona.setFechaActualizacion(LocalDateTime.now());
        personaRepository.save(persona);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApoderadoOptionDTO> obtenerApoderadosDisponibles() {
        return apoderadoRepository.findAll().stream()
                .map(apoderado -> new ApoderadoOptionDTO(
                        apoderado.getIdApoderado(),
                        apoderado.getPersona().getNombres() + " " + apoderado.getPersona().getApellidos() + " (" + apoderado.getPersona().getDni() + ")"
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradoOptionDTO> obtenerGradosDisponibles() {
        return gradoRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(grado -> new GradoOptionDTO(grado.getIdGrado(), grado.getNombreGrado()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getEstadosCiviles() {
        Map<String, String> estados = new LinkedHashMap<>();
        estados.put("SOLTERO(A)", "Soltero(a)");
        estados.put("CASADO(A)", "Casado(a)");
        estados.put("DIVORCIADO(A)", "Divorciado(a)");
        estados.put("VIUDO(A)", "Viudo(a)");
        estados.put("CONVIVIENTE", "Conviviente");
        return estados;
    }

    @Override
    public Map<String, String> getGeneros() {
        Map<String, String> generos = new LinkedHashMap<>();
        generos.put("MASCULINO", "Masculino");
        generos.put("FEMENINO", "Femenino");
        return generos;
    }

    @Override
    public Map<String, String> getTiposDocumento() {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put("DNI", "DNI");
        tipos.put("CARNET_EXTRANJERIA", "Carnet de Extranjería");
        tipos.put("PASAPORTE", "Pasaporte");
        return tipos;
    }

    @Override
    public Map<String, String> getTiposSangre() {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put("A_POSITIVO", "A+");
        tipos.put("A_NEGATIVO", "A-");
        tipos.put("B_POSITIVO", "B+");
        tipos.put("B_NEGATIVO", "B-");
        tipos.put("AB_POSITIVO", "AB+");
        tipos.put("AB_NEGATIVO", "AB-");
        tipos.put("O_POSITIVO", "O+");
        tipos.put("O_NEGATIVO", "O-");
        return tipos;
    }

    @Override
    public Map<String, String> getGradosAnteriores() {
        Map<String, String> grados = new LinkedHashMap<>();
        grados.put("INICIAL_3", "Inicial 3 años");
        grados.put("INICIAL_4", "Inicial 4 años");
        grados.put("INICIAL_5", "Inicial 5 años");
        grados.put("PRIMARIA_1", "1° Primaria");
        grados.put("PRIMARIA_2", "2° Primaria");
        grados.put("PRIMARIA_3", "3° Primaria");
        grados.put("PRIMARIA_4", "4° Primaria");
        grados.put("PRIMARIA_5", "5° Primaria");
        grados.put("PRIMARIA_6", "6° Primaria");
        return grados;
    }

    private EstudianteListadoDTO convertirAEstudianteListadoDTO(Estudiante estudiante) {
        EstudianteListadoDTO dto = new EstudianteListadoDTO();
        dto.setIdEstudiante(estudiante.getIdEstudiante());
        dto.setIdPersona(estudiante.getPersona().getIdPersona());
        dto.setCodigoEstudiante(estudiante.getCodigoEstudiante());
        dto.setNombresCompletos(estudiante.getPersona().getNombres() + " " + estudiante.getPersona().getApellidos());
        dto.setDni(estudiante.getPersona().getDni());
        dto.setEmailPersonal(estudiante.getPersona().getEmailPersonal());
        dto.setTelefono(estudiante.getPersona().getTelefono());
        dto.setFechaNacimiento(estudiante.getPersona().getFechaNacimiento());
        dto.setActivo(estudiante.getPersona().getActivo());

        matriculaRepository.findTopByEstudiante_IdEstudianteOrderByFechaMatriculaDesc(estudiante.getIdEstudiante())
                .ifPresent(matricula -> {
                    dto.setGradoActual(matricula.getGrado().getNombreGrado());
                    dto.setSeccionActual(matricula.getSeccion().getNombreSeccion());
                });
        return dto;
    }

    private EstudianteDetalleDTO convertirAEstudianteDetalleDTO(Estudiante estudiante) {
        EstudianteDetalleDTO dto = new EstudianteDetalleDTO();
        Persona persona = estudiante.getPersona();

        // Info de Persona
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombres(persona.getNombres());
        dto.setApellidos(persona.getApellidos());
        dto.setDni(persona.getDni());
        dto.setDireccion(persona.getDireccion());
        dto.setEmailPersonal(persona.getEmailPersonal());
        dto.setEstadoCivil(persona.getEstadoCivil());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setFotoUrl(persona.getFotoUrl());
        dto.setGenero(persona.getGenero());
        dto.setTelefono(persona.getTelefono());
        dto.setTipoDocumento(persona.getTipoDocumento());
        dto.setPersonaActivo(persona.getActivo());
        dto.setFechaRegistroPersona(persona.getFechaRegistro());
        dto.setFechaActualizacionPersona(persona.getFechaActualizacion());

        // Info de Estudiante
        dto.setIdEstudiante(estudiante.getIdEstudiante());
        dto.setCodigoEstudiante(estudiante.getCodigoEstudiante());
        dto.setEmailEducativo(estudiante.getEmailEducativo());
        dto.setInstitucionProcedencia(estudiante.getInstitucionProcedencia());
        dto.setGradoAnterior(estudiante.getGradoAnterior());
        dto.setSeguroEscolar(estudiante.getSeguroEscolar());
        dto.setAlergias(estudiante.getAlergias());
        dto.setObservacionesMedicas(estudiante.getObservacionesMedicas());
        dto.setContactoEmergencia(estudiante.getContactoEmergencia());
        dto.setTelefonoEmergencia(estudiante.getTelefonoEmergencia());
        dto.setTipoSangre(estudiante.getTipoSangre());

        // Info del Apoderado principal
        if (estudiante.getApoderadoPrincipal() != null) {
            Apoderado apoderado = estudiante.getApoderadoPrincipal();
            Persona apoderadoPersona = apoderado.getPersona();
            dto.setIdApoderadoPrincipal(apoderado.getIdApoderado());
            dto.setApoderadoNombresCompletos(apoderadoPersona.getNombres() + " " + apoderadoPersona.getApellidos());
            dto.setApoderadoParentesco(apoderado.getParentesco());
            dto.setApoderadoTelefonoContacto(apoderadoPersona.getTelefono());
            dto.setApoderadoEmailPersonal(apoderadoPersona.getEmailPersonal());
        }

        // Info de la Matrícula actual (obtener la más reciente, idealmente la del periodo actual si existe)
        matriculaRepository.findTopByEstudiante_IdEstudianteOrderByFechaMatriculaDesc(estudiante.getIdEstudiante())
                .ifPresent(matricula -> {
                    dto.setIdMatriculaActual(matricula.getIdMatricula());
                    dto.setNumeroMatriculaActual(matricula.getNumeroMatricula());
                    dto.setEstadoMatriculaActual(matricula.getEstadoMatricula());
                    dto.setFechaMatriculaActual(matricula.getFechaMatricula());
                    dto.setGradoMatriculado(matricula.getGrado().getNombreGrado());
                    dto.setSeccionMatriculada(matricula.getSeccion().getNombreSeccion());
                    dto.setPeriodoAcademicoMatriculado(matricula.getPeriodoAcademico().getNombrePeriodo() + " (" + matricula.getPeriodoAcademico().getAnoAcademico() + ")");
                });

        return dto;
    }

    private EstudianteRegistroDTO convertirAEstudianteRegistroDTO(Estudiante estudiante) {
        EstudianteRegistroDTO dto = new EstudianteRegistroDTO();
        Persona persona = estudiante.getPersona();

        dto.setIdEstudiante(estudiante.getIdEstudiante());
        dto.setIdPersona(persona.getIdPersona());

        // Campos de Persona
        dto.setNombres(persona.getNombres());
        dto.setApellidos(persona.getApellidos());
        dto.setDni(persona.getDni());
        dto.setDireccion(persona.getDireccion());
        dto.setEmailPersonal(persona.getEmailPersonal());
        dto.setEstadoCivil(persona.getEstadoCivil());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setFotoUrl(persona.getFotoUrl());
        dto.setGenero(persona.getGenero());
        dto.setTelefono(persona.getTelefono());
        dto.setTipoDocumento(persona.getTipoDocumento());
        dto.setPersonaActivo(persona.getActivo());

        // Campos de Estudiante
        dto.setCodigoEstudiante(estudiante.getCodigoEstudiante());
        dto.setEmailEducativo(estudiante.getEmailEducativo());
        dto.setInstitucionProcedencia(estudiante.getInstitucionProcedencia());
        dto.setGradoAnterior(estudiante.getGradoAnterior());
        dto.setSeguroEscolar(estudiante.getSeguroEscolar());
        dto.setAlergias(estudiante.getAlergias());
        dto.setObservacionesMedicas(estudiante.getObservacionesMedicas());
        dto.setContactoEmergencia(estudiante.getContactoEmergencia());
        dto.setTelefonoEmergencia(estudiante.getTelefonoEmergencia());
        dto.setTipoSangre(estudiante.getTipoSangre());

        // Apoderado asociado
        if (estudiante.getApoderadoPrincipal() != null) {
            dto.setIdApoderado(estudiante.getApoderadoPrincipal().getIdApoderado());
        }

        return dto;
    }

    private String generarCodigoEstudiante() {
        return "EST" + System.currentTimeMillis();
    }
}
