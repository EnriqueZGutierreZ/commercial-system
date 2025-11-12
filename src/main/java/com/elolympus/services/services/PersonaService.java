package com.elolympus.services.services;

import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.repository.PersonaRepository;

import com.elolympus.services.specifications.PersonaSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    private final PersonaRepository repository;
    public PersonaService(PersonaRepository repository) {
        this.repository = repository;
    }
    public Page<Persona> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public Page<Persona> list(Pageable pageable, Specification<Persona> filter) {
        return repository.findAll(filter,pageable);
    }
    public int count() {
        return (int) repository.count();
    }
    @Transactional(readOnly = true)
    public List<Persona> findAll() {
        return repository.findAll();
    }
    @Transactional
    public Persona update(Persona entity) {
        return repository.save(entity);
    }

    @Transactional
    public Persona savePersonaWithDireccion(Persona persona, Direccion direccion, DireccionService direccionService) {
        // Guardar la dirección primero si es nueva o actualizar si existe
        Direccion direccionGuardada;
        
        if (direccion.getId() != null) {
            // Si la dirección existe, recargarla desde BD
            Optional<Direccion> direccionExistente = direccionService.findById(direccion.getId());
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

    @Transactional(readOnly = true)
    public Optional<Persona> get(Long id) {
        Optional<Persona> persona = repository.findById(id);
        // Forzar la carga de la dirección si existe
        if (persona.isPresent() && persona.get().getDireccion() != null) {
            // Acceder a la dirección para forzar su carga
            persona.get().getDireccion().getDescripcion();
        }
        return persona;
    }

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

    public List<Persona> numDocumnetoNombresApellidosActivosContainsIgnoreCase(String num_documento, String nombres, String apellidos){
        Specification<Persona> spec = PersonaSpecifications.numDocumnetoNombresApellidosActivosContainsIgnoreCase(num_documento,nombres,apellidos);
        return repository.findAll(spec);
    }

}
