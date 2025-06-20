package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.model.dtos.docente.AsignacionDocenteDTO;
import com.isai.demowebregistrationsystem.model.dtos.docente.DocenteDetalleDTO;
import com.isai.demowebregistrationsystem.model.dtos.docente.DocenteRegistroDTO;
import com.isai.demowebregistrationsystem.model.entities.*;
import com.isai.demowebregistrationsystem.model.enums.Rol;
import com.isai.demowebregistrationsystem.repositorys.*;
import com.isai.demowebregistrationsystem.services.DocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}