
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY ./pom.xml .

RUN mvn dependency:go-offline

COPY src src

RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/auth-0.0.1-SNAPSHOT.jar /app/auth-0.0.1-SNAPSHOT.jar

COPY src/main/resources/database/advocacia.db /app/src/main/resources/database/advocacia.db

ENV DB_PATH=/app/src/main/resources/database/advocacia.db

CMD ["java", "-jar", "auth-0.0.1-SNAPSHOT.jar"]