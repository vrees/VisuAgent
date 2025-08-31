import { createAction, props } from '@ngrx/store';

export const setRoi = createAction('[VideoViewer] Set ROI', props<{ roi: { x: number, y: number, width: number, height: number } }>());
export const setMeasurement = createAction('[Measurement/API] Set Measurement', props<{ value: number, confidence: number }>());
export const clearMeasurement = createAction('[Settings] Clear Measurement');
export const refreshPreview = createAction('[Settings] Refresh Preview');
