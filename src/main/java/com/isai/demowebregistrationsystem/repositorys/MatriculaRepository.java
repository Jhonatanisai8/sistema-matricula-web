package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository
        extends JpaRepository<Matricula, Integer> {

    Optional<Matricula> findByEstudiante_IdEstudianteAndPeriodoAcademico_IdPeriodoAndEstadoMatricula(Integer idEstudiante, Integer idPeriodo, String estadoMatricula);

    List<Matricula> findByEstudiante_IdEstudiante(Integer idEstudiante);

    Optional<Matricula> findTopByEstudiante_IdEstudianteOrderByFechaMatriculaDesc(Integer idEstudiante);
}