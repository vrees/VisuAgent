# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

VisuAgent is a Spring Boot + Angular application for calibration labs to automatically extract measurement values from device displays using camera input and OpenAI vision API. The system captures live video/images from USB cameras, allows users to select regions of interest, and uses AI to recognize displayed measurement values.

## Architecture

**Backend (Spring Boot 3.5.4, Java 21)**
- Main package: `de.testo.cal.visuagent`
- Port: 8082
- Key components:
  - `MeasurementController` - REST API for measurement processing (`/api/measurements`)
  - `StreamController` - Video streaming API (`/api/stream`)
  - `OpenAiServiceWrapper` - OpenAI integration for measurement extraction
  - `MeasurementService` - Core business logic
- Dependencies: Spring Web, JPA, H2 database, OpenAI Java client, Lombok, SpringDoc OpenAPI
- Configuration in `application.properties`

**Frontend (Angular 20, TypeScript)**
- Port: 4200
- State management: NgRx (Redux pattern)
- UI Library: Angular Material
- Key components:
  - `VideoViewerComponent` - Camera display and ROI selection
  - `DashboardComponent` - Main application interface
  - `SettingsComponent` - Configuration panel
  - `PreviewComponent` - Selected region preview
- Services: `MeasurementService` for API communication

## Development Commands

### Backend
```bash
# Build and run
./mvnw clean install
./mvnw spring-boot:run

# Tests
./mvnw test

# OpenAPI documentation available at: http://localhost:8082/swagger-ui.html
```

### Frontend
```bash
cd frontend

# Install dependencies
npm ci

# Development server
npm start  # Runs on http://localhost:4200

# Build
npm run build

# Tests
npm test                    # Unit tests (Jasmine/Karma)
npm run e2e                # Playwright e2e tests
ng lint                    # Linting
```

### Full CI/CD Pipeline
GitHub Actions workflow (`.github/workflows/ci.yml`) builds and tests both backend and frontend automatically.

## Key Technical Patterns

**API Structure**
- All APIs use `/api/` prefix
- Main endpoint: `POST /api/measurements` - accepts image data and returns recognized measurement values
- Streaming endpoint: `/api/stream` - provides camera video feed

**OpenAI Integration**
- Uses `openai-java` client library
- Requires environment variables:
  - `OPENAI_API_KEY`
  - `OPENAI_API_URL` 
- Implements structured responses with `MeasurementResult` model
- Image processing with base64 encoding

**State Management**
- Frontend uses NgRx for application state
- Measurement coordinates and results stored in Redux store
- Session persistence for ROI selection

**Testing Strategy**
- Backend: JUnit tests with Mockito for external dependencies
- Frontend: Jasmine/Karma for unit tests, Playwright for e2e tests
- Hardware dependencies (cameras) are mocked in tests

## Configuration

**Backend Environment Variables**
- `OPENAI_API_KEY` - Required for AI functionality
- `OPENAI_API_URL` - OpenAI API endpoint
- Database: H2 in-memory (configured in application.properties)

**Frontend Environment**
- Development: `src/environments/environment.ts`
- Production: `src/environments/environment.prod.ts`

## Code Conventions

**Java**
- Package structure: `de.testo.cal.visuagent.*`
- Use Lombok for reducing boilerplate
- SpringDoc for OpenAPI documentation
- German comments in code (per project requirements)

**Angular**
- Components: kebab-case filenames, PascalCase class names
- Services in dedicated `services/` directory
- NgRx store structure in `store/` directory
- TypeScript strict mode enabled

## Important Notes

- The application is designed for industrial calibration lab use
- Hardware integration with USB cameras is required for full functionality
- OpenAI API costs should be monitored in production
- Security: Never commit API keys or sensitive configuration
- The codebase includes comprehensive error handling for camera and API failures