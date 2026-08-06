package es.uji.ei1027.clubesportiu.model;

public class TecnicoOVI {
    private int idTecnico;
    private String correo;
    private String password;
    private String nombre;

    public TecnicoOVI() {}

    public int getIdTecnico() { return idTecnico; }
    public void setIdTecnico(int idTecnico) { this.idTecnico = idTecnico; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "TecnicoOVI{" +
                "idTecnico=" + idTecnico +
                ", correo='" + correo + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}