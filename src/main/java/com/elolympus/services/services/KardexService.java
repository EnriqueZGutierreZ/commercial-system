package com.elolympus.services.services;

import com.elolympus.data.Almacen.Kardex;
import com.elolympus.services.repository.KardexRepository;
import org.springframework.stereotype.Service;

@Service
public class KardexService extends AbstractCrudService<Kardex, KardexRepository> {

    protected final KardexRepository repository;

    public KardexService(KardexRepository repository) {
        this.repository = repository;
    }

    @Override
    protected KardexRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "kardex";
    }

    @Override
    protected String getEntityName() {
        return "Kardex";
    }

    @Override
    protected void copyEditableFields(Kardex source, Kardex target) {
        target.setOrdenId(source.getOrdenId());
        target.setFecha(source.getFecha());
        target.setFechaOrden(source.getFechaOrden());
        target.setMovimiento(source.getMovimiento());
        target.setAlmacen(source.getAlmacen());
        target.setOrigen(source.getOrigen());
        target.setDestino(source.getDestino());
        target.setPrecioCosto(source.getPrecioCosto());
        target.setPrecioVenta(source.getPrecioVenta());
        target.setStockAnterior(source.getStockAnterior());
        target.setIngreso(source.getIngreso());
        target.setSalida(source.getSalida());
        target.setStock(source.getStock());
        target.setProducto(source.getProducto());
        target.setFechaVencimiento(source.getFechaVencimiento());
    }
}
