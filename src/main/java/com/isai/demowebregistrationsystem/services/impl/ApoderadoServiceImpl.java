package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.ApoderadoRegistroDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import com.isai.demowebregistrationsystem.model.entities.Persona;
import com.isai.demowebregistrationsystem.model.entities.Usuario;
import com.isai.demowebregistrationsystem.repositorys.ApoderadoRepository;
import com.isai.demowebregistrationsystem.repositorys.PersonaRepository;
import com.isai.demowebregistrationsystem.repositorys.UsuarioRepository;
import com.isai.demowebregistrationsystem.services.ApoderadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository apoderadoRepository;

    private final UsuarioRepository usuarioRepository;

    private final PersonaRepository personaRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ApoderadoDTO> listarApoderados() {
        List<Apoderado> apoderados = apoderadoRepository.findAll();
        return apoderados.stream()
                .map(this::mapApoderadoToDTO)
                .collect(Collectors.toList());
    }


    //metodo para buscar por algun termino de busqueda
    public List<ApoderadoDTO> buscarApoderados(String termianoBusqueda) {
        List<Apoderado> apoderados;
        // Verifica si el término no es nulo o vacío
        if (StringUtils.hasText(termianoBusqueda)) {
            apoderados = apoderadoRepository.searchApoderados(termianoBusqueda);
        } else {
            //si hay no termino de busqueda devuelve todos
            apoderados = apoderadoRepository.findAll();
        }
        return apoderados.stream()
                .map(this::mapApoderadoToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Apoderado registrarApoderado(ApoderadoRegistroDTO apoderadoDTO) {
        System.out.println("DNI:" + apoderadoDTO.getDni());
        if (personaRepository.findByDni(apoderadoDTO.getDni()).isPresent()) {
            throw new IllegalArgumentException("La persona ya existe en el sistema");
        }

        if (StringUtils.hasText(apoderadoDTO.getEmailPersonal())
                && personaRepository.findByEmailPersonal(apoderadoDTO.getEmailPersonal()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona en el sistema con ese email");
        }

        if (usuarioRepository.findByUserName(apoderadoDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una usuario en el sistema");
        }

        //creamos y guardamos la persona
        Persona persona = new Persona();
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
        persona = personaRepository.save(persona); // Guardar persona para obtener el ID
        // Los campos de fechaRegistro, fechaActualizacion y activo se manejan con @PrePersist en la entidad
        //creamos y guardamos el usuario
        Usuario usuario = new Usuario();
        usuario.setUserName(apoderadoDTO.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(apoderadoDTO.getPassword())); // Hashear la contraseña
        usuario.setRol("APODERADO"); // Establecer rol por defecto
        usuario.setPersona(persona); // Vincular el usuario a la persona recién creada
        // Los campos de fechaCreacion, intentosFallidos y activo se manejan con @PrePersist
        usuarioRepository.save(usuario);

        //creamos y guardamos el apoderado
        Apoderado apoderado = new Apoderado();
        apoderado.setOcupacion(apoderadoDTO.getOcupacion());
        apoderado.setLugarTrabajo(apoderadoDTO.getLugarTrabajo());
        apoderado.setTelefonoTrabajo(apoderadoDTO.getTelefonoTrabajo());
        apoderado.setParentesco(apoderadoDTO.getParentesco());
        apoderado.setNivelEducativo(apoderadoDTO.getNivelEducativo());
        apoderado.setIngresoMensual(apoderadoDTO.getIngresoMensual());
        apoderado.setEsPrincipal(apoderadoDTO.getEsPrincipal());
        apoderado.setAutorizadoRecoger(apoderadoDTO.getAutorizadoRecoger());
        apoderado.setFotoUrl(apoderadoDTO.getFotoUrl());
        apoderado.setReferenciaPersonal(apoderadoDTO.getReferenciaPersonal());
        apoderado.setTelefonoReferencia(apoderadoDTO.getTelefonoReferencia());
        apoderado.setPersona(persona); // Vincular el apoderado a la persona recién creada
        // Los campos esPrincipal y autorizadoRecoger se manejan con @PrePersist si son null

        return apoderadoRepository.save(apoderado);

    }

    @Override
    public ApoderadoDTO mapApoderadoToDTO(Apoderado apoderado) {
        ApoderadoDTO dto = new ApoderadoDTO();
        dto.setIdApoderado(apoderado.getIdApoderado());
        dto.setParentesco(apoderado.getParentesco());
        dto.setEsPrincipal(apoderado.getEsPrincipal());
        dto.setAutorizadoRecoger(apoderado.getAutorizadoRecoger());

        if (apoderado.getPersona() != null) {
            dto.setDniPersona(apoderado.getPersona().getDni());
            dto.setNombresPersona(apoderado.getPersona().getNombres());
            dto.setApellidosPersona(apoderado.getPersona().getApellidos());
            dto.setTelefono(apoderado.getPersona().getTelefono());
            dto.setEmailPersonal(apoderado.getPersona().getEmailPersonal());
        }
        return dto;
    }
}
