#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#ADD target/voda_api.jar voda_api.jar
##EXPOSE 8080
#ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container", "-jar", "voda_api.jar"]

FROM openjdk:17-jdk-slim
ADD target/voda_api.jar voda_api.jar
ENTRYPOINT ["java","-jar","voda_api.jar"]