FROM --platform=$BUILDPLATFORM bellsoft/liberica-openjre-alpine:25-cds AS builder
WORKDIR /builder
ARG JAR_FILE=target/openwms-core-admin-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM bellsoft/liberica-openjre-alpine:25-cds
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
# CDS training run: exits on context refresh and writes the archive used at runtime
RUN java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh \
    -Deureka.client.enabled=false -jar application.jar
ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa", "--enable-native-access=ALL-UNNAMED", "-jar", "application.jar"]
