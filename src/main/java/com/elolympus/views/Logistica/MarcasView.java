package com.elolympus.views.Logistica;

import com.elolympus.data.Logistica.Marca;
import com.elolympus.services.services.MarcaService;
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

@PageTitle("Marcas")
@Route(value = "marcas/:MarcaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class MarcasView extends Div implements BeforeEnterObserver {

    private final MarcaService marcaService;

    public final String MARCA_ID = "MarcaID";
    public final String MARCA_EDIT_ROUTE_TEMPLATE = "marcas/%s/edit";
    public Grid<Marca> gridMarcas = new Grid<>(Marca.class, false);
    private BeanValidationBinder<Marca> binder;
    private Marca marca;
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
    public MarcasView(MarcaService marcaService) {
        this.marcaService = marcaService;

        try {
            binder = new BeanValidationBinder<>(Marca.class);
            binder.forField(nombre).bind(Marca::getNombre, Marca::setNombre);
            binder.forField(descripcion).bind(Marca::getDescripcion, Marca::setDescripcion);
            binder.forField(codigo).bind(Marca::getCodigo, Marca::setCodigo);
            binder.forField(creador).bind(Marca::getCreador, null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        addClassNames("marcas-view");
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
        gridMarcas.asSingleSelect().addValueChangeListener(e -> asSingleSelect(e.getValue(), this.save));
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
        gridMarcas.setClassName("grilla");
        gridMarcas.setHeightFull();
        gridMarcas.addColumn(CrearComponmenteActivoRenderer())
                .setHeader("Activo")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridMarcas.addColumn(Marca::getCodigo)
                .setHeader("Código")
                .setAutoWidth(true)
                .setFlexGrow(0);
        gridMarcas.addColumn(Marca::getNombre)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setFlexGrow(1);
        gridMarcas.addColumn(Marca::getDescripcion)
                .setHeader("Descripción")
                .setAutoWidth(true)
                .setFlexGrow(1);
        return gridMarcas;
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

    private final SerializableBiConsumer<Span, Marca> EstadoComponenteActivo = (
            span, marca) -> {
        boolean isActivo = marca.isActivo();
        String theme = isActivo ? "badge success" : "badge error";
        span.getElement().setAttribute("theme", theme);
        span.setText(isActivo ? "Activo" : "Desactivado");
        
        // Asegurar que los estilos se apliquen correctamente
        span.getElement().getStyle().set("padding", "0.25em 0.5em");
        span.getElement().getStyle().set("border-radius", "4px");
        span.getElement().getStyle().set("font-size", "0.875em");
        span.getElement().getStyle().set("font-weight", "500");
        span.getElement().getStyle().set("text-transform", "uppercase");
        
        if (isActivo) {
            span.getElement().getStyle().set("background-color", "var(--lumo-success-color-10pct)");
            span.getElement().getStyle().set("color", "var(--lumo-success-text-color)");
        } else {
            span.getElement().getStyle().set("background-color", "var(--lumo-error-color-10pct)");
            span.getElement().getStyle().set("color", "var(--lumo-error-text-color)");
        }
    };

    private ComponentRenderer<Span, Marca> CrearComponmenteActivoRenderer() {
        return new ComponentRenderer<>(Span::new, EstadoComponenteActivo);
    }

    public void onBtnFiltrar() {
        onRefresh();
    }

    public void onRefresh() {
        String codigo = txtCodigo.getValue();
        String nombre = txtNombre.getValue();
        
        List<Marca> marcasActivas = marcaService.findActive();
        
        if (!codigo.isEmpty()) {
            marcasActivas.removeIf(m -> !m.getCodigo().toLowerCase().contains(codigo.toLowerCase()));
        }
        if (!nombre.isEmpty()) {
            marcasActivas.removeIf(m -> !m.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
        
        gridMarcas.setItems(marcasActivas);
        gridMarcas.getDataProvider().refreshAll();
    }

    public void onBtnSave() {
        try {
            if (this.marca == null) {
                this.marca = new Marca();
            }
            binder.writeBean(this.marca);
            marcaService.update(this.marca);
            clearForm();
            refreshGrid();
            Notification.show("Datos actualizados");
            UI.getCurrent().navigate(MarcasView.class);
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
            if (this.marca == null) {
                this.marca = new Marca();
            } else {
                this.marca.setActivo(false);
            }

            binder.writeBean(this.marca);
            marcaService.update(this.marca);
            clearForm();
            refreshGrid();
            Notification.show("Marca Eliminada");
            UI.getCurrent().navigate(MarcasView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudo Eliminar la marca. Compruebe nuevamente.");
        }
    }

    public void asSingleSelect(Marca marca, Button btnSave) {
        if (marca != null) {
            btnSave.setText("Actualizar");
            UI.getCurrent().navigate(String.format(this.MARCA_EDIT_ROUTE_TEMPLATE, marca.getId()));
        } else {
            clearForm();
            UI.getCurrent().navigate(MarcasView.class);
            btnSave.setText("Guardar");
        }
    }

    private void refreshGrid() {
        gridMarcas.select(null);
        List<Marca> marcasActivas = marcaService.findActive();
        gridMarcas.setItems(marcasActivas);
    }

    private void clearForm() {
        populateForm(null);
        txtCodigo.setValue("");
        txtNombre.setValue("");
    }

    private void populateForm(Marca value) {
        this.marca = value;
        binder.readBean(this.marca);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> MarcaId = event.getRouteParameters().get(this.MARCA_ID).map(Long::parseLong);
        if (MarcaId.isPresent()) {
            Optional<Marca> marcaFromBackend = marcaService.get(MarcaId.get());
            if (marcaFromBackend.isPresent()) {
                populateForm(marcaFromBackend.get());
            } else {
                Notification.show(
                        String.format("La marca solicitada no fue encontrada, ID = %s", MarcaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(MarcasView.class);
            }
        } else {
            refreshGrid();
        }
    }

}