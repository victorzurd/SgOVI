package es.uji.ei1027.clubesport;

public class ContratoRowMapper implements RowMapper<Contrato> {
    @Override
    public Contrato mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contrato c = new Contrato();
        c.setIdContrato(rs.getInt("id_contrato"));
        c.setIdRequest(rs.getInt("id_request"));
        c.setIdAsistente(rs.getInt("id_asistente"));
        c.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        c.setEstado(rs.getString("estado"));
        c.setContenidoHtml(rs.getString("contenido_html"));
        c.setFirmaUsuario(rs.getString("firma_usuario"));
        c.setFechaFirmaUsuario(rs.getTimestamp("fecha_firma_usuario"));
        c.setIpUsuario(rs.getString("ip_usuario"));
        c.setFirmaAsistente(rs.getString("firma_asistente"));
        c.setFechaFirmaAsistente(rs.getTimestamp("fecha_firma_asistente"));
        c.setIpAsistente(rs.getString("ip_asistente"));
        c.setRutaPdf(rs.getString("ruta_pdf"));
        return c;
    }
}
