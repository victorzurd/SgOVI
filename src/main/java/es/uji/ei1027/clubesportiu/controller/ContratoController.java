package es.uji.ei1027.clubesportiu.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.ContratoDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Contrato;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private ContratoDao contratoDao;

    @Autowired
    private APRequestDao apRequestDao;

    @Autowired
    private AsistentePersonalDao asistentePersonalDao;

    @Autowired
    private es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao usuarioOVIDao;

    @Autowired 
    private es.uji.ei1027.clubesportiu.dao.SeleccionDao seleccionDao;

    @GetMapping("/crear/{idRequest}/{idSeleccion}/{idUsuario}")
    public String vistaCrear(@PathVariable("idRequest") int idRequest,
                             @PathVariable("idSeleccion") int idSeleccion,
                             @PathVariable("idUsuario") int idUsuario,
                             Model model, HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (session.getAttribute("tecnicoLogueado") == null) {
            return "redirect:/TecnicoOVI/login";
        }

        // Validación: Evitar duplicar contratos
        if (contratoDao.existeContrato(idRequest)) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un contrato para esta solicitud.");
            return "redirect:/APRequest/gestion/" + idRequest;
        }
        
        // Prevención de NullPointerException
        var seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null) {
            redirectAttributes.addFlashAttribute("error", "La selección especificada no existe.");
            return "redirect:/APRequest/gestion/" + idRequest;
        }
        
        int idAsistente = seleccion.getIdAsistente();
        APRequest request = apRequestDao.getAPRequest(idRequest);
        AsistentePersonal asistente = asistentePersonalDao.getAsistentePersonal(idAsistente);
        UsuarioOVI usuario = usuarioOVIDao.getUsuarioOVI(idUsuario);

        String plantilla = "<p><strong>CONTRATO DE PRESTACIÓN DE SERVICIOS DE ASISTENCIA PERSONAL</strong></p>" +
                "<p>En Castellón, se acuerda la prestación de servicios entre las partes indicadas bajo la supervisión de SgOVI.</p>";

        model.addAttribute("request", request);
        model.addAttribute("asistente", asistente);
        model.addAttribute("usuario", usuario);
        model.addAttribute("plantillaBase", plantilla);

        return "contrato/crear";
    }

    @PostMapping("/crear")
    public String guardarContrato(@RequestParam("idRequest") int idRequest,
                                  @RequestParam("idAsistente") int idAsistente,
                                  @RequestParam("idUsuario") int idUsuario,
                                  @RequestParam("contenidoHtml") String contenidoHtml,
                                  @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
                                  @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("tecnicoLogueado") == null) {
            return "redirect:/TecnicoOVI/login";
        }

        if (contratoDao.existeContrato(idRequest)) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un contrato para esta solicitud.");
            return "redirect:/APRequest/gestion/" + idRequest;
        }

        Contrato c = new Contrato();
        c.setIdRequest(idRequest);
        c.setIdAsistente(idAsistente);
        c.setIdUsuario(idUsuario);
        c.setContenidoHtml(contenidoHtml);
        c.setFechaInicio(fechaInicio);
        c.setFechaFin(fechaFin);

        contratoDao.crearContrato(c);
        return "redirect:/APRequest/gestion/" + idRequest;
    }

    @GetMapping("/firmar-usuario/{idContrato}")
    public String vistaFirmarUsuario(@PathVariable("idContrato") int idContrato, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuarioOVI usuarioLogueado = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/UsuarioOVI/login";
        }
        
        Contrato contrato = contratoDao.getContrato(idContrato);
        if (contrato == null) return "redirect:/APRequest/list";
        
        // Mitigación IDOR: Verificar que el contrato pertenezca al usuario logueado
        if (contrato.getIdUsuario() != usuarioLogueado.getIdUsuario()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver o firmar este contrato.");
            return "redirect:/UsuarioOVI/dashboard";
        }
        
        model.addAttribute("contrato", contrato);
        return "contrato/firmar-usuario";
    }

    @PostMapping("/firmar-usuario")
    public String procesarFirmaUsuario(@RequestParam("idContrato") int idContrato,
                                       @RequestParam("firmaBase64") String firmaBase64,
                                       HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuarioOVI usuarioLogueado = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/UsuarioOVI/login";
        }

        Contrato contrato = contratoDao.getContrato(idContrato);
        // Mitigación IDOR
        if (contrato == null || contrato.getIdUsuario() != usuarioLogueado.getIdUsuario()) {
            redirectAttributes.addFlashAttribute("error", "Acceso denegado.");
            return "redirect:/UsuarioOVI/dashboard";
        }

        String ip = request.getRemoteAddr();
        contratoDao.registrarFirmaUsuario(idContrato, firmaBase64, ip);
        return "redirect:/contrato/firmar-usuario/" + idContrato;
    }

    @GetMapping("/firmar-asistente/{idContrato}")
    public String vistaFirmarAsistente(@PathVariable("idContrato") int idContrato, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        AsistentePersonal asistenteLogueado = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistenteLogueado == null) {
            return "redirect:/AsistentePersonal/login";
        }

        Contrato contrato = contratoDao.getContrato(idContrato);
        if (contrato == null) return "redirect:/APRequest/list";

        // Mitigación IDOR: Verificar que el contrato corresponda al asistente
        if (contrato.getIdAsistente() != asistenteLogueado.getIdAsistente()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver o firmar este contrato.");
            return "redirect:/AsistentePersonal/main";
        }

        model.addAttribute("contrato", contrato);
        return "contrato/firmar-asistente";
    }

    @PostMapping("/firmar-asistente")
    public String procesarFirmaAsistente(@RequestParam("idContrato") int idContrato,
                                         @RequestParam("firmaBase64") String firmaBase64,
                                         HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Reparada la vulnerabilidad de sesión nula
        AsistentePersonal asistenteLogueado = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistenteLogueado == null) {
            return "redirect:/AsistentePersonal/login";
        }

        Contrato contrato = contratoDao.getContrato(idContrato);
        // Mitigación IDOR
        if (contrato == null || contrato.getIdAsistente() != asistenteLogueado.getIdAsistente()) {
            redirectAttributes.addFlashAttribute("error", "Acceso denegado.");
            return "redirect:/AsistentePersonal/main";
        }

        String ip = request.getRemoteAddr();
        contratoDao.registrarFirmaAsistente(idContrato, firmaBase64, ip);
        return "redirect:/contrato/firmar-asistente/" + idContrato;
    }
}