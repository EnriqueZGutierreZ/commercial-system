package com.elolympus.services.services;

import com.elolympus.data.Logistica.Marca;
import com.elolympus.services.repository.MarcaRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de Marcas.
 * Extiende AbstractCrudService eliminando código duplicado.
 */
@Service
public class MarcaService extends AbstractCrudService<Marca, MarcaRepository> {

    private final MarcaRepository repository;

    public MarcaService(MarcaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MarcaRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "marca";
    }

    @Override
    protected String getEntityName() {
        return "Marca";
    }

    @Override
    protected void copyEditableFields(Marca source, Marca target) {
        target.setNombre(source.getNombre());
        target.setDescripcion(source.getDescripcion());
        target.setCodigo(source.getCodigo());
        target.setActivo(source.isActivo());
    }
}