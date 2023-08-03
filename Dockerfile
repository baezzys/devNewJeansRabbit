# Dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

ARG JAR_FILE=./build/libs/*.jar

COPY ${JAR_FILE} /app/application.jar

CMD java -jar /app/application.jar
