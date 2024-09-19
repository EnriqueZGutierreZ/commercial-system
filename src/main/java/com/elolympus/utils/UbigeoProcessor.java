package com.elolympus.utils;

import com.elolympus.data.Auxiliar.Ubigeo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by [EnriqueZGutierreZ]
 */
public class UbigeoProcessor {

    public UbigeoProcessor() {
        String csvFile = "E:\\TRABAJO\\repositorios\\IntelliJ IDEA\\src\\main\\resources\\ubigeo_ccpp_Filtrado.csv"; // Ruta al archivo CSV
        List<Ubigeo> ubigeoList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Leer el archivo y omitir el encabezado
            ubigeoList = br.lines()
                    .skip(1)  // Omitimos la primera línea (encabezado)
                    .distinct()  // Nos aseguramos de que no haya duplicados
                    .map(line -> line.split(","))  // Separamos las columnas
                    .map(data -> createUbigeoObject(data))  // Creamos el objeto Ubigeo
                    .collect(Collectors.toList());

            // Imprimir los objetos Ubigeo creados
            ubigeoList.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Ubigeo createUbigeoObject(String[] data) {
        // Obtener los valores del archivo CSV
        String departamento = data[2].substring(0, 2);  // Tomamos los primeros 2 caracteres para el departamento
        String provincia = data[3].substring(0, 2);     // Los primeros 2 caracteres de la provincia
        String distrito = data[4].substring(0, 2);      // Los primeros 2 caracteres del distrito
        String descripcion = data[4];                   // Nombre del distrito
        // Generar el código concatenando los tres
        String codigo = departamento + provincia + distrito;
        // Usamos un contador para el ID (incremental de 1 en 1)
        Long id = IdGenerator.generateId();  // Método para generar ID incremental
        // Creamos el objeto Ubigeo
        return new Ubigeo(id, codigo, departamento, provincia, distrito, descripcion);
    }
}

// Clase para generar el ID de manera incremental
class IdGenerator {
    private static Long idCounter = 0L;

    public static Long generateId() {
        return ++idCounter;
    }
}
