package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.RegistroContrato;

@Repository
public class RegistroContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<RegistroContrato> getContratosPorUsuario(int idUsuario) {
        String sql = "SELECT rc.* FROM registrocontrato rc " +
                     "JOIN aprequest r ON rc.idrequest = r.idrequest " +
                     "WHERE r.idusuario = ?";
        return jdbcTemplate.query(sql, new RegistroContratoRowMapper(), idUsuario);
    }

    public List<RegistroContrato> getContratosPorAsistente(int idAsistente) {
    String sql = "SELECT rc.* FROM registrocontrato rc " +
                 "JOIN seleccion s ON rc.idseleccion = s.idseleccion " +
                 "WHERE s.idasistente = ?";
    return jdbcTemplate.query(sql, new RegistroContratoRowMapper(), idAsistente);
    }

    public int countContratosPorUsuario(int idUsuario, String buscar) {

        String sql = """
            SELECT COUNT(*)
            FROM registrocontrato rc
            JOIN aprequest r ON rc.idrequest = r.idrequest
            WHERE r.idusuario = ?
            AND (
                    CAST(rc.idcontrato AS TEXT) LIKE ?
                OR LOWER(rc.estado::text) LIKE LOWER(?)
            )
            """;

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                idUsuario,
                filtro,
                filtro);
    }

    public List<RegistroContrato> getContratosPorUsuarioPaginados(
            int idUsuario,
            String buscar,
            int limit,
            int offset) {

        String sql = """
            SELECT rc.*
            FROM registrocontrato rc
            JOIN aprequest r ON rc.idrequest = r.idrequest
            WHERE r.idusuario = ?
            AND (
                    CAST(rc.idcontrato AS TEXT) LIKE ?
                OR LOWER(rc.estado::text) LIKE LOWER(?)
            )
            ORDER BY rc.idcontrato DESC
            LIMIT ? OFFSET ?
            """;

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.query(
                sql,
                new RegistroContratoRowMapper(),
                idUsuario,
                filtro,
                filtro,
                limit,
                offset);
    }

    public int countContratosPorAsistente(int idAsistente, String buscar) {

        String sql = """
            SELECT COUNT(*)
            FROM registrocontrato rc
            JOIN seleccion s ON rc.idseleccion = s.idseleccion
            WHERE s.idasistente = ?
            AND (
                    CAST(rc.idcontrato AS TEXT) LIKE ?
                OR LOWER(rc.estado::text) LIKE LOWER(?)
            )
            """;

        String filtro = "%" + buscar + "%";

        Integer total = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                idAsistente,
                filtro,
                filtro);

        return total == null ? 0 : total;
    }

    public List<RegistroContrato> getContratosPorAsistentePaginados(
            int idAsistente,
            String buscar,
            int limit,
            int offset) {

        String sql = """
            SELECT rc.*
            FROM registrocontrato rc
            JOIN seleccion s ON rc.idseleccion = s.idseleccion
            WHERE s.idasistente = ?
            AND (
                    CAST(rc.idcontrato AS TEXT) LIKE ?
                OR LOWER(rc.estado::text) LIKE LOWER(?)
            )
            ORDER BY rc.idcontrato DESC
            LIMIT ? OFFSET ?
            """;

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.query(
                sql,
                new RegistroContratoRowMapper(),
                idAsistente,
                filtro,
                filtro,
                limit,
                offset);
    }

    public void addContrato(RegistroContrato contrato) {

        String sql = """
            INSERT INTO registrocontrato
            (fechainicio,
            fechafin,
            documentopdf,
            estado,
            idrequest,
            idseleccion)
            VALUES (?, ?, ?, CAST(? AS estado), ?, ?)
            """;

        jdbcTemplate.update(sql,
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getDocumentoPdf(),
                contrato.getEstado(),
                contrato.getIdRequest(),
                contrato.getIdSeleccion());
    }

    public boolean existeContrato(int idRequest) {

        String sql = """
            SELECT COUNT(*)
            FROM registrocontrato
            WHERE idrequest = ?
            """;

        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idRequest);

        return total != null && total > 0;
    }
}