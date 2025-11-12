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
            case "region":
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

    public Optional<Ubigeo> getUbigeoByCodigo(String codigo) {
        return ubigeoRepository.findByCodigo(codigo);
    }

    public Ubigeo save(Ubigeo ubigeo) {
        return ubigeoRepository.save(ubigeo);
    }

    public void cargarDepartamentosProvinciosDistritos() {
        try {
            // Cargando departamentos, provincias y distritos desde datos ubigeo...
            
            // Limpiar tablas existentes para recargar con datos correctos
            // Limpiando datos existentes...
            distritoRepository.deleteAll();
            provinciaRepository.deleteAll();
            departamentoRepository.deleteAll();
            
            // Cargar departamentos únicos
            List<Ubigeo> ubigeos = ubigeoRepository.findAll();
            
            // Crear un mapa de códigos de departamento a nombres
            java.util.Map<String, String> departamentosMap = new java.util.HashMap<>();
            departamentosMap.put("01", "AMAZONAS");
            departamentosMap.put("02", "ÁNCASH");
            departamentosMap.put("03", "APURÍMAC");
            departamentosMap.put("04", "AREQUIPA");
            departamentosMap.put("05", "AYACUCHO");
            departamentosMap.put("06", "CAJAMARCA");
            departamentosMap.put("07", "CALLAO");
            departamentosMap.put("08", "CUSCO");
            departamentosMap.put("09", "HUANCAVELICA");
            departamentosMap.put("10", "HUÁNUCO");
            departamentosMap.put("11", "ICA");
            departamentosMap.put("12", "JUNÍN");
            departamentosMap.put("13", "LA LIBERTAD");
            departamentosMap.put("14", "LAMBAYEQUE");
            departamentosMap.put("15", "LIMA");
            departamentosMap.put("16", "LORETO");
            departamentosMap.put("17", "MADRE DE DIOS");
            departamentosMap.put("18", "MOQUEGUA");
            departamentosMap.put("19", "PASCO");
            departamentosMap.put("20", "PIURA");
            departamentosMap.put("21", "PUNO");
            departamentosMap.put("22", "SAN MARTÍN");
            departamentosMap.put("23", "TACNA");
            departamentosMap.put("24", "TUMBES");
            departamentosMap.put("25", "UCAYALI");
            
            // Crear departamentos
            java.util.Set<String> departamentosCreados = new java.util.HashSet<>();
            for (Ubigeo ubigeo : ubigeos) {
                if (ubigeo.getCodigo() != null && ubigeo.getCodigo().length() >= 2) {
                    String depCodigo = ubigeo.getCodigo().substring(0, 2);
                    if (!departamentosCreados.contains(depCodigo) && departamentosMap.containsKey(depCodigo)) {
                        Optional<Departamento> existingDep = departamentoRepository.findById(depCodigo);
                        if (existingDep.isEmpty()) {
                            Departamento departamento = new Departamento();
                            departamento.setId(depCodigo);
                            departamento.setNombre(departamentosMap.get(depCodigo));
                            departamentoRepository.save(departamento);
                            // Creado departamento: " + depCodigo + " - " + departamentosMap.get(depCodigo)
                        }
                        departamentosCreados.add(depCodigo);
                    }
                }
            }
            
            // Crear un mapeo de códigos de provincia a nombres
            java.util.Map<String, String> provinciasMap = new java.util.HashMap<>();
            
            // En lugar de mapeo manual, vamos a usar una aproximación diferente:
            // Extraer nombres de provincia únicos del JSON para cada código de provincia
            java.util.Map<String, String> provinciasNombres = new java.util.HashMap<>();
            
            // Buscar el primer distrito de cada provincia para obtener el nombre
            for (Ubigeo ubigeo : ubigeos) {
                if (ubigeo.getCodigo() != null && ubigeo.getCodigo().length() >= 4) {
                    String provCodigo = ubigeo.getCodigo().substring(0, 4);
                    if (!provinciasNombres.containsKey(provCodigo)) {
                        // Buscar un distrito capital o principal para obtener el nombre de la provincia
                        String nombreProvincia = inferirNombreProvincia(provCodigo, ubigeos);
                        provinciasNombres.put(provCodigo, nombreProvincia);
                    }
                }
            }
            
            // Crear provincias
            java.util.Set<String> provinciasCreadas = new java.util.HashSet<>();
            for (Ubigeo ubigeo : ubigeos) {
                if (ubigeo.getCodigo() != null && ubigeo.getCodigo().length() >= 4) {
                    String provCodigo = ubigeo.getCodigo().substring(0, 4);
                    String depCodigo = ubigeo.getCodigo().substring(0, 2);
                    
                    if (!provinciasCreadas.contains(provCodigo)) {
                        Optional<Provincia> existingProv = provinciaRepository.findById(provCodigo);
                        if (existingProv.isEmpty()) {
                            Provincia provincia = new Provincia();
                            provincia.setId(provCodigo);
                            
                            // Usar el mapeo dinámico
                            String nombreProvincia = provinciasNombres.get(provCodigo);
                            if (nombreProvincia == null) {
                                nombreProvincia = "PROVINCIA " + provCodigo;
                            }
                            
                            provincia.setNombre(nombreProvincia);
                            provincia.setDepartamentoId(depCodigo);
                            provinciaRepository.save(provincia);
                            // Creada provincia: " + provCodigo + " - " + provincia.getNombre()
                        }
                        provinciasCreadas.add(provCodigo);
                    }
                }
            }
            
            // Crear distritos
            for (Ubigeo ubigeo : ubigeos) {
                if (ubigeo.getCodigo() != null && ubigeo.getCodigo().length() == 6) {
                    String distCodigo = ubigeo.getCodigo();
                    String provCodigo = ubigeo.getCodigo().substring(0, 4);
                    String depCodigo = ubigeo.getCodigo().substring(0, 2);
                    
                    Optional<Distrito> existingDist = distritoRepository.findById(distCodigo);
                    if (existingDist.isEmpty()) {
                        Distrito distrito = new Distrito();
                        distrito.setId(distCodigo);
                        
                        // Usar la descripción como nombre del distrito, ya que contiene el nombre real
                        String nombreDistrito = ubigeo.getDescripcion();
                        if (nombreDistrito == null || nombreDistrito.trim().isEmpty()) {
                            nombreDistrito = "DISTRITO " + distCodigo;
                        }
                        
                        distrito.setNombre(nombreDistrito);
                        distrito.setProvinciaId(provCodigo);
                        distrito.setDepartamentoId(depCodigo);
                        distritoRepository.save(distrito);
                        // Creado distrito: " + distCodigo + " - " + distrito.getNombre()
                    }
                }
            }
            
            // Departamentos, provincias y distritos cargados exitosamente
            
        } catch (Exception e) {
            System.err.println("Error al cargar departamentos, provincias y distritos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String inferirNombreProvincia(String provCodigo, List<Ubigeo> ubigeos) {
        // Buscar el distrito "01" de la provincia (normalmente es la capital)
        String distritoCapital = provCodigo + "01";
        
        for (Ubigeo ubigeo : ubigeos) {
            if (distritoCapital.equals(ubigeo.getCodigo())) {
                String descripcion = ubigeo.getDescripcion();
                if (descripcion != null && !descripcion.trim().isEmpty()) {
                    // La descripción del distrito capital a menudo es similar al nombre de la provincia
                    return descripcion.toUpperCase();
                }
            }
        }
        
        // Si no encontramos el distrito 01, buscar cualquier distrito de esa provincia
        for (Ubigeo ubigeo : ubigeos) {
            if (ubigeo.getCodigo() != null && ubigeo.getCodigo().startsWith(provCodigo)) {
                String descripcion = ubigeo.getDescripcion();
                if (descripcion != null && !descripcion.trim().isEmpty()) {
                    return descripcion.toUpperCase();
                }
            }
        }
        
        return "PROVINCIA " + provCodigo;
    }
}

