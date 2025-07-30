import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MeasurementService {
  constructor(private http: HttpClient) {}

  extractMeasurement(file: File, prompt: string = 'Extract the measurement value and unit from the image'): Observable<{ value: string, unit: string }> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('prompt', prompt);
    return this.http.post<{ value: string, unit: string }>(`${environment.apiUrl}/measurements`, formData);
  }
}
