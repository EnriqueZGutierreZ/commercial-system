package com.elolympus.services.services;

import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.data.Logistica.OrdenCompraDet;
import com.elolympus.services.repository.OrdenCompraDetRepository;
import com.elolympus.services.repository.OrdenCompraRepository;
import com.elolympus.services.specifications.OrdenCompraSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenCompraDetService {

    private final OrdenCompraDetRepository repository;

    public OrdenCompraDetService(OrdenCompraDetRepository ordenCompraDetRepository) {
        this.repository = ordenCompraDetRepository;
    }

    @Transactional
    public OrdenCompraDet save(OrdenCompraDet ordenCompraDet) {
        return repository.save(ordenCompraDet);
    }

    @Transactional
    public OrdenCompraDet update(OrdenCompraDet ordenCompraDet) {
        return repository.save(ordenCompraDet);
    }

    @Transactional
    public void delete(OrdenCompraDet ordenCompraDet) {
        repository.delete(ordenCompraDet);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDet> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDet> findByordenCompra(OrdenCompra ordenCompra) {
        Specification<OrdenCompraDet> spec = OrdenCompraSpecifications.byordenCompra(ordenCompra);
        return repository.findAll(spec);
    }

}
