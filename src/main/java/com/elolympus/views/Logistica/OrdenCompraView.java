package com.elolympus.views.Logistica;

import com.elolympus.component.DataGrid;
import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.data.Logistica.OrdenCompraDet;
import com.elolympus.services.services.OrdenCompraService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;

@PageTitle("Orden de Compra")
@Route(value = "OrdenCompra", layout = MainLayout.class)
@PermitAll
public class OrdenCompraView extends Div {

    private final OrdenCompraService ordenCompraService;
    private OrdenCompra ordenCompra;
    private final BeanValidationBinder<OrdenCompra> binder= new BeanValidationBinder<>(OrdenCompra.class);

    //GRID
    private final Grid<OrdenCompra> grid = new Grid<>(OrdenCompra.class);
    private final DataGrid<OrdenCompraDet> dataGrid = new DataGrid<>(true,false);
    private final Button editar = new Button("EDITAR");
    private final Button agregar = new Button("AGREGAR");
    private final Button eliminar = new Button("ELIMINAR");
    private final Button cancelar = new Button("CANCELAR");


    private final VerticalLayout panel = new VerticalLayout();
    private final HorizontalLayout panelFiltro     = new HorizontalLayout();
    private final HorizontalLayout panelButton     = new HorizontalLayout();

    private final TextField Sucursal = new TextField("Documento Pago");
    private final DatePicker FechaInicio     = new DatePicker("Fecha Inicio",LocalDate.now());
    private final DatePicker FechaFin        = new DatePicker("Fecha Fin",LocalDate.now());

    //TEXTFIELD UI OrdenCompra

    private final Text titulo = new Text("ORDEN DE COMPRA");
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
    private final FormLayout form = new FormLayout();

    //constructor
    public OrdenCompraView(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;


        initDataGrid();
        this.panelFiltro.add(Sucursal,FechaInicio,FechaFin);
        this.form.add(almacenEntrega,numeroProveedor,direccionProveedor,fecha,fechaEntrega,
                formaPago,moneda,impuesto,observaciones,tipoCambio,diasCredito,sucursal,
                impuesto_incluido,documento_pago,totalCobrado,total);
        this.panelButton.add(agregar,editar,eliminar,cancelar);
        this.panel.add(form,dataGrid,panelButton);
        this.add(panel);
        init();
    }
    private void init(){
        this.panel.setSizeFull();
        this.panelButton.setWidthFull();
        //this.panelButton.setAlignItems(FlexComponent.Alignment.END);
        this.panelButton.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        this.panelFiltro.setWidthFull();
        initDataGrid();
        initButtons();
        addClassName("orden-compra-view");
        setSizeFull();
        refreshGrids();
    }

    private void initButtons(){
        //configuración de botones
        agregar.addClickListener(event -> add());
        //cancelar.addClickListener(event -> deleteOrdenCompra());
        eliminar.addClickListener(event -> deleteOrdenCompra());
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        //buttons.setClassName("button-layout");
    }

    private void initDataGrid(){
        dataGrid.addColumn(OrdenCompraDet::getOrdenCompra,"Orden Compra");
        dataGrid.addColumn(OrdenCompraDet::getAlmacen,"Almacen");
        dataGrid.addColumn(ordenCompraDet -> ordenCompraDet.getProducto() != null ? ordenCompraDet.getProducto().getNombre() : "")
                .setHeader("Producto")
                .setAutoWidth(true);
        dataGrid.addColumn(OrdenCompraDet::getCantidad,"Cantidad");
        dataGrid.addColumn(OrdenCompraDet::getCantidadTg,"Cantidad TG");
        dataGrid.addColumn(OrdenCompraDet::getDescuento,"Descuento");
        dataGrid.addColumn(OrdenCompraDet::getFechaVencimiento,"Fecha Vencimiento");
        dataGrid.addColumn(OrdenCompraDet::getPrecioUnitario,"Precio Unitario");
        dataGrid.addColumn(OrdenCompraDet::getLote,"Lote");
        dataGrid.addColumn(OrdenCompraDet::getCantidadUsada,"Cantidad Usada");
        dataGrid.addColumn(OrdenCompraDet::getTotalDet,"Total");


        dataGrid.asSingleSelect().addValueChangeListener(evt -> editOrdenCompraDet(evt.getValue()));
    }

    private void refreshGrids() {
       // dataGrid.setList(ordenCompraService.findAll());
    }



    private void add(){
//        OrdenCompraDetView ordenCompra = new OrdenCompraDetView(this.ordenCompraService);
//        Dialog view = new Dialog();
//        view.setHeaderTitle("ORDEN DE COMPRA DETALLE");
//        view.add(ordenCompra);
//        view.open();
    }


    private void deleteOrdenCompra(){
        //ordenCompra = dataGrid.getSelectedValue();
        if (ordenCompra != null) {
            ordenCompraService.delete(ordenCompra);
            refreshGrids();
            Notification.show("Orden de Compra eliminada correctamente");
        } else {
            Notification.show("No se pudo eliminar la Orden de Compra");
        }
    }


    private void editOrdenCompraDet(OrdenCompraDet ordenCompraDet){

    }


}
