package com.elolympus.services.services;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.services.repository.DireccionRepository;
import com.elolympus.services.specifications.UsuarioSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    private final DireccionRepository repository;

    public DireccionService(DireccionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Direccion> findAll() {
        return repository.findAll();
    }

    public List<Direccion> findAllByActivo(Boolean activo) {
        return repository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("activo"), activo)
        );
    }

    public Direccion save(Direccion direccion) {
        return repository.save(direccion);
    }

    public Direccion update(Direccion direccion) {
        return repository.save(direccion);
    }

    public void delete(Direccion direccion) {
        repository.delete(direccion);
    }

    public Optional<Direccion> findById(Long id) {
        return repository.findById(id);
    }
}
