package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Producto;
import com.elolympus.data.Logistica.Marca;
import com.elolympus.data.Logistica.Linea;
import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.ProductoService;
import com.elolympus.services.services.MarcaService;
import com.elolympus.services.services.LineaService;
import com.elolympus.services.services.UnidadService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@PageTitle("Productos")
@Route(value = "productos/:ProductoID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class ProductosView extends AbstractCrudView<Producto> {

    private final ProductoService productoService;
    private final MarcaService marcaService;
    private final LineaService lineaService;
    private final UnidadService unidadService;

    // Form fields
    private TextField codigo;
    private TextField nombre;
    private TextArea descripcion;
    private ComboBox<Marca> marca;
    private ComboBox<Linea> linea;
    private ComboBox<Unidad> unidad;
    private BigDecimalField precioCosto;
    private BigDecimalField precioVenta;
    private BigDecimalField stockMinimo;
    private BigDecimalField stockMaximo;
    private BigDecimalField peso;
    private BigDecimalField volumen;
    private TextField creador;

    // Filter fields
    private TextField txtCodigo;
    private TextField txtNombre;
    private ComboBox<Marca> cmbMarca;
    private ComboBox<Linea> cmbLinea;
    private Button btnBuscar;

    public ProductosView(ProductoService productoService, MarcaService marcaService,
                         LineaService lineaService, UnidadService unidadService) {
        super();
        this.productoService = productoService;
        this.marcaService = marcaService;
        this.lineaService = lineaService;
        this.unidadService = unidadService;
        initialize();
    }

    @Override
    protected Class<Producto> getEntityClass() {
        return Producto.class;
    }

    @Override
    protected AbstractCrudService<Producto, ?> getService() {
        return productoService;
    }

    @Override
    protected String getViewClassName() {
        return "productos-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return ProductosView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "ProductoID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "productos/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Producto";
    }

    @Override
    protected Producto createNewEntity() {
        return new Producto();
    }

    @Override
    protected void configureBinder() {
        // El binder ya está inicializado en la clase base
    }

    @Override
    protected void configureGrid(Grid<Producto> grid) {
        // Usar el método isEsActivo() de Producto en lugar de isActivo()
        grid.addColumn(producto -> {
            boolean isActivo = producto.isEsActivo();
            return isActivo ? "Activo" : "Desactivado";
        })
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Producto::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Producto::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(producto -> producto.getMarca() != null ? producto.getMarca().getNombre() : "")
                .setHeader("Marca")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(producto -> producto.getLinea() != null ? producto.getLinea().getNombre() : "")
                .setHeader("Línea")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(producto -> producto.getUnidad() != null ? producto.getUnidad().getNombre() : "")
                .setHeader("Unidad")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Producto::getPrecioVenta)
                .setHeader("Precio Venta")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        codigo = new TextField("Código");
        nombre = new TextField("Nombre");
        descripcion = new TextArea("Descripción");
        marca = new ComboBox<>("Marca");
        linea = new ComboBox<>("Línea");
        unidad = new ComboBox<>("Unidad");
        precioCosto = new BigDecimalField("Precio Costo");
        precioVenta = new BigDecimalField("Precio Venta");
        stockMinimo = new BigDecimalField("Stock Mínimo");
        stockMaximo = new BigDecimalField("Stock Máximo");
        peso = new BigDecimalField("Peso");
        volumen = new BigDecimalField("Volumen");
        creador = new TextField("Creador");
        creador.setReadOnly(true);

        // Los ComboBoxes se configurarán en initialize()
        marca.setItemLabelGenerator(Marca::getNombre);
        linea.setItemLabelGenerator(Linea::getNombre);
        unidad.setItemLabelGenerator(Unidad::getNombre);

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
        binder.forField(creador).bind(Producto::getCreador, null);

        formLayout.add(codigo, nombre, descripcion, marca, linea, unidad,
                      precioCosto, precioVenta, stockMinimo, stockMaximo,
                      peso, volumen, creador);
    }

    @Override
    protected void initialize() {
        // Primero llamar al método padre para crear todos los campos
        super.initialize();
        
        // Después configurar los items de los ComboBoxes cuando ya están creados
        if (marca != null && cmbMarca != null) {
            List<Marca> marcas = marcaService.findActive();
            marca.setItems(marcas);
            cmbMarca.setItems(marcas);
        }

        if (linea != null && cmbLinea != null) {
            List<Linea> lineas = lineaService.findActive();
            linea.setItems(lineas);
            cmbLinea.setItems(lineas);
        }

        if (unidad != null) {
            List<Unidad> unidades = unidadService.findActive();
            unidad.setItems(unidades);
        }
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected HorizontalLayout createFilterLayout() {
        txtCodigo = new TextField("Código", "Buscar por código");
        txtNombre = new TextField("Nombre", "Buscar por nombre");
        cmbMarca = new ComboBox<>("Marca");
        cmbMarca.setItemLabelGenerator(Marca::getNombre);
        cmbLinea = new ComboBox<>("Línea");
        cmbLinea.setItemLabelGenerator(Linea::getNombre);
        btnBuscar = new Button("BUSCAR", new Icon(VaadinIcon.SEARCH));

        HorizontalLayout filterLayout = new HorizontalLayout(txtCodigo, txtNombre, cmbMarca, cmbLinea, btnBuscar);
        filterLayout.setClassName("tophl");
        return filterLayout;
    }

    @Override
    protected void setupFilterListeners() {
        txtCodigo.addValueChangeListener(e -> onRefresh());
        txtNombre.addValueChangeListener(e -> onRefresh());
        cmbMarca.addValueChangeListener(e -> onRefresh());
        cmbLinea.addValueChangeListener(e -> onRefresh());
        btnBuscar.addClickListener(e -> onRefresh());
    }

    @Override
    protected List<Producto> applyFilters(List<Producto> productos) {
        String codigoFilter = txtCodigo.getValue();
        String nombreFilter = txtNombre.getValue();
        Marca selectedMarca = cmbMarca.getValue();
        Linea selectedLinea = cmbLinea.getValue();

        // Apply filters
        if (codigoFilter != null && !codigoFilter.isEmpty()) {
            productos.removeIf(p -> !p.getCodigo().toLowerCase().contains(codigoFilter.toLowerCase()));
        }
        if (nombreFilter != null && !nombreFilter.isEmpty()) {
            productos.removeIf(p -> !p.getNombre().toLowerCase().contains(nombreFilter.toLowerCase()));
        }
        if (selectedMarca != null) {
            productos.removeIf(p -> p.getMarca() == null || !p.getMarca().getId().equals(selectedMarca.getId()));
        }
        if (selectedLinea != null) {
            productos.removeIf(p -> p.getLinea() == null || !p.getLinea().getId().equals(selectedLinea.getId()));
        }

        return productos;
    }

    @Override
    protected void clearFilters() {
        if (txtCodigo != null) txtCodigo.clear();
        if (txtNombre != null) txtNombre.clear();
        if (cmbMarca != null) cmbMarca.clear();
        if (cmbLinea != null) cmbLinea.clear();
    }

    @Override
    protected void beforeSave(Producto entity) {
        // Asegurar que las relaciones estén establecidas
        if (marca.getValue() != null) {
            entity.setMarca(marca.getValue());
        }
        if (linea.getValue() != null) {
            entity.setLinea(linea.getValue());
        }
        if (unidad.getValue() != null) {
            entity.setUnidad(unidad.getValue());
        }
    }
}