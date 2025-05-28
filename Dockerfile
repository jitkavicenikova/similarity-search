FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build with Spring Boot plugin to create executable fat JAR
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the generated Spring Boot fat JAR (with proper manifest)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]