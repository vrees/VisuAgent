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




// Fallback: Klassische require.context-Variante für Angular CLI/Karma
declare const require: any;
const context = require.context('./', true, /\.spec\.ts$/);
context.keys().map(context);
