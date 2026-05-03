import { Component, OnInit, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/services/auth.service';
import { CartService } from 'src/services/cart.service';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit {

  isMobile = false;
  pageTitle = 'Tableau de Bord';
  userName = '';
  userEmail = '';
  userRole = '';
  isAdmin = false;
  isSeller = false;
  isCustomer = false;
  
  mobileMenuOpen = false;

  // Mapping route → titre de page
  private routeTitles: { [key: string]: string } = {
    '/tableau-de-bord': 'Tableau de Bord',
    '/produits': 'Produits',
    '/categories': 'Catégories',
    '/commandes': 'Commandes',
    '/panier': 'Mon Panier',
    '/utilisateurs': 'Utilisateurs',
    '/profil': 'Mon Profil',
  };

  cartItemCount = 0;
  
  constructor(
    private authService: AuthService,
    private router: Router,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.checkScreenSize();

    // Informations utilisateur
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName  = `${user.firstName} ${user.lastName}`;
      this.userEmail = user.email;
      this.userRole  = user.role;
    }

    this.isAdmin    = this.authService.isAdmin();
    this.isSeller   = this.authService.isSeller();
    this.isCustomer = this.authService.isCustomer();

    // Mise à jour du titre selon la route active
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const url = event.urlAfterRedirects.split('?')[0];
        const baseUrl = Object.keys(this.routeTitles).find(k => url.startsWith(k));
        this.pageTitle = baseUrl ? this.routeTitles[baseUrl] : 'ShopFlow';
      });

    // Suivi du panier
    this.cartService.cart$.subscribe(cart => {
      if (cart && cart.items) {
        this.cartItemCount = cart.items.reduce((acc, item) => acc + item.quantity, 0);
      } else {
        this.cartItemCount = 0;
      }
    });
  }

  get roleBadgeClass(): string {
    switch (this.userRole) {
      case 'ADMIN':    return 'badge-admin';
      case 'SELLER':   return 'badge-seller';
      case 'CUSTOMER': return 'badge-customer';
      default:         return '';
    }
  }

  onLogout(): void {
    this.authService.logout();
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }

  @HostListener('window:resize')
  checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
  }
}
