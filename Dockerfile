FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /opt/app
COPY pom.xml ./
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]