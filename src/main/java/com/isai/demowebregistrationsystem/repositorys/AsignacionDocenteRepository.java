package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.AsignacionDocente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionDocenteRepository
        extends JpaRepository<AsignacionDocente, Integer> {

    List<AsignacionDocente> findByDocente_IdDocente(Integer docenteId);

    List<AsignacionDocente> findByDocente_IdDocenteAndEstadoAsignacion(Long idDocente, String estadoAsignacion);

}
