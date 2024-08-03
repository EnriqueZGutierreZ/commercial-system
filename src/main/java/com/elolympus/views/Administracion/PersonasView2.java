package com.elolympus.views.Administracion;

import com.elolympus.component.DataTable;
import com.elolympus.data.Administracion.Direccion;
import com.elolympus.data.Administracion.Persona;
import com.elolympus.services.services.PersonaService;
import com.elolympus.views.Logistica.OrdenCompraView;
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

@PageTitle("Personas")
@Route(value = "persona2/:PersonaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class PersonasView2 extends Div implements BeforeEnterObserver{

    private final PersonaService PersonaService;
    private BeanValidationBinder<Persona> binder;
    private Persona persona;
    private Direccion direccion;

    public final String PERSONA_ID                  = "PersonaID";
    public final String PERSONA_EDIT_ROUTE_TEMPLATE = "persona/%s/edit";
    public Grid<Persona>    gridPersonas     = new Grid<>(Persona.class,false);
    public final TextField  txtDni           = new TextField("DNI","","Busqueda por DNI");
    public final TextField  txtNombres       = new TextField("Nombres","","Busqueda por Nombres");
    public final TextField  txtApellidos     = new TextField("Apellidos","","Busqueda por Apellidos");
    public final Button     btnFiltrar       = new Button("BUSCAR",new Icon(VaadinIcon.FILTER));
    public final Button     toggleButton     = new Button("Busqueda", new Icon(VaadinIcon.FILTER));

    public final HorizontalLayout tophl     = new HorizontalLayout(txtDni,txtNombres,txtApellidos,btnFiltrar);

    public FormLayout formLayout = new FormLayout();
    public TextField    nombres           = new TextField("Nombres","");
    public TextField    apellidos         = new TextField("Apellidos","");
    public IntegerField tipo_documento    = new IntegerField("Tipo de Documento","");
    public IntegerField num_documento     = new IntegerField("DNI","");
    public IntegerField celular           = new IntegerField("Celular","");
    public TextField    email             = new TextField("Correo","");
    public TextField    sexo              = new TextField("Sexo","");
    public TextField    txtdireccion      = new TextField("Direccion", "");
    private final Button btnDireccion     = new Button("Obtener Direccion");
    private final Button cancel           = new Button("Cancelar");
    private final Button save             = new Button("Guardar");
    private final Button delete           = new Button("Eliminar",VaadinIcon.TRASH.create());
    public final SplitLayout splitLayout  = new SplitLayout();

    public DataTable<Persona> grilla = new DataTable<>();

    @Autowired
    public PersonasView2(PersonaService PersonaService) {
        this.PersonaService = PersonaService;
        try {
            // Configure Form
            binder = new BeanValidationBinder<>(Persona.class);
            // Bind fields. This is where you'd define e.g. validation rules
            binder.bindInstanceFields(this);
        }catch (Exception e){
            System.out.println("ERRORRRR: " + e.getMessage());
        }


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
        btnDireccion.addClickListener(e->getDireccion());
        gridPersonas.asSingleSelect().addValueChangeListener(e->asSingleSelect(e.getValue(),this.save));
        refreshGrid();
    }

    public void initStyles(){
        txtdireccion.setEnabled(false);
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
        formLayout.add(nombres, apellidos,
                tipo_documento, num_documento, celular, email, sexo, txtdireccion, btnDireccion);
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
            if (this.persona == null) {
                this.persona = new Persona();
            }
            binder.writeBean(this.persona);
            PersonaService.update(this.persona);
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(PersonasView2.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudieron actualizar los datos. Compruebe nuevamente que todos los valores sean válidos.");
        }
    }

    public void onBtnCancel() {
        this.clearForm();
        this.refreshGrid();
    }

    public void onBtnDelete() {
        try {
            if (this.persona == null) {
                this.persona = new Persona();
            }else{this.persona.setActivo(false);}

            binder.writeBean(this.persona);
            PersonaService.update(this.persona);
            clearForm();
            refreshGrid();
            Notification.show("Persona Eliminada");
            UI.getCurrent().navigate(PersonasView2.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudo Eliminar a la persona. Compruebe nuevamente.");
        }
    }

    private void getDireccion() {
        DireccionView direccionView = new DireccionView();
        Dialog view = new Dialog();
        view.setHeaderTitle("DIRECCION");
        view.add(direccionView);
        view.open();
        this.direccion = direccionView.direccion;
        this.txtdireccion.setValue(direccionView.direccion.toString());
    }


    public void asSingleSelect(Persona persona, Button btnSave) {
        if (persona != null) {
            btnSave.setText("Actualizar");
            UI.getCurrent().navigate(String.format(this.PERSONA_EDIT_ROUTE_TEMPLATE, persona.getId()));
        } else {
            clearForm();
            UI.getCurrent().navigate(PersonasView2.class);
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
    }

    private void add(){
        DireccionView ordenCompra = new DireccionView();
        Dialog view = new Dialog();
        view.setHeaderTitle("ORDEN DE COMPRA DETALLE");
        view.add(ordenCompra);
        view.open();
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
                event.forwardTo(PersonasView2.class);
            }
        }
    }
}

