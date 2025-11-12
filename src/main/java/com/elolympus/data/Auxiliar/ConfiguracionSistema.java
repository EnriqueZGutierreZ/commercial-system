package com.elolympus.data.Auxiliar;

import com.elolympus.data.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_sistema")
public class ConfiguracionSistema extends AbstractEntity {

    // Configuración de Impuestos
    @Column(name = "igv_porcentaje", precision = 5, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El IGV no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El IGV no puede ser mayor a 100%")
    @NotNull(message = "El porcentaje de IGV es obligatorio")
    private BigDecimal igvPorcentaje = new BigDecimal("18.00");

    @Column(name = "aplica_igv_por_defecto", nullable = false)
    private Boolean aplicaIgvPorDefecto = true;

    // Series de Documentos
    @Column(name = "serie_factura", length = 10)
    @Size(max = 10, message = "La serie de factura no puede exceder 10 caracteres")
    private String serieFactura = "F001";

    @Column(name = "serie_boleta", length = 10)
    @Size(max = 10, message = "La serie de boleta no puede exceder 10 caracteres")
    private String serieBoleta = "B001";

    @Column(name = "serie_nota_credito", length = 10)
    @Size(max = 10, message = "La serie de nota de crédito no puede exceder 10 caracteres")
    private String serieNotaCredito = "NC01";

    @Column(name = "serie_orden_compra", length = 10)
    @Size(max = 10, message = "La serie de orden de compra no puede exceder 10 caracteres")
    private String serieOrdenCompra = "OC01";

    // Numeración Automática
    @Column(name = "numeracion_automatica_facturas", nullable = false)
    private Boolean numeracionAutomaticaFacturas = true;

    @Column(name = "numeracion_automatica_boletas", nullable = false)
    private Boolean numeracionAutomaticaBoletas = true;

    @Column(name = "numeracion_automatica_notas", nullable = false)
    private Boolean numeracionAutomaticaNotas = true;

    @Column(name = "siguiente_numero_factura")
    @Min(value = 1, message = "El siguiente número de factura debe ser mayor a 0")
    private Long siguienteNumeroFactura = 1L;

    @Column(name = "siguiente_numero_boleta")
    @Min(value = 1, message = "El siguiente número de boleta debe ser mayor a 0")
    private Long siguienteNumeroBoleta = 1L;

    @Column(name = "siguiente_numero_nota_credito")
    @Min(value = 1, message = "El siguiente número de nota de crédito debe ser mayor a 0")
    private Long siguienteNumeroNotaCredito = 1L;

    @Column(name = "siguiente_numero_orden_compra")
    @Min(value = 1, message = "El siguiente número de orden de compra debe ser mayor a 0")
    private Long siguienteNumeroOrdenCompra = 1L;

    // Configuración de Moneda
    @Column(name = "moneda_por_defecto", length = 3)
    @Size(min = 3, max = 3, message = "La moneda debe tener 3 caracteres")
    private String monedaPorDefecto = "PEN";

    @Column(name = "simbolo_moneda", length = 5)
    @Size(max = 5, message = "El símbolo de moneda no puede exceder 5 caracteres")
    private String simboloMoneda = "S/.";

    // Configuración de Stock
    @Column(name = "alertar_stock_minimo", nullable = false)
    private Boolean alertarStockMinimo = true;

    @Column(name = "bloquear_venta_sin_stock", nullable = false)
    private Boolean bloquearVentaSinStock = true;

    @Column(name = "stock_minimo_global", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "El stock mínimo global no puede ser negativo")
    private BigDecimal stockMinimoGlobal = new BigDecimal("5.00");

    // Configuración de Precios
    @Column(name = "redondear_precios", nullable = false)
    private Boolean redondearPrecios = true;

    @Column(name = "decimales_precio")
    @Min(value = 0, message = "Los decimales no pueden ser negativos")
    @Max(value = 4, message = "No se pueden usar más de 4 decimales")
    private Integer decimalesPrecio = 2;

    // Configuración de Empresa
    @Column(name = "nombre_empresa", length = 100)
    @Size(max = 100, message = "El nombre de empresa no puede exceder 100 caracteres")
    private String nombreEmpresa;

    @Column(name = "ruc_empresa", length = 11)
    @Size(min = 0, max = 11, message = "El RUC debe tener máximo 11 dígitos")
    private String rucEmpresa;

    @Column(name = "direccion_empresa", length = 200)
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccionEmpresa;

    @Column(name = "telefono_empresa", length = 20)
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefonoEmpresa;

    @Column(name = "email_empresa", length = 100)
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailEmpresa;

    @Column(name = "logo_empresa", length = 500)
    @Size(max = 500, message = "La ruta del logo no puede exceder 500 caracteres")
    private String logoEmpresa;

    // Configuración de Facturación Electrónica
    @Column(name = "facturacion_electronica_activa", nullable = false)
    private Boolean facturacionElectronicaActiva = false;

    @Column(name = "ambiente_sunat", length = 20)
    private String ambienteSunat = "PRUEBA"; // PRUEBA o PRODUCCION

    @Column(name = "certificado_digital", length = 500)
    private String certificadoDigital;

    @Column(name = "clave_certificado", length = 100)
    private String claveCertificado;

    // Configuración de Backup
    @Column(name = "backup_automatico", nullable = false)
    private Boolean backupAutomatico = false;

    @Column(name = "dias_retencion_backup")
    @Min(value = 1, message = "Los días de retención deben ser al menos 1")
    private Integer diasRetencionBackup = 30;

    @Column(name = "ruta_backup", length = 500)
    private String rutaBackup;

    // Configuración de Seguridad
    @Column(name = "sesion_timeout_minutos")
    @Min(value = 5, message = "El timeout de sesión debe ser al menos 5 minutos")
    @Max(value = 480, message = "El timeout de sesión no puede exceder 8 horas")
    private Integer sesionTimeoutMinutos = 60;

    @Column(name = "intentos_login_max")
    @Min(value = 3, message = "Los intentos máximos deben ser al menos 3")
    @Max(value = 10, message = "Los intentos máximos no pueden exceder 10")
    private Integer intentosLoginMax = 5;

    @Column(name = "bloqueo_usuario_minutos")
    @Min(value = 1, message = "El bloqueo debe ser al menos 1 minuto")
    private Integer bloqueoUsuarioMinutos = 15;

    // Metadatos de configuración
    // El campo activo se hereda de AbstractEntity

    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;

    @Column(name = "usuario_modificacion", length = 50)
    private String usuarioModificacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Constructor por defecto
    public ConfiguracionSistema() {
        this.fechaUltimaModificacion = LocalDateTime.now();
        this.creado = LocalDateTime.now();
        this.creador = "SISTEMA";
        this.activo = true;
    }

    // Métodos de utilidad
    @PreUpdate
    public void onUpdate() {
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    public String getProximoNumeroFactura() {
        return String.format("%s-%08d", serieFactura, siguienteNumeroFactura);
    }

    public String getProximoNumeroBoleta() {
        return String.format("%s-%08d", serieBoleta, siguienteNumeroBoleta);
    }

    public String getProximoNumeroNotaCredito() {
        return String.format("%s-%08d", serieNotaCredito, siguienteNumeroNotaCredito);
    }

    public String getProximoNumeroOrdenCompra() {
        return String.format("%s-%08d", serieOrdenCompra, siguienteNumeroOrdenCompra);
    }

    public void incrementarFactura() {
        this.siguienteNumeroFactura++;
    }

    public void incrementarBoleta() {
        this.siguienteNumeroBoleta++;
    }

    public void incrementarNotaCredito() {
        this.siguienteNumeroNotaCredito++;
    }

    public void incrementarOrdenCompra() {
        this.siguienteNumeroOrdenCompra++;
    }

    // Getters y Setters
    public BigDecimal getIgvPorcentaje() { return igvPorcentaje; }
    public void setIgvPorcentaje(BigDecimal igvPorcentaje) { this.igvPorcentaje = igvPorcentaje; }

    public Boolean getAplicaIgvPorDefecto() { return aplicaIgvPorDefecto; }
    public void setAplicaIgvPorDefecto(Boolean aplicaIgvPorDefecto) { this.aplicaIgvPorDefecto = aplicaIgvPorDefecto; }

    public String getSerieFactura() { return serieFactura; }
    public void setSerieFactura(String serieFactura) { this.serieFactura = serieFactura; }

    public String getSerieBoleta() { return serieBoleta; }
    public void setSerieBoleta(String serieBoleta) { this.serieBoleta = serieBoleta; }

    public String getSerieNotaCredito() { return serieNotaCredito; }
    public void setSerieNotaCredito(String serieNotaCredito) { this.serieNotaCredito = serieNotaCredito; }

    public String getSerieOrdenCompra() { return serieOrdenCompra; }
    public void setSerieOrdenCompra(String serieOrdenCompra) { this.serieOrdenCompra = serieOrdenCompra; }

    public Boolean getNumeracionAutomaticaFacturas() { return numeracionAutomaticaFacturas; }
    public void setNumeracionAutomaticaFacturas(Boolean numeracionAutomaticaFacturas) { this.numeracionAutomaticaFacturas = numeracionAutomaticaFacturas; }

    public Boolean getNumeracionAutomaticaBoletas() { return numeracionAutomaticaBoletas; }
    public void setNumeracionAutomaticaBoletas(Boolean numeracionAutomaticaBoletas) { this.numeracionAutomaticaBoletas = numeracionAutomaticaBoletas; }

    public Boolean getNumeracionAutomaticaNotas() { return numeracionAutomaticaNotas; }
    public void setNumeracionAutomaticaNotas(Boolean numeracionAutomaticaNotas) { this.numeracionAutomaticaNotas = numeracionAutomaticaNotas; }

    public Long getSiguienteNumeroFactura() { return siguienteNumeroFactura; }
    public void setSiguienteNumeroFactura(Long siguienteNumeroFactura) { this.siguienteNumeroFactura = siguienteNumeroFactura; }

    public Long getSiguienteNumeroBoleta() { return siguienteNumeroBoleta; }
    public void setSiguienteNumeroBoleta(Long siguienteNumeroBoleta) { this.siguienteNumeroBoleta = siguienteNumeroBoleta; }

    public Long getSiguienteNumeroNotaCredito() { return siguienteNumeroNotaCredito; }
    public void setSiguienteNumeroNotaCredito(Long siguienteNumeroNotaCredito) { this.siguienteNumeroNotaCredito = siguienteNumeroNotaCredito; }

    public Long getSiguienteNumeroOrdenCompra() { return siguienteNumeroOrdenCompra; }
    public void setSiguienteNumeroOrdenCompra(Long siguienteNumeroOrdenCompra) { this.siguienteNumeroOrdenCompra = siguienteNumeroOrdenCompra; }

    public String getMonedaPorDefecto() { return monedaPorDefecto; }
    public void setMonedaPorDefecto(String monedaPorDefecto) { this.monedaPorDefecto = monedaPorDefecto; }

    public String getSimboloMoneda() { return simboloMoneda; }
    public void setSimboloMoneda(String simboloMoneda) { this.simboloMoneda = simboloMoneda; }

    public Boolean getAlertarStockMinimo() { return alertarStockMinimo; }
    public void setAlertarStockMinimo(Boolean alertarStockMinimo) { this.alertarStockMinimo = alertarStockMinimo; }

    public Boolean getBloquearVentaSinStock() { return bloquearVentaSinStock; }
    public void setBloquearVentaSinStock(Boolean bloquearVentaSinStock) { this.bloquearVentaSinStock = bloquearVentaSinStock; }

    public BigDecimal getStockMinimoGlobal() { return stockMinimoGlobal; }
    public void setStockMinimoGlobal(BigDecimal stockMinimoGlobal) { this.stockMinimoGlobal = stockMinimoGlobal; }

    public Boolean getRedondearPrecios() { return redondearPrecios; }
    public void setRedondearPrecios(Boolean redondearPrecios) { this.redondearPrecios = redondearPrecios; }

    public Integer getDecimalesPrecio() { return decimalesPrecio; }
    public void setDecimalesPrecio(Integer decimalesPrecio) { this.decimalesPrecio = decimalesPrecio; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getRucEmpresa() { return rucEmpresa; }
    public void setRucEmpresa(String rucEmpresa) { this.rucEmpresa = rucEmpresa; }

    public String getDireccionEmpresa() { return direccionEmpresa; }
    public void setDireccionEmpresa(String direccionEmpresa) { this.direccionEmpresa = direccionEmpresa; }

    public String getTelefonoEmpresa() { return telefonoEmpresa; }
    public void setTelefonoEmpresa(String telefonoEmpresa) { this.telefonoEmpresa = telefonoEmpresa; }

    public String getEmailEmpresa() { return emailEmpresa; }
    public void setEmailEmpresa(String emailEmpresa) { this.emailEmpresa = emailEmpresa; }

    public String getLogoEmpresa() { return logoEmpresa; }
    public void setLogoEmpresa(String logoEmpresa) { this.logoEmpresa = logoEmpresa; }

    public Boolean getFacturacionElectronicaActiva() { return facturacionElectronicaActiva; }
    public void setFacturacionElectronicaActiva(Boolean facturacionElectronicaActiva) { this.facturacionElectronicaActiva = facturacionElectronicaActiva; }

    public String getAmbienteSunat() { return ambienteSunat; }
    public void setAmbienteSunat(String ambienteSunat) { this.ambienteSunat = ambienteSunat; }

    public String getCertificadoDigital() { return certificadoDigital; }
    public void setCertificadoDigital(String certificadoDigital) { this.certificadoDigital = certificadoDigital; }

    public String getClaveCertificado() { return claveCertificado; }
    public void setClaveCertificado(String claveCertificado) { this.claveCertificado = claveCertificado; }

    public Boolean getBackupAutomatico() { return backupAutomatico; }
    public void setBackupAutomatico(Boolean backupAutomatico) { this.backupAutomatico = backupAutomatico; }

    public Integer getDiasRetencionBackup() { return diasRetencionBackup; }
    public void setDiasRetencionBackup(Integer diasRetencionBackup) { this.diasRetencionBackup = diasRetencionBackup; }

    public String getRutaBackup() { return rutaBackup; }
    public void setRutaBackup(String rutaBackup) { this.rutaBackup = rutaBackup; }

    public Integer getSesionTimeoutMinutos() { return sesionTimeoutMinutos; }
    public void setSesionTimeoutMinutos(Integer sesionTimeoutMinutos) { this.sesionTimeoutMinutos = sesionTimeoutMinutos; }

    public Integer getIntentosLoginMax() { return intentosLoginMax; }
    public void setIntentosLoginMax(Integer intentosLoginMax) { this.intentosLoginMax = intentosLoginMax; }

    public Integer getBloqueoUsuarioMinutos() { return bloqueoUsuarioMinutos; }
    public void setBloqueoUsuarioMinutos(Integer bloqueoUsuarioMinutos) { this.bloqueoUsuarioMinutos = bloqueoUsuarioMinutos; }

    // Los métodos getActivo() y setActivo() se heredan de AbstractEntity

    public LocalDateTime getFechaUltimaModificacion() { return fechaUltimaModificacion; }
    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) { this.fechaUltimaModificacion = fechaUltimaModificacion; }

    public String getUsuarioModificacion() { return usuarioModificacion; }
    public void setUsuarioModificacion(String usuarioModificacion) { this.usuarioModificacion = usuarioModificacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}