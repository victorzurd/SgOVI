package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.ComunicacionUsuarioOVIPAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ComunicacionUsuarioOVIPAPDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    
    public void addComunicacion(ComunicacionUsuarioOVIPAP comunicacion) {

        jdbcTemplate.update(
                "INSERT INTO ComunicacionUsuarioOVIPAP VALUES (?, ?, ?, ?, ?, ?)",
                comunicacion.getIdComunicacion(),
                comunicacion.getIdSeleccion(),
                comunicacion.getFecha(),
                comunicacion.getMensaje(),
                comunicacion.getEmisor(),
                comunicacion.getReceptor()
        );
    }

    
    public void deleteComunicacion(int idComunicacion) {
        jdbcTemplate.update(
                "DELETE FROM ComunicacionUsuarioOVIPAP WHERE idComunicacion=?",
                idComunicacion
        );
    }

    
    public void updateComunicacion(ComunicacionUsuarioOVIPAP comunicacion) {

        jdbcTemplate.update(
                "UPDATE ComunicacionUsuarioOVIPAP SET idSeleccion=?, fecha=?, mensaje=?, emisor=?, receptor=? WHERE idComunicacion=?",
                comunicacion.getIdSeleccion(),
                comunicacion.getFecha(),
                comunicacion.getMensaje(),
                comunicacion.getEmisor(),
                comunicacion.getReceptor(),
                comunicacion.getIdComunicacion()
        );
    }

    
    public ComunicacionUsuarioOVIPAP getComunicacion(int idComunicacion) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM ComunicacionUsuarioOVIPAP WHERE idComunicacion=?",
                    new ComunicacionUsuarioOVIPAPRowMapper(),
                    idComunicacion
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    
    public List<ComunicacionUsuarioOVIPAP> getComunicaciones() {
        return jdbcTemplate.query(
                "SELECT * FROM ComunicacionUsuarioOVIPAP",
                new ComunicacionUsuarioOVIPAPRowMapper()
        );
    }

    
    public List<ComunicacionUsuarioOVIPAP> getComunicacionesBySeleccion(int idSeleccion) {
        return jdbcTemplate.query(
                "SELECT * FROM ComunicacionUsuarioOVIPAP WHERE idSeleccion=?",
                new ComunicacionUsuarioOVIPAPRowMapper(),
                idSeleccion
        );
    }
}