package com.elolympus.services.services;

import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.repository.BoletaRepository;
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
public class BoletaService {

    private final BoletaRepository boletaRepository;

    @Autowired
    public BoletaService(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    // CRUD básico
    public List<Boleta> findAll() {
        return boletaRepository.findAll();
    }

    public Optional<Boleta> findById(Long id) {
        return boletaRepository.findById(id);
    }

    public Boleta save(Boleta boleta) {
        // Generar número de boleta automáticamente si no existe
        if (boleta.getNumeroBoleta() == null || boleta.getNumeroBoleta().isEmpty()) {
            boleta.setNumeroBoleta(generateNumeroBoleta());
        }
        
        // Establecer fecha de creación si es nueva
        if (boleta.getId() == null) {
            boleta.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }
        
        // Calcular IGV automáticamente si no está establecido
        if (boleta.getSubtotal() != null && boleta.getIgv() == null) {
            boleta.calculateIgvFromSubtotal();
        }
        
        return boletaRepository.save(boleta);
    }

    public Boleta update(Boleta boleta) {
        return boletaRepository.save(boleta);
    }

    public void delete(Boleta boleta) {
        // No eliminar físicamente, mejor anular
        boleta.setEstado("ANULADA");
        boletaRepository.save(boleta);
    }

    public void deleteById(Long id) {
        Optional<Boleta> boleta = findById(id);
        if (boleta.isPresent()) {
            delete(boleta.get());
        }
    }

    // Métodos de búsqueda específicos
    public Optional<Boleta> findByNumeroBoleta(String numeroBoleta) {
        return boletaRepository.findByNumeroBoleta(numeroBoleta);
    }

    public List<Boleta> findByCliente(Persona cliente) {
        return boletaRepository.findByCliente(cliente);
    }

    public List<Boleta> findByEstado(String estado) {
        return boletaRepository.findByEstado(estado);
    }

    public List<Boleta> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin) {
        return boletaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    public List<Boleta> findBoletasPendientes() {
        return boletaRepository.findBoletasPendientes();
    }

    public List<Boleta> findBoletasVencidas() {
        return boletaRepository.findBoletasVencidas();
    }

    // Métodos de búsqueda para filtros
    public List<Boleta> findByNumeroBoletaContaining(String numeroBoleta) {
        return boletaRepository.findByNumeroBoletaContainingIgnoreCase(numeroBoleta);
    }

    public List<Boleta> findByClienteNombreContaining(String nombre) {
        return boletaRepository.findByClienteNombreContaining(nombre);
    }

    // Métodos para cambiar estado
    public Boleta marcarComoPagada(Long boletaId, String formaPago) {
        Optional<Boleta> optionalBoleta = findById(boletaId);
        if (optionalBoleta.isPresent()) {
            Boleta boleta = optionalBoleta.get();
            boleta.setEstado("PAGADA");
            boleta.setFormaPago(formaPago);
            boleta.setFechaPago(Date.valueOf(LocalDate.now()));
            return update(boleta);
        }
        throw new RuntimeException("Boleta no encontrada");
    }

    public Boleta anularBoleta(Long boletaId, String motivo) {
        Optional<Boleta> optionalBoleta = findById(boletaId);
        if (optionalBoleta.isPresent()) {
            Boleta boleta = optionalBoleta.get();
            boleta.setEstado("ANULADA");
            boleta.setObservaciones(motivo);
            return update(boleta);
        }
        throw new RuntimeException("Boleta no encontrada");
    }

    // Métodos de estadísticas
    public Long countByEstado(String estado) {
        return boletaRepository.countByEstado(estado);
    }

    public BigDecimal getTotalVentasEnRango(Date fechaInicio, Date fechaFin) {
        BigDecimal total = boletaRepository.getTotalVentasEnRango(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalVentasDelDia() {
        Date hoy = Date.valueOf(LocalDate.now());
        return getTotalVentasEnRango(hoy, hoy);
    }

    public BigDecimal getTotalVentasDelMes() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return getTotalVentasEnRango(Date.valueOf(inicioMes), Date.valueOf(finMes));
    }

    // Generar número de boleta automático
    private String generateNumeroBoleta() {
        long count = boletaRepository.count() + 1;
        return String.format("B%06d", count);
    }

    // Validar si un número de boleta ya existe
    public boolean existeNumeroBoleta(String numeroBoleta) {
        return boletaRepository.findByNumeroBoleta(numeroBoleta).isPresent();
    }

    // Método para buscar con múltiples criterios
    public List<Boleta> buscarConFiltros(String numeroBoleta, String clienteNombre, String estado, Date fechaInicio, Date fechaFin) {
        List<Boleta> result = findAll();
        
        if (numeroBoleta != null && !numeroBoleta.isEmpty()) {
            result = result.stream()
                    .filter(b -> b.getNumeroBoleta().toLowerCase().contains(numeroBoleta.toLowerCase()))
                    .toList();
        }
        
        if (clienteNombre != null && !clienteNombre.isEmpty()) {
            result = result.stream()
                    .filter(b -> {
                        String nombreCompleto = b.getCliente().getNombres() + " " + b.getCliente().getApellidos();
                        return nombreCompleto.toLowerCase().contains(clienteNombre.toLowerCase());
                    })
                    .toList();
        }
        
        if (estado != null && !estado.isEmpty()) {
            result = result.stream()
                    .filter(b -> b.getEstado().equals(estado))
                    .toList();
        }
        
        if (fechaInicio != null && fechaFin != null) {
            result = result.stream()
                    .filter(b -> !b.getFechaEmision().before(fechaInicio) && !b.getFechaEmision().after(fechaFin))
                    .toList();
        }
        
        return result;
    }
}