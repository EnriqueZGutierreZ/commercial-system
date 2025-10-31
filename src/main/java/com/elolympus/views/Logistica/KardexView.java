package com.elolympus.views.Logistica;


import com.elolympus.data.Almacen.Kardex;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.services.services.KardexService;
import com.elolympus.services.services.ProductoService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.sql.Timestamp;

@PageTitle("Kardex")
@Route(value = "kardex/:KardexID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class KardexView extends Div {


    private final KardexService kardexService;
    private final ProductoService productoService;
    private Kardex kardex;
    private BeanValidationBinder<Kardex> binder;

    //Componentes UI
    private Grid<Kardex> gridkardex = new Grid<>(Kardex.class, false);
    private final IntegerField ordenId = new IntegerField("Orden ID");
    private final DateTimePicker fecha = new DateTimePicker ("Fecha");
    private final DatePicker fechaOrden = new DatePicker("Fecha Orden");
    private final TextField movimiento = new TextField("Movimiento");
    private final IntegerField almacen = new IntegerField("Almacén");
    private final TextField origen = new TextField("Origen");
    private final TextField destino = new TextField("Destino");
    private final BigDecimalField precioCosto = new BigDecimalField("Precio Costo");
    private final BigDecimalField precioVenta = new BigDecimalField("Precio Venta");
    private final BigDecimalField stockAnterior = new BigDecimalField("Stock Anterior");
    private final BigDecimalField ingreso = new BigDecimalField("Ingreso");
    private final BigDecimalField salida = new BigDecimalField("Salida");
    private final BigDecimalField stock = new BigDecimalField("Stock");
    private final ComboBox<Producto> producto = new ComboBox<>("Producto");
    private final DatePicker fechaVencimiento = new DatePicker("Fecha Vencimiento");
    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar");


    private final FormLayout formLayout = new FormLayout();


    @Autowired
    public KardexView(KardexService kardexService, ProductoService productoService) {
        this.kardexService = kardexService;
        this.productoService = productoService;
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Kardex.class);
            setupForm();
            binder.bindInstanceFields(this);

        }catch (Exception e){
            System.out.println("ERRORRRR: " + e.getMessage());
        }
        addClassName("kardex-view");
        setSizeFull();
        setupGrid();
        setupForm();

        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private void setupGrid() {
        gridkardex= new Grid<>(Kardex.class, false);
        gridkardex.setClassName("grilla");
        gridkardex.setHeight("86%");
        gridkardex.setColumns("ordenId", "fecha","fechaOrden", "movimiento", "almacen", "origen", "destino", "precioCosto", "precioVenta", "stockAnterior", "ingreso", "salida", "stock", "fechaVencimiento");
        
        // Add producto column with custom renderer
        gridkardex.addColumn(kardex -> kardex.getProducto() != null ? kardex.getProducto().getNombre() : "")
                .setHeader("Producto")
                .setKey("producto")
                .setAutoWidth(true);
        
        gridkardex.getColumns().forEach(col -> col.setAutoWidth(true));
        gridkardex.asSingleSelect().addValueChangeListener(event -> editKardex(event.getValue()));
    }
    private Component createEditorLayout() {
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setWidth("30%");
        editorDiv.setClassName("editor-layout");
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        formLayout.add(ordenId, fecha,fechaOrden, movimiento, almacen, origen, destino, precioCosto, precioVenta, stockAnterior, ingreso, salida, stock, producto, fechaVencimiento);
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> delete());

        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.bindInstanceFields(this);
        div.add(formLayout);
        createButtonLayout(editorDiv);
        return editorDiv;
    }
    private void createButtonLayout(Div div){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(save, cancel, delete);
        div.add(buttonLayout);
    }
    private Component createGridLayout() {
        Div gridContainer = new Div();
        gridContainer.addClassName("grid-container");
        gridContainer.add(gridkardex);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private void refreshGrid() {
        gridkardex.setItems(kardexService.findAll());
    }

    private void setupForm() {
        // Configure producto ComboBox
        producto.setItems(productoService.findAll());
        producto.setItemLabelGenerator(Producto::getNombre);
        producto.setPlaceholder("Seleccione un producto");
        
        binder.forField(fecha)
                .withConverter(
                        localDateTime -> localDateTime == null ? null : Timestamp.valueOf(localDateTime),
                        timestamp -> timestamp == null ? null : timestamp.toLocalDateTime(),
                        "Fecha y hora inválidas"
                )
                .bind(Kardex::getFecha, Kardex::setFecha);
        binder.forField(fechaOrden)
                .withConverter(
                        localDate -> localDate == null ? null : java.sql.Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Kardex::getFechaOrden, Kardex::setFechaOrden);
        binder.forField(fechaVencimiento)
                .withConverter(
                        localDate -> localDate == null ? null : java.sql.Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(Kardex::getFechaVencimiento, Kardex::setFechaVencimiento);
    }

    private void save(){
        try {
            if (this.kardex == null) {
                this.kardex = new Kardex();
            }
            if (binder.writeBeanIfValid(this.kardex)) {
                Notification.show(this.kardex.toString());
                kardexService.update(this.kardex);
                clearForm();
                refreshGrid();
                Notification.show("Kardex guardado correctamente.");
            }else{
                Notification.show("Error al guardar los datos. Por favor, revise los campos e intente nuevamente.");
            }
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(KardexView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

    }

    private void delete(){
        if(kardex != null) {
            kardexService.delete(kardex);
            clearForm();
            refreshGrid();
            Notification.show("Kardex eliminado correctamente.");
        }else {
            Notification.show("Seleccione un Kardex e intente nuevamente.");
        }
    }

    private void clearForm(){
        this.kardex = new Kardex();
        this.kardex.setFecha(null);
        this.kardex.setFechaOrden(null);
        this.kardex.setFechaVencimiento(null);
        binder.readBean(this.kardex);
        save.setText("Guardar");
    }
    private void editKardex(Kardex kardex){
        if (kardex == null) {
            clearForm();
        } else {
            this.kardex = kardex;
            binder.readBean(this.kardex);
            setupForm();
            save.setText("Actualizar"); // Cambiar el texto del botón a "Actualizar"
        }
    }
}
