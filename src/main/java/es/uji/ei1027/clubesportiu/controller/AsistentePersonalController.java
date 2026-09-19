package es.uji.ei1027.clubesportiu.controller;

import java.util.Arrays;
import java.util.List;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Contrato;
import es.uji.ei1027.clubesportiu.model.Provincia;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UserDetails;
import es.uji.ei1027.clubesportiu.util.Paginacion;
import es.uji.ei1027.clubesportiu.validator.AsistentePersonalValidator;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AsistentePersonal")
public class AsistentePersonalController {

    private AsistentePersonalDao asistentePersonalDao;
    private es.uji.ei1027.clubesportiu.dao.APRequestDao apRequestDao;
    private es.uji.ei1027.clubesportiu.dao.ContratoDao contratoDao; // Corrección de nomenclatura

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao dao) {
        this.asistentePersonalDao = dao;
    }

    @Autowired
    public void setAPRequestDao(es.uji.ei1027.clubesportiu.dao.APRequestDao dao) {
        this.apRequestDao = dao;
    }

    @Autowired
    public void setContratoDao(es.uji.ei1027.clubesportiu.dao.ContratoDao dao) {
        this.contratoDao = dao; // Corrección
    }

    @ModelAttribute("provincias")
    public Provincia[] getProvincias() {
        return Provincia.values();
    }

    @ModelAttribute("opcionesHorario")
    public List<String> getOpcionesHorario() {
        return Arrays.asList(
            "Mañanas (08:00 - 14:00)",
            "Tardes (14:00 - 20:00)",
            "Noches (20:00 - 08:00)",
            "Jornada Completa / Indiferente",
            "Fines de Semana"
        );
    }

   @RequestMapping("/list")
    public String list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "buscar", required = false, defaultValue = "") String buscar,
            @RequestParam(name = "provincia", required = false, defaultValue = "") String provincia,
            @RequestParam(name = "estado", required = false, defaultValue = "") String estado,
            Model model) {

        String queryBusqueda = (buscar != null) ? buscar.trim() : "";
        String queryProvincia = (provincia != null) ? provincia.trim() : "";
        String queryEstado = (estado != null) ? estado.trim() : "";

        Paginacion paginacion = new Paginacion();
        paginacion.setPage(page);
        paginacion.setBuscar(queryBusqueda);

        int total = asistentePersonalDao.countAsistentes(queryBusqueda, queryProvincia);
        int totalPages = (int) Math.ceil((double) total / paginacion.getSize());

        List<AsistentePersonal> asistentes = asistentePersonalDao.getAsistentesPaginados(
                queryBusqueda,
                queryProvincia,
                queryEstado,
                paginacion.getSize(),
                paginacion.getOffset());

        model.addAttribute("asistentes", asistentes);
        model.addAttribute("page", page);
        model.addAttribute("buscar", queryBusqueda);
        model.addAttribute("provinciaSeleccionada", queryProvincia);
        model.addAttribute("estadoSeleccionado", queryEstado);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("urlBase", "/AsistentePersonal/list");
        model.addAttribute("numSolicitudes", asistentePersonalDao.countAsistentesPendientes());

        return "AsistentePersonal/list";
    }

    @RequestMapping("/list/pendientes")
    public String listPendientes(Model model) {
        model.addAttribute("asistentes", asistentePersonalDao.getAsistentesPersonalesPendientes());
        return "AsistentePersonal/TecnicoSolicitudes";
    }

    @RequestMapping("/main")
    public String main(HttpSession session, Model model) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/";
        }
        model.addAttribute("asistente", asistente);
        model.addAttribute("nombreUsuario", asistente.getNombre() + " " + asistente.getApellidos());
        return "AsistentePersonal/main";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("asistente", new AsistentePersonal());
        return "AsistentePersonal/register"; 
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                                BindingResult result, HttpSession session) {
        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);
        if (result.hasErrors()) {
            return "AsistentePersonal/register"; 
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String passEncriptada = passwordEncryptor.encryptPassword(asistente.getContraseña());
        asistente.setContraseña(passEncriptada);
        asistente.setEstadoAceptado(false); 

        asistentePersonalDao.addAsistentePersonal(asistente);
        session.setAttribute("asistenteLogueado", asistente); 
        return "redirect:/AsistentePersonal/esperaValidacion"; 
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model Model) {
        Model.addAttribute("asistente", new UserDetails());
        return "AsistentePersonal/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(@ModelAttribute("asistente") UserDetails usuario,
                              BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "AsistentePersonal/login";
        }
        AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(usuario.getUsuario());
        if (asistenteBD == null) {
            result.rejectValue("email", "bad-credentials", "El correo electrónico o la contraseña son incorrectos.");
            model.addAttribute("error", "Credenciales incorrectas");
            return "AsistentePersonal/login";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        if (!passwordEncryptor.checkPassword(usuario.getPassword(), asistenteBD.getContraseña())) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "AsistentePersonal/login";
        }

        session.removeAttribute("error");
        session.setAttribute("asistenteLogueado", asistenteBD);
        return "redirect:/AsistentePersonal/esperaValidacion";
    }

    @RequestMapping("/perfil")
    public String perfil(Model model, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login"; 
        }
        AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(asistente.getEmail());
        model.addAttribute("asistente", asistenteBD);
        return "AsistentePersonal/perfil"; 
    }

    @RequestMapping("/update")
    public String editForm(Model model, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login"; 
        }
        model.addAttribute("asistente", asistente);
        return "AsistentePersonal/update"; 
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String editSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                             BindingResult result) {
        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);
        if (result.hasErrors()) {
            return "AsistentePersonal/update"; 
        }

        AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(asistente.getEmail());
        if (asistenteBD != null && !(asistenteBD.getIdAsistente() == asistente.getIdAsistente())) {
            result.rejectValue("email", "duplicate", "El email ya está registrado por otro asistente");
            return "AsistentePersonal/update";
        }

        asistente.setContraseña(asistenteBD.getContraseña()); 
        asistentePersonalDao.updateAsistentePersonal(asistente);
        return "redirect:main";
    }

    @RequestMapping("/delete/{idAsistente}")
    public String delete(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/";
    }

    @RequestMapping("/aceptar/{idAsistente}")
    public String aceptar(@PathVariable int idAsistente, HttpSession session, Model model) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            session.invalidate();
            return "redirect:/";
        }
        AsistentePersonal asistente = asistentePersonalDao.getAsistentePersonal(idAsistente);
        if (asistente != null) {
            asistente.setEstadoAceptado(true);
            asistentePersonalDao.updateAsistentePersonal(asistente);
            model.addAttribute("correoAsistente", asistente.getEmail());
            return "AsistentePersonal/correo";
        }
        return "redirect:/AsistentePersonal/list";
    }

    @RequestMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping("/rechazar/{idAsistente}")
    public String rechazar(@PathVariable int idAsistente, HttpSession session, Model model) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            session.invalidate();
            return "redirect:/";
        }
        AsistentePersonal asistente = asistentePersonalDao.getAsistentePersonal(idAsistente);
        if (asistente != null) {
            asistentePersonalDao.deleteAsistentePersonal(idAsistente);
            model.addAttribute("correoAsistente", asistente.getEmail());
            return "AsistentePersonal/correoDenegado";
        }
        return "redirect:/AsistentePersonal/list/pendientes";
    }

    @RequestMapping("/eliminar/{idAsistente}")
    public String eliminar(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/AsistentePersonal/list";
    }

    @RequestMapping(value = "/esperaValidacion", method = RequestMethod.GET)
    public String esperaValidacion(HttpSession session, Model model) {
        AsistentePersonal asistenteSesion = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistenteSesion == null) {
            return "redirect:/AsistentePersonal/login";
        }
        
        AsistentePersonal asistenteReal = asistentePersonalDao.getAsistentePersonalByEmail(asistenteSesion.getEmail());
        if (asistenteReal != null && asistenteReal.isEstadoAceptado()) {
            session.setAttribute("asistenteLogueado", asistenteReal);
            return "redirect:/AsistentePersonal/main"; 
        }
        return "AsistentePersonal/esperaValidacion"; 
    }

    @RequestMapping(value = "/solicitudes", method = RequestMethod.GET)
    public String misSolicitudes(HttpSession session, Model model) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }
        List<APRequest> solicitudes = apRequestDao.getAPRequestsByAsistente(asistente.getIdAsistente());
        model.addAttribute("solicitudesAsistente", solicitudes);
        return "AsistentePersonal/solicitudes";
    }

    @RequestMapping(value = "/solicitudes/aceptar/{id}", method = RequestMethod.GET)
    public String aceptarSolicitud(@PathVariable("id") int idRequest, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }
        APRequest request = apRequestDao.getAPRequest(idRequest);
        if (request != null) {
            request.setEstado(es.uji.ei1027.clubesportiu.model.Estado.aprobado); 
            apRequestDao.updateEstadoAPRequest(request);
        }
        return "redirect:/AsistentePersonal/solicitudes";
    }

    @RequestMapping(value = "/solicitudes/rechazar/{id}", method = RequestMethod.GET)
    public String rechazarSolicitud(@PathVariable("id") int idRequest, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }
        APRequest request = apRequestDao.getAPRequest(idRequest);
        if (request != null) {
            request.setEstado(es.uji.ei1027.clubesportiu.model.Estado.rechazada); 
            apRequestDao.updateEstadoAPRequest(request);
        }
        return "redirect:/AsistentePersonal/solicitudes";
    }

    @RequestMapping ("/contratos")
    public String verContratosPaginados(HttpSession session, Model model) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }
        List<Contrato> contratos = contratoDao.getContratosByAsistente(asistente.getIdAsistente()); // Corrección
        model.addAttribute("contratosAsistente", contratos);
        return "AsistentePersonal/contratos";
    }
}