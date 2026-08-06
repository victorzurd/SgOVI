package es.uji.ei1027.clubesportiu.validator;

import es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UsuarioOVIValidator implements Validator {

    private final UsuarioOVIDao usuarioOVIDao;

    public UsuarioOVIValidator(UsuarioOVIDao usuarioOVIDao) {
        this.usuarioOVIDao = usuarioOVIDao;
    }

    @Override
    public boolean supports(Class<?> cls) {
        return UsuarioOVI.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UsuarioOVI usuario = (UsuarioOVI) obj;

        if (usuario.getNombre() == null || usuario.getNombre().trim().equals(""))
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio");

        if (usuario.getApellidos() == null || usuario.getApellidos().trim().equals(""))
            errors.rejectValue("apellidos", "obligatorio", "Los apellidos son obligatorios");

        if (usuario.getEmail() == null || usuario.getEmail().trim().equals("")) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (!usuario.getEmail().contains("@")) {
            errors.rejectValue("email", "formato", "El email no es válido");
        } else {
            
            boolean emailRepetido = usuarioOVIDao.getUsuariosOVI().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()) 
                            && u.getIdUsuario() != usuario.getIdUsuario());
            
            if (emailRepetido) {
                errors.rejectValue("email", "repetido", "Este correo electrónico ya está registrado en el sistema");
            }
        }

        
        if (usuario.getIdUsuario() == 0) {
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", "obligatorio", "La contraseña es obligatoria");
            } else if (usuario.getPassword().length() < 6) {
                errors.rejectValue("password", "longitud", "La contraseña debe tener al menos 6 caracteres");
            }
        }

        if (usuario.getTelefono() != null && !usuario.getTelefono().trim().equals(""))
            if (!usuario.getTelefono().matches("^[0-9]{9}$"))
                errors.rejectValue("telefono", "formato", "El teléfono debe tener 9 dígitos numéricos");

        
        if (usuario.getIdUsuario() == 0) {
            if (!usuario.isConsentimientoRGBD()) {
                errors.rejectValue("consentimientoRGBD", "obligatorio", "Debe aceptar el tratamiento de protección de datos (RGBD) para registrarse");
            }
        }
    }
}