package es.uji.ei1027.clubesportiu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.Contrato;

@Repository
public class ContratoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void crearContrato(Contrato c) {
        String sql = "INSERT INTO contrato (id_request, id_asistente, contenido_html, estado) VALUES (?, ?, ?, 'PENDIENTE_FIRMA')";
        jdbcTemplate.update(sql, c.getIdRequest(), c.getIdAsistente(), c.getContenidoHtml());
    }

    public void registrarFirmaUsuario(int idContrato, String firmaBase64, String ip) {
        String sql = "UPDATE contrato SET firma_usuario = ?, fecha_firma_usuario = CURRENT_TIMESTAMP, ip_usuario = ? WHERE id_contrato = ?";
        jdbcTemplate.update(sql, firmaBase64, ip, idContrato);
        comprobarEstadoCompletado(idContrato);
    }

    public void registrarFirmaAsistente(int idContrato, String firmaBase64, String ip) {
        String sql = "UPDATE contrato SET firma_asistente = ?, fecha_firma_asistente = CURRENT_TIMESTAMP, ip_asistente = ? WHERE id_contrato = ?";
        jdbcTemplate.update(sql, firmaBase64, ip, idContrato);
        comprobarEstadoCompletado(idContrato);
    }

    public void comprobarEstadoCompletado(int idContrato) {
        String sql = "UPDATE contrato SET estado = 'FIRMADO' WHERE id_contrato = ? AND firma_usuario IS NOT NULL AND firma_asistente IS NOT NULL";
        jdbcTemplate.update(sql, idContrato);
    }

    public void comprobarEstadoPendienteFirma(int idContrato) {
        String sql = "UPDATE contrato SET estado = 'PENDIENTE_FIRMA' WHERE id_contrato = ? AND (firma_usuario IS NULL OR firma_asistente IS NULL)";
        jdbcTemplate.update(sql, idContrato);
    }

    public void comprobarEstadoPendienteFirmaUsuario(int idContrato) {
        String sql = "UPDATE contrato SET estado = 'PENDIENTE_FIRMA_USUARIO' WHERE id_contrato = ? AND firma_usuario IS NULL AND firma_asistente IS NOT NULL";
        jdbcTemplate.update(sql, idContrato);
    }

    public void comprobarEstadoPendienteFirmaAsistente(int idContrato) {
        String sql = "UPDATE contrato SET estado = 'PENDIENTE_FIRMA_ASISTENTE' WHERE id_contrato = ? AND firma_usuario IS NOT NULL AND firma_asistente IS NULL";
        jdbcTemplate.update(sql, idContrato);
    }

    public void comprobarEstadoPendiente(int idContrato) {
        String sql = "UPDATE contrato SET estado = 'PENDIENTE' WHERE id_contrato = ? AND firma_usuario IS NULL AND firma_asistente IS NULL";
        jdbcTemplate.update(sql, idContrato);
    }

    public void comprobarEstado(int idContrato) {
        String sql = "SELECT firma_usuario, firma_asistente FROM contrato WHERE id_contrato = ?";
        jdbcTemplate.query(sql, new Object[]{idContrato}, rs -> {
            boolean firmaUsuario = rs.getString("firma_usuario") != null;
            boolean firmaAsistente = rs.getString("firma_asistente") != null;

            if (firmaUsuario && firmaAsistente) {
                comprobarEstadoCompletado(idContrato);
            } else if (!firmaUsuario && !firmaAsistente) {
                comprobarEstadoPendiente(idContrato);
            } else if (!firmaUsuario) {
                comprobarEstadoPendienteFirmaUsuario(idContrato);
            } else if (!firmaAsistente) {
                comprobarEstadoPendienteFirmaAsistente(idContrato);
            }
        });
    }

    public Contrato getContrato(int idContrato) {
        String sql = "SELECT * FROM contrato WHERE id_contrato = ?";
        return jdbcTemplate.queryForObject(sql, new ContratoRowMapper(), idContrato); 
    }

    public boolean existeContrato(int idRequest) {
        String sql = "SELECT COUNT(*) FROM contrato WHERE idrequest = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idRequest);
        return count != null && count > 0;
    }

    public Map<Integer, Contrato> getContratosMap() {
        String sql = "SELECT * FROM contrato";
        List<Contrato> contratos = jdbcTemplate.query(sql, new ContratoRowMapper());
        Map<Integer, Contrato> contratosMap = new HashMap<>();
        for (Contrato contrato : contratos) {
            contratosMap.put(contrato.getIdRequest(), contrato);
        }
        return contratosMap;
    }
}