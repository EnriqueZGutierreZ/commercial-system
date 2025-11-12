package com.elolympus.views.Ventas;

import com.elolympus.data.Ventas.CuentaPorCobrar;
import com.elolympus.data.Ventas.PagoCliente;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.services.CuentaPorCobrarService;
import com.elolympus.services.services.PagoClienteService;
import com.elolympus.services.services.PersonaService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@PageTitle("Cuentas por Cobrar")
@Route(value = "cuentas-por-cobrar", layout = MainLayout.class)
@PermitAll
public class CuentasPorCobrarView extends Div {

    private final CuentaPorCobrarService cuentaService;
    private final PagoClienteService pagoService;
    private final PersonaService personaService;

    // Componentes principales
    private Tabs mainTabs;
    private Div contentContainer;
    
    // Dashboard
    private Div dashboardContainer;
    
    // Grids
    private Grid<CuentaPorCobrar> gridCuentas;
    private Grid<PagoCliente> gridPagos;
    
    // Filtros para cuentas por cobrar
    private ComboBox<Persona> filtroCliente = new ComboBox<>("Cliente");
    private ComboBox<String> filtroEstado = new ComboBox<>("Estado");
    private ComboBox<String> filtroRangoVencimiento = new ComboBox<>("Rango Vencimiento");
    private ComboBox<String> filtroTipoDocumento = new ComboBox<>("Tipo Documento");
    private DatePicker filtroFechaDesde = new DatePicker("Desde");
    private DatePicker filtroFechaHasta = new DatePicker("Hasta");
    private Button btnLimpiarFiltros = new Button("Limpiar");
    private Button btnBuscar = new Button("Buscar");
    
    // Filtros para pagos
    private ComboBox<Persona> filtroClientePago = new ComboBox<>("Cliente");
    private ComboBox<String> filtroEstadoPago = new ComboBox<>("Estado");
    private ComboBox<String> filtroFormaPago = new ComboBox<>("Forma de Pago");
    private DatePicker filtroFechaPagoDesde = new DatePicker("Desde");
    private DatePicker filtroFechaPagoHasta = new DatePicker("Hasta");
    
    // Botones de acción
    private Button btnRegistrarPago = new Button("Registrar Pago");
    private Button btnRegistrarContacto = new Button("Registrar Contacto");
    private Button btnVerificarPago = new Button("Verificar Pago");
    private Button btnRechazarPago = new Button("Rechazar Pago");
    private Button btnExportar = new Button("Exportar");

    @Autowired
    public CuentasPorCobrarView(CuentaPorCobrarService cuentaService, PagoClienteService pagoService, PersonaService personaService) {
        this.cuentaService = cuentaService;
        this.pagoService = pagoService;
        this.personaService = personaService;

        addClassName("cuentas-por-cobrar-view");
        setSizeFull();
        
        setupUI();
        setupEventListeners();
        loadData();
    }

    private void setupUI() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);

        // Título principal
        H2 title = new H2("💰 Sistema de Cuentas por Cobrar");
        title.addClassName("view-title");

        // Crear tabs principales
        createMainTabs();

        // Container de contenido
        contentContainer = new Div();
        contentContainer.setSizeFull();
        contentContainer.addClassName("tab-content");

        // Mostrar dashboard por defecto
        showTabContent(0);

        mainLayout.add(title, mainTabs, contentContainer);
        add(mainLayout);
    }

    private void createMainTabs() {
        Tab dashboardTab = new Tab("📊 Dashboard");
        Tab cuentasTab = new Tab("📋 Cuentas por Cobrar");
        Tab pagosTab = new Tab("💳 Pagos");
        Tab reportesTab = new Tab("📈 Reportes");

        mainTabs = new Tabs(dashboardTab, cuentasTab, pagosTab, reportesTab);
        mainTabs.setSelectedIndex(0);
        mainTabs.addSelectedChangeListener(e -> showTabContent(mainTabs.getSelectedIndex()));
    }

    private void showTabContent(int selectedIndex) {
        contentContainer.removeAll();

        switch (selectedIndex) {
            case 0: contentContainer.add(createDashboardTab()); break;
            case 1: contentContainer.add(createCuentasTab()); break;
            case 2: contentContainer.add(createPagosTab()); break;
            case 3: contentContainer.add(createReportesTab()); break;
        }
    }

    // =============== TAB DASHBOARD ===============

    private Component createDashboardTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        // Obtener datos del dashboard
        Map<String, Object> dashboardData = cuentaService.getDashboardCuentasPorCobrar();
        Map<String, Object> pagosDashboard = pagoService.getDashboardPagos();

        // Tarjetas de KPIs principales
        HorizontalLayout kpisLayout = createKPIsLayout(dashboardData, pagosDashboard);

        // Alertas
        Component alertasComponent = createAlertasComponent();

        // Gráficos y estadísticas
        Component estadisticasComponent = createEstadisticasComponent(dashboardData);

        layout.add(kpisLayout, alertasComponent, estadisticasComponent);
        return layout;
    }

    private HorizontalLayout createKPIsLayout(Map<String, Object> cuentasData, Map<String, Object> pagosData) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);

        // Total pendiente
        BigDecimal totalPendiente = (BigDecimal) cuentasData.get("totalPendiente");
        Div kpiPendiente = createKPICard("Total Pendiente", formatMoney(totalPendiente), "🔴", "#ff5722");

        // Total vencido
        BigDecimal totalVencido = (BigDecimal) cuentasData.get("totalVencido");
        Div kpiVencido = createKPICard("Total Vencido", formatMoney(totalVencido), "⚠️", "#ff9800");

        // Recaudación del mes
        BigDecimal recaudacionMes = (BigDecimal) pagosData.get("recaudacionMes");
        Div kpiRecaudacion = createKPICard("Recaudación del Mes", formatMoney(recaudacionMes), "💰", "#4caf50");

        // Cuentas pendientes
        Integer cuentasPendientes = (Integer) cuentasData.get("cuentasPendientes");
        Div kpiCuentas = createKPICard("Cuentas Pendientes", cuentasPendientes.toString(), "📋", "#2196f3");

        layout.add(kpiPendiente, kpiVencido, kpiRecaudacion, kpiCuentas);
        return layout;
    }

    private Div createKPICard(String title, String value, String icon, String color) {
        Div card = new Div();
        card.addClassName("kpi-card");
        card.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "20px")
            .set("text-align", "center")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("border-left", "4px solid " + color);

        Span iconSpan = new Span(icon);
        iconSpan.getStyle().set("font-size", "2em").set("display", "block");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "0.9em").set("color", "#666").set("display", "block");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "1.5em").set("font-weight", "bold").set("color", color).set("display", "block");

        card.add(iconSpan, titleSpan, valueSpan);
        return card;
    }

    private Component createAlertasComponent() {
        VerticalLayout alertContainer = new VerticalLayout();
        alertContainer.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "16px");

        H3 alertTitle = new H3("🚨 Alertas y Notificaciones");
        alertTitle.getStyle().set("margin-top", "0");

        // Obtener alertas de cuentas por cobrar
        List<String> alertasCuentas = cuentaService.getAlertasCuentasPorCobrar();
        List<String> alertasPagos = pagoService.getAlertasPagos();

        alertContainer.add(alertTitle);

        // Mostrar alertas
        for (String alerta : alertasCuentas) {
            Span alertSpan = new Span(alerta);
            alertSpan.getStyle().set("display", "block").set("margin-bottom", "8px").set("color", "#ff5722");
            alertContainer.add(alertSpan);
        }

        for (String alerta : alertasPagos) {
            Span alertSpan = new Span(alerta);
            alertSpan.getStyle().set("display", "block").set("margin-bottom", "8px").set("color", "#ff9800");
            alertContainer.add(alertSpan);
        }

        if (alertasCuentas.isEmpty() && alertasPagos.isEmpty()) {
            Span noAlertas = new Span("✅ No hay alertas pendientes");
            noAlertas.getStyle().set("color", "#4caf50").set("font-style", "italic");
            alertContainer.add(noAlertas);
        }

        return alertContainer;
    }

    private Component createEstadisticasComponent(Map<String, Object> dashboardData) {
        VerticalLayout estadisticasContainer = new VerticalLayout();
        estadisticasContainer.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "16px");

        H3 estadisticasTitle = new H3("📊 Antigüedad de Saldos");
        estadisticasTitle.getStyle().set("margin-top", "0");

        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> montoPorRango = (Map<String, BigDecimal>) dashboardData.get("montoPorRango");
        @SuppressWarnings("unchecked")
        Map<String, Long> cantidadPorRango = (Map<String, Long>) dashboardData.get("cantidadPorRango");

        estadisticasContainer.add(estadisticasTitle);

        if (montoPorRango != null) {
            for (Map.Entry<String, BigDecimal> entry : montoPorRango.entrySet()) {
                String rango = entry.getKey();
                BigDecimal monto = entry.getValue();
                Long cantidad = cantidadPorRango.get(rango);

                HorizontalLayout rangoLayout = new HorizontalLayout();
                rangoLayout.setWidthFull();
                rangoLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

                Span rangoSpan = new Span(getRangoLabel(rango) + " (" + cantidad + " cuentas)");
                Span montoSpan = new Span(formatMoney(monto));
                montoSpan.getStyle().set("font-weight", "bold");

                rangoLayout.add(rangoSpan, montoSpan);
                estadisticasContainer.add(rangoLayout);
            }
        }

        return estadisticasContainer;
    }

    // =============== TAB CUENTAS POR COBRAR ===============

    private Component createCuentasTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(true);

        // Filtros
        Component filtrosComponent = createFiltrosCuentas();

        // Grid
        setupGridCuentas();

        // Botones de acción
        HorizontalLayout botonesLayout = createBotonesCuentas();

        // Cargar datos una vez que el grid está configurado
        refreshGridCuentas();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        
        VerticalLayout leftPanel = new VerticalLayout(filtrosComponent, gridCuentas, botonesLayout);
        leftPanel.setSizeFull();
        leftPanel.setPadding(false);
        
        splitLayout.addToPrimary(leftPanel);
        splitLayout.setSplitterPosition(100); // Solo mostrar panel izquierdo inicialmente

        layout.add(splitLayout);
        return layout;
    }

    private Component createFiltrosCuentas() {
        HorizontalLayout filtrosLayout = new HorizontalLayout();
        filtrosLayout.setWidthFull();
        filtrosLayout.setSpacing(true);
        filtrosLayout.setPadding(true);
        filtrosLayout.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("margin-bottom", "16px");

        // Configurar filtros
        filtroCliente.setItems(personaService.findAll());
        filtroCliente.setItemLabelGenerator(p -> p.getNombres() + " " + p.getApellidos());
        filtroCliente.setPlaceholder("Seleccione cliente");
        
        filtroEstado.setItems("", "PENDIENTE", "PAGADO_PARCIAL", "PAGADO_TOTAL", "VENCIDO", "ANULADO");
        filtroEstado.setPlaceholder("Todos");
        
        filtroRangoVencimiento.setItems("", "NO_VENCIDO", "1-30", "31-60", "61-90", "MAS_90");
        filtroRangoVencimiento.setPlaceholder("Todos");
        
        filtroTipoDocumento.setItems("", "FACTURA", "BOLETA", "NOTA_CREDITO");
        filtroTipoDocumento.setPlaceholder("Todos");

        btnBuscar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLimpiarFiltros.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        filtrosLayout.add(filtroCliente, filtroEstado, filtroRangoVencimiento, 
                         filtroTipoDocumento, filtroFechaDesde, filtroFechaHasta, 
                         btnBuscar, btnLimpiarFiltros);

        return filtrosLayout;
    }

    private void setupGridCuentas() {
        gridCuentas = new Grid<>(CuentaPorCobrar.class, false);
        gridCuentas.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridCuentas.setHeight("400px");

        gridCuentas.addColumn(CuentaPorCobrar::getNumeroDocumento).setHeader("Documento").setAutoWidth(true);
        gridCuentas.addColumn(CuentaPorCobrar::getTipoDocumento).setHeader("Tipo").setAutoWidth(true);
        
        gridCuentas.addColumn(cuenta -> cuenta.getCliente().getNombres() + " " + cuenta.getCliente().getApellidos())
            .setHeader("Cliente").setAutoWidth(true);
            
        gridCuentas.addColumn(CuentaPorCobrar::getFechaEmision).setHeader("Fecha Emisión").setAutoWidth(true);
        gridCuentas.addColumn(CuentaPorCobrar::getFechaVencimiento).setHeader("Fecha Vencimiento").setAutoWidth(true);
        gridCuentas.addColumn(CuentaPorCobrar::getMontoOriginal).setHeader("Monto Original").setAutoWidth(true);
        gridCuentas.addColumn(CuentaPorCobrar::getMontoPagado).setHeader("Pagado").setAutoWidth(true);
        gridCuentas.addColumn(CuentaPorCobrar::getSaldoPendiente).setHeader("Saldo Pendiente").setAutoWidth(true);
        
        // Columna de días de vencimiento con colores
        gridCuentas.addColumn(new ComponentRenderer<>(cuenta -> {
            Span diasSpan = new Span();
            Integer dias = cuenta.getDiasVencimiento();
            if (dias != null && dias > 0) {
                diasSpan.setText(dias + " días");
                if (dias > 90) {
                    diasSpan.getElement().getThemeList().add("badge error");
                } else if (dias > 30) {
                    diasSpan.getElement().getThemeList().add("badge");
                } else {
                    diasSpan.getElement().getThemeList().add("badge contrast");
                }
            } else {
                diasSpan.setText("No vencido");
                diasSpan.getElement().getThemeList().add("badge success");
            }
            return diasSpan;
        })).setHeader("Vencimiento").setAutoWidth(true);

        // Columna de estado con colores
        gridCuentas.addColumn(new ComponentRenderer<>(cuenta -> {
            Span estadoSpan = new Span(cuenta.getEstado());
            switch (cuenta.getEstado()) {
                case "PENDIENTE":
                    estadoSpan.getElement().getThemeList().add("badge contrast");
                    break;
                case "PAGADO_PARCIAL":
                    estadoSpan.getElement().getThemeList().add("badge");
                    break;
                case "PAGADO_TOTAL":
                    estadoSpan.getElement().getThemeList().add("badge success");
                    break;
                case "VENCIDO":
                    estadoSpan.getElement().getThemeList().add("badge error");
                    break;
                case "ANULADO":
                    estadoSpan.getElement().getThemeList().add("badge");
                    break;
            }
            return estadoSpan;
        })).setHeader("Estado").setAutoWidth(true);

        gridCuentas.addColumn(cuenta -> cuenta.getPrioridad() != null ? cuenta.getPrioridad() : "MEDIA")
            .setHeader("Prioridad").setAutoWidth(true);
    }

    private HorizontalLayout createBotonesCuentas() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        btnRegistrarPago.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRegistrarContacto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnExportar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        layout.add(btnRegistrarPago, btnRegistrarContacto, btnExportar);
        return layout;
    }

    // =============== TAB PAGOS ===============

    private Component createPagosTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(true);

        // Filtros para pagos
        Component filtrosPagosComponent = createFiltrosPagos();

        // Grid de pagos
        setupGridPagos();

        // Botones para pagos
        HorizontalLayout botonesPagos = createBotonesPagos();

        // Cargar datos una vez que el grid está configurado
        refreshGridPagos();

        layout.add(filtrosPagosComponent, gridPagos, botonesPagos);
        return layout;
    }

    private Component createFiltrosPagos() {
        HorizontalLayout filtrosLayout = new HorizontalLayout();
        filtrosLayout.setWidthFull();
        filtrosLayout.setSpacing(true);
        filtrosLayout.setPadding(true);
        filtrosLayout.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px");

        filtroClientePago.setItems(personaService.findAll());
        filtroClientePago.setItemLabelGenerator(p -> p.getNombres() + " " + p.getApellidos());
        filtroClientePago.setPlaceholder("Seleccione cliente");
        
        filtroEstadoPago.setItems("", "REGISTRADO", "VERIFICADO", "RECHAZADO");
        filtroEstadoPago.setPlaceholder("Todos");
        
        filtroFormaPago.setItems("", "EFECTIVO", "TRANSFERENCIA", "CHEQUE", "TARJETA");
        filtroFormaPago.setPlaceholder("Todos");

        Button btnBuscarPagos = new Button("Buscar");
        Button btnLimpiarPagos = new Button("Limpiar");
        btnBuscarPagos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLimpiarPagos.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        filtrosLayout.add(filtroClientePago, filtroEstadoPago, filtroFormaPago, 
                         filtroFechaPagoDesde, filtroFechaPagoHasta, btnBuscarPagos, btnLimpiarPagos);

        return filtrosLayout;
    }

    private void setupGridPagos() {
        gridPagos = new Grid<>(PagoCliente.class, false);
        gridPagos.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridPagos.setHeight("400px");

        gridPagos.addColumn(PagoCliente::getNumeroRecibo).setHeader("Recibo").setAutoWidth(true);
        gridPagos.addColumn(PagoCliente::getFechaPago).setHeader("Fecha Pago").setAutoWidth(true);
        
        gridPagos.addColumn(pago -> pago.getCliente().getNombres() + " " + pago.getCliente().getApellidos())
            .setHeader("Cliente").setAutoWidth(true);
            
        gridPagos.addColumn(pago -> pago.getCuentaPorCobrar().getNumeroDocumento())
            .setHeader("Documento").setAutoWidth(true);
            
        gridPagos.addColumn(PagoCliente::getMonto).setHeader("Monto").setAutoWidth(true);
        gridPagos.addColumn(PagoCliente::getFormaPago).setHeader("Forma Pago").setAutoWidth(true);
        gridPagos.addColumn(PagoCliente::getNumeroOperacion).setHeader("Nº Operación").setAutoWidth(true);
        
        // Estado del pago con colores
        gridPagos.addColumn(new ComponentRenderer<>(pago -> {
            Span estadoSpan = new Span(pago.getEstado());
            switch (pago.getEstado()) {
                case "REGISTRADO":
                    estadoSpan.getElement().getThemeList().add("badge contrast");
                    break;
                case "VERIFICADO":
                    estadoSpan.getElement().getThemeList().add("badge success");
                    break;
                case "RECHAZADO":
                    estadoSpan.getElement().getThemeList().add("badge error");
                    break;
            }
            return estadoSpan;
        })).setHeader("Estado").setAutoWidth(true);

        gridPagos.addColumn(PagoCliente::getTipoPago).setHeader("Tipo").setAutoWidth(true);
    }

    private HorizontalLayout createBotonesPagos() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        btnVerificarPago.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRechazarPago.addThemeVariants(ButtonVariant.LUMO_ERROR);

        layout.add(btnVerificarPago, btnRechazarPago);
        return layout;
    }

    // =============== TAB REPORTES ===============

    private Component createReportesTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 reportesTitle = new H3("📈 Reportes y Estadísticas");

        // Crear diferentes secciones de reportes
        Component reportesAntiguedad = createReporteAntiguedadSaldos();
        Component reportesPagos = createReportePagos();
        Component reportesClientes = createReporteClientes();

        layout.add(reportesTitle, reportesAntiguedad, reportesPagos, reportesClientes);
        return layout;
    }

    private Component createReporteAntiguedadSaldos() {
        Div container = new Div();
        container.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "16px")
            .set("margin-bottom", "16px");

        H3 title = new H3("📊 Reporte de Antigüedad de Saldos");
        title.getStyle().set("margin-top", "0");

        List<Object[]> estadisticas = cuentaService.getEstadisticasAntiguedadSaldos();
        
        if (!estadisticas.isEmpty()) {
            for (Object[] stat : estadisticas) {
                String rango = (String) stat[0];
                BigDecimal monto = (BigDecimal) stat[1];
                Long cantidad = (Long) stat[2];

                HorizontalLayout rangoLayout = new HorizontalLayout();
                rangoLayout.setWidthFull();
                rangoLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

                Span rangoInfo = new Span(getRangoLabel(rango) + " - " + cantidad + " cuentas");
                Span montoInfo = new Span(formatMoney(monto));
                montoInfo.getStyle().set("font-weight", "bold");

                rangoLayout.add(rangoInfo, montoInfo);
                container.add(rangoLayout);
            }
        }

        container.add(title);
        return container;
    }

    private Component createReportePagos() {
        Div container = new Div();
        container.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "16px")
            .set("margin-bottom", "16px");

        H3 title = new H3("💳 Resumen de Pagos");
        title.getStyle().set("margin-top", "0");

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<Object[]> pagosPorForma = pagoService.getTotalPagosPorFormaPago(
            Date.valueOf(inicioMes), Date.valueOf(hoy)
        );

        container.add(title);
        
        for (Object[] pago : pagosPorForma) {
            String formaPago = (String) pago[0];
            BigDecimal monto = (BigDecimal) pago[1];
            Long cantidad = (Long) pago[2];

            HorizontalLayout pagoLayout = new HorizontalLayout();
            pagoLayout.setWidthFull();
            pagoLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

            Span pagoInfo = new Span(formaPago + " - " + cantidad + " pagos");
            Span montoInfo = new Span(formatMoney(monto));
            montoInfo.getStyle().set("font-weight", "bold");

            pagoLayout.add(pagoInfo, montoInfo);
            container.add(pagoLayout);
        }

        return container;
    }

    private Component createReporteClientes() {
        Div container = new Div();
        container.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "16px");

        H3 title = new H3("👥 Top Clientes con Mayor Saldo Pendiente");
        title.getStyle().set("margin-top", "0");

        List<Object[]> topClientes = cuentaService.getTopClientesSaldoPendiente();
        
        container.add(title);
        
        for (int i = 0; i < Math.min(10, topClientes.size()); i++) {
            Object[] cliente = topClientes.get(i);
            Persona p = (Persona) cliente[0];
            BigDecimal saldo = (BigDecimal) cliente[1];

            HorizontalLayout clienteLayout = new HorizontalLayout();
            clienteLayout.setWidthFull();
            clienteLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

            Span clienteInfo = new Span((i + 1) + ". " + p.getNombres() + " " + p.getApellidos());
            Span saldoInfo = new Span(formatMoney(saldo));
            saldoInfo.getStyle().set("font-weight", "bold");

            clienteLayout.add(clienteInfo, saldoInfo);
            container.add(clienteLayout);
        }

        return container;
    }

    // =============== EVENT LISTENERS ===============

    private void setupEventListeners() {
        btnBuscar.addClickListener(e -> buscarCuentas());
        btnLimpiarFiltros.addClickListener(e -> limpiarFiltrosCuentas());
        btnRegistrarPago.addClickListener(e -> mostrarDialogoRegistrarPago());
        btnRegistrarContacto.addClickListener(e -> mostrarDialogoRegistrarContacto());
        btnVerificarPago.addClickListener(e -> verificarPagoSeleccionado());
        btnRechazarPago.addClickListener(e -> mostrarDialogoRechazarPago());
    }

    private void loadData() {
        // No cargar datos aquí ya que los grids se inicializan bajo demanda en los tabs
        // Los datos se cargan cuando se accede a cada tab específico
    }

    private void refreshGridCuentas() {
        if (gridCuentas != null) {
            gridCuentas.setItems(cuentaService.findCuentasPendientes());
        }
    }

    private void refreshGridPagos() {
        if (gridPagos != null) {
            gridPagos.setItems(pagoService.findPagosPendientesVerificacion());
        }
    }

    private void buscarCuentas() {
        List<CuentaPorCobrar> resultados = cuentaService.buscarConFiltros(
            filtroCliente.getValue(),
            filtroEstado.getValue(),
            filtroTipoDocumento.getValue(),
            filtroFechaDesde.getValue() != null ? Date.valueOf(filtroFechaDesde.getValue()) : null,
            filtroFechaHasta.getValue() != null ? Date.valueOf(filtroFechaHasta.getValue()) : null,
            filtroRangoVencimiento.getValue()
        );
        gridCuentas.setItems(resultados);
    }

    private void limpiarFiltrosCuentas() {
        filtroCliente.clear();
        filtroEstado.clear();
        filtroRangoVencimiento.clear();
        filtroTipoDocumento.clear();
        filtroFechaDesde.clear();
        filtroFechaHasta.clear();
        refreshGridCuentas();
    }

    private void mostrarDialogoRegistrarPago() {
        CuentaPorCobrar cuentaSeleccionada = gridCuentas.asSingleSelect().getValue();
        if (cuentaSeleccionada == null) {
            Notification.show("Seleccione una cuenta por cobrar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Aquí se implementaría el diálogo para registrar pago
        Notification.show("Diálogo de registro de pago - En desarrollo").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    private void mostrarDialogoRegistrarContacto() {
        CuentaPorCobrar cuentaSeleccionada = gridCuentas.asSingleSelect().getValue();
        if (cuentaSeleccionada == null) {
            Notification.show("Seleccione una cuenta por cobrar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Aquí se implementaría el diálogo para registrar contacto
        Notification.show("Diálogo de registro de contacto - En desarrollo").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    private void verificarPagoSeleccionado() {
        PagoCliente pagoSeleccionado = gridPagos.asSingleSelect().getValue();
        if (pagoSeleccionado == null) {
            Notification.show("Seleccione un pago para verificar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            pagoService.verificarPago(pagoSeleccionado.getId(), "ADMIN", "Verificado desde la interfaz");
            refreshGridPagos();
            Notification.show("Pago verificado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error al verificar el pago: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void mostrarDialogoRechazarPago() {
        PagoCliente pagoSeleccionado = gridPagos.asSingleSelect().getValue();
        if (pagoSeleccionado == null) {
            Notification.show("Seleccione un pago para rechazar").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Aquí se implementaría el diálogo para rechazar pago
        Notification.show("Diálogo de rechazo de pago - En desarrollo").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    // =============== UTILITY METHODS ===============

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "S/. 0.00";
        return String.format("S/. %,.2f", amount);
    }

    private String getRangoLabel(String rango) {
        switch (rango) {
            case "NO_VENCIDO": return "No vencido";
            case "1-30": return "1-30 días";
            case "31-60": return "31-60 días";
            case "61-90": return "61-90 días";
            case "MAS_90": return "Más de 90 días";
            default: return rango;
        }
    }
}