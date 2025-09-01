import { createReducer, on } from '@ngrx/store';
import * as MeasurementActions from './measurement.actions';

export interface MeasurementState {
  roi: { x: number, y: number, width: number, height: number } | null;
  value: number | null;
  confidence: number | null;
  // External trigger state
  externalMeasurement: {
    value: number | null;
    confidence: number | null;
    roiImageBase64: string | null;
    orderNumber: string | null;
    equipmentNumber: string | null;
    sessionId: string | null;
    triggerSource: string | null;
    timestamp: string | null;
  } | null;
  externalStatus: {
    status: string | null;
    message: string | null;
    sessionId: string | null;
    timestamp: number | null;
  } | null;
  webSocketConnected: boolean;
}

const initialState: MeasurementState = {
  roi: null,
  value: null,
  confidence: null,
  externalMeasurement: null,
  externalStatus: null,
  webSocketConnected: false
};

export const measurementReducer = createReducer(
  initialState,
  on(MeasurementActions.setRoi, (state, { roi }) => ({ ...state, roi })),
  on(MeasurementActions.setMeasurement, (state, { value, confidence }) => ({ ...state, value, confidence })),
  on(MeasurementActions.clearMeasurement, (state) => ({ ...state, value: null, confidence: null })),
  
  // External trigger reducers
  on(MeasurementActions.externalMeasurementReceived, (state, { 
    value, confidence, roiImageBase64, orderNumber, equipmentNumber, 
    sessionId, triggerSource, timestamp 
  }) => ({
    ...state,
    externalMeasurement: {
      value,
      confidence,
      roiImageBase64,
      orderNumber,
      equipmentNumber,
      sessionId,
      triggerSource,
      timestamp
    }
  })),
  
  on(MeasurementActions.externalStatusReceived, (state, { status, message, sessionId, timestamp }) => ({
    ...state,
    externalStatus: {
      status,
      message,
      sessionId,
      timestamp
    }
  })),
  
  on(MeasurementActions.webSocketConnectionChanged, (state, { connected }) => ({
    ...state,
    webSocketConnected: connected
  }))
);
