package com.elolympus.views.Ventas;

import com.elolympus.data.Ventas.NotaCredito;
import com.elolympus.data.Ventas.Factura;
import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.services.NotaCreditoService;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.EmpresaService;
import com.elolympus.services.services.FacturaService;
import com.elolympus.services.services.BoletaService;
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

@PageTitle("Notas de Crédito")
@Route(value = "notas-credito", layout = MainLayout.class)
@PermitAll
public class NotasCreditoView extends Div {

    private final NotaCreditoService notaCreditoService;
    private final PersonaService personaService;
    private final EmpresaService empresaService;
    private final FacturaService facturaService;
    private final BoletaService boletaService;
    private NotaCredito notaCredito;
    private BeanValidationBinder<NotaCredito> binder;

    // Componentes UI
    private Grid<NotaCredito> gridNotasCredito = new Grid<>(NotaCredito.class, false);
    private final TextField serie = new TextField("Serie");
    private final TextField numeroNota = new TextField("Número Nota");
    private final DatePicker fechaEmision = new DatePicker("Fecha Emisión");
    private final ComboBox<Persona> cliente = new ComboBox<>("Cliente");
    private final ComboBox<Empresa> empresa = new ComboBox<>("Empresa");
    
    // Documento de referencia
    private final ComboBox<String> documentoReferenciaTipo = new ComboBox<>("Tipo Documento Ref.");
    private final TextField documentoReferenciaSerie = new TextField("Serie Documento Ref.");
    private final TextField documentoReferenciaNumero = new TextField("Número Documento Ref.");
    private final DatePicker documentoReferenciaFecha = new DatePicker("Fecha Documento Ref.");
    
    private final BigDecimalField subtotal = new BigDecimalField("Subtotal");
    private final BigDecimalField igv = new BigDecimalField("IGV (18%)");
    private final BigDecimalField total = new BigDecimalField("Total");
    private final ComboBox<String> estado = new ComboBox<>("Estado");
    private final ComboBox<String> tipoNota = new ComboBox<>("Tipo de Nota");
    private final ComboBox<String> tipoDocumento = new ComboBox<>("Tipo Documento");
    private final ComboBox<String> moneda = new ComboBox<>("Moneda");
    private final TextArea motivo = new TextArea("Motivo");
    private final TextArea observaciones = new TextArea("Observaciones");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Anular");
    private final Button aplicar = new Button("Aplicar");

    private final FormLayout formLayout = new FormLayout();

    // Filtros
    private final TextField filtroNumero = new TextField("Filtrar por número");
    private final TextField filtroSerie = new TextField("Filtrar por serie");
    private final TextField filtroCliente = new TextField("Filtrar por cliente");
    private final ComboBox<String> filtroEstado = new ComboBox<>("Filtrar por estado");
    private final ComboBox<String> filtroTipoNota = new ComboBox<>("Filtrar por tipo");

    @Autowired
    public NotasCreditoView(NotaCreditoService notaCreditoService, PersonaService personaService, 
                           EmpresaService empresaService, FacturaService facturaService, BoletaService boletaService) {
        this.notaCreditoService = notaCreditoService;
        this.personaService = personaService;
        this.empresaService = empresaService;
        this.facturaService = facturaService;
        this.boletaService = boletaService;
        
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(NotaCredito.class);
            // Don't auto-bind fields - we'll bind them manually in setupForm()
            
        } catch (Exception e) {
            System.out.println("ERROR en NotasCreditoView: " + e.getMessage());
        }
        
        addClassName("notas-credito-view");
        setSizeFull();
        setupGrid();
        setupForm();

        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private void setupGrid() {
        gridNotasCredito = new Grid<>(NotaCredito.class, false);
        gridNotasCredito.setClassName("grilla");
        gridNotasCredito.setHeight("86%");
        
        gridNotasCredito.addColumn(NotaCredito::getSerie).setHeader("Serie").setAutoWidth(true);
        gridNotasCredito.addColumn(NotaCredito::getNumeroNota).setHeader("Número").setAutoWidth(true);
        gridNotasCredito.addColumn(NotaCredito::getFechaEmision).setHeader("Fecha Emisión").setAutoWidth(true);
        gridNotasCredito.addColumn(notaCredito -> notaCredito.getCliente() != null ? 
            notaCredito.getCliente().getNombres() + " " + notaCredito.getCliente().getApellidos() : "")
            .setHeader("Cliente").setAutoWidth(true);
        gridNotasCredito.addColumn(NotaCredito::getReferenciaCompleta).setHeader("Documento Ref.").setAutoWidth(true);
        gridNotasCredito.addColumn(NotaCredito::getTipoNota).setHeader("Tipo").setAutoWidth(true);
        gridNotasCredito.addColumn(NotaCredito::getTotal).setHeader("Total").setAutoWidth(true);
        
        // Columna de estado con colores
        gridNotasCredito.addColumn(new ComponentRenderer<>(notaCredito -> {
            com.vaadin.flow.component.html.Span span = new com.vaadin.flow.component.html.Span(notaCredito.getEstado());
            switch (notaCredito.getEstado()) {
                case "EMITIDA":
                    span.getElement().getThemeList().add("badge contrast");
                    break;
                case "APLICADA":
                    span.getElement().getThemeList().add("badge success");
                    break;
                case "ANULADA":
                    span.getElement().getThemeList().add("badge error");
                    break;
            }
            return span;
        })).setHeader("Estado").setAutoWidth(true);
        
        gridNotasCredito.addColumn(NotaCredito::getMoneda).setHeader("Moneda").setAutoWidth(true);
        
        gridNotasCredito.getColumns().forEach(col -> col.setAutoWidth(true));
        gridNotasCredito.asSingleSelect().addValueChangeListener(event -> editNotaCredito(event.getValue()));
    }

    private Component createGridLayout() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.addClassName("grid-container");
        
        // Crear barra de filtros
        HorizontalLayout filtros = createFiltersLayout();
        
        gridContainer.add(new H3("Notas de Crédito"), filtros, gridNotasCredito);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private HorizontalLayout createFiltersLayout() {
        // Configurar filtros
        filtroEstado.setItems("", "EMITIDA", "APLICADA", "ANULADA");
        filtroEstado.setPlaceholder("Todos los estados");
        
        filtroTipoNota.setItems("", "DEVOLUCION", "DESCUENTO", "ANULACION", "ERROR_FACTURACION");
        filtroTipoNota.setPlaceholder("Todos los tipos");
        
        // Listeners para filtros
        filtroNumero.addValueChangeListener(e -> applyFilters());
        filtroSerie.addValueChangeListener(e -> applyFilters());
        filtroCliente.addValueChangeListener(e -> applyFilters());
        filtroEstado.addValueChangeListener(e -> applyFilters());
        filtroTipoNota.addValueChangeListener(e -> applyFilters());
        
        Button limpiarFiltros = new Button("Limpiar", e -> {
            filtroNumero.clear();
            filtroSerie.clear();
            filtroCliente.clear();
            filtroEstado.clear();
            filtroTipoNota.clear();
            refreshGrid();
        });
        limpiarFiltros.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        // Crear layout para estado y botón limpiar juntos
        HorizontalLayout estadoYLimpiar = new HorizontalLayout(filtroTipoNota, limpiarFiltros);
        estadoYLimpiar.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);
        estadoYLimpiar.setSpacing(true);
        estadoYLimpiar.getStyle().set("gap", "10px");
        
        HorizontalLayout filtros = new HorizontalLayout(filtroNumero, filtroSerie, filtroCliente, filtroEstado, estadoYLimpiar);
        filtros.addClassName("filter-layout");
        return filtros;
    }

    private Component createEditorLayout() {
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setWidth("45%");
        editorDiv.setClassName("editor-layout");
        
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        
        formLayout.add(
            serie, numeroNota, fechaEmision, cliente, empresa,
            documentoReferenciaTipo, documentoReferenciaSerie, documentoReferenciaNumero, documentoReferenciaFecha,
            subtotal, igv, total, estado, tipoNota, tipoDocumento, moneda,
            motivo, observaciones
        );
        formLayout.setColspan(motivo, 2);
        formLayout.setColspan(observaciones, 2);
        
        // Configurar listeners de botones
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> anularNotaCredito());
        aplicar.addClickListener(event -> aplicarNotaCredito());

        // Estilos de botones
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        aplicar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
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
        buttonLayout2.add(aplicar, delete);
        
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
        
        // Referencias de documentos
        documentoReferenciaTipo.setItems("FACTURA", "BOLETA");
        
        estado.setItems("EMITIDA", "APLICADA", "ANULADA");
        estado.setValue("EMITIDA");
        
        tipoNota.setItems("DEVOLUCION", "DESCUENTO", "ANULACION", "ERROR_FACTURACION");
        tipoNota.setValue("DEVOLUCION");
        
        tipoDocumento.setItems("NOTA_CREDITO", "NOTA_CREDITO_ELECTRONICA");
        tipoDocumento.setValue("NOTA_CREDITO");
        
        moneda.setItems("PEN", "USD");
        moneda.setValue("PEN");
        
        serie.setValue("NC01");
        serie.setRequiredIndicatorVisible(true);
        motivo.setRequiredIndicatorVisible(true);
        
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
    }
    
    private void calculateTotals() {
        if (subtotal.getValue() != null) {
            BigDecimal subtotalValue = subtotal.getValue();
            BigDecimal igvValue = subtotalValue.multiply(new BigDecimal("0.18"));
            BigDecimal totalValue = subtotalValue.add(igvValue);
            
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
                .bind(NotaCredito::getFechaEmision, NotaCredito::setFechaEmision);
                
        binder.forField(documentoReferenciaFecha)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(NotaCredito::getDocumentoReferenciaFecha, NotaCredito::setDocumentoReferenciaFecha);
        
        // Bind other fields
        binder.forField(serie).bind(NotaCredito::getSerie, NotaCredito::setSerie);
        binder.forField(numeroNota).bind(NotaCredito::getNumeroNota, NotaCredito::setNumeroNota);
        binder.forField(cliente).bind(NotaCredito::getCliente, NotaCredito::setCliente);
        binder.forField(empresa).bind(NotaCredito::getEmpresa, NotaCredito::setEmpresa);
        binder.forField(documentoReferenciaTipo).bind(NotaCredito::getDocumentoReferenciaTipo, NotaCredito::setDocumentoReferenciaTipo);
        binder.forField(documentoReferenciaSerie).bind(NotaCredito::getDocumentoReferenciaSerie, NotaCredito::setDocumentoReferenciaSerie);
        binder.forField(documentoReferenciaNumero).bind(NotaCredito::getDocumentoReferenciaNumero, NotaCredito::setDocumentoReferenciaNumero);
        binder.forField(subtotal).bind(NotaCredito::getSubtotal, NotaCredito::setSubtotal);
        binder.forField(igv).bind(NotaCredito::getIgv, NotaCredito::setIgv);
        binder.forField(total).bind(NotaCredito::getTotal, NotaCredito::setTotal);
        binder.forField(estado).bind(NotaCredito::getEstado, NotaCredito::setEstado);
        binder.forField(tipoNota).bind(NotaCredito::getTipoNota, NotaCredito::setTipoNota);
        binder.forField(tipoDocumento).bind(NotaCredito::getTipoDocumento, NotaCredito::setTipoDocumento);
        binder.forField(moneda).bind(NotaCredito::getMoneda, NotaCredito::setMoneda);
        binder.forField(motivo).bind(NotaCredito::getMotivo, NotaCredito::setMotivo);
        binder.forField(observaciones).bind(NotaCredito::getObservaciones, NotaCredito::setObservaciones);
    }

    private void refreshGrid() {
        gridNotasCredito.setItems(notaCreditoService.findAll());
    }

    private void applyFilters() {
        String numero = filtroNumero.getValue();
        String serieText = filtroSerie.getValue();
        String clienteNombre = filtroCliente.getValue();
        String estadoFiltro = filtroEstado.getValue();
        String tipoNotaFiltro = filtroTipoNota.getValue();
        
        gridNotasCredito.setItems(notaCreditoService.buscarConFiltros(numero, serieText, clienteNombre, estadoFiltro, tipoNotaFiltro, null, null, null));
    }

    private void save() {
        try {
            if (this.notaCredito == null) {
                this.notaCredito = new NotaCredito();
            }
            if (binder.writeBeanIfValid(this.notaCredito)) {
                notaCreditoService.save(this.notaCredito);
                clearForm();
                refreshGrid();
                Notification.show("Nota de crédito guardada correctamente.");
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

    private void anularNotaCredito() {
        if (notaCredito != null && notaCredito.getId() != null) {
            try {
                notaCreditoService.anularNotaCredito(notaCredito.getId(), observaciones.getValue());
                clearForm();
                refreshGrid();
                Notification.show("Nota de crédito anulada correctamente.");
            } catch (Exception e) {
                Notification.show("Error al anular la nota de crédito: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una nota de crédito para anular.");
        }
    }

    private void aplicarNotaCredito() {
        if (notaCredito != null && notaCredito.getId() != null) {
            try {
                notaCreditoService.aplicarNotaCredito(notaCredito.getId());
                clearForm();
                refreshGrid();
                Notification.show("Nota de crédito aplicada correctamente.");
            } catch (Exception e) {
                Notification.show("Error al aplicar la nota de crédito: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una nota de crédito para aplicar.");
        }
    }

    private void clearForm() {
        this.notaCredito = new NotaCredito();
        binder.readBean(this.notaCredito);
        estado.setValue("EMITIDA");
        tipoNota.setValue("DEVOLUCION");
        tipoDocumento.setValue("NOTA_CREDITO");
        moneda.setValue("PEN");
        serie.setValue("NC01");
        save.setText("Guardar");
    }

    private void editNotaCredito(NotaCredito notaCredito) {
        if (notaCredito == null) {
            clearForm();
        } else {
            this.notaCredito = notaCredito;
            binder.readBean(this.notaCredito);
            save.setText("Actualizar");
        }
    }
}
