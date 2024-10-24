FROM openjdk:17
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.output.ansi.enabled=ALWAYS", "-jar", "app.jar"]
