FROM gradle:9.5.1-jdk21 AS build

WORKDIR /app

COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src src

RUN gradle clean bootJar --no-daemon


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]