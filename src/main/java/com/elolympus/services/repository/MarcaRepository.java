package com.elolympus.services.repository;

import com.elolympus.data.Logistica.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long>, JpaSpecificationExecutor<Marca> {
    List<Marca> findByActivoTrue();
}