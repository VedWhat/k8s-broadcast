FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/publisher-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
