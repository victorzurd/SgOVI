package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uji.ei1027.clubesportiu.model.Contrato;

@Repository
public class ContratoDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void crearContrato(Contrato c) {
        String sql = "INSERT INTO contrato (id_request, id_asistente, id_usuario, contenido_html, estado) " +
                     "VALUES (?, ?, ?, ?, 'pendiente_firma')";
        jdbcTemplate.update(sql, c.getIdRequest(), c.getIdAsistente(), c.getIdUsuario(), c.getContenidoHtml());
    }

    @Transactional
    public void registrarFirmaUsuario(int idContrato, String firmaBase64, String ip) {
        String sql = "UPDATE contrato SET firma_usuario = ?, fecha_firma_usuario = CURRENT_TIMESTAMP, ip_usuario = ?, " +
                     "estado = CASE WHEN firma_asistente IS NOT NULL THEN 'firmado' ELSE 'pendiente_firma_asistente' END " +
                     "WHERE id_contrato = ?";
        jdbcTemplate.update(sql, firmaBase64, ip, idContrato);
    }

    @Transactional
    public void registrarFirmaAsistente(int idContrato, String firmaBase64, String ip) {
        String sql = "UPDATE contrato SET firma_asistente = ?, fecha_firma_asistente = CURRENT_TIMESTAMP, ip_asistente = ?, " +
                     "estado = CASE WHEN firma_usuario IS NOT NULL THEN 'firmado' ELSE 'pendiente_firma_usuario' END " +
                     "WHERE id_contrato = ?";
        jdbcTemplate.update(sql, firmaBase64, ip, idContrato);
    }

    public Contrato getContrato(int idContrato) {
        String sql = "SELECT * FROM contrato WHERE id_contrato = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ContratoRowMapper(), idContrato);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean existeContrato(int idRequest) {
        String sql = "SELECT COUNT(*) FROM contrato WHERE id_request = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idRequest);
        return count != null && count > 0;
    }

    public Contrato getContratoPorRequest(int idRequest) {
        String sql = "SELECT * FROM contrato WHERE id_request = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ContratoRowMapper(), idRequest);
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }

    public List<Contrato> getContratosByAsistente(int idAsistente) {
        String sql = "SELECT * FROM contrato WHERE id_asistente = ?";
        return jdbcTemplate.query(sql, new ContratoRowMapper(), idAsistente);
    }

    public List<Contrato> getContratosByUsuario(int idUsuario) {
        String sql = "SELECT * FROM contrato WHERE id_usuario = ?";
        return jdbcTemplate.query(sql, new ContratoRowMapper(), idUsuario);
    }
}