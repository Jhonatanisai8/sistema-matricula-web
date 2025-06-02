package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.model.dtos.UsuarioDTO;
import com.isai.demowebregistrationsystem.model.entities.Usuario;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> listarUsuarios();

    List<UsuarioDTO> buscarUsuarios(String terminoBusqueda);

    UsuarioDTO mapUsuarioToDTO(Usuario usuario);

    UsuarioDTO obtenerUsuarioParaEdicion(Integer idUsuario);

    UsuarioDTO actualizarUsuario(UsuarioDTO usuarioDTO);
}