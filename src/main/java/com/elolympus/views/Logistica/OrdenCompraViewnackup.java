package com.elolympus.views.Logistica;

import com.elolympus.component.DataGrid;
import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.data.Logistica.OrdenCompraDet;
import com.elolympus.services.services.OrdenCompraService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Orden de Comprabackuppp")
@Route(value = "ordenComprabnacluaaaa", layout = MainLayout.class)
@PermitAll
public class OrdenCompraViewnackup extends Dialog {

    private final OrdenCompraService ordenCompraService;
    private OrdenCompra ordenCompra;
    private OrdenCompraDet ordenCompraDet;
    private final BeanValidationBinder<OrdenCompra> binder= new BeanValidationBinder<>(OrdenCompra.class);
    private final BeanValidationBinder<OrdenCompraDet> binderDet= new BeanValidationBinder<>(OrdenCompraDet.class);

    //componentes UI OrdenCompra
    private final IntegerField almacenEntrega = new IntegerField("Almacen Entrega");
    private final IntegerField numeroProveedor = new IntegerField("Numero Proveedor");
    private final IntegerField direccionProveedor = new IntegerField("Direccion Proveedor");
    private final DatePicker fecha = new DatePicker("Fecha");
    private final DatePicker fechaEntrega = new DatePicker("Fecha Entrega");
    private final IntegerField formaPago = new IntegerField("Forma Pago");
    private final IntegerField moneda = new IntegerField("Moneda");
    private final IntegerField impuesto = new IntegerField("Impuesto");
    private final BigDecimalField total = new BigDecimalField("Total");
    private final TextField observaciones = new TextField("Observaciones");
    private final BigDecimalField totalCobrado = new BigDecimalField("Total Cobrado");
    private final BigDecimalField tipoCambio = new BigDecimalField("Tipo Cambio");
    private final IntegerField diasCredito = new IntegerField("Dias Credito");
    private final IntegerField sucursal = new IntegerField("Sucursal");
    private final Checkbox impuesto_incluido = new Checkbox("Impuesto Incluido");
    private final TextField documento_pago = new TextField("Documento Pago");


    private final Grid<OrdenCompra> grid = new Grid<>(OrdenCompra.class);
    private final DataGrid<OrdenCompra> dataGrid = new DataGrid<>();
    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar");
    private final FormLayout form = new FormLayout();


    private final VerticalLayout panel = new VerticalLayout();
    private final HorizontalLayout panelFiltro     = new HorizontalLayout();
    private final HorizontalLayout panelButton     = new HorizontalLayout();

    private final TextField Sucursal = new TextField("Documento Pago");
    private final DatePicker FechaInicio     = new DatePicker("Fecha Inicio");
    private final DatePicker FechaFin        = new DatePicker("Fecha Fin");

    //constructor
    public OrdenCompraViewnackup(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;

        initDataGrid();
        this.panelFiltro.add(Sucursal,FechaInicio,FechaFin);
        this.form.add(almacenEntrega,numeroProveedor,direccionProveedor,fecha,fechaEntrega,
                formaPago,moneda,impuesto,observaciones,tipoCambio,diasCredito,sucursal,
                impuesto_incluido,documento_pago,totalCobrado,total);
        this.panelButton.add(save,delete,cancel);
        this.panel.add(form,dataGrid,panelButton);
        this.add(panel);
        init();
    }
    private void init(){
        addClassName("orden-compra-view");
        setSizeFull();
        //setupGrid();
        configureForm();
        SplitLayout layout = new SplitLayout(createGridLayout(),createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrids();
    }

    private void initFiltros(){

    }

    private void initDataGrid(){
        dataGrid.addColumn(orden -> 
                orden.getAlmacenEntrega() != null ? orden.getAlmacenEntrega().getDescripcion() : "")
                .setHeader("Almacén Entrega")
                .setAutoWidth(true);
        dataGrid.addColumn(orden -> 
                orden.getProveedor() != null ? orden.getProveedor().getNombreCompleto() : "")
                .setHeader("Proveedor")
                .setAutoWidth(true);
        dataGrid.addColumn(orden -> 
                orden.getDireccionProveedor() != null ? orden.getDireccionProveedor().getDescripcion() : "")
                .setHeader("Dirección Proveedor")
                .setAutoWidth(true);
        dataGrid.addColumn(OrdenCompra::getFecha,"Fecha");
        dataGrid.addColumn(OrdenCompra::getFechaEntrega,"Fecha Entrega");
        dataGrid.addColumn(OrdenCompra::getMoneda,"Moneda");
        dataGrid.addColumn(OrdenCompra::getImpuesto,"Impuesto");
        dataGrid.addColumn(OrdenCompra::getObservaciones,"Observaciones");
        dataGrid.addColumn(OrdenCompra::getTotalCobrado,"Total Cobrado");
        dataGrid.addColumn(OrdenCompra::getTipoCambio,"Tipo Cambio");
        dataGrid.addColumn(OrdenCompra::getDiasCredito,"Credias/Dias");
        dataGrid.addColumn(orden -> 
                orden.getSucursal() != null ? orden.getSucursal().getDescripcion() : "")
                .setHeader("Sucursal")
                .setAutoWidth(true);
        dataGrid.addColumn(OrdenCompra::getImpuesto_incluido,"Impuesto Incluido");
        dataGrid.addColumn(OrdenCompra::getDocumento_pago,"Documento Pago");
        dataGrid.addColumn(OrdenCompra::getTotal,"Total");

        dataGrid.asSingleSelect().addValueChangeListener(evt -> editOrdenCompra(evt.getValue()));
    }

    private void refreshGrids() {
        grid.setItems(ordenCompraService.findAll());
    }

    private void setupGrid() {
        grid.setClassName("grilla");
        grid.setColumns("almacenEntrega", "numeroProveedor", "direccionProveedor", "fecha", "fechaEntrega",
                "formaPago", "moneda", "impuesto", "total", "observaciones", "totalCobrado", "tipoCambio",
                "diasCredito", "sucursal", "impuesto_incluido", "documento_pago");
        grid.asSingleSelect().addValueChangeListener(evt -> editOrdenCompra(evt.getValue()));
    }

    private Component createEditorLayout(){
        Div editorCompraDiv = new Div();
        editorCompraDiv.setHeightFull();
        editorCompraDiv.setWidth("30%");
        editorCompraDiv.setClassName("editor-layout");

        // Etiqueta para la sección de cabecera
        Div headerLabel = new Div();
        headerLabel.setText("Datos de Cabecera");
        headerLabel.addClassNames("header-label"); // Añade tu clase de CSS para estilizar la etiqueta

        binder.bindInstanceFields(this);
        setupButtons(editorCompraDiv);
        return editorCompraDiv;
    }
    private void setupButtons(Div div){
        //configuración de botones
        save.addClickListener(event -> saveOrdenCompra());
        cancel.addClickListener(event -> this.close());
        delete.addClickListener(event -> deleteOrdenCompra());

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttons = new HorizontalLayout(save, cancel, delete);
        buttons.setClassName("button-layout");
        div.add(buttons);
    }

    private Component createGridLayout(){
        HorizontalLayout busquedaDiv = new HorizontalLayout();
        busquedaDiv.addClassName("tophl");
        Div gridContainer = new Div();
        gridContainer.addClassName("grid-wrapper");
        gridContainer.add(busquedaDiv,grid);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private void configureForm(){
//        binderDet.forField(producto).bind(OrdenCompraDet::getProducto, OrdenCompraDet::setProducto);
//        binderDet.forField(cantidad).bind(OrdenCompraDet::getCantidad, OrdenCompraDet::setCantidad);
//        binderDet.forField(precioUnitario).bind(OrdenCompraDet::getPrecioUnitario, OrdenCompraDet::setPrecioUnitario);
//        binderDet.forField(totaldet).bind(OrdenCompraDet::getTotaldet, OrdenCompraDet::setTotaldet);
//        binderDet.forField(descuento).bind(OrdenCompraDet::getDescuento, OrdenCompraDet::setDescuento);
//        binderDet.forField(almacen).bind(OrdenCompraDet::getAlmacen, OrdenCompraDet::setAlmacen);
//        binderDet.forField(cantidadTg).bind(OrdenCompraDet::getCantidadTg, OrdenCompraDet::setCantidadTg);
//        binderDet.forField(lote).bind(OrdenCompraDet::getLote, OrdenCompraDet::setLote);
//        binderDet.forField(fechaVencimiento)
//                .withConverter(
//                        localDate -> localDate == null ? null : java.sql.Date.valueOf(localDate),
//                        date -> date == null ? null : date.toLocalDate(),
//                        "Fecha inválida"
//                ).bind(OrdenCompraDet::getFechaVencimiento, OrdenCompraDet::setFechaVencimiento);
//        binderDet.forField(cantidadUsada).bind(OrdenCompraDet::getCantidadUsada, OrdenCompraDet::setCantidadUsada);
//        binderDet.forField(cantidadFraccion).bind(OrdenCompraDet::getCantidadFraccion, OrdenCompraDet::setCantidadFraccion);
    }

    private void saveOrdenCompra(){
        try {
            if (ordenCompra == null || ordenCompraDet == null) {
                ordenCompra = new OrdenCompra();
                ordenCompraDet = new OrdenCompraDet();
            }
            if (binder.writeBeanIfValid(ordenCompra)) {
                ordenCompraService.update(ordenCompra);
                ordenCompraDet.setOrdenCompra(ordenCompra);
                refreshGrids();
                clearForm();
                Notification.show("Orden de Compra guardada correctamente");
            } else {
                Notification.show("No se pudo guardar la Orden de Compra");
            }
            clearForm();
            refreshGrids();
            UI.getCurrent().navigate(OrdenCompraViewnackup.class);
        }catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteOrdenCompra(){
        if (ordenCompra != null) {
            ordenCompraService.delete(ordenCompra);
            refreshGrids();
            clearForm();
            Notification.show("Orden de Compra eliminada correctamente");
        } else {
            Notification.show("No se pudo eliminar la Orden de Compra");
        }
    }

    private void clearForm(){
        ordenCompra = new OrdenCompra();
        ordenCompraDet = new OrdenCompraDet();
        binder.readBean(ordenCompra);
        binderDet.readBean(ordenCompraDet);
        save.setText("Guardar");
    }

    private void editOrdenCompra(OrdenCompra ordenCompra){
        if (ordenCompra == null) {
            clearForm();
        } else {
            this.ordenCompra = ordenCompra;
            binder.readBean(ordenCompra);
            save.setText("Actualizar");
        }
    }

    private void editOrdenCompraDet(OrdenCompraDet ordenCompraDet){
        if (ordenCompraDet == null) {
            clearForm();
        } else {
            this.ordenCompraDet = ordenCompraDet;
            binderDet.readBean(ordenCompraDet);
            save.setText("Actualizar");
        }
    }

}
