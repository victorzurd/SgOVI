package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.Estado;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class APRequestRowMapper implements RowMapper<APRequest> {
    @Override
    public APRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        APRequest request = new APRequest();
        request.setIdRequest(rs.getInt("idrequest"));
        request.setIdUsuario(rs.getInt("idusuario"));
        request.setFechaSolicitud(rs.getDate("fechasolicitud").toLocalDate());
        request.setDescripcion(rs.getString("descripcion"));
        request.setEstado(Estado.valueOf(rs.getString("estado")));
        request.setIdSeleccion((Integer) rs.getObject("idseleccion"));
        request.setTitulo(rs.getString("titulo"));
        request.setZona(rs.getString("zona"));
        request.setPreferencias(rs.getString("preferencias"));
        request.setHorario(rs.getString("horario"));
        return request;
    }
}