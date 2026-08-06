package es.uji.ei1027.clubesportiu.controller;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.TecnicoOVIDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UserDetails;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/TecnicoOVI")
public class TecnicoOVIController {

    private TecnicoOVIDao tecnicoOVIDao;
    private AsistentePersonalDao asistentePersonalDao;
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

        if (tecnico == null || !userDetails.getPassword().equals(tecnico.getPassword())) {
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

        int numSolicitudes = asistentePersonalDao.countAsistentesPendientes();
        model.addAttribute("numSolicitudes", numSolicitudes);

        model.addAttribute("tecnico", tecnico);

        return "TecnicoOVI/dashboard";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}