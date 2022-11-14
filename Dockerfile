FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 80:80
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/wikipedia-links-preview.jar
ENTRYPOINT ["java","-jar","/app/wikipedia-links-preview.jar"]
