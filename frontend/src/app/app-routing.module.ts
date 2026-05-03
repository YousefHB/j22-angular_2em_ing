import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LandingComponent } from './landing/landing.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProductListComponent } from './products/product-list/product-list.component';
import { ProductFormComponent } from './products/product-form/product-form.component';
import { ProductDetailComponent } from './products/product-detail/product-detail.component';
import { CategoryListComponent } from './categories/category-list/category-list.component';
import { CategoryFormComponent } from './categories/category-form/category-form.component';
import { OrderListComponent } from './orders/order-list/order-list.component';
import { OrderDetailComponent } from './orders/order-detail/order-detail.component';
import { CartComponent } from './cart/cart.component';
import { UserListComponent } from './users/user-list/user-list.component';
import { ProfileComponent } from './profile/profile.component';
import { AddressListComponent } from './profile/address-list/address-list.component';
import { NotFoundComponent } from './shared/not-found/not-found.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { CouponFormComponent } from './coupons/coupon-form/coupon-form.component';
import { CouponListComponent } from './coupons/coupon-list/coupon-list.component';

import { AuthGuard } from 'src/guards/auth.guard';
import { RoleGuard } from 'src/guards/role.guard';

const routes: Routes = [
  // ── Routes publiques ──────────────────────────────────────────
  { path: '',         component: LandingComponent },
  { path: 'connexion', component: LoginComponent },
  { path: 'inscription', component: RegisterComponent },

  // ── Routes privées (layout avec sidenav) ─────────────────────
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'tableau-de-bord', component: DashboardComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },

      // Produits
      { path: 'produits',            component: ProductListComponent },
      { path: 'produits/creer',      component: ProductFormComponent,
        canActivate: [RoleGuard], data: { roles: ['SELLER', 'ADMIN'] } },
      { path: 'produits/modifier/:id', component: ProductFormComponent,
        canActivate: [RoleGuard], data: { roles: ['SELLER', 'ADMIN'] } },
      { path: 'produits/:id',        component: ProductDetailComponent },

      // Catégories (ADMIN uniquement)
      { path: 'categories',            component: CategoryListComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'categories/creer',      component: CategoryFormComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'categories/modifier/:id', component: CategoryFormComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },

      // Commandes
      { path: 'commandes',     component: OrderListComponent },
      { path: 'commandes/:id', component: OrderDetailComponent },

      // Panier & Checkout (CUSTOMER)
      { path: 'panier', component: CartComponent,
        canActivate: [RoleGuard], data: { roles: ['CUSTOMER'] } },
      { path: 'checkout', component: CheckoutComponent,
        canActivate: [RoleGuard], data: { roles: ['CUSTOMER'] } },

      // Utilisateurs (ADMIN)
      { path: 'admin/utilisateurs', component: UserListComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },

      // Coupons (ADMIN)
      { path: 'coupons', component: CouponListComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'coupons/creer', component: CouponFormComponent,
        canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },

      // Profil
      { path: 'profil', component: ProfileComponent },
      { path: 'profil/adresses', component: AddressListComponent },
    ]
  },

  // Fallback
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
