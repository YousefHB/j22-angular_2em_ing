import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page, User } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = environment.apiUrl;

/**
 * Service HTTP Utilisateurs (ADMIN)
 */
@Injectable({ providedIn: 'root' })
export class UserService {

  constructor(private http: HttpClient) { }

  GetAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${API_URL}/users`);
  }

  GetUserByID(id: number): Observable<User> {
    return this.http.get<User>(`${API_URL}/users/${id}`);
  }

  ToggleUserActive(id: number): Observable<User> {
    return this.http.put<User>(`${API_URL}/users/${id}/deactivate`, {});
  }

  UpdateUser(id: number, request: any): Observable<User> {
    return this.http.put<User>(`${API_URL}/users/${id}`, request);
  }

  DeleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/users/${id}`);
  }

  GetUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${API_URL}/users/role/${role}`);
  }

  GetCurrentUser(): Observable<User> {
    return this.http.get<User>(`${API_URL}/users/me`);
  }

  GetUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${API_URL}/users/email/${email}`);
  }
}
