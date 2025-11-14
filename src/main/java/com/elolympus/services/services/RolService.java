package com.elolympus.services.services;

import com.elolympus.data.Administracion.Rol;
import com.elolympus.services.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService extends AbstractCrudService<Rol, RolRepository> {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    protected RolRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "rol";
    }

    @Override
    protected String getEntityName() {
        return "Rol";
    }

    @Override
    protected void copyEditableFields(Rol source, Rol target) {
        target.setArea(source.getArea());
        target.setCargo(source.getCargo());
        target.setDescripcion(source.getDescripcion());
        target.setCanCreate(source.getCanCreate());
        target.setCanRead(source.getCanRead());
        target.setCanUpdate(source.getCanUpdate());
        target.setCanDelete(source.getCanDelete());
    }

    // Método de búsqueda personalizado
    public List<Rol> findRolesByAreaContainingAndCargoContainingAndDescriptionContaining(String area, String cargo, String description) {
        return repository.findRolesByAreaContainingAndCargoContainingAndDescripcionContaining(area, cargo, description);
    }
}
