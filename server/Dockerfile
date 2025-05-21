# 第一阶段：构建 JAR 包
FROM maven:3.8.6-openjdk-8 AS builder
WORKDIR /app
COPY . /app
RUN mvn clean package

# 第二阶段：运行 JAR 包
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]