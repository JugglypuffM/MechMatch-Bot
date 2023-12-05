FROM jugglypuff/mvn-deps:1.0
WORKDIR /app
COPY . .
RUN mvn package -Dmaven.test.skip
WORKDIR /app/target
COPY .env .
ENTRYPOINT ["java", "-jar", "*-jar-with-dependencies.jar"]