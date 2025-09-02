## VisuAgent Application

Bitte erstelle eine Spring Boot Anwendung mit Maven mit dem Namen 'VisuAgent'. 
Die Anwendung soll im Kalibrierlabor der Firma Testo verwendet werden um Messwerte von Prüfobjekten zu lesen und zu erkennen. 
Eine Kamera, die per USB angeschlossen wird, soll dazu Bilder und Videos von den Displays verschiedener Prüfobjekte (Messgeräte) aufnehmen. VideoUrl: "/api/stream"
Die Anwendung soll eine einfache Benutzeroberfläche haben, die es dem Benutzer ermöglicht das Video der Kamera darzustellen und die Ergebnisse anzuzeigen.
Als Frontend soll das Framework Angular UI verwendet werden. 

Falls es zu aufwendig ist ein Video darzustellen, können auch einzelne Bilder verwendet werden, die in regelmässigen Abständen aufgenommen werden. 
Dabei sollte die Frequenz, in der die Bilder dargestellte werden, konfigurierbare sein, zum Beispiel 2 Bilder pro Sekunde

Der Benutzer soll ein Bereich des Bildes auswählen können, in dem der Messwert angezeigt wird. Die Koordinaten des markierten Bereichs sollen in der Session gespeichert werden.
Dazu soll Redux verwendet werden, um den Zustand der Anwendung zu verwalten.

Der markierte Bildausschnitte wird dann extrahiert und an ans Spring Boot Backend gesendet. 
Die Anwendung soll eine REST-API (Path '/api/measurements') bereitstellen, die den markierten Bildausschnitt entgegennimmt und an eine KI gesendet, die den Messwert erkennt und zurückliefert.

Als KI soll OpenaAi Platform verwendet werden: https://api.openai.com/... 
Zusammen mit dem Bildausschnitt und dem Prompt 'Extract the measurement value and unit from the image' wird die KI den Messwert und die Einheit erkennen und zurückliefern.
In der Antwort soll die KI dann den Messwert und die Messgrösse zurückliefern, zum Beispiel '23.5 Newton' oder '12.3 Volt' oder '1.2 bar'.
Dieser Wert soll dann im unteren rechten Teil des Dashboards angezeigt werden.

Zusätzliche Anforderungen:
- Verwende eine allgemein gut verfügbare und einfache Angular UI Library
- all notwendigen API sollen mit  /api/... beginnen
- verwende bei der Code-Erstellung englische Begriffe, Namen und Dokumentation
- verwende gängige gut verfügbare Libraries, idealerweise OpenSource
- verwende aktuelle Versionen der Libraries
- verwende Java 21
- verwende Spring Boot 3.5.4
- erstelle sowohl für Frontend als auch für das Backend eine README.md Datei mit den wichtigsten Informationen zur Installation und zum Starten der Anwendung
- die Anwendung soll in Deutsch kommentiert sein
- die Anwendung soll in der Lage sein, Bilder und Videos von der Kamera aufzunehmen und darzustellen
- die Anwendung soll in der Lage sein, den markierten Bildausschnitt zu extrahieren und an die REST-API zu senden
- die Anwendung soll in der Lage sein, die Antwort der KI zu empfangen und anzuzeigen
- die Anwendung soll in der Lage sein, den Zustand der Anwendung mit Redux zu verwalten
- die Anwendung soll in der Lage sein, die Koordinaten des markierten Bereich des Bildes in der Session zu speichern
- die Anwendung soll eine einfache Benutzeroberfläche haben, die es dem Benutzer ermöglicht, das Video der Kamera darzustellen und die Ergebnisse anzuzeigen
- Erstelle JUnit Tests für das Backend 
- Erstelle Playwrite für das Frontend (https://playwright.dev/)
- Port für das Backend: 8082
- Port für das Frontend: 4200
- Frontend und Backend sollen im gleichen Repo liegen. Das Frontend soll im Ordner 'frontend' und das Backend soll root folder sein.
- Das Frontend soll 2 componenten haben: 
- eine Komponente für die Anzeige des Videos der Kamera
- eine Komponente mit dem Namen 'Einstellungen'. Diese Seite ist derzeit noch leer, aber sie soll später für die Einstellungen der Anwendung verwendet werden.
- Erstelle eine CI/CD Pipeline mit GitHub Actions, die das Backend und das Frontend baut und testet.

---

# Build & Run Instructions

## Backend (Spring Boot)

- **Build:**
  ```sh
  ./mvnw clean install
  ```
- **Run:**
  ```sh
  ./mvnw spring-boot:run
  ```
- **API Doku:**
  OpenAPI-Spezifikation: `backend-openapi.yaml`

## Frontend (Angular)

- **Install dependencies:**
  ```sh
  cd frontend
  npm ci
  ```
- **Run development server:**
  ```sh
  npm start
  ```
  (Standard: http://localhost:4200)

## Tests

- **Backend:**
  ```sh
  ./mvnw test
  ```
- **Frontend:**
  ```sh
  cd frontend
  npm test
  ```

## CI/CD

- GitHub Actions Workflow unter `.github/workflows/ci.yml` baut und testet Backend & Frontend automatisch bei jedem Push/PR.
- Für Deployment kann der Workflow um weitere Schritte ergänzt werden (z.B. Docker, Cloud Deploy).

---

# Projektstruktur
- Backend: Java 21, Spring Boot 3.5.4, REST API, OpenAPI, JUnit
- Frontend: Angular, Redux, Angular Material, Playwright
- Siehe PRD (VisuAgent.md) für Details zu Anforderungen und Architektur.
