# VisuAgent Backend

This is the backend service for VisuAgent, built with Spring Boot 3.5.4 and Java 21.

## Environment Setup

Ensure that the environment variable `OPENAI_API_KEY` is set for OpenAI integration:

```sh
cat /etc/environment
```

## Build

```sh
./mvnw clean install
```

## Run

```sh
./mvnw spring-boot:run
```

## Test

```sh
./mvnw test
```

## API

- Video stream: `/api/stream`
- Measurement extraction: `/api/measurements`
- OpenAPI/Swagger UI: `/swagger-ui.html`

## Notes

- Uses in-memory database (H2) for demo purposes.
- Main class: `de.testo.cal.visuagent.VisuagentApplication`
- All endpoints start with `/api/`
- For integration tests, use the `integrationtest` Spring profile.
- Hardware dependencies are mocked in tests.
- OpenAI integration is optional and demo only (see `application-openai.properties`).
- set Environment Variable in /etc/environment (not in ~/.bashrc): OPENAI_API_KEY="sk-pr...."

---

Alle öffentlichen Methoden und Klassen sind auf Englisch dokumentiert, der Quellcode ist auf Englisch, Kommentare auf
Deutsch.
