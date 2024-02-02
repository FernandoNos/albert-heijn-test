#
# BUILD STAGE
#
FROM maven:3.8.4-openjdk-17-slim AS build
RUN mkdir /usr/src/app
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean install -DskipTests

#
# PACKAGE STAGE
#
FROM openjdk:17-jdk
COPY --from=build /usr/src/app/target/backend-assignment-sandbox-0.0.1-SNAPSHOT.jar /usr/app/backend-assignment-sandbox-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java","-jar","/usr/app/backend-assignment-sandbox-0.0.1-SNAPSHOT.jar"]