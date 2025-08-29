import { Component, ViewChild } from '@angular/core';
import { VideoViewerComponent } from '../video-viewer/video-viewer.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: false
})
export class DashboardComponent {
  @ViewChild(VideoViewerComponent) videoViewer!: VideoViewerComponent;

  onTriggerAI() {
    if (this.videoViewer) {
      this.videoViewer.triggerAI();
    }
  }
}