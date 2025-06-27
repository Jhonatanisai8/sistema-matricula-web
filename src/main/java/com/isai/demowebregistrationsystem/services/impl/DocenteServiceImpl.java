package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.exceptions.ValidationException;
import com.isai.demowebregistrationsystem.model.dtos.docente.*;
import com.isai.demowebregistrationsystem.model.dtos.secciones.SeccionDetalleDTO;
import com.isai.demowebregistrationsystem.model.entities.*;
import com.isai.demowebregistrationsystem.model.enums.Rol;
import com.isai.demowebregistrationsystem.repositorys.*;
import com.isai.demowebregistrationsystem.services.DocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final GradoRepository gradoRepository;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final AsignacionDocenteRepository asignacionDocenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlmacenArchivoImpl almacenArchivo;
    private final HorarioRepository horarioRepository;
    private final MatriculaRepository matriculaRepository;


    @Override
    @Transactional
    public DocenteRegistroDTO registrarDocente(DocenteRegistroDTO dto) {
        // 1. Validaciones de unicidad antes de guardar
        if (personaRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe una persona con el DNI: " + dto.getDni());
        }
        if (docenteRepository.existsByCodigoDocente(dto.getCodigoDocente())) {
            throw new IllegalArgumentException("Ya existe un docente con el código: " + dto.getCodigoDocente());
        }
        if (usuarioRepository.existsByUserName(dto.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre de usuario: " + dto.getUsername());
        }
        if (personaRepository.existsByEmailPersonal(dto.getEmailPersonal())) {
            throw new IllegalArgumentException("Ya existe una persona con el email personal: " + dto.getEmailPersonal());
        }
        if (docenteRepository.existsByEmailInstitucional(dto.getEmailInstitucional())) {
            throw new IllegalArgumentException("Ya existe un docente con el email institucional: " + dto.getEmailInstitucional());
        }

        // creamos y guardamos Persona
        Persona persona = Persona.builder()
                .dni(dto.getDni())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .fechaNacimiento(dto.getFechaNacimiento())
                .genero(dto.getGenero())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .emailPersonal(dto.getEmailPersonal())
                .estadoCivil(dto.getEstadoCivil())
                .tipoDocumento(dto.getTipoDocumento())
                .fotoUrl(dto.getFotoUrlPersona()) // Usar el campo de Persona
                .activo(true)
                .build();
        String ruta = "";
        if (dto.getFoto() != null
                && !dto.getFoto().isEmpty()) {
            ruta = almacenArchivo.almacenarArchivo(dto.getFoto());
            persona.setFotoUrl(ruta);
        }
        System.out.println(persona.getFotoUrl());
        persona = personaRepository.save(persona);
        // actualizamos el  DTO con el ID generado
        dto.setIdPersona(persona.getIdPersona());

        // creamos y guardamos el Docente
        Docente docente = Docente.builder()
                .codigoDocente(dto.getCodigoDocente())
                .emailInstitucional(dto.getEmailInstitucional())
                .especialidadPrincipal(dto.getEspecialidadPrincipal())
                .especialidadSecundaria(dto.getEspecialidadSecundaria())
                .tituloProfesional(dto.getTituloProfesional())
                .universidadEgreso(dto.getUniversidadEgreso())
                .fechaContratacion(dto.getFechaContratacion())
                .salarioBase(dto.getSalarioBase())
                .tipoContrato(dto.getTipoContrato())
                .estadoLaboral(dto.getEstadoLaboral())
                .anosExperiencia(dto.getAnosExperiencia())
                .cvUrl(dto.getCvUrl())
                .coordinador(dto.getCoordinador())
                .persona(persona)
                .build();
        docente = docenteRepository.save(docente);
        // actualizamos el DTO con el ID generado
        dto.setIdDocente(docente.getIdDocente());

        // creamos y guardamos Usuario para el docente
        if (dto.getUsername() != null && !dto.getUsername().isEmpty() && dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            Usuario usuario = Usuario.builder()
                    .userName(dto.getUsername())
                    .passwordHash(passwordEncoder.encode(dto.getPassword()))
                    .rol(Rol.DOCENTE)
                    .activo(true)
                    .intentosFallidos(0)
                    .persona(persona)
                    .build();
            usuarioRepository.save(usuario);
        }
        return dto;
    }

    @Override
    @Transactional
    public DocenteRegistroDTO actualizarDocente(Integer idDocente, DocenteRegistroDTO dto) {
        Docente docenteExistente = docenteRepository.findById(idDocente)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + idDocente));
        Persona personaExistente = docenteExistente.getPersona();

        // Validaciones de unicidad (excepto para el propio registro)
        if (!personaExistente.getDni().equals(dto.getDni()) && personaRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("El DNI '" + dto.getDni() + "' ya está registrado para otra persona.");
        }
        if (!docenteExistente.getCodigoDocente().equals(dto.getCodigoDocente()) && docenteRepository.existsByCodigoDocente(dto.getCodigoDocente())) {
            throw new IllegalArgumentException("El código de docente '" + dto.getCodigoDocente() + "' ya está en uso.");
        }
        if (!personaExistente.getEmailPersonal().equals(dto.getEmailPersonal()) && personaRepository.existsByEmailPersonal(dto.getEmailPersonal())) {
            throw new IllegalArgumentException("El email personal '" + dto.getEmailPersonal() + "' ya está en uso.");
        }
        if (!docenteExistente.getEmailInstitucional().equals(dto.getEmailInstitucional()) && docenteRepository.existsByEmailInstitucional(dto.getEmailInstitucional())) {
            throw new IllegalArgumentException("El email institucional '" + dto.getEmailInstitucional() + "' ya está en uso.");
        }

        // Actualizar Persona
        personaExistente.setDni(dto.getDni());
        personaExistente.setNombres(dto.getNombres());
        personaExistente.setApellidos(dto.getApellidos());
        personaExistente.setFechaNacimiento(dto.getFechaNacimiento());
        personaExistente.setGenero(dto.getGenero());
        personaExistente.setDireccion(dto.getDireccion());
        personaExistente.setTelefono(dto.getTelefono());
        personaExistente.setEmailPersonal(dto.getEmailPersonal());
        personaExistente.setEstadoCivil(dto.getEstadoCivil());
        personaExistente.setTipoDocumento(dto.getTipoDocumento());
        String ruta = "";
        if (dto.getFoto() != null
                && !dto.getFoto().isEmpty()) {
            ruta = almacenArchivo.almacenarArchivo(dto.getFoto());
            personaExistente.setFotoUrl(ruta);
        }
        System.out.println(personaExistente.getFotoUrl());
        // El campo 'activo' de Persona se gestiona por separado en activar/desactivar

        personaRepository.save(personaExistente);

        // Actualizar Docente
        docenteExistente.setCodigoDocente(dto.getCodigoDocente());
        docenteExistente.setEmailInstitucional(dto.getEmailInstitucional());
        docenteExistente.setEspecialidadPrincipal(dto.getEspecialidadPrincipal());
        docenteExistente.setEspecialidadSecundaria(dto.getEspecialidadSecundaria());
        docenteExistente.setTituloProfesional(dto.getTituloProfesional());
        docenteExistente.setUniversidadEgreso(dto.getUniversidadEgreso());
        docenteExistente.setFechaContratacion(dto.getFechaContratacion());
        docenteExistente.setSalarioBase(dto.getSalarioBase());
        docenteExistente.setTipoContrato(dto.getTipoContrato());
        docenteExistente.setEstadoLaboral(dto.getEstadoLaboral());
        docenteExistente.setAnosExperiencia(dto.getAnosExperiencia());
        docenteExistente.setCvUrl(dto.getCvUrl());
        docenteExistente.setCoordinador(dto.getCoordinador());

        docenteRepository.save(docenteExistente);

        // Actualizamos el usuario (si se proporciona username/password y no está vacío)
        Optional<Usuario> optionalUsuario = usuarioRepository.findByPersonaIdPersona(personaExistente.getIdPersona());
        if (optionalUsuario.isPresent()) {
            Usuario usuarioExistente = optionalUsuario.get();
            // Actualizar username si ha cambiado y no está en uso por otro
            if (dto.getUsername() != null && !dto.getUsername().isEmpty() && !usuarioExistente.getUserName().equals(dto.getUsername())) {
                if (usuarioRepository.existsByUserName(dto.getUsername())) {
                    throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsername() + "' ya está en uso.");
                }
                usuarioExistente.setUserName(dto.getUsername());
            }
            // Actualizar contraseña si se proporciona una nueva (no debería ser obligatorio en update)
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                    throw new IllegalArgumentException("Las contraseñas no coinciden.");
                }
                usuarioExistente.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            }
            usuarioRepository.save(usuarioExistente);
        } else if (dto.getUsername() != null && !dto.getUsername().isEmpty() && dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            // Si no tiene usuario pero se le está creando uno
            Usuario nuevoUsuario = Usuario.builder()
                    .userName(dto.getUsername())
                    .passwordHash(passwordEncoder.encode(dto.getPassword()))
                    .rol(Rol.DOCENTE)
                    .activo(true)
                    .intentosFallidos(0)
                    .persona(personaExistente)
                    .build();
            usuarioRepository.save(nuevoUsuario);
        }

        dto.setIdPersona(personaExistente.getIdPersona());
        dto.setIdDocente(docenteExistente.getIdDocente());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public DocenteDetalleDTO obtenerDetalleDocente(Integer idDocente) {
        Docente docente = docenteRepository.findById(idDocente)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + idDocente));
        Persona persona = docente.getPersona();
        Optional<Usuario> usuarioOptional = usuarioRepository.findByPersonaIdPersona(persona.getIdPersona());

        return DocenteDetalleDTO.builder()
                .idPersona(persona.getIdPersona())
                .dni(persona.getDni())
                .nombresCompletos(persona.getNombres() + " " + persona.getApellidos())
                .fechaNacimiento(persona.getFechaNacimiento())
                .genero(persona.getGenero())
                .direccion(persona.getDireccion())
                .telefono(persona.getTelefono())
                .emailPersonal(persona.getEmailPersonal())
                .estadoCivil(persona.getEstadoCivil())
                .tipoDocumento(persona.getTipoDocumento())
                .fotoUrlPersona(persona.getFotoUrl())
                .idDocente(docente.getIdDocente())
                .codigoDocente(docente.getCodigoDocente())
                .emailInstitucional(docente.getEmailInstitucional())
                .especialidadPrincipal(docente.getEspecialidadPrincipal())
                .especialidadSecundaria(docente.getEspecialidadSecundaria())
                .tituloProfesional(docente.getTituloProfesional())
                .universidadEgreso(docente.getUniversidadEgreso())
                .fechaContratacion(docente.getFechaContratacion())
                .salarioBase(docente.getSalarioBase())
                .tipoContrato(docente.getTipoContrato())
                .estadoLaboral(docente.getEstadoLaboral())
                .anosExperiencia(docente.getAnosExperiencia())
                .cvUrl(docente.getCvUrl())
                .coordinador(docente.getCoordinador())
                .username(usuarioOptional.map(Usuario::getUserName).orElse(null))
                .usuarioActivo(usuarioOptional.map(Usuario::getActivo).orElse(null))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocenteDetalleDTO> listarDocentes(Pageable pageable) {
        Page<Docente> docentesPage = docenteRepository.findAll(pageable);
        List<DocenteDetalleDTO> dtos = docentesPage.getContent().stream()
                .map(docente -> {
                    Persona persona = docente.getPersona();
                    Optional<Usuario> usuarioOptional = usuarioRepository.findByPersonaIdPersona(persona.getIdPersona());
                    return DocenteDetalleDTO.builder()
                            .idDocente(docente.getIdDocente())
                            .idPersona(persona.getIdPersona())
                            .dni(persona.getDni())
                            .nombresCompletos(persona.getNombres() + " " + persona.getApellidos())
                            .codigoDocente(docente.getCodigoDocente())
                            .emailInstitucional(docente.getEmailInstitucional())
                            .especialidadPrincipal(docente.getEspecialidadPrincipal())
                            .estadoLaboral(docente.getEstadoLaboral())
                            .coordinador(docente.getCoordinador())
                            .username(usuarioOptional.map(Usuario::getUserName).orElse(null))
                            .usuarioActivo(persona.getActivo() != null ? persona.getActivo() : false) // Usar activo de Persona para listado
                            .build();
                })
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, docentesPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocenteDetalleDTO> listarTodosDocentes() {
        return docenteRepository.findAll().stream()
                .filter(docente -> docente.getPersona().getActivo())
                .map(docente -> {
                    Persona persona = docente.getPersona();
                    return DocenteDetalleDTO.builder()
                            .idDocente(docente.getIdDocente())
                            .idPersona(persona.getIdPersona())
                            .nombresCompletos(persona.getNombres() + " " + persona.getApellidos())
                            .codigoDocente(docente.getCodigoDocente())
                            .emailInstitucional(docente.getEmailInstitucional())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public void desactivarDocente(Integer idDocente) {
        Docente docente = docenteRepository.findById(idDocente)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + idDocente));
        Persona persona = docente.getPersona();
        persona.setActivo(false);
        personaRepository.save(persona);

        usuarioRepository.findByPersonaIdPersona(persona.getIdPersona()).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void activarDocente(Integer idDocente) {
        Docente docente = docenteRepository.findById(idDocente)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + idDocente));
        Persona persona = docente.getPersona();
        persona.setActivo(true);
        personaRepository.save(persona);

        usuarioRepository.findByPersonaIdPersona(persona.getIdPersona()).ifPresent(usuario -> {
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        });
    }

    @Override
    @Transactional
    public AsignacionDocente asignarCursoADocente(AsignacionDocenteDTO dto) {
        Docente docente = docenteRepository.findById(dto.getIdDocente())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + dto.getIdDocente()));
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + dto.getIdCurso()));
        Grado grado = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + dto.getIdGrado()));
        PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findById(dto.getIdPeriodoAcademico())
                .orElseThrow(() -> new ResourceNotFoundException("Período Académico no encontrado con ID: " + dto.getIdPeriodoAcademico()));

        AsignacionDocente asignacion = AsignacionDocente.builder()
                .docente(docente)
                .curso(curso)
                .grado(grado)
                .periodoAcademico(periodoAcademico)
                .fechaAsignacion(dto.getFechaAsignacion())
                .esTitular(dto.getEsTitular())
                .estadoAsignacion(dto.getEstadoAsignacion() != null ? dto.getEstadoAsignacion() : "ACTIVA")
                .observaciones(dto.getObservaciones())
                .build();
        return asignacionDocenteRepository.save(asignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDocente> obtenerAsignacionesPorDocente(Integer idDocente) {
        return asignacionDocenteRepository.findByDocente_IdDocente(idDocente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AsignacionDocente> obtenerAsignacionPorId(Integer idAsignacion) {
        return asignacionDocenteRepository.findById(idAsignacion);
    }

    @Override
    @Transactional
    public AsignacionDocente actualizarAsignacion(Integer idAsignacion, AsignacionDocenteDTO dto) {
        AsignacionDocente asignacionExistente = asignacionDocenteRepository.findById(idAsignacion)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con ID: " + idAsignacion));
        Docente docente = docenteRepository.findById(dto.getIdDocente())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + dto.getIdDocente()));
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + dto.getIdCurso()));
        Grado grado = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + dto.getIdGrado()));
        PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findById(dto.getIdPeriodoAcademico())
                .orElseThrow(() -> new ResourceNotFoundException("Período Académico no encontrado con ID: " + dto.getIdPeriodoAcademico()));
        asignacionExistente.setDocente(docente);
        asignacionExistente.setCurso(curso);
        asignacionExistente.setGrado(grado);
        asignacionExistente.setPeriodoAcademico(periodoAcademico);
        asignacionExistente.setFechaAsignacion(dto.getFechaAsignacion());
        asignacionExistente.setEsTitular(dto.getEsTitular());
        asignacionExistente.setEstadoAsignacion(dto.getEstadoAsignacion() != null ? dto.getEstadoAsignacion() : "ACTIVA");
        asignacionExistente.setObservaciones(dto.getObservaciones());
        return asignacionDocenteRepository.save(asignacionExistente);
    }

    @Override
    @Transactional
    public void eliminarAsignacion(Integer idAsignacion) {
        if (!asignacionDocenteRepository.existsById(idAsignacion)) {
            throw new ResourceNotFoundException("Asignación no encontrada con ID: " + idAsignacion);
        }
        asignacionDocenteRepository.deleteById(idAsignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listarTodosCursos() {
        return cursoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grado> listarTodosGrados() {
        return gradoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoAcademico> listarTodosPeriodosAcademicos() {
        return periodoAcademicoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocentePerfilDTO> obtenerPerfilDocentePorUsername(String username) {
        return usuarioRepository.findByUserName(username)
                .map(Usuario::getPersona)
                .flatMap(persona -> docenteRepository.findByPersonaIdPersona(persona.getIdPersona()))
                .map(this::convertirADocentePerfilDTO);
    }

    @Override
    public DocentePerfilDTO actualizarPerfilDocente(DocentePerfilDTO docentePerfilDTO) {
        personaRepository.findByDni(docentePerfilDTO.getDni()).ifPresent(existingPersona -> {
            if (!existingPersona.getIdPersona().equals(docentePerfilDTO.getIdPersona())) {
                throw new ValidationException("El DNI '" + docentePerfilDTO.getDni() + "' ya está registrado para otra persona.");
            }
        });

        // Buscar el docente y la persona
        Docente docente = docenteRepository.findById(docentePerfilDTO.getIdDocente())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + docentePerfilDTO.getIdDocente()));

        Persona persona = personaRepository.findById(docentePerfilDTO.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + docentePerfilDTO.getIdPersona()));

        // Actualizar datos de Persona
        persona.setNombres(docentePerfilDTO.getNombres());
        persona.setApellidos(docentePerfilDTO.getApellidos());
        persona.setDni(docentePerfilDTO.getDni());
        persona.setDireccion(docentePerfilDTO.getDireccion());
        persona.setEmailPersonal(docentePerfilDTO.getEmailPersonal());
        persona.setEstadoCivil(docentePerfilDTO.getEstadoCivil());
        persona.setFechaNacimiento(docentePerfilDTO.getFechaNacimiento());
        persona.setGenero(docentePerfilDTO.getGenero());
        persona.setTelefono(docentePerfilDTO.getTelefono());
        persona.setTipoDocumento(docentePerfilDTO.getTipoDocumento());
        persona.setFechaActualizacion(LocalDateTime.now());
        String ruta = "";
        if (docentePerfilDTO.getFoto() != null
                && !docentePerfilDTO.getFoto().isEmpty()) {
            ruta = almacenArchivo.almacenarArchivo(docentePerfilDTO.getFoto());
            persona.setFotoUrl(ruta);
        }
        Persona personaActualizada = personaRepository.save(persona);

        // Actualizar datos de Docente
        docente.setAnosExperiencia(docentePerfilDTO.getAnosExperiencia());
        docente.setCvUrl(docentePerfilDTO.getCvUrl());
        docente.setEmailInstitucional(docentePerfilDTO.getEmailInstitucional());
        docente.setEspecialidadPrincipal(docentePerfilDTO.getEspecialidadPrincipal());
        docente.setEspecialidadSecundaria(docentePerfilDTO.getEspecialidadSecundaria());
        docente.setTituloProfesional(docentePerfilDTO.getTituloProfesional());
        docente.setUniversidadEgreso(docentePerfilDTO.getUniversidadEgreso());

        Docente docenteActualizado = docenteRepository.save(docente);

        return convertirADocentePerfilDTO(docenteActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Docente> findByPersonaId(Integer personaId) {
        return docenteRepository.findByPersonaIdPersona(personaId);
    }

    private static final List<String> ESTADOS_CIVILES = Arrays.asList(
            "Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a"
    );
    private static final List<String> GENEROS = Arrays.asList(
            "Masculino", "Femenino"
    );
    private static final List<String> TIPOS_DOCUMENTO = Arrays.asList(
            "DNI", "Pasaporte", "Carnet de Extranjería"
    );

    private DocentePerfilDTO convertirADocentePerfilDTO(Docente docente) {
        Persona persona = docente.getPersona();
        if (persona == null) {
            throw new ResourceNotFoundException("La persona asociada al docente no fue encontrada.");
        }

        DocentePerfilDTO dto = new DocentePerfilDTO();
        dto.setIdDocente(docente.getIdDocente());
        dto.setIdPersona(persona.getIdPersona());

        // Datos de Persona
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

        // Datos de Docente
        dto.setAnosExperiencia(docente.getAnosExperiencia());
        dto.setCvUrl(docente.getCvUrl());
        dto.setEmailInstitucional(docente.getEmailInstitucional());
        dto.setEspecialidadPrincipal(docente.getEspecialidadPrincipal());
        dto.setEspecialidadSecundaria(docente.getEspecialidadSecundaria());
        dto.setTituloProfesional(docente.getTituloProfesional());
        dto.setUniversidadEgreso(docente.getUniversidadEgreso());

        dto.setCodigoDocente(docente.getCodigoDocente());
        dto.setCoordinador(docente.getCoordinador());
        dto.setEstadoLaboral(docente.getEstadoLaboral());
        dto.setFechaContratacion(docente.getFechaContratacion());
        dto.setSalarioBase(docente.getSalarioBase());
        dto.setTipoContrato(docente.getTipoContrato());

        return dto;
    }

    public List<String> getEstadosCiviles() {
        return ESTADOS_CIVILES;
    }

    public List<String> getGeneros() {
        return GENEROS;
    }

    public List<String> getTiposDocumento() {
        return TIPOS_DOCUMENTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoAsignadoDTO> listarCursosAsignadosPorUserName(String username) {
        Docente docente = usuarioRepository.findByUserName(username)
                .map(Usuario::getPersona)
                .flatMap(persona -> docenteRepository.findByPersonaIdPersona(persona.getIdPersona()))
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado para el usuario: " + username));
        List<AsignacionDocente> asignaciones = asignacionDocenteRepository.findByDocente_IdDocente(docente.getIdDocente());
        return asignaciones.stream()
                .map(this::convertirACursoAsignadoDTO)
                .collect(Collectors.toList());
    }

    private CursoAsignadoDTO convertirACursoAsignadoDTO(AsignacionDocente asignacion) {
        CursoAsignadoDTO dto = new CursoAsignadoDTO();
        dto.setIdAsignacion(asignacion.getIdAsignacion());
        dto.setIdCurso(asignacion.getCurso().getIdCurso());
        dto.setNombreCurso(asignacion.getCurso().getNombreCurso());
        dto.setCodigoCurso(asignacion.getCurso().getCodigoCurso());
        dto.setIdPeriodoAcademico(asignacion.getPeriodoAcademico().getIdPeriodo());
        dto.setNombrePeriodoAcademico(asignacion.getPeriodoAcademico().getNombrePeriodo());
        dto.setAnoAcademico(asignacion.getPeriodoAcademico().getAnoAcademico());
        dto.setNombreGrado(asignacion.getGrado().getNombreGrado());
        dto.setEstadoAsignacion(asignacion.getEstadoAsignacion());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionDocenteDetalleDTO obtenerDetallesAsignacion(Integer idAsignacion, String username) {
        Usuario usuario = usuarioRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        Integer idPersonaDocente = usuario.getPersona().getIdPersona();

        Docente docente = docenteRepository.findByPersonaIdPersona(idPersonaDocente)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado para el usuario: " + username));
        Integer idDocente = docente.getIdDocente();

        AsignacionDocente asignacion = asignacionDocenteRepository
                .findByIdAsignacionAndDocente_IdDocente(idAsignacion, idDocente)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada o no pertenece a este docente."));

        // Obtener los horarios
        List<Horario> horarios = horarioRepository.findByCurso_IdCursoAndDocente_IdDocenteAndGrado_IdGradoAndPeriodoAcademico_IdPeriodo(
                asignacion.getCurso().getIdCurso(),
                asignacion.getDocente().getIdDocente(),
                asignacion.getGrado().getIdGrado(),
                asignacion.getPeriodoAcademico().getIdPeriodo()
        );

        // Convertir la lista de Horario a List<HorarioAsignacionDTO>
        List<HorarioAsignacionDTO> horariosDTO = horarios.stream()
                .map(this::convertirAHorarioAsignacionDTO)
                .collect(Collectors.toList());

        // Obtener las matrículas
        List<Matricula> matriculas = matriculaRepository.findByGrado_IdGradoAndPeriodoAcademico_IdPeriodo(
                asignacion.getGrado().getIdGrado(),
                asignacion.getPeriodoAcademico().getIdPeriodo()
        );

        // Convertir la lista de Matricula a List<EstudianteBasicoDTO>
        List<EstudianteBasicoDTO> estudiantes = matriculas.stream()
                .map(matricula -> EstudianteBasicoDTO.builder()
                        .idEstudiante(matricula.getEstudiante().getIdEstudiante())
                        .codigoEstudiante(matricula.getEstudiante().getCodigoEstudiante())
                        .nombresCompletos(matricula.getEstudiante().getPersona().getNombres() + " " + matricula.getEstudiante().getPersona().getApellidos())
                        .dni(matricula.getEstudiante().getPersona().getDni())
                        .estadoMatricula(matricula.getEstadoMatricula())
                        .build())
                .collect(Collectors.toList());

        return AsignacionDocenteDetalleDTO.builder()
                .idAsignacion(asignacion.getIdAsignacion())
                .fechaAsignacion(asignacion.getFechaAsignacion())
                .esTitular(asignacion.getEsTitular())
                .estadoAsignacion(asignacion.getEstadoAsignacion())
                .observaciones(asignacion.getObservaciones())
                .nombreDocente(asignacion.getDocente().getPersona().getNombres() + " " + asignacion.getDocente().getPersona().getApellidos())
                .curso(convertirACursoDetalleAsignacionDTO(asignacion.getCurso()))
                .grado(convertirAGradoDetalleAsignacionDTO(asignacion.getGrado()))
                .periodoAcademico(convertirAPeriodoAcademicoDetalleAsignacionDTO(asignacion.getPeriodoAcademico()))
                .horarios(horariosDTO) // <-- Asigna la lista de DTOs de horarios
                .estudiantes(estudiantes) // <-- Asigna la lista de DTOs de estudiantes
                .build();
    }

    private HorarioAsignacionDTO convertirAHorarioAsignacionDTO(Horario horario) {
        return HorarioAsignacionDTO.builder()
                .idHorario(horario.getIdHorario())
                .diaSemana(horario.getDiaSemana())
                .horaInicio(horario.getHoraInicio())
                .horaFin(horario.getHoraFin())
                .tipoClase(horario.getTipoClase())
                .observaciones(horario.getObservaciones())
                .salon(convertirASalonDTO(horario.getSalon()))
                .seccion(convertirASeccionDTO(horario.getSeccion()))
                .build();
    }

    private SeccionDetalleDTO convertirASeccionDTO(Seccion seccion) {
        return SeccionDetalleDTO.builder()
                .idSeccion(seccion.getIdSeccion())
                .nombreSeccion(seccion.getNombreSeccion())
                .build();
    }

    private SalonDocenteDTO convertirASalonDTO(Salon salon) {
        return SalonDocenteDTO.builder()
                .idSalon(salon.getIdSalon())
                .nombreSalon(salon.getNombreSalon())
                .codigoSalon(salon.getCodigoSalon())
                .ubicacion(salon.getUbicacion())
                .capacidadMaxima(salon.getCapacidadMaxima())
                .build();
    }

    private PeriodoAcademicoDetalleAsignacionDTO convertirAPeriodoAcademicoDetalleAsignacionDTO(PeriodoAcademico periodo) {
        return PeriodoAcademicoDetalleAsignacionDTO.builder()
                .idPeriodo(periodo.getIdPeriodo())
                .nombrePeriodo(periodo.getNombrePeriodo())
                .anoAcademico(periodo.getAnoAcademico())
                .fechaInicio(periodo.getFechaInicio())
                .fechaFin(periodo.getFechaFin())
                .estado(periodo.getEstado())
                .build();
    }

    private GradoDetalleAsignacionDTO convertirAGradoDetalleAsignacionDTO(Grado grado) {
        return GradoDetalleAsignacionDTO.builder()
                .idGrado(grado.getIdGrado())
                .nombreGrado(grado.getNombreGrado())
                .codigoGrado(grado.getCodigoGrado())
                .descripcion(grado.getDescripcion())
                .nivel(grado.getNivel())
                .build();
    }

    private CursoDetalleAsignacionDTO convertirACursoDetalleAsignacionDTO(Curso curso) {
        return CursoDetalleAsignacionDTO.builder()
                .idCurso(curso.getIdCurso())
                .codigoCurso(curso.getCodigoCurso())
                .nombreCurso(curso.getNombreCurso())
                .descripcion(curso.getDescripcion())
                .areaConocimiento(curso.getAreaConocimiento())
                .creditos(curso.getCreditos())
                .horasSemanales(curso.getHorasSemanales())
                .esObligatorio(curso.getEsObligatorio())
                .prerequisitos(curso.getPrerequisitos())
                .competencias(curso.getCompetencias())
                .build();
    }


    @Transactional(readOnly = true)
    @Override
    public List<CursoDocenteDTO> getCursosAsignadosAlDocente(String username) {
        Usuario usuario = usuarioRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        Docente docente = docenteRepository.findByPersonaIdPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado para el usuario: " + username));

        List<AsignacionDocente> asignaciones = asignacionDocenteRepository
                .findByDocente_IdDocenteAndPeriodoAcademico_Activo(docente.getIdDocente(), true);

        return asignaciones.stream()
                .map(asignacion -> CursoDocenteDTO.builder()
                        .idAsignacion(asignacion.getIdAsignacion())
                        .nombreCurso(asignacion.getCurso().getNombreCurso())
                        .codigoCurso(asignacion.getCurso().getCodigoCurso())
                        .nombreGrado(asignacion.getGrado().getNombreGrado())
                        .nombrePeriodoAcademico(asignacion.getPeriodoAcademico().getNombrePeriodo() + " " + asignacion.getPeriodoAcademico().getAnoAcademico())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EstudianteListaDTO> getEstudiantesPorAsignacion(String username, Integer idAsignacion, int page, int size) {

        Usuario usuario = usuarioRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        Docente docente = docenteRepository.findByPersonaIdPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado para el usuario: " + username));

        AsignacionDocente asignacion = asignacionDocenteRepository
                .findByIdAsignacionAndDocente_IdDocente(idAsignacion, docente.getIdDocente())
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada o no pertenece a este docente."));


        Pageable pageable = PageRequest.of(page, size);


        Page<Matricula> matriculasPage = matriculaRepository.findByGrado_IdGradoAndPeriodoAcademico_IdPeriodo(
                asignacion.getGrado().getIdGrado(),
                asignacion.getPeriodoAcademico().getIdPeriodo(),
                pageable
        );


        return matriculasPage.map(matricula -> EstudianteListaDTO.builder()
                .idEstudiante(matricula.getEstudiante().getIdEstudiante())
                .codigoEstudiante(matricula.getEstudiante().getCodigoEstudiante())
                .nombresCompletos(matricula.getEstudiante().getPersona().getNombres() + " " + matricula.getEstudiante().getPersona().getApellidos())
                .dni(matricula.getEstudiante().getPersona().getDni())
                .nombreSeccion(matricula.getSeccion().getNombreSeccion())
                .estadoMatricula(matricula.getEstadoMatricula())
                .build());
    }

    @Transactional(readOnly = true)
    @Override
    public MisEstudiantesViewDTO getMisEstudiantesViewData(String username) {
        List<CursoDocenteDTO> cursosAsignados = getCursosAsignadosAlDocente(username);
        return MisEstudiantesViewDTO.builder()
                .cursosAsignados(cursosAsignados)
                .build();
    }
}