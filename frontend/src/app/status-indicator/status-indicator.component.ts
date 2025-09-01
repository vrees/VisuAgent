import { Component, OnDestroy } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { AppState } from '../store';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-status-indicator',
  template: `
    <div class="status-indicator">
      <div class="status-row">
        <div class="status-item" [ngClass]="{'connected': webSocketConnected, 'disconnected': !webSocketConnected}">
          <span class="status-dot"></span>
          <span class="status-label">WebSocket: {{ webSocketConnected ? 'Verbunden' : 'Getrennt' }}</span>
        </div>
      </div>
      
      <div class="status-row" *ngIf="lastExternalStatus">
        <div class="status-item external-status" [ngClass]="getExternalStatusClass()">
          <span class="status-dot"></span>
          <span class="status-label">{{ lastExternalStatus.message }}</span>
          <span class="status-time" *ngIf="lastExternalStatus.timestamp">
            {{ formatStatusTime(lastExternalStatus.timestamp) }}
          </span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .status-indicator {
      padding: 8px 12px;
      background: rgba(0, 0, 0, 0.7);
      border-radius: 6px;
      border: 1px solid rgba(255, 255, 255, 0.2);
      font-size: 12px;
      max-width: 300px;
    }
    
    .status-row {
      margin-bottom: 4px;
    }
    
    .status-row:last-child {
      margin-bottom: 0;
    }
    
    .status-item {
      display: flex;
      align-items: center;
      gap: 6px;
    }
    
    .status-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      flex-shrink: 0;
    }
    
    .connected .status-dot {
      background: #4caf50;
      box-shadow: 0 0 4px rgba(76, 175, 80, 0.6);
    }
    
    .disconnected .status-dot {
      background: #f44336;
      box-shadow: 0 0 4px rgba(244, 67, 54, 0.6);
    }
    
    .external-status.success .status-dot {
      background: #2196f3;
      box-shadow: 0 0 4px rgba(33, 150, 243, 0.6);
    }
    
    .external-status.processing .status-dot {
      background: #ff9800;
      box-shadow: 0 0 4px rgba(255, 152, 0, 0.6);
      animation: pulse 1.5s infinite;
    }
    
    .external-status.error .status-dot {
      background: #f44336;
      box-shadow: 0 0 4px rgba(244, 67, 54, 0.6);
    }
    
    .status-label {
      color: rgba(255, 255, 255, 0.9);
      font-weight: 500;
      flex-grow: 1;
    }
    
    .status-time {
      color: rgba(255, 255, 255, 0.6);
      font-size: 11px;
      margin-left: auto;
    }
    
    @keyframes pulse {
      0% { opacity: 1; }
      50% { opacity: 0.5; }
      100% { opacity: 1; }
    }
  `],
  standalone: false
})
export class StatusIndicatorComponent implements OnDestroy {
  webSocketConnected = false;
  lastExternalStatus: any = null;
  private subscription = new Subscription();

  constructor(private store: Store<AppState>) {
    this.subscription.add(
      this.store.pipe(select(state => state.measurement.webSocketConnected))
        .subscribe(connected => {
          this.webSocketConnected = connected;
        })
    );

    this.subscription.add(
      this.store.pipe(select(state => state.measurement.externalStatus))
        .subscribe(status => {
          if (status) {
            this.lastExternalStatus = status;
          }
        })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  getExternalStatusClass(): string {
    if (!this.lastExternalStatus?.status) return '';
    
    const status = this.lastExternalStatus.status.toLowerCase();
    if (status.includes('success') || status.includes('completed')) {
      return 'success';
    } else if (status.includes('processing') || status.includes('working')) {
      return 'processing';
    } else if (status.includes('error') || status.includes('failed')) {
      return 'error';
    }
    return 'success';
  }

  formatStatusTime(timestamp: number | null): string {
    if (!timestamp) return '';
    try {
      const date = new Date(timestamp);
      const now = new Date();
      const diffMs = now.getTime() - date.getTime();
      
      if (diffMs < 60000) { // Less than 1 minute
        return 'gerade eben';
      } else if (diffMs < 3600000) { // Less than 1 hour
        const minutes = Math.floor(diffMs / 60000);
        return `vor ${minutes}min`;
      } else {
        return date.toLocaleTimeString('de-DE', { 
          hour: '2-digit', 
          minute: '2-digit' 
        });
      }
    } catch (error) {
      return '';
    }
  }
}