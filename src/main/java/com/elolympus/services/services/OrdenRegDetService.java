package com.elolympus.services.services;

import com.elolympus.data.Almacen.OrdenRegularizacion;
import com.elolympus.data.Almacen.OrdenRegularizacionDet;
import com.elolympus.services.repository.OrdenRegDetRepository;
import com.elolympus.services.specifications.OrdenRegSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenRegDetService {

    private final OrdenRegDetRepository repository;

    public OrdenRegDetService(OrdenRegDetRepository ordenRegDetRepository) {
        this.repository = ordenRegDetRepository;
    }

    public List<OrdenRegularizacionDet> findAll() {
        return repository.findAll();
    }

    public OrdenRegularizacionDet save(OrdenRegularizacionDet ordenRegularizacionDet) {
        return repository.save(ordenRegularizacionDet);
    }

    public OrdenRegularizacionDet update(OrdenRegularizacionDet ordenRegularizacionDet) {
        return repository.save(ordenRegularizacionDet);
    }

    public void delete(OrdenRegularizacionDet ordenRegularizacionDet) {
        repository.delete(ordenRegularizacionDet);
    }

//    public List<OrdenRegularizacionDet> findByOrdenRegularizacion(OrdenRegularizacion ordenRegularizacion) {
//        return repository.findByOrdenRegularizacion(ordenRegularizacion);
//    }

    public List<OrdenRegularizacionDet> findByOrdenRegularizacion(OrdenRegularizacion ordenRegularizacion) {
        Specification<OrdenRegularizacionDet> spec = OrdenRegSpecifications.byOrdenRegularizacion(ordenRegularizacion);
        return repository.findAll(spec);
    }

}
