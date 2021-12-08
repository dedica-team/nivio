# use not slim base image, because libfontmanager needs libfreetype (mfbieber)
FROM openjdk:17-alpine

ENV JAVA_TOOL_OPTIONS="-Xms100m -Xmx400m"
VOLUME /tmp

ADD src/test/resources/example /src/test/resources/example
ADD target/nivio.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
