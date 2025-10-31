package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Linea;
import com.elolympus.services.services.LineaService;
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

@PageTitle("Líneas")
@Route(value = "lineas/:LineaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class LineasView extends Div implements BeforeEnterObserver {

    private final LineaService lineaService;
    private BeanValidationBinder<Linea> binder;
    private Linea linea;

    public final String LINEA_ID = "LineaID";
    public final String LINEA_EDIT_ROUTE_TEMPLATE = "lineas/%s/edit";
    public Grid<Linea> gridLineas = new Grid<>(Linea.class, false);
    public final TextField txtNombre = new TextField("Nombre", "", "Buscar por nombre");
    public final TextField txtCodigo = new TextField("Código", "", "Buscar por código");
    public final Button btnFiltrar = new Button("BUSCAR", new Icon(VaadinIcon.FILTER));
    public final Button toggleButton = new Button("Búsqueda", new Icon(VaadinIcon.FILTER));

    public final HorizontalLayout tophl = new HorizontalLayout(txtCodigo, txtNombre, btnFiltrar);

    public FormLayout formLayout = new FormLayout();
    public TextField nombre = new TextField("Nombre", "");
    public TextField descripcion = new TextField("Descripción", "");
    public TextField codigo = new TextField("Código", "");
    public TextField creador = new TextField("Creador", "");
    {
        creador.setReadOnly(true);
    }
    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    public final SplitLayout splitLayout = new SplitLayout();

    @Autowired
    public LineasView(LineaService lineaService) {
        this.lineaService = lineaService;
        
        try {
            binder = new BeanValidationBinder<>(Linea.class);
            binder.forField(nombre).bind(Linea::getNombre, Linea::setNombre);
            binder.forField(descripcion).bind(Linea::getDescripcion, Linea::setDescripcion);
            binder.forField(codigo).bind(Linea::getCodigo, Linea::setCodigo);
            // creador es solo lectura - solo se muestra, no se guarda
            binder.forField(creador).bind(Linea::getCreador, null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        addClassNames("lineas-view");
        setSizeFull();
        this.createGridLayout(splitLayout);
        this.createEditorLayout(splitLayout);
        add(splitLayout);
        initStyles();

        btnFiltrar.addClickListener(e -> onBtnFiltrar());
        txtCodigo.addValueChangeListener(e -> onBtnFiltrar());
        txtNombre.addValueChangeListener(e -> onBtnFiltrar());
        
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
        gridLineas.asSingleSelect().addValueChangeListener(e -> asSingleSelect(e.getValue(), this.save));
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

    private final SerializableBiConsumer<Span, Linea> EstadoComponenteActivo = (
            span, linea) -> {
        String theme = String.format("badge %s",
                linea.isActivo() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(linea.isActivo() ? "Activo" : "Desactivado");
    };

    private ComponentRenderer<Span, Linea> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setHeightFull();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        formLayout.add(nombre, descripcion, codigo, creador);
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
        gridLineas.setClassName("grilla");
        gridLineas.setHeightFull();
        gridLineas.addColumn(CrearComponmenteActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridLineas.addColumn(Linea::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridLineas.addColumn(Linea::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        gridLineas.addColumn(Linea::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
        return gridLineas;
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
        String codigo = txtCodigo.getValue();
        String nombre = txtNombre.getValue();
        
        List<Linea> lineasActivas = lineaService.findActive();
        
        if (!codigo.isEmpty()) {
            lineasActivas.removeIf(l -> !l.getCodigo().toLowerCase().contains(codigo.toLowerCase()));
        }
        if (!nombre.isEmpty()) {
            lineasActivas.removeIf(l -> !l.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
        
        gridLineas.setItems(lineasActivas);
        gridLineas.getDataProvider().refreshAll();
    }

    public void onBtnSave() {
        try {
            if (this.linea == null) {
                this.linea = new Linea();
            }
            binder.writeBean(this.linea);
            lineaService.update(this.linea);
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(LineasView.class);
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
            if (this.linea == null) {
                this.linea = new Linea();
            } else {
                this.linea.setActivo(false);
            }

            binder.writeBean(this.linea);
            lineaService.update(this.linea);
            clearForm();
            refreshGrid();
            Notification.show("Línea Eliminada");
            UI.getCurrent().navigate(LineasView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudo Eliminar la línea. Compruebe nuevamente.");
        }
    }

    public void asSingleSelect(Linea linea, Button btnSave) {
        if (linea != null) {
            btnSave.setText("Actualizar");
            UI.getCurrent().navigate(String.format(this.LINEA_EDIT_ROUTE_TEMPLATE, linea.getId()));
        } else {
            clearForm();
            UI.getCurrent().navigate(LineasView.class);
            btnSave.setText("Guardar");
        }
    }

    private void refreshGrid() {
        gridLineas.select(null);
        List<Linea> lineasActivas = lineaService.findActive();
        gridLineas.setItems(lineasActivas);
    }

    private void clearForm() {
        populateForm(null);
        txtCodigo.setValue("");
        txtNombre.setValue("");
    }

    private void populateForm(Linea value) {
        this.linea = value;
        binder.readBean(this.linea);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> LineaId = event.getRouteParameters().get(this.LINEA_ID).map(Long::parseLong);
        if (LineaId.isPresent()) {
            Optional<Linea> lineaFromBackend = lineaService.get(LineaId.get());
            if (lineaFromBackend.isPresent()) {
                populateForm(lineaFromBackend.get());
            } else {
                Notification.show(
                        String.format("La línea solicitada no fue encontrada, ID = %s", LineaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(LineasView.class);
            }
        }
    }
}