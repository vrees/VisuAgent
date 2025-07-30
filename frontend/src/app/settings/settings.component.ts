import { Component, Output, EventEmitter } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { AppState } from '../store';

@Component({
  selector: 'app-settings',
  template: `
    <mat-card>
      <h2>Settings</h2>
      <mat-form-field appearance="fill">
        <mat-label>Prüfmittel</mat-label>
        <input matInput maxlength="50">
      </mat-form-field>
      <mat-form-field appearance="fill">
        <mat-label>Bemerkung</mat-label>
        <textarea matInput maxlength="1000"></textarea>
      </mat-form-field>
      <div *ngIf="roi$ | async as roi">
        <mat-form-field appearance="fill">
          <mat-label>X</mat-label>
          <input matInput [value]="roi?.x" readonly>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Y</mat-label>
          <input matInput [value]="roi?.y" readonly>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Width</mat-label>
          <input matInput [value]="roi?.width" readonly>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Height</mat-label>
          <input matInput [value]="roi?.height" readonly>
        </mat-form-field>
      </div>
      <button mat-raised-button color="primary" id="triggerAI" (click)="triggerAI.emit()">Wert erkennen</button>
    </mat-card>
  `,
  styles: [':host { display: block; }']
})
export class SettingsComponent {
  @Output() triggerAI = new EventEmitter<void>();
  roi$ = this.store.pipe(select(state => state.measurement.roi));
  constructor(private store: Store<AppState>) {}
}
