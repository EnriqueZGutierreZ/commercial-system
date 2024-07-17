package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.data.Logistica.OrdenCompraDet;
import com.elolympus.services.services.OrdenCompraDetService;
import com.elolympus.services.services.OrdenCompraService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Orden de CompraBACKUP")
@Route(value = "ordenComprabackup", layout = MainLayout.class)
@PermitAll
public class OrdenCompraViewBACKUP extends Div {

    private final OrdenCompraService ordenCompraService;
    private final OrdenCompraDetService ordenCompraDetService;
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
    //componentes UI OrdenCompraDet
    private final IntegerField producto = new IntegerField("Producto");
    private final BigDecimalField cantidad = new BigDecimalField("Cantidad");
    private final BigDecimalField precioUnitario = new BigDecimalField("Precio Unitario");
    private final BigDecimalField totaldet = new BigDecimalField("Total");
    private final BigDecimalField descuento = new BigDecimalField("Descuento");
    private final IntegerField almacen = new IntegerField("Almacen");
    private final BigDecimalField cantidadTg = new BigDecimalField("Cantidad Tg");
    private final TextField lote = new TextField("Lote");
    private final DatePicker fechaVencimiento = new DatePicker("Fecha Vencimiento");
    private final BigDecimalField cantidadUsada = new BigDecimalField("Cantidad Usada");
    private final BigDecimalField cantidadFraccion = new BigDecimalField("Cantidad Fraccion");

    private final Grid<OrdenCompra> grid = new Grid<>(OrdenCompra.class);
    private final Grid<OrdenCompraDet> gridDet = new Grid<>(OrdenCompraDet.class);
    private final Button saveoc = new Button("Guardar");
    private final Button canceloc = new Button("Cancelar");
    private final Button deleteoc = new Button("Eliminar");
    private final FormLayout headerForm = new FormLayout();
    private final FormLayout detailForm = new FormLayout();

    //constructor
    public OrdenCompraViewBACKUP(OrdenCompraService ordenCompraService, OrdenCompraDetService ordenCompraDetService) {
        this.ordenCompraService = ordenCompraService;
        this.ordenCompraDetService = ordenCompraDetService;
        init();
    }
    private void init(){
        addClassName("orden-compra-view");
        setSizeFull();
        setupGrid();
        configureForm();
        SplitLayout layout = new SplitLayout(createGridLayout(),createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrids();
    }

    private void refreshGrids() {
        grid.setItems(ordenCompraService.findAll());
    }

    private void setupGrid() {
        grid.setClassName("grilla");
        grid.setColumns("almacenEntrega", "numeroProveedor", "direccionProveedor", "fecha", "fechaEntrega", "formaPago", "moneda", "impuesto", "total", "observaciones", "totalCobrado", "tipoCambio", "diasCredito", "sucursal", "impuesto_incluido", "documento_pago");
        grid.asSingleSelect().addValueChangeListener(evt -> editOrdenCompra(evt.getValue()));
        grid.setItemDetailsRenderer(new ComponentRenderer<>(ordenCompra->{
            gridDet.setItems(ordenCompraDetService.findByordenCompra(ordenCompra));
            gridDet.setColumns("producto", "cantidad", "precioUnitario", "totaldet", "descuento", "almacen", "cantidadTg", "lote", "fechaVencimiento", "cantidadUsada", "cantidadFraccion");
            gridDet.asSingleSelect().addValueChangeListener(event -> editOrdenCompraDet(event.getValue()));

            return gridDet;
        }));
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
        headerForm.addClassName("header-form-layout");
        headerForm.add(almacenEntrega, numeroProveedor, direccionProveedor, fecha, fechaEntrega, formaPago, moneda, impuesto, total, observaciones, totalCobrado, tipoCambio, diasCredito, sucursal, impuesto_incluido, documento_pago);

        // Etiqueta para la sección de detalle
        Div detailLabel = new Div();
        detailLabel.setText("Datos de Detalle");
        detailLabel.addClassNames("detail-label"); // Añade tu clase de CSS para estilizar la etiqueta
        detailForm.addClassName("detail-form-layout");
        detailForm.add(producto, cantidad, precioUnitario, totaldet, descuento, almacen, cantidadTg, lote, fechaVencimiento, cantidadUsada, cantidadFraccion);

        editorCompraDiv.add(headerLabel, headerForm, detailLabel, detailForm);

        binder.bindInstanceFields(this);
        setupButtons(editorCompraDiv);
        return editorCompraDiv;
    }
    private void setupButtons(Div div){
        //configuración de botones
        saveoc.addClickListener(event -> saveOrdenCompra());
        canceloc.addClickListener(event -> clearForm());
        deleteoc.addClickListener(event -> deleteOrdenCompra());

        saveoc.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        canceloc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteoc.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttons = new HorizontalLayout(saveoc, canceloc, deleteoc);
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
        binderDet.forField(producto).bind(OrdenCompraDet::getProducto, OrdenCompraDet::setProducto);
        binderDet.forField(cantidad).bind(OrdenCompraDet::getCantidad, OrdenCompraDet::setCantidad);
        binderDet.forField(precioUnitario).bind(OrdenCompraDet::getPrecioUnitario, OrdenCompraDet::setPrecioUnitario);
        binderDet.forField(totaldet).bind(OrdenCompraDet::getTotaldet, OrdenCompraDet::setTotaldet);
        binderDet.forField(descuento).bind(OrdenCompraDet::getDescuento, OrdenCompraDet::setDescuento);
        binderDet.forField(almacen).bind(OrdenCompraDet::getAlmacen, OrdenCompraDet::setAlmacen);
        binderDet.forField(cantidadTg).bind(OrdenCompraDet::getCantidadTg, OrdenCompraDet::setCantidadTg);
        binderDet.forField(lote).bind(OrdenCompraDet::getLote, OrdenCompraDet::setLote);
        binderDet.forField(fechaVencimiento)
                .withConverter(
                        localDate -> localDate == null ? null : java.sql.Date.valueOf(localDate),
                        date -> date == null ? null : date.toLocalDate(),
                        "Fecha inválida"
                ).bind(OrdenCompraDet::getFechaVencimiento, OrdenCompraDet::setFechaVencimiento);
        binderDet.forField(cantidadUsada).bind(OrdenCompraDet::getCantidadUsada, OrdenCompraDet::setCantidadUsada);
        binderDet.forField(cantidadFraccion).bind(OrdenCompraDet::getCantidadFraccion, OrdenCompraDet::setCantidadFraccion);
    }

    private void saveOrdenCompra(){
        try {
            if (ordenCompra == null || ordenCompraDet == null) {
                ordenCompra = new OrdenCompra();
                ordenCompraDet = new OrdenCompraDet();
            }
            if (binder.writeBeanIfValid(ordenCompra) && binderDet.writeBeanIfValid(ordenCompraDet)) {
                ordenCompraService.update(ordenCompra);
                ordenCompraDet.setOrdenCompra(ordenCompra);
                ordenCompraDetService.update(ordenCompraDet);
                refreshGrids();
                clearForm();
                Notification.show("Orden de Compra guardada correctamente");
            } else {
                Notification.show("No se pudo guardar la Orden de Compra");
            }
            clearForm();
            refreshGrids();
            UI.getCurrent().navigate(OrdenCompraViewBACKUP.class);
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
            ordenCompraDetService.delete(ordenCompraDet);
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
        saveoc.setText("Guardar");
    }

    private void editOrdenCompra(OrdenCompra ordenCompra){
        if (ordenCompra == null) {
            clearForm();
        } else {
            this.ordenCompra = ordenCompra;
            binder.readBean(ordenCompra);
            saveoc.setText("Actualizar");
        }
    }

    private void editOrdenCompraDet(OrdenCompraDet ordenCompraDet){
        if (ordenCompraDet == null) {
            clearForm();
        } else {
            this.ordenCompraDet = ordenCompraDet;
            binderDet.readBean(ordenCompraDet);
            saveoc.setText("Actualizar");
        }
    }

}
