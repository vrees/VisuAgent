import { Component } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { AppState } from '../store';

@Component({
  selector: 'app-preview',
  template: `
    <mat-card>
      <h2>Preview</h2>
      <div style="height: 200px; background: #eee; display: flex; align-items: center; justify-content: center; flex-direction: column;">
        <ng-container *ngIf="value$ | async as value; else noValue">
          <ng-container *ngIf="unit$ | async as unit; else noValue">
            <span style="font-size: 2em;">{{ value }} {{ unit }}</span>
          </ng-container>
        </ng-container>
        <ng-template #noValue>
          <span>Preview of selected ROI</span>
        </ng-template>
      </div>
    </mat-card>
  `,
  styles: [':host { display: block; }']
})
export class PreviewComponent {
  value$;
  unit$;
  constructor(private store: Store<AppState>) {
    this.value$ = this.store.pipe(select(state => state.measurement.value));
    this.unit$ = this.store.pipe(select(state => state.measurement.unit));
  }
}
