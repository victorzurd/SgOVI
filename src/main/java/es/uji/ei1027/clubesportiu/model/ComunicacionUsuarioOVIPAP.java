package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDateTime;

public class ComunicacionUsuarioOVIPAP {

    private int idComunicacion;
    private int idSeleccion;
    private LocalDateTime fecha;
    private String mensaje;
    private String emisor;
    private String receptor;

    public ComunicacionUsuarioOVIPAP() {
    }

    public int getIdComunicacion() {
        return idComunicacion;
    }

    public void setIdComunicacion(int idComunicacion) {
        this.idComunicacion = idComunicacion;
    }

    public int getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    @Override
    public String toString() {
        return "ComunicacionUsuarioOVIPAP{" +
                "idComunicacion=" + idComunicacion +
                ", idSeleccion=" + idSeleccion +
                ", fecha=" + fecha +
                ", mensaje='" + mensaje + '\'' +
                ", emisor='" + emisor + '\'' +
                ", receptor='" + receptor + '\'' +
                '}';
    }
}