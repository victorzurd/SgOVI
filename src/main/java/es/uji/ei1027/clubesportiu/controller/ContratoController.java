package es.uji.ei1027.clubesportiu.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao;
import es.uji.ei1027.clubesportiu.dao.SeleccionDao;
import es.uji.ei1027.clubesportiu.dao.RegistroContratoDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.RegistroContrato;
import es.uji.ei1027.clubesportiu.model.Seleccion;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private APRequestDao apRequestDao;

    @Autowired
    private UsuarioOVIDao usuarioDao;

    @Autowired
    private SeleccionDao seleccionDao;

    @Autowired
    private AsistentePersonalDao asistentePersonalDao;

    @Autowired
    private RegistroContratoDao registroContratoDao;

    @PostMapping("/generar/{idRequest}")
    public String generarContrato(@PathVariable int idRequest) {

        System.out.println("He entrado al controlador");

        if (registroContratoDao.existeContrato(idRequest)) {
            System.out.println("Ya existe contrato");
            return "redirect:/APRequest/gestion/" + idRequest;
        }

        System.out.println("Voy a generar contrato");

        APRequest request = apRequestDao.getAPRequest(idRequest);

        if (request == null) {
            return "redirect:/APRequest/list";
        }

        if (request.getIdSeleccion() == null) {
            return "redirect:/APRequest/gestion/" + idRequest;
        }

        UsuarioOVI usuario = usuarioDao.getUsuarioOVI(request.getIdUsuario());

        Seleccion seleccion = seleccionDao.getSeleccion(request.getIdSeleccion());

        AsistentePersonal asistente =
                asistentePersonalDao.getAsistentePersonal(seleccion.getIdAsistente());

        RegistroContrato contrato = new RegistroContrato();

        String nombrePdf = "contrato_" + idRequest + ".pdf";

        contrato.setFechaInicio(LocalDate.now());
        contrato.setFechaFin(null);
        contrato.setEstado("con_contrato");
        contrato.setDocumentoPdf(nombrePdf);

        contrato.setIdRequest(request.getIdRequest());
        contrato.setIdSeleccion(request.getIdSeleccion());

        registroContratoDao.addContrato(contrato);

        try {
            System.out.println("Entrando a generar PDF");

            String plantilla = "src/main/resources/static/contrato/ContratoRellenable.pdf";

            String destino = "src/main/resources/static/contratos/" + nombrePdf;

            File fichero = new File(plantilla);

            System.out.println(fichero.getAbsolutePath());
            System.out.println(fichero.exists());

            PDDocument documento = Loader.loadPDF(new File(plantilla));

            PDAcroForm formulario = documento.getDocumentCatalog().getAcroForm();

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            formulario.getField("Campo de fecha 1")
                    .setValue(LocalDate.now().format(formato));

            // Usuario
            formulario.getField("Cuadro de texto 1")
                    .setValue(usuario.getNombre() + " " + usuario.getApellidos());

            // Asistente
            formulario.getField("Cuadro de texto 4")
                    .setValue(asistente.getNombre() + " " + asistente.getApellidos());

            // Correos
            formulario.getField("Cuadro de texto 2")
                    .setValue(usuario.getEmail());

            formulario.getField("Cuadro de texto 5")
                    .setValue(asistente.getEmail());

            // Telefonos
            formulario.getField("Cuadro de texto 3")
                    .setValue(usuario.getTelefono());

            formulario.getField("Cuadro de texto 6")
                    .setValue(asistente.getTelefono());

            formulario.getField("Cuadro de texto 7")
                    .setValue(request.getZona());

            documento.save(destino);
            documento.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/APRequest/gestion/" + idRequest;
    }
}