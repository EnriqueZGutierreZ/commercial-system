package com.elolympus.services.services;

import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Almacen.OrdenRegularizacion;
import com.elolympus.services.repository.OrdenRegRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenRegService {

    private final OrdenRegRepository repository;

    public OrdenRegService(OrdenRegRepository ordenRegRepository) {
        this.repository = ordenRegRepository;
    }

    public List<OrdenRegularizacion> findAll() {
        return repository.findAll();
    }

    public OrdenRegularizacion save(OrdenRegularizacion ordenRegularizacion) {
        return repository.save(ordenRegularizacion);
    }

    public OrdenRegularizacion update(OrdenRegularizacion ordenRegularizacion) {
        return repository.save(ordenRegularizacion);
    }

    public void delete(OrdenRegularizacion ordenRegularizacion) {
        repository.delete(ordenRegularizacion);
    }

}
