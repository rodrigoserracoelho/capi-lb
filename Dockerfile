FROM openjdk:14
VOLUME /tmp
ARG JAR_FILE
COPY target/capi-lb-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
