FROM amazoncorretto:21-alpine-jdk

COPY target/demo-web-registration-system-0.0.1-SNAPSHOT.jar /service-demo-matricula.jar

ENTRYPOINT ["java","-jar","/service-demo-matricula.jar"]