package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.RegistroContrato;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistroContratoRowMapper implements RowMapper<RegistroContrato> {
    @Override
    public RegistroContrato mapRow(ResultSet rs, int rowNum) throws SQLException {
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdContrato(rs.getInt("idcontrato"));
        if (rs.getDate("fechainicio") != null) {
            contrato.setFechaInicio(rs.getDate("fechainicio").toLocalDate());
        }
        if (rs.getDate("fechafin") != null) {
            contrato.setFechaFin(rs.getDate("fechafin").toLocalDate());
        }
        contrato.setDocumentoPdf(rs.getString("documentopdf"));
        contrato.setEstado(rs.getString("estado"));
        contrato.setIdRequest(rs.getInt("idrequest"));
        contrato.setIdSeleccion(rs.getInt("idseleccion"));
        return contrato;
    }
}