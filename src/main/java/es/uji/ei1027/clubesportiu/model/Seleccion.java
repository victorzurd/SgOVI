package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class Seleccion {
    private int idSeleccion;
    private LocalDate fechaSeleccion;
    private Estado estado;
    private int idUsuario;
    private int idAsistente;

    public Seleccion() {
    }

    public int getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public LocalDate getFechaSeleccion() {
        return fechaSeleccion;
    }

    public void setFechaSeleccion(LocalDate fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public void setIdAsistente(int idAsistente) {
        this.idAsistente = idAsistente;
    }

    @Override
    public String toString() {
        return "Seleccion{" +
                "idSeleccion=" + idSeleccion +
                ", fechaSeleccion='" + fechaSeleccion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}