package com.elolympus.services.repository.ubigeo;

import com.elolympus.data.Auxiliar.Ubigeo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Created by [EnriqueZGutierreZ]
 */
public interface UbigeoRepository extends JpaRepository<Ubigeo, Long> {
    Optional<Ubigeo> findByCodigo(String codigo);
}
