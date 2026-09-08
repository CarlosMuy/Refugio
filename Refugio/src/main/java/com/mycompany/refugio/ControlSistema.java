
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
    
    public void registrarError(String modulo, String tipoEvento, String descripcion) {
        if (totalErrores <MAX_BITACORA) {
            bitacoraErrores[totalErrores] = new EntradaBitacora(getFechaActual(), usuarioLogueado, modulo, tipoEvento, descripcion);
            totalErrores++;
            guardarArchivoTexto("bitacora_errores.txt", bitacoraErrores , totalErrores);
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
    
    public boolean registrarAnimal(String nombre, String especie, int edad, int fila, int col) {
        if (fila >= 0 && fila < FILAS_ZONAS && col >= 0 && col < COLUMNAS_JAULAS) {
            if (matrizRefugio[fila][col].isEmpty()) {
                String codigo = "A-" + (fila + 1) + "-" + (col + 1);
                
                Animal nuevo = new Animal(codigo, nombre, especie, edad, "sano", "disponible");
                if (contadorAnimales < MAX_ANIMALES) {
                    ListaAnimales[contadorAnimales] = nuevo;
                    contadorAnimales++;
                }
                
                matrizRefugio[fila][col] = nombre;
                registrarAccion("REGISTRO", "ANIMAL_REGISTRADO", "Animal " + nombre + " asignado a z" + (fila + 1) + "_J" + (col + 1));
                
                return true;
            } else {
                registrarError("REGISTRO", "JAULA_OCUPADA", "Intento de registro en casilla ocupada z" + (fila + 1) + "_J" + (col + 1));
            }
        }
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

