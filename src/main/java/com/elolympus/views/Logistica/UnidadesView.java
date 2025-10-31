package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Unidad;
import com.elolympus.services.services.UnidadService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

@PageTitle("Unidades")
@Route(value = "unidades/:UnidadID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class UnidadesView extends Div implements BeforeEnterObserver {

    private final UnidadService unidadService;
    private BeanValidationBinder<Unidad> binder;
    private Unidad unidad;

    public final String UNIDAD_ID = "UnidadID";
    public final String UNIDAD_EDIT_ROUTE_TEMPLATE = "unidades/%s/edit";
    public Grid<Unidad> gridUnidades = new Grid<>(Unidad.class, false);
    public final TextField txtNombre = new TextField("Nombre", "", "Buscar por nombre");
    public final TextField txtAbreviatura = new TextField("Abreviatura", "", "Buscar por abreviatura");
    public final Button btnFiltrar = new Button("BUSCAR", new Icon(VaadinIcon.FILTER));
    public final Button toggleButton = new Button("Búsqueda", new Icon(VaadinIcon.FILTER));

    public final HorizontalLayout tophl = new HorizontalLayout(txtNombre, txtAbreviatura, btnFiltrar);

    public FormLayout formLayout = new FormLayout();
    public TextField nombre = new TextField("Nombre", "");
    public TextField abreviatura = new TextField("Abreviatura", "");
    public TextField descripcion = new TextField("Descripción", "");
    public TextField creador = new TextField("Creador", "");
    {
        creador.setReadOnly(true);
    }
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    public final SplitLayout splitLayout = new SplitLayout();

    @Autowired
    public UnidadesView(UnidadService unidadService) {
        this.unidadService = unidadService;
        
        try {
            binder = new BeanValidationBinder<>(Unidad.class);
            binder.forField(nombre).bind(Unidad::getNombre, Unidad::setNombre);
            binder.forField(abreviatura).bind(Unidad::getAbreviatura, Unidad::setAbreviatura);
            binder.forField(descripcion).bind(Unidad::getDescripcion, Unidad::setDescripcion);
            // creador es solo lectura - solo se muestra, no se guarda
            binder.forField(creador).bind(Unidad::getCreador, null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        addClassNames("unidades-view");
        setSizeFull();
        this.createGridLayout(splitLayout);
        this.createEditorLayout(splitLayout);
        add(splitLayout);
        initStyles();

        btnFiltrar.addClickListener(e -> onBtnFiltrar());
        txtNombre.addValueChangeListener(e -> onBtnFiltrar());
        txtAbreviatura.addValueChangeListener(e -> onBtnFiltrar());
        
        // Hacer visible el área de búsqueda por defecto
        tophl.setVisible(true);
        tophl.addClassName("tophl-visible");
        
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
        gridUnidades.asSingleSelect().addValueChangeListener(e -> asSingleSelect(e.getValue(), this.save));
        refreshGrid();
    }

    public void initStyles() {
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(70);
        toggleButton.addClassName("toggle-button");
        tophl.addClassName("tophl");
        tophl.setAlignItems(FlexComponent.Alignment.BASELINE);
    }

    private final SerializableBiConsumer<Span, Unidad> EstadoComponenteActivo = (
            span, unidad) -> {
        String theme = String.format("badge %s",
                unidad.isActivo() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(unidad.isActivo() ? "Activo" : "Desactivado");
    };

    private ComponentRenderer<Span, Unidad> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setHeightFull();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        formLayout.add(nombre, abreviatura, descripcion, creador);
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
        gridUnidades.setClassName("grilla");
        gridUnidades.setHeightFull();
        gridUnidades.addColumn(CrearComponmenteActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridUnidades.addColumn(Unidad::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        gridUnidades.addColumn(Unidad::getAbreviatura)
                .setHeader("Abreviatura")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridUnidades.addColumn(Unidad::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
        return gridUnidades;
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

    public void onBtnFiltrar() {
        onRefresh();
    }

    public void onRefresh() {
        String nombre = txtNombre.getValue();
        String abreviatura = txtAbreviatura.getValue();
        
        List<Unidad> unidadesActivas = unidadService.findActive();
        
        if (!nombre.isEmpty()) {
            unidadesActivas.removeIf(u -> !u.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
        if (!abreviatura.isEmpty()) {
            unidadesActivas.removeIf(u -> !u.getAbreviatura().toLowerCase().contains(abreviatura.toLowerCase()));
        }
        
        gridUnidades.setItems(unidadesActivas);
        gridUnidades.getDataProvider().refreshAll();
    }

    public void onBtnSave() {
        try {
            if (this.unidad == null) {
                this.unidad = new Unidad();
            }
            binder.writeBean(this.unidad);
            unidadService.update(this.unidad);
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(UnidadesView.class);
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
            if (this.unidad == null) {
                this.unidad = new Unidad();
            } else {
                this.unidad.setActivo(false);
            }

            binder.writeBean(this.unidad);
            unidadService.update(this.unidad);
            clearForm();
            refreshGrid();
            Notification.show("Unidad Eliminada");
            UI.getCurrent().navigate(UnidadesView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudo Eliminar la unidad. Compruebe nuevamente.");
        }
    }

    public void asSingleSelect(Unidad unidad, Button btnSave) {
        if (unidad != null) {
            btnSave.setText("Actualizar");
            UI.getCurrent().navigate(String.format(this.UNIDAD_EDIT_ROUTE_TEMPLATE, unidad.getId()));
        } else {
            clearForm();
            UI.getCurrent().navigate(UnidadesView.class);
            btnSave.setText("Guardar");
        }
    }

    private void refreshGrid() {
        gridUnidades.select(null);
        List<Unidad> unidadesActivas = unidadService.findActive();
        gridUnidades.setItems(unidadesActivas);
    }

    private void clearForm() {
        populateForm(null);
        txtNombre.setValue("");
        txtAbreviatura.setValue("");
    }

    private void populateForm(Unidad value) {
        this.unidad = value;
        binder.readBean(this.unidad);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> UnidadId = event.getRouteParameters().get(this.UNIDAD_ID).map(Long::parseLong);
        if (UnidadId.isPresent()) {
            Optional<Unidad> unidadFromBackend = unidadService.get(UnidadId.get());
            if (unidadFromBackend.isPresent()) {
                populateForm(unidadFromBackend.get());
            } else {
                Notification.show(
                        String.format("La unidad solicitada no fue encontrada, ID = %s", UnidadId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(UnidadesView.class);
            }
        }
    }
}