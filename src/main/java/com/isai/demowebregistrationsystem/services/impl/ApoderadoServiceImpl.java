package com.isai.demowebregistrationsystem.services.impl;

import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import com.isai.demowebregistrationsystem.repositorys.ApoderadoRepository;
import com.isai.demowebregistrationsystem.services.ApoderadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository apoderadoRepository;

    @Override
    public List<ApoderadoDTO> listarApoderados() {
        List<Apoderado> apoderados = apoderadoRepository.findAll();
        return apoderados.stream()
                .map(this::mapApoderadoToDTO)
                .collect(Collectors.toList());
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
