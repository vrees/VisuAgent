import {Component, EventEmitter, Output} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {AppState} from '../store';
import {CalibrationService} from '../services/calibration.service';

@Component({
    selector: 'app-settings',
    template: `
        <mat-card>
            <h2>Settings</h2>
            <mat-form-field appearance="fill">
                <mat-label>Auftragsnummer</mat-label>
                <input matInput maxlength="20" [(ngModel)]="orderNumber" pattern="[a-zA-Z0-9]*"
                       title="Nur Zahlen und Buchstaben">
            </mat-form-field>
            <mat-form-field appearance="fill">
                <mat-label>Equipmentnummer</mat-label>
                <input matInput maxlength="20" [(ngModel)]="equipmentNumber" pattern="[a-zA-Z0-9]*"
                       title="Nur Zahlen und Buchstaben">
            </mat-form-field>
            <mat-form-field appearance="fill">
                <mat-label>Bemerkung</mat-label>
                <textarea matInput maxlength="1000" [(ngModel)]="remark"></textarea>
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
            <button mat-raised-button color="primary" id="triggerAI" (click)="triggerAI.emit()" [disabled]="!isAITriggerEnabled()">Wert erkennen</button>
            <button mat-raised-button color="accent" id="createProjekt" (click)="createProject()"
                    [disabled]="!isProjectCreationEnabled()">Projekt anlegen
            </button>
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
            margin-bottom: 8px;
        }

        button[disabled] {
            opacity: 0.6;
            cursor: not-allowed;
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
    orderNumber = '';
    equipmentNumber = '';
    remark = '';
    private currentRoi: any = null;

    constructor(
        private readonly store: Store<AppState>,
        private readonly calibrationService: CalibrationService
    ) {
        this.roi$ = this.store.pipe(select(state => state.measurement.roi));

        // Subscribe to ROI changes to keep track of current ROI
        this.roi$.subscribe(roi => {
            this.currentRoi = roi;
        });
    }

    isAITriggerEnabled(): boolean {
        // Check if ROI is available with valid coordinates
        return this.currentRoi &&
            this.currentRoi.x >= 0 &&
            this.currentRoi.y >= 0 &&
            this.currentRoi.width > 0 &&
            this.currentRoi.height > 0;
    }

    isProjectCreationEnabled(): boolean {
        // Check if orderNumber and equipmentNumber are provided and not empty
        const hasOrderNumber = this.orderNumber && this.orderNumber.trim().length > 0;
        const hasEquipmentNumber = this.equipmentNumber && this.equipmentNumber.trim().length > 0;

        // Check if ROI is available with valid coordinates
        const hasValidRoi = this.isAITriggerEnabled(); // Reuse the ROI validation logic

        return hasOrderNumber && hasEquipmentNumber && hasValidRoi;
    }

    createProject() {
        if (!this.orderNumber || !this.equipmentNumber) {
            alert('Bitte füllen Sie beide Felder aus');
            return;
        }

        // Prepare calibration data with imageArea and remark
        const calibrationData: any = {
            id: {
                orderNumber: this.orderNumber,
                equipmentNumber: this.equipmentNumber
            }
        };

        // Add remark if provided
        if (this.remark?.trim()) {
            calibrationData.remark = this.remark.trim();
        }

        // Add imageArea (ROI coordinates) if available
        if (this.currentRoi) {
            calibrationData.imageArea = {
                x: this.currentRoi.x,
                y: this.currentRoi.y,
                width: this.currentRoi.width,
                height: this.currentRoi.height
            };
        }

        console.log('Creating calibration with data:', calibrationData);

        this.calibrationService.createCalibration(calibrationData).subscribe({
            next: (calibration) => {
                alert('Projekt erfolgreich angelegt');
                console.log('Calibration created:', calibration);

                // Keep form data after successful creation
                // this.orderNumber = '';
                // this.equipmentNumber = '';  
                // this.remark = '';
            },
            error: (error) => {
                alert('Fehler beim Anlegen des Projekts');
                console.error('Error creating calibration:', error);
            }
        });
    }
}
