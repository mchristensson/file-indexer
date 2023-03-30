FROM eclipse-temurin:19

RUN mkdir /opt/app

COPY target/fileindexerapp.jar /opt/app
COPY test-filestructure /opt/app/test-filestructure
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/opt/app/fileindexerapp.jar"]