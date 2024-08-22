FROM maven:3.8.4-openjdk-17-slim as build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE ${API_LISTENING_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]