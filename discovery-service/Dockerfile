FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar discovery-service.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","discovery-service.jar"]