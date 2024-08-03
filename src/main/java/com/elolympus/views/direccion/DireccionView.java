package com.elolympus.views.direccion;

import com.elolympus.component.DataGrid;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.services.services.DireccionService;
import com.elolympus.services.services.OrdenCompraService;
import com.elolympus.services.services.UbigeoService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
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

/**
 * Created by [EnriqueZGutierreZ]
 */
@PageTitle("Direccion")
@Route(value = "Direccion", layout = MainLayout.class)
@PermitAll
public class DireccionView extends Div {

    private DireccionService direccionService;
    private UbigeoService ubigeoService;
    private DireccionView direccionView;
    public Direccion direccion;
    private final BeanValidationBinder<DireccionView> binder = new BeanValidationBinder<>(DireccionView.class);

    //GRID
    private final Grid<Direccion> grid = new Grid<>(Direccion.class);
    private final DataGrid<Direccion> dataGrid = new DataGrid<>(true, false);
    private final Button editar = new Button("EDITAR");
    private final Button agregar = new Button("AGREGAR");
    private final Button eliminar = new Button("ELIMINAR");
    private final Button cancelar = new Button("CANCELAR");


    private final VerticalLayout    panel               = new VerticalLayout();
    private final HorizontalLayout  panelUbigeo         = new HorizontalLayout();
    private ComboBox<Departamento>  departamentoComboBox= new ComboBox<>("Departamento");
    private ComboBox<Provincia>     provinciaComboBox   = new ComboBox<>("Provincia");
    private ComboBox<Distrito>      distritoComboBox    = new ComboBox<>("Distrito");
    private Text                    txtnumeroUbigeo     = new Text("Número de Ubigeo: ");

    private final HorizontalLayout  panelButton         = new HorizontalLayout();


    //TEXTFIELD UI Direccion

    private final Text titulo = new Text("DIRECCION");
    private final TextField Descripcion = new TextField("Descripcion");
    private final TextField Referencia = new TextField("Referencia");
    private final FormLayout form = new FormLayout();

    //constructor SOBRECARGADO

    //CONTRUCTOR
    public DireccionView(UbigeoService ubigeoService) {
        this.ubigeoService = ubigeoService;
        this.panelUbigeo.add(departamentoComboBox, provinciaComboBox, distritoComboBox , txtnumeroUbigeo);
        this.form.add(Descripcion, Referencia);
        this.panelButton.add(agregar, editar, eliminar, cancelar);
        this.panel.add(panelUbigeo,form, panelButton);
        this.add(panel);
        init();
    }

    private void init() {
        this.panel.setSizeFull();
        this.panelButton.setWidthFull();
        //this.panelButton.setAlignItems(FlexComponent.Alignment.END);
        this.panelButton.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        this.panelUbigeo.setWidthFull();
        initUbigeo();
        initButtons();
        addClassName("orden-compra-view");
        setSizeFull();
        refreshGrids();
    }

    private void initButtons() {
        //configuración de botones
        agregar.addClickListener(event -> add());
        //cancelar.addClickListener(event -> deleteOrdenCompra());
        eliminar.addClickListener(event -> deleteOrdenCompra());
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        //buttons.setClassName("button-layout");
    }

    private void initUbigeo(){
        departamentoComboBox.setItemLabelGenerator(Departamento::getNombre);
        provinciaComboBox.setItemLabelGenerator(Provincia::getNombre);
        distritoComboBox.setItemLabelGenerator(Distrito::getNombre);

        departamentoComboBox.setItems(ubigeoService.getAllRegiones());
        departamentoComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                provinciaComboBox.setItems(ubigeoService.getProvinciasByRegion(event.getValue().getId()));
                txtnumeroUbigeo.setText("Número de Ubigeo: " + ubigeoService.getNumeroUbigeo(event.getValue().getId(), "region"));
            }
        });

        provinciaComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                distritoComboBox.setItems(ubigeoService.getDistritosByProvincia(event.getValue().getId()));
                txtnumeroUbigeo.setText("Número de Ubigeo: " + ubigeoService.getNumeroUbigeo(event.getValue().getId(), "provincia"));
            }
        });

        distritoComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                txtnumeroUbigeo.setText("Número de Ubigeo: " + ubigeoService.getNumeroUbigeo(event.getValue().getId(), "distrito"));
            }
        });
    }

    private void refreshGrids() {
        // dataGrid.setList(ordenCompraService.findAll());
    }


    private void add() {
//        OrdenCompraDetView ordenCompra = new OrdenCompraDetView(this.ordenCompraService);
//        Dialog view = new Dialog();
//        view.setHeaderTitle("ORDEN DE COMPRA DETALLE");
//        view.add(ordenCompra);
//        view.open();
    }


    private void deleteOrdenCompra() {
        //ordenCompra = dataGrid.getSelectedValue();
//        if (direccion != null) {
//            DireccionService.delete(direccion);
//            refreshGrids();
//            Notification.show("Orden de Compra eliminada correctamente");
//        } else {
//            Notification.show("No se pudo eliminar la Orden de Compra");
//        }
    }


//    private void editOrdenCompraDet(OrdenCompraDet ordenCompraDet){
//
//    }

}