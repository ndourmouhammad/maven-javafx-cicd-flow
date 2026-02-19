# Étape de build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Étape d'exécution
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/candidats-1.0-SNAPSHOT.jar app.jar

# Note: Pour une application JavaFX, l'exécution nécessite normalement un serveur d'affichage (X11).
ENTRYPOINT ["java", "-jar", "app.jar"]