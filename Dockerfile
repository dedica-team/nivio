# use not slim base image, because libfontmanager needs libfreetype (mfbieber)
FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.8_10

ENV JAVA_TOOL_OPTIONS="-Xms100m -Xmx400m"
VOLUME /tmp

ADD src/test/resources/example /src/test/resources/example
ADD target/nivio.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
