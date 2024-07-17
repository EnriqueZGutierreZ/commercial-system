package com.elolympus.services.services;

import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {
    private final EmpresaRepository repository;

    @Autowired
    public EmpresaService(EmpresaRepository empresaRepository) {
        this.repository = empresaRepository;
    }

    public List<Empresa> findAll() {
        return repository.findAll();
    }

    public Empresa save(Empresa empresa) {
        return repository.save(empresa);
    }

    public Empresa update(Empresa empresa) {
        return repository.save(empresa);
    }

    public void delete(Empresa empresa) {
        repository.delete(empresa);
    }


}
