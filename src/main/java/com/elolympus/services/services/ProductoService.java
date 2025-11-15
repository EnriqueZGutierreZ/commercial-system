package com.elolympus.services.services;

import com.elolympus.data.Logistica.Producto;
import com.elolympus.data.Logistica.Marca;
import com.elolympus.data.Logistica.Linea;
import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.repository.ProductoRepository;
import com.elolympus.services.repository.MarcaRepository;
import com.elolympus.services.repository.LineaRepository;
import com.elolympus.services.repository.UnidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService extends AbstractCrudService<Producto, ProductoRepository> {

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

    @Override
    protected ProductoRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "producto";
    }

    @Override
    protected String getEntityName() {
        return "Producto";
    }
    
    /**
     * Obtener productos activos con relaciones cargadas (optimizado para evitar N+1)
     */
    @Transactional(readOnly = true)
    public java.util.List<Producto> findActiveWithRelations() {
        return repository.findAllActiveWithRelations();
    }
    
    /**
     * Override del método findActive para usar query optimizada
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<Producto> findActive() {
        return repository.findAllActiveWithRelations();
    }

    @Override
    protected void copyEditableFields(Producto source, Producto target) {
        target.setCodigo(source.getCodigo());
        target.setNombre(source.getNombre());
        target.setDescripcion(source.getDescripcion());
        target.setMarca(source.getMarca());
        target.setLinea(source.getLinea());
        target.setUnidad(source.getUnidad());
        target.setPrecioCosto(source.getPrecioCosto());
        target.setPrecioVenta(source.getPrecioVenta());
        target.setStockMinimo(source.getStockMinimo());
        target.setStockMaximo(source.getStockMaximo());
        target.setPeso(source.getPeso());
        target.setVolumen(source.getVolumen());
    }

    @Override
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
        
        return super.update(entity);
    }
}