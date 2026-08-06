package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class RegistroContrato {
    private int idContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String documentoPdf;
    private String estado;
    private int idRequest;
    private int idSeleccion;

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getDocumentoPdf() { return documentoPdf; }
    public void setDocumentoPdf(String documentoPdf) { this.documentoPdf = documentoPdf; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdRequest() { return idRequest; }
    public void setIdRequest(int idRequest) { this.idRequest = idRequest; }

    public int getIdSeleccion() { return idSeleccion; }
    public void setIdSeleccion(int idSeleccion) { this.idSeleccion = idSeleccion; }

}