import { Component, ElementRef, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { setRoi, setMeasurement, refreshPreview, clearMeasurement } from '../store/measurement.actions';
import { AppState } from '../store';
import { MeasurementService } from '../services/measurement.service';
import { environment } from '../../environments/environment';

@Component({
    selector: 'app-video-viewer',
    templateUrl: './video-viewer.component.html',
    styleUrls: ['./video-viewer.component.css'],
    standalone: false
})
export class VideoViewerComponent implements AfterViewInit, OnDestroy {
  @ViewChild('video') videoRef!: ElementRef<HTMLImageElement>;
  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  private isSelecting = false;
  private startX = 0;
  private startY = 0;
  private roi: { x: number, y: number, width: number, height: number } | null = null;
  roi$;
  streamUrl = `${environment.apiUrl}/stream`;
  private refreshTimer: any;

  constructor(private store: Store<AppState>, private measurementService: MeasurementService) {
    this.roi$ = this.store.pipe(select(state => state.measurement.roi));
  }
  /**
   * Extrahiert den aktuellen ROI aus dem Video und sendet ihn an das Backend.
   */
  triggerAI() {
    if (!this.roi) return;
    
    // Schritt 1: Ergebniswert löschen
    this.store.dispatch(clearMeasurement());
    
    // Schritt 2: Aktuelles Stream-Bild laden und verarbeiten
    const currentStreamImage = new Image();
    currentStreamImage.crossOrigin = 'anonymous';
    
    currentStreamImage.onload = () => {
      // Schritt 3: ROI aus dem aktuellen Stream-Bild extrahieren
      const canvas = document.createElement('canvas');
      canvas.width = this.roi!.width;
      canvas.height = this.roi!.height;
      const ctx = canvas.getContext('2d');
      
      if (ctx) {
        // ROI extrahieren
        ctx.drawImage(
          currentStreamImage,
          this.roi!.x, this.roi!.y, this.roi!.width, this.roi!.height,
          0, 0, this.roi!.width, this.roi!.height
        );
        
        // Schritt 4: Preview mit diesem Bild aktualisieren
        this.store.dispatch(refreshPreview());
        
        // Schritt 5: API-Call mit dem gleichen Bild
        canvas.toBlob(blob => {
          if (blob) {
            const file = new File([blob], 'roi.jpg', { type: 'image/jpeg' });
            this.measurementService.extractMeasurement(file).subscribe(result => {
              // Schritt 6: Ergebnis anzeigen
              this.store.dispatch(setMeasurement({ value: result.value, confidence: result.confidence }));
            });
          }
        }, 'image/jpeg');
      }
    };
    
    currentStreamImage.onerror = () => {
      console.error('Failed to load current stream image for AI processing');
    };
    
    // Aktuelles Stream-Bild laden
    currentStreamImage.src = `${environment.apiUrl}/stream?t=${Date.now()}`;
  }

  ngAfterViewInit() {
    // Canvas will be sized when image loads
    const canvas = this.canvasRef.nativeElement;
    canvas.addEventListener('mousedown', this.onMouseDown.bind(this));
    canvas.addEventListener('mousemove', this.onMouseMove.bind(this));
    canvas.addEventListener('mouseup', this.onMouseUp.bind(this));
    canvas.style.pointerEvents = 'auto';
    
    // Start auto-refresh for live streaming (2 FPS)
    this.startAutoRefresh();
  }
  
  ngOnDestroy() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
    }
  }
  
  private startAutoRefresh() {
    this.refreshTimer = setInterval(() => {
      // Add timestamp to force reload
      this.streamUrl = `${environment.apiUrl}/stream?t=` + Date.now();
    }, 1000); // 1 FPS
  }

  onImageLoaded(event: Event) {
    const img = event.target as HTMLImageElement;
    const canvas = this.canvasRef.nativeElement;
    canvas.width = img.naturalWidth;
    canvas.height = img.naturalHeight;
    canvas.style.width = img.offsetWidth + 'px';
    canvas.style.height = img.offsetHeight + 'px';
  }

  onVideoClick(event: MouseEvent, img: HTMLImageElement) {
    // Forward click to canvas for ROI selection
    const canvas = this.canvasRef.nativeElement;
    const rect = img.getBoundingClientRect();
    const evt = new MouseEvent('mousedown', {
      clientX: event.clientX,
      clientY: event.clientY
    });
    canvas.dispatchEvent(evt);
  }

  onMouseDown(event: MouseEvent) {
    this.isSelecting = true;
    const canvas = this.canvasRef.nativeElement;
    const rect = canvas.getBoundingClientRect();
    
    // Berechne die Skalierung zwischen angezeigter Größe und tatsächlicher Canvas-Größe
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    
    this.startX = Math.round((event.clientX - rect.left) * scaleX);
    this.startY = Math.round((event.clientY - rect.top) * scaleY);
  }

  onMouseMove(event: MouseEvent) {
    if (!this.isSelecting) return;
    const canvas = this.canvasRef.nativeElement;
    const rect = canvas.getBoundingClientRect();
    
    // Berechne die Skalierung zwischen angezeigter Größe und tatsächlicher Canvas-Größe
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    
    const x = Math.round((event.clientX - rect.left) * scaleX);
    const y = Math.round((event.clientY - rect.top) * scaleY);
    
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.strokeStyle = 'red';
      ctx.lineWidth = 2;
      ctx.strokeRect(this.startX, this.startY, x - this.startX, y - this.startY);
    }
  }

  onMouseUp(event: MouseEvent) {
    if (!this.isSelecting) return;
    this.isSelecting = false;
    const canvas = this.canvasRef.nativeElement;
    const rect = canvas.getBoundingClientRect();
    
    // Berechne die Skalierung zwischen angezeigter Größe und tatsächlicher Canvas-Größe
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    
    const endX = Math.round((event.clientX - rect.left) * scaleX);
    const endY = Math.round((event.clientY - rect.top) * scaleY);
    
    const roi = {
      x: Math.min(this.startX, endX),
      y: Math.min(this.startY, endY),
      width: Math.abs(endX - this.startX),
      height: Math.abs(endY - this.startY)
    };
    this.roi = roi;
    this.store.dispatch(setRoi({ roi }));
    
    // Zeichnung beibehalten
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.strokeStyle = 'red';
      ctx.lineWidth = 2;
      ctx.strokeRect(roi.x, roi.y, roi.width, roi.height);
    }
  }
  
  onImageError(event: Event) {
    console.error('Image loading failed:', event);
    // Retry after a short delay
    setTimeout(() => {
      this.streamUrl = `${environment.apiUrl}/stream?t=` + Date.now();
    }, 1000);
  }
}
