package com.elolympus.views.Administracion;

import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.services.DireccionService;
import com.elolympus.services.services.PersonaService;
import com.elolympus.services.services.UbigeoService;
import com.elolympus.views.MainLayout;
import com.elolympus.views.direccion.DireccionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
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
import java.util.Optional;

import java.util.List;

@PageTitle("Personas")
@Route(value = "persona/:PersonaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class PersonasView extends Div implements BeforeEnterObserver{

    private final PersonaService PersonaService;
    private final UbigeoService ubigeoService;
    private final DireccionService direccionService;
    private BeanValidationBinder<Persona> binder;
    private Persona persona;
    private Direccion direccion;

    public final String PERSONA_ID = "PersonaID";
    public final String PERSONA_EDIT_ROUTE_TEMPLATE = "persona/%s/edit";
    public Grid<Persona> gridPersonas       = new Grid<>(Persona.class,false);
    public final TextField txtDni           = new TextField("DNI","","Busqueda por DNI");
    public final TextField txtNombres       = new TextField("Nombres","","Busqueda por Nombres");
    public final TextField txtApellidos     = new TextField("Apellidos","","Busqueda por Apellidos");
    public final Button btnFiltrar          = new Button("BUSCAR",new Icon(VaadinIcon.FILTER));
    public final Button toggleButton        = new Button("Busqueda", new Icon(VaadinIcon.FILTER));

    public final HorizontalLayout tophl     = new HorizontalLayout(txtDni,txtNombres,txtApellidos,btnFiltrar);

    public FormLayout formLayout = new FormLayout();
    public TextField nombres           = new TextField("Nombres","");
    public TextField apellidos         = new TextField("Apellidos","");
    public ComboBox<String> tipo_documento = new ComboBox<>("Tipo de Documento");
    public IntegerField num_documento     = new IntegerField("DNI","");
    public IntegerField celular           = new IntegerField("Celular","");
    public TextField email             = new TextField("Correo","");
    public TextField sexo              = new TextField("Sexo","");
    public TextField creador           = new TextField("Creador", "");
    public TextField txtdireccion      = new TextField("Direccion", "");
    {
        txtdireccion.setReadOnly(true);
    }
    private final Button btnDireccion  = new Button("Obtener Direccion");
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button delete = new Button("Eliminar",VaadinIcon.TRASH.create());
    public final SplitLayout splitLayout = new SplitLayout();

    @Autowired
    public PersonasView(PersonaService PersonaService, UbigeoService ubigeoService, DireccionService direccionService) {
        this.PersonaService = PersonaService;
        this.ubigeoService = ubigeoService;
        this.direccionService = direccionService;
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Persona.class);
            // Bind fields. This is where you'd define e.g. validation rules
            binder.bindInstanceFields(this);
        }catch (Exception e){
            System.out.println("ERRORRRR: " + e.getMessage());
        }

        // Configurar ComboBox de tipo de documento
        configurarTipoDocumento();

        addClassNames("persona-view");
        // Create UI
        this.createGridLayout(splitLayout);
        this.createEditorLayout(splitLayout);
        add(splitLayout);
        initStyles();

        //EVENTOS
        btnFiltrar.addClickListener(e->onBtnFiltrar());
        txtDni.addValueChangeListener(e->onBtnFiltrar());
        txtApellidos.addValueChangeListener(e->onBtnFiltrar());
        txtNombres.addValueChangeListener(e->onBtnFiltrar());
        btnDireccion.addClickListener(e->getDireccion());
        toggleButton.addClickListener(event -> {
            boolean isVisible = tophl.isVisible();
            tophl.setVisible(!isVisible);
            if (isVisible) {
                tophl.removeClassName("tophl-visible");
            } else {
                tophl.addClassName("tophl-visible");
            }
        });
        save.addClickListener(e->onBtnSave());
        cancel.addClickListener(e->onBtnCancel());
        delete.addClickListener(e->onBtnDelete());
        gridPersonas.asSingleSelect().addValueChangeListener(e->asSingleSelect(e.getValue(),this.save));
        refreshGrid();
    }

    public void initStyles(){
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        splitLayout.setSizeFull();
        toggleButton.addClassName("toggle-button");
        tophl.addClassName("tophl");
        tophl.setAlignItems(FlexComponent.Alignment.BASELINE);
    }

    private final SerializableBiConsumer<Span, Persona> EstadoComponenteActivo = (
            span, persona) -> {
        String theme = String.format("badge %s",
                persona.isActivo() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(persona.isActivo()?"Activo":"Desactivado");
    };

    private ComponentRenderer<Span, Persona> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }
    private void createEditorLayout(SplitLayout splitLayout) {

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setHeightFull();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        HorizontalLayout direccionLayout = new HorizontalLayout(txtdireccion, btnDireccion);
        direccionLayout.setAlignItems(FlexComponent.Alignment.END);
        direccionLayout.setWidthFull();
        txtdireccion.setWidthFull();
        formLayout.add(nombres, apellidos,
                tipo_documento, num_documento, celular, email, sexo, creador);
        formLayout.add(direccionLayout);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(save, cancel,delete);
        editorLayoutDiv.add(buttonLayout);
    }
    private Component createGrid() {
        gridPersonas = new Grid<>(Persona.class, false);
        gridPersonas.setClassName("grilla");
        gridPersonas.setHeight("86%");
        gridPersonas.addColumn(CrearComponmenteActivoRenderer())          .setHeader("Activo")       .setAutoWidth(true);
        gridPersonas.addColumn(Persona::getNum_documento)   .setHeader("DNI")          .setAutoWidth(true);
        gridPersonas.addColumn(Persona::getNombres)         .setHeader("Nombres")      .setAutoWidth(true);
        gridPersonas.addColumn(Persona::getApellidos)       .setHeader("Apellidos")    .setAutoWidth(true);
        gridPersonas.addColumn(Persona::getCelular)         .setHeader("Celular") .setAutoWidth(true);
        return gridPersonas;
    }
    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(toggleButton,tophl,createGrid());
    }

    //EVENTOS+++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void onBtnFiltrar() {
        onRefresh();
    }

    public void onRefresh() {
        String dni = txtDni.getValue();
        String apellidos = txtApellidos.getValue();
        String nombres = txtNombres.getValue();
        List<Persona> personasActivas = PersonaService.numDocumnetoNombresApellidosActivosContainsIgnoreCase(dni,nombres,apellidos);
        gridPersonas.setItems(personasActivas);
        gridPersonas.getDataProvider().refreshAll();

    }

    public void onBtnSave() {
        try {
            // Prevenir múltiples clicks deshabilitando el botón
            save.setEnabled(false);
            
            Persona personaToSave;
            
            if (this.persona == null) {
                // Persona nueva
                personaToSave = new Persona();
            } else if (this.persona.getId() != null) {
                // Persona existente - recargar desde la base de datos para evitar entidades detached
                Optional<Persona> personaFromDb = PersonaService.get(this.persona.getId());
                if (personaFromDb.isPresent()) {
                    personaToSave = personaFromDb.get();
                } else {
                    // Si no se encuentra en la DB, crear nueva
                    personaToSave = new Persona();
                }
            } else {
                // Persona sin ID - es nueva
                personaToSave = new Persona();
            }
            
            // Escribir los datos del formulario a la entidad
            binder.writeBean(personaToSave);
            
            // Usar el método especializado para guardar persona con dirección
            if (this.direccion != null) {
                try {
                    this.persona = PersonaService.savePersonaWithDireccion(personaToSave, this.direccion, direccionService);
                } catch (Exception e) {
                    throw new RuntimeException("Error al guardar persona con dirección: " + e.getMessage(), e);
                }
            } else {
                // Guardar persona sin dirección
                this.persona = PersonaService.update(personaToSave);
            }
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(PersonasView.class);
            
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudieron actualizar los datos. Compruebe nuevamente que todos los valores sean válidos.");
        } catch (Exception e) {
            Notification.show("Error inesperado: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        } finally {
            // Rehabilitar el botón siempre
            save.setEnabled(true);
        }
    }

    public void onBtnCancel() {
        this.clearForm();
        this.refreshGrid();
    }

    public void onBtnDelete() {
        try {
            // Prevenir múltiples clicks
            delete.setEnabled(false);
            
            if (this.persona == null || this.persona.getId() == null) {
                Notification.show("Seleccione una persona para eliminar");
                return;
            }
            
            // Recargar la persona desde la base de datos para evitar entidades detached
            Optional<Persona> personaFromDb = PersonaService.get(this.persona.getId());
            if (personaFromDb.isPresent()) {
                Persona personaToDelete = personaFromDb.get();
                personaToDelete.setActivo(false);
                PersonaService.update(personaToDelete);
                clearForm();
                refreshGrid();
                Notification.show("Persona Eliminada");
                UI.getCurrent().navigate(PersonasView.class);
            } else {
                Notification.show("La persona ya no existe en la base de datos");
            }
            
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Error inesperado al eliminar: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        } finally {
            // Rehabilitar el botón
            delete.setEnabled(true);
        }
    }


    public void asSingleSelect(Persona persona, Button btnSave) {
        if (persona != null) {
            btnSave.setText("Actualizar");
            
            // Recargar la persona completa desde la base de datos para asegurar que la dirección esté cargada
            Optional<Persona> personaCompleta = PersonaService.get(persona.getId());
            if (personaCompleta.isPresent()) {
                populateForm(personaCompleta.get());
            } else {
                populateForm(persona);
            }
        } else {
            clearForm();
            btnSave.setText("Guardar");
        }
    }

    private void refreshGrid() {
        gridPersonas.select(null);
        List<Persona> personasActivas = PersonaService.numDocumnetoNombresApellidosActivosContainsIgnoreCase("","","");
        gridPersonas.setItems(personasActivas);
        //gridPersonas.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
        txtDni.setValue("");
        txtApellidos.setValue("");
        txtNombres.setValue("");
    }

    private void populateForm(Persona value) {
        this.persona = value;
        binder.readBean(this.persona);
        
        // Cargar la información de la dirección si existe
        if (this.persona != null) {
            if (this.persona.getDireccion() != null) {
                this.direccion = this.persona.getDireccion();
                txtdireccion.setValue(this.direccion.getDescripcion() + " - " + this.direccion.getReferencia());
            } else {
                this.direccion = null;
                txtdireccion.setValue("");
            }
        } else {
            this.direccion = null;
            txtdireccion.setValue("");
        }
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
                        Optional<Direccion> direccionFromDb = direccionService.findById(direccionGuardada.getId());
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> PersonaId = event.getRouteParameters().get(this.PERSONA_ID).map(Long::parseLong);
        if (PersonaId.isPresent()) {
            Optional<Persona> samplePersonFromBackend = PersonaService.get(PersonaId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", PersonaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PersonasView.class);
            }
        }
    }

    private void configurarTipoDocumento() {
        // Configurar opciones del ComboBox
        tipo_documento.setItems("DNI", "RUC", "Carné de Extranjería", "Pasaporte");
        tipo_documento.setPlaceholder("Seleccione tipo de documento");
        
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
}

