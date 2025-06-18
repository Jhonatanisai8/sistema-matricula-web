package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.Estudiante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);

    Page<Estudiante> findAll(Specification<Estudiante> spec, Pageable pageable);
}


