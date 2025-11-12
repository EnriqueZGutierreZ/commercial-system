package com.elolympus.services.services;

import com.elolympus.data.Almacen.Stock;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.data.Almacen.Kardex;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.services.repository.StockRepository;
import com.elolympus.services.repository.AlmacenRepository;
import com.elolympus.services.services.KardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final AlmacenRepository almacenRepository;
    private final KardexService kardexService;

    @Autowired
    public StockService(StockRepository stockRepository, AlmacenRepository almacenRepository, KardexService kardexService) {
        this.stockRepository = stockRepository;
        this.almacenRepository = almacenRepository;
        this.kardexService = kardexService;
    }

    // CRUD básico
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Optional<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }

    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    public void deleteById(Long id) {
        stockRepository.deleteById(id);
    }

    // Métodos de búsqueda específicos
    public Optional<Stock> findByProductoAndAlmacen(Producto producto, Almacen almacen) {
        return stockRepository.findByProductoAndAlmacen(producto, almacen);
    }

    public List<Stock> findByProducto(Producto producto) {
        return stockRepository.findByProducto(producto);
    }

    public List<Stock> findByAlmacen(Almacen almacen) {
        return stockRepository.findByAlmacen(almacen);
    }

    // Métodos de consulta de stock
    public BigDecimal getStockTotalProducto(Producto producto) {
        BigDecimal total = stockRepository.getStockTotalProducto(producto);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getStockDisponibleTotalProducto(Producto producto) {
        BigDecimal total = stockRepository.getStockDisponibleTotalProducto(producto);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Obtener o crear stock para un producto en un almacén
    public Stock getOrCreateStock(Producto producto, Almacen almacen) {
        Optional<Stock> stockOpt = findByProductoAndAlmacen(producto, almacen);
        if (stockOpt.isPresent()) {
            return stockOpt.get();
        } else {
            Stock nuevoStock = new Stock(producto, almacen, BigDecimal.ZERO);
            return save(nuevoStock);
        }
    }

    // Obtener almacén principal (primer almacén disponible)
    public Almacen getAlmacenPrincipal() {
        List<Almacen> almacenes = almacenRepository.findAll();
        return almacenes.isEmpty() ? null : almacenes.get(0);
    }

    // MÉTODOS PRINCIPALES PARA INTEGRACIÓN VENTAS-STOCK

    /**
     * Procesar salida de stock por venta
     * @param producto Producto vendido
     * @param cantidad Cantidad vendida
     * @param almacen Almacén de donde sale (opcional, usa principal si es null)
     * @param tipoMovimiento VENTA_FACTURA o VENTA_BOLETA
     * @param numeroDocumento Número de factura o boleta
     * @return true si se pudo descontar el stock, false si no hay stock suficiente
     */
    public boolean procesarSalidaPorVenta(Producto producto, BigDecimal cantidad, Almacen almacen, 
                                         String tipoMovimiento, String numeroDocumento) {
        try {
            // Si no se especifica almacén, usar el principal
            if (almacen == null) {
                almacen = getAlmacenPrincipal();
                if (almacen == null) {
                    throw new RuntimeException("No hay almacenes configurados");
                }
            }

            // Obtener o crear stock
            Stock stock = getOrCreateStock(producto, almacen);

            // Verificar si hay stock suficiente
            if (!stock.tieneStockDisponible(cantidad)) {
                return false; // No hay stock suficiente
            }

            // Restar stock
            boolean stockDescontado = stock.restarStock(cantidad);
            if (stockDescontado) {
                // Guardar cambios en stock
                save(stock);

                // Crear movimiento en kardex
                crearMovimientoKardex(producto, almacen, cantidad, BigDecimal.ZERO, cantidad,
                                    stock.getStockActual(), tipoMovimiento, numeroDocumento);
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Procesar entrada de stock por compra o ingreso
     */
    public boolean procesarEntradaPorCompra(Producto producto, BigDecimal cantidad, BigDecimal costoUnitario,
                                          Almacen almacen, String numeroDocumento) {
        try {
            if (almacen == null) {
                almacen = getAlmacenPrincipal();
                if (almacen == null) {
                    throw new RuntimeException("No hay almacenes configurados");
                }
            }

            Stock stock = getOrCreateStock(producto, almacen);
            BigDecimal stockAnterior = stock.getStockActual();

            // Agregar stock
            stock.agregarStock(cantidad, costoUnitario);
            save(stock);

            // Crear movimiento en kardex
            crearMovimientoKardex(producto, almacen, cantidad, cantidad, BigDecimal.ZERO,
                                stock.getStockActual(), "COMPRA", numeroDocumento);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reversar movimiento de stock (por anulación de venta)
     */
    public boolean reversarSalidaPorVenta(Producto producto, BigDecimal cantidad, Almacen almacen,
                                        String tipoMovimiento, String numeroDocumento) {
        try {
            if (almacen == null) {
                almacen = getAlmacenPrincipal();
            }

            Stock stock = getOrCreateStock(producto, almacen);
            
            // Devolver stock
            stock.agregarStock(cantidad, stock.getCostoPromedio());
            save(stock);

            // Crear movimiento en kardex (ajuste positivo)
            crearMovimientoKardex(producto, almacen, cantidad, cantidad, BigDecimal.ZERO,
                                stock.getStockActual(), "DEVOLUCION_" + tipoMovimiento, 
                                "ANULACION-" + numeroDocumento);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método privado para crear movimientos en kardex
    private void crearMovimientoKardex(Producto producto, Almacen almacen, BigDecimal cantidad,
                                     BigDecimal ingreso, BigDecimal salida, BigDecimal stockFinal,
                                     String tipoMovimiento, String referencia) {
        try {
            Kardex kardex = new Kardex();
            kardex.setProducto(producto);
            kardex.setAlmacen(almacen.getId().intValue());
            kardex.setFecha(new Timestamp(System.currentTimeMillis()));
            kardex.setFechaOrden(Date.valueOf(LocalDate.now()));
            kardex.setMovimiento(tipoMovimiento);
            kardex.setOrigen(almacen.getDescripcion());
            kardex.setDestino(tipoMovimiento.startsWith("VENTA") ? "CLIENTE" : "INVENTARIO");
            kardex.setPrecioCosto(producto.getPrecioCosto());
            kardex.setPrecioVenta(producto.getPrecioVenta());
            
            // Calcular stock anterior
            BigDecimal stockAnterior = stockFinal.subtract(ingreso).add(salida);
            kardex.setStockAnterior(stockAnterior);
            kardex.setIngreso(ingreso);
            kardex.setSalida(salida);
            kardex.setStock(stockFinal);

            // Guardar en kardex
            kardexService.save(kardex);
        } catch (Exception e) {
            // Log error pero no fallar la transacción principal
            System.err.println("Error creando movimiento kardex: " + e.getMessage());
        }
    }

    // Métodos de consulta para alertas
    public List<Stock> getProductosConStockBajo() {
        return stockRepository.findProductosConStockBajo();
    }

    public List<Stock> getProductosSinStock() {
        return stockRepository.findProductosSinStock();
    }

    public List<Stock> getProductosParaReabastecer(Almacen almacen) {
        return stockRepository.findProductosParaReabastecer(almacen);
    }

    // Métodos de validación
    public boolean validarStockParaVenta(Producto producto, BigDecimal cantidadRequerida, Almacen almacen) {
        if (almacen == null) {
            // Verificar stock en cualquier almacén
            return getStockDisponibleTotalProducto(producto).compareTo(cantidadRequerida) >= 0;
        } else {
            // Verificar stock en almacén específico
            Optional<Stock> stock = findByProductoAndAlmacen(producto, almacen);
            return stock.isPresent() && stock.get().tieneStockDisponible(cantidadRequerida);
        }
    }

    // Métodos para reportes
    public BigDecimal getValorTotalInventario() {
        BigDecimal valor = stockRepository.getValorTotalInventario();
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public BigDecimal getValorInventarioPorAlmacen(Almacen almacen) {
        BigDecimal valor = stockRepository.getValorInventarioPorAlmacen(almacen);
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public List<Object[]> getResumenStockPorProducto() {
        return stockRepository.getResumenStockPorProducto();
    }

    // Método para inicializar stock de productos existentes
    public void inicializarStockProductos() {
        // Este método se puede usar para crear registros de stock para productos que no los tienen
        almacenRepository.findAll().forEach(almacen -> {
            // Aquí podrías agregar lógica para inicializar stocks si es necesario
        });
    }
}