import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MeasurementService {
  constructor(private http: HttpClient) {}

  extractMeasurement(file: File, prompt: string = 'Extract the measurement value from the image'): Observable<{ value: number, confidence: number }> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('prompt', prompt);
    return this.http.post<{ value: number, confidence: number }>(`${environment.apiUrl}/measurements`, formData);
  }
}
