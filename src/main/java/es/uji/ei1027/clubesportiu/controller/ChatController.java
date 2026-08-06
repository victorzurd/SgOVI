package es.uji.ei1027.clubesportiu.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.ChatDao;
import es.uji.ei1027.clubesportiu.dao.MensajeChatDao;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.ChatSession;
import es.uji.ei1027.clubesportiu.model.MensajeChat;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private ChatDao chatDao;
    private APRequestDao apRequestDao; 

    @Autowired
    public void setChatDao(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    @Autowired
    public void setApRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    
    
    

    
    @GetMapping("/usuario/sala/{idChat}")
    public String salaUsuario(@PathVariable int idChat, Model model, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/UsuarioOVI/login";

        List<ChatSession> chats = chatDao.getChatsPorUsuario(usuario.getIdUsuario());
        model.addAttribute("chats", chats);

        if (idChat > 0) {
            ChatSession chatActual = chatDao.getChat(idChat);
            
            if (chatActual != null && chatActual.getIdUsuario() == usuario.getIdUsuario()) {
                model.addAttribute("chatActual", chatActual);
                model.addAttribute("mensajes", chatDao.getMensajesDelChat(idChat));
            } else {
                return "redirect:/chat/usuario/sala/0";
            }
        } else {
            
            model.addAttribute("chatActual", new ChatSession());
            model.addAttribute("mensajes", new ArrayList<MensajeChat>());
        }

        return "UsuarioOVI/sala";
    }

    @PostMapping("/usuario/enviar/{idChat}")
    public String usuarioEnviarMensaje(@PathVariable int idChat, @RequestParam String contenido, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        if (idChat > 0 && contenido != null && !contenido.trim().isEmpty()) {
            chatDao.guardarMensaje(idChat, "USUARIO", contenido.trim());
        }

        return "redirect:/chat/usuario/sala/" + idChat;
    }


    
    
    

    
    @GetMapping("/asistente/sala/{idChat}")
    public String salaAsistente(@PathVariable int idChat, Model model, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) return "redirect:/AsistentePersonal/login";

        List<ChatSession> chats = chatDao.getChatsPorAsistente(asistente.getIdAsistente());
        model.addAttribute("chats", chats);

        if (idChat > 0) {
            ChatSession chatActual = chatDao.getChat(idChat);
            if (chatActual != null && chatActual.getIdAsistente() == asistente.getIdAsistente()) {
                model.addAttribute("chatActual", chatActual);
                model.addAttribute("mensajes", chatDao.getMensajesDelChat(idChat));
            } else {
                return "redirect:/chat/asistente/sala/0";
            }
        } else {
            model.addAttribute("chatActual", new ChatSession());
            model.addAttribute("mensajes", new ArrayList<MensajeChat>());
        }

        return "AsistentePersonal/sala";
    }

@PostMapping("/asistente/enviar/{idChat}")
public String asistenteEnviarMensaje(@PathVariable("idChat") int idChat,
                                     @RequestParam("contenido") String contenido,
                                     @RequestParam(value = "idRequest", required = false) Integer idRequest,
                                     HttpSession session) {
    
    
    AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
    if (asistente == null) {
        return "redirect:/AsistentePersonal/login";
    }

    int idAsistenteReal = asistente.getIdAsistente();

    
    if (idChat == 0 && idRequest != null) {
        es.uji.ei1027.clubesportiu.model.APRequest sol = apRequestDao.getAPRequest(idRequest);
        if (sol != null) {
            chatDao.iniciarChat(sol.getIdUsuario(), idAsistenteReal, idRequest);
            
            
            List<ChatSession> todos = chatDao.getTodosLosChats();
            for (ChatSession cs : todos) {
                if (cs.getIdRequest() == idRequest && cs.getIdAsistente() == idAsistenteReal) {
                    idChat = cs.getIdChat();
                    break;
                }
            }
        }
    }

    
    if (idChat > 0 && contenido != null && !contenido.trim().isEmpty()) {
        chatDao.guardarMensaje(idChat, "ASISTENTE", contenido.trim());
    }

    
    
    return "redirect:/chat/asistente/sala/" + idChat;
}

    @GetMapping("/iniciar/{idAsistente}/{idReq}")
    public String iniciarChatDesdeCandidato(@PathVariable("idAsistente") int idAsistente, 
                                        @PathVariable("idReq") int idRequest, 
                                        HttpSession session) {
        
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/UsuarioOVI/login";

        
        chatDao.iniciarChat(usuario.getIdUsuario(), idAsistente, idRequest);

        
        List<ChatSession> chats = chatDao.getChatsPorUsuario(usuario.getIdUsuario());
        int idChatDestino = 0;
        for (ChatSession cs : chats) {
            if (cs.getIdRequest() == idRequest && cs.getIdAsistente() == idAsistente) {
                idChatDestino = cs.getIdChat();
                break;
            }
        }

        
        return "redirect:/chat/usuario/sala/" + idChatDestino;
    }

    
    
    

    @GetMapping("/tecnico/lista")
    public String listaChatsTecnico(Model model, HttpSession session) {
        
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        
        List<ChatSession> todosLosChats = chatDao.getTodosLosChats();
        model.addAttribute("chats", todosLosChats);
        
        
        model.addAttribute("rol", "TECNICO");

        return "TecnicoOVI/listaChats"; 
    }


    @GetMapping("/tecnico/ver/{idChat}") 
    public String verDetalleChatTecnico(@PathVariable("idChat") int idChat, Model model, HttpSession session) {
        
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        
        List<MensajeChat> mensajes = chatDao.getMensajesPorChat(idChat);
        if (mensajes == null) {
            mensajes = new ArrayList<>();
        }

        
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("idChat", idChat);
        model.addAttribute("mensajes", mensajes);

        
        return "APRequest/verChatTecnico"; 
    }
}