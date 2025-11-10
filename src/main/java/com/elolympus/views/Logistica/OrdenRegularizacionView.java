package com.elolympus.views.Logistica;

import com.elolympus.data.Almacen.OrdenRegularizacion;
import com.elolympus.data.Almacen.OrdenRegularizacionDet;
import com.elolympus.services.services.OrdenRegDetService;
import com.elolympus.services.services.OrdenRegService;
import com.elolympus.services.services.ProductoService;
import com.elolympus.services.services.AlmacenService;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;


@PageTitle("Orden de Regularización")
@Route(value = "ordenRegularizacion", layout = MainLayout.class)
@PermitAll
public class OrdenRegularizacionView extends Div {

    private final OrdenRegService ordenRegService;
    private final OrdenRegDetService ordenRegDetService;
    private final ProductoService productoService;
    private final AlmacenService almacenService;
    private OrdenRegularizacion ordenRegularizacion;
    private OrdenRegularizacionDet ordenRegularizacionDet;
    private final BeanValidationBinder<OrdenRegularizacion> binder= new BeanValidationBinder<>(OrdenRegularizacion.class);
    private final BeanValidationBinder<OrdenRegularizacionDet> binderDet= new BeanValidationBinder<>(OrdenRegularizacionDet.class);

    //Componentes UI Orden de Regularización
    private final IntegerField numero = new IntegerField("Número");
    private final DatePicker fecha = new DatePicker("Fecha");
    private final ComboBox<Almacen> almacen = new ComboBox<>("Almacén");
    private final TextField movimiento = new TextField("Movimiento");
    private final TextField observaciones = new TextField("Observaciones");

    //Componentes UI Detalles de Orden de Regularización
    private final ComboBox<Producto> producto = new ComboBox<>("Producto");
    private final BigDecimalField cantidad = new BigDecimalField("Cantidad");
    private final BigDecimalField cantidadFraccion = new BigDecimalField("Cantidad Fracción");
    private final DatePicker fechaVencimiento = new DatePicker("Fecha Vencimiento");

    private final Grid<OrdenRegularizacion> gridOrdenRegularizacion = new Grid<>(OrdenRegularizacion.class, false);
    private final Grid<OrdenRegularizacionDet> detailGrid = new Grid<>(OrdenRegularizacionDet.class);
    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar");
    //private final FormLayout formLayout = new FormLayout();
    private final FormLayout headerFormLayout = new FormLayout();
    private final FormLayout detailFormLayout = new FormLayout();
    public OrdenRegularizacionView(OrdenRegService ordenRegService, OrdenRegDetService ordenRegDetService, ProductoService productoService, AlmacenService almacenService) {
        this.ordenRegService = ordenRegService;
        this.ordenRegDetService = ordenRegDetService;
        this.productoService = productoService;
        this.almacenService = almacenService;
        init();
    }
    private void init() {
        addClassName("orden-regularizacion-view");
        setSizeFull();
        setupGrid();
        configureForms();
        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrids();
    }

    private void refreshGrids() {
        gridOrdenRegularizacion.setItems(ordenRegService.findAll());
    }
    
    private void updateList(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            gridOrdenRegularizacion.setItems(ordenRegService.findAll());
        } else {
            gridOrdenRegularizacion.setItems(
                ordenRegService.findAll().stream()
                    .filter(orden -> 
                        (orden.getNumero() != null && orden.getNumero().toString().toLowerCase().contains(searchText.toLowerCase())) ||
                        (orden.getMovimiento() != null && orden.getMovimiento().toLowerCase().contains(searchText.toLowerCase())) ||
                        (orden.getAlmacen() != null && orden.getAlmacen().getDescripcion() != null && 
                         orden.getAlmacen().getDescripcion().toLowerCase().contains(searchText.toLowerCase())) ||
                        (orden.getObservaciones() != null && orden.getObservaciones().toLowerCase().contains(searchText.toLowerCase()))
                    )
                    .toList()
            );
        }
    }

    private void setupGrid() {
        gridOrdenRegularizacion.setClassName("grilla");
        gridOrdenRegularizacion.setColumns("numero", "fecha", "movimiento", "observaciones");
        gridOrdenRegularizacion.addColumn(ordenRegularizacion -> 
                ordenRegularizacion.getAlmacen() != null ? ordenRegularizacion.getAlmacen().getDescripcion() : "")
                .setHeader("Almacén")
                .setAutoWidth(true);
        gridOrdenRegularizacion.asSingleSelect().addValueChangeListener(event -> editOrdenRegularizacion(event.getValue()));
        gridOrdenRegularizacion.setItemDetailsRenderer(new ComponentRenderer<>(ordenRegularizacion -> {
            detailGrid.setItems(ordenRegDetService.findByOrdenRegularizacion(ordenRegularizacion)); // Suponiendo que este método devuelve los detalles relacionados.
            detailGrid.setColumns("cantidad", "cantidadFraccion", "fechaVencimiento");
            detailGrid.addColumn(ordenRegularizacionDet -> ordenRegularizacionDet.getProducto() != null ? ordenRegularizacionDet.getProducto().getNombre() : "")
                    .setHeader("Producto")
                    .setAutoWidth(true);
            detailGrid.asSingleSelect().addValueChangeListener(event -> editOrdenRegularizacionDet(event.getValue()));

            return detailGrid;
        }));
    }

    private Component createEditorLayout(){
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setWidth("30%");
        editorDiv.setClassName("editor-layout");

        // Etiqueta para la sección de cabecera
        Div headerLabel = new Div();
        headerLabel.setText("Datos de Cabecera");
        headerLabel.addClassNames("header-label"); // Añade tu clase de CSS para estilizar la etiqueta

        // Layout para los campos de cabecera
        headerFormLayout.addClassName("header-form-layout");
        headerFormLayout.add(numero, fecha, almacen, movimiento, observaciones);

        // Etiqueta para la sección de detalle
        Div detailLabel = new Div();
        detailLabel.setText("Detalles de la Orden");
        detailLabel.addClassNames("detail-label"); // Añade tu clase de CSS para estilizar la etiqueta

        // Layout para los campos de detalle
        detailFormLayout.addClassName("detail-form-layout");
        detailFormLayout.add(producto, cantidad, cantidadFraccion, fechaVencimiento);

        // Añadir los FormLayouts al Div principal
        editorDiv.add(headerLabel,headerFormLayout, detailLabel, detailFormLayout);

//        Div div = new Div();
//        div.setClassName("editor");
//        editorDiv.add(div);
//        formLayout.add(numero, fecha, almacen, movimiento, observaciones, producto, cantidad, cantidadFraccion, fechaVencimiento);

        binder.bindInstanceFields(this);
        //div.add(formLayout);
        setupButtons(editorDiv);
        return editorDiv;
    }
    private void setupButtons(Div div) {
        // Configuración de los botones
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> delete());

        // Añadir variantes de tema a los botones para mejorar la apariencia
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(save, cancel, delete);
        div.add(buttonLayout);
    }
    private Component createGridLayout() {
        HorizontalLayout busquedaDiv = new HorizontalLayout();
        busquedaDiv.addClassName("tophl");
        
        // Agregar componentes de búsqueda
        TextField searchField = new TextField("Buscar");
        searchField.setPlaceholder("Buscar por número, almacén o movimiento...");
        searchField.setPrefixComponent(new Icon(com.vaadin.flow.component.icon.VaadinIcon.SEARCH));
        searchField.addValueChangeListener(e -> updateList(e.getValue()));
        
        Button newButton = new Button("Nueva Orden");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(e -> {
            clearForm();
            ordenRegularizacion = new OrdenRegularizacion();
            binder.readBean(ordenRegularizacion);
        });
        
        busquedaDiv.add(searchField, newButton);
        busquedaDiv.setDefaultVerticalComponentAlignment(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);
        busquedaDiv.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.BETWEEN);
        busquedaDiv.setWidthFull();
        
        Div gridContainer = new Div();
        gridContainer.addClassName("grid-wrapper");
        gridContainer.add(busquedaDiv, gridOrdenRegularizacion);
        gridContainer.setSizeFull();
        gridOrdenRegularizacion.setSizeFull();
        return gridContainer;
    }

    private void configureForms(){
        binder.forField(fecha)
                .withConverter(
                        localDate -> localDate == null ? null : java.sql.Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(OrdenRegularizacion::getFecha, OrdenRegularizacion::setFecha);
        
        // Configurar ComboBox de almacén
        almacen.setItems(almacenService.findAll());
        almacen.setItemLabelGenerator(Almacen::getDescripcion);
        binder.forField(almacen)
                .bind(OrdenRegularizacion::getAlmacen, OrdenRegularizacion::setAlmacen);
                
        //binder de detalles
        // Load productos for the combo box
        producto.setItems(productoService.findActive());
        producto.setItemLabelGenerator(Producto::getNombre);
        
        binderDet.forField(producto)
                .bind(OrdenRegularizacionDet::getProducto, OrdenRegularizacionDet::setProducto);
        binderDet.forField(cantidad)
                .withConverter(
                        bigDecimal -> bigDecimal == null ? null : bigDecimal,
                        bigDecimal -> bigDecimal == null ? null : bigDecimal,
                        "Cantidad inválida"
                )
                .bind(OrdenRegularizacionDet::getCantidad, OrdenRegularizacionDet::setCantidad);
        binderDet.forField(cantidadFraccion)
                .withConverter(
                        bigDecimal -> bigDecimal == null ? null : bigDecimal,
                        bigDecimal -> bigDecimal == null ? null : bigDecimal,
                        "Cantidad inválida"
                )
                .bind(OrdenRegularizacionDet::getCantidadFraccion, OrdenRegularizacionDet::setCantidadFraccion);
        binderDet.forField(fechaVencimiento)
                .withConverter(
                        localDate -> localDate == null ? null : java.sql.Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                )
                .bind(OrdenRegularizacionDet::getFechaVencimiento, OrdenRegularizacionDet::setFechaVencimiento);
    }

    private void save(){
        try{
            if(ordenRegularizacion == null || ordenRegularizacionDet == null){
                ordenRegularizacion = new OrdenRegularizacion();
                ordenRegularizacionDet = new OrdenRegularizacionDet();
            }
            if (binder.writeBeanIfValid(ordenRegularizacion) && binderDet.writeBeanIfValid(ordenRegularizacionDet)) {
                ordenRegService.update(ordenRegularizacion);
                ordenRegularizacionDet.setOrdenRegularizacion(ordenRegularizacion);
                ordenRegDetService.update(ordenRegularizacionDet);
                clearForm();
                refreshGrids();
                Notification.show("Orden de Regularización guardada correctamente.");
            }else{
                Notification.show("Error al guardar la Orden de Regularización.");
            }
            clearForm();
            refreshGrids();
            UI.getCurrent().navigate(OrdenRegularizacionView.class);
        }catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    private void delete(){
        if(ordenRegularizacion != null){
            ordenRegService.delete(ordenRegularizacion);
            ordenRegDetService.delete(ordenRegularizacionDet);
            clearForm();
            refreshGrids();
            Notification.show("Orden de Regularización eliminada correctamente.");
        }else{
            Notification.show("Error al eliminar la Orden de Regularización.");
        }
    }
    private void clearForm(){
        ordenRegularizacion= new OrdenRegularizacion();
        ordenRegularizacionDet = new OrdenRegularizacionDet();
        ordenRegularizacion.setFecha(null);
        ordenRegularizacionDet.setFechaVencimiento(null);
        binder.readBean(ordenRegularizacion);
        binderDet.readBean(ordenRegularizacionDet);
        save.setText("Guardar");
    }
    private void editOrdenRegularizacion(OrdenRegularizacion ordenRegularizacion){
        if(ordenRegularizacion == null){
            clearForm();
        }else{
            this.ordenRegularizacion = ordenRegularizacion;
            binder.readBean(ordenRegularizacion);
            save.setText("Actualizar");
        }
    }
    private void editOrdenRegularizacionDet(OrdenRegularizacionDet ordenRegularizacionDet){
        if(ordenRegularizacionDet == null){
            clearForm();
        }else{
            this.ordenRegularizacionDet = ordenRegularizacionDet;
            binderDet.readBean(ordenRegularizacionDet);
            save.setText("Actualizar");
        }
    }

}
