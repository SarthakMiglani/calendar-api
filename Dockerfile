# ---- Build Stage ----
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Package Stage ----
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/calendar-booking-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
