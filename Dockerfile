FROM openjdk:17-jdk-slim
LABEL authors="shoira"

COPY build/libs/TransactionGatewayExchange-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]