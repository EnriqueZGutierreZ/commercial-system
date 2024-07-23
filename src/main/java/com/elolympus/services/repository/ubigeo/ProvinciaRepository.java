package com.elolympus.services.repository.ubigeo;

import com.elolympus.data.ubigeo.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, String> {
    List<Provincia> findByDepartamentoId(String regionId);
}
