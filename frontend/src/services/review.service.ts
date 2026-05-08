import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page, Review, ReviewCreateRequest } from 'src/Models/models';
import { HttpParams } from '@angular/common/http';
import { environment } from '../environments/environment';

const API_URL = `${environment.apiUrl}/reviews`;

@Injectable({ providedIn: 'root' })
export class ReviewService {

  constructor(private http: HttpClient) { }

  getReviewsByProduct(productId: number, page = 0, size = 10): Observable<Page<Review>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Review>>(`${API_URL}/product/${productId}`, { params });
  }

  addReview(request: ReviewCreateRequest): Observable<Review> {
    return this.http.post<Review>(API_URL, request);
  }

  approveReview(id: number): Observable<Review> {
    return this.http.put<Review>(`${API_URL}/${id}/approve`, {});
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
