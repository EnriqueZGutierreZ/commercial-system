package com.elolympus.services.repository;

import com.elolympus.data.Logistica.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenCompraRepository extends
        JpaRepository<OrdenCompra, Long>,
        JpaSpecificationExecutor<OrdenCompra> {

}
