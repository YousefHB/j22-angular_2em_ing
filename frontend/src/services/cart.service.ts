import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Cart, CartAddItemRequest } from 'src/Models/models';

const API_URL = 'http://localhost:8084/api';

/**
 * Service HTTP Panier
 */
@Injectable({ providedIn: 'root' })
export class CartService {

  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {
    // Charger le panier initial au démarrage (si authentifié)
    this.refreshCart();
  }

  refreshCart(): void {
    this.GetCart().subscribe({
      next: (cart) => this.cartSubject.next(cart),
      error: () => this.cartSubject.next(null)
    });
  }

  GetCart(): Observable<Cart> {
    return this.http.get<Cart>(`${API_URL}/cart`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  AddToCart(request: CartAddItemRequest): Observable<Cart> {
    return this.http.post<Cart>(`${API_URL}/cart/add`, request).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  UpdateCartItem(itemId: number, quantity: number): Observable<Cart> {
    const params = new HttpParams().set('quantity', quantity);

    return this.http.put<Cart>(`${API_URL}/cart/item/${itemId}`, {}, { params }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  RemoveCartItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${API_URL}/cart/item/${itemId}`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  ClearCart(): Observable<void> {
    return this.http.delete<void>(`${API_URL}/cart`).pipe(
      tap(() => this.cartSubject.next(null))
    );
  }

  ApplyCoupon(code: string): Observable<Cart> {
    const params = new HttpParams().set('couponCode', code.trim());
    return this.http.post<Cart>(`${API_URL}/cart/coupon`, {}, { params }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }
}
