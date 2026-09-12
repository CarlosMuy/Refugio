
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
    
    public void liberarCeldaPorCodigoAnimal(String codigoAnimal) {
        if (codigoAnimal == null) return;
        for (int i = 0; i < FILAS_ZONAS; i++) {
            for (int j = 0; j < COLUMNAS_JAULAS; j++) {
                if (matrizRefugio[i][j] != null && matrizRefugio[i][j].equals(codigoAnimal)) {
                    matrizRefugio[i][j] = null;
                    return;
                }
            }
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
        
        Animal nuevo = new Animal(codigo, nombre, especie, edad, estadoClinico, fila, col);
        
        if (contadorAnimales < MAX_ANIMALES) {
        ListaAnimales[contadorAnimales] = nuevo;
        contadorAnimales++;
    }
        
        matrizRefugio[fila][col] = nombre;
        registrarAccion("ANIMALES", "ANIML_REGISTRADO", "Animal " + codigo + " (" + nombre + ") asignado a z" + (fila + 1) + "_J" + (col + 1));
        return true;
    }
    
    public Animal buscarAnimalPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        for (int i = 0; i < contadorAnimales; i++) {
            if (ListaAnimales[i] != null && ListaAnimales[i].getCodigo().equalsIgnoreCase(codigo.trim())) {
                return ListaAnimales[i];
            }
        }
        return null;
    }
    
    public Adoptante buscarAdoptantePorCodigo(String codigo) {
        if (codigo == null) return null;
        for (int i = 0; i < contadorAdoptantes; i++) {
            if (listaAdoptantes[i] != null && listaAdoptantes[i].getCodigo().equalsIgnoreCase(codigo.trim())) {
                return listaAdoptantes[i];
            }
        }        
        return null;
    }
    public boolean actualizarEstadoClinico(String codigo, String nuevoEstado) {
        Animal animal = buscarAnimalPorCodigo(codigo);
        
        if (animal == null || "ELIMINADO".equals(animal.getEstadoAdopcion())) {
            registrarError("ANIMALES", "BUSQUEDA_FALLIDA", "El animal " + codigo + " no existe o esta eliminado.");
            return false;
        }
        
        animal.setEstadoClinico(nuevoEstado);
        registrarAccion("ANIMALES", "CAMBIO_ESTADO","Se actualizo el estado clinico de " + codigo + " a " + nuevoEstado);
        return true;
    }
    
    public boolean procesarBajaOLiberacion(String codigo, String nuevoEstadoAdopcion) {
        Animal animal = buscarAnimalPorCodigo(codigo);
        
        if (animal == null || "ELIMINADO".equals(animal.getEstadoAdopcion())) {
            registrarError("ANIMALES", "OPERACIÓN_INVALIDA", "El animal " + codigo + " no esta disponible o ya fue eliminado.");
            return true;
        }
        
        animal.setEstadoAdopcion(nuevoEstadoAdopcion);
        
        int f = animal.getFila();
        int c = animal.getCol();
        if (f >= 0 && c >= 0 && matrizRefugio[f][c] != null) {
            matrizRefugio[f][c] = null;
            registrarAccion("UBICACIONES", "CELDA_LIBERADA", "Se libero la jaula z" + (f + 1) + "_J" + (c + 1) + " porestadp " + nuevoEstadoAdopcion + " del animal " + codigo);
        }
        
        registrarAccion("ANIMALES", "CAMBIO_ADOPCION", "El animal "+ codigo + " cambio su estado a " + nuevoEstadoAdopcion);
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
    
    public boolean asignarUbicacion(String codigoAnimal, int fila, int columna) {
    if (fila < 0 || fila >= FILAS_ZONAS || columna < 0 || columna >= COLUMNAS_JAULAS) {
        registrarError("UBICACIONES", "INDICE_INVALIDO", "Las coordenadas [" + fila + "][" + columna + "] están fuera de la matriz.");
        return false;
    }

    if (matrizRefugio[fila][columna] != null && !matrizRefugio[fila][columna].isEmpty()) {
        registrarError("UBICACIONES", "ESPACIO_OCUPADO", "La celda [" + fila + "][" + columna + "] ya contiene al animal " + matrizRefugio[fila][columna]);
        return false;
    }

    matrizRefugio[fila][columna] = codigoAnimal;
    registrarAccion("UBICACIONES", "ASIGNACION_EXITOSA", "Se asignó " + codigoAnimal + " a la celda [" + fila + "][" + columna + "]");
    return true;
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
    
    private static final int MAX_ADOPTANTES = 100;
    private Adoptante[] listaAdoptantes = new Adoptante[MAX_ADOPTANTES];
    private int contadorAdoptantes = 0;

    public boolean registrarAdoptante(String codigo, String nombre, String dpi, String telefono) {
    
    if (codigo == null || !codigo.matches("^AD-\\d+$")) {
        registrarError("ADOPTANTES", "FORMATO_INVALIDO", "El código debe tener el prefijo 'AD-' seguido de números (ej. AD-007).");
        return false;
    }

    if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
        registrarError("ADOPTANTES", "FORMATO_INVALIDO", "El nombre solo debe contener letras y espacios.");
        return false;
    }

    if (dpi == null || !dpi.matches("^\\d{13}$")) {
        registrarError("ADOPTANTES", "FORMATO_INVALIDO", "El DPI debe contener exactamente 13 dígitos numéricos.");
        return false;
    }

    if (telefono == null || !telefono.matches("^\\d{8}$")) {
        registrarError("ADOPTANTES", "FORMATO_INVALIDO", "El teléfono debe contener exactamente 8 dígitos numéricos.");
        return false;
    }

    for (int i = 0; i < contadorAdoptantes; i++) {
        if (listaAdoptantes[i] != null && listaAdoptantes[i].getDpi().equals(dpi.trim())) {
            registrarError("ADOPTANTES", "DPI_DUPLICADO", "El DPI " + dpi + " ya se encuentra registrado.");
            return false;
        }
    }

    if (contadorAdoptantes < MAX_ADOPTANTES) {
        listaAdoptantes[contadorAdoptantes] = new Adoptante(codigo.trim(), nombre.trim(), dpi.trim(), telefono.trim());
        contadorAdoptantes++;
        registrarAccion("ADOPTANTES", "REGISTRO_EXITOSO", "Adoptante registrado: " + nombre + " (DPI: " + dpi + ")");
        return true;
    } else {
        registrarError("ADOPTANTES", "CAPACIDAD_MAXIMA", "No se pueden registrar más adoptantes.");
        return false;
    }
}
    
    private static final int MAX_SOLICITUDES = 100;
    private SolicitudAdopcion[] listaSolicitudes = new SolicitudAdopcion[MAX_SOLICITUDES];
    private int contadorSolicitudes = 0;
    
    public boolean crearSolicitudAdopcion(String codigo, String codigoAnimal, String codigoAdoptante, String fecha){
         if (codigo == null || !codigo.matches("^S-\\d+$")) {
             registrarError("SOLICITUDES", "FORMATO_INVALIDO", "El codigo debe iniciar con 'S-' (eje. S-012");
             return false;
         }
         
         Animal animal = buscarAnimalPorCodigo(codigoAnimal);
         if (animal == null) {
             registrarError("SOLICITUDES", "ANIMAL_NO_ENCONTRADO", "El animal " + codigoAnimal + " no existe.");
             return false;
         }
         
         if (!"APTO".equals(animal.getEstadoClinico()) && !"DISPONIBLE".equals(animal.getEstadoAdopcion())) {
             registrarError("SOLICITUDES", "ANIMAL_NO_DISPONIBLE", "El animal " + codigoAnimal + " no está DISPONIbLE para adoción.");
             return false;
         }
         
         Adoptante adoptante = buscarAdoptantePorCodigo(codigoAdoptante);
         if (adoptante == null) {
             registrarError("SOLICITUDES", "ADOPTANTE_NO_ENCONTRADO", "El adoptante " + codigoAdoptante + " no existe.");
             return false;
         }
         
         if (fecha == null) return false;
         String fechaLimpia = fecha.trim();
                 
                 if (!fechaLimpia.matches("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}")) {
             registrarError("SOLICITUDES", "FECHA_INVALIDA", "La fecha debe tener el formato dd/mm/aaaa.");
             return false;
         }
         
         if (contadorSolicitudes < MAX_SOLICITUDES) {
             listaSolicitudes[contadorSolicitudes++] = new SolicitudAdopcion(codigo.trim(), codigoAnimal.trim(), codigoAdoptante.trim(), fecha.trim());
             registrarAccion("SOLICITUDES", "SOLICITUD_CREADA", "Solicitud " + codigo + " creada para animal " + codigoAnimal);
             return true;
         } else {
             registrarError("SOLICITUDES", "CAPACIDAD_MAXIMA", "No se pueden registrar más solicitudes.");
             return false;
         }
    }
    
    public boolean aprobarSolicitud(String codigoSolicitud) {
        SolicitudAdopcion solicitudAprobar = null;
        
        for (int i = 0; i < contadorSolicitudes; i++) {
            if (listaSolicitudes[i] != null && listaSolicitudes[i].getCodigo().equals(codigoSolicitud)) {
                solicitudAprobar = listaSolicitudes[i];
                break;
            }
        }
        
        if (solicitudAprobar == null) {
            registrarError("SOLICITUDES", "NO_ENCONTRADA", "La solicitud " + codigoSolicitud + " no existe.");
            return false;
        }
        
        String codAnimal = solicitudAprobar.getCodigoAnimal();
        solicitudAprobar.setEstado("APROBAR");
        registrarAccion("SOLICITUDES", "SOLICITUD_APROBADA", "Solicitud " + codigoSolicitud + " aprobada.");
        
        Animal a = buscarAnimalPorCodigo(codAnimal);
        if (a != null) {
            a.setEstadoAdopcion("ADOPTADO");
            liberarCeldaPorCodigoAnimal(codAnimal);
        }
        
        for (int i = 0; i < contadorSolicitudes; i++) {
            SolicitudAdopcion s = listaSolicitudes[i];
            if (s != null && !s.getCodigo().equals(codigoSolicitud) && s.getCodigoAnimal().equals(codAnimal) && "PENDIENTE".equals(s.getEstado())) {
                s.setEstado("RECHAZADA");
                registrarAccion("SOLICITUDES", "SOLICITUD_RECHAZADA_AUTO", "Solicitud " + s.getCodigo() + " rechazada automáticamente al aprobar " + codigoSolicitud);
            }
        }
        return true;
    }

    public boolean eliminarAdoptante(String dpi) {
        for (int i = 0; i < contadorAdoptantes; i++) {
            if (listaAdoptantes[i] != null && listaAdoptantes[i].getDpi().equals(dpi)) {
             if (listaAdoptantes[i].isTieneSolicitudAprobadaActiva()) {
                registrarError("ADOPTANTES", "ELIMINACION_INVALIDA", "No se puede eliminar un adoptante con una solicitud Aprobada activa.");
                return false;
            }
            
            for (int j = i; j < contadorAdoptantes - 1; j++) {
                listaAdoptantes[j] = listaAdoptantes[j + 1];
            }
            listaAdoptantes[contadorAdoptantes - 1] = null;
            contadorAdoptantes--;
            
            registrarAccion("ADOPTANTES", "ELIMINACION_EXITOSA", "Se eliminó al adoptante con DPI: " + dpi);
            return true;
        }
    }
    registrarError("ADOPTANTES", "NO_ENCONTRADO", "No se encontró ningún adoptante con el DPI especificado.");
    return false;
}
    
    public void liberarCelda(int fila, int columna) {
        if (fila >= 0 && fila < FILAS_ZONAS && columna >= 0 && columna < COLUMNAS_JAULAS) {
            matrizRefugio[fila][columna] = null;
        }
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

