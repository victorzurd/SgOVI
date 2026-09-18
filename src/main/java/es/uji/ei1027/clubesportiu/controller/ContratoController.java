package es.uji.ei1027.clubesportiu.controller;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.ContratoDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Contrato;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private ContratoDao contratoDao;

    @Autowired
    private APRequestDao apRequestDao;

    @Autowired
    private AsistentePersonalDao asistentePersonalDao;

    // 1. Muestra formulario al técnico para crear contrato
    @GetMapping("/crear/{idRequest}/{idAsistente}")
    public String vistaCrear(@PathVariable("idRequest") int idRequest,
                             @PathVariable("idAsistente") int idAsistente,
                             Model model, HttpSession session) {

        if (session.getAttribute("tecnicoLogueado") == null) {
            return "redirect:/TecnicoOVI/login";
        }

        APRequest request = apRequestDao.getAPRequest(idRequest);
        AsistentePersonal asistente = asistentePersonalDao.getAsistentePersonal(idAsistente);

        String plantilla = "<p><strong>CONTRATO DE PRESTACIÓN DE SERVICIOS DE ASISTENCIA PERSONAL</strong></p>" +
                "<p>En Castellón, se acuerda la prestación de servicios entre las partes indicadas bajo la supervisión de SgOVI.</p>";

        model.addAttribute("request", request);
        model.addAttribute("asistente", asistente);
        model.addAttribute("plantillaBase", plantilla);

        return "contrato/crear";
    }

    // 2. Guarda el borrador generado por el técnico
    @PostMapping("/crear")
    public String guardarContrato(@RequestParam("idRequest") int idRequest,
                                  @RequestParam("idAsistente") int idAsistente,
                                  @RequestParam("contenidoHtml") String contenidoHtml) {

        Contrato c = new Contrato();
        c.setIdRequest(idRequest);
        c.setIdAsistente(idAsistente);
        c.setContenidoHtml(contenidoHtml);

        contratoDao.crearContrato(c);
        return "redirect:/APRequest/gestion/" + idRequest;
    }

    // 3. Vista y proceso de firma del Usuario
    @GetMapping("/firmar-usuario/{idContrato}")
    public String vistaFirmarUsuario(@PathVariable("idContrato") int idContrato, Model model) {
        Contrato contrato = contratoDao.getContrato(idContrato);
        model.addAttribute("contrato", contrato);
        return "contrato/firmar-usuario";
    }

    @PostMapping("/firmar-usuario")
    public String procesarFirmaUsuario(@RequestParam("idContrato") int idContrato,
                                       @RequestParam("firmaBase64") String firmaBase64,
                                       HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        contratoDao.registrarFirmaUsuario(idContrato, firmaBase64, ip);
        return "redirect:/contrato/firmar-usuario/" + idContrato;
    }

    // 4. Vista y proceso de firma del Asistente
    @GetMapping("/firmar-asistente/{idContrato}")
    public String vistaFirmarAsistente(@PathVariable("idContrato") int idContrato, Model model) {
        Contrato contrato = contratoDao.getContrato(idContrato);
        model.addAttribute("contrato", contrato);
        return "contrato/firmar-asistente";
    }

    @PostMapping("/firmar-asistente")
    public String procesarFirmaAsistente(@RequestParam("idContrato") int idContrato,
                                         @RequestParam("firmaBase64") String firmaBase64,
                                         HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        contratoDao.registrarFirmaAsistente(idContrato, firmaBase64, ip);
        return "redirect:/contrato/firmar-asistente/" + idContrato;
    }
}