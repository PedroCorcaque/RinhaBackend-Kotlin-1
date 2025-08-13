FROM gradle:8.7-jdk21 AS build
WORKDIR /home/gradle/project
COPY . .
RUN ./gradlew :app:shadowJar --no-daemon

FROM registry.access.redhat.com/ubi9/openjdk-21:1.21
ENV LANGUAGE='en_US:en'

WORKDIR /deployments

COPY --chown=185 --from=build /home/gradle/project/app/build/libs/app-all.jar /deployments/app.jar

EXPOSE 9999
USER 185
ENV JAVA_APP_JAR="/deployments/app.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]