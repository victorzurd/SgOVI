package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class APRequest {

    private int idRequest;
    private int idUsuario;
    private LocalDate fechaSolicitud;
    private String descripcion;
    private Estado estado;
    private Integer idSeleccion;
    private String titulo;
    private String zona;
    private String preferencias;
    private String horario;

    public APRequest() {
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Integer getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(Integer idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "APRequest{" +
                "idRequest=" + idRequest +
                ", idUsuario=" + idUsuario +
                ", fechaSolicitud=" + fechaSolicitud +
                ", descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                ", titulo='" + titulo + '\'' +
                ", zona='" + zona + '\'' +
                ", preferencias='" + preferencias + '\'' +
                ", horario='" + horario + '\'' +
                '}';
    }
}