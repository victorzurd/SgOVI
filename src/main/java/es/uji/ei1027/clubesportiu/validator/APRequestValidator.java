package es.uji.ei1027.clubesportiu.validator;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.Estado;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class APRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return APRequest.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        APRequest request = (APRequest) obj;

        
        if (request.getIdUsuario() <= 0) {
            errors.rejectValue("idUsuario", "obligatorio", "El usuario es obligatorio");
        }

        
        if (request.getFechaSolicitud() == null) {
            request.setFechaSolicitud(LocalDate.now());
        }

        
        if (request.getDescripcion() != null && request.getDescripcion().length() > 1000) {
            errors.rejectValue("descripcion", "longitud", "La descripción no puede superar 1000 caracteres");
        }

        
        if (request.getEstado() == null) {
            errors.rejectValue("estado", "obligatorio", "El estado es obligatorio");
        } 
    }
}
