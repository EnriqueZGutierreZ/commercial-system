package com.elolympus.services.repository;


import com.elolympus.data.Administracion.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository  extends
        JpaRepository<Persona, Long>,
        JpaSpecificationExecutor<Persona> {
}