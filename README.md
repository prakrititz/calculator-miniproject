# Calculator Mini Project

A small Spring Boot REST calculator, wrapped in a CI/CD pipeline (Git, Jenkins, Docker, Docker Compose).
Mini-project for CSE 816 Software Production Engineering.

**Stack:** Java 17, Spring Boot 4, Maven, JUnit 5.

## Build and test

```bash
mvn clean package        # compiles, runs tests, builds target/calculator-miniproject-1.0.0.jar
mvn test                 # runs the unit tests and MockMvc integration tests
```

Test reports (JUnit XML) are written to `target/surefire-reports/`.

## Run locally

```bash
mvn spring-boot:run
```

or run the packaged JAR:

```bash
java -jar target/calculator-miniproject-1.0.0.jar
```

The app listens on <http://localhost:8080>.

## API

All operations are `GET` requests with two query parameters, `a` and `b`.

| Method | Path | Example |
|--------|------|---------|
| GET | `/api/v1/add?a=&b=` | `/api/v1/add?a=2&b=3` |
| GET | `/api/v1/subtract?a=&b=` | `/api/v1/subtract?a=5&b=2` |
| GET | `/api/v1/multiply?a=&b=` | `/api/v1/multiply?a=4&b=3` |
| GET | `/api/v1/divide?a=&b=` | `/api/v1/divide?a=10&b=2` |
| GET | `/actuator/health` | health probe (`UP` / `DOWN`) |

### Success response

`GET /api/v1/add?a=2&b=3`

```json
{"operation":"add","a":2.0,"b":3.0,"result":5.0}
```

### Error responses

Invalid input returns HTTP `400` with a structured body.

`GET /api/v1/divide?a=10&b=0`

```json
{
  "timestamp": "2026-10-29T17:26:28.177Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot divide by zero",
  "path": "/api/v1/divide"
}
```

Other cases handled the same way:

| Request | Message |
|---------|---------|
| `/api/v1/add?a=2` | `Missing required parameter: b` |
| `/api/v1/add?a=foo&b=3` | `Invalid value for parameter 'a': must be a number` |

## Project layout

```
src/main/java/com/example/calculator_miniproject/
  CalculatorMiniprojectApplication.java   entry point
  controller/CalculatorController.java    REST endpoints
  service/CalculatorService.java          the arithmetic
  dto/                                    CalculationResponse, ApiError
  exception/                              DivideByZeroException, GlobalExceptionHandler
src/test/java/com/example/calculator_miniproject/
  CalculatorServiceTest.java              unit tests
  CalculatorControllerIT.java             MockMvc integration tests
```

## Docker

The `Dockerfile` is a multi-stage build: a Maven + JDK 17 stage compiles the JAR, and a small
`eclipse-temurin:17-jre-alpine` stage runs it as a non-root user.

```bash
docker build -t calculator-miniproject:1.0 .
docker run -d -p 8080:8080 --name calc calculator-miniproject:1.0
```

Open <http://localhost:8080/actuator/health>. Stop it with `docker rm -f calc`.

## Docker Compose

`docker-compose.yml` starts two services on a private network (`app-net`):

| Service | Image | Role |
|---------|-------|------|
| `calculator` | `calculator-miniproject:1.0` | the Spring Boot app (port 8080, internal only), health-checked via `/actuator/health` |
| `nginx` | `nginx:alpine` | reverse proxy and the only public entry point (port 80), configured by `nginx/nginx.conf` |

```bash
docker compose up -d     # start the whole stack
docker compose ps        # calculator should show (healthy)
docker compose down      # stop and remove it
```

Then call the API through Nginx, with no port needed: <http://localhost/api/v1/add?a=2&b=3>

## Jenkins

_Coming as the pipeline is built._
