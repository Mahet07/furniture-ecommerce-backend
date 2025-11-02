# Dockerfile
FROM openjdk:17-jdk-slim

# Non-root user (Render best practice)
RUN addgroup --system spring && adduser --system --ingroup spring spring

COPY target/*.jar app.jar

USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]