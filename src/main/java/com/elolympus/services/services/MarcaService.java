package com.elolympus.services.services;

import com.elolympus.data.Logistica.Marca;
import com.elolympus.services.repository.MarcaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    private final MarcaRepository repository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public MarcaService(MarcaRepository repository) {
        this.repository = repository;
    }

    public Page<Marca> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Marca> findAll() {
        return repository.findAll();
    }

    public List<Marca> findActive() {
        return repository.findByActivoTrue();
    }

    @Transactional
    public Marca update(Marca entity) {
        if (entity.getId() != null) {
            // Para actualizaciones, recargar la entidad desde la BD y copiar los valores
            Marca existingEntity = repository.findById(entity.getId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + entity.getId()));
            
            // Si version es null, actualizarlo directamente en BD con query nativa
            if (existingEntity.getVersion() == null) {
                entityManager.createNativeQuery("UPDATE marca SET version = 0 WHERE id = :id")
                        .setParameter("id", entity.getId())
                        .executeUpdate();
                entityManager.clear(); // Limpiar cache para forzar recarga
                // Recargar la entidad con version actualizado desde BD
                existingEntity = repository.findById(entity.getId())
                        .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + entity.getId()));
            }
            
            // Copiar solo los campos editables (no los campos de auditoría)
            existingEntity.setNombre(entity.getNombre());
            existingEntity.setDescripcion(entity.getDescripcion());
            existingEntity.setCodigo(entity.getCodigo());
            existingEntity.setActivo(entity.isActivo());
            
            return repository.save(existingEntity);
        } else {
            // Para nuevas entidades, usar save directamente
            return repository.save(entity);
        }
    }

    public Optional<Marca> get(Long id) {
        return repository.findById(id);
    }
}