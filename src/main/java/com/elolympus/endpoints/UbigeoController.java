package com.elolympus.endpoints;

import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.services.services.UbigeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ubigeo")
public class UbigeoController {
    @Autowired
    private UbigeoService ubigeoService;

    @GetMapping("/regiones")
    public List<Departamento> getAllRegiones() {
        return ubigeoService.getAllRegiones();
    }

    @GetMapping("/provincias/{regionId}")
    public List<Provincia> getProvinciasByRegion(@PathVariable String regionId) {
        return ubigeoService.getProvinciasByRegion(regionId);
    }

    @GetMapping("/distritos/{provinciaId}")
    public List<Distrito> getDistritosByProvincia(@PathVariable String provinciaId) {
        return ubigeoService.getDistritosByProvincia(provinciaId);
    }

    @GetMapping("/numeroUbigeo/{id}/{type}")
    public String getNumeroUbigeo(@PathVariable String id, @PathVariable String type) {
        return ubigeoService.getNumeroUbigeo(id, type);
    }
}
