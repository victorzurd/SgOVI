-- =============================================================================
-- SCRIPT DE CREACIÓN DE TABLAS - PROYECTO SgOVI (PostgreSQL)
-- =============================================================================

-- 1. Tabla: UsuarioOVI
CREATE TABLE IF NOT EXISTS usuarioovi (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    consentimiento_rgbd BOOLEAN NOT NULL DEFAULT FALSE,
    estado_aceptado BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(255) NOT NULL
);

-- 2. Tabla: AsistentePersonal
CREATE TABLE IF NOT EXISTS asistentepersonal (
    id_asistente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    disponibilidad VARCHAR(255),
    estado_aceptado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    zona VARCHAR(100),
    preferencias TEXT,
    puntuacion INT DEFAULT 0,
    consentimiento_rgbd BOOLEAN NOT NULL DEFAULT FALSE
);

-- 3. Tabla: TecnicoOVI
CREATE TABLE IF NOT EXISTS tecnicoovi (
    id_tecnico SERIAL PRIMARY KEY,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

-- 4. Tabla: Seleccion
CREATE TABLE IF NOT EXISTS seleccion (
    id_seleccion SERIAL PRIMARY KEY,
    fecha_seleccion DATE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    id_usuario INT NOT NULL,
    id_asistente INT NOT NULL,
    CONSTRAINT fk_seleccion_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuario_ovi(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_seleccion_asistente FOREIGN KEY (id_asistente) 
        REFERENCES asistente_personal(id_asistente) ON DELETE CASCADE
);

-- 5. Tabla: APRequest
CREATE TABLE IF NOT EXISTS aprequest (
    id_request SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_solicitud DATE NOT NULL,
    descripcion TEXT,
    estado VARCHAR(50) NOT NULL,
    id_seleccion INT,
    titulo VARCHAR(150),
    zona VARCHAR(100),
    preferencias TEXT,
    horario VARCHAR(100),
    CONSTRAINT fk_ap_request_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuario_ovi(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_ap_request_seleccion FOREIGN KEY (id_seleccion) 
        REFERENCES seleccion(id_seleccion) ON DELETE SET NULL
);

-- 6. Tabla: ComunicacionUsuarioOVIPAP
CREATE TABLE IF NOT EXISTS comunicacionusuarioovipap (
    id_comunicacion SERIAL PRIMARY KEY,
    id_seleccion INT NOT NULL,
    fecha TIMESTAMP NOT NULL,
    mensaje TEXT NOT NULL,
    emisor VARCHAR(100) NOT NULL,
    receptor VARCHAR(100) NOT NULL,
    CONSTRAINT fk_comunicacion_seleccion FOREIGN KEY (id_seleccion) 
        REFERENCES seleccion(id_seleccion) ON DELETE CASCADE
);

-- 7. Tabla: RegistroContrato
CREATE TABLE IF NOT EXISTS registrocontrato (
    id_contrato SERIAL PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    documento_pdf VARCHAR(255),
    estado VARCHAR(50) NOT NULL,
    id_request INT NOT NULL,
    id_seleccion INT NOT NULL,
    CONSTRAINT fk_contrato_request FOREIGN KEY (id_request) 
        REFERENCES ap_request(id_request) ON DELETE CASCADE,
    CONSTRAINT fk_contrato_seleccion FOREIGN KEY (id_seleccion) 
        REFERENCES seleccion(id_seleccion) ON DELETE CASCADE
);

-- 8. Tabla: ChatSession
CREATE TABLE IF NOT EXISTS chatsession (
    id_chat SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_asistente INT NOT NULL,
    id_request INT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) NOT NULL,
    CONSTRAINT fk_chat_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuario_ovi(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_chat_asistente FOREIGN KEY (id_asistente) 
        REFERENCES asistente_personal(id_asistente) ON DELETE CASCADE,
    CONSTRAINT fk_chat_request FOREIGN KEY (id_request) 
        REFERENCES ap_request(id_request) ON DELETE CASCADE
);

-- 9. Tabla: MensajeChat
CREATE TABLE IF NOT EXISTS mensajechat (
    id_mensaje SERIAL PRIMARY KEY,
    id_chat INT NOT NULL,
    remitente VARCHAR(100) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mensaje_chat FOREIGN KEY (id_chat) 
        REFERENCES chat_session(id_chat) ON DELETE CASCADE
);

INSERT INTO tecnicoovi (correo, password, nombre)
VALUES ('admin@sgovi.es', 'admin123', 'Admin')
ON CONFLICT (correo) DO NOTHING;