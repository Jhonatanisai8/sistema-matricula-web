package com.isai.demowebregistrationsystem.services.impl;


import com.isai.demowebregistrationsystem.model.dtos.UsuarioDTO;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.repositorys.PersonaRepository;
import com.isai.demowebregistrationsystem.repositorys.UsuarioRepository;
import com.isai.demowebregistrationsystem.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final PersonaRepository personaRepository;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private Usuario convertToEntity(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, usuario, "password", "confirmPassword", "nombreCompletoPersona", "fechaCreacion", "ultimoAcceso", "intentosFallidos");
        if (usuarioDTO.getPersonaId() != null) {
            Persona persona = personaRepository.findById(usuarioDTO.getPersonaId())
                    .orElseThrow(() -> new IllegalArgumentException("Persona con ID " + usuarioDTO.getPersonaId() + " no encontrada."));
            usuario.setPersona(persona);
        }
        return usuario;
    }
    private UsuarioDTO convertToDto(Usuario usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        BeanUtils.copyProperties(usuario, usuarioDTO, "passwordHash");

        if (usuario.getPersona() != null) {
            usuarioDTO.setPersonaId(usuario.getPersona().getIdPersona());
            usuarioDTO.setNombreCompletoPersona(usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos());
        } else {
            usuarioDTO.setNombreCompletoPersona("Persona No Asignada"); // O un valor por defecto
        }
        return usuarioDTO;
    }


    @Transactional(readOnly = true)
    @Override
    public Page<UsuarioDTO> obtenerUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UsuarioDTO> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public UsuarioDTO guardarUsuario(UsuarioDTO usuarioDTO) {

        if (usuarioDTO.getIdUsuario() == null || usuarioDTO.getIdUsuario() == 0) {
            if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isEmpty()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para un nuevo usuario.");
            }
            if (!usuarioDTO.getPassword().equals(usuarioDTO.getConfirmPassword())) {
                throw new IllegalArgumentException("La contraseña y su confirmación no coinciden.");
            }
        } else {
            if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
                if (!usuarioDTO.getPassword().equals(usuarioDTO.getConfirmPassword())) {
                    throw new IllegalArgumentException("La nueva contraseña y su confirmación no coinciden.");
                }
            }
        }

        Optional<Usuario> existingUserByUsername = usuarioRepository.findByUserName(usuarioDTO.getUserName());
        if (existingUserByUsername.isPresent() &&
                (usuarioDTO.getIdUsuario() == null || !existingUserByUsername.get().getIdUsuario().equals(usuarioDTO.getIdUsuario()))) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre de usuario '" + usuarioDTO.getUserName() + "'.");
        }


        Optional<Usuario> existingUserByPersona = usuarioRepository.findByPersonaIdPersona(usuarioDTO.getPersonaId());
        if (existingUserByPersona.isPresent() &&
                (usuarioDTO.getIdUsuario() == null || !existingUserByPersona.get().getIdUsuario().equals(usuarioDTO.getIdUsuario()))) {

            throw new IllegalArgumentException("La persona seleccionada ya está asociada a otro usuario.");
        }


        Usuario usuario;
        if (usuarioDTO.getIdUsuario() == null || usuarioDTO.getIdUsuario() == 0) {
            usuario = new Usuario();
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setActivo(usuarioDTO.getActivo() != null ? usuarioDTO.getActivo() : true);
            usuario.setIntentosFallidos(0);
        } else {
            usuario = usuarioRepository.findById(usuarioDTO.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + usuarioDTO.getIdUsuario() + " no encontrado."));
        }


        usuario.setUserName(usuarioDTO.getUserName());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setActivo(usuarioDTO.getActivo() != null ? usuarioDTO.getActivo() : false);


        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(usuarioDTO.getPassword()));
        }


        Persona persona = personaRepository.findById(usuarioDTO.getPersonaId())
                .orElseThrow(() -> new IllegalArgumentException("Persona con ID " + usuarioDTO.getPersonaId() + " no encontrada."));
        usuario.setPersona(persona);

        return convertToDto(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario con ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public void alternarEstadoUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(!usuario.getActivo()); // Alterna el estado activo
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Persona> encontrarPersonasDisponibles() {
        List<Persona> allPersonas = personaRepository.findAll();
        Set<Integer> personasConUsuario = usuarioRepository.findAll().stream()
                .filter(u -> u.getPersona() != null)
                .map(u -> u.getPersona().getIdPersona())
                .collect(Collectors.toSet());

        return allPersonas.stream()
                .filter(p -> !personasConUsuario.contains(p.getIdPersona()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Persona> encontrarPersonasDisponiblesParaUsuario(Integer currentUserId) {
        List<Persona> availablePersonas = encontrarPersonasDisponibles();
        if (currentUserId != null) {
            Optional<Usuario> currentUserOpt = usuarioRepository.findById(currentUserId);
            currentUserOpt.ifPresent(u -> {
                if (u.getPersona() != null) {
                    // Si el usuario tiene una persona asociada y esa persona no está ya en la lista de disponibles, añadirla
                    if (!availablePersonas.contains(u.getPersona())) {
                        availablePersonas.add(u.getPersona());
                    }
                }
            });
        }
        availablePersonas.sort((p1, p2) -> {
            String fullName1 = p1.getNombres() + " " + p1.getApellidos();
            String fullName2 = p2.getNombres() + " " + p2.getApellidos();
            return fullName1.compareToIgnoreCase(fullName2);
        });
        return availablePersonas;
    }
}