package com.elolympus.views.Empresa;

import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.services.EmpresaService;
import com.elolympus.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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

@PageTitle("Empresa")
@Route(value = "empresa/:EmpresaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class EmpresaView extends Div {

    private final EmpresaService empresaService;
    private Empresa empresa;
    private BeanValidationBinder<Empresa> binder;

    //Componentes UI
    private final Grid<Empresa> gridempresa = new Grid<>(Empresa.class, false);
    private final IntegerField direccion = new IntegerField("Direccion");
    private final TextField folderTemps = new TextField("Informes de carpeta");
    private final TextField folderReports = new TextField("Informes de carpeta");
    private final Checkbox allowBuyWithoutStock = new Checkbox("Permitir comprar sin stock");
    private final Checkbox requireSalesPin = new Checkbox("Requerir pin de ventas");
    private final IntegerField documentoTipoXdefecto = new IntegerField("Documento Tipo Xdefecto");
    private final TextField logoEnterprise = new TextField("Logotipo Empresa");
    private final TextField logoWidth = new TextField("Ancho del logotipo");
    private final TextField commercialName = new TextField("Nombre comercial");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");
    private final Button delete = new Button("Eliminar");

    private final FormLayout formLayout = new FormLayout();

    public EmpresaView(EmpresaService empresaService) {
        this.empresaService = empresaService;
        try{
            binder = new BeanValidationBinder<>(Empresa.class);
            binder.bindInstanceFields(this);
        }catch (Exception e){
            System.out.println("ERRORRR: " +e.getMessage());
        }
        addClassName("empresa-view");
        setSizeFull();
        setupGrid();
        SplitLayout layout = new SplitLayout(createGridLayout(), createEditorLayout());
        layout.setSizeFull();
        add(layout);
        refreshGrid();
    }

    private void setupGrid() {
        gridempresa.addClassName("empresa-grid");
        gridempresa.setSizeFull();
        gridempresa.setColumns("commercialName", "folderTemps", "folderReports", "allowBuyWithoutStock", "requireSalesPin", "documentoTipoXdefecto", "logoEnterprise", "logoWidth");
        gridempresa.asSingleSelect().addValueChangeListener(evt -> editEmpresa(evt.getValue()));
    }

    private Component createEditorLayout(){
        Div editorDiv = new Div();
        editorDiv.setHeightFull();
        editorDiv.setClassName("editor-layout");
        Div div = new Div();
        div.setClassName("editor");
        editorDiv.add(div);
        formLayout.add(direccion, folderTemps, folderReports, allowBuyWithoutStock, requireSalesPin, documentoTipoXdefecto, logoEnterprise, logoWidth, commercialName);
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
        gridContainer.add(busquedaDiv,gridempresa);
        gridContainer.setSizeFull();
        return gridContainer;
    }

    private void refreshGrid() {
        gridempresa.setItems(empresaService.findAll());
    }

    private void save(){
        try{
            if(empresa==null){
                empresa = new Empresa();
            }
            if(binder.writeBeanIfValid(empresa)){
                empresaService.update(empresa);
                clearForm();
                refreshGrid();
                Notification.show("Empresa guardada correctamente");
            }else {
                Notification.show("Error al guardar la empresa");
            }
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(EmpresaView.class);
        }catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error al actualizar los datos. Alguien más actualizó el registro mientras usted hacía cambios.");
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void delete(){
        if(empresa!=null){
            empresaService.delete(empresa);
            clearForm();
            refreshGrid();
            Notification.show("Empresa eliminada correctamente");
        }else{
            Notification.show("Seleccione una empresa para eliminar");
        }
    }

    private void clearForm(){
        empresa= new Empresa();
        binder.readBean(empresa);
        save.setText("Guardar");
    }

    private void editEmpresa(Empresa empresa){
        if(empresa==null){
            clearForm();
        }else{
            this.empresa=empresa;
            binder.readBean(empresa);
            save.setText("Actualizar");
        }
    }
}
