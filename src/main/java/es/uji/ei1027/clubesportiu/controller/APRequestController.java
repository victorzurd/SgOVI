package es.uji.ei1027.clubesportiu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.CandidatoDao;
import es.uji.ei1027.clubesportiu.dao.MensajeChatDao;
import es.uji.ei1027.clubesportiu.dao.RegistroContratoDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.MensajeChat;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import es.uji.ei1027.clubesportiu.util.Paginacion;

@Controller
@RequestMapping("/APRequest")
public class APRequestController {

    private APRequestDao apRequestDao;
    private AsistentePersonalDao asistentePersonalDao;
    private CandidatoDao candidatoDao; 

    @Autowired
    private MensajeChatDao mensajeChatDao;

    @Autowired
    private RegistroContratoDao registroContratoDao;

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao asistentePersonalDao) {
        this.asistentePersonalDao = asistentePersonalDao;
    }

    @Autowired
    public void setCandidatoDao(CandidatoDao candidatoDao) {
        this.candidatoDao = candidatoDao;
    }

    @RequestMapping("/list")
    public String listAPRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String buscar,
            HttpSession session,
            Model model) {

        Paginacion paginacion = new Paginacion();
        paginacion.setPage(page);
        paginacion.setBuscar(buscar);



        List<APRequest> requests;
        String rol;
        int total;

        if (session.getAttribute("tecnicoLogueado") != null) {

            rol = "TECNICO";

            total = apRequestDao.countAPRequests(buscar);

            requests = apRequestDao.getAPRequestsPaginados(
                    buscar,
                    paginacion.getSize(),
                    paginacion.getOffset());

        } else if (session.getAttribute("usuarioLogueado") != null) {

            UsuarioOVI usuario =
                    (UsuarioOVI) session.getAttribute("usuarioLogueado");

            rol = "USUARIO";

            total = apRequestDao.countAPRequestsByUsuario(
                    usuario.getIdUsuario(),
                    buscar);

            requests = apRequestDao.getAPRequestsByUsuarioPaginados(
                    usuario.getIdUsuario(),
                    buscar,
                    paginacion.getSize(),
                    paginacion.getOffset());

        } else {
            return "redirect:/";
        }

        int totalPages = (int) Math.ceil((double) total / paginacion.getSize());

        Map<Integer, String> nombresUsuarios = new HashMap<>();

        if ("TECNICO".equals(rol)) {
            for (APRequest req : requests) {
                if (req.getIdUsuario() != 0) {
                    nombresUsuarios.put(
                            req.getIdUsuario(),
                            apRequestDao.getNombreUsuarioPorId(req.getIdUsuario()));
                }
            }
        }

        model.addAttribute("requests", requests);
        model.addAttribute("rol", rol);
        model.addAttribute("buscar", buscar);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("urlBase", "/APRequest/list");
        model.addAttribute("nombresUsuarios", nombresUsuarios);
        model.addAttribute("mostrarZona", false);

        return "APRequest/list";
    }

    @GetMapping("/add")
    public String addAPRequest(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/UsuarioOVI/login";
        }
        model.addAttribute("apRequest", new APRequest());
        return "APRequest/add";
    }

    @PostMapping("/add")
    public String addAPRequestSubmit(@ModelAttribute("apRequest") APRequest apRequest, 
                                     BindingResult bindingResult, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }
        if (bindingResult.hasErrors()) {
            return "APRequest/add";
        }
        apRequest.setIdUsuario(usuario.getIdUsuario());
        apRequest.setEstado(Estado.pendiente);
        apRequestDao.addAPRequest(apRequest);
        return "APRequest/aceptada";
    }

    @GetMapping("/delete/{id}")
    public String processDelete(@PathVariable int id) {
        apRequestDao.deleteAPRequest(id);
        return "redirect:/APRequest/list";
    }

    @GetMapping("/assign/{id}")
    public String asignarAsistentes(@PathVariable("id") int id, Model model, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        APRequest request = apRequestDao.getAPRequest(id);
        if (request == null) {
            return "redirect:/APRequest/list";
        }

        List<AsistentePersonal> todosAsistentes = asistentePersonalDao.getAsistentesPersonales();
        if (todosAsistentes == null) todosAsistentes = new ArrayList<>();

        List<Integer> idsAsignados = candidatoDao.getIdsCandidatosPorSolicitud(id);
        if (idsAsignados == null) idsAsignados = new ArrayList<>();

        List<AsistentePersonal> candidatos = candidatoDao.getAsistentesCandidatos(id);
        if (candidatos == null) candidatos = new ArrayList<>();

        boolean tieneAsistente = (request.getIdSeleccion() != null);

        model.addAttribute("request", request);
        model.addAttribute("asistentes", todosAsistentes);
        model.addAttribute("ids", idsAsignados);
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("tecnico", tecnico);
        
        model.addAttribute("tieneAsistente", tieneAsistente); 

        return "APRequest/assign";
    }

    @GetMapping("/candidatos/add/{idSolicitud}/{idAsistente}")
    public String addCandidato(@PathVariable int idSolicitud, @PathVariable int idAsistente, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        candidatoDao.addCandidato(idSolicitud, idAsistente);

        return "redirect:/APRequest/assign/" + idSolicitud;
    }

    @GetMapping("/candidatos/enviar/{idSolicitud}")
    public String enviarCandidatos(@PathVariable int idSolicitud, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        return "APRequest/propuestaEnviada";
    }

    @GetMapping("/candidatos/delete/{idSolicitud}/{idAsistente}")
    public String deleteCandidato(@PathVariable int idSolicitud, @PathVariable int idAsistente, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        candidatoDao.deleteCandidato(idSolicitud, idAsistente);

        return "redirect:/APRequest/assign/" + idSolicitud;
    }

    @GetMapping("/usuario/candidatos/{idSolicitud}")
    public String verCandidatosUsuario(@PathVariable int idSolicitud, Model model, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        APRequest solicitud = apRequestDao.getAPRequest(idSolicitud);
        boolean tieneAsistente = (solicitud != null && solicitud.getIdSeleccion() != null);

        List<AsistentePersonal> candidatos = candidatoDao.getAsistentesCandidatos(idSolicitud);

        model.addAttribute("tieneAsistente", tieneAsistente);
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);
        
        model.addAttribute("request", solicitud); 

        return "UsuarioOVI/candidatos";
    }

    @GetMapping("/candidatos/seleccionar/{idSolicitud}/{idAsistente}")
    public String seleccionarAsistente(
            @PathVariable("idSolicitud") int idSolicitud, 
            @PathVariable("idAsistente") int idAsistente, 
            HttpSession session) {
        
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        APRequest solicitud = apRequestDao.getAPRequest(idSolicitud);
        if (solicitud != null && solicitud.getIdSeleccion() != null) {
            return "redirect:/APRequest/candidatos/" + idSolicitud + "?error=ya_asignado";
        }

        apRequestDao.asignarAsistente(idSolicitud, idAsistente, usuario.getIdUsuario());

        return "redirect:/APRequest/list"; 
    }

    @GetMapping("/candidatos/{idSolicitud}")
    public String verListaCandidatos(@PathVariable("idSolicitud") int idSolicitud, Model model, HttpSession session) {
        
        List<AsistentePersonal> candidatos = candidatoDao.getAsistentesCandidatos(idSolicitud);
        if (candidatos == null) {
            candidatos = new ArrayList<>();
        }

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);
        
        APRequest request = apRequestDao.getAPRequest(idSolicitud);
        model.addAttribute("request", request);

        boolean tieneAsistente = (request != null && request.getIdSeleccion() != null);
        model.addAttribute("tieneAsistente", tieneAsistente);

        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico != null) {
            model.addAttribute("tecnico", tecnico);
            model.addAttribute("rol", "TECNICO");
            return "APRequest/candidatos"; 
        }

        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("rol", "USUARIO"); 
            return "UsuarioOVI/candidatos";
        }

        return "redirect:/TecnicoOVI/login";
    }

    @GetMapping("/chat/tecnico/ver/{idChat}")
    public String verDetalleChatTecnico(@PathVariable("idChat") int idChat, Model model, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        List<MensajeChat> mensajes = mensajeChatDao.getMensajesPorChat(idChat);

        model.addAttribute("tecnico", tecnico);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("idChat", idChat);

        return "APRequest/verChatTecnico"; 
    }

    @GetMapping("/update/{id}")
    public String updateAPRequest(@PathVariable("id") int id, Model model, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        APRequest request = apRequestDao.getAPRequest(id);
        if (request == null) {
            return "redirect:/APRequest/list";
        }

        model.addAttribute("apRequest", request);
        model.addAttribute("estados", Estado.values()); 
        
        return "APRequest/update";
    }

    @PostMapping("/update")
    public String processUpdateSubmit(@ModelAttribute("apRequest") APRequest apRequest, 
                                      BindingResult bindingResult, HttpSession session) {
                                          
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        if (bindingResult.hasErrors()) {
            return "APRequest/update";
        }
        
        apRequestDao.updateAPRequest(apRequest);
        
        return "redirect:/APRequest/gestion/" + apRequest.getIdRequest();
    }

    @GetMapping("/gestion/{id}")
    public String gestionarPeticion(@PathVariable("id") int id, Model model, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        APRequest request = apRequestDao.getAPRequest(id);
        if (request == null) {
            return "redirect:/APRequest/list";
        }
        
        String nombreUsuario = apRequestDao.getNombreUsuarioPorId(request.getIdUsuario());

        model.addAttribute("request", request);
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("existeContrato", registroContratoDao.existeContrato(id));
        
        return "APRequest/gestion"; 
    }
}