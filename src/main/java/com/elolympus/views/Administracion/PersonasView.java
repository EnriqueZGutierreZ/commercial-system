package com.elolympus.views.Administracion;

import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.services.AbstractCrudService;
import com.elolympus.services.services.DireccionService;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.UbigeoService;
import com.elolympus.views.AbstractCrudView;
import com.elolympus.views.MainLayout;
import com.elolympus.views.direccion.DireccionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@PageTitle("Personas")
@Route(value = "persona/:PersonaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class PersonasView extends AbstractCrudView<Persona> {

    private final PersonaService personaService;
    private final UbigeoService ubigeoService;
    private final DireccionService direccionService;
    private Direccion direccion;

    // Campos de formulario adicionales
    public TextField nombres = new TextField("Nombres", "");
    public TextField apellidos = new TextField("Apellidos", "");
    public ComboBox<String> tipo_documento = new ComboBox<>("Tipo de Documento");
    public IntegerField num_documento = new IntegerField("DNI", "");
    public IntegerField celular = new IntegerField("Celular", "");
    public TextField email = new TextField("Correo", "");
    public TextField sexo = new TextField("Sexo", "");
    public TextField creador = new TextField("Creador", "");
    public TextField txtdireccion = new TextField("Direccion", "");
    {
        txtdireccion.setReadOnly(true);
    }
    private final Button btnDireccion = new Button("Obtener Direccion");

    // Campos de búsqueda - inicializados inline
    private final TextField txtDni = new TextField("DNI", "", "Busqueda por DNI");
    private final TextField txtNombres = new TextField("Nombres", "", "Busqueda por Nombres");
    private final TextField txtApellidos = new TextField("Apellidos", "", "Busqueda por Apellidos");

    @Autowired
    public PersonasView(PersonaService personaService, UbigeoService ubigeoService, DireccionService direccionService) {
        super();
        this.personaService = personaService;
        this.ubigeoService = ubigeoService;
        this.direccionService = direccionService;
        
        // Configurar items del ComboBox ANTES de cualquier binding
        tipo_documento.setItems("DNI", "RUC", "Carné de Extranjería", "Pasaporte");
        tipo_documento.setPlaceholder("Seleccione tipo de documento");
        
        // Eventos de búsqueda
        txtDni.addValueChangeListener(e -> applyFilter());
        txtApellidos.addValueChangeListener(e -> applyFilter());
        txtNombres.addValueChangeListener(e -> applyFilter());
        btnDireccion.addClickListener(e -> getDireccion());
        
        initialize();
    }

    @Override
    protected Class<Persona> getEntityClass() {
        return Persona.class;
    }

    @Override
    protected AbstractCrudService<Persona, ?> getService() {
        return personaService;
    }

    @Override
    protected String getViewClassName() {
        return "persona-view";
    }

    @Override
    protected Class<? extends Component> getViewClass() {
        return PersonasView.class;
    }

    @Override
    protected String getEntityIdParam() {
        return "PersonaID";
    }

    @Override
    protected String getEditRouteTemplate() {
        return "persona/%s/edit";
    }

    @Override
    protected String getEntityName() {
        return "Persona";
    }

    @Override
    protected Persona createNewEntity() {
        return new Persona();
    }

    @Override
    protected void configureBinder() {
        // Configurar binding explícito para cada campo para mayor control
        binder.forField(nombres)
                .bind(Persona::getNombres, Persona::setNombres);
        
        binder.forField(apellidos)
                .bind(Persona::getApellidos, Persona::setApellidos);
        
        binder.forField(num_documento)
                .bind(Persona::getNum_documento, Persona::setNum_documento);
        
        binder.forField(celular)
                .bind(Persona::getCelular, Persona::setCelular);
        
        binder.forField(email)
                .bind(Persona::getEmail, Persona::setEmail);
        
        binder.forField(sexo)
                .bind(Persona::getSexo, Persona::setSexo);
        
        binder.forField(creador)
                .bind(Persona::getCreador, Persona::setCreador);
        
        // Configurar el binding personalizado del tipo_documento con converter
        configurarTipoDocumento();
    }

    @Override
    protected void configureGrid(Grid<Persona> grid) {
        grid.addColumn(createActivoRenderer()).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(Persona::getNum_documento).setHeader("DNI").setAutoWidth(true);
        grid.addColumn(Persona::getNombres).setHeader("Nombres").setAutoWidth(true);
        grid.addColumn(Persona::getApellidos).setHeader("Apellidos").setAutoWidth(true);
        grid.addColumn(Persona::getCelular).setHeader("Celular").setAutoWidth(true);
    }

    @Override
    protected void configureFormLayout(FormLayout formLayout) {
        HorizontalLayout direccionLayout = new HorizontalLayout(txtdireccion, btnDireccion);
        direccionLayout.setAlignItems(FlexComponent.Alignment.END);
        direccionLayout.setWidthFull();
        txtdireccion.setWidthFull();
        
        formLayout.add(nombres, apellidos, tipo_documento, num_documento, celular, email, sexo, creador);
        formLayout.add(direccionLayout);
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected HorizontalLayout createFilterLayout() {
        txtDni.setPlaceholder("Busqueda por DNI");
        txtNombres.setPlaceholder("Busqueda por Nombres");
        txtApellidos.setPlaceholder("Busqueda por Apellidos");
        
        // Crear botón de búsqueda
        Button btnBuscar = new Button("BUSCAR");
        btnBuscar.addClickListener(e -> applyFilter());
        
        HorizontalLayout searchLayout = new HorizontalLayout(txtDni, txtNombres, txtApellidos, btnBuscar);
        searchLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        return searchLayout;
    }

    @Override
    protected void setupFilterListeners() {
        // Ya configurado en el constructor
    }

    @Override
    protected void clearFilters() {
        if (txtDni != null) txtDni.clear();
        if (txtNombres != null) txtNombres.clear();
        if (txtApellidos != null) txtApellidos.clear();
    }

    private void applyFilter() {
        String dni = txtDni.getValue();
        String apellidos = txtApellidos.getValue();
        String nombres = txtNombres.getValue();
        List<Persona> personasActivas = personaService.numDocumnetoNombresApellidosActivosContainsIgnoreCase(dni, nombres, apellidos);
        grid.setItems(personasActivas);
    }

    protected void customRefreshGrid() {
        List<Persona> personasActivas = personaService.numDocumnetoNombresApellidosActivosContainsIgnoreCase("", "", "");
        grid.setItems(personasActivas);
    }

    @Override
    protected void beforeSave(Persona entity) {
        // Lógica especial para guardar persona con dirección
        if (this.direccion != null) {
            try {
                // Usar el método especializado del servicio
                Persona savedPersona = personaService.savePersonaWithDireccion(entity, this.direccion, direccionService);
                // Actualizar la referencia local
                if (savedPersona != null && savedPersona.getId() != null) {
                    entity.setId(savedPersona.getId());
                    entity.setVersion(savedPersona.getVersion());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar persona con dirección: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void populateForm(Persona entity) {
        try {
            // Llamar al método padre para el binding automático
            super.populateForm(entity);
            
            // Asegurar que los campos se pueblan correctamente
            if (entity != null) {
                // Verificar y establecer valores manualmente si el binding automático falló
                if (nombres.getValue() == null || nombres.getValue().isEmpty()) {
                    nombres.setValue(entity.getNombres() != null ? entity.getNombres() : "");
                }
                if (apellidos.getValue() == null || apellidos.getValue().isEmpty()) {
                    apellidos.setValue(entity.getApellidos() != null ? entity.getApellidos() : "");
                }
                if (num_documento.getValue() == null && entity.getNum_documento() != null) {
                    num_documento.setValue(entity.getNum_documento());
                }
                if (celular.getValue() == null && entity.getCelular() != null) {
                    celular.setValue(entity.getCelular());
                }
                if (email.getValue() == null || email.getValue().isEmpty()) {
                    email.setValue(entity.getEmail() != null ? entity.getEmail() : "");
                }
                if (sexo.getValue() == null || sexo.getValue().isEmpty()) {
                    sexo.setValue(entity.getSexo() != null ? entity.getSexo() : "");
                }
                if (creador.getValue() == null || creador.getValue().isEmpty()) {
                    creador.setValue(entity.getCreador() != null ? entity.getCreador() : "");
                }
                
                // Establecer el tipo de documento manualmente si no se estableció automáticamente
                if (tipo_documento.getValue() == null && entity.getTipo_documento() != null) {
                    String tipoDocumentoStr = switch (entity.getTipo_documento()) {
                        case 1 -> "DNI";
                        case 2 -> "RUC";
                        case 3 -> "Carné de Extranjería";
                        case 4 -> "Pasaporte";
                        default -> null;
                    };
                    if (tipoDocumentoStr != null) {
                        tipo_documento.setValue(tipoDocumentoStr);
                    }
                }
            }
            
            // Cargar la información de la dirección si existe
            if (entity != null && entity.getDireccion() != null) {
                this.direccion = entity.getDireccion();
                String direccionTexto = "";
                if (this.direccion.getDescripcion() != null && this.direccion.getReferencia() != null) {
                    direccionTexto = this.direccion.getDescripcion() + " - " + this.direccion.getReferencia();
                } else if (this.direccion.getDescripcion() != null) {
                    direccionTexto = this.direccion.getDescripcion();
                } else if (this.direccion.getReferencia() != null) {
                    direccionTexto = this.direccion.getReferencia();
                }
                txtdireccion.setValue(direccionTexto);
            } else {
                this.direccion = null;
                txtdireccion.setValue("");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR en populateForm: " + e.getMessage());
            e.printStackTrace();
            Notification.show("Error al cargar los datos de la persona: " + e.getMessage());
        }
    }

    protected void afterClearForm() {
        txtDni.setValue("");
        txtApellidos.setValue("");
        txtNombres.setValue("");
        this.direccion = null;
        txtdireccion.setValue("");
    }

    private void getDireccion() {
        DireccionView direccionView = new DireccionView(ubigeoService, direccionService);
        direccionView.setHeaderTitle("DIRECCION");
        
        // Si ya existe una dirección, cargarla en el formulario para edición
        if (this.direccion != null) {
            direccionView.direccion = this.direccion;
            direccionView.populateForm();
        }
        
        direccionView.addDireccionGuardadaListener(event -> {
            Direccion direccionGuardada = event.getDireccion();
            if (direccionGuardada != null) {
                try {
                    // SIEMPRE recargar la dirección desde BD para garantizar que esté gestionada
                    if (direccionGuardada.getId() != null) {
                        Optional<Direccion> direccionFromDb = direccionService.get(direccionGuardada.getId());
                        if (direccionFromDb.isPresent()) {
                            // Usar la versión gestionada desde BD
                            this.direccion = direccionFromDb.get();
                        } else {
                            // Si no se encuentra, crear una copia local para guardar después
                            this.direccion = new Direccion();
                            this.direccion.setDescripcion(direccionGuardada.getDescripcion());
                            this.direccion.setReferencia(direccionGuardada.getReferencia());
                            this.direccion.setUbigeo(direccionGuardada.getUbigeo());
                        }
                    } else {
                        // Dirección nueva sin ID - crear copia local
                        this.direccion = new Direccion();
                        this.direccion.setDescripcion(direccionGuardada.getDescripcion());
                        this.direccion.setReferencia(direccionGuardada.getReferencia());
                        this.direccion.setUbigeo(direccionGuardada.getUbigeo());
                    }
                    
                    String descripcionTexto = "";
                    if (direccionGuardada.getDescripcion() != null && direccionGuardada.getReferencia() != null) {
                        descripcionTexto = direccionGuardada.getDescripcion() + " - " + direccionGuardada.getReferencia();
                    } else if (direccionGuardada.getDescripcion() != null) {
                        descripcionTexto = direccionGuardada.getDescripcion();
                    } else if (direccionGuardada.getReferencia() != null) {
                        descripcionTexto = direccionGuardada.getReferencia();
                    }
                    txtdireccion.setValue(descripcionTexto);
                    
                    // NO asociar inmediatamente - esperar a que el usuario haga click en Guardar
                    Notification.show("Dirección cargada. Haga click en Guardar para asociarla a la persona", 3000, Notification.Position.MIDDLE);
                    
                } catch (Exception e) {
                    Notification.show("Error al cargar la dirección: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                    this.direccion = null;
                }
            }
        });
        direccionView.open();
    }

    private void configurarTipoDocumento() {
        // Configurar el binding para convertir String a Integer y viceversa
        binder.forField(tipo_documento)
                .withConverter(
                    // String to Integer converter
                    tipoStr -> {
                        if (tipoStr == null) return null;
                        return switch (tipoStr) {
                            case "DNI" -> 1;
                            case "RUC" -> 2;
                            case "Carné de Extranjería" -> 3;
                            case "Pasaporte" -> 4;
                            default -> null;
                        };
                    },
                    // Integer to String converter
                    tipoInt -> {
                        if (tipoInt == null) return null;
                        return switch (tipoInt) {
                            case 1 -> "DNI";
                            case 2 -> "RUC";
                            case 3 -> "Carné de Extranjería";
                            case 4 -> "Pasaporte";
                            default -> "";
                        };
                    },
                    "Seleccione un tipo de documento válido"
                )
                .bind(Persona::getTipo_documento, Persona::setTipo_documento);
    }

    private final SerializableBiConsumer<Span, Persona> EstadoComponenteActivo = (
            span, persona) -> {
        String theme = String.format("badge %s",
                persona.isActivo() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(persona.isActivo() ? "Activo" : "Desactivado");
    };

    private ComponentRenderer<Span, Persona> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }
}

