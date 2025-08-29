import { createReducer, on } from '@ngrx/store';
import * as MeasurementActions from './measurement.actions';

export interface MeasurementState {
  roi: { x: number, y: number, width: number, height: number } | null;
  value: string | null;
  unit: string | null;
}

const initialState: MeasurementState = {
  roi: null,
  value: null,
  unit: null
};

export const measurementReducer = createReducer(
  initialState,
  on(MeasurementActions.setRoi, (state, { roi }) => ({ ...state, roi })),
  on(MeasurementActions.setMeasurement, (state, { value, unit }) => ({ ...state, value, unit })),
  on(MeasurementActions.clearMeasurement, (state) => ({ ...state, value: null, unit: null }))
);
