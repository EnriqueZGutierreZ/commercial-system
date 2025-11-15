package com.elolympus.services.services;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Rol;
import com.elolympus.data.Administracion.Usuario;
import com.elolympus.services.repository.UsuarioRepository;
import com.elolympus.services.specifications.UsuarioSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService extends AbstractCrudService<Usuario, UsuarioRepository> {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    protected UsuarioRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "usuario";
    }

    @Override
    protected String getEntityName() {
        return "Usuario";
    }

    @Override
    protected void copyEditableFields(Usuario source, Usuario target) {
        target.setUsuario(source.getUsuario());
        target.setPassword(source.getPassword());
        target.setPersona(source.getPersona());
        target.setRol(source.getRol());
    }

    // Métodos adicionales específicos de Usuario
    // El método count() ya está heredado de AbstractCrudService

    /**
     * Override del método findActive para usar query optimizada
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findActive() {
        return repository.findAllActiveWithRelations();
    }
    
    /**
     * Obtener usuario por ID con relaciones cargadas (optimizado para perfil)
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> getWithRelations(Long id) {
        return repository.findById(id)
                .map(usuario -> {
                    // Forzar la inicialización de las relaciones lazy dentro de la transacción
                    if (usuario.getPersona() != null) {
                        usuario.getPersona().getNombres(); // Inicializar proxy
                    }
                    if (usuario.getRol() != null) {
                        usuario.getRol().getCargo(); // Inicializar proxy
                    }
                    return usuario;
                });
    }

    public List<Usuario> findAllByActivo(Boolean activo) {
        return repository.findAll(UsuarioSpecifications.conEstadoActivo(activo));
    }

    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

    public void delete(Usuario usuario) {
        repository.delete(usuario);
    }

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
