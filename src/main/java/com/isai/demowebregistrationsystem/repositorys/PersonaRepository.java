package com.isai.demowebregistrationsystem.repositorys;

import com.isai.demowebregistrationsystem.model.entities.Persona;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends CrudRepository<Persona, Integer> {
    Optional<Persona> findByDni(String dni);

    Optional<Persona> findByEmailPersonal(String emailPersonal);
}
