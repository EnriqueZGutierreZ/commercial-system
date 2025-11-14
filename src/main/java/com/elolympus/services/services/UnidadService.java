package com.elolympus.services.services;

import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.repository.UnidadRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de Unidades.
 * Extiende AbstractCrudService eliminando código duplicado.
 */
@Service
public class UnidadService extends AbstractCrudService<Unidad, UnidadRepository> {

    private final UnidadRepository repository;

    public UnidadService(UnidadRepository repository) {
        this.repository = repository;
    }

    @Override
    protected UnidadRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "unidad";
    }

    @Override
    protected String getEntityName() {
        return "Unidad";
    }

    @Override
    protected void copyEditableFields(Unidad source, Unidad target) {
        target.setNombre(source.getNombre());
        target.setAbreviatura(source.getAbreviatura());
        target.setDescripcion(source.getDescripcion());
        target.setActivo(source.isActivo());
    }
}