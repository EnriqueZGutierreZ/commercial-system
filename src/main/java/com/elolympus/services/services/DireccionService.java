package com.elolympus.services.services;

import com.elolympus.data.Administracion.Direccion;
import com.elolympus.services.repository.DireccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionService extends AbstractCrudService<Direccion, DireccionRepository> {

    protected final DireccionRepository repository;

    public DireccionService(DireccionRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DireccionRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "direccion";
    }

    @Override
    protected String getEntityName() {
        return "Dirección";
    }

    @Override
    protected void copyEditableFields(Direccion source, Direccion target) {
        target.setDescripcion(source.getDescripcion());
        target.setReferencia(source.getReferencia());
        target.setUbigeo(source.getUbigeo());
    }

    // Método de búsqueda personalizado
    public List<Direccion> findAllByActivo(Boolean activo) {
        return repository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("activo"), activo)
        );
    }
}
