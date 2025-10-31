package com.elolympus.endpoints;

import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.services.services.UbigeoBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubigeo-basic")
public class UbigeoBasicController {

    @Autowired
    private UbigeoBasicService ubigeoService;

    @GetMapping("/departamentos")
    public List<Departamento> getAllDepartamentos() {
        return ubigeoService.getAllDepartamentos();
    }

    @GetMapping("/provincias/{departamentoId}")
    public List<Provincia> getProvinciasByDepartamento(@PathVariable String departamentoId) {
        return ubigeoService.getProvinciasByDepartamento(departamentoId);
    }

    @GetMapping("/distritos/{provinciaId}")
    public List<Distrito> getDistritosByProvincia(@PathVariable String provinciaId) {
        return ubigeoService.getDistritosByProvincia(provinciaId);
    }
}