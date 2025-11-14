package com.elolympus.services.services;

import com.elolympus.data.Logistica.Linea;
import com.elolympus.services.repository.LineaRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de Líneas.
 * Extiende AbstractCrudService eliminando código duplicado.
 */
@Service
public class LineaService extends AbstractCrudService<Linea, LineaRepository> {

    private final LineaRepository repository;

    public LineaService(LineaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected LineaRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "linea";
    }

    @Override
    protected String getEntityName() {
        return "Línea";
    }

    @Override
    protected void copyEditableFields(Linea source, Linea target) {
        target.setNombre(source.getNombre());
        target.setDescripcion(source.getDescripcion());
        target.setCodigo(source.getCodigo());
        target.setActivo(source.isActivo());
    }
}