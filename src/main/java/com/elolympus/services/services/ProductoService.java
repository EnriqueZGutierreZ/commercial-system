package com.elolympus.services.services;

import com.elolympus.data.Logistica.Producto;
import com.elolympus.data.Logistica.Marca;
import com.elolympus.data.Logistica.Linea;
import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.repository.ProductoRepository;
import com.elolympus.services.repository.MarcaRepository;
import com.elolympus.services.repository.LineaRepository;
import com.elolympus.services.repository.UnidadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final MarcaRepository marcaRepository;
    private final LineaRepository lineaRepository;
    private final UnidadRepository unidadRepository;

    public ProductoService(ProductoRepository repository,
                          MarcaRepository marcaRepository,
                          LineaRepository lineaRepository,
                          UnidadRepository unidadRepository) {
        this.repository = repository;
        this.marcaRepository = marcaRepository;
        this.lineaRepository = lineaRepository;
        this.unidadRepository = unidadRepository;
    }

    public Page<Producto> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Producto> findAll() {
        return repository.findAll();
    }

    public List<Producto> findActive() {
        return repository.findByActivoTrue();
    }

    @Transactional
    public Producto update(Producto entity) {
        // Recargar las entidades relacionadas desde la base de datos
        // para asegurar que tengan el campo version inicializado
        if (entity.getMarca() != null && entity.getMarca().getId() != null) {
            Marca marca = marcaRepository.findById(entity.getMarca().getId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
            entity.setMarca(marca);
        }
        
        if (entity.getLinea() != null && entity.getLinea().getId() != null) {
            Linea linea = lineaRepository.findById(entity.getLinea().getId())
                    .orElseThrow(() -> new RuntimeException("Línea no encontrada"));
            entity.setLinea(linea);
        }
        
        if (entity.getUnidad() != null && entity.getUnidad().getId() != null) {
            Unidad unidad = unidadRepository.findById(entity.getUnidad().getId())
                    .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));
            entity.setUnidad(unidad);
        }
        
        return repository.save(entity);
    }

    public Optional<Producto> get(Long id) {
        return repository.findById(id);
    }
}