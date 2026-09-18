package es.uji.ei1027.clubesportiu.controller;

import java.util.Arrays;
import java.util.List;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.uji.ei1027.clubesportiu.dao.ContratoDao;
import es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao;
import es.uji.ei1027.clubesportiu.model.Provincia;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UserDetails;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import es.uji.ei1027.clubesportiu.util.Paginacion;
import es.uji.ei1027.clubesportiu.validator.UsuarioOVIValidator;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/UsuarioOVI")
public class UsuarioOVIController {

    private UsuarioOVIDao usuarioOVIDao;
    private ContratoDao ContratoDao;

    @Autowired
    public void setUsuarioOVIDao(UsuarioOVIDao usuarioOVIDao) {
        this.usuarioOVIDao = usuarioOVIDao;
    }

    @Autowired
    public void setRegistroContratoDao(ContratoDao registroContratoDao) {
        this.ContratoDao = registroContratoDao;
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

        int total = usuarioOVIDao.countUsuarios(queryBusqueda, queryProvincia);
        int totalPages = (int) Math.ceil((double) total / paginacion.getSize());

        List<UsuarioOVI> usuarios = usuarioOVIDao.getUsuariosPaginados(
                queryBusqueda,
                queryProvincia,
                queryEstado,
                paginacion.getSize(),
                paginacion.getOffset());

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("page", page);
        model.addAttribute("buscar", queryBusqueda);
        model.addAttribute("provinciaSeleccionada", queryProvincia);
        model.addAttribute("estadoSeleccionado", queryEstado);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("urlBase", "/UsuarioOVI/list");

        return "UsuarioOVI/list";
    }
    @RequestMapping(value = "/register")
    public String addForm(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "UsuarioOVI/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("usuario") UsuarioOVI usuario, BindingResult bindingResult) {
        UsuarioOVIValidator validator = new UsuarioOVIValidator(usuarioOVIDao);
        
        validator.validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "UsuarioOVI/register";
        }
        
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String passEncriptada = passwordEncryptor.encryptPassword(usuario.getPassword());
    
        usuario.setPassword(passEncriptada);

        usuario.setEstadoAceptado(false);
        usuarioOVIDao.addUsuarioOVI(usuario);
        return "redirect:/UsuarioOVI/pending";
    }

    @GetMapping("/delete")
    public String confirmDeletePropioPerfil(HttpSession session, Model model) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }
        model.addAttribute("usuario", usuario);
        return "UsuarioOVI/delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        UsuarioOVI usuarioSesion = (UsuarioOVI) session.getAttribute("usuarioLogueado");

        usuarioOVIDao.deleteUsuarioOVI(id);

        if (usuarioSesion != null && usuarioSesion.getIdUsuario() == id) {
            session.invalidate();
            return "redirect:/";
        }

        return "redirect:/UsuarioOVI/list";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "UsuarioOVI/login"; 
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails userDetails, 
                             BindingResult bindingResult, HttpSession session, Model model) {
        
        if (bindingResult.hasErrors()) {
            return "UsuarioOVI/login";
        }

        UsuarioOVI usuario = usuarioOVIDao.loadUserByUsername(userDetails.getUsuario());

        if (usuario == null) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "/UsuarioOVI/login";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        if (!passwordEncryptor.checkPassword(userDetails.getPassword(), usuario.getPassword())) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "/UsuarioOVI/login";
        }

        if (!usuario.isEstadoAceptado()) {
            return "UsuarioOVI/pending";
        }

        session.removeAttribute("error");
        session.setAttribute("usuarioLogueado", usuario);
        return "redirect:/UsuarioOVI/dashboard";
    }

    @RequestMapping("/pending")
    public String pending() {
        return "UsuarioOVI/pending";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellidos());
        return "UsuarioOVI/dashboard";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        model.addAttribute("usuario", usuario);
        return "UsuarioOVI/perfil";
    }

    @GetMapping("/edit")
    public String editarPerfil(HttpSession session, Model model) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        model.addAttribute("usuario", usuario);
        return "UsuarioOVI/edit";
    }

    @PostMapping("/edit")
    public String guardarEdicion(@ModelAttribute("usuario") UsuarioOVI usuario,
                                 BindingResult bindingResult,
                                 HttpSession session) {

        UsuarioOVI usuarioSesion = (UsuarioOVI) session.getAttribute("usuarioLogueado");

        if (usuarioSesion == null) {
            return "redirect:/UsuarioOVI/login";
        }

        usuario.setIdUsuario(usuarioSesion.getIdUsuario());
        usuario.setEstadoAceptado(true);
        usuario.setConsentimientoRGBD(usuarioSesion.isConsentimientoRGBD());
        usuario.setPassword(usuarioSesion.getPassword());

        UsuarioOVIValidator validator = new UsuarioOVIValidator(usuarioOVIDao);
        validator.validate(usuario, bindingResult);

        if (bindingResult.hasErrors()) {
            return "UsuarioOVI/edit";
        }

        usuarioOVIDao.updateUsuarioOVI(usuario);
        session.setAttribute("usuarioLogueado", usuario);

        return "redirect:/UsuarioOVI/perfil";
    }

    @GetMapping("/ver-candidatos")
    public String verCandidatos(HttpSession session, Model model) {

        UsuarioOVI usuario =
            (UsuarioOVI) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        model.addAttribute("usuarios", usuarioOVIDao.getUsuariosOVI());

        return "UsuarioOVI/ver-candidatos";
    }

    @RequestMapping(value="/aceptar/{id}", method = RequestMethod.GET)
    public String aceptarUsuario(@PathVariable int id, HttpSession session, Model model) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            session.invalidate();
            return "redirect:/";
        }

        UsuarioOVI usuario = usuarioOVIDao.getUsuarioOVI(id);
        
        if (usuario != null) {
            usuario.setEstadoAceptado(true);
            
            usuarioOVIDao.updateUsuarioOVI(usuario);

            model.addAttribute("correoUsuario", usuario.getEmail());

            return "UsuarioOVI/correo";
        }

        return "redirect:/UsuarioOVI/list";
    }

    @RequestMapping("/rechazar/{idUsuario}")
    public String rechazar(@PathVariable int idUsuario, HttpSession session, Model model) {

        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            session.invalidate();
            return "redirect:/";
        }

        UsuarioOVI usuario = usuarioOVIDao.getUsuarioOVI(idUsuario);

        if (usuario != null) {
            usuarioOVIDao.deleteUsuarioOVI(idUsuario);
            model.addAttribute("correoUsuario", usuario.getEmail());
            return "UsuarioOVI/correoDenegado";
        }

        return "redirect:/UsuarioOVI/list";
    }
}