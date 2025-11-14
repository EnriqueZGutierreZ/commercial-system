package com.elolympus.services.services;

import com.elolympus.data.Empresa.Sucursal;
import com.elolympus.services.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SucursalService extends AbstractCrudService<Sucursal, SucursalRepository> {

    private final SucursalRepository repository;

    @Autowired
    public SucursalService(SucursalRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SucursalRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "sucursal";
    }

    @Override
    protected String getEntityName() {
        return "Sucursal";
    }

    @Override
    protected void copyEditableFields(Sucursal source, Sucursal target) {
        target.setPrincipal(source.isPrincipal());
        target.setCodigo(source.getCodigo());
        target.setDescripcion(source.getDescripcion());
        target.setEmpresa(source.getEmpresa());
        target.setSerie(source.getSerie());
    }
}