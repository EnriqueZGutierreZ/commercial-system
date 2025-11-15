package com.elolympus.services.services;

import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.repository.PersonaRepository;
import com.elolympus.services.specifications.PersonaSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService extends AbstractCrudService<Persona, PersonaRepository> {

    protected final PersonaRepository repository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public PersonaService(PersonaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected PersonaRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "persona";
    }

    @Override
    protected String getEntityName() {
        return "Persona";
    }

    @Override
    protected void copyEditableFields(Persona source, Persona target) {
        target.setTipo_documento(source.getTipo_documento());
        target.setNum_documento(source.getNum_documento());
        target.setNombres(source.getNombres());
        target.setApellidos(source.getApellidos());
        target.setCelular(source.getCelular());
        target.setEmail(source.getEmail());
        target.setSexo(source.getSexo());
        target.setDireccion(source.getDireccion());
    }

    // Sobrescribir get() para usar fetch join y evitar LazyInitializationException
    @Override
    @Transactional(readOnly = true)
    public Optional<Persona> get(Long id) {
        try {
            // Primero intentar con repository
            Optional<Persona> persona = repository.findByIdWithDireccion(id);
            
            if (persona.isPresent()) {
                Persona p = persona.get();
                
                if (p.getDireccion() != null) {
                    try {
                        // Verificar que la dirección esté realmente cargada
                        p.getDireccion().getDescripcion();
                        return persona; // Todo bien, devolver
                    } catch (Exception e) {
                        // Usar EntityManager como fallback
                        return getPersonaWithDireccionUsingEM(id);
                    }
                } else {
                    return persona;
                }
            } else {
                return Optional.empty();
            }
            
        } catch (Exception e) {
            return getPersonaWithDireccionUsingEM(id);
        }
    }
    
    // Método alternativo usando EntityManager directo
    @Transactional(readOnly = true)
    public Optional<Persona> getPersonaWithDireccionUsingEM(Long id) {
        try {
            String jpql = "SELECT p FROM Persona p LEFT JOIN FETCH p.direccion WHERE p.id = :id";
            List<Persona> results = entityManager.createQuery(jpql, Persona.class)
                    .setParameter("id", id)
                    .getResultList();
            
            if (!results.isEmpty()) {
                Persona persona = results.get(0);
                return Optional.of(persona);
            } else {
                return Optional.empty();
            }
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Persona savePersonaWithDireccion(Persona persona, Direccion direccion, DireccionService direccionService) {
        // Guardar la dirección primero si es nueva o actualizar si existe
        Direccion direccionGuardada;
        
        if (direccion.getId() != null) {
            // Si la dirección existe, recargarla desde BD
            Optional<Direccion> direccionExistente = direccionService.get(direccion.getId());
            if (direccionExistente.isPresent()) {
                direccionGuardada = direccionExistente.get();
                // Actualizar los campos
                direccionGuardada.setDescripcion(direccion.getDescripcion());
                direccionGuardada.setReferencia(direccion.getReferencia());
                direccionGuardada.setUbigeo(direccion.getUbigeo());
                // Guardar los cambios
                direccionGuardada = direccionService.save(direccionGuardada);
            } else {
                // Si no se encuentra, crear nueva
                direccionGuardada = new Direccion();
                direccionGuardada.setDescripcion(direccion.getDescripcion());
                direccionGuardada.setReferencia(direccion.getReferencia());
                direccionGuardada.setUbigeo(direccion.getUbigeo());
                direccionGuardada = direccionService.save(direccionGuardada);
            }
        } else {
            // Crear nueva dirección
            direccionGuardada = new Direccion();
            direccionGuardada.setDescripcion(direccion.getDescripcion());
            direccionGuardada.setReferencia(direccion.getReferencia());
            direccionGuardada.setUbigeo(direccion.getUbigeo());
            direccionGuardada = direccionService.save(direccionGuardada);
        }
        
        // Asignar la dirección gestionada a la persona
        persona.setDireccion(direccionGuardada);
        
        // Guardar la persona
        return repository.save(persona);
    }

    // Métodos de búsqueda personalizados
    public List<Persona> buscarPorNombresYApellidosActivos(String nombres, String apellidos) {
        return repository.findAll(PersonaSpecifications.nombresApellidosContainsIgnoreCase(nombres, apellidos));
    }
    public List<Persona> buscarPorDni(String num_documento) {
        Specification<Persona> spec = PersonaSpecifications.num_documentoContainsIgnoreCase(num_documento);
        return repository.findAll(spec);
    }

    public List<Persona> obtenerPersonasActivas() {
        return repository.findAll();
    }

    public List<Persona> numDocumnetoNombresApellidosActivosContainsIgnoreCase(String num_documento, String nombres, String apellidos) {
        // Usar el método con fetch join para evitar LazyInitializationException
        return repository.findWithFiltersAndDireccion(num_documento, nombres, apellidos);
    }
    
    // Método para obtener todas las personas activas con dirección cargada
    @Transactional(readOnly = true)
    public List<Persona> findAllActivosWithDireccion() {
        return repository.findAllActivosWithDireccion();
    }
}
