import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Address, AddressCreateRequest } from 'src/Models/models';
import { environment } from '../environments/environment';

const API_URL = `${environment.apiUrl}/addresses`;

@Injectable({ providedIn: 'root' })
export class AddressService {

  constructor(private http: HttpClient) { }

  getMyAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(API_URL);
  }

  getAddressById(id: number): Observable<Address> {
    return this.http.get<Address>(`${API_URL}/${id}`);
  }

  createAddress(request: AddressCreateRequest): Observable<Address> {
    return this.http.post<Address>(API_URL, request);
  }

  updateAddress(id: number, request: AddressCreateRequest): Observable<Address> {
    return this.http.put<Address>(`${API_URL}/${id}`, request);
  }

  deleteAddress(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  getAddressesByUser(userId: number): Observable<Address[]> {
    return this.http.get<Address[]>(`${API_URL}/user/${userId}`);
  }
}
