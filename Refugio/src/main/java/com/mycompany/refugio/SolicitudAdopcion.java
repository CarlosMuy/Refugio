package com.mycompany.refugio;


public class SolicitudAdopcion {
    private String codigo;
    private String codigoAnimal;
    private String codigoAdopcion;
    private String fecha;
    private String estado;
    
    public SolicitudAdopcion(String codigo, String codigoAnimal, String codigoAdopcion, String fecha) {
        this.codigo = codigo;
        this.codigoAnimal = codigoAnimal;
        this.codigoAdopcion = codigoAdopcion;
        this.fecha = fecha;
        this.estado = "PENDIENTE";
    }
    
    public String getCodigo() { return codigo; }
    public String getCodigoAnimal() { return codigoAnimal; }
    public String getCodigoAdopcion() { return codigoAdopcion; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
