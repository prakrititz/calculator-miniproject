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

## Docker, Compose and Jenkins

_Coming as the pipeline is built._
