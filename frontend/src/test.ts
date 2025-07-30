// Dies ist die Einstiegspunkt-Datei für Angular Unit-Tests.
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

// Test-Kontext für alle .spec.ts Dateien laden
const context = require.context('./', true, /\.spec\.ts$/);

// Testumgebung initialisieren
declare const require: {
  context(path: string, deep?: boolean, filter?: RegExp): {
    keys(): string[];
    <T>(id: string): T;
  };
};

getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

context.keys().map(context);
