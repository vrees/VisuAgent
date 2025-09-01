import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

export interface CalibrationId {
    orderNumber: string;
    equipmentNumber: string;
}

export interface ImageArea {
    x: number;
    y: number;
    width: number;
    height: number;
}

export interface Calibration {
    id: CalibrationId;
    remark?: string;
    imageArea?: ImageArea;
    createdAt?: string;
    updatedAt?: string;
}

@Injectable({
    providedIn: 'root'
})
export class CalibrationService {
    private readonly apiUrl = `${environment.apiUrl}/calibration`;

    constructor(private http: HttpClient) {
    }

    createCalibration(calibration: Calibration): Observable<Calibration> {
        return this.http.post<Calibration>(this.apiUrl, calibration);
    }

    getAllCalibrations(): Observable<Calibration[]> {
        return this.http.get<Calibration[]>(this.apiUrl);
    }

    getCalibrationById(orderNumber: string, equipmentNumber: string): Observable<Calibration> {
        return this.http.get<Calibration>(`${this.apiUrl}/${orderNumber}/${equipmentNumber}`);
    }

    updateCalibration(orderNumber: string, equipmentNumber: string, calibration: Calibration): Observable<Calibration> {
        return this.http.put<Calibration>(`${this.apiUrl}/${orderNumber}/${equipmentNumber}`, calibration);
    }

    deleteCalibration(orderNumber: string, equipmentNumber: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${orderNumber}/${equipmentNumber}`);
    }

    existsCalibration(orderNumber: string, equipmentNumber: string): Observable<boolean> {
        return this.http.get<boolean>(`${this.apiUrl}/exists/${orderNumber}/${equipmentNumber}`);
    }
}
