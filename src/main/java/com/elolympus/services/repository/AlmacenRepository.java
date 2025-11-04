package com.elolympus.services.repository;

import com.elolympus.data.Almacen.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
}