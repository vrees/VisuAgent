import { createReducer, on } from '@ngrx/store';
import * as MeasurementActions from './measurement.actions';

export interface MeasurementState {
  roi: { x: number, y: number, width: number, height: number } | null;
  value: number | null;
  confidence: number | null;
}

const initialState: MeasurementState = {
  roi: null,
  value: null,
  confidence: null
};

export const measurementReducer = createReducer(
  initialState,
  on(MeasurementActions.setRoi, (state, { roi }) => ({ ...state, roi })),
  on(MeasurementActions.setMeasurement, (state, { value, confidence }) => ({ ...state, value, confidence })),
  on(MeasurementActions.clearMeasurement, (state) => ({ ...state, value: null, confidence: null }))
);
