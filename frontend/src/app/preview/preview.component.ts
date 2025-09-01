import { Component, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { AppState } from '../store';
import { combineLatest, Subscription } from 'rxjs';
import { Actions, ofType } from '@ngrx/effects';
import { refreshPreview } from '../store/measurement.actions';

@Component({
    selector: 'app-preview',
    template: `
    <mat-card>
      <h2>Preview</h2>
      <div style="min-height: 250px; background: rgba(255, 255, 255, 0.1); display: flex; align-items: center; justify-content: center; flex-direction: column; padding: 20px; border-radius: 8px;">
        <div *ngIf="roiImageUrl; else noRoi" style="text-align: center; width: 100%;">
          <img [src]="roiImageUrl" style="max-width: 100%; max-height: 120px; border: 2px solid rgba(255, 255, 255, 0.3); border-radius: 8px; margin-bottom: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.2);" alt="Selected ROI">
          
          <div class="result-section">
            <h3 class="result-title">
              {{ currentExternalMeasurement ? 'Externer Trigger' : 'Erkannter Wert' }}
            </h3>
            <div class="result-value">
              <ng-container *ngIf="getDisplayValue() !== null; else processingOrNoResult">
                {{ getDisplayValue() }}
              </ng-container>
              <ng-template #processingOrNoResult>
                ?
              </ng-template>
            </div>
            <div class="confidence-section" *ngIf="getDisplayConfidence() !== null">
              <span class="confidence-label">Konfidenz:</span>
              <span class="confidence-value" [ngClass]="getConfidenceClass(getDisplayConfidence()!)">
                {{ (getDisplayConfidence()! * 100) | number:'1.0-0' }}%
              </span>
            </div>
            <div class="external-info" *ngIf="currentExternalMeasurement">
              <div class="info-row">
                <span class="info-label">Auftragsnummer:</span>
                <span class="info-value">{{ currentExternalMeasurement.orderNumber }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Gerätenummer:</span>
                <span class="info-value">{{ currentExternalMeasurement.equipmentNumber }}</span>
              </div>
              <div class="info-row" *ngIf="currentExternalMeasurement.triggerSource">
                <span class="info-label">Quelle:</span>
                <span class="info-value">{{ currentExternalMeasurement.triggerSource }}</span>
              </div>
              <div class="info-row" *ngIf="currentExternalMeasurement.timestamp">
                <span class="info-label">Zeit:</span>
                <span class="info-value">{{ formatTimestamp(currentExternalMeasurement.timestamp) }}</span>
              </div>
            </div>
          </div>
        </div>
        <ng-template #noRoi>
          <div class="no-roi">
            <span>Wählen Sie einen Bereich im Live-Stream aus</span>
          </div>
        </ng-template>
      </div>
    </mat-card>
  `,
    styles: [`
      :host { 
        display: block; 
      }
      
      .result-section {
        background: rgba(0, 0, 0, 0.8);
        border-radius: 12px;
        padding: 20px;
        margin-top: 15px;
        border: 2px solid #333;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
        backdrop-filter: blur(10px);
      }
      
      .result-title {
        color: #ffffff;
        font-size: 18px;
        font-weight: 600;
        margin: 0 0 15px 0;
        text-transform: uppercase;
        letter-spacing: 1px;
        text-align: center;
        text-shadow: 0 2px 4px rgba(0,0,0,0.5);
      }
      
      .result-value {
        color: #000000;
        background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
        font-size: 32px;
        font-weight: bold;
        font-family: 'Courier New', monospace;
        padding: 15px 25px;
        border-radius: 8px;
        border: 3px solid #333;
        text-align: center;
        box-shadow: inset 0 2px 4px rgba(0,0,0,0.1);
        min-height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        letter-spacing: 2px;
      }
      
      .no-roi {
        color: rgba(255, 255, 255, 0.8);
        font-size: 18px;
        text-align: center;
        font-weight: 500;
      }
      
      .confidence-section {
        margin-top: 15px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 15px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 6px;
        border: 1px solid rgba(255, 255, 255, 0.2);
      }
      
      .confidence-label {
        color: rgba(255, 255, 255, 0.9);
        font-size: 14px;
        font-weight: 500;
      }
      
      .confidence-value {
        font-size: 16px;
        font-weight: bold;
        padding: 4px 8px;
        border-radius: 4px;
      }
      
      .confidence-high {
        color: #4caf50;
        background: rgba(76, 175, 80, 0.2);
      }
      
      .confidence-medium {
        color: #ff9800;
        background: rgba(255, 152, 0, 0.2);
      }
      
      .confidence-low {
        color: #f44336;
        background: rgba(244, 67, 54, 0.2);
      }
      
      .external-info {
        margin-top: 15px;
        padding: 15px;
        background: rgba(0, 100, 200, 0.1);
        border-radius: 8px;
        border: 1px solid rgba(0, 100, 200, 0.3);
      }
      
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
      }
      
      .info-row:last-child {
        margin-bottom: 0;
      }
      
      .info-label {
        color: rgba(255, 255, 255, 0.8);
        font-size: 12px;
        font-weight: 500;
      }
      
      .info-value {
        color: #ffffff;
        font-size: 13px;
        font-weight: 600;
        font-family: 'Courier New', monospace;
      }
    `],
    standalone: false
})
export class PreviewComponent implements OnDestroy {
  @ViewChild('hiddenCanvas') hiddenCanvasRef!: ElementRef<HTMLCanvasElement>;
  
  value$;
  confidence$;
  roi$;
  externalMeasurement$;
  roiImageUrl: string | null = null;
  currentValue: number | null = null;
  currentConfidence: number | null = null;
  currentExternalMeasurement: any = null;
  private subscription = new Subscription();

  constructor(private store: Store<AppState>, private actions$: Actions) {
    this.value$ = this.store.pipe(select(state => state.measurement.value));
    this.confidence$ = this.store.pipe(select(state => state.measurement.confidence));
    this.roi$ = this.store.pipe(select(state => state.measurement.roi));
    this.externalMeasurement$ = this.store.pipe(select(state => state.measurement.externalMeasurement));
    
    // Value and confidence subscriptions
    this.subscription.add(
      this.value$.subscribe(value => {
        this.currentValue = value;
        console.log('Value updated:', value);
      })
    );
    
    this.subscription.add(
      this.confidence$.subscribe(confidence => {
        this.currentConfidence = confidence;
        console.log('Confidence updated:', confidence);
      })
    );
    
    // External measurement subscription
    this.subscription.add(
      this.externalMeasurement$.subscribe(externalMeasurement => {
        this.currentExternalMeasurement = externalMeasurement;
        console.log('External measurement updated:', externalMeasurement);
      })
    );
    
    // ROI-Änderungen überwachen und Preview aktualisieren
    this.subscription.add(
      this.roi$.subscribe(roi => {
        if (roi) {
          this.updateRoiPreview(roi);
        } else {
          this.roiImageUrl = null;
        }
      })
    );

    // Auf refreshPreview Action reagieren
    this.subscription.add(
      this.actions$.pipe(ofType(refreshPreview)).subscribe(() => {
        // Aktuellen ROI aus dem Store holen und Preview aktualisieren
        this.store.pipe(select(state => state.measurement.roi)).subscribe(roi => {
          if (roi) {
            this.updateRoiPreview(roi);
          }
        }).unsubscribe();
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    if (this.roiImageUrl) {
      URL.revokeObjectURL(this.roiImageUrl);
    }
  }

  private updateRoiPreview(roi: { x: number, y: number, width: number, height: number }) {
    // Stream-Bild laden und ROI extrahieren
    const img = new Image();
    img.crossOrigin = 'anonymous';
    img.onload = () => {
      const canvas = document.createElement('canvas');
      canvas.width = roi.width;
      canvas.height = roi.height;
      const ctx = canvas.getContext('2d');
      
      if (ctx) {
        // ROI aus dem Stream-Bild extrahieren
        ctx.drawImage(
          img,
          roi.x, roi.y, roi.width, roi.height,
          0, 0, roi.width, roi.height
        );
        
        // Altes URL freigeben
        if (this.roiImageUrl) {
          URL.revokeObjectURL(this.roiImageUrl);
        }
        
        // Neues Blob erstellen und URL generieren
        canvas.toBlob(blob => {
          if (blob) {
            this.roiImageUrl = URL.createObjectURL(blob);
          }
        }, 'image/jpeg', 0.8);
      }
    };
    
    img.onerror = () => {
      console.error('Failed to load stream image for ROI preview');
    };
    
    // Aktuelles Stream-Bild laden
    img.src = `/api/stream?t=${Date.now()}`;
  }

  getConfidenceClass(confidence: number): string {
    if (confidence >= 0.8) return 'confidence-high';
    if (confidence >= 0.5) return 'confidence-medium';
    return 'confidence-low';
  }

  getDisplayValue(): number | null {
    // External measurements have priority over manual measurements
    if (this.currentExternalMeasurement && 
        this.currentExternalMeasurement.value !== null && 
        this.currentExternalMeasurement.value !== undefined) {
      return this.currentExternalMeasurement.value;
    }
    return this.currentValue;
  }

  getDisplayConfidence(): number | null {
    // External measurements have priority over manual measurements
    if (this.currentExternalMeasurement && 
        this.currentExternalMeasurement.confidence !== null && 
        this.currentExternalMeasurement.confidence !== undefined) {
      return this.currentExternalMeasurement.confidence;
    }
    return this.currentConfidence;
  }

  formatTimestamp(timestamp: string | null): string {
    if (!timestamp) return '';
    try {
      const date = new Date(timestamp);
      return date.toLocaleString('de-DE', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch (error) {
      return timestamp;
    }
  }
}
