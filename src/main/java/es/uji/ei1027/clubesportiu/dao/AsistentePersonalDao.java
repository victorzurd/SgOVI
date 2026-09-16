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
            "INSERT INTO asistentepersonal (nombre, apellidos, email, contraseña, telefono, disponibilidad, estadoaceptado, activo, zona, provincia, preferencias, puntuacion, consentimientorgbd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            asistente.getNombre(),
            asistente.getApellidos(),
            asistente.getEmail(),
            asistente.getContraseña(),
            asistente.getTelefono(),
            asistente.getDisponibilidad(),
            asistente.isEstadoAceptado(),
            asistente.isActivo(),
            asistente.getZona(),
            asistente.getProvincia(),
            asistente.getPreferencias(),
            asistente.getPuntuacion(),
            asistente.isConsentimientoRGBD()
        );
    }

    public void updateAsistentePersonal(AsistentePersonal asistente) {
        jdbcTemplate.update(
            "UPDATE asistentepersonal SET nombre=?, apellidos=?, email=?, contraseña=?, telefono=?, disponibilidad=?, estadoaceptado=?, activo=?, zona=?, provincia=?, preferencias=?, puntuacion=?, consentimientorgbd=? WHERE idasistente=?",
            asistente.getNombre(),
            asistente.getApellidos(),
            asistente.getEmail(),
            asistente.getContraseña(),
            asistente.getTelefono(),
            asistente.getDisponibilidad(),
            asistente.isEstadoAceptado(),
            asistente.isActivo(),
            asistente.getZona(),
            asistente.getProvincia(),       
            asistente.getPreferencias(),
            asistente.getPuntuacion(),
            asistente.isConsentimientoRGBD(),
            asistente.getIdAsistente()
        );
    }

    public void deleteAsistentePersonal(int idAsistente) {
        jdbcTemplate.update("DELETE FROM asistentepersonal WHERE idasistente=?", idAsistente);
    }

    public AsistentePersonal getAsistentePersonal(int idAsistente) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM asistentepersonal WHERE idasistente=?",
                    new AsistentePersonalRowMapper(),
                    idAsistente
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AsistentePersonal> getAsistentesPersonales() {
        return jdbcTemplate.query(
                "SELECT * FROM asistentepersonal WHERE estadoaceptado = true",
                new AsistentePersonalRowMapper()
        );
    }

    public List<AsistentePersonal> buscarCompatibles(APRequest request) {
        return jdbcTemplate.query(
                "SELECT * FROM asistentepersonal",
                new AsistentePersonalRowMapper()
        );
    }

    public AsistentePersonal getAsistentePersonalByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM asistentepersonal WHERE email=?",
                    new AsistentePersonalRowMapper(),
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean existeEmail(String email, int idAsistente) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM asistentepersonal WHERE email = ? AND idasistente != ?",
                Integer.class,
                email,
                idAsistente
        );
        return count != null && count > 0;
    }

    public List<AsistentePersonal> getAsistentesPersonalesPendientes() {
        return jdbcTemplate.query(
                "SELECT * FROM asistentepersonal WHERE estadoaceptado = false",
                new AsistentePersonalRowMapper()
        );
    }

    public int countAsistentesPendientes() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM asistentepersonal WHERE estadoaceptado = false",
            Integer.class
        );
        return (count != null) ? count : 0;
    }

    public List<AsistentePersonal> getAsistentesPaginados(String buscar, int limit, int offset) {
        String texto = (buscar == null) ? "" : buscar.trim();
        String filtro = "%" + texto + "%";

        String sql =
            "SELECT * FROM asistentepersonal " +
            "WHERE estadoaceptado = true " +
            "AND (" +
            "   LOWER(nombre) LIKE LOWER(?) " +
            "   OR LOWER(apellidos) LIKE LOWER(?) " +
            "   OR LOWER(email) LIKE LOWER(?) " +
            "   OR LOWER(COALESCE(provincia, '')) LIKE LOWER(?) " +
            "   OR LOWER(COALESCE(zona, '')) LIKE LOWER(?)" +
            ") " +
            "ORDER BY nombre ASC " +
            "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(
                sql,
                new AsistentePersonalRowMapper(),
                filtro,
                filtro,
                filtro,
                filtro,
                filtro,
                limit,
                offset
        );
    }

    public int countAsistentes(String buscar) {
        String texto = (buscar == null) ? "" : buscar.trim();
        String filtro = "%" + texto + "%";

        String sql =
            "SELECT COUNT(*) FROM asistentepersonal " +
            "WHERE estadoaceptado = true " +
            "AND (" +
            "   LOWER(nombre) LIKE LOWER(?) " +
            "   OR LOWER(apellidos) LIKE LOWER(?) " +
            "   OR LOWER(email) LIKE LOWER(?) " +
            "   OR LOWER(COALESCE(provincia, '')) LIKE LOWER(?) " +
            "   OR LOWER(COALESCE(zona, '')) LIKE LOWER(?)" +
            ")";

        Integer total = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                filtro,
                filtro,
                filtro,
                filtro,
                filtro
        );

        return total == null ? 0 : total;
    }
    public List<AsistentePersonal> getAsistentesPaginados(String buscar, String provincia, String estado, int limit, int offset) {
    String texto = (buscar == null) ? "" : buscar.trim();
    String filtroBuscar = "%" + texto + "%";
    String prov = (provincia == null) ? "" : provincia.trim();

    String ordenSql = "ORDER BY nombre ASC";
    if ("pendiente".equalsIgnoreCase(estado)) {
        ordenSql = "ORDER BY estadoaceptado ASC, nombre ASC";
    } else if ("aceptado".equalsIgnoreCase(estado)) {
        ordenSql = "ORDER BY estadoaceptado DESC, nombre ASC";
    }

    String sql =
        "SELECT * FROM asistentepersonal " +
        "WHERE (" +
        "   LOWER(nombre) LIKE LOWER(?) " +
        "   OR LOWER(apellidos) LIKE LOWER(?) " +
        "   OR LOWER(email) LIKE LOWER(?) " +
        "   OR LOWER(COALESCE(zona, '')) LIKE LOWER(?)" +
        ") " +
        "AND (? = '' OR LOWER(COALESCE(provincia, '')) = LOWER(?)) " +
        ordenSql + " " +
        "LIMIT ? OFFSET ?";

    return jdbcTemplate.query(
            sql,
            new AsistentePersonalRowMapper(),
            filtroBuscar,
            filtroBuscar,
            filtroBuscar,
            filtroBuscar,
            prov,
            prov,
            limit,
            offset
    );
}

public int countAsistentes(String buscar, String provincia) {
    String texto = (buscar == null) ? "" : buscar.trim();
    String filtroBuscar = "%" + texto + "%";
    String prov = (provincia == null) ? "" : provincia.trim();

    String sql =
        "SELECT COUNT(*) FROM asistentepersonal " +
        "WHERE (" +
        "   LOWER(nombre) LIKE LOWER(?) " +
        "   OR LOWER(apellidos) LIKE LOWER(?) " +
        "   OR LOWER(email) LIKE LOWER(?) " +
        "   OR LOWER(COALESCE(zona, '')) LIKE LOWER(?)" +
        ") " +
        "AND (? = '' OR LOWER(COALESCE(provincia, '')) = LOWER(?))";

    Integer total = jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            filtroBuscar,
            filtroBuscar,
            filtroBuscar,
            filtroBuscar,
            prov,
            prov
    );

    return total == null ? 0 : total;
}
}