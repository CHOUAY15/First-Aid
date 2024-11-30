FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar training-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","training-service.jar"]