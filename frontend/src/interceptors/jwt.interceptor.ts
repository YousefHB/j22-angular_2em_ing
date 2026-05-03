import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, catchError } from 'rxjs';
import { AuthService } from 'src/services/auth.service';
import { Router } from '@angular/router';

/**
 * Interceptor JWT — injecte "Authorization: Bearer <token>" à chaque requête
 * Gère aussi les erreurs 401 (redirection vers connexion)
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private router: Router) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    // Injecter le token si disponible et si ce n'est pas une route auth
    if (token && !request.url.includes('/auth/')) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token expiré ou invalide → déconnexion
          this.authService.logout();
        }
        return throwError(() => error);
      })
    );
  }
}
