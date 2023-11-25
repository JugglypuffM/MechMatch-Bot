FROM mymaven
WORKDIR /app
COPY . .
RUN mvn package -Dmaven.test.skip
WORKDIR /app/target
COPY .env .
ENTRYPOINT ["java", "-jar", "mavenedu-1.0-SNAPSHOT-jar-with-dependencies.jar"]