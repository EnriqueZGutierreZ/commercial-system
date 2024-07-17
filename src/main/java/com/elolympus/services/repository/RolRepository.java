package com.elolympus.services.repository;

import com.elolympus.data.Administracion.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RolRepository extends
        JpaRepository<Rol, Long>,
        JpaSpecificationExecutor<Rol> {

    public List<Rol> findRolesByAreaContainingAndCargoContainingAndDescripcionContaining(String area, String cargo, String descripcion);
}
