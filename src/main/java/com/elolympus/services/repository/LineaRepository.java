package com.elolympus.services.repository;

import com.elolympus.data.Logistica.Linea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaRepository extends JpaRepository<Linea, Long>, JpaSpecificationExecutor<Linea> {
    List<Linea> findByActivoTrue();
}