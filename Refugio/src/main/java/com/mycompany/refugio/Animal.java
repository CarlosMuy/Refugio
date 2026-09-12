
package com.mycompany.refugio;


public class Animal {
    private String codigo;
    private String nombre;
    private String especie;
    private int edadEstimada;
    private String estadoClinico;
    private String estadoAdopcion;
    private int fila;
    private int col;
    
    public Animal(String codigo, String nombre, String especie, int edadEstimada, String estadoClinico, int fila, int col) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.especie = especie;
        this.edadEstimada = edadEstimada;
        this.estadoClinico = estadoClinico;
        this.estadoAdopcion = "APTO".equalsIgnoreCase(estadoClinico) ? "DISPONIBLE" : "EN_PROCESO";
        this.fila = fila;
        this.col = col;
        
    }
    
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getFila() { return fila; }
    public int getCol() { return col; }
    
    public String getEspecie() { return especie;}
    public int getEdadEstimada() { return edadEstimada; }
    public int getEdad() { return edadEstimada; }
    
    public String getEstadoClinico() { return estadoClinico; }
    public void setEstadoClinico(String estadoClinico) {
        this.estadoClinico = estadoClinico;
        if ("APTO".equalsIgnoreCase(estadoClinico) && !"ADOPTADO".equals(this.estadoAdopcion)) {
            this.estadoAdopcion = "DISPONIBLE";
        }
    }
    
    public String getEstadoAdopcion() { return estadoAdopcion; }
    public void setEstadoAdopcion(String estadoAdopcion) { this.estadoAdopcion = estadoAdopcion; }
    
    
}
