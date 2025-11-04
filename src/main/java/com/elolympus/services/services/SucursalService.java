package com.elolympus.services.services;

import com.elolympus.data.Empresa.Sucursal;
import com.elolympus.services.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalService {
    private final SucursalRepository repository;

    @Autowired
    public SucursalService(SucursalRepository sucursalRepository) {
        this.repository = sucursalRepository;
    }

    public List<Sucursal> findAll() {
        return repository.findAll();
    }

    public Sucursal save(Sucursal sucursal) {
        return repository.save(sucursal);
    }

    public Sucursal update(Sucursal sucursal) {
        return repository.save(sucursal);
    }

    public void delete(Sucursal sucursal) {
        repository.delete(sucursal);
    }
}