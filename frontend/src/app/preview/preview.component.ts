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
          
          <div class="result-section" *ngIf="value$ | async as value; else noResult">
            <h3 class="result-title">Erkannter Wert</h3>
            <div class="result-value">{{ value }}</div>
          </div>
          
          <ng-template #noResult>
            <div class="no-result">
              <span>Klicken Sie "Wert erkennen" für die Analyse</span>
            </div>
          </ng-template>
        </div>
        <ng-template #noRoi>
          <div class="no-roi">
            <span>Wählen Sie einen Bereich im Live-Stream aus</span>
          </div>
        </ng-template>
      </div>
    </mat-card>
  `,
    styles: [':host { display: block; }'],
    standalone: false
})
export class PreviewComponent implements OnDestroy {
  @ViewChild('hiddenCanvas') hiddenCanvasRef!: ElementRef<HTMLCanvasElement>;
  
  value$;
  unit$;
  roi$;
  roiImageUrl: string | null = null;
  private subscription = new Subscription();

  constructor(private store: Store<AppState>, private actions$: Actions) {
    this.value$ = this.store.pipe(select(state => state.measurement.value));
    this.unit$ = this.store.pipe(select(state => state.measurement.unit));
    this.roi$ = this.store.pipe(select(state => state.measurement.roi));
    
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
}
