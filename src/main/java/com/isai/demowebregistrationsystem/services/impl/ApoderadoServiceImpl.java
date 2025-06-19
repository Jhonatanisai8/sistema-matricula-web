package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.dtos.registroInicioSesion.RegistroApoderadoDTO;
import com.isai.demowebregistrationsystem.model.enums.Rol;
import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.repositorys.ApoderadoRepository;
import com.isai.demowebregistrationsystem.repositorys.PersonaRepository;
import com.isai.demowebregistrationsystem.repositorys.UsuarioRepository;
import com.isai.demowebregistrationsystem.services.ApoderadoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository apoderadoRepository;

    private final UsuarioRepository usuarioRepository;

    private final AlmacenArchivoImpl almacenArchivo;

    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public Page<ApoderadoDTO> buscarApoderados(String dni, Pageable pageable) {
        Page<Apoderado> apoderadoPage;
        if (StringUtils.hasText(dni)) {
            apoderadoPage = apoderadoRepository.findByPersonaDniContainingIgnoreCase(dni, pageable);
        } else {
            apoderadoPage = apoderadoRepository.findAll(pageable);
        }
        return apoderadoPage.map(this::convertirA_DTO);
    }

    @Override
    public Optional<ApoderadoDTO> buscarApoderadoPorId(Integer idApoderado) {
        return apoderadoRepository.findById(idApoderado)
                .map(apoderado -> {
                    ApoderadoDTO dto = convertirA_DTO(apoderado);
                    // Cargar datos del usuario si existe
                    usuarioRepository.findByPersonaIdPersona(apoderado.getPersona().getIdPersona())
                            .ifPresent(usuario -> {
                                dto.setIdUsuario(usuario.getIdUsuario());
                                dto.setUserName(usuario.getUserName());
                            });
                    return dto;
                });
    }

    @Override
    public ApoderadoDTO guardarApoderado(ApoderadoDTO apoderadoDTO) {
        Persona persona;
        if (apoderadoDTO.getIdPersona() != null) { // Es una edición de Persona existente
            persona = personaRepository.findById(apoderadoDTO.getIdPersona())
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con ID: " + apoderadoDTO.getIdPersona()));
            Optional<Persona> existingPersonaByDni = personaRepository.findByDni(apoderadoDTO.getDni());
            if (existingPersonaByDni.isPresent() && !existingPersonaByDni.get().getIdPersona().equals(apoderadoDTO.getIdPersona())) {
                throw new IllegalArgumentException("El DNI " + apoderadoDTO.getDni() + " ya está registrado para otra persona.");
            }
        } else {
            if (personaRepository.existsByDni(apoderadoDTO.getDni())) {
                throw new IllegalArgumentException("Ya existe una persona con el DNI: " + apoderadoDTO.getDni());
            }
            persona = new Persona();
        }

        persona.setDni(apoderadoDTO.getDni());
        persona.setNombres(apoderadoDTO.getNombres());
        persona.setApellidos(apoderadoDTO.getApellidos());
        persona.setFechaNacimiento(apoderadoDTO.getFechaNacimiento());
        persona.setGenero(apoderadoDTO.getGenero());
        persona.setDireccion(apoderadoDTO.getDireccion());
        persona.setTelefono(apoderadoDTO.getTelefono());
        persona.setEmailPersonal(apoderadoDTO.getEmailPersonal());
        persona.setEstadoCivil(apoderadoDTO.getEstadoCivil());
        persona.setTipoDocumento(apoderadoDTO.getTipoDocumento());
        String ruta = "";
        if (apoderadoDTO.getFoto() != null
                && !apoderadoDTO.getFoto().isEmpty()) {
            ruta = almacenArchivo.almacenarArchivo(apoderadoDTO.getFoto());
            persona.setFotoUrl(ruta);
        }
        Persona savedPersona = personaRepository.save(persona);
        Apoderado apoderado;
        if (apoderadoDTO.getIdApoderado() != null) {
            apoderado = apoderadoRepository.findById(apoderadoDTO.getIdApoderado())
                    .orElseThrow(() -> new EntityNotFoundException("Apoderado no encontrado con ID: " + apoderadoDTO.getIdApoderado()));
        } else {
            apoderado = new Apoderado();
            apoderado.setPersona(savedPersona);
        }

        apoderado.setOcupacion(apoderadoDTO.getOcupacion());
        apoderado.setLugarTrabajo(apoderadoDTO.getLugarTrabajo());
        apoderado.setTelefonoTrabajo(apoderadoDTO.getTelefonoTrabajo());
        apoderado.setParentesco(apoderadoDTO.getParentesco());
        apoderado.setNivelEducativo(apoderadoDTO.getNivelEducativo());
        apoderado.setIngresoMensual(apoderadoDTO.getIngresoMensual());
        apoderado.setEsPrincipal(apoderadoDTO.getEsPrincipal());
        apoderado.setAutorizadoRecoger(apoderadoDTO.getAutorizadoRecoger());
        apoderado.setReferenciaPersonal(apoderadoDTO.getReferenciaPersonal());
        apoderado.setTelefonoReferencia(apoderadoDTO.getTelefonoReferencia());

        Apoderado savedApoderado = apoderadoRepository.save(apoderado);

        Usuario usuario;
        if (apoderadoDTO.getIdUsuario() != null) { // Es una edición de Usuario existente
            usuario = usuarioRepository.findById(apoderadoDTO.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + apoderadoDTO.getIdUsuario()));

            Optional<Usuario> existingUserByUsername = usuarioRepository.findByUserName(apoderadoDTO.getUserName());
            if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getIdUsuario().equals(apoderadoDTO.getIdUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario '" + apoderadoDTO.getUserName() + "' ya está en uso.");
            }
        } else {
            if (usuarioRepository.existsByUserName(apoderadoDTO.getUserName())) {
                throw new IllegalArgumentException("El nombre de usuario '" + apoderadoDTO.getUserName() + "' ya está en uso.");
            }
            usuario = new Usuario();
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setActivo(true);
            usuario.setIntentosFallidos(0);
            usuario.setRol(Rol.APODERADO);
            usuario.setPersona(savedPersona);
        }

        usuario.setUserName(apoderadoDTO.getUserName());
        if (apoderadoDTO.getPassword() != null && !apoderadoDTO.getPassword().isEmpty()) {
            if (!apoderadoDTO.getPassword().equals(apoderadoDTO.getConfirmPassword())) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            usuario.setPasswordHash(passwordEncoder.encode(apoderadoDTO.getPassword()));
        }

        usuarioRepository.save(usuario);

        return convertirA_DTO(savedApoderado);
    }

    @Override
    public void eliminarApoderado(Integer idApoderado) {
        Apoderado apoderado = apoderadoRepository.findById(idApoderado)
                .orElseThrow(() -> new EntityNotFoundException("Apoderado no encontrado con ID: " + idApoderado));
        Persona persona = apoderado.getPersona();
        usuarioRepository.findByPersonaIdPersona(persona.getIdPersona())
                .ifPresent(usuarioRepository::delete);
        apoderadoRepository.delete(apoderado);
        personaRepository.delete(persona);
    }

    private ApoderadoDTO convertirA_DTO(Apoderado apoderado) {
        ApoderadoDTO dto = new ApoderadoDTO();
        dto.setIdApoderado(apoderado.getIdApoderado());
        if (apoderado.getPersona() != null) {
            Persona persona = apoderado.getPersona();
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombres(persona.getNombres());
            dto.setApellidos(persona.getApellidos());
            dto.setDni(persona.getDni());
            dto.setEmailPersonal(persona.getEmailPersonal());
            dto.setTelefono(persona.getTelefono());
            dto.setFechaNacimiento(persona.getFechaNacimiento());
            dto.setDireccion(persona.getDireccion());
            dto.setGenero(persona.getGenero());
            dto.setEstadoCivil(persona.getEstadoCivil());
            dto.setTipoDocumento(persona.getTipoDocumento());
            dto.setRutaImagen(persona.getFotoUrl());
        }
        dto.setOcupacion(apoderado.getOcupacion());
        dto.setLugarTrabajo(apoderado.getLugarTrabajo());
        dto.setTelefonoTrabajo(apoderado.getTelefonoTrabajo());
        dto.setParentesco(apoderado.getParentesco());
        dto.setNivelEducativo(apoderado.getNivelEducativo());
        dto.setIngresoMensual(apoderado.getIngresoMensual());
        dto.setEsPrincipal(apoderado.getEsPrincipal());
        dto.setAutorizadoRecoger(apoderado.getAutorizadoRecoger());
        dto.setReferenciaPersonal(apoderado.getReferenciaPersonal());
        dto.setTelefonoReferencia(apoderado.getTelefonoReferencia());

        usuarioRepository.findByPersonaIdPersona(apoderado.getPersona().getIdPersona())
                .ifPresent(usuario -> {
                    dto.setIdUsuario(usuario.getIdUsuario());
                    dto.setUserName(usuario.getUserName());
                });
        return dto;
    }

    @Override
    public Apoderado registrarNuevoApoderado(RegistroApoderadoDTO apoderadoDTO) {
        return null;
    }
}

