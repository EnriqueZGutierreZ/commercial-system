package com.elolympus.services.services;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.services.repository.DireccionRepository;
import com.elolympus.services.specifications.UsuarioSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    private final DireccionRepository repository;

    public DireccionService(DireccionRepository repository) {
        this.repository = repository;
    }

    public int count() {
        return (int) repository.count();
    }

    // Método para encontrar todos los usuarios
    public List<Direccion> findAll() {
        return repository.findAll();
    }
    public List<Direccion> findAllByActivo(Boolean activo) {
        return repository.findAll(UsuarioSpecifications.conEstadoActivo(true));
    }

    // Método para guardar o actualizar un usuario
    public Direccion update(Direccion direccion) {
        // Aquí puedes añadir lógica antes de guardar el usuario
        return repository.save(usuario);
    }
    public Direccion save(Direccion direccion) {
        // Aquí puedes añadir lógica antes de guardar el usuario
        return repository.save(usuario);
    }

    // Método para eliminar un usuario
    public void delete(Direccion direccion) {
        repository.delete(direccion);
    }

    // Método para encontrar un usuario por ID
    public Optional<Direccion> findById(Long id) {
        return repository.findById(id);
    }

}
