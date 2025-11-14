package com.elolympus.services.services;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Clase base abstracta para servicios CRUD que elimina la duplicación de código.
 * Proporciona funcionalidad común para operaciones Create, Read, Update, Delete.
 * 
 * @param <T> El tipo de entidad que maneja este servicio (debe extender AbstractEntity)
 * @param <R> El tipo de repositorio que maneja esta entidad (debe extender JpaRepository)
 */
public abstract class AbstractCrudService<T extends AbstractEntity, R extends JpaRepository<T, Long>> {

    @PersistenceContext
    protected EntityManager entityManager;
    
    /**
     * Obtiene el repositorio específico para esta entidad
     * @return El repositorio JPA
     */
    protected abstract R getRepository();
    
    /**
     * Obtiene el nombre de la tabla en la base de datos
     * @return El nombre de la tabla
     */
    protected abstract String getTableName();
    
    /**
     * Obtiene el nombre de la entidad para mensajes de error
     * @return El nombre de la entidad
     */
    protected abstract String getEntityName();
    
    /**
     * Lista todas las entidades con paginación
     * @param pageable Configuración de paginación
     * @return Página de entidades
     */
    public Page<T> list(Pageable pageable) {
        return getRepository().findAll(pageable);
    }
    
    /**
     * Busca todas las entidades
     * @return Lista de todas las entidades
     */
    public List<T> findAll() {
        return getRepository().findAll();
    }
    
    /**
     * Busca solo las entidades activas
     * @return Lista de entidades activas
     */
    public List<T> findActive() {
        return getRepository().findAll().stream()
                .filter(AbstractEntity::isActivo)
                .toList();
    }
    
    /**
     * Busca una entidad por su ID
     * @param id El ID de la entidad
     * @return Optional con la entidad si existe
     */
    public Optional<T> get(Long id) {
        return getRepository().findById(id);
    }
    
    /**
     * Actualiza o crea una entidad con manejo inteligente de versiones
     * @param entity La entidad a actualizar o crear
     * @return La entidad guardada
     */
    @Transactional
    public T update(T entity) {
        if (entity.getId() != null) {
            // Para actualizaciones, recargar la entidad desde la BD y copiar los valores
            T existingEntity = getRepository().findById(entity.getId())
                    .orElseThrow(() -> new RuntimeException(getEntityName() + " no encontrada con ID: " + entity.getId()));
            
            // Si version es null, actualizarlo directamente en BD con query nativa
            if (existingEntity.getVersion() == null) {
                fixVersionField(entity.getId());
                entityManager.clear(); // Limpiar cache para forzar recarga
                // Recargar la entidad con version actualizado desde BD
                existingEntity = getRepository().findById(entity.getId())
                        .orElseThrow(() -> new RuntimeException(getEntityName() + " no encontrada con ID: " + entity.getId()));
            }
            
            // Copiar los campos editables de la entidad recibida a la existente
            copyEditableFields(entity, existingEntity);
            
            return getRepository().save(existingEntity);
        } else {
            // Para nuevas entidades, usar save directamente
            return getRepository().save(entity);
        }
    }
    
    /**
     * Guarda una entidad directamente
     * @param entity La entidad a guardar
     * @return La entidad guardada
     */
    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }
    
    /**
     * Elimina una entidad (físicamente)
     * @param entity La entidad a eliminar
     */
    @Transactional
    public void delete(T entity) {
        getRepository().delete(entity);
    }
    
    /**
     * Elimina una entidad por ID (físicamente)
     * @param id El ID de la entidad a eliminar
     */
    @Transactional
    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }
    
    /**
     * Desactiva una entidad (eliminación lógica)
     * @param id El ID de la entidad a desactivar
     * @return La entidad desactivada
     */
    @Transactional
    public T deactivate(Long id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException(getEntityName() + " no encontrada con ID: " + id));
        entity.setActivo(false);
        return getRepository().save(entity);
    }
    
    /**
     * Activa una entidad
     * @param id El ID de la entidad a activar
     * @return La entidad activada
     */
    @Transactional
    public T activate(Long id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException(getEntityName() + " no encontrada con ID: " + id));
        entity.setActivo(true);
        return getRepository().save(entity);
    }
    
    /**
     * Arregla el campo version cuando es null
     * @param id El ID de la entidad
     */
    protected void fixVersionField(Long id) {
        entityManager.createNativeQuery("UPDATE " + getTableName() + " SET version = 0 WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
    
    /**
     * Copia los campos editables de una entidad a otra
     * Este método debe ser sobrescrito por las subclases para copiar campos específicos
     * @param source Entidad origen
     * @param target Entidad destino
     */
    protected abstract void copyEditableFields(T source, T target);
    
    /**
     * Cuenta el total de entidades
     * @return El número total de entidades
     */
    public long count() {
        return getRepository().count();
    }
    
    /**
     * Cuenta las entidades activas
     * @return El número de entidades activas
     */
    public long countActive() {
        return findActive().size();
    }
    
    /**
     * Verifica si existe una entidad por ID
     * @param id El ID a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(Long id) {
        return getRepository().existsById(id);
    }
}