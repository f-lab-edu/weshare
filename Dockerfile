FROM openjdk:17
COPY build/libs/*.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]
