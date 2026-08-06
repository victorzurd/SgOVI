package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class CandidatoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    
    public void addCandidato(int idRequest, int idAsistente) {
        jdbcTemplate.update(
            "INSERT INTO candidatos (idrequest, idasistente) VALUES (?, ?) ON CONFLICT DO NOTHING",
            idRequest, idAsistente
        );
    }

    
    public List<Integer> getIdsCandidatosPorSolicitud(int idRequest) {
        return jdbcTemplate.queryForList(
            "SELECT idasistente FROM candidatos WHERE idrequest = ?", 
            Integer.class, 
            idRequest
        );
    }

    
    public List<AsistentePersonal> getAsistentesCandidatos(int idRequest) {
        String sql = "SELECT a.* FROM asistentepersonal a " +
                     "JOIN candidatos c ON a.idasistente = c.idasistente " +
                     "WHERE c.idrequest = ?";
        
        return jdbcTemplate.query(sql, new AsistentePersonalRowMapper(), idRequest); 
    }

    public void deleteCandidato(int idRequest, int idAsistente) {
        this.jdbcTemplate.update(
            "DELETE FROM candidatos WHERE idrequest = ? AND idasistente = ?",
            idRequest, idAsistente
        );
    }
}