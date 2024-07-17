package com.elolympus.services.specifications;

import com.elolympus.data.Almacen.OrdenRegularizacion;
import com.elolympus.data.Almacen.OrdenRegularizacionDet;
import org.springframework.data.jpa.domain.Specification;
public class OrdenRegSpecifications {
    public static Specification<OrdenRegularizacionDet> byOrdenRegularizacion(OrdenRegularizacion ordenRegularizacion) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ordenRegularizacion"), ordenRegularizacion);
    }

}
