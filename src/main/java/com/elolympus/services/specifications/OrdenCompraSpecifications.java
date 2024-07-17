package com.elolympus.services.specifications;

import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.data.Logistica.OrdenCompraDet;
import org.springframework.data.jpa.domain.Specification;

public class OrdenCompraSpecifications {
    public static Specification<OrdenCompraDet> byordenCompra(OrdenCompra ordenCompra) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ordenCompra"), ordenCompra);
    }
}
