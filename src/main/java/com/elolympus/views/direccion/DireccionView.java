package com.elolympus.views.direccion;

import com.elolympus.component.DataGrid;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Auxiliar.Ubigeo;
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
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
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

/**
 * Created by [EnriqueZGutierreZ]
 */
@PageTitle("Direccion")
@Route(value = "Direccion", layout = MainLayout.class)
@PermitAll
public class DireccionView extends Dialog {

    private DireccionService direccionService;
    private UbigeoService ubigeoService;
    private DireccionView direccionView;
    public Direccion direccion;
    private final BeanValidationBinder<Direccion> binder = new BeanValidationBinder<>(Direccion.class);

    //GRID
    private final Grid<Direccion> grid = new Grid<>(Direccion.class);
    private final DataGrid<Direccion> dataGrid = new DataGrid<>(true, false);
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
    private final TextField txtDescripcion = new TextField("Descripcion");
    private final TextField txtReferencia = new TextField("Referencia");
    private final FormLayout form = new FormLayout();

    //constructor SOBRECARGADO

    //CONTRUCTOR
    public DireccionView(UbigeoService ubigeoService, DireccionService direccionService) {
        this.ubigeoService = ubigeoService;
        this.direccionService = direccionService;
        this.panelUbigeo.add(departamentoComboBox, provinciaComboBox, distritoComboBox , txtnumeroUbigeo);
        this.form.add(txtDescripcion, txtReferencia);
        this.panelButton.add(agregar, eliminar, cancelar);
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
        //setSizeFull();
        refreshGrids();
    }

    private void initButtons() {
        //configuración de botones
        agregar.addClickListener(event -> add());
        cancelar.addClickListener(event -> this.close());
        eliminar.addClickListener(event -> deleteOrdenCompra());
        agregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        departamentoComboBox.setPlaceholder("Selec. Departamento");
        provinciaComboBox.setPlaceholder("Selec. Provincia");
        distritoComboBox.setPlaceholder("Selec. Distrito");
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
        try {
            // Validar campos requeridos
            if (txtDescripcion.isEmpty()) {
                Notification.show("La descripción es requerida", 3000, Notification.Position.MIDDLE);
                return;
            }
            
            if (distritoComboBox.getValue() == null) {
                Notification.show("Debe seleccionar un distrito", 3000, Notification.Position.MIDDLE);
                return;
            }
            
            // Obtener información seleccionada
            Distrito distritoSeleccionado = distritoComboBox.getValue();
            Provincia provinciaSeleccionada = provinciaComboBox.getValue();
            Departamento departamentoSeleccionado = departamentoComboBox.getValue();
            
            // Buscar si ya existe un Ubigeo con el mismo código
            String codigoUbigeo = distritoSeleccionado.getId();
            Ubigeo ubigeoExistente = ubigeoService.getUbigeoByCodigo(codigoUbigeo).orElse(null);
            
            Ubigeo ubigeoAUsar;
            if (ubigeoExistente != null) {
                // Usar el Ubigeo existente
                ubigeoAUsar = ubigeoExistente;
            } else {
                // Crear y guardar nuevo Ubigeo
                Ubigeo nuevoUbigeo = new Ubigeo();
                nuevoUbigeo.setCodigo(codigoUbigeo);
                nuevoUbigeo.setDepartamento(departamentoSeleccionado.getNombre());
                nuevoUbigeo.setProvincia(provinciaSeleccionada.getNombre());
                nuevoUbigeo.setDistrito(distritoSeleccionado.getNombre());
                
                // Guardar el Ubigeo primero
                ubigeoAUsar = ubigeoService.save(nuevoUbigeo);
            }
            
            // Usar dirección existente si está editando, o crear nueva
            Direccion direccionAGuardar;
            if (this.direccion != null) {
                // Editando dirección existente
                direccionAGuardar = this.direccion;
            } else {
                // Creando nueva dirección
                direccionAGuardar = new Direccion();
            }
            
            direccionAGuardar.setDescripcion(txtDescripcion.getValue());
            direccionAGuardar.setReferencia(txtReferencia.getValue());
            direccionAGuardar.setUbigeo(ubigeoAUsar);
            
            // Guardar la dirección
            this.direccion = direccionService.save(direccionAGuardar);
            
            // Mostrar confirmación
            Notification.show("Dirección guardada correctamente", 3000, Notification.Position.MIDDLE);
            
            // Notificar a los listeners antes de cerrar
            fireEvent(new DireccionGuardadaEvent(this, this.direccion));
            
            // Cerrar el dialog
            this.close();
            
        } catch (Exception e) {
            Notification.show("Error al guardar la dirección: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
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

    // Clase del evento personalizado
    public static class DireccionGuardadaEvent extends ComponentEvent<DireccionView> {
        private final Direccion direccion;

        public DireccionGuardadaEvent(DireccionView source, Direccion direccion) {
            super(source, false);
            this.direccion = direccion;
        }

        public Direccion getDireccion() {
            return direccion;
        }
    }

    // Método para registrar listeners del evento
    public Registration addDireccionGuardadaListener(ComponentEventListener<DireccionGuardadaEvent> listener) {
        return addListener(DireccionGuardadaEvent.class, listener);
    }

    public void populateForm() {
        if (this.direccion != null) {
            txtDescripcion.setValue(direccion.getDescripcion() != null ? direccion.getDescripcion() : "");
            txtReferencia.setValue(direccion.getReferencia() != null ? direccion.getReferencia() : "");
            
            // Cambiar el texto del botón para indicar que es edición
            agregar.setText("ACTUALIZAR");
            
            // Si la dirección tiene ubigeo, cargar los combos
            if (direccion.getUbigeo() != null) {
                // Buscar y seleccionar departamento, provincia y distrito basado en el ubigeo
                String codigo = direccion.getUbigeo().getCodigo();
                if (codigo != null && codigo.length() >= 6) {
                    // Los primeros 2 dígitos son departamento, los siguientes 2 provincia, los últimos 2 distrito
                    String depCodigo = codigo.substring(0, 2);
                    String provCodigo = codigo.substring(0, 4);
                    String distCodigo = codigo;
                    
                    // Buscar y seleccionar departamento
                    ubigeoService.getAllRegiones().stream()
                        .filter(dep -> dep.getId().equals(depCodigo))
                        .findFirst()
                        .ifPresent(departamentoComboBox::setValue);
                    
                    // Buscar y seleccionar provincia
                    if (departamentoComboBox.getValue() != null) {
                        ubigeoService.getProvinciasByRegion(depCodigo).stream()
                            .filter(prov -> prov.getId().equals(provCodigo))
                            .findFirst()
                            .ifPresent(provinciaComboBox::setValue);
                    }
                    
                    // Buscar y seleccionar distrito
                    if (provinciaComboBox.getValue() != null) {
                        ubigeoService.getDistritosByProvincia(provCodigo).stream()
                            .filter(dist -> dist.getId().equals(distCodigo))
                            .findFirst()
                            .ifPresent(distritoComboBox::setValue);
                    }
                }
            }
        }
    }


//    private void editOrdenCompraDet(OrdenCompraDet ordenCompraDet){
//
//    }

}