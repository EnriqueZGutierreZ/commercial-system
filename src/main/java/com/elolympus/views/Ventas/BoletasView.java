package com.elolympus.views.Ventas;

import com.elolympus.data.Ventas.Boleta;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.services.BoletaService;
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

@PageTitle("Boletas")
@Route(value = "boletas", layout = MainLayout.class)
@PermitAll
public class BoletasView extends Div {

    private final BoletaService boletaService;
    private final PersonaService personaService;
    private final EmpresaService empresaService;
    private Boleta boleta;
    private BeanValidationBinder<Boleta> binder;

    // Componentes UI
    private Grid<Boleta> gridBoletas = new Grid<>(Boleta.class, false);
    private final TextField numeroBoleta = new TextField("Número Boleta");
    private final DatePicker fechaEmision = new DatePicker("Fecha Emisión");
    private final DatePicker fechaVencimiento = new DatePicker("Fecha Vencimiento");
    private final ComboBox<Persona> cliente = new ComboBox<>("Cliente");
    private final ComboBox<Empresa> empresa = new ComboBox<>("Empresa");
    private final BigDecimalField subtotal = new BigDecimalField("Subtotal");
    private final BigDecimalField igv = new BigDecimalField("IGV (18%)");
    private final BigDecimalField total = new BigDecimalField("Total");
    private final ComboBox<String> estado = new ComboBox<>("Estado");
    private final ComboBox<String> formaPago = new ComboBox<>("Forma de Pago");
    private final DatePicker fechaPago = new DatePicker("Fecha Pago");
    private final TextArea observaciones = new TextArea("Observaciones");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Anular");
    private final Button marcarPagada = new Button("Marcar como Pagada");

    private final FormLayout formLayout = new FormLayout();

    // Filtros
    private final TextField filtroNumero = new TextField("Filtrar por número");
    private final TextField filtroCliente = new TextField("Filtrar por cliente");
    private final ComboBox<String> filtroEstado = new ComboBox<>("Filtrar por estado");

    @Autowired
    public BoletasView(BoletaService boletaService, PersonaService personaService, EmpresaService empresaService) {
        this.boletaService = boletaService;
        this.personaService = personaService;
        this.empresaService = empresaService;
        
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Boleta.class);
            // Don't auto-bind fields - we'll bind them manually in setupForm()
            
        } catch (Exception e) {
            System.out.println("ERROR en BoletasView: " + e.getMessage());
        }
        
        addClassName("boletas-view");
        setSizeFull();
        setupGrid();
        setupForm();

        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private void setupGrid() {
        gridBoletas = new Grid<>(Boleta.class, false);
        gridBoletas.setClassName("grilla");
        gridBoletas.setHeight("86%");
        
        gridBoletas.addColumn(Boleta::getNumeroBoleta).setHeader("Número").setAutoWidth(true);
        gridBoletas.addColumn(Boleta::getFechaEmision).setHeader("Fecha Emisión").setAutoWidth(true);
        gridBoletas.addColumn(boleta -> boleta.getCliente() != null ? 
            boleta.getCliente().getNombres() + " " + boleta.getCliente().getApellidos() : "")
            .setHeader("Cliente").setAutoWidth(true);
        gridBoletas.addColumn(Boleta::getTotal).setHeader("Total").setAutoWidth(true);
        
        // Columna de estado con colores
        gridBoletas.addColumn(new ComponentRenderer<>(boleta -> {
            com.vaadin.flow.component.html.Span span = new com.vaadin.flow.component.html.Span(boleta.getEstado());
            switch (boleta.getEstado()) {
                case "PENDIENTE":
                    span.getElement().getThemeList().add("badge contrast");
                    break;
                case "PAGADA":
                    span.getElement().getThemeList().add("badge success");
                    break;
                case "ANULADA":
                    span.getElement().getThemeList().add("badge error");
                    break;
            }
            return span;
        })).setHeader("Estado").setAutoWidth(true);
        
        gridBoletas.addColumn(Boleta::getFormaPago).setHeader("Forma Pago").setAutoWidth(true);
        
        gridBoletas.getColumns().forEach(col -> col.setAutoWidth(true));
        gridBoletas.asSingleSelect().addValueChangeListener(event -> editBoleta(event.getValue()));
    }

    private Component createGridLayout() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.addClassName("grid-container");
        
        // Crear barra de filtros
        HorizontalLayout filtros = createFiltersLayout();
        
        gridContainer.add(new H3("Boletas"), filtros, gridBoletas);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private HorizontalLayout createFiltersLayout() {
        // Configurar filtros
        filtroEstado.setItems("", "PENDIENTE", "PAGADA", "ANULADA");
        filtroEstado.setPlaceholder("Todos los estados");
        
        // Listeners para filtros
        filtroNumero.addValueChangeListener(e -> applyFilters());
        filtroCliente.addValueChangeListener(e -> applyFilters());
        filtroEstado.addValueChangeListener(e -> applyFilters());
        
        Button limpiarFiltros = new Button("Limpiar", e -> {
            filtroNumero.clear();
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
        
        HorizontalLayout filtros = new HorizontalLayout(filtroNumero, filtroCliente, estadoYLimpiar);
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
            numeroBoleta, fechaEmision, fechaVencimiento, 
            cliente, empresa, subtotal, igv, total, 
            estado, formaPago, fechaPago, observaciones
        );
        
        // Configurar listeners de botones
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> anularBoleta());
        marcarPagada.addClickListener(event -> marcarComoPagada());

        // Estilos de botones
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        marcarPagada.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
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
        buttonLayout2.add(marcarPagada, delete);
        
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
        
        estado.setItems("PENDIENTE", "PAGADA", "ANULADA");
        estado.setValue("PENDIENTE");
        
        formaPago.setItems("EFECTIVO", "TARJETA", "TRANSFERENCIA", "CHEQUE");
        
        // Campos de solo lectura
        total.setReadOnly(true);
        igv.setReadOnly(true);
        
        // Auto-cálculo de IGV y total
        setupAutoCalculation();
        
        // Bind fields manually
        bindFields();
    }

    private void setupAutoCalculation() {
        subtotal.addValueChangeListener(e -> {
            if (subtotal.getValue() != null) {
                BigDecimal subtotalValue = subtotal.getValue();
                BigDecimal igvValue = subtotalValue.multiply(new BigDecimal("0.18"));
                BigDecimal totalValue = subtotalValue.add(igvValue);
                
                igv.setValue(igvValue);
                total.setValue(totalValue);
            }
        });
    }

    private void bindFields() {
        binder.forField(fechaEmision)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Boleta::getFechaEmision, Boleta::setFechaEmision);
                
        binder.forField(fechaVencimiento)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Boleta::getFechaVencimiento, Boleta::setFechaVencimiento);
                
        binder.forField(fechaPago)
                .withConverter(
                        localDate -> localDate == null ? null : Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Boleta::getFechaPago, Boleta::setFechaPago);
        
        // Bind other fields
        binder.forField(numeroBoleta).bind(Boleta::getNumeroBoleta, Boleta::setNumeroBoleta);
        binder.forField(cliente).bind(Boleta::getCliente, Boleta::setCliente);
        binder.forField(empresa).bind(Boleta::getEmpresa, Boleta::setEmpresa);
        binder.forField(subtotal).bind(Boleta::getSubtotal, Boleta::setSubtotal);
        binder.forField(igv).bind(Boleta::getIgv, Boleta::setIgv);
        binder.forField(total).bind(Boleta::getTotal, Boleta::setTotal);
        binder.forField(estado).bind(Boleta::getEstado, Boleta::setEstado);
        binder.forField(formaPago).bind(Boleta::getFormaPago, Boleta::setFormaPago);
        binder.forField(observaciones).bind(Boleta::getObservaciones, Boleta::setObservaciones);
    }

    private void refreshGrid() {
        gridBoletas.setItems(boletaService.findAll());
    }

    private void applyFilters() {
        String numero = filtroNumero.getValue();
        String clienteNombre = filtroCliente.getValue();
        String estadoFiltro = filtroEstado.getValue();
        
        gridBoletas.setItems(boletaService.buscarConFiltros(numero, clienteNombre, estadoFiltro, null, null));
    }

    private void save() {
        try {
            if (this.boleta == null) {
                this.boleta = new Boleta();
            }
            if (binder.writeBeanIfValid(this.boleta)) {
                boletaService.save(this.boleta);
                clearForm();
                refreshGrid();
                Notification.show("Boleta guardada correctamente.");
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

    private void anularBoleta() {
        if (boleta != null && boleta.getId() != null) {
            try {
                boletaService.anularBoleta(boleta.getId(), observaciones.getValue());
                clearForm();
                refreshGrid();
                Notification.show("Boleta anulada correctamente.");
            } catch (Exception e) {
                Notification.show("Error al anular la boleta: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una boleta para anular.");
        }
    }

    private void marcarComoPagada() {
        if (boleta != null && boleta.getId() != null && formaPago.getValue() != null) {
            try {
                boletaService.marcarComoPagada(boleta.getId(), formaPago.getValue());
                clearForm();
                refreshGrid();
                Notification.show("Boleta marcada como pagada correctamente.");
            } catch (Exception e) {
                Notification.show("Error al marcar como pagada: " + e.getMessage());
            }
        } else {
            Notification.show("Seleccione una boleta y la forma de pago para marcar como pagada.");
        }
    }

    private void clearForm() {
        this.boleta = new Boleta();
        binder.readBean(this.boleta);
        estado.setValue("PENDIENTE");
        save.setText("Guardar");
    }

    private void editBoleta(Boleta boleta) {
        if (boleta == null) {
            clearForm();
        } else {
            this.boleta = boleta;
            binder.readBean(this.boleta);
            save.setText("Actualizar");
        }
    }
}
