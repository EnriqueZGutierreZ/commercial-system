package com.elolympus.services.services;

import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.services.repository.ubigeo.DistritoRepository;
import com.elolympus.services.repository.ubigeo.ProvinciaRepository;
import com.elolympus.services.repository.ubigeo.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbigeoBasicService {
    
    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    public List<Departamento> getAllDepartamentos() {
        return departamentoRepository.findAll();
    }

    public List<Provincia> getProvinciasByDepartamento(String departamentoId) {
        return provinciaRepository.findByDepartamentoId(departamentoId);
    }

    public List<Distrito> getDistritosByProvincia(String provinciaId) {
        return distritoRepository.findByProvinciaId(provinciaId);
    }
}