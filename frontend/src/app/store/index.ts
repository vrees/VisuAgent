import { ActionReducerMap } from '@ngrx/store';
import { measurementReducer, MeasurementState } from './measurement.reducer';

export interface AppState {
  measurement: MeasurementState;
}

export const reducers: ActionReducerMap<AppState> = {
  measurement: measurementReducer
};
