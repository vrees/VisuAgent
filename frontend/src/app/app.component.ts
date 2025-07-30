import { Component, ViewChild } from '@angular/core';
import { VideoViewerComponent } from './video-viewer/video-viewer.component';

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar color="primary">VisuAgent Dashboard</mat-toolbar>
    <div style="display: flex; height: 100vh;">
      <div style="flex: 1;">
        <app-video-viewer #videoViewer></app-video-viewer>
      </div>
      <div style="flex: 1; display: flex; flex-direction: column;">
        <app-settings (triggerAI)="onTriggerAI()"></app-settings>
        <app-preview style="flex: 1;"></app-preview>
      </div>
    </div>
  `,
  styles: []
})
export class AppComponent {
  @ViewChild('videoViewer') videoViewer!: VideoViewerComponent;
  onTriggerAI() {
    if (this.videoViewer) {
      this.videoViewer.triggerAI();
    }
  }
}
