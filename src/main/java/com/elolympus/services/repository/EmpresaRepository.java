package com.elolympus.services.repository;

import com.elolympus.data.Empresa.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends
            JpaRepository<Empresa, Long> {
}
