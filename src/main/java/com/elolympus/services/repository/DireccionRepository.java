package com.elolympus.services.repository;

import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Administracion.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DireccionRepository extends
        JpaRepository<Direccion, Long>,
        JpaSpecificationExecutor<Direccion> {
}
