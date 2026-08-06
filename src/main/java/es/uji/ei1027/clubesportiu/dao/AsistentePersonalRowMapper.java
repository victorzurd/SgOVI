package es.uji.ei1027.clubesportiu.dao;
 
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
 
public class AsistentePersonalRowMapper implements RowMapper<AsistentePersonal> {
    @Override
    public AsistentePersonal mapRow(ResultSet rs, int rowNum) throws SQLException {
        AsistentePersonal asistente = new AsistentePersonal();
        asistente.setIdAsistente(rs.getInt("idAsistente"));
        asistente.setNombre(rs.getString("nombre"));
        asistente.setApellidos(rs.getString("apellidos"));
        asistente.setEmail(rs.getString("email"));
        asistente.setContraseña(rs.getString("contraseña"));
        asistente.setTelefono(rs.getString("telefono"));
        asistente.setDisponibilidad(rs.getString("disponibilidad"));
        asistente.setEstadoAceptado(rs.getBoolean("estadoAceptado"));
        asistente.setActivo(rs.getBoolean("activo"));
        asistente.setZona(rs.getString("zona"));
        asistente.setPreferencias(rs.getString("preferencias"));
        asistente.setPuntuacion(rs.getInt("puntuacion"));
        asistente.setConsentimientoRGBD(rs.getBoolean("consentimientoRGBD"));
        return asistente;
    }
}