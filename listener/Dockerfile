FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/listener-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
