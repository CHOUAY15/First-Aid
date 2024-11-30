FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar discovery-service.jar
EXPOSE 8761
RUN apt-get update && apt-get install -y wget
ENTRYPOINT ["java","-jar","discovery-service.jar"]