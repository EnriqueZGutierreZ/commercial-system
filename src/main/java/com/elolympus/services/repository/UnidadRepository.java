package com.elolympus.services.repository;

import com.elolympus.data.Logistica.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long>, JpaSpecificationExecutor<Unidad> {
    List<Unidad> findByActivoTrue();
}