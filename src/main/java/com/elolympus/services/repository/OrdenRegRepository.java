package com.elolympus.services.repository;

import com.elolympus.data.Almacen.OrdenRegularizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRegRepository extends
        JpaRepository<OrdenRegularizacion, Long>,
        JpaSpecificationExecutor<OrdenRegularizacion> {
}
