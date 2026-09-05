
package com.mycompany.refugio;


public class Animal {
    private String codigo;
    private String especie;
    private int edadEstimada;
    private String estadoClinico;
    private String estadoAdopcion;
    
    public Animal(String codigo, String especie, int edadEstimada, String estadoClinico, String estadoAdopcion) {
        this.codigo = codigo;
        this.especie = especie;
        this.edadEstimada = edadEstimada;
        this.estadoClinico = estadoClinico;
        this.estadoAdopcion = estadoAdopcion;
        
    }
    
    public String getCodigo() { return codigo; }
    public String getEspecie() { return especie;}
    public int getEdadEstimada() { return edadEstimada; }
    
    public String getEstadoClinico() { return estadoClinico; }
    public void setEstadoClinico(String estadoClinico) { this.estadoClinico = estadoClinico; }
    
    public String getEstadoAdopcion() { return estadoAdopcion; }
    public void setEstadoAdopcion(String estadoAdopcion) { this.estadoAdopcion = estadoAdopcion; }
}
