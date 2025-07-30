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
// Einstiegspunkt für Angular Unit-Tests (klassisch, kompatibel mit Karma/Webpack)
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

declare const require: any;
// Alle .spec.ts-Dateien im Projekt laden
const context = require.context('./', true, /\.spec\.ts$/);

getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

context.keys().map(context);
