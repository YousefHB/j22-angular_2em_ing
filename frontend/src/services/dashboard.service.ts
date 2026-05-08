import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminDashboard, SellerDashboard } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = environment.apiUrl;

/**
 * Service HTTP Tableau de Bord
 */
@Injectable({ providedIn: 'root' })
export class DashboardService {

  constructor(private http: HttpClient) { }

  GetAdminDashboard(): Observable<AdminDashboard> {
    return this.http.get<AdminDashboard>(`${API_URL}/dashboard/admin`);
  }

  GetSellerDashboard(): Observable<SellerDashboard> {
    return this.http.get<SellerDashboard>(`${API_URL}/dashboard/seller`);
  }
}
