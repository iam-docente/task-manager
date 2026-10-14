# 📋 Task Manager API

[![CI/CD Pipeline](https://github.com/USUARIO/task-manager-demo/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/USUARIO/task-manager-demo/actions/workflows/ci-cd.yml)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-multi--stage-blue.svg)](https://www.docker.com/)

Microservicio REST para gestión de tareas, desarrollado como proyecto didáctico de la asignatura **Ingeniería DevOps (DOY0101)** de Duoc UC.

El objetivo del proyecto es **construir un pipeline CI/CD funcional** que automatice la integración, pruebas, análisis de seguridad, contenedorización, orquestación y despliegue continuo del microservicio.

> ⚠️ **Nota:** Reemplaza `USUARIO` en los badges por tu nombre de usuario de GitHub antes de hacer push.

---

## 📑 Tabla de Contenidos

1. [Descripción del proyecto](#-descripción-del-proyecto)
2. [Stack tecnológico](#-stack-tecnológico)
3. [Arquitectura](#-arquitectura)
4. [Requisitos previos](#-requisitos-previos)
5. [Ejecución local](#-ejecución-local)
6. [Ejecución con Docker](#-ejecución-con-docker)
7. [Endpoints de la API](#-endpoints-de-la-api)
8. [Pipeline CI/CD](#-pipeline-cicd)
9. [Trazabilidad y Calidad](#-trazabilidad-y-calidad)
10. [Uso de Inteligencia Artificial](#-uso-de-inteligencia-artificial)
11. [Reflexiones individuales](#-reflexiones-individuales)
12. [Autores](#-autores)

---

## 🎯 Descripción del proyecto

**Task Manager API** es un microservicio REST que permite gestionar tareas personales. Ofrece operaciones CRUD sobre tareas, con persistencia en base de datos y despliegue automatizado.

**Funcionalidades:**
- ✅ Crear tareas con validación de título.
- ✅ Listar todas las tareas o solo las completadas.
- ✅ Obtener una tarea por ID.
- ✅ Marcar una tarea como completada (idempotente).
- ✅ Eliminar tareas.
- ✅ Health check para monitoreo.

**Objetivos pedagógicos alcanzados:**

| Indicador de Logro | Descripción | Estado |
|---|---|---|
| **IL2.1** | Incorpora contenedores para aplicaciones en la nube | ✅ |
| **IL2.2** | Integra pruebas automatizadas en CI | ✅ |
| **IL2.3** | Ajusta parámetros de escalabilidad y seguridad | ✅ |
| **IL2.4** | Despliega automáticamente con trazabilidad | ✅ |
| **IL2.5** | Orquesta contenedores con Docker Compose | ✅ |

---

## 🛠 Stack tecnológico

### Backend
- **Java 17** — Lenguaje de programación.
- **Spring Boot 3.3.4** — Framework principal.
- **Spring Web** — Capa REST.
- **Spring Data JPA** — Persistencia.
- **Hibernate** — ORM.
- **H2** — Base de datos en desarrollo.
- **PostgreSQL 15** — Base de datos en producción.

### Testing
- **JUnit 5** — Framework de pruebas.
- **Mockito** — Mocking de dependencias.
- **JaCoCo** — Cobertura de código.
- **Spring Boot Test** — Integración web (`MockMvc`).

### DevOps
- **Docker** — Contenedorización (multi-stage build).
- **Docker Compose** — Orquestación multi-servicio.
- **GitHub Actions** — CI/CD.
- **Dependabot** — Escaneo de dependencias.
- **SonarQube** — Análisis estático de calidad.

---

## 🏗 Arquitectura

El proyecto sigue una **arquitectura en capas** clásica de Spring Boot:

```
┌──────────────────────────────────────────┐
│  Cliente HTTP (curl, Postman, frontend)  │
└────────────────────┬─────────────────────┘
                     │ JSON / HTTP
┌────────────────────▼─────────────────────┐
│  TaskController   (@RestController)      │  ← Capa REST
├──────────────────────────────────────────┤
│  GlobalExceptionHandler                   │  ← Manejo de errores
├──────────────────────────────────────────┤
│  TaskService      (@Service)             │  ← Lógica de negocio
├──────────────────────────────────────────┤
│  TaskRepository   (JpaRepository)        │  ← Acceso a datos
├──────────────────────────────────────────┤
│  Task             (@Entity)              │  ← Modelo de dominio
└────────────────────┬─────────────────────┘
                     │ JDBC
┌────────────────────▼─────────────────────┐
│  H2 (dev)  |  PostgreSQL 16 (docker)     │  ← Persistencia
└──────────────────────────────────────────┘
```

**Estructura de directorios:**

```
task-manager/
├── .github/
│   ├── workflows/ci-cd.yml      # Pipeline CI/CD
│   └── dependabot.yml            # Escaneo de dependencias
├── src/
│   ├── main/
│   │   ├── java/com/example/taskmanager/
│   │   │   ├── controller/        # Capa REST
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── exception/         # Manejo de excepciones
│   │   │   ├── model/             # Entidades JPA
│   │   │   ├── repository/        # Acceso a datos
│   │   │   └── service/           # Lógica de negocio
│   │   └── resources/
│   │       ├── application.properties          # Config base (H2)
│   │       └── application-docker.properties   # Config Docker (PostgreSQL)
│   └── test/java/                 # Tests unitarios e integración
├── Dockerfile                     # Multi-stage build
├── docker-compose.yml             # Orquestación app + BD
├── pom.xml                        # Dependencias Maven
├── sonar-project.properties       # Config SonarQube
└── README.md                      # Este archivo
```

---

## ⚙️ Requisitos previos

| Herramienta | Versión mínima | Verificación |
|---|---|---|
| **Java JDK** | 17 | `java -version` |
| **Maven** | 3.8+ | `mvn -version` |
| **Docker** | 20+ | `docker --version` |
| **Docker Compose** | v2 (plugin) | `docker compose version` |
| **Git** | 2.30+ | `git --version` |

**Instalación en Ubuntu/Debian:**

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven docker.io docker-compose-plugin git
sudo usermod -aG docker $USER  # (cerrar sesión y volver a entrar)
```

---

## 🖥 Ejecución local

### Opción 1: Con Maven (más rápida para desarrollo)

```bash
# 1. Clonar el repositorio
git clone https://github.com/USUARIO/task-manager-demo.git
cd task-manager-demo

# 2. Ejecutar pruebas
mvn clean test

# 3. Arrancar la aplicación
mvn spring-boot:run
```

La app estará disponible en `http://localhost:8088`.

**Consola H2** (útil para explorar la BD en desarrollo):
- URL: `http://localhost:8088/h2-console`
- JDBC URL: `jdbc:h2:mem:taskdb`
- Usuario: `sa`
- Password: *(vacío)*

### Opción 2: Con Docker Compose (más realista)

```bash
# 1. Clonar
git clone https://github.com/USUARIO/task-manager-demo.git
cd task-manager-demo

# 2. Levantar todos los servicios
docker compose up -d

# 3. Ver estado
docker compose ps

# 4. Ver logs
docker compose logs -f app
```

**Servicios levantados:**

| Servicio | Contenedor | Puerto | Estado |
|---|---|---|---|
| API | `task-manager-app` | 8088 | `healthy` |
| PostgreSQL | `task-manager-db` | 5432 | `healthy` |

**Detener y limpiar:**

```bash
docker compose down        # Detiene contenedores (mantiene datos)
docker compose down -v     # También borra volúmenes (borra datos)
```

---

## 📡 Endpoints de la API

### Resumen

| Método | Endpoint | Descripción | Código éxito |
|---|---|---|---|
| `GET` | `/api/tasks` | Lista todas las tareas | 200 |
| `GET` | `/api/tasks/completed` | Lista solo las completadas | 200 |
| `GET` | `/api/tasks/{id}` | Obtiene una tarea por ID | 200 |
| `POST` | `/api/tasks` | Crea una nueva tarea | 201 |
| `PUT` | `/api/tasks/{id}/complete` | Marca como completada | 200 |
| `DELETE` | `/api/tasks/{id}` | Elimina una tarea | 204 |
| `GET` | `/actuator/health` | Health check | 200 |

### Ejemplos con `curl`

**Crear una tarea:**

```bash
curl -X POST http://localhost:8088/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Preparar clase de DevOps"}'
```

**Respuesta (HTTP 201):**
```json
{
  "id": 1,
  "title": "Preparar clase de DevOps",
  "completed": false,
  "createdAt": "2025-01-15T10:30:00"
}
```

**Listar todas las tareas:**

```bash
curl http://localhost:8088/api/tasks
```

**Completar una tarea:**

```bash
curl -X PUT http://localhost:8088/api/tasks/1/complete
```

**Eliminar una tarea:**

```bash
curl -X DELETE http://localhost:8088/api/tasks/1
```

**Health check:**

```bash
curl http://localhost:8088/actuator/health
# {"status":"UP"}
```

### Formato de errores

Todos los errores siguen el mismo formato JSON:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Tarea no encontrada con id: 99"
}
```

---

## 🔄 Pipeline CI/CD

El pipeline se define en `.github/workflows/ci-cd.yml` y se ejecuta automáticamente en cada `push` a `main` o `develop`, y en cada Pull Request hacia `main`.

### Diagrama del pipeline

```
                    git push
                        │
                        ▼
        ┌───────────────────────────────┐
        │  JOB 1: TEST                  │
        │  ─────────────                │
        │  • Checkout del código        │
        │  • Configurar JDK 17          │
        │  • mvn clean test             │ ← IE2
        │  • Generar reporte JaCoCo     │
        │  • Subir artefacto            │
        └──────────────┬────────────────┘
                       │ (si pasa)
                       ▼
        ┌───────────────────────────────┐
        │  JOB 2: SECURITY              │
        │  ─────────────                │
        │  • Análisis con SonarQube     │ ← IE3
        │  • Quality Gate bloqueante    │
        └──────────────┬────────────────┘
                       │ (si pasa)
                       ▼
        ┌───────────────────────────────┐
        │  JOB 3: BUILD                 │
        │  ─────────────                │
        │  • docker build (multi-stage) │ ← IE1
        │  • Guardar imagen             │
        └──────────────┬────────────────┘
                       │ (si pasa y es push a main)
                       ▼
        ┌───────────────────────────────┐
        │  JOB 4: DEPLOY                │
        │  ─────────────                │
        │  • docker compose up          │ ← IE4
        │  • Health check               │
        │  • Log de trazabilidad        │
        └───────────────────────────────┘
```

### Etapas en detalle

| Etapa | Propósito | Herramientas | Indicador |
|---|---|---|---|
| **test** | Validar que el código funciona | JUnit 5, Mockito, JaCoCo | IE2 |
| **security** | Detectar vulnerabilidades y code smells | SonarQube, Dependabot | IE3 |
| **build** | Empaquetar en imagen Docker | Docker, Buildx | IE1 |
| **deploy** | Desplegar en entorno cloud simulado | Docker Compose | IE4 |

### Bloqueo automático

El pipeline está diseñado para **detenerse** si cualquier etapa falla:

- Si **los tests fallan** → jobs `security`, `build` y `deploy` no se ejecutan.
- Si **SonarQube falla el Quality Gate** → jobs `build` y `deploy` no se ejecutan.
- Si **Docker build falla** → job `deploy` no se ejecuta.

**Consecuencia:** el código defectuoso **nunca llega a producción**.

---

## 🎯 Trazabilidad y Calidad

### Trazabilidad

La trazabilidad se garantiza a **tres niveles**:

**1. Trazabilidad del código (Git):**
- Cada cambio está asociado a un commit con SHA único.
- Cada commit tiene autor, fecha y mensaje.
- El historial completo es auditable en GitHub.

**2. Trazabilidad del pipeline (GitHub Actions):**
- Cada ejecución del workflow tiene un `run_id` único.
- Cada job y step deja logs completos y permanentes.
- Cada artefacto (reporte JaCoCo, imagen Docker) tiene una retención definida.

**3. Trazabilidad del despliegue (Docker):**
- Cada imagen Docker se etiqueta con el **SHA del commit**:
  ```
  task-manager:a1b2c3d4e5f6...
  task-manager:latest
  ```
- Al inspeccionar un contenedor, se puede saber **exactamente** qué código está corriendo:
  ```bash
  docker inspect task-manager-app | grep Image
  ```
- Los logs de despliegue registran:
  - `Commit SHA`: el código exacto.
  - `Autor`: quién hizo el push.
  - `Run ID`: la ejecución específica del pipeline.
  - `Timestamp`: cuándo se desplegó.

**Ejemplo de log de trazabilidad (extraído del pipeline):**

```
================================
  DESPLIEGUE EXITOSO
================================
Commit SHA:   a1b2c3d4e5f6...
Autor:        juan.perez
Repositorio:  juanperez/task-manager-demo
Rama:         main
Workflow:     CI/CD Pipeline - Task Manager
Run ID:       1234567890
Fecha:        2025-01-15T10:30:00Z
================================
```

### Calidad

La calidad se garantiza con **múltiples capas**:

| Capa | Herramienta | Métrica |
|---|---|---|
| **Cobertura de tests** | JaCoCo | > 70% |
| **Análisis estático** | SonarQube | 0 bugs, 0 vulnerabilidades blocker |
| **Escaneo de dependencias** | Dependabot | PRs automáticos |
| **Validación de entrada** | Jakarta Validation | `@NotBlank`, `@Size` |
| **Manejo de errores** | `@RestControllerAdvice` | Formato estándar |

### Quality Gate de SonarQube

El pipeline **se bloquea** si el Quality Gate no se cumple. Los criterios mínimos son:

- **Cobertura:** ≥ 70%
- **Duplicación:** ≤ 3%
- **Vulnerabilidades:** 0 (Blocker, Critical)
- **Bugs:** 0 (Blocker, Critical)

**Resultado:** El código no llega a `main` si no supera estos umbrales.

### Reportes generados

- **Reporte de cobertura JaCoCo:** Disponible como artefacto descargable en cada run del pipeline.
- **Reporte de SonarQube:** Disponible en el dashboard de SonarCloud.
- **Historial de despliegues:** Disponible en la pestaña "Actions" de GitHub.

---

## 🤖 Uso de Inteligencia Artificial

En cumplimiento con la política de uso de IA de la asignatura, declaramos lo siguiente:

### Herramientas de IA utilizadas

| Herramienta | Uso | Justificación |
|---|---|---|
| **[Nombre de IA 1]** | Redacción inicial del README | Apoyo en redacción |
| **[Nombre de IA 2]** | Consultas sobre sintaxis de GitHub Actions | Referencia técnica |
| *(Agregar o eliminar según corresponda)* | | |

### ¿Cómo se aplicó la IA?

- **Búsqueda de referencias:** Se consultaron ejemplos de configuración de Docker y GitHub Actions.
- **Redacción:** Se utilizó asistencia para mejorar la claridad del README.
- **Diagramas:** Apoyo en la creación de diagramas ASCII.

### ¿Qué NO se delegó a la IA?

En cumplimiento estricto de la política:

- ❌ **NO** se usó IA para redactar las reflexiones individuales.
- ❌ **NO** se usó IA para justificar decisiones técnicas.
- ❌ **NO** se usó IA para escribir la lógica de negocio.
- ❌ **NO** se copió código sin comprenderlo y validarlo.

### Validación

Todo contenido generado o asistido por IA fue:

1. **Revisado** por los integrantes del equipo.
2. **Adaptado** al contexto específico del proyecto.
3. **Validado** contra los requerimientos de la rúbrica.
4. **Citado** en esta sección.

### Referencias de IA

- Política institucional: [https://bibliotecas.duoc.cl/ia](https://bibliotecas.duoc.cl/ia)

---

## 💭 Reflexiones individuales

> **Nota para los autores:** Esta sección **debe ser redactada por cada integrante SIN apoyo de IA**, según la política del encargo. Es un requisito obligatorio. Las reflexiones deben ser **personales, específicas y honestas**.

### Reflexión — [Nombre del Integrante 1]

*(Escribir aquí sin asistencia de IA. Extensión sugerida: 150-250 palabras.)*

**Preguntas guía:**
- ¿Qué aprendiste durante el desarrollo de este proyecto que no sabías antes?
- ¿Cuál fue el mayor desafío técnico que enfrentaste y cómo lo resolviste?
- ¿Qué harías diferente si tuvieras que hacerlo de nuevo?
- ¿Cómo contribuiste específicamente al proyecto?

---

### Reflexión — [Nombre del Integrante 2]

*(Escribir aquí sin asistencia de IA. Extensión sugerida: 150-250 palabras.)*

**Preguntas guía:**
- ¿Qué concepto de DevOps comprendiste mejor al aplicar el pipeline en la práctica?
- ¿Qué parte del proyecto te resultó más compleja y cómo la abordaste?
- ¿Cómo describirías tu aporte concreto al trabajo en equipo?
- ¿Qué te llevas de esta experiencia para tu futuro profesional?

---

### Reflexión — [Nombre del Integrante 3] *(si aplica)*

*(Escribir aquí sin asistencia de IA. Extensión sugerida: 150-250 palabras.)*

---

## 👥 Autores

| Nombre | Rol | GitHub |
|---|---|---|
| [Nombre 1] | [Rol: ej. Backend, DevOps] | [@usuario1](https://github.com/usuario1) |
| [Nombre 2] | [Rol: ej. Testing, Docs] | [@usuario2](https://github.com/usuario2) |
| [Nombre 3] | [Rol] | [@usuario3](https://github.com/usuario3) |

**Institución:** Duoc UC — Ingeniería en Informática
**Asignatura:** DOY0101 — Ingeniería DevOps
**Docente:** [Nombre del docente]
**Fecha de entrega:** [Fecha]

---

## 📄 Licencia

Este proyecto es de uso académico. Desarrollado como parte del curso **Ingeniería DevOps (DOY0101)**.

---

## 🔗 Referencias

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Compose Reference](https://docs.docker.com/compose/)
- [SonarQube Quality Gates](https://docs.sonarsource.com/sonarqube/latest/user-guide/quality-gates/)

---

⭐ Si este proyecto te resultó útil, considera darle una estrella en GitHub.
