package com.isai.demowebregistrationsystem.services;

import com.isai.demowebregistrationsystem.exceptions.ResourceNotFoundException;
import com.isai.demowebregistrationsystem.model.dtos.ApoderadoDTO;
import com.isai.demowebregistrationsystem.model.dtos.apoderado.DashboardApoderadpDTO;
import com.isai.demowebregistrationsystem.model.dtos.registroInicioSesion.RegistroApoderadoDTO;
import com.isai.demowebregistrationsystem.model.entities.Apoderado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApoderadoService {

    Page<ApoderadoDTO> buscarApoderados(String dni, Pageable pageable);

    Optional<ApoderadoDTO> buscarApoderadoPorId(Integer idApoderado);

    ApoderadoDTO guardarApoderado(ApoderadoDTO apoderado);

    void eliminarApoderado(Integer idApoderado);

    Apoderado registrarNuevoApoderado(RegistroApoderadoDTO apoderadoDTO);

    /*METODOS PARA ROL DE APODERADO*/

    /**
     * Obtiene los datos necesarios para el dashboard del apoderado logueado.
     *
     * @param username El nombre de usuario del apoderado autenticado.
     * @return Un DTO con la información del dashboard.
     * @throws ResourceNotFoundException Si el apoderado no se encuentra.
     */
    DashboardApoderadpDTO obtenerDatosDashboardApoderado(String username) throws ResourceNotFoundException;

}
