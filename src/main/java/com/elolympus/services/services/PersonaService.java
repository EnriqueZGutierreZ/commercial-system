package com.elolympus.services.services;

import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.repository.PersonaRepository;

import com.elolympus.services.specifications.PersonaSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    public List<Persona> findAll() {
        return repository.findAll();
    }
    public Persona update(Persona entity) {
        return repository.save(entity);
    }

    public Optional<Persona> get(Long id) {
        return repository.findById(id);
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
