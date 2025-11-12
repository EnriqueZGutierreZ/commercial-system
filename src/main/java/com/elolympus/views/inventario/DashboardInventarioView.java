package com.elolympus.views.inventario;

import com.elolympus.data.Almacen.Stock;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.services.services.StockService;
import com.elolympus.services.services.ProductoService;
import com.elolympus.services.services.FacturaDetalleService;
import com.elolympus.services.services.BoletaDetalleService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@PageTitle("Dashboard de Inventario")
@Route(value = "dashboard-inventario", layout = MainLayout.class)
@AnonymousAllowed
public class DashboardInventarioView extends VerticalLayout {

    private final StockService stockService;
    private final ProductoService productoService;
    private final FacturaDetalleService facturaDetalleService;
    private final BoletaDetalleService boletaDetalleService;

    // Componentes para métricas
    private Div valorTotalInventario;
    private Div totalProductos;
    private Div productosConStock;
    private Div productosSinStock;
    private Div productosStockBajo;

    // Grids
    private Grid<Stock> gridStockBajo;
    private Grid<Stock> gridSinStock;
    private Grid<Object[]> gridProductosMasVendidos;
    private Grid<Stock> gridValorInventario;

    @Autowired
    public DashboardInventarioView(StockService stockService, ProductoService productoService,
                                  FacturaDetalleService facturaDetalleService, BoletaDetalleService boletaDetalleService) {
        this.stockService = stockService;
        this.productoService = productoService;
        this.facturaDetalleService = facturaDetalleService;
        this.boletaDetalleService = boletaDetalleService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createDashboard();
        loadData();
    }

    private void createDashboard() {

        // KPIs principales
        add(createKPISection());

        // Alertas críticas
        add(createAlertsSection());

        // Análisis de productos
        add(createAnalysisSection());

        // Botón de actualización
        Button refresh = new Button("🔄 Actualizar Dashboard", VaadinIcon.REFRESH.create());
        refresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refresh.addClickListener(e -> {
            loadData();
            com.vaadin.flow.component.notification.Notification.show("Dashboard actualizado", 2000, 
                com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER);
        });
        add(refresh);
    }

    private Component createKPISection() {
        H2 titulo = new H2("📈 Indicadores Clave");
        titulo.getStyle().set("margin-bottom", "10px");

        // Crear tarjetas de KPIs
        valorTotalInventario = createKPICard("💰 Valor Total Inventario", "S/. 0.00", "var(--lumo-success-color)");
        totalProductos = createKPICard("📦 Total Productos", "0", "var(--lumo-primary-color)");
        productosConStock = createKPICard("✅ Con Stock", "0", "var(--lumo-success-color)");
        productosSinStock = createKPICard("❌ Sin Stock", "0", "var(--lumo-error-color)");
        productosStockBajo = createKPICard("⚠️ Stock Bajo", "0", "var(--lumo-warning-color)");

        HorizontalLayout kpiLayout = new HorizontalLayout(
            valorTotalInventario, totalProductos, productosConStock, 
            productosSinStock, productosStockBajo
        );
        kpiLayout.setWidthFull();
        kpiLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout section = new VerticalLayout(titulo, kpiLayout);
        section.setPadding(false);
        section.setSpacing(true);
        return section;
    }

    private Div createKPICard(String title, String value, String color) {
        Div card = new Div();
        card.addClassName("kpi-card");
        card.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e0e0e0")
            .set("border-radius", "8px")
            .set("padding", "20px")
            .set("text-align", "center")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("min-width", "180px")
            .set("margin", "5px");

        H4 cardTitle = new H4(title);
        cardTitle.getStyle()
            .set("margin", "0 0 10px 0")
            .set("font-size", "14px")
            .set("color", "#666");

        H2 cardValue = new H2(value);
        cardValue.getStyle()
            .set("margin", "0")
            .set("font-size", "28px")
            .set("font-weight", "bold")
            .set("color", color);

        card.add(cardTitle, cardValue);
        return card;
    }

    private Component createAlertsSection() {
        H2 titulo = new H2("🚨 Alertas Críticas");
        titulo.getStyle().set("color", "#d32f2f");

        // Grid para productos sin stock
        gridSinStock = new Grid<>(Stock.class, false);
        gridSinStock.addColumn(stock -> stock.getProducto().getCodigo()).setHeader("Código").setAutoWidth(true);
        gridSinStock.addColumn(stock -> stock.getProducto().getNombre()).setHeader("Producto").setAutoWidth(true);
        gridSinStock.addColumn(Stock::getStockActual).setHeader("Stock Actual").setAutoWidth(true);
        gridSinStock.addColumn(stock -> stock.getAlmacen().getDescripcion()).setHeader("Almacén").setAutoWidth(true);
        gridSinStock.setHeight("200px");

        // Grid para productos con stock bajo
        gridStockBajo = new Grid<>(Stock.class, false);
        gridStockBajo.addColumn(stock -> stock.getProducto().getCodigo()).setHeader("Código").setAutoWidth(true);
        gridStockBajo.addColumn(stock -> stock.getProducto().getNombre()).setHeader("Producto").setAutoWidth(true);
        gridStockBajo.addColumn(Stock::getStockActual).setHeader("Stock Actual").setAutoWidth(true);
        gridStockBajo.addColumn(stock -> stock.getProducto().getStockMinimo()).setHeader("Stock Mínimo").setAutoWidth(true);
        gridStockBajo.addColumn(new ComponentRenderer<Component, Stock>(this::createStockProgressBar)).setHeader("% Stock").setAutoWidth(true);
        gridStockBajo.addColumn(stock -> stock.getAlmacen().getDescripcion()).setHeader("Almacén").setAutoWidth(true);
        gridStockBajo.setHeight("300px");

        HorizontalLayout alertsLayout = new HorizontalLayout();
        alertsLayout.setSizeFull();

        VerticalLayout sinStockSection = new VerticalLayout();
        sinStockSection.add(new H3("❌ Productos Sin Stock"), gridSinStock);
        sinStockSection.setWidth("50%");

        VerticalLayout stockBajoSection = new VerticalLayout();
        stockBajoSection.add(new H3("⚠️ Productos con Stock Bajo"), gridStockBajo);
        stockBajoSection.setWidth("50%");

        alertsLayout.add(sinStockSection, stockBajoSection);

        VerticalLayout section = new VerticalLayout(titulo, alertsLayout);
        section.setPadding(false);
        return section;
    }


    private Component createStockProgressBar(Stock stock) {
        if (stock.getProducto().getStockMinimo() == null || 
            stock.getProducto().getStockMinimo().compareTo(BigDecimal.ZERO) == 0) {
            return new Span("N/A");
        }

        BigDecimal porcentaje = stock.getStockActual()
            .divide(stock.getProducto().getStockMinimo(), 2, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));

        ProgressBar progressBar = new ProgressBar();
        double value = Math.min(porcentaje.doubleValue() / 100.0, 1.0);
        progressBar.setValue(value);
        progressBar.setWidth("100px");

        // Color según el nivel de stock
        if (value < 0.3) {
            progressBar.getStyle().set("--lumo-progress-color", "var(--lumo-error-color)");
        } else if (value < 0.6) {
            progressBar.getStyle().set("--lumo-progress-color", "var(--lumo-warning-color)");
        } else {
            progressBar.getStyle().set("--lumo-progress-color", "var(--lumo-success-color)");
        }

        Span label = new Span(porcentaje.intValue() + "%");
        label.getStyle().set("font-size", "12px");

        VerticalLayout container = new VerticalLayout(progressBar, label);
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setPadding(false);
        container.setSpacing(false);

        return container;
    }

    private Component createAnalysisSection() {
        H2 titulo = new H2("📊 Análisis de Productos");

        // Grid para productos más vendidos
        gridProductosMasVendidos = new Grid<>();
        gridProductosMasVendidos.addColumn(row -> ((Producto) ((Object[]) row)[0]).getCodigo()).setHeader("Código").setAutoWidth(true);
        gridProductosMasVendidos.addColumn(row -> ((Producto) ((Object[]) row)[0]).getNombre()).setHeader("Producto").setAutoWidth(true);
        gridProductosMasVendidos.addColumn(row -> ((BigDecimal) ((Object[]) row)[1]).toString()).setHeader("Cantidad Vendida").setAutoWidth(true);
        gridProductosMasVendidos.setHeight("300px");

        // Grid para valor de inventario por producto
        gridValorInventario = new Grid<>(Stock.class, false);
        gridValorInventario.addColumn(stock -> stock.getProducto().getCodigo()).setHeader("Código").setAutoWidth(true);
        gridValorInventario.addColumn(stock -> stock.getProducto().getNombre()).setHeader("Producto").setAutoWidth(true);
        gridValorInventario.addColumn(Stock::getStockActual).setHeader("Stock").setAutoWidth(true);
        gridValorInventario.addColumn(Stock::getCostoPromedio).setHeader("Costo Promedio").setAutoWidth(true);
        gridValorInventario.addColumn(this::calcularValorTotal).setHeader("Valor Total").setAutoWidth(true);
        gridValorInventario.setHeight("300px");

        HorizontalLayout analysisLayout = new HorizontalLayout();
        analysisLayout.setSizeFull();

        VerticalLayout ventasSection = new VerticalLayout();
        ventasSection.add(new H3("🏆 Productos Más Vendidos (Último Mes)"), gridProductosMasVendidos);
        ventasSection.setWidth("50%");

        VerticalLayout valorSection = new VerticalLayout();
        valorSection.add(new H3("💎 Valor de Inventario por Producto"), gridValorInventario);
        valorSection.setWidth("50%");

        analysisLayout.add(ventasSection, valorSection);

        VerticalLayout section = new VerticalLayout(titulo, analysisLayout);
        section.setPadding(false);
        return section;
    }

    private String calcularValorTotal(Stock stock) {
        if (stock.getStockActual() != null && stock.getCostoPromedio() != null) {
            BigDecimal valor = stock.getStockActual().multiply(stock.getCostoPromedio());
            return "S/. " + valor.setScale(2, RoundingMode.HALF_UP).toString();
        }
        return "S/. 0.00";
    }

    private void loadData() {
        // Cargar KPIs principales
        loadKPIs();

        // Cargar alertas
        loadAlerts();

        // Cargar análisis
        loadAnalysis();
    }

    private void loadKPIs() {
        try {
            // Valor total del inventario
            BigDecimal valorTotal = stockService.getValorTotalInventario();
            updateKPIValue(valorTotalInventario, "S/. " + valorTotal.setScale(2, RoundingMode.HALF_UP).toString());

            // Total de productos
            List<Producto> productos = productoService.findAll();
            updateKPIValue(totalProductos, String.valueOf(productos.size()));

            // Productos con stock
            List<Stock> stocksConStock = stockService.findAll().stream()
                .filter(s -> s.getStockActual().compareTo(BigDecimal.ZERO) > 0)
                .toList();
            updateKPIValue(productosConStock, String.valueOf(stocksConStock.size()));

            // Productos sin stock
            List<Stock> stocksSinStock = stockService.getProductosSinStock();
            updateKPIValue(productosSinStock, String.valueOf(stocksSinStock.size()));

            // Productos con stock bajo
            List<Stock> stocksBajo = stockService.getProductosConStockBajo();
            updateKPIValue(productosStockBajo, String.valueOf(stocksBajo.size()));

        } catch (Exception e) {
            System.err.println("Error cargando KPIs: " + e.getMessage());
        }
    }

    private void updateKPIValue(Div kpiCard, String newValue) {
        // Buscar el H2 dentro del card y actualizar su texto
        kpiCard.getChildren()
            .filter(component -> component instanceof H2)
            .findFirst()
            .ifPresent(h2 -> ((H2) h2).setText(newValue));
    }

    private void loadAlerts() {
        try {
            // Productos sin stock
            List<Stock> sinStock = stockService.getProductosSinStock();
            gridSinStock.setItems(sinStock);

            // Productos con stock bajo
            List<Stock> stockBajo = stockService.getProductosConStockBajo();
            gridStockBajo.setItems(stockBajo);

        } catch (Exception e) {
            System.err.println("Error cargando alertas: " + e.getMessage());
        }
    }

    private void loadAnalysis() {
        try {
            // Productos más vendidos (último mes)
            Date inicioMes = Date.valueOf(LocalDate.now().withDayOfMonth(1));
            Date finMes = Date.valueOf(LocalDate.now());

            List<Object[]> facturasMasVendidos = facturaDetalleService.getProductosMasVendidos(inicioMes, finMes);
            List<Object[]> boletasMasVendidos = boletaDetalleService.getProductosMasVendidos(inicioMes, finMes);

            // Combinar resultados (tomar los primeros 10 de facturas por simplicidad)
            gridProductosMasVendidos.setItems(facturasMasVendidos.stream().limit(10).toList());

            // Valor de inventario (top 20 por valor)
            List<Stock> stocksConValor = stockService.findAll().stream()
                .filter(s -> s.getStockActual().compareTo(BigDecimal.ZERO) > 0 && s.getCostoPromedio() != null)
                .sorted((s1, s2) -> {
                    BigDecimal valor1 = s1.getStockActual().multiply(s1.getCostoPromedio());
                    BigDecimal valor2 = s2.getStockActual().multiply(s2.getCostoPromedio());
                    return valor2.compareTo(valor1); // Orden descendente
                })
                .limit(20)
                .toList();

            gridValorInventario.setItems(stocksConValor);

        } catch (Exception e) {
            System.err.println("Error cargando análisis: " + e.getMessage());
        }
    }
}