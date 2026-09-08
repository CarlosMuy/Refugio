
package com.mycompany.refugio;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ControlSistema {
    private static final int MAX_ANIMALES = 100;
    private static final int MAX_BITACORA = 200;
    private static final int FILAS_ZONAS = 4;
    private static final int COLUMNAS_JAULAS = 5;
    
    private Animal[] ListaAnimales = new Animal[MAX_ANIMALES];
    private int contadorAnimales = 0;
    
    private EntradaBitacora[] bitacoraAcciones = new EntradaBitacora[MAX_BITACORA];
    private int totalAcciones = 0;
    
    private EntradaBitacora[] bitacoraErrores = new EntradaBitacora[MAX_BITACORA];
    private int totalErrores = 0;
    
    private String[][] matrizRefugio = new String[FILAS_ZONAS][COLUMNAS_JAULAS];
    
    private String usuarioLogueado = "";
    private String rolLogueado = "";
    
    private String ultimoError = "";
    
    public void registrarError(String modulo, String tipoEvento, String descripcion) {
        this.ultimoError = descripcion;
        
        if(totalErrores < MAX_BITACORA) {
            bitacoraErrores[totalErrores] = new EntradaBitacora(getFechaActual(), usuarioLogueado, modulo, tipoEvento, descripcion);
            totalErrores++;
            guardarArchivoTexto("bitacora_errores.txt", bitacoraErrores, totalErrores);
        }
    }
    
    public String getUltimoError() {
        return ultimoError;
    }
    
    public ControlSistema() {
        for (int i = 0; i < FILAS_ZONAS; i++) {
            for (int j = 0; j < COLUMNAS_JAULAS; j++) {
             matrizRefugio[i][j] = "";   
            }
        }
    }
    
    private String getFechaActual() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dtf.format(LocalDateTime.now());
    }
    
    public void registrarAccion(String modulo, String tipoEvento, String descripcion) {
        if (totalAcciones < MAX_BITACORA) {
            bitacoraAcciones[totalAcciones] = new EntradaBitacora(getFechaActual(), usuarioLogueado, modulo, tipoEvento, descripcion);
            totalAcciones++;
            guardarArchivoTexto("bitacora_acciones.txt", bitacoraAcciones, totalAcciones);
        }
    }
    
    private void guardarArchivoTexto(String archivo, EntradaBitacora[] arreglo, int limite) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (int i = 0; i < limite; i++) {
                pw.println(arreglo[i].aFormatoTexto());
            }
        } catch (Exception e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
    
    public boolean existeCodigoAnimal(String codigo) {
        for (int i = 0; i < contadorAnimales; i++) {
            if (ListaAnimales[i] != null && ListaAnimales[i].getCodigo().equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean registrarAnimalConValidacion(String codigo, String nombre, String especie, int edad, String estadoClinico, int fila, int col) {
        if (codigo == null || !codigo.matches("^A-\\d+$")) {
            registrarError("ANIMALES", "CODIGO_INVALIDO", "Formato de codigo incorrecto: " + codigo);
            return false;
        }
        if (existeCodigoAnimal(codigo)) {
            registrarError("ANIMALES", "CODIGO_DUPLICADO", "El codigo " + codigo + " ya existe o fue dado de baja.");
            return false;
        }
        
        if (!especie.equalsIgnoreCase("Perro") && !especie.equalsIgnoreCase("Gato")) {
            registrarError("ANIMALES", "ESPECIE_INVALIDA" , "Especie fuera de dominio: " + especie);
            return false;
        }
        
        if (edad < 0 || edad > 25) {
            registrarError("ANIMALES", "EDAD_INVALIDA", "Edad fuera de rango: " + edad);
            return false;
        }
        
        if (!estadoClinico.equals("EN_OBSERVACION") && !estadoClinico.equals("EN_TRATAMIENTO") && !estadoClinico.equals("APTO")) {
            registrarError("ANIMALES", "ESTADO_CLINICO_INVALIDO", "Estado clinico no permitido: " + estadoClinico);
            return false;
        }
        
        if (fila < 0 || fila >= FILAS_ZONAS || col < 0 || col >= COLUMNAS_JAULAS) {
            registrarError("ANIMALES", "POSICION_INVALIDA", "Coordenadas fuera de rango.");
            return false;
        }
        
        if (matrizRefugio[fila][col] != null && !matrizRefugio[fila][col].toString().trim().isEmpty()){ {
            registrarError("ANIMALES", "JAULA_OCUPADA", "La jaula z" + (fila + 1) + "_J" + (col + 1) + " esta ocupada.");
            return false;
         }
        }
        
        Animal nuevo = new Animal(codigo, nombre, especie, edad, estadoClinico, "DISPONIBLE");
        
        if (contadorAnimales < MAX_ANIMALES) {
        ListaAnimales[contadorAnimales] = nuevo;
        contadorAnimales++;
    }
        
        matrizRefugio[fila][col] = nombre;
        registrarAccion("ANIMALES", "ANIML_REGISTRADO", "Animal " + codigo + " (" + nombre + ") asignado a z" + (fila + 1) + "_J" + (col + 1));
        return true;
    }
    
    public boolean darDeBajaAnimal(String codigo) { 
        for (int i = 0; i < contadorAnimales; i++){
            Animal a = ListaAnimales[i];
            if (a != null && a.getCodigo().equalsIgnoreCase(codigo)) {
                if (a.getEstadoAdopcion().equals("ELIMINADO")) {
                    registrarError("ANIMALES", "BAJA_FALLIDA", "El animal " + codigo + " ya esta ELIMINADO.");
                    return false;
                }
                
                a.setEstadoAdopcion("ELIMINADO");
                
                for (int f = 0; f < FILAS_ZONAS; f++) {
                    for (int c = 0; c < COLUMNAS_JAULAS; c++) {
                        if (matrizRefugio[f][c].equalsIgnoreCase(a.getNombre())) {
                            matrizRefugio[f][c] = "";
                        }
                    }
            }
            
            registrarAccion("ANIMALES", "BAJA_LOGICA", "Animal " + codigo + " marcado como ELIMINADO.");
            return false;
        }
                
    }
        registrarError("ANIMALES", "ANIMAL_NO_ENCONTRADO", "No se encontro el animal con codigo: " + codigo);
        return false;
    }
    
    public boolean autenticar(String usuario, String contrasena){
        if (usuario.equals("admin1") && contrasena.equals("Refugio2026")) {
            usuarioLogueado = usuario;
            rolLogueado = "ADMIN";
            registrarAccion("AUTENTICACION", "LOGIN_OK", "Inicio de sesión correcto");
            return true;
        } else if (usuario.equals("auxiliar1") && contrasena.equals("Auxiliar2026")) {
            usuarioLogueado = usuario;
            rolLogueado = "AUXILIAR";
            registrarAccion("AUTENTICACION", "LOGIN_OK", "Inicio de sesión correcto");
            return true;
        }
        return false;
    }
    
    public String getUsuarioLogueado() { return usuarioLogueado; }
    public String getRolLogueado() { return rolLogueado; }
    
    public Animal obtenerAnimalEnMatriz(int fila, int col){
        if (fila >= 0 && fila < 4 && col >= 0 && col < 5) {
            String nombreOID = matrizRefugio[fila][col];
            if (nombreOID != null && !nombreOID.isEmpty()) {
                for (Animal a: ListaAnimales) {
                    if (a != null && a.getNombre().equals(nombreOID)) {
                        return a;
                    }
                }
            }
        }
        return null;
    }
    
    public String cargarBitacoraTexto(String nombreArchivo) {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (IOException e) {
            return "No se pudo leer el archivo " + nombreArchivo + " o aún no contiene registros.";
        }
        return contenido.length() > 0 ? contenido.toString() : "El archivo está vacío.";
    }
}

