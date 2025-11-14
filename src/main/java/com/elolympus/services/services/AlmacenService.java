package com.elolympus.services.services;

import com.elolympus.data.Almacen.Almacen;
import com.elolympus.services.repository.AlmacenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlmacenService extends AbstractCrudService<Almacen, AlmacenRepository> {

    private final AlmacenRepository almacenRepository;

    @Autowired
    public AlmacenService(AlmacenRepository almacenRepository) {
        this.almacenRepository = almacenRepository;
    }

    @Override
    protected AlmacenRepository getRepository() {
        return almacenRepository;
    }

    @Override
    protected String getTableName() {
        return "almacen";
    }

    @Override
    protected String getEntityName() {
        return "Almacén";
    }

    @Override
    protected void copyEditableFields(Almacen source, Almacen target) {
        target.setCodigo(source.getCodigo());
        target.setDescripcion(source.getDescripcion());
        target.setSucursal(source.getSucursal());
    }
}