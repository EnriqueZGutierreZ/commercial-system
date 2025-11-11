package com.elolympus.views.reportes;

import com.elolympus.services.services.FacturaService;
import com.elolympus.services.services.BoletaService;
import com.elolympus.services.services.NotaCreditoService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@PageTitle("Reportes")
@Route(value = "reportes", layout = MainLayout.class)
@AnonymousAllowed
public class ReportesView extends VerticalLayout {

    private final FacturaService facturaService;
    private final BoletaService boletaService;
    private final NotaCreditoService notaCreditoService;

    @Autowired
    public ReportesView(FacturaService facturaService, BoletaService boletaService, NotaCreditoService notaCreditoService) {
        this.facturaService = facturaService;
        this.boletaService = boletaService;
        this.notaCreditoService = notaCreditoService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        createDashboard();
    }

    private void createDashboard() {
        H2 titulo = new H2("Dashboard de Ventas");
        add(titulo);
        
        // Estadísticas del día
        H3 estadisticasHoy = new H3("Estadísticas de Hoy");
        add(estadisticasHoy);
        
        // Ventas del día
        BigDecimal totalFacturasHoy = facturaService.getTotalVentasDelDia();
        BigDecimal totalBoletasHoy = boletaService.getTotalVentasDelDia();
        BigDecimal totalNotasCreditoHoy = notaCreditoService.getTotalNotasCreditoDelDia();
        BigDecimal totalVentasHoy = totalFacturasHoy.add(totalBoletasHoy).subtract(totalNotasCreditoHoy);
        
        Span ventasHoy = new Span("Total Ventas Netas Hoy: S/. " + totalVentasHoy);
        ventasHoy.getStyle().set("font-size", "18px").set("font-weight", "bold").set("color", "green");
        add(ventasHoy);
        
        Span facturasHoy = new Span("Facturas: S/. " + totalFacturasHoy);
        Span boletasHoy = new Span("Boletas: S/. " + totalBoletasHoy);
        Span notasCreditoHoy = new Span("Notas de Crédito: S/. " + totalNotasCreditoHoy);
        add(facturasHoy, boletasHoy, notasCreditoHoy);
        
        // Estadísticas del mes
        H3 estadisticasMes = new H3("Estadísticas del Mes");
        add(estadisticasMes);
        
        BigDecimal totalFacturasMes = facturaService.getTotalVentasDelMes();
        BigDecimal totalBoletasMes = boletaService.getTotalVentasDelMes();
        BigDecimal totalNotasCreditoMes = notaCreditoService.getTotalNotasCreditoDelMes();
        BigDecimal totalVentasMes = totalFacturasMes.add(totalBoletasMes).subtract(totalNotasCreditoMes);
        
        Span ventasMes = new Span("Total Ventas Netas del Mes: S/. " + totalVentasMes);
        ventasMes.getStyle().set("font-size", "18px").set("font-weight", "bold").set("color", "blue");
        add(ventasMes);
        
        Span facturasMes = new Span("Facturas: S/. " + totalFacturasMes);
        Span boletasMes = new Span("Boletas: S/. " + totalBoletasMes);
        Span notasCreditoMes = new Span("Notas de Crédito: S/. " + totalNotasCreditoMes);
        add(facturasMes, boletasMes, notasCreditoMes);
        
        // Contadores por estado
        H3 contadores = new H3("Estados de Documentos");
        add(contadores);
        
        // Facturas por estado
        Long facturasEmitidas = facturaService.countByEstado("EMITIDA");
        Long facturasPagadas = facturaService.countByEstado("PAGADA");
        Long facturasVencidas = facturaService.countByEstado("VENCIDA");
        Long facturasAnuladas = facturaService.countByEstado("ANULADA");
        
        add(new Span("Facturas Emitidas: " + facturasEmitidas));
        add(new Span("Facturas Pagadas: " + facturasPagadas));
        add(new Span("Facturas Vencidas: " + facturasVencidas));
        add(new Span("Facturas Anuladas: " + facturasAnuladas));
        
        // Boletas por estado
        Long boletasPendientes = boletaService.countByEstado("PENDIENTE");
        Long boletasPagadas = boletaService.countByEstado("PAGADA");
        Long boletasAnuladas = boletaService.countByEstado("ANULADA");
        
        add(new Span("Boletas Pendientes: " + boletasPendientes));
        add(new Span("Boletas Pagadas: " + boletasPagadas));
        add(new Span("Boletas Anuladas: " + boletasAnuladas));
        
        // Notas de crédito por estado
        Long notasCreditoEmitidas = notaCreditoService.countByEstado("EMITIDA");
        Long notasCreditoAplicadas = notaCreditoService.countByEstado("APLICADA");
        Long notasCreditoAnuladas = notaCreditoService.countByEstado("ANULADA");
        
        add(new Span("Notas Crédito Emitidas: " + notasCreditoEmitidas));
        add(new Span("Notas Crédito Aplicadas: " + notasCreditoAplicadas));
        add(new Span("Notas Crédito Anuladas: " + notasCreditoAnuladas));
    }
}