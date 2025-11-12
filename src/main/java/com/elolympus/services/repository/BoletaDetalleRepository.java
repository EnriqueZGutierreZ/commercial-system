package com.elolympus.services.repository;

import com.elolympus.data.Ventas.BoletaDetalle;
import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Logistica.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface BoletaDetalleRepository extends JpaRepository<BoletaDetalle, Long> {

    // Buscar detalles por boleta
    List<BoletaDetalle> findByBoleta(Boleta boleta);

    // Buscar detalles por producto
    List<BoletaDetalle> findByProducto(Producto producto);

    // Buscar detalles por boleta ordenados por ID
    List<BoletaDetalle> findByBoletaOrderById(Boleta boleta);

    // Obtener ventas de un producto en un rango de fechas
    @Query("SELECT SUM(bd.cantidad) FROM BoletaDetalle bd WHERE bd.producto = :producto AND bd.boleta.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND bd.boleta.estado != 'ANULADA'")
    java.math.BigDecimal getCantidadVendidaEnRango(@Param("producto") Producto producto, @Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Obtener el total vendido de un producto
    @Query("SELECT SUM(bd.total) FROM BoletaDetalle bd WHERE bd.producto = :producto AND bd.boleta.estado != 'ANULADA'")
    java.math.BigDecimal getTotalVendidoProducto(@Param("producto") Producto producto);

    // Productos más vendidos
    @Query("SELECT bd.producto, SUM(bd.cantidad) as total FROM BoletaDetalle bd WHERE bd.boleta.fechaEmision BETWEEN :fechaInicio AND :fechaFin AND bd.boleta.estado != 'ANULADA' GROUP BY bd.producto ORDER BY total DESC")
    List<Object[]> getProductosMasVendidos(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);

    // Verificar si un producto tiene ventas
    @Query("SELECT COUNT(bd) > 0 FROM BoletaDetalle bd WHERE bd.producto = :producto")
    boolean productoTieneVentas(@Param("producto") Producto producto);
}