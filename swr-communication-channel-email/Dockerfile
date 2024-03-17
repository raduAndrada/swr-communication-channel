FROM openjdk:21-jdk
MAINTAINER andraduradu
VOLUME /tmp
EXPOSE 8902
COPY ./target/*.jar communication-channel-email.jar
ENTRYPOINT ["java","-jar","/communication-channel-email.jar"]

