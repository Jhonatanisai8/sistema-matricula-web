package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.dtos.UsuarioDTO;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.repositorys.UsuarioRepository;
import com.isai.demowebregistrationsystem.repositorys.PersonaRepository;
import com.isai.demowebregistrationsystem.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::mapUsuarioToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarUsuarios(String terminoBusqueda) {
        List<Usuario> usuarios;
        if (StringUtils.hasText(terminoBusqueda)) {
            usuarios = usuarioRepository.searchUsuarios(terminoBusqueda);
        } else {
            usuarios = usuarioRepository.findAll();
        }
        return usuarios.stream()
                .map(this::mapUsuarioToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public UsuarioDTO mapUsuarioToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setUserName(usuario.getUserName());
        dto.setRol(usuario.getRol());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setIntentosFallidos(usuario.getIntentosFallidos());

        if (usuario.getPersona() != null) {
            dto.setIdPersona(usuario.getPersona().getIdPersona());
            dto.setDniPersona(usuario.getPersona().getDni());
            dto.setNombresPersona(usuario.getPersona().getNombres());
            dto.setApellidosPersona(usuario.getPersona().getApellidos());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioParaEdicion(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + idUsuario));

        UsuarioDTO dto = mapUsuarioToDTO(usuario);
        dto.setPassword("");
        return dto;
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getIdUsuario() == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido para la actualización.");
        }

        Usuario usuarioExistente = usuarioRepository.findById(usuarioDTO.getIdUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + usuarioDTO.getIdUsuario()));

        usuarioRepository.findByUserName(usuarioDTO.getUserName()).ifPresent(u -> {
            if (!u.getIdUsuario().equals(usuarioExistente.getIdUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario '" + usuarioDTO.getUserName() + "' ya está en uso.");
            }
        });

        usuarioExistente.setUserName(usuarioDTO.getUserName());
        usuarioExistente.setRol(usuarioDTO.getRol());
        usuarioExistente.setActivo(usuarioDTO.getActivo());

        if (StringUtils.hasText(usuarioDTO.getPassword())) {
            if (usuarioDTO.getPassword().length() < 8) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
            }
            usuarioExistente.setPasswordHash(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return mapUsuarioToDTO(usuarioActualizado);
    }
}