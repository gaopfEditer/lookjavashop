FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY ../server/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]