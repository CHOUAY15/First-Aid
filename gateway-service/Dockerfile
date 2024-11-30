FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar gateway-service.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","gateway-service.jar"]