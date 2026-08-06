package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.ChatSession;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatSessionRowMapper implements RowMapper<ChatSession> {
    @Override
    public ChatSession mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatSession chatSession = new ChatSession();
        chatSession.setIdChat(rs.getInt("idchat"));
        chatSession.setIdUsuario(rs.getInt("idusuario"));
        chatSession.setIdAsistente(rs.getInt("idasistente"));
        chatSession.setIdRequest(rs.getInt("idrequest"));
        chatSession.setFechaCreacion(rs.getTimestamp("fechacreacion"));
        chatSession.setEstado(rs.getString("estado"));
        chatSession.setNombreUsuario(rs.getString("nombreusuario"));
        chatSession.setNombreAsistente(rs.getString("nombreasistente"));
        return chatSession;
    }
}