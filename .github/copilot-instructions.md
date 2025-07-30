# Copilot Instructions für visuagent

Willkommen im visuagent-Projekt!

Dieses Dokument enthält Anweisungen und Konventionen für die Nutzung von GitHub Copilot in diesem Repository. Bitte beachte die folgenden Hinweise, um eine konsistente und effiziente Zusammenarbeit zu gewährleisten.

## Projektüberblick
- **Projektname:** visuagent
- **Repository:** visuagent
- **Backend:** Spring Boot (Java)
- **Frontend:** Angular

## Namenskonventionen
- Java-Klassen und -Pakete: `de.testo.cal.visuagent...`
- Angular-Komponenten: `kebab-case` für Dateinamen, `PascalCase` für Klassennamen

## Backend (Spring Boot)
- Konfigurationsdateien liegen unter `src/main/resources/`
- Hauptklasse: `VisuSnapApplication.java` (bitte in visuagent umbenennen, falls ein neues Projekt erstellt wird)
- Tests liegen unter `src/test/java/de/testo/cal/visuagent/`
- Für Integrationstests ein eigenes Spring-Profil `integrationtest` verwenden
- Hardware-Abhängigkeiten (z.B. Kamera) in Tests mit Mockito mocken

## Frontend (Angular)
- Quellcode unter `frontend/src/app/`
- Services und Models im jeweiligen Unterordner ablegen
- Umgebungsvariablen in `frontend/src/environments/` pflegen

## Codequalität
- Unbenutzte Imports entfernen (z.B. mit automatischen Funktionen in der IDE)
- SonarQube-Warnungen und -Fehler beheben
- Tests müssen kompilierbar und lauffähig sein (Mocks für externe Abhängigkeiten verwenden)

## Best Practices
- Schreibe für neue Features und Bugfixes immer Unit- und ggf. Integrationstests
- Nutze Dependency Injection für Services und Komponenten
- Dokumentiere öffentliche Methoden und Klassen mit JavaDoc bzw. TypeDoc

## Hinweise für Copilot-Nutzer
- Generiere Code immer im Kontext der bestehenden Architektur und Konventionen
- Bei Unsicherheiten zu Namensgebung oder Struktur: Siehe bestehende Dateien als Vorlage
- Erstelle keine sensiblen Daten oder Zugangsdaten im Code

## Weitere Informationen
- Lies die README.md für Build- und Startanweisungen
- Bei Fragen zu Architektur oder Prozessen: Siehe Prompt.md oder VisuAgent.md

Viel Erfolg beim Entwickeln mit visuagent!

