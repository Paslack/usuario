FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon



FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR
COPY --from=build /app/build/libs/*.jar /app/usuario.jar

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app

USER spring:spring

EXPOSE 8080

# Otimizações JVM para containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "usuario.jar"]