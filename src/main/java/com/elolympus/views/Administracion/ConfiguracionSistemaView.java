package com.elolympus.views.Administracion;

import com.elolympus.data.Auxiliar.ConfiguracionSistema;
import com.elolympus.services.services.ConfiguracionSistemaService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@PageTitle("Configuración del Sistema")
@Route(value = "configuracion-sistema", layout = MainLayout.class)
@PermitAll
public class ConfiguracionSistemaView extends Div {

    private final ConfiguracionSistemaService configuracionService;
    private ConfiguracionSistema configuracion;
    private BeanValidationBinder<ConfiguracionSistema> binder;

    // Tabs principales
    private Tabs tabs;
    private Div contentContainer;

    // Formularios por sección
    private FormLayout formImpuestos;
    private FormLayout formDocumentos;
    private FormLayout formNumeracion;
    private FormLayout formEmpresa;
    private FormLayout formStock;
    private FormLayout formPrecios;
    private FormLayout formFacturacionElectronica;
    private FormLayout formSeguridad;
    private FormLayout formBackup;

    // Campos de Impuestos
    private final BigDecimalField igvPorcentaje = new BigDecimalField("IGV (%)");
    private final Checkbox aplicaIgvPorDefecto = new Checkbox("Aplicar IGV por defecto");

    // Campos de Series de Documentos
    private final TextField serieFactura = new TextField("Serie Factura");
    private final TextField serieBoleta = new TextField("Serie Boleta");
    private final TextField serieNotaCredito = new TextField("Serie Nota de Crédito");
    private final TextField serieOrdenCompra = new TextField("Serie Orden de Compra");

    // Campos de Numeración
    private final Checkbox numeracionAutomaticaFacturas = new Checkbox("Numeración automática de facturas");
    private final Checkbox numeracionAutomaticaBoletas = new Checkbox("Numeración automática de boletas");
    private final Checkbox numeracionAutomaticaNotas = new Checkbox("Numeración automática de notas de crédito");
    private final IntegerField siguienteNumeroFactura = new IntegerField("Siguiente número de factura");
    private final IntegerField siguienteNumeroBoleta = new IntegerField("Siguiente número de boleta");
    private final IntegerField siguienteNumeroNotaCredito = new IntegerField("Siguiente número de nota de crédito");
    private final IntegerField siguienteNumeroOrdenCompra = new IntegerField("Siguiente número de orden de compra");

    // Campos de Empresa
    private final TextField nombreEmpresa = new TextField("Nombre de la Empresa");
    private final TextField rucEmpresa = new TextField("RUC");
    private final TextArea direccionEmpresa = new TextArea("Dirección");
    private final TextField telefonoEmpresa = new TextField("Teléfono");
    private final EmailField emailEmpresa = new EmailField("Email");
    private final TextField logoEmpresa = new TextField("Logo (Ruta)");
    private Upload uploadLogo;

    // Campos de Stock
    private final Checkbox alertarStockMinimo = new Checkbox("Alertar stock mínimo");
    private final Checkbox bloquearVentaSinStock = new Checkbox("Bloquear ventas sin stock");
    private final BigDecimalField stockMinimoGlobal = new BigDecimalField("Stock mínimo global");

    // Campos de Precios
    private final Checkbox redondearPrecios = new Checkbox("Redondear precios");
    private final IntegerField decimalesPrecio = new IntegerField("Decimales en precios");
    private final ComboBox<String> monedaPorDefecto = new ComboBox<>("Moneda por defecto");
    private final TextField simboloMoneda = new TextField("Símbolo de moneda");

    // Campos de Facturación Electrónica
    private final Checkbox facturacionElectronicaActiva = new Checkbox("Facturación electrónica activa");
    private final ComboBox<String> ambienteSunat = new ComboBox<>("Ambiente SUNAT");
    private final TextField certificadoDigital = new TextField("Certificado digital");
    private final TextField claveCertificado = new TextField("Clave del certificado");

    // Campos de Seguridad
    private final IntegerField sesionTimeoutMinutos = new IntegerField("Timeout de sesión (minutos)");
    private final IntegerField intentosLoginMax = new IntegerField("Máximo intentos de login");
    private final IntegerField bloqueoUsuarioMinutos = new IntegerField("Bloqueo de usuario (minutos)");

    // Campos de Backup
    private final Checkbox backupAutomatico = new Checkbox("Backup automático");
    private final IntegerField diasRetencionBackup = new IntegerField("Días de retención de backup");
    private final TextField rutaBackup = new TextField("Ruta de backup");

    // Campos comunes
    private final TextArea observaciones = new TextArea("Observaciones");

    // Botones
    private final Button guardar = new Button("Guardar Configuración");
    private final Button restaurarDefecto = new Button("Restaurar Valores por Defecto");
    private final Button exportar = new Button("Exportar Configuración");
    private final Button importar = new Button("Importar Configuración");

    @Autowired
    public ConfiguracionSistemaView(ConfiguracionSistemaService configuracionService) {
        this.configuracionService = configuracionService;
        this.binder = new BeanValidationBinder<>(ConfiguracionSistema.class);

        addClassName("configuracion-sistema-view");
        setSizeFull();

        setupUI();
        loadConfiguration();
        bindFields();
        setupEventListeners();
    }

    private void setupUI() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);

        // Título
        H2 title = new H2("⚙️ Configuración del Sistema");
        title.addClassName("view-title");

        // Crear tabs
        createTabs();

        // Container de contenido
        contentContainer = new Div();
        contentContainer.setSizeFull();
        contentContainer.addClassName("tab-content");

        // Mostrar la primera pestaña por defecto
        showTabContent(0);

        // Botones de acción
        HorizontalLayout buttonLayout = createButtonLayout();

        mainLayout.add(title, tabs, contentContainer, buttonLayout);
        add(mainLayout);
    }

    private void createTabs() {
        Tab tabImpuestos = new Tab("💰 Impuestos");
        Tab tabDocumentos = new Tab("📄 Documentos");
        Tab tabNumeracion = new Tab("🔢 Numeración");
        Tab tabEmpresa = new Tab("🏢 Empresa");
        Tab tabStock = new Tab("📦 Stock");
        Tab tabPrecios = new Tab("💲 Precios");
        Tab tabFacturacion = new Tab("📧 Fact. Electrónica");
        Tab tabSeguridad = new Tab("🔒 Seguridad");
        Tab tabBackup = new Tab("💾 Backup");

        tabs = new Tabs(tabImpuestos, tabDocumentos, tabNumeracion, tabEmpresa,
                        tabStock, tabPrecios, tabFacturacion, tabSeguridad, tabBackup);
        tabs.setSelectedIndex(0);
        tabs.addSelectedChangeListener(e -> showTabContent(tabs.getSelectedIndex()));
    }

    private void showTabContent(int selectedIndex) {
        contentContainer.removeAll();

        switch (selectedIndex) {
            case 0: contentContainer.add(createImpuestosTab()); break;
            case 1: contentContainer.add(createDocumentosTab()); break;
            case 2: contentContainer.add(createNumeracionTab()); break;
            case 3: contentContainer.add(createEmpresaTab()); break;
            case 4: contentContainer.add(createStockTab()); break;
            case 5: contentContainer.add(createPreciosTab()); break;
            case 6: contentContainer.add(createFacturacionTab()); break;
            case 7: contentContainer.add(createSeguridadTab()); break;
            case 8: contentContainer.add(createBackupTab()); break;
        }
    }

    private Component createImpuestosTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("💰 Configuración de Impuestos");
        
        formImpuestos = new FormLayout();
        formImpuestos.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        // Configurar campos
        igvPorcentaje.setPlaceholder("18.00");
        
        aplicaIgvPorDefecto.setLabel("Aplicar IGV por defecto en las ventas");

        formImpuestos.add(igvPorcentaje, aplicaIgvPorDefecto);

        layout.add(title, formImpuestos);
        return layout;
    }

    private Component createDocumentosTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("📄 Series de Documentos");
        
        formDocumentos = new FormLayout();
        formDocumentos.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        serieFactura.setHelperText("Serie para facturas (ej: F001)");
        serieBoleta.setHelperText("Serie para boletas (ej: B001)");
        serieNotaCredito.setHelperText("Serie para notas de crédito (ej: NC01)");
        serieOrdenCompra.setHelperText("Serie para órdenes de compra (ej: OC01)");

        formDocumentos.add(serieFactura, serieBoleta, serieNotaCredito, serieOrdenCompra);

        layout.add(title, formDocumentos);
        return layout;
    }

    private Component createNumeracionTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("🔢 Configuración de Numeración");
        
        formNumeracion = new FormLayout();
        formNumeracion.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        numeracionAutomaticaFacturas.setLabel("Generar números de factura automáticamente");
        numeracionAutomaticaBoletas.setLabel("Generar números de boleta automáticamente");
        numeracionAutomaticaNotas.setLabel("Generar números de nota de crédito automáticamente");

        siguienteNumeroFactura.setValue(1);
        siguienteNumeroBoleta.setValue(1);
        siguienteNumeroNotaCredito.setValue(1);
        siguienteNumeroOrdenCompra.setValue(1);

        formNumeracion.add(
            numeracionAutomaticaFacturas, siguienteNumeroFactura,
            numeracionAutomaticaBoletas, siguienteNumeroBoleta,
            numeracionAutomaticaNotas, siguienteNumeroNotaCredito,
            siguienteNumeroOrdenCompra
        );
        formNumeracion.setColspan(siguienteNumeroOrdenCompra, 2);

        layout.add(title, formNumeracion);
        return layout;
    }

    private Component createEmpresaTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("🏢 Información de la Empresa");
        
        formEmpresa = new FormLayout();
        formEmpresa.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        rucEmpresa.setPlaceholder("20123456789");
        rucEmpresa.setMaxLength(11);
        
        direccionEmpresa.setMaxLength(200);
        direccionEmpresa.setHeight("100px");
        
        telefonoEmpresa.setPlaceholder("+51 123 456 789");
        emailEmpresa.setPlaceholder("contacto@empresa.com");

        // Upload para logo
        MemoryBuffer buffer = new MemoryBuffer();
        uploadLogo = new Upload(buffer);
        uploadLogo.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadLogo.setMaxFileSize(1024 * 1024); // 1MB
        uploadLogo.setDropLabel(new com.vaadin.flow.component.html.Span("Arrastra el logo aquí"));

        formEmpresa.add(
            nombreEmpresa, rucEmpresa,
            direccionEmpresa, telefonoEmpresa,
            emailEmpresa, logoEmpresa
        );
        formEmpresa.setColspan(direccionEmpresa, 2);
        formEmpresa.setColspan(logoEmpresa, 2);

        layout.add(title, formEmpresa, uploadLogo);
        return layout;
    }

    private Component createStockTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("📦 Configuración de Stock");
        
        formStock = new FormLayout();
        formStock.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        alertarStockMinimo.setLabel("Mostrar alertas cuando el stock esté bajo");
        bloquearVentaSinStock.setLabel("Impedir ventas de productos sin stock");
        
        stockMinimoGlobal.setValue(new BigDecimal("5.00"));
        stockMinimoGlobal.setPlaceholder("5.00");

        formStock.add(alertarStockMinimo, bloquearVentaSinStock, stockMinimoGlobal);

        layout.add(title, formStock);
        return layout;
    }

    private Component createPreciosTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("💲 Configuración de Precios y Moneda");
        
        formPrecios = new FormLayout();
        formPrecios.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        // Configurar campos
        monedaPorDefecto.setItems("PEN", "USD", "EUR");
        monedaPorDefecto.setPlaceholder("Seleccione moneda");
        
        simboloMoneda.setPlaceholder("S/.");
        
        redondearPrecios.setLabel("Redondear precios al número de decimales especificado");
        
        decimalesPrecio.setValue(2);
        decimalesPrecio.setPlaceholder("2");

        formPrecios.add(monedaPorDefecto, simboloMoneda, redondearPrecios, decimalesPrecio);

        layout.add(title, formPrecios);
        return layout;
    }

    private Component createFacturacionTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("📧 Facturación Electrónica");
        
        formFacturacionElectronica = new FormLayout();
        formFacturacionElectronica.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        facturacionElectronicaActiva.setLabel("Activar facturación electrónica SUNAT");
        
        ambienteSunat.setItems("PRUEBA", "PRODUCCION");
        ambienteSunat.setPlaceholder("Seleccione ambiente");
        
        certificadoDigital.setPlaceholder("Ruta al certificado (.pfx)");
        claveCertificado.setPlaceholder("Clave del certificado");

        formFacturacionElectronica.add(
            facturacionElectronicaActiva, ambienteSunat,
            certificadoDigital, claveCertificado
        );
        formFacturacionElectronica.setColspan(certificadoDigital, 2);
        formFacturacionElectronica.setColspan(claveCertificado, 2);

        layout.add(title, formFacturacionElectronica);
        return layout;
    }

    private Component createSeguridadTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("🔒 Configuración de Seguridad");
        
        formSeguridad = new FormLayout();
        formSeguridad.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        sesionTimeoutMinutos.setValue(60);
        sesionTimeoutMinutos.setPlaceholder("60");

        intentosLoginMax.setValue(5);
        intentosLoginMax.setPlaceholder("5");

        bloqueoUsuarioMinutos.setValue(15);
        bloqueoUsuarioMinutos.setPlaceholder("15");

        formSeguridad.add(sesionTimeoutMinutos, intentosLoginMax, bloqueoUsuarioMinutos);

        layout.add(title, formSeguridad);
        return layout;
    }

    private Component createBackupTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3("💾 Configuración de Backup");
        
        formBackup = new FormLayout();
        formBackup.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        backupAutomatico.setLabel("Realizar backup automático del sistema");
        
        diasRetencionBackup.setValue(30);
        diasRetencionBackup.setPlaceholder("30");
        
        rutaBackup.setPlaceholder("/backup/sistema");

        formBackup.add(backupAutomatico, diasRetencionBackup, rutaBackup);
        formBackup.setColspan(rutaBackup, 2);

        // Agregar observaciones aquí
        observaciones.setMaxLength(1000);
        observaciones.setHeight("100px");
        observaciones.setHelperText("Observaciones generales sobre la configuración");
        formBackup.add(observaciones);
        formBackup.setColspan(observaciones, 2);

        layout.add(title, formBackup);
        return layout;
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(true);
        
        // Configurar estilos de botones
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        restaurarDefecto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        exportar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        importar.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        
        buttonLayout.add(guardar, restaurarDefecto, exportar, importar);
        return buttonLayout;
    }

    private void loadConfiguration() {
        try {
            configuracion = configuracionService.getConfiguracionActiva();
            if (configuracion == null) {
                // Crear configuración por defecto si no existe
                configuracion = new ConfiguracionSistema();
                configuracion.setUsuarioModificacion("ADMIN");
                configuracion = configuracionService.actualizarConfiguracion(configuracion, "ADMIN");
            }
            binder.readBean(configuracion);
        } catch (Exception e) {
            Notification.show("Error al cargar la configuración: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            // Crear configuración de emergencia
            configuracion = new ConfiguracionSistema();
            binder.readBean(configuracion);
        }
    }

    private void bindFields() {
        // Bind Impuestos
        binder.forField(igvPorcentaje).bind(ConfiguracionSistema::getIgvPorcentaje, ConfiguracionSistema::setIgvPorcentaje);
        binder.forField(aplicaIgvPorDefecto).bind(ConfiguracionSistema::getAplicaIgvPorDefecto, ConfiguracionSistema::setAplicaIgvPorDefecto);
        
        // Bind Series de Documentos
        binder.forField(serieFactura).bind(ConfiguracionSistema::getSerieFactura, ConfiguracionSistema::setSerieFactura);
        binder.forField(serieBoleta).bind(ConfiguracionSistema::getSerieBoleta, ConfiguracionSistema::setSerieBoleta);
        binder.forField(serieNotaCredito).bind(ConfiguracionSistema::getSerieNotaCredito, ConfiguracionSistema::setSerieNotaCredito);
        binder.forField(serieOrdenCompra).bind(ConfiguracionSistema::getSerieOrdenCompra, ConfiguracionSistema::setSerieOrdenCompra);
        
        // Bind Numeración
        binder.forField(numeracionAutomaticaFacturas).bind(ConfiguracionSistema::getNumeracionAutomaticaFacturas, ConfiguracionSistema::setNumeracionAutomaticaFacturas);
        binder.forField(numeracionAutomaticaBoletas).bind(ConfiguracionSistema::getNumeracionAutomaticaBoletas, ConfiguracionSistema::setNumeracionAutomaticaBoletas);
        binder.forField(numeracionAutomaticaNotas).bind(ConfiguracionSistema::getNumeracionAutomaticaNotas, ConfiguracionSistema::setNumeracionAutomaticaNotas);
        
        binder.forField(siguienteNumeroFactura)
            .withConverter(
                value -> value != null ? value.longValue() : 1L,
                value -> value != null ? value.intValue() : 1,
                "Número inválido"
            )
            .bind(ConfiguracionSistema::getSiguienteNumeroFactura, ConfiguracionSistema::setSiguienteNumeroFactura);
        binder.forField(siguienteNumeroBoleta)
            .withConverter(
                value -> value != null ? value.longValue() : 1L,
                value -> value != null ? value.intValue() : 1,
                "Número inválido"
            )
            .bind(ConfiguracionSistema::getSiguienteNumeroBoleta, ConfiguracionSistema::setSiguienteNumeroBoleta);
        binder.forField(siguienteNumeroNotaCredito)
            .withConverter(
                value -> value != null ? value.longValue() : 1L,
                value -> value != null ? value.intValue() : 1,
                "Número inválido"
            )
            .bind(ConfiguracionSistema::getSiguienteNumeroNotaCredito, ConfiguracionSistema::setSiguienteNumeroNotaCredito);
        binder.forField(siguienteNumeroOrdenCompra)
            .withConverter(
                value -> value != null ? value.longValue() : 1L,
                value -> value != null ? value.intValue() : 1,
                "Número inválido"
            )
            .bind(ConfiguracionSistema::getSiguienteNumeroOrdenCompra, ConfiguracionSistema::setSiguienteNumeroOrdenCompra);
        
        // Bind Empresa
        binder.forField(nombreEmpresa).bind(ConfiguracionSistema::getNombreEmpresa, ConfiguracionSistema::setNombreEmpresa);
        binder.forField(rucEmpresa).bind(ConfiguracionSistema::getRucEmpresa, ConfiguracionSistema::setRucEmpresa);
        binder.forField(direccionEmpresa).bind(ConfiguracionSistema::getDireccionEmpresa, ConfiguracionSistema::setDireccionEmpresa);
        binder.forField(telefonoEmpresa).bind(ConfiguracionSistema::getTelefonoEmpresa, ConfiguracionSistema::setTelefonoEmpresa);
        binder.forField(emailEmpresa).bind(ConfiguracionSistema::getEmailEmpresa, ConfiguracionSistema::setEmailEmpresa);
        binder.forField(logoEmpresa).bind(ConfiguracionSistema::getLogoEmpresa, ConfiguracionSistema::setLogoEmpresa);
        
        // Bind Stock
        binder.forField(alertarStockMinimo).bind(ConfiguracionSistema::getAlertarStockMinimo, ConfiguracionSistema::setAlertarStockMinimo);
        binder.forField(bloquearVentaSinStock).bind(ConfiguracionSistema::getBloquearVentaSinStock, ConfiguracionSistema::setBloquearVentaSinStock);
        binder.forField(stockMinimoGlobal).bind(ConfiguracionSistema::getStockMinimoGlobal, ConfiguracionSistema::setStockMinimoGlobal);
        
        // Bind Precios
        binder.forField(redondearPrecios).bind(ConfiguracionSistema::getRedondearPrecios, ConfiguracionSistema::setRedondearPrecios);
        binder.forField(decimalesPrecio).bind(ConfiguracionSistema::getDecimalesPrecio, ConfiguracionSistema::setDecimalesPrecio);
        binder.forField(monedaPorDefecto).bind(ConfiguracionSistema::getMonedaPorDefecto, ConfiguracionSistema::setMonedaPorDefecto);
        binder.forField(simboloMoneda).bind(ConfiguracionSistema::getSimboloMoneda, ConfiguracionSistema::setSimboloMoneda);
        
        // Bind Facturación Electrónica
        binder.forField(facturacionElectronicaActiva).bind(ConfiguracionSistema::getFacturacionElectronicaActiva, ConfiguracionSistema::setFacturacionElectronicaActiva);
        binder.forField(ambienteSunat).bind(ConfiguracionSistema::getAmbienteSunat, ConfiguracionSistema::setAmbienteSunat);
        binder.forField(certificadoDigital).bind(ConfiguracionSistema::getCertificadoDigital, ConfiguracionSistema::setCertificadoDigital);
        binder.forField(claveCertificado).bind(ConfiguracionSistema::getClaveCertificado, ConfiguracionSistema::setClaveCertificado);
        
        // Bind Seguridad
        binder.forField(sesionTimeoutMinutos).bind(ConfiguracionSistema::getSesionTimeoutMinutos, ConfiguracionSistema::setSesionTimeoutMinutos);
        binder.forField(intentosLoginMax).bind(ConfiguracionSistema::getIntentosLoginMax, ConfiguracionSistema::setIntentosLoginMax);
        binder.forField(bloqueoUsuarioMinutos).bind(ConfiguracionSistema::getBloqueoUsuarioMinutos, ConfiguracionSistema::setBloqueoUsuarioMinutos);
        
        // Bind Backup
        binder.forField(backupAutomatico).bind(ConfiguracionSistema::getBackupAutomatico, ConfiguracionSistema::setBackupAutomatico);
        binder.forField(diasRetencionBackup).bind(ConfiguracionSistema::getDiasRetencionBackup, ConfiguracionSistema::setDiasRetencionBackup);
        binder.forField(rutaBackup).bind(ConfiguracionSistema::getRutaBackup, ConfiguracionSistema::setRutaBackup);
        
        // Bind Observaciones
        binder.forField(observaciones).bind(ConfiguracionSistema::getObservaciones, ConfiguracionSistema::setObservaciones);
    }

    private void setupEventListeners() {
        guardar.addClickListener(e -> guardarConfiguracion());
        restaurarDefecto.addClickListener(e -> restaurarValoresPorDefecto());
        exportar.addClickListener(e -> exportarConfiguracion());
        importar.addClickListener(e -> importarConfiguracion());
        
        // Listeners adicionales
        setupConditionalFields();
    }

    private void setupConditionalFields() {
        // Habilitar/deshabilitar campos según otros campos
        facturacionElectronicaActiva.addValueChangeListener(e -> {
            boolean enabled = e.getValue();
            ambienteSunat.setEnabled(enabled);
            certificadoDigital.setEnabled(enabled);
            claveCertificado.setEnabled(enabled);
        });
        
        numeracionAutomaticaFacturas.addValueChangeListener(e -> 
            siguienteNumeroFactura.setEnabled(e.getValue()));
        
        numeracionAutomaticaBoletas.addValueChangeListener(e -> 
            siguienteNumeroBoleta.setEnabled(e.getValue()));
        
        numeracionAutomaticaNotas.addValueChangeListener(e -> 
            siguienteNumeroNotaCredito.setEnabled(e.getValue()));
        
        backupAutomatico.addValueChangeListener(e -> {
            boolean enabled = e.getValue();
            diasRetencionBackup.setEnabled(enabled);
            rutaBackup.setEnabled(enabled);
        });
    }

    private void guardarConfiguracion() {
        try {
            // Verificar que tenemos una configuración
            if (configuracion == null) {
                configuracion = new ConfiguracionSistema();
            }
            
            // Intentar escribir los datos del formulario al bean
            if (binder.writeBeanIfValid(configuracion)) {
                configuracion = configuracionService.actualizarConfiguracion(configuracion, "ADMIN");
                Notification.show("✅ Configuración guardada correctamente")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                // Recargar la configuración
                loadConfiguration();
            } else {
                // Mostrar errores específicos de validación
                String errores = binder.validate().getValidationErrors().stream()
                    .map(error -> error.getErrorMessage())
                    .reduce("", (a, b) -> a + "\n" + b);
                
                Notification.show("❌ Error en los datos:\n" + errores)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            Notification.show("❌ Error al guardar: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void restaurarValoresPorDefecto() {
        configuracion = new ConfiguracionSistema();
        binder.readBean(configuracion);
        Notification.show("⚠️ Valores restaurados por defecto. No olvide guardar los cambios")
            .addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    private void exportarConfiguracion() {
        try {
            ConfiguracionSistema config = configuracionService.exportarConfiguracion();
            // Aquí se podría implementar la descarga del archivo JSON
            Notification.show("✅ Configuración exportada correctamente")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("❌ Error al exportar: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void importarConfiguracion() {
        // Aquí se podría implementar la carga de archivo JSON
        Notification.show("⚠️ Funcionalidad de importación en desarrollo")
            .addThemeVariants(NotificationVariant.LUMO_WARNING);
    }
}