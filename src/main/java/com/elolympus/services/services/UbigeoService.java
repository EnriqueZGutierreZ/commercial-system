package com.elolympus.services.services;

import com.elolympus.data.Auxiliar.Ubigeo;
import com.elolympus.data.ubigeo.Distrito;
import com.elolympus.data.ubigeo.Provincia;
import com.elolympus.data.ubigeo.Departamento;
import com.elolympus.services.repository.ubigeo.DistritoRepository;
import com.elolympus.services.repository.ubigeo.ProvinciaRepository;
import com.elolympus.services.repository.ubigeo.DepartamentoRepository;
import com.elolympus.services.repository.ubigeo.UbigeoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class UbigeoService {
    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    @Autowired
    private UbigeoRepository ubigeoRepository;

//    LEER ARCHIVO JSON
    private ObjectMapper objectMapper = new ObjectMapper();
    public void cargarUbigeosDesdeJson() throws IOException {
        // Leer el archivo ubigeo.json
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ubigeos.json");
        if (inputStream == null) {
            throw new FileNotFoundException("El archivo ubigeo.json no se encuentra.");
        }

        // Mapear el archivo JSON a una lista de objetos Ubigeo
        List<Ubigeo> ubigeos = objectMapper.readValue(inputStream, new TypeReference<List<Ubigeo>>(){});

        // Insertar los ubigeos nuevos si no existen
        for (Ubigeo ubigeo : ubigeos) {
            Optional<Ubigeo> existingUbigeo = ubigeoRepository.findByCodigo(ubigeo.getCodigo());
            if (existingUbigeo.isEmpty()) {
                ubigeoRepository.save(ubigeo);
            }
        }
    }

    public List<Departamento> getAllRegiones() {
        return departamentoRepository.findAll();
    }

    public List<Provincia> getProvinciasByRegion(String departamentoId) {
        return provinciaRepository.findByDepartamentoId(departamentoId);
    }

    public List<Distrito> getDistritosByProvincia(String provinciaId) {
        return distritoRepository.findByProvinciaId(provinciaId);
    }

    public String getNumeroUbigeo(String id, String type) {
        switch(type) {
            case "departamento":
                return departamentoRepository.findById(id).map(Departamento::getId).orElse(null);
            case "provincia":
                return provinciaRepository.findById(id).map(Provincia::getId).orElse(null);
            case "distrito":
                return distritoRepository.findById(id).map(Distrito::getId).orElse(null);
            default:
                return null;
        }
    }
}

