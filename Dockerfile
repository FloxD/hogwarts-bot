FROM gradle:7.5.1-jdk17-alpine

COPY . /appl

WORKDIR /appl

RUN gradle build --info

ENTRYPOINT ["java", "-jar", "build/libs/hogwarts-bot-0.0.1-SNAPSHOT.jar"]
