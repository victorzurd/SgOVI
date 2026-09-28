# SgOVI

### Sistema de Gestión de Operaciones e Información

Aplicación web para gestionar usuarios de la OVI, asistentes personales, técnicos, solicitudes, contratos y comunicaciones desde una plataforma centralizada.

<p align="center">
  <strong>Java 21</strong> · <strong>Spring Boot 3.4.1</strong> · <strong>PostgreSQL 15</strong> · <strong>Thymeleaf</strong>
</p>

---

## Contenido

- [Características principales](#características-principales)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos e instalación](#requisitos-previos-e-instalación)
- [Variables de entorno](#variables-de-entorno)
- [Ejemplos de uso y rutas](#ejemplos-de-uso-y-rutas)
- [Seguridad](#seguridad)

## Características principales

- Registro, inicio de sesión y gestión de perfiles de usuarios OVI y asistentes personales.
- Gestión de técnicos y validación de cuentas.
- Creación y seguimiento de solicitudes de asistencia personal.
- Gestión de candidatos y selección de asistentes para solicitudes.
- Creación, consulta y firma de contratos.
- Comunicación entre usuarios y asistentes mediante salas de chat.
- Interfaz web renderizada en el servidor con vistas Thymeleaf.

## Tecnologías utilizadas

| Área | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Aplicación web | Spring Web / Spring MVC |
| Vistas | Thymeleaf y Thymeleaf Layout Dialect |
| Interfaz | HTML y CSS |
| Persistencia | Spring JDBC |
| Base de datos | PostgreSQL 15 |
| Correo | Spring Boot Mail |
| Documentos PDF | Apache PDFBox 3.0.2 |
| Hash de contraseñas | Jasypt 1.9.3 |
| Construcción y dependencias | Maven y Maven Wrapper |
| Pruebas | Spring Boot Starter Test |
| Base de datos local | Docker Compose |

## Estructura del proyecto

```text
SgOVI/
├── src/
│   ├── main/
│   │   ├── java/es/uji/ei1027/clubesportiu/
│   │   │   ├── controller/    # Rutas y controladores web
│   │   │   ├── dao/           # Acceso a datos y mapeo de filas
│   │   │   ├── model/         # Modelos de dominio
│   │   │   ├── util/          # Utilidades compartidas
│   │   │   └── validator/     # Validación de formularios
│   │   └── resources/
│   │       ├── static/        # CSS y recursos estáticos
│   │       ├── templates/     # Vistas Thymeleaf
│   │       └── application.properties
│   └── test/                  # Pruebas automatizadas
├── docker-compose.yml         # PostgreSQL para desarrollo local
├── init.sql                   # Inicialización de la base de datos
├── pom.xml                    # Dependencias y configuración de Maven
├── mvnw                       # Maven Wrapper para Linux/macOS
└── mvnw.cmd                   # Maven Wrapper para Windows
```

## Requisitos previos e instalación

### Requisitos

- Git
- JDK 21
- Docker Desktop (Windows/macOS) o Docker Engine y Docker Compose (Linux)

### Arranque local

1. Clona el repositorio y accede a su directorio:

   ```bash
   git clone https://github.com/victorzurd/SgOVI.git
   cd SgOVI
   ```

2. Inicia la base de datos PostgreSQL:

   ```bash
   docker compose up -d postgres-db
   ```

   Compose publica PostgreSQL en el puerto `5432` y monta `init.sql` para inicializar la base de datos.

3. Inicia la aplicación Spring Boot.

   **Windows (PowerShell):**

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   **Linux/macOS:**

   ```bash
   ./mvnw spring-boot:run
   ```

4. Abre `http://localhost:8080` en el navegador.

Para detener la base de datos local:

```bash
docker compose down
```

### Ejecutar las pruebas

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux/macOS:

```bash
./mvnw test
```

## Variables de entorno

El servicio PostgreSQL de `docker-compose.yml` define estas variables dentro del contenedor:

| Variable | Propósito |
|---|---|
| `POSTGRES_DB` | Nombre de la base de datos |
| `POSTGRES_USER` | Usuario de PostgreSQL |
| `POSTGRES_PASSWORD` | Contraseña del usuario de PostgreSQL |

La conexión usada por Spring está configurada en `src/main/resources/application.properties`. Actualmente no se detectan sustituciones de variables de entorno (`${...}`) ni un archivo `.env`; si cambias los datos de PostgreSQL, mantén sincronizados los valores de Compose y de la configuración de Spring.

Los valores de desarrollo incluidos en los archivos de configuración no deben reutilizarse en producción. Para desplegar, proporciona los secretos mediante la configuración segura del entorno y evita guardarlos en el repositorio.

## Ejemplos de uso y rutas

SgOVI es una aplicación web Spring MVC que devuelve páginas Thymeleaf; no se presenta como una API REST independiente. Estas rutas ilustran algunos flujos disponibles:

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/` | Página de inicio |
| `GET` | `/Inicio/logins` | Selección de acceso |
| `POST` | `/UsuarioOVI/login` | Inicio de sesión de usuario OVI |
| `POST` | `/AsistentePersonal/login` | Inicio de sesión de asistente personal |
| `POST` | `/TecnicoOVI/login` | Inicio de sesión de técnico |
| `GET` | `/chat/usuario/sala/{idChat}` | Acceso de usuario a una sala de chat |
| `POST` | `/chat/usuario/enviar/{idChat}` | Envío de mensaje en una sala |
| `GET` | `/contrato/ver/{idContrato}` | Consulta de un contrato |

Las rutas que incluyen identificadores requieren sustituir los marcadores, por ejemplo `{idChat}`, por los valores correspondientes. Algunas páginas y operaciones dependen de que el usuario haya iniciado sesión y de que existan registros relacionados.

## Seguridad

- Las contraseñas se procesan con `BasicPasswordEncryptor` de Jasypt antes de guardarse y se verifican durante el inicio de sesión. Es un hash/verificador unidireccional, no una contraseña cifrada para recuperarla en texto claro.
- Según la configuración inicial del proyecto, todas las cuentas ya creadas comparten una misma contraseña inicial(admin123). 
- Cambia también las credenciales de ejemplo de PostgreSQL antes de cualquier despliegue.
- Usa contraseñas únicas y robustas, y no guardes secretos, tokens ni datos personales en Git.
