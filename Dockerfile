FROM openjdk:11

CMD ["./gradlew", "clean", "package"]

ARG JAR_FILE=./build/libs/oauth-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
