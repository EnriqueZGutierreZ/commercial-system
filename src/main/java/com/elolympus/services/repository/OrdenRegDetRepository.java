package com.elolympus.services.repository;

import com.elolympus.data.Almacen.OrdenRegularizacion;
import com.elolympus.data.Almacen.OrdenRegularizacionDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRegDetRepository extends
        JpaRepository<OrdenRegularizacionDet, Long>,
        JpaSpecificationExecutor<OrdenRegularizacionDet> {
//    List<OrdenRegularizacionDet> findByOrdenRegularizacion(OrdenRegularizacion ordenRegularizacion);

}
