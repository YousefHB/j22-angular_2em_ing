import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CouponCheckResponse, CouponCreateRequest } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = `${environment.apiUrl}/coupons`;

@Injectable({ providedIn: 'root' })
export class CouponService {

  constructor(private http: HttpClient) { }

  createCoupon(request: CouponCreateRequest): Observable<CouponCheckResponse> {
    return this.http.post<CouponCheckResponse>(API_URL, request);
  }

  checkCoupon(code: string): Observable<CouponCheckResponse> {
    return this.http.get<CouponCheckResponse>(`${API_URL}/check?code=${code}`);
  }

  getCoupons(): Observable<CouponCheckResponse[]> {
    return this.http.get<CouponCheckResponse[]>(API_URL);
  }
}
