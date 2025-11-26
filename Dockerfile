FROM eclipse-temurin:17
COPY target/student-app-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
