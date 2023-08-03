# Dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

ARG JAR_FILE=*.jar

COPY ${JAR_FILE} /app/application.jar

CMD java -jar /app/application.jari
