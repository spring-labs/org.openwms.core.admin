# OpenWMS CORE Admin UI
This monitoring application is based on [Spring Boot Admin Server](https://github.com/codecentric/spring-boot-admin) and can be used to 
visualize the state of a distributed microservice application and monitor and control single services.

It is part of the OpenWMS.org CORE domain because it can also be used outside logistics projects and belongs to the technical infrastructure
components of a typical microservice application built with Spring Boot.

The service is built with Spring Boot 4.1 / Spring Boot Admin 4.1 and runs on Java 25 (BellSoft Liberica). Metrics of monitored
services are read via `/actuator/metrics` and log levels are changed via `/actuator/loggers`. The service itself exposes Prometheus
metrics at `/actuator/prometheus` and exports traces via OTLP to the collector configured with `owms.tracing.url`.

## Build

A JDK 25 and Maven 3.9+ are required to build:

```
./mvnw verify
```

The Docker image is based on `bellsoft/liberica-openjre-alpine:25-cds`. The CI pipeline builds and pushes it as a multi-arch image for
`linux/amd64` and `linux/arm64` with Docker Buildx, published under a single tag (`interface21/org.openwms.core.admin:<version>`), so
Docker pulls the matching architecture automatically. A local single-arch image can be built with:

```
./scripts/docker_build <version>
```

![overview][1]

The main purpose in production is to control the log level of single components and categories as well as controlling caches. Viewing the
application logs is usually done with a centralized log monitoring system.

Additional configuration parameter has been added or preset to the configuration parameters of [Spring Boot Admin Server](https://github.com/codecentric/spring-boot-admin):

| Configuration parameter name | Default value                   | Description                                                                                         |
|------------------------------|---------------------------------|-----------------------------------------------------------------------------------------------------|
| `owms.admin.start-page`      | `/wallboard`                    | The existing wallboard page shall be the start page after login. Possible values ``, `/application` |
| `owms.eureka.hostname`       | `localhost`                     | Hostname where the discovery server is running                                                      |
| `owms.eureka.port`           | `8761`                          | Port of the discovery server instance                                                               |
| `owms.eureka.url`            | `http://user:sa@localhost:8761` | URI to connect to the discovery server - a combination or hostname, port, username and password     |
| `owms.eureka.user.name`      | `user`                          | User's name to access the discovery server                                                          |
| `owms.eureka.user.password`  | `sa`                            | User's password to access to the discovery server                                                   |
| `owms.eureka.zone`           | `${owms.eureka.url}/eureka/`    | URI to get the zone settings from Eureka discovery server                                           |
| `owms.srv.hostname`          | `localhost`                     | Hostname that is used to register the Admin UI at the discovery server                              |
| `owms.srv.protocol`          | `http`                          | Port that is used to register the Admin UI at the discovery server                                  |
| `owms.tracing.url`           | `http://localhost:4317`         | URL to the OpenTelemetry server (only defined with the `OTLP` profile)                              |
| `server.port`                | `${PORT:8155}`                  | Port where the Admin UI web server listening at                                                     |

Supported Spring profiles:

| Profile name | Description                                                                                                                |
|--------------|----------------------------------------------------------------------------------------------------------------------------|
| `ELK`        | All logs and traces are pushed to Logstash (via syslog) expected to listen on a server with hostname `elk` and port `5000` | 
| `LOKI`       | All logs and traces are pushed to a Loki server (expected to listen on a server with hostname `loki` and port `3100`       | 
| `OTLP`       | Enables distributed tracing: spans are exported via OTLP/gRPC to the collector configured with `owms.tracing.url`          | 

[1]: src/site/resources/images/overview.png
