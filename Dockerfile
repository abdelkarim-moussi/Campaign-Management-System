FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/campaign-cms.jar /app/campaign-cms.jar
EXPOSE 9000
CMD ["java", "-jar", "your-app.jar"]