package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TecnicoOVIRowMapper implements RowMapper<TecnicoOVI> {
    @Override
    public TecnicoOVI mapRow(ResultSet rs, int rowNum) throws SQLException {
        TecnicoOVI tecnico = new TecnicoOVI();
        tecnico.setIdTecnico(rs.getInt("idtecnico"));
        tecnico.setCorreo(rs.getString("correo"));
        tecnico.setPassword(rs.getString("password"));
        tecnico.setNombre(rs.getString("nombre"));
        return tecnico;
    }
}