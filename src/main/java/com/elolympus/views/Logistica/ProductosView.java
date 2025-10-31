package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Producto;
import com.elolympus.data.Logistica.Marca;
import com.elolympus.data.Logistica.Linea;
import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.services.ProductoService;
import com.elolympus.services.services.MarcaService;
import com.elolympus.services.services.LineaService;
import com.elolympus.services.services.UnidadService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;

@PageTitle("Productos")
@Route(value = "productos/:ProductoID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class ProductosView extends Div implements BeforeEnterObserver {

    private final ProductoService productoService;
    private final MarcaService marcaService;
    private final LineaService lineaService;
    private final UnidadService unidadService;
    private BeanValidationBinder<Producto> binder;
    private Producto producto;

    public final String PRODUCTO_ID = "ProductoID";
    public final String PRODUCTO_EDIT_ROUTE_TEMPLATE = "productos/%s/edit";
    public Grid<Producto> gridProductos = new Grid<>(Producto.class, false);
    public final TextField txtCodigo = new TextField("Código", "", "Buscar por código");
    public final TextField txtNombre = new TextField("Nombre", "", "Buscar por nombre");
    public final ComboBox<Marca> cmbMarca = new ComboBox<>("Marca");
    public final ComboBox<Linea> cmbLinea = new ComboBox<>("Línea");
    public final Button btnFiltrar = new Button("BUSCAR", new Icon(VaadinIcon.FILTER));
    public final Button toggleButton = new Button("Búsqueda", new Icon(VaadinIcon.FILTER));

    public final HorizontalLayout tophl = new HorizontalLayout(txtCodigo, txtNombre, cmbMarca, cmbLinea, btnFiltrar);

    public FormLayout formLayout = new FormLayout();
    public TextField codigo = new TextField("Código", "");
    public TextField nombre = new TextField("Nombre", "");
    public TextArea descripcion = new TextArea("Descripción", "");
    public ComboBox<Marca> marca = new ComboBox<>("Marca");
    public ComboBox<Linea> linea = new ComboBox<>("Línea");
    public ComboBox<Unidad> unidad = new ComboBox<>("Unidad");
    public BigDecimalField precioCosto = new BigDecimalField("Precio Costo", "");
    public BigDecimalField precioVenta = new BigDecimalField("Precio Venta", "");
    public BigDecimalField stockMinimo = new BigDecimalField("Stock Mínimo", "");
    public BigDecimalField stockMaximo = new BigDecimalField("Stock Máximo", "");
    public BigDecimalField peso = new BigDecimalField("Peso", "");
    public BigDecimalField volumen = new BigDecimalField("Volumen", "");
    public TextField creador = new TextField("Creador", "");
    {
        creador.setReadOnly(true);
    }
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    public final SplitLayout splitLayout = new SplitLayout();

    @Autowired
    public ProductosView(ProductoService productoService, MarcaService marcaService, 
                        LineaService lineaService, UnidadService unidadService) {
        this.productoService = productoService;
        this.marcaService = marcaService;
        this.lineaService = lineaService;
        this.unidadService = unidadService;
        
        try {
            // Configure Form - solo bindear campos específicos manualmente
            binder = new BeanValidationBinder<>(Producto.class);
            // Bind campos específicos en lugar de automático
            binder.forField(codigo).bind(Producto::getCodigo, Producto::setCodigo);
            binder.forField(nombre).bind(Producto::getNombre, Producto::setNombre);
            binder.forField(descripcion).bind(Producto::getDescripcion, Producto::setDescripcion);
            binder.forField(marca).bind(Producto::getMarca, Producto::setMarca);
            binder.forField(linea).bind(Producto::getLinea, Producto::setLinea);
            binder.forField(unidad).bind(Producto::getUnidad, Producto::setUnidad);
            binder.forField(precioCosto).bind(Producto::getPrecioCosto, Producto::setPrecioCosto);
            binder.forField(precioVenta).bind(Producto::getPrecioVenta, Producto::setPrecioVenta);
            binder.forField(stockMinimo).bind(Producto::getStockMinimo, Producto::setStockMinimo);
            binder.forField(stockMaximo).bind(Producto::getStockMaximo, Producto::setStockMaximo);
            binder.forField(peso).bind(Producto::getPeso, Producto::setPeso);
            binder.forField(volumen).bind(Producto::getVolumen, Producto::setVolumen);
            // creador es solo lectura - solo se muestra, no se guarda
            binder.forField(creador).bind(Producto::getCreador, null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Load combo box data
        loadComboBoxData();

        addClassNames("productos-view");
        setSizeFull(); // Asegurar que la vista ocupe todo el espacio
        // Create UI
        this.createGridLayout(splitLayout);
        this.createEditorLayout(splitLayout);
        add(splitLayout);
        initStyles();
        
        // LLAMAR refreshGrid() DESPUÉS DE CREAR TODOS LOS COMPONENTES
        refreshGrid();

        // EVENTOS
        btnFiltrar.addClickListener(e -> onBtnFiltrar());
        txtCodigo.addValueChangeListener(e -> onBtnFiltrar());
        txtNombre.addValueChangeListener(e -> onBtnFiltrar());
        cmbMarca.addValueChangeListener(e -> onBtnFiltrar());
        cmbLinea.addValueChangeListener(e -> onBtnFiltrar());
        
        toggleButton.addClickListener(event -> {
            boolean isVisible = tophl.isVisible();
            tophl.setVisible(!isVisible);
            if (isVisible) {
                tophl.removeClassName("tophl-visible");
            } else {
                tophl.addClassName("tophl-visible");
            }
        });
        
        save.addClickListener(e -> onBtnSave());
        cancel.addClickListener(e -> onBtnCancel());
        delete.addClickListener(e -> onBtnDelete());
        gridProductos.asSingleSelect().addValueChangeListener(e -> asSingleSelect(e.getValue(), this.save));
    }

    private void loadComboBoxData() {
        // Load marcas
        List<Marca> marcas = marcaService.findActive();
        cmbMarca.setItems(marcas);
        cmbMarca.setItemLabelGenerator(Marca::getNombre);
        marca.setItems(marcas);
        marca.setItemLabelGenerator(Marca::getNombre);

        // Load lineas
        List<Linea> lineas = lineaService.findActive();
        cmbLinea.setItems(lineas);
        cmbLinea.setItemLabelGenerator(Linea::getNombre);
        linea.setItems(lineas);
        linea.setItemLabelGenerator(Linea::getNombre);

        // Load unidades
        List<Unidad> unidades = unidadService.findActive();
        unidad.setItems(unidades);
        unidad.setItemLabelGenerator(Unidad::getNombre);
    }

    public void initStyles() {
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(70); // 70% para el grid, 30% para el form
        toggleButton.addClassName("toggle-button");
        tophl.addClassName("tophl");
        tophl.setAlignItems(FlexComponent.Alignment.BASELINE);
    }

    private final SerializableBiConsumer<Span, Producto> EstadoComponenteActivo = (
            span, producto) -> {
        String theme = String.format("badge %s",
                producto.isEsActivo() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(producto.isEsActivo() ? "Activo" : "Desactivado");
    };

    private ComponentRenderer<Span, Producto> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setHeightFull();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        formLayout.add(codigo, nombre, descripcion, marca, linea, unidad, 
                      precioCosto, precioVenta, stockMinimo, stockMaximo, 
                      peso, volumen, creador);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private Component createGrid() {
        // No recrear el grid, usar la instancia existente
        gridProductos.setClassName("grilla");
        gridProductos.setHeightFull();
        gridProductos.addColumn(CrearComponmenteActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridProductos.addColumn(Producto::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridProductos.addColumn(Producto::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        gridProductos.addColumn(producto -> producto.getMarca() != null ? producto.getMarca().getNombre() : "")
                .setHeader("Marca")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridProductos.addColumn(producto -> producto.getLinea() != null ? producto.getLinea().getNombre() : "")
                .setHeader("Línea")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridProductos.addColumn(producto -> producto.getUnidad() != null ? producto.getUnidad().getNombre() : "")
                .setHeader("Unidad")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridProductos.addColumn(Producto::getPrecioVenta)
                .setHeader("Precio Venta")
                .setAutoWidth(true)
                .setFlexGrow(0);
        return gridProductos;
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        wrapper.getStyle().set("display", "flex");
        wrapper.getStyle().set("flex-direction", "column");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(toggleButton, tophl, createGrid());
    }

    // EVENTOS+++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void onBtnFiltrar() {
        onRefresh();
    }

    public void onRefresh() {
        String codigo = txtCodigo.getValue();
        String nombre = txtNombre.getValue();
        Marca selectedMarca = cmbMarca.getValue();
        Linea selectedLinea = cmbLinea.getValue();
        
        List<Producto> productosActivos = productoService.findActive();
        
        // Apply filters
        if (!codigo.isEmpty()) {
            productosActivos.removeIf(p -> !p.getCodigo().toLowerCase().contains(codigo.toLowerCase()));
        }
        if (!nombre.isEmpty()) {
            productosActivos.removeIf(p -> !p.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
        if (selectedMarca != null) {
            productosActivos.removeIf(p -> p.getMarca() == null || !p.getMarca().getId().equals(selectedMarca.getId()));
        }
        if (selectedLinea != null) {
            productosActivos.removeIf(p -> p.getLinea() == null || !p.getLinea().getId().equals(selectedLinea.getId()));
        }
        
        gridProductos.setItems(productosActivos);
        gridProductos.getDataProvider().refreshAll();
    }

    public void onBtnSave() {
        try {
            if (this.producto == null) {
                this.producto = new Producto();
            }
            binder.writeBean(this.producto);
            productoService.update(this.producto);
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(ProductosView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudieron actualizar los datos. Compruebe nuevamente que todos los valores sean válidos.");
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }

    public void onBtnCancel() {
        this.clearForm();
        this.refreshGrid();
    }

    public void onBtnDelete() {
        try {
            if (this.producto == null) {
                this.producto = new Producto();
            } else {
                this.producto.setActivo(false);
            }

            binder.writeBean(this.producto);
            productoService.update(this.producto);
            clearForm();
            refreshGrid();
            Notification.show("Producto Eliminado");
            UI.getCurrent().navigate(ProductosView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudo Eliminar el producto. Compruebe nuevamente.");
        }
    }

    public void asSingleSelect(Producto producto, Button btnSave) {
        if (producto != null) {
            btnSave.setText("Actualizar");
            UI.getCurrent().navigate(String.format(this.PRODUCTO_EDIT_ROUTE_TEMPLATE, producto.getId()));
        } else {
            clearForm();
            UI.getCurrent().navigate(ProductosView.class);
            btnSave.setText("Guardar");
        }
    }

    private void refreshGrid() {
        gridProductos.select(null);
        List<Producto> productosActivos = productoService.findActive();
        
        if (productosActivos.isEmpty()) {
            productosActivos = productoService.findAll();
        }
        
        gridProductos.setItems(productosActivos);
    }

    private void clearForm() {
        populateForm(null);
        txtCodigo.setValue("");
        txtNombre.setValue("");
        cmbMarca.clear();
        cmbLinea.clear();
    }

    private void populateForm(Producto value) {
        this.producto = value;
        binder.readBean(this.producto);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> ProductoId = event.getRouteParameters().get(this.PRODUCTO_ID).map(Long::parseLong);
        if (ProductoId.isPresent()) {
            Optional<Producto> productoFromBackend = productoService.get(ProductoId.get());
            if (productoFromBackend.isPresent()) {
                populateForm(productoFromBackend.get());
            } else {
                Notification.show(
                        String.format("El producto solicitado no fue encontrado, ID = %s", ProductoId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(ProductosView.class);
            }
        } else {
            // Si no hay ID específico, cargar la grilla con todos los productos activos
            refreshGrid();
        }
    }
}