package com.elolympus.services.repository;

import com.elolympus.data.Administracion.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsuarioRepository extends
        JpaRepository<Usuario, Long>,
        JpaSpecificationExecutor<Usuario> {
}
