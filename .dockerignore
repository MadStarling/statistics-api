FROM openjdk:11-jdk-slim-stretch as build

WORKDIR /app
COPY . .
RUN ./gradlew build -x test

FROM openjdk:11-jdk-slim-stretch

WORKDIR /app
COPY --from=build /app/build/libs/ .
EXPOSE 8080

CMD java -jar $(ls | grep -m1 .jar)