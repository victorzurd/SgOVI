package es.uji.ei1027.clubesportiu.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.ChatSession;
import es.uji.ei1027.clubesportiu.model.MensajeChat;

@Repository
public class ChatDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void iniciarChat(int idUsuario, int idAsistente, int idRequest) {
        String sqlCheck = "SELECT COUNT(*) FROM chatsession WHERE idusuario=? AND idasistente=? AND idrequest=?";
        Integer count = jdbcTemplate.queryForObject(sqlCheck, Integer.class, idUsuario, idAsistente, idRequest);
        
        if (count == null || count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO chatsession (idusuario, idasistente, idrequest) VALUES (?, ?, ?)",
                    idUsuario, idAsistente, idRequest);
        }
    }

    public List<ChatSession> getChatsPorUsuario(int idUsuario) {
        return jdbcTemplate.query(
                "SELECT c.*, '' as nombreusuario, a.nombre as nombreasistente FROM chatsession c " +
                        "JOIN asistentepersonal a ON c.idasistente = a.idasistente WHERE c.idusuario = ?",
                new ChatSessionRowMapper(),
                idUsuario);
    }

    public List<ChatSession> getChatsPorAsistente(int idAsistente) {
        return jdbcTemplate.query(
                "SELECT c.*, u.nombre as nombreusuario, '' as nombreasistente FROM chatsession c " +
                        "JOIN usuarioovi u ON c.idusuario = u.idusuario WHERE c.idasistente = ?",
                new ChatSessionRowMapper(),
                idAsistente);
    }

    public List<ChatSession> getTodosLosChats() {
        return jdbcTemplate.query(
                "SELECT c.*, u.nombre as nombreusuario, a.nombre as nombreasistente FROM chatsession c " +
                        "JOIN usuarioovi u ON c.idusuario = u.idusuario " +
                        "JOIN asistentepersonal a ON c.idasistente = a.idasistente",
                new ChatSessionRowMapper());
    }

    public ChatSession getChat(int idChat) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT c.*, u.nombre as nombreusuario, a.nombre as nombreasistente FROM chatsession c " +
                            "JOIN usuarioovi u ON c.idusuario = u.idusuario " +
                            "JOIN asistentepersonal a ON c.idasistente = a.idasistente WHERE c.idchat = ?",
                    new ChatSessionRowMapper(),
                    idChat);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<MensajeChat> getMensajesDelChat(int idChat) {
        return jdbcTemplate.query(
                "SELECT * FROM mensajechat WHERE idchat = ? ORDER BY fechaenvio ASC",
                new MensajeChatRowMapper(),
                idChat);
    }

    public void guardarMensaje(int idChat, String remitente, String contenido) {
        jdbcTemplate.update(
                "INSERT INTO mensajechat (idchat, remitente, contenido) VALUES (?, ?, ?)",
                idChat, remitente, contenido);
    }


    public List<MensajeChat> getMensajesPorChat(int idChat) {
        try {
            return jdbcTemplate.query(
                "SELECT * FROM mensajechat WHERE idchat = ? ORDER BY fechaenvio ASC",
                new MensajeChatRowMapper(), 
                idChat
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }
}