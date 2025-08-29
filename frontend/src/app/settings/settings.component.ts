import {Component, EventEmitter, Output} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {AppState} from '../store';

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
            <div *ngIf="roi$ | async as roi" class="roi-fields">
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
    styles: [`
        :host {
            display: block;
        }

        mat-card {
            font-size: 16px;
        }

        mat-card h2 {
            font-size: 24px;
            margin-bottom: 20px;
        }

        mat-form-field {
            width: 100%;
            margin-bottom: 16px;
            font-size: 18px;
        }

        mat-form-field mat-label {
            font-size: 18px;
            font-weight: 500;
        }

        mat-form-field input,
        mat-form-field textarea {
            color: #000000 !important;
            font-size: 26px !important;
            font-weight: 500 !important;
            background: rgba(255, 255, 255, 0.9) !important;
        }

        mat-form-field input[readonly] {
            color: #333333 !important;
            background: rgba(255, 255, 255, 0.7) !important;
            font-weight: 600 !important;
        }

        button {
            font-size: 16px;
            padding: 12px 24px;
            margin-top: 16px;
            width: 100%;
        }

        .roi-fields {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin: 20px 0;
        }
    `],
    standalone: false
})
export class SettingsComponent {
    @Output() triggerAI = new EventEmitter<void>();
    roi$;

    constructor(readonly private store: Store<AppState>) {
        this.roi$ = this.store.pipe(select(state => state.measurement.roi));
    }
}
