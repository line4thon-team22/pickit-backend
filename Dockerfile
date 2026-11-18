# build.gradle이 Java 21 toolchain을 쓰므로 빌드·실행 이미지 모두 21을 쓴다.
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
