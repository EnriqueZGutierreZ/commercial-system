package com.elolympus.services.repository.ubigeo;

import com.elolympus.data.ubigeo.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, String> {
}
