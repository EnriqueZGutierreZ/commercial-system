package com.elolympus.services.repository;

import com.elolympus.data.Logistica.OrdenCompraDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenCompraDetRepository extends
        JpaRepository<OrdenCompraDet, Long>,
        JpaSpecificationExecutor<OrdenCompraDet> {

}
