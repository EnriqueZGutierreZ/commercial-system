package com.elolympus.views.Logistica;

import com.elolympus.component.DataGrid;
import com.elolympus.data.Logistica.OrdenCompra;
import com.elolympus.data.Logistica.OrdenCompraDet;
import com.elolympus.services.services.OrdenCompraService;
import com.elolympus.services.services.AlmacenService;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.DireccionService;
import com.elolympus.services.services.SucursalService;
import com.elolympus.services.services.ProductoService;
import com.elolympus.services.services.OrdenCompraDetService;
import com.elolympus.data.Almacen.Almacen;
import com.elolympus.data.Logistica.Producto;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Empresa.Sucursal;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

import java.time.LocalDate;

@PageTitle("Orden de Compra")
@Route(value = "OrdenCompra", layout = MainLayout.class)
@PermitAll
public class OrdenCompraView extends Div {

    private final OrdenCompraService ordenCompraService;
    private final OrdenCompraDetService ordenCompraDetService;
    private final AlmacenService almacenService;
    private final PersonaService personaService;
    private final DireccionService direccionService;
    private final SucursalService sucursalService;
    private final ProductoService productoService;
    private OrdenCompra ordenCompra;
    private final BeanValidationBinder<OrdenCompra> binder= new BeanValidationBinder<>(OrdenCompra.class);

    //GRIDS
    private final Grid<OrdenCompra> gridOrdenCompra = new Grid<>(OrdenCompra.class, false);
    private final DataGrid<OrdenCompraDet> gridDetalles = new DataGrid<>(true,false);
    
    //BOTONES
    private final Button nuevaOrden = new Button("Nueva Orden");
    private final Button editarOrden = new Button("Editar");
    private final Button eliminarOrden = new Button("Eliminar");
    private final Button guardarOrden = new Button("Guardar");
    private final Button cancelarOrden = new Button("Cancelar");
    
    private final Button agregarProducto = new Button("Agregar Producto");
    private final Button editarProducto = new Button("Editar");
    private final Button eliminarProducto = new Button("Eliminar");
    
    // Layout para botones del formulario
    private HorizontalLayout formButtonLayout;


    private final VerticalLayout panel = new VerticalLayout();
    private final HorizontalLayout panelFiltro     = new HorizontalLayout();
    private final HorizontalLayout panelButton     = new HorizontalLayout();

    private final TextField Sucursal = new TextField("Documento Pago");
    private final DatePicker FechaInicio     = new DatePicker("Fecha Inicio",LocalDate.now());
    private final DatePicker FechaFin        = new DatePicker("Fecha Fin",LocalDate.now());

    //TEXTFIELD UI OrdenCompra

    private final Text titulo = new Text("ORDEN DE COMPRA");
    private final ComboBox<Almacen> almacenEntrega = new ComboBox<>("Almacén Entrega");
    private final ComboBox<Persona> proveedor = new ComboBox<>("Proveedor");
    private final ComboBox<Direccion> direccionProveedor = new ComboBox<>("Dirección Proveedor");
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
    private final ComboBox<Sucursal> sucursal = new ComboBox<>("Sucursal");
    private final Checkbox impuesto_incluido = new Checkbox("Impuesto Incluido");
    private final TextField documento_pago = new TextField("Documento Pago");
    private final FormLayout form = new FormLayout();

    //constructor
    public OrdenCompraView(OrdenCompraService ordenCompraService, OrdenCompraDetService ordenCompraDetService,
                          AlmacenService almacenService, PersonaService personaService, 
                          DireccionService direccionService, SucursalService sucursalService,
                          ProductoService productoService) {
        this.ordenCompraService = ordenCompraService;
        this.ordenCompraDetService = ordenCompraDetService;
        this.almacenService = almacenService;
        this.personaService = personaService;
        this.direccionService = direccionService;
        this.sucursalService = sucursalService;
        this.productoService = productoService;


        // Crear layout Master-Detail
        VerticalLayout masterSection = createMasterSection();
        VerticalLayout detailSection = createDetailSection();
        
        SplitLayout masterDetailLayout = new SplitLayout(masterSection, detailSection);
        masterDetailLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        masterDetailLayout.setSplitterPosition(60); // 60% arriba para master, 40% abajo para detail
        masterDetailLayout.setSizeFull();
        
        this.add(masterDetailLayout);
        init();
    }
    
    private VerticalLayout createMasterSection() {
        VerticalLayout masterLayout = new VerticalLayout();
        masterLayout.setPadding(false);
        masterLayout.setSpacing(true);
        
        // Barra de búsqueda y botones para órdenes
        HorizontalLayout searchLayout = new HorizontalLayout();
        TextField searchField = new TextField("Buscar");
        searchField.setPlaceholder("Buscar por proveedor, fecha o número...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addValueChangeListener(e -> updateOrdenCompraList(e.getValue()));
        
        HorizontalLayout masterButtons = new HorizontalLayout();
        nuevaOrden.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editarOrden.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        eliminarOrden.addThemeVariants(ButtonVariant.LUMO_ERROR);
        masterButtons.add(nuevaOrden, editarOrden, eliminarOrden);
        
        searchLayout.add(searchField, masterButtons);
        searchLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        searchLayout.setWidthFull();
        
        // Grid de órdenes de compra
        setupMasterGrid();
        
        // Formulario de orden (inicialmente oculto)
        this.form.add(almacenEntrega, proveedor, direccionProveedor, fecha, fechaEntrega,
                formaPago, moneda, impuesto, observaciones, tipoCambio, diasCredito, sucursal,
                impuesto_incluido, documento_pago, totalCobrado, total);
        form.setVisible(false); // Oculto hasta que se seleccione una orden
        
        formButtonLayout = new HorizontalLayout();
        guardarOrden.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelarOrden.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        formButtonLayout.add(guardarOrden, cancelarOrden);
        formButtonLayout.setVisible(false);
        
        masterLayout.add(searchLayout, gridOrdenCompra, form, formButtonLayout);
        masterLayout.setSizeFull();
        return masterLayout;
    }
    
    private VerticalLayout createDetailSection() {
        VerticalLayout detailLayout = new VerticalLayout();
        detailLayout.setPadding(false);
        detailLayout.setSpacing(true);
        
        // Barra de botones para productos
        HorizontalLayout detailHeader = new HorizontalLayout();
        Text detailTitle = new Text("Productos de la Orden");
        
        HorizontalLayout detailButtons = new HorizontalLayout();
        agregarProducto.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editarProducto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        eliminarProducto.addThemeVariants(ButtonVariant.LUMO_ERROR);
        detailButtons.add(agregarProducto, editarProducto, eliminarProducto);
        
        detailHeader.add(detailTitle, detailButtons);
        detailHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        detailHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        detailHeader.setWidthFull();
        
        // Grid de detalles
        setupDetailGrid();
        
        detailLayout.add(detailHeader, gridDetalles);
        detailLayout.setSizeFull();
        return detailLayout;
    }
    
    private void setupMasterGrid() {
        gridOrdenCompra.removeAllColumns();
        gridOrdenCompra.addColumn(OrdenCompra::getId).setHeader("ID").setAutoWidth(true);
        gridOrdenCompra.addColumn(OrdenCompra::getFecha).setHeader("Fecha").setAutoWidth(true);
        gridOrdenCompra.addColumn(orden -> 
                orden.getProveedor() != null ? orden.getProveedor().getNombreCompleto() : "")
                .setHeader("Proveedor")
                .setAutoWidth(true);
        gridOrdenCompra.addColumn(orden -> 
                orden.getAlmacenEntrega() != null ? orden.getAlmacenEntrega().getDescripcion() : "")
                .setHeader("Almacén")
                .setAutoWidth(true);
        gridOrdenCompra.addColumn(OrdenCompra::getTotal).setHeader("Total").setAutoWidth(true);
        gridOrdenCompra.addColumn(OrdenCompra::getObservaciones).setHeader("Observaciones").setAutoWidth(true);
        
        gridOrdenCompra.asSingleSelect().addValueChangeListener(event -> {
            OrdenCompra selectedOrden = event.getValue();
            if (selectedOrden != null) {
                editOrdenCompra(selectedOrden);
                loadDetallesForOrden(selectedOrden);
            } else {
                clearForm();
                gridDetalles.setItems();
            }
        });
        
        gridOrdenCompra.setSizeFull();
    }
    
    private void setupDetailGrid() {
        gridDetalles.addColumn(ordenCompraDet -> 
                ordenCompraDet.getAlmacen() != null ? ordenCompraDet.getAlmacen().getDescripcion() : "")
                .setHeader("Almacén")
                .setAutoWidth(true);
        gridDetalles.addColumn(ordenCompraDet -> 
                ordenCompraDet.getProducto() != null ? ordenCompraDet.getProducto().getNombre() : "")
                .setHeader("Producto")
                .setAutoWidth(true);
        gridDetalles.addColumn(OrdenCompraDet::getCantidad).setHeader("Cantidad");
        gridDetalles.addColumn(OrdenCompraDet::getPrecioUnitario).setHeader("Precio Unit.");
        gridDetalles.addColumn(OrdenCompraDet::getDescuento).setHeader("Descuento");
        gridDetalles.addColumn(OrdenCompraDet::getTotalDet).setHeader("Total");
        gridDetalles.addColumn(OrdenCompraDet::getFechaVencimiento).setHeader("F. Vencimiento");
        gridDetalles.addColumn(OrdenCompraDet::getLote).setHeader("Lote");
        
        gridDetalles.asSingleSelect().addValueChangeListener(evt -> editOrdenCompraDet(evt.getValue()));
        gridDetalles.setSizeFull();
    }
    
    private void updateOrdenCompraList(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            gridOrdenCompra.setItems(ordenCompraService.findAll());
        } else {
            gridOrdenCompra.setItems(
                ordenCompraService.findAll().stream()
                    .filter(orden -> 
                        (orden.getId() != null && orden.getId().toString().contains(searchText.toLowerCase())) ||
                        (orden.getProveedor() != null && orden.getProveedor().getNombreCompleto().toLowerCase().contains(searchText.toLowerCase())) ||
                        (orden.getObservaciones() != null && orden.getObservaciones().toLowerCase().contains(searchText.toLowerCase()))
                    )
                    .toList()
            );
        }
    }
    
    private void editOrdenCompra(OrdenCompra orden) {
        this.ordenCompra = orden;
        binder.readBean(orden);
        form.setVisible(true);
        formButtonLayout.setVisible(true);
    }
    
    private void clearForm() {
        this.ordenCompra = new OrdenCompra();
        binder.readBean(ordenCompra);
        form.setVisible(false);
        formButtonLayout.setVisible(false);
    }
    
    private void loadDetallesForOrden(OrdenCompra orden) {
        if (orden != null && orden.getId() != null) {
            try {
                // Cargar detalles desde el servicio
                var detalles = ordenCompraDetService.findAll().stream()
                        .filter(det -> det.getOrdenCompra() != null && 
                               det.getOrdenCompra().getId().equals(orden.getId()))
                        .toList();
                gridDetalles.setItems(detalles);
            } catch (Exception e) {
                gridDetalles.setItems();
                System.out.println("No se pudieron cargar los detalles: " + e.getMessage());
            }
        } else {
            gridDetalles.setItems();
        }
    }
    
    private void configureComboBoxes() {
        // Configurar ComboBox de Almacén
        almacenEntrega.setItems(almacenService.findAll());
        almacenEntrega.setItemLabelGenerator(Almacen::getDescripcion);
        binder.forField(almacenEntrega)
                .bind(OrdenCompra::getAlmacenEntrega, OrdenCompra::setAlmacenEntrega);
        
        // Configurar ComboBox de Proveedor (Personas con RUC)
        proveedor.setItems(personaService.findAll().stream()
                .filter(p -> p.getTipo_documento() != null && p.getTipo_documento() == 2) // RUC
                .toList());
        proveedor.setItemLabelGenerator(Persona::getNombreCompleto);
        binder.forField(proveedor)
                .bind(OrdenCompra::getProveedor, OrdenCompra::setProveedor);
        
        // Configurar ComboBox de Dirección
        direccionProveedor.setItems(direccionService.findAll());
        direccionProveedor.setItemLabelGenerator(Direccion::getDescripcion);
        binder.forField(direccionProveedor)
                .bind(OrdenCompra::getDireccionProveedor, OrdenCompra::setDireccionProveedor);
        
        // Configurar ComboBox de Sucursal
        sucursal.setItems(sucursalService.findAll());
        sucursal.setItemLabelGenerator(s -> s.getDescripcion());
        binder.forField(sucursal)
                .bind(OrdenCompra::getSucursal, OrdenCompra::setSucursal);
                
        // Configurar campos de fecha
        binder.forField(fecha)
                .bind(
                    orden -> orden.getFecha() != null ? orden.getFecha().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null,
                    (orden, fecha) -> orden.setFecha(fecha != null ? java.sql.Date.valueOf(fecha) : null)
                );
                
        binder.forField(fechaEntrega)
                .bind(
                    orden -> orden.getFechaEntrega() != null ? orden.getFechaEntrega().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null,
                    (orden, fechaEntrega) -> orden.setFechaEntrega(fechaEntrega != null ? java.sql.Date.valueOf(fechaEntrega) : null)
                );
    }
    
    private void init(){
        configureComboBoxes();
        setupMasterGrid();
        setupDetailGrid();
        initButtons();
        addClassName("orden-compra-view");
        setSizeFull();
        refreshGrids();
    }

    private void initButtons(){
        // Botones del Master (Órdenes)
        nuevaOrden.addClickListener(event -> createNewOrden());
        editarOrden.addClickListener(event -> editSelectedOrden());
        eliminarOrden.addClickListener(event -> deleteOrdenCompra());
        guardarOrden.addClickListener(event -> saveOrdenCompra());
        cancelarOrden.addClickListener(event -> cancelEditOrden());
        
        // Botones del Detail (Productos)
        agregarProducto.addClickListener(event -> addProducto());
        editarProducto.addClickListener(event -> editSelectedProducto());
        eliminarProducto.addClickListener(event -> deleteProducto());
    }


    private void refreshGrids() {
        gridOrdenCompra.setItems(ordenCompraService.findAll());
    }
    
    // Métodos de acción para botones Master (Órdenes)
    private void createNewOrden() {
        clearForm();
        ordenCompra = new OrdenCompra();
        binder.readBean(ordenCompra);
        form.setVisible(true);
        formButtonLayout.setVisible(true);
    }
    
    private void editSelectedOrden() {
        OrdenCompra selected = gridOrdenCompra.asSingleSelect().getValue();
        if (selected != null) {
            editOrdenCompra(selected);
        } else {
            Notification.show("Seleccione una orden para editar");
        }
    }
    
    private void saveOrdenCompra() {
        if (ordenCompra != null && binder.validate().isOk()) {
            try {
                binder.writeBean(ordenCompra);
                ordenCompraService.save(ordenCompra);
                refreshGrids();
                clearForm();
                Notification.show("Orden de compra guardada correctamente");
            } catch (Exception e) {
                Notification.show("Error al guardar la orden: " + e.getMessage());
            }
        } else {
            Notification.show("Complete todos los campos requeridos");
        }
    }
    
    private void cancelEditOrden() {
        clearForm();
        gridOrdenCompra.asSingleSelect().clear();
    }
    
    // Métodos de acción para botones Detail (Productos)
    private void addProducto() {
        OrdenCompra selectedOrden = gridOrdenCompra.asSingleSelect().getValue();
        if (selectedOrden != null) {
            openProductDialog(null, selectedOrden); // null = nuevo producto
        } else {
            Notification.show("Seleccione una orden primero");
        }
    }
    
    private void openProductDialog(OrdenCompraDet detalle, OrdenCompra orden) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(detalle == null ? "Agregar Producto" : "Editar Producto");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("600px");
        
        // Crear formulario para producto
        FormLayout formLayout = new FormLayout();
        
        // Campos del formulario
        ComboBox<Producto> productoCombo = new ComboBox<>("Producto");
        productoCombo.setItems(productoService.findActive());
        productoCombo.setItemLabelGenerator(Producto::getNombre);
        productoCombo.setRequiredIndicatorVisible(true);
        
        ComboBox<Almacen> almacenCombo = new ComboBox<>("Almacén");
        almacenCombo.setItems(almacenService.findAll());
        almacenCombo.setItemLabelGenerator(Almacen::getDescripcion);
        almacenCombo.setRequiredIndicatorVisible(true);
        
        BigDecimalField cantidadField = new BigDecimalField("Cantidad");
        cantidadField.setRequiredIndicatorVisible(true);
        cantidadField.setValue(java.math.BigDecimal.ONE);
        
        BigDecimalField precioField = new BigDecimalField("Precio Unitario");
        precioField.setRequiredIndicatorVisible(true);
        
        BigDecimalField descuentoField = new BigDecimalField("Descuento");
        descuentoField.setValue(java.math.BigDecimal.ZERO);
        
        BigDecimalField totalField = new BigDecimalField("Total");
        totalField.setReadOnly(true);
        
        TextField loteField = new TextField("Lote");
        DatePicker fechaVencimientoField = new DatePicker("Fecha Vencimiento");
        
        // Cálculo automático del total
        Runnable calcularTotal = () -> {
            try {
                java.math.BigDecimal cantidad = cantidadField.getValue();
                java.math.BigDecimal precio = precioField.getValue();
                java.math.BigDecimal descuento = descuentoField.getValue();
                
                if (cantidad != null && precio != null && descuento != null) {
                    java.math.BigDecimal subtotal = cantidad.multiply(precio);
                    java.math.BigDecimal total = subtotal.subtract(descuento);
                    totalField.setValue(total);
                }
            } catch (Exception e) {
                totalField.setValue(java.math.BigDecimal.ZERO);
            }
        };
        
        cantidadField.addValueChangeListener(e -> calcularTotal.run());
        precioField.addValueChangeListener(e -> calcularTotal.run());
        descuentoField.addValueChangeListener(e -> calcularTotal.run());
        
        // Si es edición, cargar datos existentes
        if (detalle != null) {
            productoCombo.setValue(detalle.getProducto());
            almacenCombo.setValue(detalle.getAlmacen());
            cantidadField.setValue(detalle.getCantidad());
            precioField.setValue(detalle.getPrecioUnitario());
            descuentoField.setValue(detalle.getDescuento());
            loteField.setValue(detalle.getLote());
            if (detalle.getFechaVencimiento() != null) {
                fechaVencimientoField.setValue(detalle.getFechaVencimiento().toLocalDate());
            }
        }
        
        formLayout.add(productoCombo, almacenCombo, cantidadField, precioField, 
                      descuentoField, totalField, loteField, fechaVencimientoField);
        
        // Botones
        Button guardarBtn = new Button("Guardar");
        guardarBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarBtn.addClickListener(e -> {
            if (validateProductForm(productoCombo, almacenCombo, cantidadField, precioField)) {
                saveProducto(detalle, orden, productoCombo.getValue(), almacenCombo.getValue(),
                           cantidadField.getValue(), precioField.getValue(), descuentoField.getValue(),
                           loteField.getValue(), fechaVencimientoField.getValue());
                dialog.close();
                loadDetallesForOrden(orden); // Recargar grid
                Notification.show(detalle == null ? "Producto agregado correctamente" : "Producto actualizado correctamente");
            }
        });
        
        Button cancelarBtn = new Button("Cancelar");
        cancelarBtn.addClickListener(e -> dialog.close());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(guardarBtn, cancelarBtn);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private void editSelectedProducto() {
        OrdenCompraDet selectedDet = gridDetalles.getSelectedValue();
        OrdenCompra selectedOrden = gridOrdenCompra.asSingleSelect().getValue();
        if (selectedDet != null && selectedOrden != null) {
            openProductDialog(selectedDet, selectedOrden); // Pasar detalle existente
        } else {
            Notification.show("Seleccione un producto para editar");
        }
    }
    
    private void deleteProducto() {
        OrdenCompraDet selectedDet = gridDetalles.getSelectedValue();
        OrdenCompra selectedOrden = gridOrdenCompra.asSingleSelect().getValue();
        if (selectedDet != null && selectedOrden != null) {
            // Confirmación de eliminación
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Confirmar eliminación");
            
            Text message = new Text("¿Está seguro que desea eliminar este producto de la orden?");
            
            Button confirmarBtn = new Button("Eliminar");
            confirmarBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            confirmarBtn.addClickListener(e -> {
                try {
                    ordenCompraDetService.delete(selectedDet);
                    loadDetallesForOrden(selectedOrden); // Recargar grid
                    Notification.show("Producto eliminado correctamente");
                } catch (Exception ex) {
                    Notification.show("Error al eliminar producto: " + ex.getMessage());
                }
                confirmDialog.close();
            });
            
            Button cancelarBtn = new Button("Cancelar");
            cancelarBtn.addClickListener(e -> confirmDialog.close());
            
            HorizontalLayout buttonLayout = new HorizontalLayout(confirmarBtn, cancelarBtn);
            VerticalLayout dialogLayout = new VerticalLayout(message, buttonLayout);
            confirmDialog.add(dialogLayout);
            confirmDialog.open();
        } else {
            Notification.show("Seleccione un producto para eliminar");
        }
    }
    
    private boolean validateProductForm(ComboBox<Producto> producto, ComboBox<Almacen> almacen, 
                                      BigDecimalField cantidad, BigDecimalField precio) {
        if (producto.getValue() == null) {
            Notification.show("Seleccione un producto");
            return false;
        }
        if (almacen.getValue() == null) {
            Notification.show("Seleccione un almacén");
            return false;
        }
        if (cantidad.getValue() == null || cantidad.getValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            Notification.show("La cantidad debe ser mayor a cero");
            return false;
        }
        if (precio.getValue() == null || precio.getValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            Notification.show("El precio debe ser mayor a cero");
            return false;
        }
        return true;
    }
    
    private void saveProducto(OrdenCompraDet detalleExistente, OrdenCompra orden, Producto producto, 
                             Almacen almacen, java.math.BigDecimal cantidad, java.math.BigDecimal precio,
                             java.math.BigDecimal descuento, String lote, java.time.LocalDate fechaVencimiento) {
        try {
            OrdenCompraDet detalle;
            if (detalleExistente != null) {
                // Editar existente
                detalle = detalleExistente;
            } else {
                // Crear nuevo
                detalle = new OrdenCompraDet();
                detalle.setOrdenCompra(orden);
            }
            
            detalle.setProducto(producto);
            detalle.setAlmacen(almacen);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setDescuento(descuento != null ? descuento : java.math.BigDecimal.ZERO);
            detalle.setLote(lote);
            
            if (fechaVencimiento != null) {
                detalle.setFechaVencimiento(java.sql.Date.valueOf(fechaVencimiento));
            }
            
            // Calcular total
            java.math.BigDecimal subtotal = cantidad.multiply(precio);
            java.math.BigDecimal total = subtotal.subtract(detalle.getDescuento());
            detalle.setTotalDet(total);
            
            ordenCompraDetService.save(detalle);
            
            // Actualizar total de la orden principal
            updateOrdenTotal(orden);
            
        } catch (Exception e) {
            Notification.show("Error al guardar producto: " + e.getMessage());
        }
    }
    
    private void updateOrdenTotal(OrdenCompra orden) {
        try {
            // Calcular total sumando todos los detalles
            var detalles = ordenCompraDetService.findAll().stream()
                    .filter(det -> det.getOrdenCompra() != null && 
                           det.getOrdenCompra().getId().equals(orden.getId()))
                    .toList();
            
            java.math.BigDecimal totalOrden = detalles.stream()
                    .map(OrdenCompraDet::getTotalDet)
                    .filter(total -> total != null)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            orden.setTotal(totalOrden);
            ordenCompraService.save(orden);
            
            // Refresh grid para mostrar nuevo total
            refreshGrids();
            
        } catch (Exception e) {
            System.out.println("Error actualizando total de orden: " + e.getMessage());
        }
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

    // Método para configurar la orden de compra en modo edición
    public void setOrdenCompraParaEdicion(OrdenCompra ordenCompraExistente) {
        if (ordenCompraExistente != null) {
            this.ordenCompra = ordenCompraExistente;
            
            // Cargar los datos de la orden en el formulario
            binder.readBean(this.ordenCompra);
            
            // Cargar los detalles de la orden
            refreshGrids();
            
            // Cambiar el texto del botón para indicar que es edición
            guardarOrden.setText("ACTUALIZAR");
        }
    }


}
