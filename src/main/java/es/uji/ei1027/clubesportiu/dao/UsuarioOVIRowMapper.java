package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioOVIRowMapper implements RowMapper<UsuarioOVI> {
    public  UsuarioOVI mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsuarioOVI usuarioOVI = new UsuarioOVI();
        usuarioOVI.setIdUsuario(rs.getInt("idusuario"));
        usuarioOVI.setNombre(rs.getString("nombre"));
        usuarioOVI.setApellidos(rs.getString("apellidos"));
        usuarioOVI.setEmail(rs.getString("email"));
        usuarioOVI.setTelefono(rs.getString("telefono"));
        usuarioOVI.setDireccion(rs.getString("direccion"));
        usuarioOVI.setConsentimientoRGBD(rs.getBoolean("consentimientorgbd"));
        usuarioOVI.setEstadoAceptado(rs.getBoolean("estadoaceptado"));
        usuarioOVI.setPassword(rs.getString("password"));
        return usuarioOVI;
    }
}
