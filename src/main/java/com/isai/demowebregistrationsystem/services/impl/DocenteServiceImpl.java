package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.dtos.registroInicioSesion.RegistroDocenteDTO;
import com.isai.demowebregistrationsystem.model.entities.Docente;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.repositorys.DocenteRepository;
import com.isai.demowebregistrationsystem.repositorys.PersonaRepository;
import com.isai.demowebregistrationsystem.repositorys.UsuarioRepository;
import com.isai.demowebregistrationsystem.services.DocenteService;
import com.isai.demowebregistrationsystem.utils.CredentialGeneratorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {

    private final PersonaRepository personaRepository;
    private final DocenteRepository docenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Inyectar el PasswordEncoder

    @Override
    @Transactional
    public DocenteDTO registrarDocente(DocenteRegistroDTO docenteRegistroDTO) {
        // 1. Validar unicidad de DNI y Código de Docente
        personaRepository.findByDni(docenteRegistroDTO.getDni()).ifPresent(p -> {
            throw new IllegalArgumentException("El DNI '" + docenteRegistroDTO.getDni() + "' ya está registrado en el sistema.");
        });
        docenteRepository.findByCodigoDocente(docenteRegistroDTO.getCodigoDocente()).ifPresent(d -> {
            throw new IllegalArgumentException("El Código de Docente '" + docenteRegistroDTO.getCodigoDocente() + "' ya está en uso.");
        });

        // 2. Crear y guardar Persona
        Persona nuevaPersona = new Persona();
        nuevaPersona.setDni(docenteRegistroDTO.getDni());
        nuevaPersona.setNombres(docenteRegistroDTO.getNombres());
        nuevaPersona.setApellidos(docenteRegistroDTO.getApellidos());
        nuevaPersona.setFechaNacimiento(docenteRegistroDTO.getFechaNacimiento());
        nuevaPersona.setGenero(docenteRegistroDTO.getGenero());
        nuevaPersona.setDireccion(docenteRegistroDTO.getDireccion());
        nuevaPersona.setTelefono(docenteRegistroDTO.getTelefono());
        nuevaPersona.setEmailPersonal(docenteRegistroDTO.getEmailPersonal());
        nuevaPersona.setEstadoCivil(docenteRegistroDTO.getEstadoCivil());
        nuevaPersona.setTipoDocumento(docenteRegistroDTO.getTipoDocumento());
        nuevaPersona.setActivo(true); // Se activa por defecto al crear
        // Los campos fechaRegistro y fechaActualizacion se setean con @PrePersist en la entidad Persona
        Persona personaGuardada = personaRepository.save(nuevaPersona);

        // 3. Crear y guardar Docente
        Docente nuevoDocente = new Docente();
        nuevoDocente.setCodigoDocente(docenteRegistroDTO.getCodigoDocente());
        nuevoDocente.setEmailInstitucional(docenteRegistroDTO.getEmailInstitucional());
        nuevoDocente.setEspecialidadPrincipal(docenteRegistroDTO.getEspecialidadPrincipal());
        nuevoDocente.setEspecialidadSecundaria(docenteRegistroDTO.getEspecialidadSecundaria());
        nuevoDocente.setTituloProfesional(docenteRegistroDTO.getTituloProfesional());
        nuevoDocente.setUniversidadEgreso(docenteRegistroDTO.getUniversidadEgreso());
        nuevoDocente.setFechaContratacion(docenteRegistroDTO.getFechaContratacion());
        nuevoDocente.setSalarioBase(docenteRegistroDTO.getSalarioBase());
        nuevoDocente.setTipoContrato(docenteRegistroDTO.getTipoContrato());
        nuevoDocente.setEstadoLaboral(docenteRegistroDTO.getEstadoLaboral());
        nuevoDocente.setAnosExperiencia(docenteRegistroDTO.getAnosExperiencia());
        nuevoDocente.setCvUrl(docenteRegistroDTO.getCvUrl());
        nuevoDocente.setCoordinador(docenteRegistroDTO.getCoordinador());
        nuevoDocente.setPersona(personaGuardada);
        // Los campos pre-persist se setean con @PrePersist en la entidad Docente
        Docente docenteGuardado = docenteRepository.save(nuevoDocente);

        // 4. Crear y guardar Usuario para el Docente
        Usuario nuevoUsuario = new Usuario();
        String generatedUsername = CredentialGeneratorUtils.generateUsername(
                docenteRegistroDTO.getNombres(),
                docenteRegistroDTO.getApellidos()
        );
        String generatedPassword = CredentialGeneratorUtils.generateRandomPassword();

        // Verificar si el username generado ya existe
        // Si existe, generar uno nuevo (ej. añadiendo un sufijo extra o reintentando)
        // Para simplificar, asumimos que es único en este ejemplo, pero en un caso real
        // podrías tener un bucle de reintentos o un generador más robusto.
        int attempt = 0;
        String finalUsername = generatedUsername;
        while (usuarioRepository.findByUserName(finalUsername).isPresent() && attempt < 5) { // Limitar intentos
            finalUsername = generatedUsername + (++attempt);
        }
        if (usuarioRepository.findByUserName(finalUsername).isPresent()) {
            throw new IllegalStateException("No se pudo generar un nombre de usuario único después de varios intentos.");
        }


        nuevoUsuario.setUserName(finalUsername);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(generatedPassword)); // Encriptar la contraseña
        nuevoUsuario.setPersona(personaGuardada);
        nuevoUsuario.setActivo(true); // Usuario activo por defecto
        // Los campos fechaCreacion, intentosFallidos se setean con @PrePersist en la entidad Usuario
        usuarioRepository.save(nuevoUsuario);

        // 5. Mapear a DTO de respuesta
        DocenteDTO responseDTO = mapDocenteToDTO(docenteGuardado);
        responseDTO.setUsername(finalUsername);
        responseDTO.setPasswordGenerada(generatedPassword); // Solo para mostrar una vez en el formulario de confirmación
        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocenteDTO> listarDocentes() {
        return docenteRepository.findAll().stream()
                .map(this::mapDocenteToDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear Entidad Docente a DocenteDTO
    private DocenteDTO mapDocenteToDTO(Docente docente) {
        DocenteDTO dto = new DocenteDTO();
        dto.setIdDocente(docente.getIdDocente());
        dto.setCodigoDocente(docente.getCodigoDocente());
        // Se asegura que la persona esté cargada antes de acceder a sus propiedades
        docente.getPersona();
        dto.setNombresCompletos(docente.getPersona().getNombres() + " " + docente.getPersona().getApellidos());
        dto.setDni(docente.getPersona().getDni());
        dto.setEmailInstitucional(docente.getEmailInstitucional());
        dto.setEspecialidadPrincipal(docente.getEspecialidadPrincipal());
        dto.setTituloProfesional(docente.getTituloProfesional());
        dto.setFechaContratacion(docente.getFechaContratacion());
        dto.setSalarioBase(docente.getSalarioBase());
        dto.setCoordinador(docente.getCoordinador());
        // No obtenemos el username ni password generada aquí para el listado por seguridad,
        // solo se hace en el método de registro para retornarla una vez.
        // Si necesitas el username en el listado, tendrías que cargar el Usuario asociado.


        return dto;
    }

    @Override
    public Docente registrarNuevoDocente(RegistroDocenteDTO docenteRegistroDTO) {
        return null;
    }
}