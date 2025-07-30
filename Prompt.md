You are an autonomous software engineer.

Goal:
Based on the provided PRD document 'VisuAgent.md', generate a new, complete production-ready application:

1. **Backend**:
    - Java 17
    - Spring Boot (REST API)
    - JPA/Hibernate for persistence (use in-memory DB for demo)
    - Structured, layered architecture with clear packages
    - provide suitable .gitignore file

2. **Frontend**:
    - Angular (latest LTS)
    - Connect to the REST API
    - Include basic routing and a modular structure

3. **Tests**:
    - Backend: JUnit and integration tests for main endpoints
    - Frontend: Angular unit tests with Karma/Jasmine
    - Make sure all tests run automatically in CI

4. **CI/CD**:
    - Create GitHub Actions workflows:
        - Build backend and run backend tests
        - Build frontend and run frontend tests
        - Combine in a single workflow on push and PR
    - Provide instructions to extend for deployment later.

5. **Repository**:
    - Organize code in folders: `/backend` and `/frontend`
    - Include a clear README.md with:
        - How to build/run backend
        - How to build/run frontend
        - How to run tests
        - How to deploy (if applicable)

6. **Delivery**:
    - Generate the full project structure and commit to a new GitHub repository.
    - After generation, run the CI tests and ensure a passing pipeline.

Inputs:
- Use the uploaded PRD document as the specification for endpoints, UI modules and data models.

Additional instructions:
- Use English for code and comments.
- Make the code idiomatic and clean.
- If something is unclear in the PRD, choose reasonable defaults and document assumptions in the README.

Output:
Push the completed project structure, code, tests, workflows and README into the repository.
