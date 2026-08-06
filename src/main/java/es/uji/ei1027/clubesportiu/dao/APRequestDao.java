package es.uji.ei1027.clubesportiu.dao;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Estado;

@Repository
public class APRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    
    public void addAPRequest(APRequest request) {

    jdbcTemplate.update(
    "INSERT INTO aprequest (idusuario, descripcion, estado, titulo, zona, preferencias, horario) VALUES (?, ?, CAST(? AS estado), ?, ?, ?, ?)",
        request.getIdUsuario(),
        request.getDescripcion(),
        request.getEstado().name(),
        request.getTitulo(),
        request.getZona(),
        request.getPreferencias(),
        request.getHorario()
    );
}

    
    public void deleteAPRequest(int idRequest) {
        jdbcTemplate.update(
                "DELETE FROM aprequest WHERE idrequest = ?",
                idRequest
        );
    }

    public void updateAPRequest(APRequest request) {
        
        String sql = "UPDATE aprequest SET idusuario=?, fechasolicitud=?, descripcion=?, estado=?::estado, titulo=?, zona=?, preferencias=?, horario=? WHERE idrequest=?";
        
        jdbcTemplate.update(sql, 
            request.getIdUsuario(), 
            request.getFechaSolicitud(), 
            request.getDescripcion(), 
            request.getEstado().name(),
            request.getTitulo(),
            request.getZona(),
            request.getPreferencias(),
            request.getHorario(),
            request.getIdRequest()
        );
    }

    public void updateEstadoAPRequest(APRequest request) {
        jdbcTemplate.update(
                "UPDATE aprequest SET estado=CAST(? AS estado) WHERE idrequest=?",
                request.getEstado().name(), 
                request.getIdRequest()
        );
    }

    public void asignarAsistente(int idSolicitud, int idAsistente, int idUsuario) {
        String insertSeleccion = "INSERT INTO seleccion (fechaseleccion, estado, idusuario, idasistente) " +
                                 "VALUES (CURRENT_DATE, CAST('pendiente' AS estado), ?, ?) RETURNING idseleccion";
        
        Integer idSeleccionGenerado = jdbcTemplate.queryForObject(insertSeleccion, Integer.class, idUsuario, idAsistente);
        
        String updateRequest = "UPDATE aprequest SET idseleccion = ? WHERE idrequest = ?";
        jdbcTemplate.update(updateRequest, idSeleccionGenerado, idSolicitud);
    }
    
    public APRequest getAPRequest(int idRequest) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM aprequest WHERE idrequest=?",
                    new APRequestRowMapper(),
                    idRequest
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    
    public List<APRequest> getAPRequests() {
        return jdbcTemplate.query(
                "SELECT * FROM aprequest",
                new APRequestRowMapper()
        );
    }

    
    public List<APRequest> getAPRequestsByUsuario(int idUsuario) {
        return jdbcTemplate.query(
                "SELECT * FROM aprequest WHERE idusuario=?",
                new APRequestRowMapper(),
                idUsuario
        );
    }


   public List<APRequest> getAPRequestsByAsistente(int idAsistente) {
        
        String sql = "SELECT r.* FROM aprequest r " +
                    "JOIN seleccion s ON r.idrequest = s.idseleccion " + 
                    "WHERE s.idasistente = ?";
                    
        return jdbcTemplate.query(sql, new APRequestRowMapper(), idAsistente);
    }

    public String getNombreUsuarioPorId(Integer idUsuario) {
        String sql = "SELECT nombre FROM usuarioovi WHERE idusuario = ?";
        return jdbcTemplate.queryForObject(sql, String.class, idUsuario);
    }

    public int countAPRequests(String buscar) {

        String sql = """
            SELECT COUNT(*)
            FROM aprequest
            WHERE LOWER(titulo) LIKE LOWER(?)
            OR LOWER(descripcion) LIKE LOWER(?)
            """;

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.queryForObject(sql, Integer.class, filtro, filtro);
    }

    public List<APRequest> getAPRequestsPaginados(
            String buscar,
            int limit,
            int offset) {

        String sql = """
            SELECT *
            FROM aprequest
            WHERE LOWER(titulo) LIKE LOWER(?)
            OR LOWER(descripcion) LIKE LOWER(?)
            ORDER BY idrequest DESC
            LIMIT ? OFFSET ?
            """;

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.query(
                sql,
                new APRequestRowMapper(),
                filtro,
                filtro,
                limit,
                offset);
    }

    public int countAPRequestsByUsuario(int idUsuario, String buscar) {

        String sql = """
            SELECT COUNT(*)
            FROM aprequest
            WHERE idusuario = ?
            AND (
                    LOWER(titulo) LIKE LOWER(?)
                OR LOWER(descripcion) LIKE LOWER(?)
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

    public List<APRequest> getAPRequestsByUsuarioPaginados(
            int idUsuario,
            String buscar,
            int limit,
            int offset) {

        String sql = """
            SELECT *
            FROM aprequest
            WHERE idusuario = ?
            AND (
                    LOWER(titulo) LIKE LOWER(?)
                OR LOWER(descripcion) LIKE LOWER(?)
            )
            ORDER BY idrequest DESC
            LIMIT ? OFFSET ?
            """;

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.query(
                sql,
                new APRequestRowMapper(),
                idUsuario,
                filtro,
                filtro,
                limit,
                offset);
    }
}