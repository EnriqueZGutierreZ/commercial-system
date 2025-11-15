package com.elolympus.views;

import com.elolympus.data.AbstractEntity;
import com.elolympus.services.services.AbstractCrudService;
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;

/**
 * Clase base abstracta para vistas CRUD que elimina la duplicación de código.
 * Proporciona funcionalidad común para operaciones Create, Read, Update, Delete.
 * 
 * @param <T> El tipo de entidad que maneja esta vista (debe extender AbstractEntity)
 */
public abstract class AbstractCrudView<T extends AbstractEntity> extends Div implements BeforeEnterObserver {

    // Componentes UI comunes
    protected Grid<T> grid;
    protected BeanValidationBinder<T> binder;
    protected T currentEntity;
    
    // Botones CRUD
    protected final Button save = new Button("Guardar");
    protected final Button cancel = new Button("Cancelar");
    protected final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    
    // Botón de filtro toggle
    protected final Button toggleButton = new Button("Búsqueda", new Icon(VaadinIcon.FILTER));
    
    // Layouts
    protected final Dialog formDialog = new Dialog();
    protected final FormLayout formLayout = new FormLayout();
    protected HorizontalLayout tophl;
    protected final Button addButton = new Button("Agregar", VaadinIcon.PLUS.create());
    
    /**
     * Constructor que inicializa la vista CRUD
     */
    public AbstractCrudView() {
        addClassNames(getViewClassName());
        setSizeFull();
        
        // Solo crear el binder, NO configurarlo aún
        binder = new BeanValidationBinder<>(getEntityClass());
        
        // NO llamar a createUI() aquí - será llamado desde initialize()
    }
    
    /**
     * Crea la interfaz de usuario completa
     */
    protected void createUI() {
        createMainLayout();
        createFormDialog();
        initStyles();
        setupEventListeners();
        // No llamar a refreshGrid() aquí - se llamará después desde la subclase
    }
    
    /**
     * Método para inicializar la vista después de que todos los campos estén inicializados.
     * Debe ser llamado desde el constructor de la subclase DESPUÉS de super() y DESPUÉS de
     * inicializar todos los campos de la subclase.
     */
    protected void initialize() {
        createUI();
        refreshGrid();
    }
    
    /**
     * Inicializa los estilos de los componentes
     */
    protected void initStyles() {
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        toggleButton.addClassName("toggle-button");
        
        // Configurar el dialog
        formDialog.setHeaderTitle("Formulario");
        formDialog.setModal(true);
        formDialog.setResizable(true);
        formDialog.setWidth("800px");
        formDialog.setHeight("600px");
        
        if (tophl != null) {
            tophl.addClassName("tophl");
            tophl.setAlignItems(FlexComponent.Alignment.BASELINE);
            tophl.setVisible(true);
            tophl.addClassName("tophl-visible");
        }
    }
    
    /**
     * Configura los listeners de eventos
     */
    protected void setupEventListeners() {
        save.addClickListener(e -> onBtnSave());
        cancel.addClickListener(e -> onBtnCancel());
        delete.addClickListener(e -> onBtnDelete());
        addButton.addClickListener(e -> openFormDialog(null));
        
        if (toggleButton != null && tophl != null) {
            toggleButton.addClickListener(event -> {
                boolean isVisible = tophl.isVisible();
                tophl.setVisible(!isVisible);
                if (isVisible) {
                    tophl.removeClassName("tophl-visible");
                } else {
                    tophl.addClassName("tophl-visible");
                }
            });
        }
        
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                openFormDialog(e.getValue());
            }
        });
    }
    
    /**
     * Crea el layout principal con el grid
     */
    protected void createMainLayout() {
        setSizeFull();
        
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        
        // Crear toolbar con botón agregar y filtros
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setPadding(true);
        
        HorizontalLayout leftToolbar = new HorizontalLayout();
        leftToolbar.add(addButton);
        
        HorizontalLayout rightToolbar = new HorizontalLayout();
        if (hasFilters()) {
            rightToolbar.add(toggleButton);
        }
        
        toolbar.add(leftToolbar, rightToolbar);
        
        // Crear wrapper para grid y filtros
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        wrapper.getStyle().set("display", "flex");
        wrapper.getStyle().set("flex-direction", "column");
        
        grid = createGrid();
        configureGrid(grid);
        
        if (hasFilters()) {
            tophl = createFilterLayout();
            setupFilterListeners();
            wrapper.add(tophl, grid);
        } else {
            wrapper.add(grid);
        }
        
        mainLayout.add(toolbar, wrapper);
        add(mainLayout);
    }
    
    /**
     * Crea el dialog del formulario
     */
    protected void createFormDialog() {
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);
        dialogLayout.setSizeFull();
        
        // Primero crear los campos del formulario
        configureFormLayout(formLayout);
        
        // DESPUÉS configurar el binding (ahora que los campos existen)
        try {
            configureBinder();
        } catch (Exception e) {
            System.out.println("ERROR al configurar binder: " + e.getMessage());
            e.printStackTrace();
        }
        
        formLayout.setSizeFull();
        dialogLayout.add(formLayout);
        
        // Crear layout de botones
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.add(save, cancel, delete);
        
        dialogLayout.add(buttonLayout);
        formDialog.add(dialogLayout);
    }
    
    /**
     * Abre el dialog del formulario
     */
    protected void openFormDialog(T entity) {
        try {
            // Si hay una entidad seleccionada, recargarla desde el servicio para asegurar fetch join
            T entityToEdit = entity;
            if (entity != null && entity.getId() != null) {
                Optional<T> freshEntity = getService().get(entity.getId());
                if (freshEntity.isPresent()) {
                    entityToEdit = freshEntity.get();
                } else {
                    entityToEdit = entity;
                }
            }
            
            populateForm(entityToEdit);
            
            if (entityToEdit == null) {
                formDialog.setHeaderTitle("Agregar " + getEntityName());
                save.setText("Guardar");
                delete.setVisible(false);
            } else {
                formDialog.setHeaderTitle("Editar " + getEntityName());
                save.setText("Actualizar");
                delete.setVisible(true);
            }
            
            formDialog.open();
            
        } catch (Exception e) {
            // Como fallback, intentar abrir con la entidad original
            populateForm(entity);
            formDialog.setHeaderTitle(entity == null ? "Agregar " + getEntityName() : "Editar " + getEntityName());
            formDialog.open();
        }
    }
    
    /**
     * Crea el grid con configuración básica
     */
    protected Grid<T> createGrid() {
        Grid<T> newGrid = new Grid<>(getEntityClass(), false);
        newGrid.setClassName("grilla");
        newGrid.setHeightFull();
        return newGrid;
    }
    
    /**
     * Renderer común para el estado activo/desactivado
     */
    protected ComponentRenderer<Span, T> createActivoRenderer() {
        SerializableBiConsumer<Span, T> statusRenderer = (span, entity) -> {
            boolean isActivo = entity.isActivo();
            String theme = isActivo ? "badge success" : "badge error";
            span.getElement().setAttribute("theme", theme);
            span.setText(isActivo ? "Activo" : "Desactivado");
            
            // Estilos consistentes
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
        
        return new ComponentRenderer<>(Span::new, statusRenderer);
    }
    
    /**
     * Maneja el evento de guardar
     */
    protected void onBtnSave() {
        try {
            if (this.currentEntity == null) {
                this.currentEntity = createNewEntity();
            }
            binder.writeBean(this.currentEntity);
            
            beforeSave(this.currentEntity);
            T savedEntity = getService().update(this.currentEntity);
            afterSave(savedEntity);
            
            clearForm();
            refreshGrid();
            formDialog.close();
            grid.deselectAll();
            Notification.show("Datos actualizados");
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
    
    /**
     * Maneja el evento de cancelar
     */
    protected void onBtnCancel() {
        clearForm();
        formDialog.close();
        grid.deselectAll();
    }
    
    /**
     * Maneja el evento de eliminar (desactivar)
     */
    protected void onBtnDelete() {
        try {
            if (this.currentEntity == null) {
                this.currentEntity = createNewEntity();
            } else {
                this.currentEntity.setActivo(false);
            }
            
            binder.writeBean(this.currentEntity);
            getService().update(this.currentEntity);
            clearForm();
            refreshGrid();
            formDialog.close();
            grid.deselectAll();
            Notification.show(getEntityName() + " Eliminado");
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al Eliminar. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("No se pudo Eliminar. Compruebe nuevamente.");
        }
    }
    
    /**
     * Maneja la selección en el grid (método legacy mantenido por compatibilidad)
     */
    protected void asSingleSelect(T entity, Button btnSave) {
        // Este método se mantiene por compatibilidad pero ya no se usa
        // La selección se maneja directamente en setupEventListeners()
    }
    
    /**
     * Refresca el grid con datos actualizados
     */
    protected void refreshGrid() {
        grid.select(null);
        List<T> activeEntities = getService().findActive();
        grid.setItems(activeEntities);
    }
    
    /**
     * Limpia el formulario
     */
    protected void clearForm() {
        populateForm(null);
        clearFilters();
    }
    
    /**
     * Puebla el formulario con una entidad
     */
    protected void populateForm(T entity) {
        this.currentEntity = entity;
        binder.readBean(this.currentEntity);
    }
    
    /**
     * Aplica filtros a los datos
     */
    protected void onRefresh() {
        List<T> filteredEntities = applyFilters(getService().findActive());
        grid.setItems(filteredEntities);
        grid.getDataProvider().refreshAll();
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> entityId = event.getRouteParameters().get(getEntityIdParam()).map(Long::parseLong);
        if (entityId.isPresent()) {
            Optional<T> entityFromBackend = getService().get(entityId.get());
            if (entityFromBackend.isPresent()) {
                // En lugar de solo poblar el form, abrir el dialog
                openFormDialog(entityFromBackend.get());
            } else {
                Notification.show(
                        String.format("El %s solicitado no fue encontrado, ID = %s", getEntityName(), entityId.get()), 
                        3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(getViewClass());
            }
        } else {
            refreshGrid();
        }
    }
    
    // ========== MÉTODOS ABSTRACTOS QUE DEBEN SER IMPLEMENTADOS ==========
    
    /**
     * @return La clase de la entidad
     */
    protected abstract Class<T> getEntityClass();
    
    /**
     * @return El servicio que maneja esta entidad
     */
    protected abstract AbstractCrudService<T, ?> getService();
    
    /**
     * @return El nombre de la clase CSS para la vista
     */
    protected abstract String getViewClassName();
    
    /**
     * @return La clase de la vista actual
     */
    protected abstract Class<? extends Component> getViewClass();
    
    /**
     * @return El nombre del parámetro de ID en la ruta
     */
    protected abstract String getEntityIdParam();
    
    /**
     * @return El template de ruta para edición
     */
    protected abstract String getEditRouteTemplate();
    
    /**
     * @return El nombre de la entidad para mensajes
     */
    protected abstract String getEntityName();
    
    /**
     * Crea una nueva instancia de la entidad
     */
    protected abstract T createNewEntity();
    
    /**
     * Configura el binder con los campos del formulario
     */
    protected abstract void configureBinder();
    
    /**
     * Configura las columnas del grid
     */
    protected abstract void configureGrid(Grid<T> grid);
    
    /**
     * Configura los campos del formulario
     */
    protected abstract void configureFormLayout(FormLayout formLayout);
    
    // ========== MÉTODOS OPCIONALES CON IMPLEMENTACIÓN POR DEFECTO ==========
    
    /**
     * @return true si la vista tiene filtros
     */
    protected boolean hasFilters() {
        return false;
    }
    
    /**
     * Crea el layout de filtros
     */
    protected HorizontalLayout createFilterLayout() {
        return new HorizontalLayout();
    }
    
    /**
     * Configura los listeners de los filtros
     */
    protected void setupFilterListeners() {
        // Por defecto no hace nada
    }
    
    /**
     * Limpia los filtros
     */
    protected void clearFilters() {
        // Por defecto no hace nada
    }
    
    /**
     * Aplica filtros a una lista de entidades
     */
    protected List<T> applyFilters(List<T> entities) {
        return entities;
    }
    
    /**
     * Hook ejecutado antes de guardar
     */
    protected void beforeSave(T entity) {
        // Por defecto no hace nada
    }
    
    /**
     * Hook ejecutado después de guardar
     */
    protected void afterSave(T entity) {
        // Por defecto no hace nada
    }
}