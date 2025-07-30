# Product Requirements Document (PRD) – VisuAgent

## 1. Überblick
**Produktname:** VisuAgent  
**Zweck:**  
VisuAgent wird im Kalibrierlabor der Firma Testo eingesetzt, um Messwerte von Prüfobjekten (Messgeräten) automatisch zu erfassen.  
Eine angeschlossene USB-Kamera liefert Live-Bilder und Videos von Messgeräte-Displays.  
Die Software erkennt die angezeigten Werte mithilfe von KI (OpenAI) und stellt sie dem Benutzer über eine Weboberfläche bereit.

## 2. Zielsetzung
- Automatische Erkennung von Messwerten und deren Einheit aus den Kameraaufnahmen, um manuelle Ablesefehler zu vermeiden.
- Effizienzsteigerung, Dokumentationssicherheit und Fehlerreduktion in Kalibrierprozessen.

## 3. Systemübersicht

### Architektur:
- **Backend:** Spring Boot 3.5.4, Java 21
- **Frontend:** Angular mit Redux (Zustandsverwaltung)
- **Kommunikation:** REST-API (`/api/...`)
- **KI:** OpenAI API (Text+Vision Modelle)

## 4. Funktionale Anforderungen

### 4.1 Hauptfunktionen
1. Videoaufnahme und Anzeige 
   - URL: `/api/stream`
   - Alternativ: Einzelne JPEG Bilder in konfigurierbaren Intervallen (z.B. 2 Bilder pro Sekunde)
2. Auswahl eines Bildbereichs (ROI) durch Benutzer
3. Extraktion des Bereichs und Übermittlung an REST-API `/api/measurements`
4. Nutzung von OpenAI, um Wert + Einheit zu erkennen
5. Anzeige des Messwertes rechts neben dem Video im Dashboard

### 4.2 Weitere Anforderungen
- API-Pfade: `/api/...`
- UI Framework: Angular Material
- Ports: Backend 8082, Frontend 4200
- Tests: JUnit (Backend), Playwright (Frontend)
- Dokumentation: README.md für Frontend und Backend

## 5. Nicht-funktionale Anforderungen
- Genauigkeit: ≥ 98 % Erkennungsrate
- Latenz: < 1 Sekunde
- Minimalistische Benutzeroberfläche

## 6. Akzeptanzkriterien
- Livestream sichtbar
- ROI auswählbar
- API liefert Messwert + Einheit
- Ergebnisse korrekt im Frontend dargestellt
- SonarQube-Integration für Codequalität

## 7. Risiken
- Schlechte Bildqualität kann zu Fehlinterpretationen führen
- OpenAI API-Latenz oder -Verfügbarkeit
- Datenschutz bei Video-/Bildübertragung

## 8. Nächste Schritte
- Github Copilot erstellt mithilfe des Agent das Grundgerüst, Tests, Deployment und CI/CD mit Github Actions
- API Design
- Erstelle API-Spezifikatio (OpenAPI/Swagger) für das Backend 
- Prototyp für Frontend/Backend


