package com.elolympus.services.repository;

import com.elolympus.data.Logistica.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    
    List<Producto> findByActivoTrue();
    
    // Query optimizada para evitar N+1 con JOIN FETCH
    @Query("SELECT p FROM Producto p " +
           "LEFT JOIN FETCH p.marca " +
           "LEFT JOIN FETCH p.linea " +
           "LEFT JOIN FETCH p.unidad " +
           "WHERE p.activo = true")
    List<Producto> findAllActiveWithRelations();
    
    // Alternative using EntityGraph (more flexible)
    @EntityGraph(attributePaths = {"marca", "linea", "unidad"})
    @Query("SELECT p FROM Producto p WHERE p.activo = true")
    List<Producto> findAllActiveWithEntityGraph();
}