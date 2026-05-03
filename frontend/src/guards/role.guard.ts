import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from 'src/services/auth.service';

/**
 * Guard de rôle — vérifie le rôle requis via données de route
 * Usage: { path: '...', canActivate: [RoleGuard], data: { roles: ['ADMIN'] } }
 */
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles: string[] = route.data['roles'] || [];
    const userRole = this.authService.getRole();

    if (!userRole) {
      this.router.navigate(['/connexion']);
      return false;
    }

    if (requiredRoles.length === 0 || requiredRoles.includes(userRole)) {
      return true;
    }

    // Redirige vers le dashboard si rôle insuffisant
    this.router.navigate(['/tableau-de-bord']);
    return false;
  }
}
