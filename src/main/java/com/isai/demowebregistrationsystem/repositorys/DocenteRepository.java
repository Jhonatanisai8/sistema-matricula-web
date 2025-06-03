package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Integer> {
    Optional<Docente> findByCodigoDocente(String codigoDocente);

    Optional<Docente> findByPersonaDni(String dni);
    // metodo si si el DNI de la persona ya es un docente
}