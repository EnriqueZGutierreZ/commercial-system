package com.elolympus.views.ubigeo;

import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.services.services.UbigeoBasicService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Ubigeo Básico")
@Route(value = "ubigeo-basico", layout = com.elolympus.views.MainLayout.class)
public class UbigeoBasicView extends VerticalLayout {

    private final UbigeoBasicService ubigeoService;
    
    private ComboBox<Departamento> departamentoCombo = new ComboBox<>("Departamento");
    private ComboBox<Provincia> provinciaCombo = new ComboBox<>("Provincia");
    private ComboBox<Distrito> distritoCombo = new ComboBox<>("Distrito");

    @Autowired
    public UbigeoBasicView(UbigeoBasicService ubigeoService) {
        this.ubigeoService = ubigeoService;
        
        // Configurar los comboboxes
        configurarDepartamentoCombo();
        configurarProvinciaCombo();
        configurarDistritoCombo();
        
        // Añadir los componentes al layout
        add(departamentoCombo, provinciaCombo, distritoCombo);
        
        // Cargar departamentos inicialmente
        cargarDepartamentos();
    }

    private void configurarDepartamentoCombo() {
        departamentoCombo.setItemLabelGenerator(Departamento::getNombre);
        departamentoCombo.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                cargarProvincias(event.getValue().getId());
                provinciaCombo.clear();
                distritoCombo.clear();
            }
        });
    }

    private void configurarProvinciaCombo() {
        provinciaCombo.setItemLabelGenerator(Provincia::getNombre);
        provinciaCombo.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                cargarDistritos(event.getValue().getId());
                distritoCombo.clear();
            }
        });
    }

    private void configurarDistritoCombo() {
        distritoCombo.setItemLabelGenerator(Distrito::getNombre);
    }

    private void cargarDepartamentos() {
        List<Departamento> departamentos = ubigeoService.getAllDepartamentos();
        departamentoCombo.setItems(departamentos);
    }

    private void cargarProvincias(String departamentoId) {
        List<Provincia> provincias = ubigeoService.getProvinciasByDepartamento(departamentoId);
        provinciaCombo.setItems(provincias);
    }

    private void cargarDistritos(String provinciaId) {
        List<Distrito> distritos = ubigeoService.getDistritosByProvincia(provinciaId);
        distritoCombo.setItems(distritos);
    }
}