# ============================================================
# ETAPA 1: Construcción (BUILD)
# ============================================================
# Esta etapa NO formará parte de la imagen final.
# Solo se usa para compilar el JAR con Maven.
FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /app

# Paso 1: Copiamos SOLO el pom.xml primero.
# Ventaja: si el código cambia pero el pom no, Docker reutiliza
# la capa de dependencias descargadas (caché de capas).
COPY pom.xml .

# Paso 2: Descargamos todas las dependencias.
# -B = modo batch (sin output interactivo, ideal para CI/CD).
# go-offline = descarga todo lo que necesitará Maven para compilar.
RUN mvn dependency:go-offline -B

# Paso 3: Copiamos el código fuente.
COPY src ./src

# Paso 4: Compilamos y empaquetamos el JAR, saltando los tests.
# Los tests YA se ejecutaron en el pipeline (ver Fase 9).
# Saltarlos aquí acelera el build de la imagen.
RUN mvn clean package -DskipTests -B

# ============================================================
# ETAPA 2: Runtime (imagen final, ligera y segura)
# ============================================================
# Usamos JRE (no JDK) sobre Alpine (distribución minimalista).
# Tamaño: ~180 MB vs. ~600 MB de la imagen con Maven.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Creamos un usuario NO root por seguridad.
# Si un atacante compromete el contenedor, no tendrá privilegios de root.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiamos SOLO el JAR desde la etapa de construcción.
# --from=build hace referencia a la etapa 1.
COPY --from=build /app/target/*.jar app.jar

# Documentamos el puerto que expone la app.
# Debe coincidir con server.port=8088 de application.properties.
EXPOSE 8088

# HEALTHCHECK: Docker (y Kubernetes) usarán este comando para
# saber si el contenedor está "sano". Si falla 3 veces seguidas,
# Docker lo marca como "unhealthy" y lo puede reiniciar.
# Usa el endpoint de Actuator, que devuelve {"status":"UP"}.
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8088/actuator/health || exit 1

# Comando que se ejecuta al iniciar el contenedor.
# Formato exec (con corchetes) → NO usa shell → mejor manejo de señales.
ENTRYPOINT ["java", "-jar", "app.jar"]
