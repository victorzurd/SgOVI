package es.uji.ei1027.clubesportiu.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.MensajeChat;

@Repository
public class MensajeChatDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class MensajeChatRowMapper implements RowMapper<MensajeChat> {
        @Override
        public MensajeChat mapRow(ResultSet rs, int rowNum) throws SQLException {
            MensajeChat mensaje = new MensajeChat();
            mensaje.setIdMensaje(rs.getInt("idmensaje")); 
            mensaje.setIdChat(rs.getInt("idchat")); 
            mensaje.setContenido(rs.getString("contenido"));
            mensaje.setRemitente(rs.getString("remitente")); 
            mensaje.setFechaEnvio(rs.getTimestamp("fechaenvio")); 
            return mensaje;
        }
    }

    
    public List<MensajeChat> getMensajesPorChat(int idChat) {
        try {
            return jdbcTemplate.query(
                "SELECT * FROM mensajechat WHERE idchat = ? ORDER BY fechaenvio ASC",
                new MensajeChatRowMapper(),
                idChat
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}