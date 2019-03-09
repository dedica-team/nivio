FROM openjdk:11-jre-slim

VOLUME /tmp

ADD target/nivio-0.1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]