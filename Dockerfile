
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]