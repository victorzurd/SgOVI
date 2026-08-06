package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.MensajeChat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MensajeChatRowMapper implements RowMapper<MensajeChat> {
    @Override
    public MensajeChat mapRow(ResultSet rs, int rowNum) throws SQLException {
        MensajeChat mensajeChat = new MensajeChat();
        mensajeChat.setIdMensaje(rs.getInt("idmensaje"));
        mensajeChat.setIdChat(rs.getInt("idchat"));
        mensajeChat.setRemitente(rs.getString("remitente"));
        mensajeChat.setContenido(rs.getString("contenido"));
        mensajeChat.setFechaEnvio(rs.getTimestamp("fechaenvio"));
        return mensajeChat;
    }
}