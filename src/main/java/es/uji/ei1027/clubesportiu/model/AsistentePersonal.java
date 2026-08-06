package es.uji.ei1027.clubesportiu.model;

public class AsistentePersonal {

    private int idAsistente;
    private String nombre;
    private String apellidos;
    private String email;
    private String contraseña; 
    private String telefono;
    private String disponibilidad;
    private boolean estadoAceptado;
    private boolean activo;
    private String zona; 
    private String preferencias; 
    private int puntuacion;
    private boolean consentimientoRGBD; 

    public AsistentePersonal() {
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public void setIdAsistente(int idAsistente) {
        this.idAsistente = idAsistente;
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

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public boolean isEstadoAceptado() {
        return estadoAceptado;
    }

    public void setEstadoAceptado(boolean estadoAceptado) {
        this.estadoAceptado = estadoAceptado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
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

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public boolean isConsentimientoRGBD() {
        return consentimientoRGBD;
    }

    public void setConsentimientoRGBD(boolean consentimientoRGBD) {
        this.consentimientoRGBD = consentimientoRGBD;
    }

    @Override
    public String toString() {
        return "AsistentePersonal{" +
                "idAsistente=" + idAsistente +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", contraseña='" + contraseña + '\'' +
                ", telefono='" + telefono + '\'' +
                ", disponibilidad='" + disponibilidad + '\'' +
                ", estadoAceptado=" + estadoAceptado +
                ", activo=" + activo +
                ", zona='" + zona + '\'' +
                ", preferencias='" + preferencias + '\'' +
                ", puntuacion=" + puntuacion +
                ", consentimientoRGBD=" + consentimientoRGBD +
                '}';
    }
}