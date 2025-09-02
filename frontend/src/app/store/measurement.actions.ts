import { createAction, props } from '@ngrx/store';

export const setRoi = createAction('[VideoViewer] Set ROI', props<{ roi: { x: number, y: number, width: number, height: number } }>());
export const setMeasurement = createAction('[Measurement/API] Set Measurement', props<{ value: number, confidence: number }>());
export const clearMeasurement = createAction('[Settings] Clear Measurement');
export const refreshPreview = createAction('[Settings] Refresh Preview');
export const clearExternalMeasurement = createAction('[Measurement] Clear External Measurement');

// External trigger actions
export const externalMeasurementReceived = createAction(
  '[External] Measurement Received',
  props<{ 
    value: number; 
    confidence: number; 
    roiImageBase64: string;
    orderNumber: string;
    equipmentNumber: string;
    sessionId: string;
    triggerSource: string;
    timestamp: string;
  }>()
);

export const externalStatusReceived = createAction(
  '[External] Status Received',
  props<{ 
    status: string; 
    message: string; 
    sessionId?: string; 
    timestamp: number;
  }>()
);

export const webSocketConnectionChanged = createAction(
  '[WebSocket] Connection Changed',
  props<{ connected: boolean }>()
);
