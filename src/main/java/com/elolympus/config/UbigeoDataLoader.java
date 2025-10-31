package com.elolympus.config;

import com.elolympus.services.services.UbigeoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UbigeoDataLoader {

    @Autowired
    private UbigeoService ubigeoService;

    @PostConstruct
    public void loadUbigeoData() {
        try {
            System.out.println("Cargando datos de ubigeo desde JSON...");
            ubigeoService.cargarUbigeosDesdeJson();
            System.out.println("Datos de ubigeo cargados exitosamente");
        } catch (IOException e) {
            System.err.println("Error al cargar datos de ubigeo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}