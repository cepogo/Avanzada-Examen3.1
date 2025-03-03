FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /build
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]