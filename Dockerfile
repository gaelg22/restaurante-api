# Etapa 1: compilar el JAR con Maven (ya no hace falta subir target/ al repo)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw && ./mvnw -B clean package -DskipTests

# Etapa 2: correr solo el JAR con un JRE ligero
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
