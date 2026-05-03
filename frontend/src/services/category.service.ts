import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, CategoryCreateRequest } from 'src/Models/models';

const API_URL = 'http://localhost:8084/api';

/**
 * Service HTTP Catégories
 */
@Injectable({ providedIn: 'root' })
export class CategoryService {

  constructor(private http: HttpClient) {}

  GetAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${API_URL}/categories`);
  }

  GetCategoryByID(id: number): Observable<Category> {
    return this.http.get<Category>(`${API_URL}/categories/${id}`);
  }

  AddCategory(request: CategoryCreateRequest): Observable<Category> {
    return this.http.post<Category>(`${API_URL}/categories`, request);
  }

  UpdateCategory(id: number, request: CategoryCreateRequest): Observable<Category> {
    return this.http.put<Category>(`${API_URL}/categories/${id}`, request);
  }

  DeleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/categories/${id}`);
  }

  GetTopLevelCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${API_URL}/categories/top-level`);
  }
}
