// Einstiegspunkt für Angular Unit-Tests (Angular CLI Standard)
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

// Alle .spec.ts-Dateien automatisch laden (Angular 16+)
// Workaround für fehlende Typdefinition von import.meta.glob
// @ts-ignore
const allSpecFiles = import.meta.glob('./**/*.spec.ts');
for (const path in allSpecFiles) {
  // @ts-ignore
  allSpecFiles[path]();
}
