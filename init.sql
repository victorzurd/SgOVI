-- Estructura de prueba (ajusta según los modelos de SgOVI)
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    rol VARCHAR(50) NOT NULL
);

-- Datos ficticios iniciales
INSERT INTO usuarios (nombre, email, rol) VALUES
('Usuario Demo', 'demo@universidad.edu', 'ESTUDIANTE'),
('Administrador', 'admin@universidad.edu', 'ADMIN')
ON CONFLICT (email) DO NOTHING;