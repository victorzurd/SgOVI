package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.AsistentePersonal;

@Repository
public class CandidatoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    
    public void addCandidato(int idRequest, int idAsistente) {
        jdbcTemplate.update(
            "INSERT INTO candidato (idrequest, idasistente) VALUES (?, ?) ON CONFLICT DO NOTHING",
            idRequest, idAsistente
        );
    }

    
    public List<Integer> getIdsCandidatosPorSolicitud(int idRequest) {
        return jdbcTemplate.queryForList(
            "SELECT idasistente FROM candidato WHERE idrequest = ?", 
            Integer.class, 
            idRequest
        );
    }

    
    public List<AsistentePersonal> getAsistentesCandidatos(int idRequest) {
        String sql = "SELECT a.* FROM asistentepersonal a " +
                     "JOIN candidato c ON a.idasistente = c.idasistente " +
                     "WHERE c.idrequest = ?";
        
        return jdbcTemplate.query(sql, new AsistentePersonalRowMapper(), idRequest); 
    }

    public void deleteCandidato(int idRequest, int idAsistente) {
        this.jdbcTemplate.update(
            "DELETE FROM candidato WHERE idrequest = ? AND idasistente = ?",
            idRequest, idAsistente
        );
    }
}