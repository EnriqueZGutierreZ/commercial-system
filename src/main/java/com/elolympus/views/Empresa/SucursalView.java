package com.elolympus.views.Empresa;

import com.elolympus.data.Empresa.Empresa;
import com.elolympus.data.Empresa.Sucursal;
import com.elolympus.services.services.EmpresaService;
import com.elolympus.services.services.SucursalService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Sucursal")
@Route(value = "sucursal/:SucursalID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class SucursalView extends Div {

    private final SucursalService sucursalService;
    private final EmpresaService empresaService;
    private Sucursal sucursal;
    private BeanValidationBinder<Sucursal> binder;

    //Componentes UI
    private final Grid<Sucursal> gridsucursal = new Grid<>(Sucursal.class, false);
    private final Checkbox principal = new Checkbox("Principal");
    private final IntegerField codigo = new IntegerField("Código");
    private final TextField descripcion = new TextField("Descripción");
    private final ComboBox<Empresa> empresaComboBox = new ComboBox<>("Empresa");
    private final IntegerField serie = new IntegerField("Serie");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar");

    private final FormLayout formLayout = new FormLayout();

    public SucursalView(SucursalService sucursalService, EmpresaService empresaService) {
        this.sucursalService = sucursalService;
        this.empresaService = empresaService;
        try{
            binder = new BeanValidationBinder<>(Sucursal.class);
            binder.bindInstanceFields(this);
        }catch (Exception e){
            System.out.println("ERRORRR: " +e.getMessage());
        }
        addClassName("sucursal-view");
        setSizeFull();
        setupGrid();
        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private void setupGrid() {
        gridsucursal.addClassName("sucursal-grid");
        gridsucursal.setSizeFull();
        gridsucursal.setColumns("principal", "codigo", "descripcion", "serie");
        gridsucursal.addColumn(sucursal -> sucursal.getEmpresa() != null ? sucursal.getEmpresa().getCommercialName() : "").setHeader("Empresa");
        gridsucursal.asSingleSelect().addValueChangeListener(evt -> editSucursal(evt.getValue()));
    }

    private Component createEditorLayout(){
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setClassName("editor-layout");
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        empresaComboBox.setItems(empresaService.findAll());
        empresaComboBox.setItemLabelGenerator(Empresa::getCommercialName);
        formLayout.add(principal, codigo, descripcion, empresaComboBox, serie);
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> clearForm());
        delete.addClickListener(event -> delete());

        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        div.add(formLayout);
        createButtonsLayout(editorDiv);
        return editorDiv;
    }

    private void createButtonsLayout(Div div) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(save, cancel, delete);
        div.add(buttonLayout);
    }

    private Component createGridLayout() {
        HorizontalLayout busquedaDiv = new HorizontalLayout();
        busquedaDiv.addClassName("tophl");
        Div gridContainer = new Div();
        gridContainer.addClassName("grid-wrapper");
        gridContainer.add(busquedaDiv,gridsucursal);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private void refreshGrid() {
        gridsucursal.setItems(sucursalService.findAll());
    }

    private void save(){
        try{
            if(sucursal==null){
                sucursal = new Sucursal();
            }
            sucursal.setEmpresa(empresaComboBox.getValue());
            if(binder.writeBeanIfValid(sucursal)){
                sucursalService.update(sucursal);
                clearForm();
                refreshGrid();
                Notification.show("Sucursal guardada correctamente");
            }else {
                Notification.show("Error al guardar la sucursal");
            }
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(SucursalView.class);
        }catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void delete(){
        if(sucursal!=null){
            sucursalService.delete(sucursal);
            clearForm();
            refreshGrid();
            Notification.show("Sucursal eliminada correctamente");
        }else{
            Notification.show("Seleccione una sucursal para eliminar");
        }
    }

    private void clearForm(){
        sucursal= new Sucursal();
        binder.readBean(sucursal);
        empresaComboBox.clear();
        save.setText("Guardar");
    }

    private void editSucursal(Sucursal sucursal){
        if(sucursal==null){
            clearForm();
        }else{
            this.sucursal=sucursal;
            binder.readBean(sucursal);
            empresaComboBox.setValue(sucursal.getEmpresa());
            save.setText("Actualizar");
        }
    }
}