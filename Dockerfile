FROM gradle:7.5.1-jdk17-alpine as build

COPY . /appl

WORKDIR /appl

RUN gradle build --info

FROM eclipse-temurin:17.0.5_8-jre

WORKDIR /appl

COPY --from=build /appl/build/libs/hogwarts-bot-0.0.1-SNAPSHOT.jar hogwarts-bot-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "hogwarts-bot-0.0.1-SNAPSHOT.jar"]
