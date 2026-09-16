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
    fechafin DATE,
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
    'CASTELLON', 
    'Acompañamiento y soporte en movilidad', 
    5, 
    true
)
ON CONFLICT (email) DO NOTHING;

-- =============================================================================
-- 1. USUARIOS (3 Nuevos - Contraseña original para admin123)
-- =============================================================================
INSERT INTO usuarioovi (nombre, apellidos, email, telefono, direccion, provincia, consentimientorgbd, estadoaceptado, password) VALUES
('Ana', 'Gómez Martín', 'ana.gomez@email.com', '600111222', 'Av. Valencia 45, Castellón', 'CASTELLON', true, true, 'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C'),
('Carlos', 'Ruiz Vidal', 'carlos.ruiz@email.com', '600333444', 'C/ Colón 12, Valencia', 'VALENCIA', true, true, 'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C'),
('Laura', 'Martínez Soler', 'laura.martinez@email.com', '600555666', 'C/ Mayor 88, Alicante', 'ALICANTE', true, true, 'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C')
ON CONFLICT (email) DO NOTHING;

-- =============================================================================
-- 2. ASISTENTES PERSONALES (3 Nuevos - Contraseña original para admin123)
-- =============================================================================
INSERT INTO asistentepersonal (nombre, apellidos, email, contraseña, telefono, disponibilidad, estadoaceptado, activo, zona, provincia, preferencias, puntuacion, consentimientorgbd) VALUES
('Pedro', 'Sánchez Gil', 'pedro.sanchez@email.com', 'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C', '677111222', 'Mañanas (8:00 - 14:00)', true, true, 'Grao', 'CASTELLON', 'Asistencia física y movilidad', 4, true),
('Elena', 'Rodríguez Pons', 'elena.rodriguez@email.com', 'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C', '677333444', 'Tardes y fines de semana', true, true, 'Ciutat Vella', 'VALENCIA', 'Acompañamiento académico y laboral', 5, true),
('David', 'Fernández Cano', 'david.fernandez@email.com', 'rkZFXza8U4vE2gdfMWq9adlhPkwzaY+C', '677555666', 'Jornada completa', true, true, 'Centro', 'ALICANTE', 'Conducción adaptada y tareas de hogar', 3, true)
ON CONFLICT (email) DO NOTHING;

-- =============================================================================
-- 3. SELECCIONES (3 Registros coherentes con Usuarios 2, 3 y 4 y Asistentes 2, 3 y 4)
-- =============================================================================
INSERT INTO seleccion (fechaseleccion, estado, idusuario, idasistente) VALUES
('2026-02-10', 'en_revision', 2, 2),  -- Ana con Pedro
('2026-03-01', 'con_contrato', 3, 3), -- Carlos con Elena
('2026-01-15', 'finalizada', 4, 4);   -- Laura con David

-- =============================================================================
-- 4. SOLICITUDES DE ASISTENCIA (3 Registros vinculados a los Usuarios 2, 3 y 4)
-- =============================================================================
INSERT INTO aprequest (idusuario, fechasolicitud, descripcion, estado, idseleccion, titulo, zona, provincia, preferencias, horario) VALUES
(2, '2026-02-05', 'Acompañamiento a citas médicas y gestiones administrativas.', 'en_revision', 1, 'Apoyo puntual en mañanas', 'Castellón Centro', 'CASTELLON', 'Vehículo propio preferible', 'Lunes y Miércoles 9:00-12:00'),
(3, '2026-02-20', 'Acompañamiento a clases universitarias y toma de apuntes.', 'con_contrato', 2, 'Apoyo universitario de tarde', 'Ciutat Vella', 'VALENCIA', 'Estudios universitarios preferibles', 'Lunes a Jueves 15:00-19:00'),
(4, '2026-01-05', 'Asistencia para actividades de ocio e integración comunitaria.', 'finalizada', 3, 'Acompañamiento ocio fin de semana', 'Centro', 'ALICANTE', 'Movilidad reducida', 'Sábados 10:00-18:00');

-- =============================================================================
-- 5. CANDIDATOS (3 Postulaciones cruzando Asistentes 2, 3 y 4 con las Requests)
-- =============================================================================
INSERT INTO candidato (idasistente, idrequest) VALUES
(2, 1), -- Pedro en la solicitud 1
(3, 2), -- Elena en la solicitud 2
(4, 3); -- David en la solicitud 3

-- =============================================================================
-- 6. COMUNICACIONES (3 Mensajes oficiales vinculados a las Selecciones 1, 2 y 3)
-- =============================================================================
INSERT INTO comunicacionusuarioovipap (idseleccion, fecha, mensaje, emisor, receptor) VALUES
(1, '2026-02-11 10:00:00', 'Hola Pedro, ¿tienes disponibilidad para empezar la próxima semana?', 'Ana Gómez', 'Pedro Sánchez'),
(2, '2026-03-02 09:30:00', 'Hola Elena, he enviado la documentación requerida para el contrato.', 'Carlos Ruiz', 'Elena Rodríguez'),
(3, '2026-01-16 11:15:00', 'Hola David, todo listo para la asistencia de este fin de semana.', 'Laura Martínez', 'David Fernández');

-- =============================================================================
-- 7. REGISTRO DE CONTRATOS (3 Contratos asociados a las Solicitudes y Selecciones)
-- =============================================================================
INSERT INTO registrocontrato (fechainicio, fechafin, documentopdf, estado, idrequest, idseleccion) VALUES
('2026-02-15', '2026-08-15', '/docs/contratos/contrato_req1_sel1.pdf', 'en_revision', 1, 1),
('2026-03-05', '2026-09-05', '/docs/contratos/contrato_req2_sel2.pdf', 'con_contrato', 2, 2),
('2026-01-20', '2026-02-20', '/docs/contratos/contrato_req3_sel3.pdf', 'finalizada', 3, 3);

-- =============================================================================
-- 8. CHATSESSION (3 Sesiones de chat entre Usuario, Asistente y Solicitud)
-- =============================================================================
INSERT INTO chatsession (idusuario, idasistente, idrequest, fechacreacion, estado) VALUES
(2, 2, 1, '2026-02-10 09:00:00', 'en_revision'),
(3, 3, 2, '2026-03-01 14:00:00', 'aprobado'),
(4, 4, 3, '2026-01-15 16:30:00', 'finalizada');

-- =============================================================================
-- 9. MENSAJECHAT (3 Mensajes en el chat)
-- =============================================================================
INSERT INTO mensajechat (idchat, remitente, contenido, fechaenvio) VALUES
(1, 'Ana Gómez', 'Buenos días Pedro, ¿podemos concretar los días de esta semana?', '2026-02-10 09:05:00'),
(2, 'Carlos Ruiz', 'Hola Elena, nos vemos en la entrada del campus a las 15:00.', '2026-03-01 14:05:00'),
(3, 'Laura Martínez', 'Muchas gracias por la ayuda prestada durante este mes, David.', '2026-01-15 16:35:00');

