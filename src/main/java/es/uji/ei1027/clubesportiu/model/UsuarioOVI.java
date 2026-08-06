package es.uji.ei1027.clubesportiu.model;

public class UsuarioOVI {

    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private boolean consentimientoRGBD;
    private boolean estadoAceptado;
    private String password;

    public UsuarioOVI() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isConsentimientoRGBD() {
        return consentimientoRGBD;
    }

    public void setConsentimientoRGBD(boolean consentimientoRGBD) {
        this.consentimientoRGBD = consentimientoRGBD;
    }

    public boolean isEstadoAceptado() {
        return estadoAceptado;
    }

    public void setEstadoAceptado(boolean estadoAceptado) {
        this.estadoAceptado = estadoAceptado;
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    @Override
    public String toString() {
        return "UsuarioOVI{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", consentimientoRGPD=" + consentimientoRGBD +
                ", estadoAceptado=" + estadoAceptado +
                '}';
    }
}