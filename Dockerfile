FROM jugglypuff/mvn-deps:1.0
WORKDIR /app
COPY . .
RUN mvn package -Dmaven.test.skip
WORKDIR /app/target
COPY .env .
RUN mv *-jar-with-dependencies.jar executable.jar
ENTRYPOINT ["java", "-jar", "executable.jar"]