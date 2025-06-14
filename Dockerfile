FROM openjdk:17-jdk-slim

LABEL maintainer="miglani.sarthak27@gmail.com"
LABEL description="Calendar Booking API"

WORKDIR /app

COPY target/calendar-booking-api-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
