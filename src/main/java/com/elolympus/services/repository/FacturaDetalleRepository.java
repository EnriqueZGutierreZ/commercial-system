package com.elolympus.services.repository;

import com.elolympus.data.Ventas.FacturaDetalle;
import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Logistica.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface FacturaDetalleRepository extends JpaRepository<FacturaDetalle, Long> {

    // Buscar detalles por factura
    List<FacturaDetalle> findByFactura(Factura factura);

    // Buscar detalles por producto
    List<FacturaDetalle> findByProducto(Producto producto);

    // Buscar detalles por factura ordenados por ID
    List<FacturaDetalle> findByFacturaOrderById(Factura factura);

    // Obtener ventas de un producto en un rango de fechas
    @Query("SELECT SUM(fd.cantidad) FROM FacturaDetalle fd WHERE fd.producto = :producto AND fd.factura.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND fd.factura.estado != 'ANULADA'")
    java.math.BigDecimal getCantidadVendidaEnRango(@Param("producto") Producto producto, @Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Obtener el total vendido de un producto
    @Query("SELECT SUM(fd.total) FROM FacturaDetalle fd WHERE fd.producto = :producto AND fd.factura.estado != 'ANULADA'")
    java.math.BigDecimal getTotalVendidoProducto(@Param("producto") Producto producto);

    // Productos más vendidos
    @Query("SELECT fd.producto, SUM(fd.cantidad) as total FROM FacturaDetalle fd WHERE fd.factura.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND fd.factura.estado != 'ANULADA' GROUP BY fd.producto ORDER BY total DESC")
    List<Object[]> getProductosMasVendidos(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Verificar si un producto tiene ventas
    @Query("SELECT COUNT(fd) > 0 FROM FacturaDetalle fd WHERE fd.producto = :producto")
    boolean productoTieneVentas(@Param("producto") Producto producto);
}