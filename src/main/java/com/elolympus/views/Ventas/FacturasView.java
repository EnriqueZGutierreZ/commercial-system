package com.elolympus.views.Ventas;

import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.services.FacturaService;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.EmpresaService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.sql.Date;

@PageTitle("Facturas")
@Route(value = "facturas", layout = MainLayout.class)
@PermitAll
public class FacturasView extends Div {

    private final FacturaService facturaService;
    private final PersonaService personaService;
    private final EmpresaService empresaService;
    private Factura factura;
    private BeanValidationBinder<Factura> binder;

    // Componentes UI
    private Grid<Factura> gridFacturas = new Grid<>(Factura.class, false);
    private final TextField serie = new TextField("Serie");
    private final TextField numeroFactura = new TextField("Número Factura");
    private final DatePicker fechaEmision = new DatePicker("Fecha Emisión");
    private final DatePicker fechaVencimiento = new DatePicker("Fecha Vencimiento");
    private final ComboBox<Persona> cliente = new ComboBox<>("Cliente");
    private final ComboBox<Empresa> empresa = new ComboBox<>("Empresa");
    private final BigDecimalField subtotal = new BigDecimalField("Subtotal");
    private final BigDecimalField descuento = new BigDecimalField("Descuento");
    private final BigDecimalField igv = new BigDecimalField("IGV (18%)");
    private final BigDecimalField total = new BigDecimalField("Total");
    private final ComboBox<String> estado = new ComboBox<>("Estado");
    private final ComboBox<String> formaPago = new ComboBox<>("Forma de Pago");
    private final ComboBox<String> tipoDocumento = new ComboBox<>("Tipo Documento");
    private final ComboBox<String> moneda = new ComboBox<>("Moneda");
    private final TextField numeroOrdenCompra = new TextField("Nº Orden Compra");
    private final DatePicker fechaPago = new DatePicker("Fecha Pago");
    private final TextArea observaciones = new TextArea("Observaciones");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Anular");
    private final Button marcarPagada = new Button("Marcar como Pagada");
    private final Button marcarVencida = new Button("Marcar como Vencida");

    private final FormLayout formLayout = new FormLayout();

    // Filtros
    private final TextField filtroNumero = new TextField("Filtrar por número");
    private final TextField filtroSerie = new TextField("Filtrar por serie");
    private final TextField filtroCliente = new TextField("Filtrar por cliente");
    private final ComboBox<String> filtroEstado = new ComboBox<>("Filtrar por estado");

    @Autowired
    public FacturasView(FacturaService facturaService, PersonaService personaService, EmpresaService empresaService) {
        this.facturaService = facturaService;
        this.personaService = personaService;
        this.empresaService = empresaService;
        
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Factura.class);
            // Don't auto-bind fields - we'll bind them manually in setupForm()
            
        } catch (Exception e) {
            System.out.println("ERROR en FacturasView: " + e.getMessage());
        }
        
        addClassName("facturas-view");
        setSizeFull();
        setupGrid();
        setupForm();

        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private void setupGrid() {
        gridFacturas = new Grid<>(Factura.class, false);
        gridFacturas.setClassName("grilla");
        gridFacturas.setHeight("86%");
        
        gridFacturas.addColumn(Factura::getSerie).setHeader("Serie").setAutoWidth(true);
        gridFacturas.addColumn(Factura::getNumeroFactura).setHeader("Número").setAutoWidth(true);
        gridFacturas.addColumn(Factura::getFechaEmision).setHeader("Fecha Emisión").setAutoWidth(true);
        gridFacturas.addColumn(factura -> factura.getCliente() != null ? 
            factura.getCliente().getNombres() + " " + factura.getCliente().getApellidos() : "")
            .setHeader("Cliente").setAutoWidth(true);
        gridFacturas.addColumn(Factura::getTotal).setHeader("Total").setAutoWidth(true);
        
        // Columna de estado con colores
        gridFacturas.addColumn(new ComponentRenderer<>(factura -> {
            com.vaadin.flow.component.html.Span span = new com.vaadin.flow.component.html.Span(factura.getEstado());
            switch (factura.getEstado()) {
                case "EMITIDA":
                    span.getElement().getThemeList().add("badge contrast");
                    break;
                case "PAGADA":
                    span.getElement().getThemeList().add("badge success");
                    break;
                case "ANULADA":
                    span.getElement().getThemeList().add("badge error");
                    break;
                case "VENCIDA":
                    span.getElement().getThemeList().add("badge error primary");
                    break;
            }
            return span;
        })).setHeader("Estado").setAutoWidth(true);
        
        gridFacturas.addColumn(Factura::getFormaPago).setHeader("Forma Pago").setAutoWidth(true);
        gridFacturas.addColumn(Factura::getMoneda).setHeader("Moneda").setAutoWidth(true);
        
        gridFacturas.getColumns().forEach(col -> col.setAutoWidth(true));
        gridFacturas.asSingleSelect().addValueChangeListener(event -> editFactura(event.getValue()));
    }

    private Component createGridLayout() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.addClassName("grid-container");
        
        // Crear barra de filtros
        HorizontalLayout filtros = createFiltersLayout();
        
        gridContainer.add(new H3("Facturas"), filtros, gridFacturas);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private HorizontalLayout createFiltersLayout() {
        // Configurar filtros
        filtroEstado.setItems("", "EMITIDA", "PAGADA", "ANULADA", "VENCIDA");
        filtroEstado.setPlaceholder("Todos los estados");
        
        // Listeners para filtros
        filtroNumero.addValueChangeListener(e -> applyFilters());
        filtroSerie.addValueChangeListener(e -> applyFilters());
        filtroCliente.addValueChangeListener(e -> applyFilters());
        filtroEstado.addValueChangeListener(e -> applyFilters());
        
        Button limpiarFiltros = new Button("Limpiar", e -> {
            filtroNumero.clear();
            filtroSerie.clear();
            filtroCliente.clear();
            filtroEstado.clear();
            refreshGrid();
        });
        limpiarFiltros.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        // Crear layout para estado y botón limpiar juntos
        HorizontalLayout estadoYLimpiar = new HorizontalLayout(filtroEstado, limpiarFiltros);
        estadoYLimpiar.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);
        estadoYLimpiar.setSpacing(true);
        estadoYLimpiar.getStyle().set("gap", "10px");
        
        HorizontalLayout filtros = new HorizontalLayout(filtroNumero, filtroSerie, filtroCliente, estadoYLimpiar);
        filtros.addClassName("filter-layout");
        return filtros;
    }

    private Component createEditorLayout() {
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setWidth("40%");
        editorDiv.setClassName("editor-layout");
        
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        
        formLayout.add(
            serie, numeroFactura, fechaEmision, fechaVencimiento, 
            cliente, empresa, subtotal, descuento, igv, total, 
            estado, formaPago, tipoDocumento, moneda, numeroOrdenCompra,
            fechaPago, observaciones
        );
        
        // Configurar listeners de botones
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> anularFactura());
        marcarPagada.addClickListener(event -> marcarComoPagada());
        marcarVencida.addClickListener(event -> marcarComoVencida());

        // Estilos de botones
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        marcarPagada.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        marcarVencida.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_CONTRAST);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        div.add(formLayout);
        createButtonLayout(editorDiv);
        return editorDiv;
    }

    private void createButtonLayout(Div div) {
        HorizontalLayout buttonLayout1 = new HorizontalLayout();
        buttonLayout1.setClassName("button-layout");
        buttonLayout1.add(save, cancel);
        
        HorizontalLayout buttonLayout2 = new HorizontalLayout();
        buttonLayout2.setClassName("button-layout");
        buttonLayout2.add(marcarPagada, marcarVencida, delete);
        
        div.add(buttonLayout1, buttonLayout2);
    }

    private void setupForm() {
        // Configure ComboBoxes
        cliente.setItems(personaService.findAll());
        cliente.setItemLabelGenerator(persona -> persona.getNombres() + " " + persona.getApellidos() + " - " + persona.getNum_documento());
        cliente.setPlaceholder("Seleccione un cliente");
        cliente.setRequiredIndicatorVisible(true);
        
        empresa.setItems(empresaService.findAll());
        empresa.setItemLabelGenerator(Empresa::getCommercialName);
        empresa.setPlaceholder("Seleccione una empresa");
        empresa.setRequiredIndicatorVisible(true);
        
        estado.setItems("EMITIDA", "PAGADA", "ANULADA", "VENCIDA");
        estado.setValue("EMITIDA");
        
        formaPago.setItems("CONTADO", "CREDITO");
        
        tipoDocumento.setItems("FACTURA", "FACTURA_ELECTRONICA");
        tipoDocumento.setValue("FACTURA");
        
        moneda.setItems("PEN", "USD");
        moneda.setValue("PEN");
        
        serie.setValue("F001");
        serie.setRequiredIndicatorVisible(true);
        
        // Campos de solo lectura
        total.setReadOnly(true);
        igv.setReadOnly(true);
        
        // Auto-cálculo de IGV y total
        setupAutoCalculation();
        
        // Bind fields manually
        bindFields();
    }

    private void setupAutoCalculation() {
        subtotal.addValueChangeListener(e -> calculateTotals());
        descuento.addValueChangeListener(e -> calculateTotals());
    }
    
    private void calculateTotals() {
        if (subtotal.getValue() != null) {
            BigDecimal subtotalValue = subtotal.getValue();
            BigDecimal descuentoValue = descuento.getValue() != null ? descuento.getValue() : BigDecimal.ZERO;
            BigDecimal igvValue = subtotalValue.multiply(new BigDecimal("0.18"));
            BigDecimal totalValue = subtotalValue.add(igvValue).subtract(descuentoValue);
            
            igv.setValue(igvValue);
            total.setValue(totalValue);
        }
    }

    private void bindFields() {
        binder.forField(fechaEmision)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Factura::getFechaEmision, Factura::setFechaEmision);
                
        binder.forField(fechaVencimiento)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Factura::getFechaVencimiento, Factura::setFechaVencimiento);
                
        binder.forField(fechaPago)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Factura::getFechaPago, Factura::setFechaPago);
        
        // Bind other fields
        binder.forField(serie).bind(Factura::getSerie, Factura::setSerie);
        binder.forField(numeroFactura).bind(Factura::getNumeroFactura, Factura::setNumeroFactura);
        binder.forField(cliente).bind(Factura::getCliente, Factura::setCliente);
        binder.forField(empresa).bind(Factura::getEmpresa, Factura::setEmpresa);
        binder.forField(subtotal).bind(Factura::getSubtotal, Factura::setSubtotal);
        binder.forField(descuento).bind(Factura::getDescuento, Factura::setDescuento);
        binder.forField(igv).bind(Factura::getIgv, Factura::setIgv);
        binder.forField(total).bind(Factura::getTotal, Factura::setTotal);
        binder.forField(estado).bind(Factura::getEstado, Factura::setEstado);
        binder.forField(formaPago).bind(Factura::getFormaPago, Factura::setFormaPago);
        binder.forField(tipoDocumento).bind(Factura::getTipoDocumento, Factura::setTipoDocumento);
        binder.forField(moneda).bind(Factura::getMoneda, Factura::setMoneda);
        binder.forField(numeroOrdenCompra).bind(Factura::getNumeroOrdenCompra, Factura::setNumeroOrdenCompra);
        binder.forField(observaciones).bind(Factura::getObservaciones, Factura::setObservaciones);
    }

    private void refreshGrid() {
        gridFacturas.setItems(facturaService.findAll());
    }

    private void applyFilters() {
        String numero = filtroNumero.getValue();
        String serieText = filtroSerie.getValue();
        String clienteNombre = filtroCliente.getValue();
        String estadoFiltro = filtroEstado.getValue();
        
        gridFacturas.setItems(facturaService.buscarConFiltros(numero, serieText, clienteNombre, estadoFiltro, null, null));
    }

    private void save() {
        try {
            if (this.factura == null) {
                this.factura = new Factura();
            }
            if (binder.writeBeanIfValid(this.factura)) {
                facturaService.save(this.factura);
                clearForm();
                refreshGrid();
                Notification.show("Factura guardada correctamente.");
            } else {
                Notification.show("Error al guardar los datos. Por favor, revise los campos e intente nuevamente.");
            }
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }

    private void anularFactura() {
        if (factura != null && factura.getId() != null) {
            try {
                facturaService.anularFactura(factura.getId(), observaciones.getValue());
                clearForm();
                refreshGrid();
                Notification.show("Factura anulada correctamente.");
            } catch (Exception e) {
                Notification.show("Error al anular la factura: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una factura para anular.");
        }
    }

    private void marcarComoPagada() {
        if (factura != null && factura.getId() != null && formaPago.getValue() != null) {
            try {
                facturaService.marcarComoPagada(factura.getId(), formaPago.getValue());
                clearForm();
                refreshGrid();
                Notification.show("Factura marcada como pagada correctamente.");
            } catch (Exception e) {
                Notification.show("Error al marcar como pagada: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una factura y la forma de pago para marcar como pagada.");
        }
    }

    private void marcarComoVencida() {
        if (factura != null && factura.getId() != null) {
            try {
                facturaService.marcarComoVencida(factura.getId());
                clearForm();
                refreshGrid();
                Notification.show("Factura marcada como vencida correctamente.");
            } catch (Exception e) {
                Notification.show("Error al marcar como vencida: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una factura para marcar como vencida.");
        }
    }

    private void clearForm() {
        this.factura = new Factura();
        binder.readBean(this.factura);
        estado.setValue("EMITIDA");
        tipoDocumento.setValue("FACTURA");
        moneda.setValue("PEN");
        serie.setValue("F001");
        save.setText("Guardar");
    }

    private void editFactura(Factura factura) {
        if (factura == null) {
            clearForm();
        } else {
            this.factura = factura;
            binder.readBean(this.factura);
            save.setText("Actualizar");
        }
    }
}
