FROM maven:3.9.5-amazoncorretto-17-debian
WORKDIR /app
COPY . .
RUN mvn package
WORKDIR /app/target
COPY .env .
ENTRYPOINT ["java", "-jar", "mavenedu-1.0-SNAPSHOT-jar-with-dependencies.jar"]