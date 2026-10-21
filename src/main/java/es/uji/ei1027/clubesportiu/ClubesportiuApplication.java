package es.uji.ei1027.clubesportiu;

import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import es.uji.ei1027.clubesportiu.dao.TecnicoOVIDao;

@SpringBootApplication
public class ClubesportiuApplication implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(ClubesportiuApplication.class.getName());

    @Autowired
    private TecnicoOVIDao tecnicoOVIDao;

    public static void main(String[] args) {
        // Auto-configura l'aplicació
        new SpringApplicationBuilder(ClubesportiuApplication.class).run(args);
    }

    // Funció principal
    public void run(String... strings) throws Exception {
        tecnicoOVIDao.migratePlaintextPasswords();
        log.info("Aplicacion iniciada correctamente.");
    }
}
