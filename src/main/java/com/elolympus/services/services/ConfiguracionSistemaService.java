package com.elolympus.services.services;

import com.elolympus.data.Auxiliar.ConfiguracionSistema;
import com.elolympus.services.repository.ConfiguracionSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ConfiguracionSistemaService {

    private final ConfiguracionSistemaRepository repository;
    private ConfiguracionSistema configuracionCache;

    @Autowired
    public ConfiguracionSistemaService(ConfiguracionSistemaRepository repository) {
        this.repository = repository;
        inicializarConfiguracion();
    }

    /**
     * Obtiene la configuración activa del sistema.
     * Si no existe, crea una con valores por defecto.
     */
    public ConfiguracionSistema getConfiguracionActiva() {
        if (configuracionCache == null) {
            inicializarConfiguracion();
        }
        return configuracionCache;
    }

    /**
     * Inicializa la configuración del sistema con valores por defecto
     */
    private void inicializarConfiguracion() {
        Optional<ConfiguracionSistema> config = repository.findConfiguracionActiva();
        
        if (config.isPresent()) {
            configuracionCache = config.get();
        } else {
            // Crear configuración por defecto si no existe
            configuracionCache = crearConfiguracionPorDefecto();
            repository.save(configuracionCache);
        }
    }

    /**
     * Actualiza la configuración del sistema
     */
    public ConfiguracionSistema actualizarConfiguracion(ConfiguracionSistema configuracion, String usuario) {
        configuracion.setUsuarioModificacion(usuario);
        configuracion.setFechaUltimaModificacion(LocalDateTime.now());
        
        // Asegurar que solo hay una configuración activa
        if (configuracion.isActivo()) {
            desactivarOtrasConfiguraciones();
        }
        
        ConfiguracionSistema saved = repository.save(configuracion);
        configuracionCache = saved; // Actualizar cache
        return saved;
    }

    /**
     * Desactiva todas las configuraciones existentes
     */
    private void desactivarOtrasConfiguraciones() {
        repository.findAll().forEach(config -> {
            config.setActivo(false);
            repository.save(config);
        });
    }

    /**
     * Crea una configuración por defecto
     */
    private ConfiguracionSistema crearConfiguracionPorDefecto() {
        ConfiguracionSistema config = new ConfiguracionSistema();
        config.setUsuarioModificacion("SISTEMA");
        config.setObservaciones("Configuración inicial creada automáticamente");
        return config;
    }

    // =============== MÉTODOS DE UTILIDAD PARA ACCESO RÁPIDO ===============

    /**
     * Obtiene el porcentaje de IGV configurado
     */
    public BigDecimal getIgvPorcentaje() {
        return getConfiguracionActiva().getIgvPorcentaje();
    }

    /**
     * Obtiene el valor decimal del IGV (ej: 0.18 para 18%)
     */
    public BigDecimal getIgvDecimal() {
        return getIgvPorcentaje().divide(BigDecimal.valueOf(100));
    }

    /**
     * Calcula el IGV de un monto
     */
    public BigDecimal calcularIgv(BigDecimal monto) {
        return monto.multiply(getIgvDecimal());
    }

    /**
     * Calcula el total con IGV incluido
     */
    public BigDecimal calcularTotal(BigDecimal subtotal) {
        return subtotal.add(calcularIgv(subtotal));
    }

    /**
     * Obtiene el próximo número de factura y lo incrementa
     */
    @Transactional
    public String obtenerYIncrementarNumeroFactura() {
        ConfiguracionSistema config = getConfiguracionActiva();
        String numero = config.getProximoNumeroFactura();
        config.incrementarFactura();
        repository.save(config);
        return numero;
    }

    /**
     * Obtiene el próximo número de boleta y lo incrementa
     */
    @Transactional
    public String obtenerYIncrementarNumeroBoleta() {
        ConfiguracionSistema config = getConfiguracionActiva();
        String numero = config.getProximoNumeroBoleta();
        config.incrementarBoleta();
        repository.save(config);
        return numero;
    }

    /**
     * Obtiene el próximo número de nota de crédito y lo incrementa
     */
    @Transactional
    public String obtenerYIncrementarNumeroNotaCredito() {
        ConfiguracionSistema config = getConfiguracionActiva();
        String numero = config.getProximoNumeroNotaCredito();
        config.incrementarNotaCredito();
        repository.save(config);
        return numero;
    }

    /**
     * Obtiene el próximo número de orden de compra y lo incrementa
     */
    @Transactional
    public String obtenerYIncrementarNumeroOrdenCompra() {
        ConfiguracionSistema config = getConfiguracionActiva();
        String numero = config.getProximoNumeroOrdenCompra();
        config.incrementarOrdenCompra();
        repository.save(config);
        return numero;
    }

    /**
     * Verifica si la numeración automática está habilitada para facturas
     */
    public boolean isNumeracionAutomaticaFacturas() {
        return getConfiguracionActiva().getNumeracionAutomaticaFacturas();
    }

    /**
     * Verifica si la numeración automática está habilitada para boletas
     */
    public boolean isNumeracionAutomaticaBoletas() {
        return getConfiguracionActiva().getNumeracionAutomaticaBoletas();
    }

    /**
     * Verifica si debe bloquear ventas sin stock
     */
    public boolean isBloquearVentaSinStock() {
        return getConfiguracionActiva().getBloquearVentaSinStock();
    }

    /**
     * Verifica si debe alertar sobre stock mínimo
     */
    public boolean isAlertarStockMinimo() {
        return getConfiguracionActiva().getAlertarStockMinimo();
    }

    /**
     * Obtiene el stock mínimo global configurado
     */
    public BigDecimal getStockMinimoGlobal() {
        return getConfiguracionActiva().getStockMinimoGlobal();
    }

    /**
     * Obtiene la moneda por defecto del sistema
     */
    public String getMonedaPorDefecto() {
        return getConfiguracionActiva().getMonedaPorDefecto();
    }

    /**
     * Obtiene el símbolo de moneda configurado
     */
    public String getSimboloMoneda() {
        return getConfiguracionActiva().getSimboloMoneda();
    }

    /**
     * Verifica si debe redondear precios
     */
    public boolean isRedondearPrecios() {
        return getConfiguracionActiva().getRedondearPrecios();
    }

    /**
     * Obtiene la cantidad de decimales para precios
     */
    public int getDecimalesPrecio() {
        return getConfiguracionActiva().getDecimalesPrecio();
    }

    /**
     * Formatea un precio según la configuración del sistema
     */
    public BigDecimal formatearPrecio(BigDecimal precio) {
        if (precio == null) return BigDecimal.ZERO;
        
        ConfiguracionSistema config = getConfiguracionActiva();
        int decimales = config.getDecimalesPrecio();
        
        if (config.getRedondearPrecios()) {
            return precio.setScale(decimales, BigDecimal.ROUND_HALF_UP);
        } else {
            return precio.setScale(decimales, BigDecimal.ROUND_DOWN);
        }
    }

    /**
     * Obtiene información completa de la empresa
     */
    public String getNombreEmpresa() {
        return getConfiguracionActiva().getNombreEmpresa();
    }

    public String getRucEmpresa() {
        return getConfiguracionActiva().getRucEmpresa();
    }

    public String getDireccionEmpresa() {
        return getConfiguracionActiva().getDireccionEmpresa();
    }

    /**
     * Verifica si la facturación electrónica está activa
     */
    public boolean isFacturacionElectronicaActiva() {
        return getConfiguracionActiva().getFacturacionElectronicaActiva();
    }

    /**
     * Invalida el cache para forzar recarga desde BD
     */
    public void invalidarCache() {
        configuracionCache = null;
    }

    /**
     * Exporta la configuración actual (para backup)
     */
    public ConfiguracionSistema exportarConfiguracion() {
        return getConfiguracionActiva();
    }

    /**
     * Importa una configuración (para restaurar backup)
     */
    public ConfiguracionSistema importarConfiguracion(ConfiguracionSistema configuracion, String usuario) {
        configuracion.setId(null); // Asegurar que se cree un nuevo registro
        configuracion.setActivo(true);
        return actualizarConfiguracion(configuracion, usuario);
    }
}