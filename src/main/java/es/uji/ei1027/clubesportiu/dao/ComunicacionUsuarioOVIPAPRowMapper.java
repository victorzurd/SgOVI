package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.ComunicacionUsuarioOVIPAP;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class ComunicacionUsuarioOVIPAPRowMapper implements RowMapper<ComunicacionUsuarioOVIPAP> {
    @Override
    public ComunicacionUsuarioOVIPAP mapRow(ResultSet rs, int rowNum) throws SQLException {
        ComunicacionUsuarioOVIPAP comunicacion = new ComunicacionUsuarioOVIPAP();
        comunicacion.setIdComunicacion(rs.getInt("idComunicacion"));
        comunicacion.setIdSeleccion(rs.getInt("idSeleccion"));
        comunicacion.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        comunicacion.setMensaje(rs.getString("mensaje"));
        comunicacion.setEmisor(rs.getString("emisor"));
        comunicacion.setReceptor(rs.getString("receptor"));
        return comunicacion;
    }
}
 