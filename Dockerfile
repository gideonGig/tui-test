
FROM adoptopenjdk/openjdk16:alpine-jre

WORKDIR /src/main

COPY target/your-application.jar /usr/src/app/your-application.jar

EXPOSE 8080

CMD ["java", "-jar", "your-application.jar"]