package es.uji.ei1027.clubesportiu.model;
import java.util.Date;

public class ChatSession {
    private int idChat;
    private int idUsuario;
    private int idAsistente;
    private int idRequest;
    private Date fechaCreacion;
    private String estado;
    
    private String nombreUsuario;
    private String nombreAsistente;

    public int getIdChat() { return idChat; }
    public void setIdChat(int idChat) { this.idChat = idChat; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdAsistente() { return idAsistente; }
    public void setIdAsistente(int idAsistente) { this.idAsistente = idAsistente; }
    public int getIdRequest() { return idRequest; }
    public void setIdRequest(int idRequest) { this.idRequest = idRequest; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getNombreAsistente() { return nombreAsistente; }
    public void setNombreAsistente(String nombreAsistente) { this.nombreAsistente = nombreAsistente; }
}