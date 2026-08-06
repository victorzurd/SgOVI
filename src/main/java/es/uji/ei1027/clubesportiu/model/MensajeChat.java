package es.uji.ei1027.clubesportiu.model;
import java.util.Date;

public class MensajeChat {
    private int idMensaje;
    private int idChat;
    private String remitente;
    private String contenido;
    private Date fechaEnvio;

    public int getIdMensaje() { return idMensaje; }
    public void setIdMensaje(int idMensaje) { this.idMensaje = idMensaje; }
    public int getIdChat() { return idChat; }
    public void setIdChat(int idChat) { this.idChat = idChat; }
    public String getRemitente() { return remitente; }
    public void setRemitente(String remitente) { this.remitente = remitente; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Date getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(Date fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}