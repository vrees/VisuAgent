import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

export interface MeasurementResult {
  value: number;
  confidence: number;
  roiImageBase64: string;
  triggerSource: string;
  orderNumber: string;
  equipmentNumber: string;
  sessionId: string;
  timestamp: string;
}

export interface StatusMessage {
  status: string;
  message: string;
  sessionId?: string;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  
  private stompClient: Client | null = null;
  private connected$ = new BehaviorSubject<boolean>(false);
  private measurementSubject = new Subject<MeasurementResult>();
  private statusSubject = new Subject<StatusMessage>();

  constructor() {
    this.initializeWebSocketConnection();
  }

  /**
   * Initialize WebSocket connection with auto-reconnect
   */
  private initializeWebSocketConnection(): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8082/ws'),
      connectHeaders: {},
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected to WebSocket:', frame);
      this.connected$.next(true);
      this.subscribeToTopics();
    };

    this.stompClient.onStompError = (frame) => {
      console.error('WebSocket STOMP error:', frame.headers['message']);
      console.error('Error details:', frame.body);
      this.connected$.next(false);
    };

    this.stompClient.onWebSocketClose = (event) => {
      console.log('WebSocket connection closed:', event);
      this.connected$.next(false);
    };

    // Auto-connect
    this.connect();
  }

  /**
   * Connect to WebSocket server
   */
  connect(): void {
    if (this.stompClient && !this.stompClient.active) {
      this.stompClient.activate();
    }
  }

  /**
   * Disconnect from WebSocket server
   */
  disconnect(): void {
    if (this.stompClient?.active) {
      this.stompClient.deactivate();
    }
    this.connected$.next(false);
  }

  /**
   * Subscribe to WebSocket topics
   */
  private subscribeToTopics(): void {
    if (!this.stompClient?.connected) {
      return;
    }

    // Subscribe to measurement results
    this.stompClient.subscribe('/topic/measurement', (message: IMessage) => {
      try {
        const measurementResult: MeasurementResult = JSON.parse(message.body);
        console.log('Received measurement result:', measurementResult);
        this.measurementSubject.next(measurementResult);
      } catch (error) {
        console.error('Error parsing measurement message:', error);
      }
    });

    // Subscribe to status messages
    this.stompClient.subscribe('/topic/status', (message: IMessage) => {
      try {
        const statusMessage: StatusMessage = JSON.parse(message.body);
        console.log('Received status message:', statusMessage);
        this.statusSubject.next(statusMessage);
      } catch (error) {
        console.error('Error parsing status message:', error);
      }
    });

    console.log('Subscribed to WebSocket topics');
  }

  /**
   * Send ping message to server
   */
  sendPing(): void {
    if (this.stompClient?.connected) {
      this.stompClient.publish({
        destination: '/app/ping',
        body: 'ping from Angular client'
      });
    }
  }

  /**
   * Observable for connection status
   */
  getConnectionStatus(): Observable<boolean> {
    return this.connected$.asObservable();
  }

  /**
   * Observable for measurement results
   */
  getMeasurementResults(): Observable<MeasurementResult> {
    return this.measurementSubject.asObservable();
  }

  /**
   * Observable for status messages
   */
  getStatusMessages(): Observable<StatusMessage> {
    return this.statusSubject.asObservable();
  }

  /**
   * Check if currently connected
   */
  isConnected(): boolean {
    return this.connected$.value;
  }
}