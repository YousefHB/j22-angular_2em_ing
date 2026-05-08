import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page, Product, ProductCreateRequest, ProductUpdateRequest } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = environment.apiUrl;

/**
 * Service HTTP Produits — méthodes CRUD typées
 */
@Injectable({ providedIn: 'root' })
export class ProductService {

  constructor(private http: HttpClient) { }

  GetAllProducts(page = 0, size = 10): Observable<Page<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${API_URL}/products`, { params });
  }

  GetAdminProducts(page = 0, size = 10): Observable<Page<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${API_URL}/products/admin/all`, { params });
  }

  GetArchivedProducts(page = 0, size = 10): Observable<Page<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${API_URL}/products/admin/archived`, { params });
  }

  GetSellerProducts(page = 0, size = 10): Observable<Page<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${API_URL}/products/seller/me`, { params });
  }

  GetProductByID(id: number): Observable<Product> {
    return this.http.get<Product>(`${API_URL}/products/${id}`);
  }

  SearchProducts(keyword: string, page = 0, size = 10): Observable<Page<Product>> {
    const params = new HttpParams().set('keyword', keyword).set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${API_URL}/products/search`, { params });
  }

  GetProductsByCategory(categoryId: number, page = 0, size = 10): Observable<Page<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${API_URL}/products/category/${categoryId}`, { params });
  }

  AddProduct(request: ProductCreateRequest): Observable<Product> {
    return this.http.post<Product>(`${API_URL}/products`, request);
  }

  UpdateProduct(id: number, request: ProductUpdateRequest): Observable<Product> {
    return this.http.put<Product>(`${API_URL}/products/${id}`, request);
  }

  DeleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/products/${id}`);
  }

  RestoreProduct(id: number): Observable<Product> {
    return this.http.patch<Product>(`${API_URL}/products/${id}/restore`, {});
  }
}
