# =========================================================
# FASE 1: Compilación con Java 21
# =========================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================
# FASE 2: Entorno de ejecución con Java 21 (JRE)
# =========================================================
# Cambiamos eclipse-temurin:17 por eclipse-temurin:21
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV TZ=America/El_Salvador

ENTRYPOINT ["java", "-Duser.timezone=America/El_Salvador", "-jar", "app.jar"]