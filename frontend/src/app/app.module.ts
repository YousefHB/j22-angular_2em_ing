import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

// ── Routing ──────────────────────────────────────────────────────────────
import { AppRoutingModule } from './app-routing.module';

// ── Angular Material ─────────────────────────────────────────────────────
import { MatToolbarModule }       from '@angular/material/toolbar';
import { MatSidenavModule }       from '@angular/material/sidenav';
import { MatListModule }          from '@angular/material/list';
import { MatIconModule }          from '@angular/material/icon';
import { MatButtonModule }        from '@angular/material/button';
import { MatCardModule }          from '@angular/material/card';
import { MatTableModule }         from '@angular/material/table';
import { MatPaginatorModule }     from '@angular/material/paginator';
import { MatSortModule }          from '@angular/material/sort';
import { MatFormFieldModule }     from '@angular/material/form-field';
import { MatInputModule }         from '@angular/material/input';
import { MatSelectModule }        from '@angular/material/select';
import { MatCheckboxModule }      from '@angular/material/checkbox';
import { MatDialogModule }        from '@angular/material/dialog';
import { MatSnackBarModule }      from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule }         from '@angular/material/chips';
import { MatMenuModule }          from '@angular/material/menu';
import { MatTooltipModule }       from '@angular/material/tooltip';
import { MatDividerModule }       from '@angular/material/divider';
import { MatBadgeModule }         from '@angular/material/badge';
import { MatAutocompleteModule }  from '@angular/material/autocomplete';
import { MatExpansionModule }     from '@angular/material/expansion';
import { MatRadioModule }         from '@angular/material/radio';

// ── ng2-charts ───────────────────────────────────────────────────────────
import { NgChartsModule } from 'ng2-charts';

// ── Interceptors ─────────────────────────────────────────────────────────
import { JwtInterceptor } from 'src/interceptors/jwt.interceptor';
import { ErrorInterceptor } from 'src/interceptors/error.interceptor';

// ── Composants app ───────────────────────────────────────────────────────
import { AppComponent }          from './app.component';
import { LandingComponent }      from './landing/landing.component';
import { LoginComponent }        from './auth/login/login.component';
import { RegisterComponent }     from './auth/register/register.component';
import { MainLayoutComponent }   from './layout/main-layout/main-layout.component';
import { DashboardComponent }    from './dashboard/dashboard.component';
import { ProductListComponent }  from './products/product-list/product-list.component';
import { ProductFormComponent }  from './products/product-form/product-form.component';
import { ProductDetailComponent } from './products/product-detail/product-detail.component';
import { CategoryListComponent } from './categories/category-list/category-list.component';
import { CategoryFormComponent } from './categories/category-form/category-form.component';
import { OrderListComponent }    from './orders/order-list/order-list.component';
import { OrderDetailComponent }  from './orders/order-detail/order-detail.component';
import { CartComponent }         from './cart/cart.component';
import { UserListComponent }     from './users/user-list/user-list.component';
import { ProfileComponent }      from './profile/profile.component';
import { AddressListComponent }  from './profile/address-list/address-list.component';
import { AddressFormDialogComponent } from './shared/address-form-dialog/address-form-dialog.component';
import { ConfirmDialogComponent } from './shared/confirm-dialog/confirm-dialog.component';
import { NotFoundComponent }     from './shared/not-found/not-found.component';
import { CheckoutComponent }     from './checkout/checkout.component';
import { CouponFormComponent }   from './coupons/coupon-form/coupon-form.component';
import { CouponListComponent }   from './coupons/coupon-list/coupon-list.component';

const MATERIAL_MODULES = [
  MatToolbarModule, MatSidenavModule, MatListModule, MatIconModule,
  MatButtonModule, MatCardModule, MatTableModule, MatPaginatorModule,
  MatSortModule, MatFormFieldModule, MatInputModule, MatSelectModule,
  MatCheckboxModule, MatDialogModule, MatSnackBarModule,
  MatProgressSpinnerModule, MatChipsModule, MatMenuModule,
  MatTooltipModule, MatDividerModule, MatBadgeModule,
  MatAutocompleteModule, MatExpansionModule, MatRadioModule
];

@NgModule({
  declarations: [
    AppComponent,
    LandingComponent,
    LoginComponent,
    RegisterComponent,
    MainLayoutComponent,
    DashboardComponent,
    ProductListComponent,
    ProductFormComponent,
    ProductDetailComponent,
    CategoryListComponent,
    CategoryFormComponent,
    OrderListComponent,
    OrderDetailComponent,
    CartComponent,
    UserListComponent,
    ProfileComponent,
    AddressListComponent,
    AddressFormDialogComponent,
    ConfirmDialogComponent,
    NotFoundComponent,
    CheckoutComponent,
    CouponFormComponent,
    CouponListComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule,
    NgChartsModule,
    ...MATERIAL_MODULES,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
