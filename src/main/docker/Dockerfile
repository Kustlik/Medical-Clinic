FROM openjdk:17-jdk-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} medical-clinic-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/medical-clinic-0.0.1-SNAPSHOT.jar"]