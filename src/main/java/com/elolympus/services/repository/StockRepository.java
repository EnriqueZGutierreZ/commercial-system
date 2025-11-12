package com.elolympus.services.repository;

import com.elolympus.data.Almacen.Stock;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.data.Logistica.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // Buscar stock por producto y almacén
    Optional<Stock> findByProductoAndAlmacen(Producto producto, Almacen almacen);

    // Buscar todos los stocks de un producto
    List<Stock> findByProducto(Producto producto);

    // Buscar todos los stocks de un almacén
    List<Stock> findByAlmacen(Almacen almacen);

    // Obtener stock total de un producto (suma de todos los almacenes)
    @Query("SELECT SUM(s.stockActual) FROM Stock s WHERE s.producto = :producto")
    BigDecimal getStockTotalProducto(@Param("producto") Producto producto);

    // Obtener stock disponible total de un producto
    @Query("SELECT SUM(s.stockDisponible) FROM Stock s WHERE s.producto = :producto")
    BigDecimal getStockDisponibleTotalProducto(@Param("producto") Producto producto);

    // Productos con stock bajo mínimo
    @Query("SELECT s FROM Stock s WHERE s.stockActual < s.producto.stockMinimo AND s.producto.stockMinimo IS NOT NULL")
    List<Stock> findProductosConStockBajo();

    // Productos sin stock
    @Query("SELECT s FROM Stock s WHERE s.stockActual = 0")
    List<Stock> findProductosSinStock();

    // Productos con stock disponible mayor a cero
    @Query("SELECT s FROM Stock s WHERE s.stockDisponible > 0")
    List<Stock> findProductosConStockDisponible();

    // Buscar por producto con stock disponible suficiente
    @Query("SELECT s FROM Stock s WHERE s.producto = :producto AND s.stockDisponible >= :cantidadRequerida")
    List<Stock> findByProductoConStockSuficiente(@Param("producto") Producto producto, @Param("cantidadRequerida") BigDecimal cantidadRequerida);

    // Obtener valor total del inventario por almacén
    @Query("SELECT SUM(s.stockActual * s.costoPromedio) FROM Stock s WHERE s.almacen = :almacen")
    BigDecimal getValorInventarioPorAlmacen(@Param("almacen") Almacen almacen);

    // Obtener valor total del inventario
    @Query("SELECT SUM(s.stockActual * s.costoPromedio) FROM Stock s")
    BigDecimal getValorTotalInventario();

    // Productos que necesitan reabastecimiento (por almacén)
    @Query("SELECT s FROM Stock s WHERE s.almacen = :almacen AND s.stockActual <= s.producto.stockMinimo AND s.producto.stockMinimo IS NOT NULL")
    List<Stock> findProductosParaReabastecer(@Param("almacen") Almacen almacen);

    // Verificar si existe stock para un producto en cualquier almacén
    @Query("SELECT COUNT(s) > 0 FROM Stock s WHERE s.producto = :producto AND s.stockDisponible > 0")
    boolean existeStockDisponible(@Param("producto") Producto producto);

    // Obtener el almacén con mayor stock de un producto
    @Query("SELECT s FROM Stock s WHERE s.producto = :producto ORDER BY s.stockDisponible DESC")
    List<Stock> findAlmacenesConMayorStock(@Param("producto") Producto producto);

    // Productos por vencer (si tuvieran fecha de vencimiento)
    @Query("SELECT s FROM Stock s WHERE s.stockActual > 0 ORDER BY s.ultimaActualizacion ASC")
    List<Stock> findStockOrdenadoPorAntiguedad();

    // Resumen de stock por producto
    @Query("SELECT s.producto, SUM(s.stockActual), SUM(s.stockDisponible), AVG(s.costoPromedio) FROM Stock s GROUP BY s.producto")
    List<Object[]> getResumenStockPorProducto();
}