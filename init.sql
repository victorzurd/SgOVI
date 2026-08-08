-- Crear tipo ENUM para estado si no existe
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'estado') THEN
        CREATE TYPE estado AS ENUM (
            'pendiente',
            'PENDIENTE',
            'en_revision',
            'EN_REVISION',
            'aprobado',
            'APROBADO',
            'rechazada',
            'RECHAZADA',
            'con_contrato',
            'CON_CONTRATO',
            'finalizada',
            'FINALIZADA'
        );
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS usuarioovi (
    idusuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    provincia VARCHAR(100),
    consentimientorgbd BOOLEAN NOT NULL DEFAULT FALSE,
    estadoaceptado BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(255) NOT NULL
);

-- 2. Tabla: asistentepersonal
CREATE TABLE IF NOT EXISTS asistentepersonal (
    idasistente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    disponibilidad VARCHAR(255),
    estadoaceptado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    zona VARCHAR(100),
    provincia VARCHAR(100),
    preferencias TEXT,
    puntuacion INT DEFAULT 0,
    consentimientorgbd BOOLEAN NOT NULL DEFAULT FALSE
);

-- 3. Tabla: tecnicoovi
CREATE TABLE IF NOT EXISTS tecnicoovi (
    idtecnico SERIAL PRIMARY KEY,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

-- 4. Tabla: seleccion (CORREGIDA: idusuario en lugar de id_usuario)
CREATE TABLE IF NOT EXISTS seleccion (
    idseleccion SERIAL PRIMARY KEY,
    fechaseleccion DATE NOT NULL,
    estado estado NOT NULL,
    idusuario INT NOT NULL,
    idasistente INT NOT NULL,
    CONSTRAINT fk_seleccion_usuario FOREIGN KEY (idusuario) 
        REFERENCES usuarioovi(idusuario) ON DELETE CASCADE,
    CONSTRAINT fk_seleccion_asistente FOREIGN KEY (idasistente) 
        REFERENCES asistentepersonal(idasistente) ON DELETE CASCADE
);

-- 5. Tabla: aprequest
CREATE TABLE IF NOT EXISTS aprequest (
    idrequest SERIAL PRIMARY KEY,
    idusuario INT NOT NULL,
    fechasolicitud DATE NOT NULL,
    descripcion TEXT,
    estado estado NOT NULL,
    idseleccion INT,
    titulo VARCHAR(150),
    zona VARCHAR(100),
    provincia VARCHAR(100),
    preferencias TEXT,
    horario VARCHAR(100),
    CONSTRAINT fk_aprequest_usuario FOREIGN KEY (idusuario) 
        REFERENCES usuarioovi(idusuario) ON DELETE CASCADE,
    CONSTRAINT fk_aprequest_seleccion FOREIGN KEY (idseleccion) 
        REFERENCES seleccion(idseleccion) ON DELETE SET NULL
);

-- 6. Tabla: comunicacionusuarioovipap
CREATE TABLE IF NOT EXISTS comunicacionusuarioovipap (
    idcomunicacion SERIAL PRIMARY KEY,
    idseleccion INT NOT NULL,
    fecha TIMESTAMP NOT NULL,
    mensaje TEXT NOT NULL,
    emisor VARCHAR(100) NOT NULL,
    receptor VARCHAR(100) NOT NULL,
    CONSTRAINT fk_comunicacion_seleccion FOREIGN KEY (idseleccion) 
        REFERENCES seleccion(idseleccion) ON DELETE CASCADE
);

-- 7. Tabla: registrocontrato
CREATE TABLE IF NOT EXISTS registrocontrato (
    idcontrato SERIAL PRIMARY KEY,
    fechainicio DATE NOT NULL,
    fechafin DATE NOT NULL,
    documentopdf VARCHAR(255),
    estado estado NOT NULL,
    idrequest INT NOT NULL,
    idseleccion INT NOT NULL,
    CONSTRAINT fk_contrato_request FOREIGN KEY (idrequest) 
        REFERENCES aprequest(idrequest) ON DELETE CASCADE,
    CONSTRAINT fk_contrato_seleccion FOREIGN KEY (idseleccion) 
        REFERENCES seleccion(idseleccion) ON DELETE CASCADE
);

-- 8. Tabla: chatsession
CREATE TABLE IF NOT EXISTS chatsession (
    idchat SERIAL PRIMARY KEY,
    idusuario INT NOT NULL,
    idasistente INT NOT NULL,
    idrequest INT NOT NULL,
    fechacreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado estado NOT NULL DEFAULT 'aprobado',
    CONSTRAINT fk_chat_usuario FOREIGN KEY (idusuario) 
        REFERENCES usuarioovi(idusuario) ON DELETE CASCADE,
    CONSTRAINT fk_chat_asistente FOREIGN KEY (idasistente) 
        REFERENCES asistentepersonal(idasistente) ON DELETE CASCADE,
    CONSTRAINT fk_chat_request FOREIGN KEY (idrequest) 
        REFERENCES aprequest(idrequest) ON DELETE CASCADE
);

CREATE TABLE candidato (
    idasistente INT,
    idrequest INT,
    PRIMARY KEY (idasistente, idrequest)
);

-- 9. Tabla: mensajechat
CREATE TABLE IF NOT EXISTS mensajechat (
    idmensaje SERIAL PRIMARY KEY,
    idchat INT NOT NULL,
    remitente VARCHAR(100) NOT NULL,
    contenido TEXT NOT NULL,
    fechaenvio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mensaje_chat FOREIGN KEY (idchat) 
        REFERENCES chatsession(idchat) ON DELETE CASCADE
);


INSERT INTO tecnicoovi (correo, password, nombre)
VALUES ('admin@sgovi.es', 'admin123', 'Administrador Principal')
ON CONFLICT (correo) DO NOTHING;

-- =============================================================================
-- USUARIO OVI DE PRUEBA
-- =============================================================================
INSERT INTO usuarioovi (nombre, apellidos, email, telefono, direccion, provincia, consentimientorgbd, estadoaceptado, password)
VALUES (
    'Juan', 
    'Pérez García', 
    'usuario@sgovi.es', 
    '611223344', 
    'Calle Mayor 12, Castellón', 
    'Castellón', 
    true, 
    true, 
    'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C'
)
ON CONFLICT (email) DO NOTHING;

-- =============================================================================
-- ASISTENTE PERSONAL (PAP) DE PRUEBA
-- =============================================================================
INSERT INTO asistentepersonal (nombre, apellidos, email, contraseña, telefono, disponibilidad, estadoaceptado, activo, zona, provincia, preferencias, puntuacion, consentimientorgbd)
VALUES (
    'María', 
    'López Fernández', 
    'asistente@sgovi.es', 
    'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C', 
    '655443322', 
    'Lunes a Viernes de 9:00 a 18:00', 
    true, 
    true, 
    'Castellón Centro', 
    'Castellón', 
    'Acompañamiento y soporte en movilidad', 
    5, 
    true
)
ON CONFLICT (email) DO NOTHING;

