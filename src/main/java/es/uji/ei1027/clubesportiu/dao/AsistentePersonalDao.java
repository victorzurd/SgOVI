package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;

@Repository
public class AsistentePersonalDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    
    public void addAsistentePersonal(AsistentePersonal asistente) {
        jdbcTemplate.update(
            "INSERT INTO AsistentePersonal (nombre, apellidos, email, contraseña, telefono, disponibilidad, estadoAceptado, activo, zona, preferencias, puntuacion, consentimientoRGBD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            asistente.getNombre(),
            asistente.getApellidos(),
            asistente.getEmail(),
            asistente.getContraseña(),
            asistente.getTelefono(),
            asistente.getDisponibilidad(),
            asistente.isEstadoAceptado(),
            asistente.isActivo(),
            asistente.getZona(),
            asistente.getPreferencias(),
            asistente.getPuntuacion(),
            asistente.isConsentimientoRGBD()
        );
    }

    
    public void updateAsistentePersonal(AsistentePersonal asistente) {
        jdbcTemplate.update(
            "UPDATE AsistentePersonal SET nombre=?, apellidos=?, email=?, contraseña=?, telefono=?, disponibilidad=?, estadoAceptado=?, activo=?, zona=?, preferencias=?, puntuacion=?, consentimientoRGBD=? WHERE idAsistente=?",
            asistente.getNombre(),
            asistente.getApellidos(),
            asistente.getEmail(),
            asistente.getContraseña(),
            asistente.getTelefono(),
            asistente.getDisponibilidad(),
            asistente.isEstadoAceptado(),
            asistente.isActivo(),
            asistente.getZona(),
            asistente.getPreferencias(),
            asistente.getPuntuacion(),
            asistente.isConsentimientoRGBD(),
            asistente.getIdAsistente()
        );
    }

    
    public void deleteAsistentePersonal(int idAsistente) {
        jdbcTemplate.update("DELETE FROM AsistentePersonal WHERE idAsistente=?", idAsistente);
    }

    
    public AsistentePersonal getAsistentePersonal(int idAsistente) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE idAsistente=?",
                    new AsistentePersonalRowMapper(),
                    idAsistente
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    
    public List<AsistentePersonal> getAsistentesPersonales() {
        return jdbcTemplate.query(
                "SELECT * FROM AsistentePersonal WHERE estadoAceptado = true",
                new AsistentePersonalRowMapper()
        );
    }

    

    public List<AsistentePersonal> buscarCompatibles(APRequest request) {
        return jdbcTemplate.query(
                "SELECT * FROM AsistentePersonal",
                new AsistentePersonalRowMapper()
        );
    }

    public AsistentePersonal getAsistentePersonalByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE email=?",
                    new AsistentePersonalRowMapper(),
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    
    public boolean existeEmail(String email, int idAsistente) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM AsistentePersonal WHERE email = ? AND idAsistente != ?",
                Integer.class,
                email,
                idAsistente
        );
        return count != null && count > 0;
    }

        
        public List<AsistentePersonal> getAsistentesPersonalesPendientes() {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal WHERE estadoAceptado = false",
                    new AsistentePersonalRowMapper()
            );
        }


    public int countAsistentesPendientes() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM AsistentePersonal WHERE estadoAceptado = false",
            Integer.class
        );
        return (count != null) ? count : 0;
    }

    public List<AsistentePersonal> getAsistentesPaginados(String buscar, int limit, int offset) {

        String sql =
            "SELECT * FROM AsistentePersonal " +
            "WHERE estadoAceptado = true " +
            "AND (LOWER(nombre) LIKE LOWER(?) " +
            "OR LOWER(apellidos) LIKE LOWER(?) " +
            "OR LOWER(email) LIKE LOWER(?)) " +
            "ORDER BY nombre " +
            "LIMIT ? OFFSET ?";

        String filtro = "%" + buscar + "%";

        return jdbcTemplate.query(
                sql,
                new AsistentePersonalRowMapper(),
                filtro,
                filtro,
                filtro,
                limit,
                offset
        );
    }

    public int countAsistentes(String buscar) {

        String sql =
            "SELECT COUNT(*) FROM AsistentePersonal " +
            "WHERE estadoAceptado = true " +
            "AND (LOWER(nombre) LIKE LOWER(?) " +
            "OR LOWER(apellidos) LIKE LOWER(?) " +
            "OR LOWER(email) LIKE LOWER(?))";

        String filtro = "%" + buscar + "%";

        Integer total = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                filtro,
                filtro,
                filtro
        );

        return total == null ? 0 : total;
    }
}