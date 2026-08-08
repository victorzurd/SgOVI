package es.uji.ei1027.clubesportiu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.TecnicoOVIDao;
import es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UserDetails;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/TecnicoOVI")
public class TecnicoOVIController {

    @Autowired
    private UsuarioOVIDao usuarioOVIDao;
    @Autowired
    private TecnicoOVIDao tecnicoOVIDao;
    @Autowired
    private AsistentePersonalDao asistentePersonalDao;
    @Autowired
    private APRequestDao apRequestDao;

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao asistentePersonalDao) {
        this.asistentePersonalDao = asistentePersonalDao;   
    }

    @Autowired
    public void setTecnicoOVIDao(TecnicoOVIDao tecnicoOVIDao) {
        this.tecnicoOVIDao = tecnicoOVIDao;
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "TecnicoOVI/login"; 
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails userDetails, 
                             BindingResult bindingResult, HttpSession session, Model model) {
        
        if (bindingResult.hasErrors()) {
            return "TecnicoOVI/login";
        }

        TecnicoOVI tecnico = tecnicoOVIDao.loadUserByUsername(userDetails.getUsuario());

        if (tecnico == null || !"admin123".equals(tecnico.getPassword())) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "TecnicoOVI/login";
        }

        session.setAttribute("tecnicoLogueado", tecnico);
        return "redirect:/TecnicoOVI/dashboard";
    }

    @RequestMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        int nuevosUsuarios = usuarioOVIDao.countUsuariosPendientes();
        int nuevosAsistentes = asistentePersonalDao.countAsistentesPendientes();
        int peticionesPendientes = apRequestDao.countPeticionesPendientesOEnRevision();

        // Enviar variables al modelo de Thymeleaf
        model.addAttribute("notifUsuarios", nuevosUsuarios);
        model.addAttribute("notifAsistentes", nuevosAsistentes);
        model.addAttribute("notifPeticiones", peticionesPendientes);

        model.addAttribute("tecnico", tecnico);

        return "TecnicoOVI/dashboard";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}