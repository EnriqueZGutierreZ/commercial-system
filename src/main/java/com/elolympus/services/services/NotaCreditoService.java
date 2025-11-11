package com.elolympus.services.services;

import com.elolympus.data.Ventas.NotaCredito;
import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.repository.NotaCreditoRepository;
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
public class NotaCreditoService {

    private final NotaCreditoRepository notaCreditoRepository;

    @Autowired
    public NotaCreditoService(NotaCreditoRepository notaCreditoRepository) {
        this.notaCreditoRepository = notaCreditoRepository;
    }

    // CRUD básico
    public List<NotaCredito> findAll() {
        return notaCreditoRepository.findAll();
    }

    public Optional<NotaCredito> findById(Long id) {
        return notaCreditoRepository.findById(id);
    }

    public NotaCredito save(NotaCredito notaCredito) {
        // Generar número de nota automáticamente si no existe
        if (notaCredito.getNumeroNota() == null || notaCredito.getNumeroNota().isEmpty()) {
            String serie = notaCredito.getSerie() != null ? notaCredito.getSerie() : "NC01";
            notaCredito.setSerie(serie);
            notaCredito.setNumeroNota(generateNumeroNota(serie));
        }
        
        // Establecer fecha de creación si es nueva
        if (notaCredito.getId() == null) {
            notaCredito.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }
        
        // Calcular IGV automáticamente si no está establecido
        if (notaCredito.getSubtotal() != null && notaCredito.getIgv() == null) {
            notaCredito.calculateIgvFromSubtotal();
        }
        
        return notaCreditoRepository.save(notaCredito);
    }

    public NotaCredito update(NotaCredito notaCredito) {
        return notaCreditoRepository.save(notaCredito);
    }

    public void delete(NotaCredito notaCredito) {
        // No eliminar físicamente, mejor anular
        notaCredito.setEstado("ANULADA");
        notaCreditoRepository.save(notaCredito);
    }

    public void deleteById(Long id) {
        Optional<NotaCredito> notaCredito = findById(id);
        if (notaCredito.isPresent()) {
            delete(notaCredito.get());
        }
    }

    // Métodos de búsqueda específicos
    public Optional<NotaCredito> findByNumeroNota(String numeroNota) {
        return notaCreditoRepository.findByNumeroNota(numeroNota);
    }

    public Optional<NotaCredito> findBySerieAndNumero(String serie, String numero) {
        return notaCreditoRepository.findBySerieAndNumeroNota(serie, numero);
    }

    public List<NotaCredito> findByCliente(Persona cliente) {
        return notaCreditoRepository.findByCliente(cliente);
    }

    public List<NotaCredito> findByEstado(String estado) {
        return notaCreditoRepository.findByEstado(estado);
    }

    public List<NotaCredito> findBySerie(String serie) {
        return notaCreditoRepository.findBySerie(serie);
    }

    public List<NotaCredito> findByTipoNota(String tipoNota) {
        return notaCreditoRepository.findByTipoNota(tipoNota);
    }

    public List<NotaCredito> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin) {
        return notaCreditoRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
    }

    public List<NotaCredito> findNotasCreditoEmitidas() {
        return notaCreditoRepository.findNotasCreditoEmitidas();
    }

    public List<NotaCredito> findNotasCreditoAplicadas() {
        return notaCreditoRepository.findNotasCreditoAplicadas();
    }

    public List<NotaCredito> findByFacturaReferencia(Factura factura) {
        return notaCreditoRepository.findByFacturaReferencia(factura);
    }

    public List<NotaCredito> findByBoletaReferencia(Boleta boleta) {
        return notaCreditoRepository.findByBoletaReferencia(boleta);
    }

    // Métodos de búsqueda para filtros
    public List<NotaCredito> findByNumeroNotaContaining(String numeroNota) {
        return notaCreditoRepository.findByNumeroNotaContainingIgnoreCase(numeroNota);
    }

    public List<NotaCredito> findByClienteNombreContaining(String nombre) {
        return notaCreditoRepository.findByClienteNombreContaining(nombre);
    }

    public List<NotaCredito> findBySerieContaining(String serie) {
        return notaCreditoRepository.findBySerieContainingIgnoreCase(serie);
    }

    public List<NotaCredito> findByMotivoContaining(String motivo) {
        return notaCreditoRepository.findByMotivoContainingIgnoreCase(motivo);
    }

    public List<NotaCredito> findByDocumentoReferenciaNumeroContaining(String numero) {
        return notaCreditoRepository.findByDocumentoReferenciaNumeroContaining(numero);
    }

    // Métodos para cambiar estado
    public NotaCredito aplicarNotaCredito(Long notaCreditoId) {
        Optional<NotaCredito> optionalNotaCredito = findById(notaCreditoId);
        if (optionalNotaCredito.isPresent()) {
            NotaCredito notaCredito = optionalNotaCredito.get();
            notaCredito.setEstado("APLICADA");
            return update(notaCredito);
        }
        throw new RuntimeException("Nota de crédito no encontrada");
    }

    public NotaCredito anularNotaCredito(Long notaCreditoId, String motivo) {
        Optional<NotaCredito> optionalNotaCredito = findById(notaCreditoId);
        if (optionalNotaCredito.isPresent()) {
            NotaCredito notaCredito = optionalNotaCredito.get();
            notaCredito.setEstado("ANULADA");
            notaCredito.setObservaciones(motivo);
            return update(notaCredito);
        }
        throw new RuntimeException("Nota de crédito no encontrada");
    }

    // Métodos para crear notas de crédito a partir de documentos existentes
    public NotaCredito crearNotaCreditoParaFactura(Factura factura, BigDecimal monto, String motivo, String tipoNota) {
        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setFacturaReferencia(factura);
        notaCredito.setCliente(factura.getCliente());
        notaCredito.setEmpresa(factura.getEmpresa());
        notaCredito.setFechaEmision(Date.valueOf(LocalDate.now()));
        notaCredito.setMotivo(motivo);
        notaCredito.setTipoNota(tipoNota);
        notaCredito.setMoneda(factura.getMoneda());
        
        // Calcular montos proporcionalmente
        if (monto.compareTo(factura.getTotal()) <= 0) {
            BigDecimal proporcion = monto.divide(factura.getTotal(), 4, BigDecimal.ROUND_HALF_UP);
            notaCredito.setSubtotal(factura.getSubtotal().multiply(proporcion));
            notaCredito.setIgv(factura.getIgv().multiply(proporcion));
            notaCredito.setTotal(monto);
        } else {
            throw new RuntimeException("El monto de la nota de crédito no puede ser mayor al total de la factura");
        }
        
        return save(notaCredito);
    }

    public NotaCredito crearNotaCreditoParaBoleta(Boleta boleta, BigDecimal monto, String motivo, String tipoNota) {
        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setBoletaReferencia(boleta);
        notaCredito.setCliente(boleta.getCliente());
        notaCredito.setEmpresa(boleta.getEmpresa());
        notaCredito.setFechaEmision(Date.valueOf(LocalDate.now()));
        notaCredito.setMotivo(motivo);
        notaCredito.setTipoNota(tipoNota);
        notaCredito.setMoneda("PEN"); // Las boletas generalmente son en soles
        
        // Calcular montos proporcionalmente
        if (monto.compareTo(boleta.getTotal()) <= 0) {
            BigDecimal proporcion = monto.divide(boleta.getTotal(), 4, BigDecimal.ROUND_HALF_UP);
            notaCredito.setSubtotal(boleta.getSubtotal().multiply(proporcion));
            notaCredito.setIgv(boleta.getIgv().multiply(proporcion));
            notaCredito.setTotal(monto);
        } else {
            throw new RuntimeException("El monto de la nota de crédito no puede ser mayor al total de la boleta");
        }
        
        return save(notaCredito);
    }

    // Métodos de estadísticas
    public Long countByEstado(String estado) {
        return notaCreditoRepository.countByEstado(estado);
    }

    public BigDecimal getTotalNotasCreditoEnRango(Date fechaInicio, Date fechaFin) {
        BigDecimal total = notaCreditoRepository.getTotalNotasCreditoEnRango(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalIgvNotasCreditoEnRango(Date fechaInicio, Date fechaFin) {
        BigDecimal total = notaCreditoRepository.getTotalIgvNotasCreditoEnRango(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalNotasCreditoDelDia() {
        Date hoy = Date.valueOf(LocalDate.now());
        return getTotalNotasCreditoEnRango(hoy, hoy);
    }

    public BigDecimal getTotalNotasCreditoDelMes() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return getTotalNotasCreditoEnRango(Date.valueOf(inicioMes), Date.valueOf(finMes));
    }

    // Generar número de nota automático por serie
    private String generateNumeroNota(String serie) {
        String ultimoNumero = notaCreditoRepository.getUltimoNumeroPorSerie(serie);
        int siguienteNumero = 1;
        
        if (ultimoNumero != null) {
            try {
                siguienteNumero = Integer.parseInt(ultimoNumero) + 1;
            } catch (NumberFormatException e) {
                // Si no se puede parsear, empezar desde 1
                siguienteNumero = 1;
            }
        }
        
        return String.format("%08d", siguienteNumero);
    }

    // Validar si un número de nota ya existe en una serie
    public boolean existeNumeroNota(String serie, String numeroNota) {
        return notaCreditoRepository.findBySerieAndNumeroNota(serie, numeroNota).isPresent();
    }

    // Verificar si existe nota de crédito para un documento
    public boolean existeNotaCreditoParaDocumento(String tipo, String serie, String numero) {
        return notaCreditoRepository.existeNotaCreditoParaDocumento(tipo, serie, numero);
    }

    // Obtener monto total creditado para un documento
    public BigDecimal getTotalNotasCreditoAplicadasAFactura(Factura factura) {
        BigDecimal total = notaCreditoRepository.getTotalNotasCreditoAplicadasAFactura(factura);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalNotasCreditoAplicadasABoleta(Boleta boleta) {
        BigDecimal total = notaCreditoRepository.getTotalNotasCreditoAplicadasABoleta(boleta);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Método para buscar con múltiples criterios
    public List<NotaCredito> buscarConFiltros(String numeroNota, String serie, String clienteNombre, 
                                            String estado, String tipoNota, String documentoReferencia,
                                            Date fechaInicio, Date fechaFin) {
        List<NotaCredito> result = findAll();
        
        if (numeroNota != null && !numeroNota.isEmpty()) {
            result = result.stream()
                    .filter(nc -> nc.getNumeroNota().toLowerCase().contains(numeroNota.toLowerCase()))
                    .toList();
        }
        
        if (serie != null && !serie.isEmpty()) {
            result = result.stream()
                    .filter(nc -> nc.getSerie() != null && nc.getSerie().toLowerCase().contains(serie.toLowerCase()))
                    .toList();
        }
        
        if (clienteNombre != null && !clienteNombre.isEmpty()) {
            result = result.stream()
                    .filter(nc -> {
                        String nombreCompleto = nc.getCliente().getNombres() + " " + nc.getCliente().getApellidos();
                        return nombreCompleto.toLowerCase().contains(clienteNombre.toLowerCase());
                    })
                    .toList();
        }
        
        if (estado != null && !estado.isEmpty()) {
            result = result.stream()
                    .filter(nc -> nc.getEstado().equals(estado))
                    .toList();
        }
        
        if (tipoNota != null && !tipoNota.isEmpty()) {
            result = result.stream()
                    .filter(nc -> nc.getTipoNota().equals(tipoNota))
                    .toList();
        }
        
        if (documentoReferencia != null && !documentoReferencia.isEmpty()) {
            result = result.stream()
                    .filter(nc -> {
                        String referencia = nc.getReferenciaCompleta();
                        return referencia.toLowerCase().contains(documentoReferencia.toLowerCase());
                    })
                    .toList();
        }
        
        if (fechaInicio != null && fechaFin != null) {
            result = result.stream()
                    .filter(nc -> !nc.getFechaEmision().before(fechaInicio) && !nc.getFechaEmision().after(fechaFin))
                    .toList();
        }
        
        return result;
    }

    // Métodos para reportes
    public List<Object[]> getNotasCreditoPorCliente(Date fechaInicio, Date fechaFin) {
        return notaCreditoRepository.getNotasCreditoPorCliente(fechaInicio, fechaFin);
    }

    public List<Object[]> getReporteNotasCreditoPorTipo(Date fechaInicio, Date fechaFin) {
        return notaCreditoRepository.getReporteNotasCreditoPorTipo(fechaInicio, fechaFin);
    }
}