FROM openjdk:11-jre-slim

ARG JAR_FILE=./build/libs/oauth-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

COPY /src/main/resources/application.yaml application.yaml

WORKDIR /HOME/spring/

EXPOSE 8080

CMD ["bash", "-c", "java -jar ./app.jar"]
