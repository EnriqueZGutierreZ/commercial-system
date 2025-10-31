package com.elolympus.services.services;

import com.elolympus.data.Logistica.Linea;
import com.elolympus.services.repository.LineaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LineaService {

    private final LineaRepository repository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public LineaService(LineaRepository repository) {
        this.repository = repository;
    }

    public Page<Linea> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Linea> findAll() {
        return repository.findAll();
    }

    public List<Linea> findActive() {
        return repository.findByActivoTrue();
    }

    @Transactional
    public Linea update(Linea entity) {
        if (entity.getId() != null) {
            // Para actualizaciones, recargar la entidad desde la BD y copiar los valores
            Linea existingEntity = repository.findById(entity.getId())
                    .orElseThrow(() -> new RuntimeException("Línea no encontrada con ID: " + entity.getId()));
            
            // Si version es null, actualizarlo directamente en BD con query nativa
            if (existingEntity.getVersion() == null) {
                entityManager.createNativeQuery("UPDATE linea SET version = 0 WHERE id = :id")
                        .setParameter("id", entity.getId())
                        .executeUpdate();
                entityManager.clear(); // Limpiar cache para forzar recarga
                // Recargar la entidad con version actualizado desde BD
                existingEntity = repository.findById(entity.getId())
                        .orElseThrow(() -> new RuntimeException("Línea no encontrada con ID: " + entity.getId()));
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

    public Optional<Linea> get(Long id) {
        return repository.findById(id);
    }
}