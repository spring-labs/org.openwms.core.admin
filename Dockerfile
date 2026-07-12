FROM --platform=$BUILDPLATFORM bellsoft/liberica-openjre-alpine:25-cds AS builder
WORKDIR /application
ARG JAR_FILE=target/openwms-core-admin-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted

FROM bellsoft/liberica-openjre-alpine:25-cds
WORKDIR /application
COPY --from=builder /application/extracted/dependencies/ ./
COPY --from=builder /application/extracted/spring-boot-loader/ ./
COPY --from=builder /application/extracted/snapshot-dependencies/ ./
COPY --from=builder /application/extracted/application/ ./
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "org.springframework.boot.loader.launch.JarLauncher"]
