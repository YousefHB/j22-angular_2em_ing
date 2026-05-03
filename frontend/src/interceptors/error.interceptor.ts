import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private snackBar: MatSnackBar) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMsg = '';

        if (error.error instanceof ErrorEvent) {
          // Erreur côté client
          errorMsg = `Erreur: ${error.error.message}`;
        } else {
          // Erreur côté serveur
          if (error.status === 0) {
            errorMsg = "Le serveur est indisponible. Veuillez vérifier votre connexion.";
          } else if (error.status === 403) {
            errorMsg = "Accès refusé. Vous n'avez pas les droits nécessaires.";
          } else if (error.status === 404) {
            errorMsg = "Ressource introuvable.";
          } else if (error.status === 500) {
            errorMsg = "Une erreur interne du serveur s'est produite.";
          } else {
            // Afficher le message d'erreur du backend si disponible
            errorMsg = error.error?.message || error.message || `Code d'erreur: ${error.status}`;
          }
        }

        // Ignorer les 401 car gérés par JwtInterceptor
        if (error.status !== 401) {
          this.snackBar.open(errorMsg, 'OK', {
            duration: 4000,
            panelClass: ['snack-error']
          });
        }

        return throwError(() => error);
      })
    );
  }
}
