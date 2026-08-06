package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class TecnicoOVIDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public TecnicoOVI loadUserByUsername(String correo) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM tecnicoovi WHERE correo=?",
                    new TecnicoOVIRowMapper(),
                    correo);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}