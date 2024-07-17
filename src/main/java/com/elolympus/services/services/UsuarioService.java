package com.elolympus.services.services;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Administracion.Usuario;
import com.elolympus.services.repository.UsuarioRepository;
import com.elolympus.services.specifications.UsuarioSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public int count() {
        return (int) repository.count();
    }

    // Método para encontrar todos los usuarios
    public List<Usuario> findAll() {
        return repository.findAll();
    }
    public List<Usuario> findAllByActivo(Boolean activo) {
        return repository.findAll(UsuarioSpecifications.conEstadoActivo(true));
    }

    // Método para guardar o actualizar un usuario
    public Usuario update(Usuario usuario) {
        // Aquí puedes añadir lógica antes de guardar el usuario
        return repository.save(usuario);
    }
    public Usuario save(Usuario usuario) {
        // Aquí puedes añadir lógica antes de guardar el usuario
        return repository.save(usuario);
    }

    // Método para eliminar un usuario
    public void delete(Usuario usuario) {
        repository.delete(usuario);
    }

    // Método para encontrar un usuario por ID
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id);
    }

    // Método para buscar usuarios por nombre de usuario, rol y persona
    public List<Usuario> findByUsernameRolAndPersona(String usuario, Rol rol, Persona persona) {
        Specification<Usuario> spec = Specification.where(null);

        if (usuario != null && !usuario.isEmpty()) {
            spec = spec.and(UsuarioSpecifications.hasUsuario(usuario));
        }
        if (rol != null) {
            spec = spec.and(UsuarioSpecifications.hasRol(rol));
        }
        if (persona != null) {
            spec = spec.and(UsuarioSpecifications.hasPersona(persona));
        }

        return repository.findAll(spec);
    }
}
