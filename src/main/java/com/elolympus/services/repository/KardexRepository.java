package com.elolympus.services.repository;

import com.elolympus.data.Almacen.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KardexRepository extends
        JpaRepository<Kardex, Long> {

}
