package es.uji.ei1027.clubesportiu.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.jasypt.util.password.BasicPasswordEncryptor;
import java.util.List;

import es.uji.ei1027.clubesportiu.model.TecnicoOVI;

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

    /** Migrates legacy clear-text technician passwords without changing their values. */
    public void migratePlaintextPasswords() {
        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        List<TecnicoOVI> tecnicos = jdbcTemplate.query("SELECT * FROM tecnicoovi", new TecnicoOVIRowMapper());
        for (TecnicoOVI tecnico : tecnicos) {
            String stored = tecnico.getPassword();
            if (stored != null && !looksLikeJasyptHash(stored)) {
                jdbcTemplate.update("UPDATE tecnicoovi SET password = ? WHERE idtecnico = ?",
                        encryptor.encryptPassword(stored), tecnico.getIdTecnico());
            }
        }
    }

    private boolean looksLikeJasyptHash(String value) {
        return value.length() >= 32 && value.matches("[A-Za-z0-9+/]+={0,2}");
    }
}
