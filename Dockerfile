FROM openjdk:20

WORKDIR /usr/src/app

COPY target/github-0.0.1-SNAPSHOT.jar /usr/src/app/github.jar

EXPOSE 8080

CMD ["java", "-jar", "github.jar"]