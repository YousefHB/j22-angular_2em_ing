import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = environment.apiUrl;
const TOKEN_KEY = 'sf_access_token';
const REFRESH_KEY = 'sf_refresh_token';
const USER_KEY = 'sf_user';

/**
 * Service d'authentification — JWT Spring Boot
 * Gère login, register, logout, stockage localStorage
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private http: HttpClient, private router: Router) { }

  // ── API calls ──────────────────────────────────────────────────────────

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/register`, request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(REFRESH_KEY);
    return this.http.post<AuthResponse>(
      `${API_URL}/auth/refresh?refreshToken=${refreshToken}`, {}
    ).pipe(tap(response => this.saveSession(response)));
  }

  // ── Session ────────────────────────────────────────────────────────────

  private saveSession(auth: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, auth.accessToken);
    localStorage.setItem(REFRESH_KEY, auth.refreshToken || '');
    localStorage.setItem(USER_KEY, JSON.stringify(auth));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.router.navigate(['/connexion']);
  }

  // ── Getters ────────────────────────────────────────────────────────────

  isLoggedIn(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getCurrentUser(): AuthResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  getRole(): string | null {
    const user = this.getCurrentUser();
    return user ? user.role : null;
  }

  isAdmin(): boolean { return this.getRole() === 'ADMIN'; }
  isSeller(): boolean { return this.getRole() === 'SELLER'; }
  isCustomer(): boolean { return this.getRole() === 'CUSTOMER'; }

  getUserId(): number | null {
    const user = this.getCurrentUser();
    return user ? user.userId : null;
  }
}
