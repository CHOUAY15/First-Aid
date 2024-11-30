FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar participant-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","participant-service.jar"]