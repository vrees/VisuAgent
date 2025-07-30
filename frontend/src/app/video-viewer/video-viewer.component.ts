import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { setRoi } from '../store/measurement.actions';
import { AppState } from '../store';
import { MeasurementService } from '../services/measurement.service';
import { setMeasurement } from '../store/measurement.actions';

@Component({
    selector: 'app-video-viewer',
    templateUrl: './video-viewer.component.html',
    styleUrls: ['./video-viewer.component.css'],
    standalone: false
})
export class VideoViewerComponent implements AfterViewInit {
  @ViewChild('video') videoRef!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  private isSelecting = false;
  private startX = 0;
  private startY = 0;
  private roi: { x: number, y: number, width: number, height: number } | null = null;
  roi$;

  constructor(private store: Store<AppState>, private measurementService: MeasurementService) {
    this.roi$ = this.store.pipe(select(state => state.measurement.roi));
  }
  /**
   * Extrahiert den aktuellen ROI aus dem Video und sendet ihn an das Backend.
   */
  triggerAI() {
    if (!this.roi) return;
    const video = this.videoRef.nativeElement;
    const canvas = document.createElement('canvas');
    canvas.width = this.roi.width;
    canvas.height = this.roi.height;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.drawImage(
        video,
        this.roi.x, this.roi.y, this.roi.width, this.roi.height,
        0, 0, this.roi.width, this.roi.height
      );
      canvas.toBlob(blob => {
        if (blob) {
          const file = new File([blob], 'roi.jpg', { type: 'image/jpeg' });
          this.measurementService.extractMeasurement(file).subscribe(result => {
            this.store.dispatch(setMeasurement({ value: result.value, unit: result.unit }));
          });
        }
      }, 'image/jpeg');
    }
  }

  ngAfterViewInit() {
    const canvas = this.canvasRef.nativeElement;
    canvas.width = this.videoRef.nativeElement.videoWidth;
    canvas.height = this.videoRef.nativeElement.videoHeight;
    canvas.style.width = this.videoRef.nativeElement.offsetWidth + 'px';
    canvas.style.height = this.videoRef.nativeElement.offsetHeight + 'px';
    canvas.addEventListener('mousedown', this.onMouseDown.bind(this));
    canvas.addEventListener('mousemove', this.onMouseMove.bind(this));
    canvas.addEventListener('mouseup', this.onMouseUp.bind(this));
    canvas.style.pointerEvents = 'auto';
  }

  onVideoLoaded(video: HTMLVideoElement) {
    const canvas = this.canvasRef.nativeElement;
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.style.width = video.offsetWidth + 'px';
    canvas.style.height = video.offsetHeight + 'px';
  }

  onVideoClick(event: MouseEvent, video: HTMLVideoElement) {
    // Forward click to canvas for ROI selection
    const canvas = this.canvasRef.nativeElement;
    const rect = video.getBoundingClientRect();
    const evt = new MouseEvent('mousedown', {
      clientX: event.clientX,
      clientY: event.clientY
    });
    canvas.dispatchEvent(evt);
  }

  onMouseDown(event: MouseEvent) {
    this.isSelecting = true;
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    this.startX = event.clientX - rect.left;
    this.startY = event.clientY - rect.top;
  }

  onMouseMove(event: MouseEvent) {
    if (!this.isSelecting) return;
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    const ctx = this.canvasRef.nativeElement.getContext('2d');
    if (ctx) {
      ctx.clearRect(0, 0, this.canvasRef.nativeElement.width, this.canvasRef.nativeElement.height);
      ctx.strokeStyle = 'red';
      ctx.lineWidth = 2;
      ctx.strokeRect(this.startX, this.startY, x - this.startX, y - this.startY);
    }
  }

  onMouseUp(event: MouseEvent) {
    if (!this.isSelecting) return;
    this.isSelecting = false;
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const endX = event.clientX - rect.left;
    const endY = event.clientY - rect.top;
    const roi = {
      x: Math.min(this.startX, endX),
      y: Math.min(this.startY, endY),
      width: Math.abs(endX - this.startX),
      height: Math.abs(endY - this.startY)
    };
    this.roi = roi;
    this.store.dispatch(setRoi({ roi }));
    // Zeichnung beibehalten
    const ctx = this.canvasRef.nativeElement.getContext('2d');
    if (ctx) {
      ctx.clearRect(0, 0, this.canvasRef.nativeElement.width, this.canvasRef.nativeElement.height);
      ctx.strokeStyle = 'red';
      ctx.lineWidth = 2;
      ctx.strokeRect(roi.x, roi.y, roi.width, roi.height);
    }
  }
}
