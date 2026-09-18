package es.uji.ei1027.clubesportiu.model;

import java.util.Date;

public class Contrato {

    private int idContrato;
    private int idRequest;
    private int idAsistente;
    private Date fechaCreacion;
    private String estado;
    private String contenidoHtml;
    private String firmaUsuario;
    private Date fechaFirmaUsuario;
    private String ipUsuario;
    private String firmaAsistente;
    private Date fechaFirmaAsistente;
    private String ipAsistente;
    private String rutaPdf;

    public Contrato() {
    }

    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public void setIdAsistente(int idAsistente) {
        this.idAsistente = idAsistente;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getContenidoHtml() {
        return contenidoHtml;
    }

    public void setContenidoHtml(String contenidoHtml) {
        this.contenidoHtml = contenidoHtml;
    }

    public String getFirmaUsuario() {
        return firmaUsuario;
    }

    public void setFirmaUsuario(String firmaUsuario) {
        this.firmaUsuario = firmaUsuario;
    }

    public Date getFechaFirmaUsuario() {
        return fechaFirmaUsuario;
    }

    public void setFechaFirmaUsuario(Date fechaFirmaUsuario) {
        this.fechaFirmaUsuario = fechaFirmaUsuario;
    }

    public String getIpUsuario() {
        return ipUsuario;
    }

    public void setIpUsuario(String ipUsuario) {
        this.ipUsuario = ipUsuario;
    }

    public String getFirmaAsistente() {
        return firmaAsistente;
    }

    public void setFirmaAsistente(String firmaAsistente) {
        this.firmaAsistente = firmaAsistente;
    }

    public Date getFechaFirmaAsistente() {
        return fechaFirmaAsistente;
    }

    public void setFechaFirmaAsistente(Date fechaFirmaAsistente) {
        this.fechaFirmaAsistente = fechaFirmaAsistente;
    }

    public String getIpAsistente() {
        return ipAsistente;
    }

    public void setIpAsistente(String ipAsistente) {
        this.ipAsistente = ipAsistente;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }

    @Override
    public String toString() {
        return "Contrato{" +
                "idContrato=" + idContrato +
                ", idRequest=" + idRequest +
                ", idAsistente=" + idAsistente +
                ", fechaCreacion=" + fechaCreacion +
                ", estado='" + estado + '\'' +
                ", contenidoHtml='" + contenidoHtml + '\'' +
                ", firmaUsuario='" + firmaUsuario + '\'' +
                ", fechaFirmaUsuario=" + fechaFirmaUsuario +
                ", ipUsuario='" + ipUsuario + '\'' +
                ", firmaAsistente='" + firmaAsistente + '\'' +
                ", fechaFirmaAsistente=" + fechaFirmaAsistente +
                ", ipAsistente='" + ipAsistente + '\'' +
                ", rutaPdf='" + rutaPdf + '\'' +
                '}';
    }
}