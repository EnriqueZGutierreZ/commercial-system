package com.elolympus.services.services;

import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.services.repository.OrdenCompraRepository;
import com.elolympus.services.repository.OrdenRegDetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenCompraService {

    private final OrdenCompraRepository repository;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository) {
        this.repository = ordenCompraRepository;
    }

    @Transactional
    public OrdenCompra save(OrdenCompra ordenCompra) {
        return repository.save(ordenCompra);
    }

    @Transactional
    public OrdenCompra update(OrdenCompra ordenCompra) {
        return repository.save(ordenCompra);
    }

    @Transactional
    public void delete(OrdenCompra ordenCompra) {
        repository.delete(ordenCompra);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompra> findAll() {
        return repository.findAll();
    }

}
