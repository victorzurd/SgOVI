package es.uji.ei1027.clubesportiu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/") 
    public String mostrarInicio() {
        return "Inicio/inicio"; 
    }

    @GetMapping("/Inicio/logins")
    public String mostrarLogins() {
        return "Inicio/logins"; 
    }
}