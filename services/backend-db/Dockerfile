FROM gradle:6.5.1-jdk11 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:11.0.7-jre
COPY --from=builder /home/gradle/src/build/libs/src-all.jar /usr/jar/sample.jar
WORKDIR /usr/jar
EXPOSE 9999
CMD ["java", "-jar", "sample.jar"]