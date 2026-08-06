package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.Seleccion;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class SeleccionRowMapper implements RowMapper<Seleccion> {
    @Override
    public Seleccion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(rs.getInt("idSeleccion"));
        seleccion.setFechaSeleccion(rs.getDate("fechaSeleccion").toLocalDate());
        seleccion.setEstado(Estado.valueOf(rs.getString("estado")));
        seleccion.setIdUsuario(rs.getInt("idusuario"));
        seleccion.setIdAsistente(rs.getInt("idasistente"));
        return seleccion;
    }
}
 