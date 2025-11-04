package com.elolympus.services.services;

import com.elolympus.data.Almacen.Almacen;
import com.elolympus.services.repository.AlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlmacenService {
    private final AlmacenRepository repository;

    @Autowired
    public AlmacenService(AlmacenRepository almacenRepository) {
        this.repository = almacenRepository;
    }

    public List<Almacen> findAll() {
        return repository.findAll();
    }

    public Almacen save(Almacen almacen) {
        return repository.save(almacen);
    }

    public Almacen update(Almacen almacen) {
        return repository.save(almacen);
    }

    public void delete(Almacen almacen) {
        repository.delete(almacen);
    }
}