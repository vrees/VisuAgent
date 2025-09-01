import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap, takeUntil } from 'rxjs/operators';
import { WebSocketService } from '../services/websocket.service';
import * as MeasurementActions from './measurement.actions';
import { EMPTY, merge } from 'rxjs';

@Injectable()
export class WebSocketEffects {

  constructor(
    private actions$: Actions,
    private webSocketService: WebSocketService
  ) {
    // Initialize WebSocket listeners on effect creation
    // Delay initialization to avoid circular dependency issues
    setTimeout(() => this.initializeWebSocketListeners(), 0);
  }

  /**
   * Initialize WebSocket listeners to dispatch actions based on received messages
   */
  private initializeWebSocketListeners() {
    try {
      // Listen for connection status changes
      if (this.webSocketService && typeof this.webSocketService.getConnectionStatus === 'function') {
        this.webSocketService.getConnectionStatus().subscribe(connected => {
          console.log('WebSocket connection status changed:', connected);
        });
      }

      // Listen for measurement results from external triggers
      if (this.webSocketService && typeof this.webSocketService.getMeasurementResults === 'function') {
        this.webSocketService.getMeasurementResults().subscribe(result => {
          console.log('External measurement result received:', result);
        });
      }

      // Listen for status messages
      if (this.webSocketService && typeof this.webSocketService.getStatusMessages === 'function') {
        this.webSocketService.getStatusMessages().subscribe(status => {
          console.log('External status message received:', status);
        });
      }
    } catch (error) {
      console.error('Error initializing WebSocket listeners:', error);
    }
  }

  /**
   * Effect to handle WebSocket connection status changes
   */
  connectionStatus$ = createEffect(() => {
    if (this.webSocketService && typeof this.webSocketService.getConnectionStatus === 'function') {
      return this.webSocketService.getConnectionStatus().pipe(
        map(connected => MeasurementActions.webSocketConnectionChanged({ connected }))
      );
    }
    return EMPTY;
  });

  /**
   * Effect to handle external measurement results from WebSocket
   */
  externalMeasurements$ = createEffect(() => {
    if (this.webSocketService && typeof this.webSocketService.getMeasurementResults === 'function') {
      return this.webSocketService.getMeasurementResults().pipe(
        map(result => MeasurementActions.externalMeasurementReceived({
          value: result.value,
          confidence: result.confidence,
          roiImageBase64: result.roiImageBase64,
          orderNumber: result.orderNumber,
          equipmentNumber: result.equipmentNumber,
          sessionId: result.sessionId,
          triggerSource: result.triggerSource,
          timestamp: result.timestamp
        }))
      );
    }
    return EMPTY;
  });

  /**
   * Effect to handle external status messages from WebSocket
   */
  externalStatus$ = createEffect(() => {
    if (this.webSocketService && typeof this.webSocketService.getStatusMessages === 'function') {
      return this.webSocketService.getStatusMessages().pipe(
        map(status => MeasurementActions.externalStatusReceived({
          status: status.status,
          message: status.message,
          sessionId: status.sessionId,
          timestamp: status.timestamp
        }))
      );
    }
    return EMPTY;
  });
}