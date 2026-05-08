import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderCreateRequest, Page } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = environment.apiUrl;

/**
 * Service HTTP Commandes
 */
@Injectable({ providedIn: 'root' })
export class OrderService {

  constructor(private http: HttpClient) { }

  GetAllOrders(page = 0, size = 10): Observable<Page<Order>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Order>>(`${API_URL}/orders`, { params });
  }

  GetOrderByID(id: number): Observable<Order> {
    return this.http.get<Order>(`${API_URL}/orders/${id}`);
  }

  CreateOrder(request: OrderCreateRequest): Observable<Order> {
    return this.http.post<Order>(`${API_URL}/orders`, request);
  }

  UpdateOrderStatus(id: number, status: string): Observable<Order> {
    return this.http.put<Order>(`${API_URL}/orders/${id}/status?status=${status}`, {});
  }

  CancelOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/orders/${id}`);
  }
}
