import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CartService } from 'src/services/cart.service';
import { OrderService } from 'src/services/order.service';
import { CouponService } from 'src/services/coupon.service';
import { Cart, CartItem } from 'src/Models/models';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {

  cart: Cart | null = null;
  loading = true;
  couponCode = '';

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private couponService: CouponService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.couponCode = localStorage.getItem('pendingCoupon') || '';
    this.cartService.cart$.subscribe(c => {
      this.cart = c;
      this.loading = false;
    });
    this.cartService.refreshCart();
  }

  updateQty(item: CartItem, delta: number): void {
    const newQty = item.quantity + delta;
    if (newQty < 1) {
      this.removeItem(item);
      return;
    }
    this.cartService.UpdateCartItem(item.id, newQty).subscribe({
      next: (c) => { this.cart = c; },
      error: () => {}
    });
  }

  removeItem(item: CartItem): void {
    this.cartService.RemoveCartItem(item.id).subscribe({
      next: () => {},
      error: () => {}
    });
  }

  applyCoupon(): void {
    if (!this.couponCode.trim()) return;
    this.couponService.checkCoupon(this.couponCode).subscribe({
      next: (res) => {
        if (res.valid) {
          localStorage.setItem('pendingCoupon', this.couponCode);
          this.cartService.ApplyCoupon(this.couponCode).subscribe({
            next: (updatedCart) => {
              this.cart = updatedCart;
              this.snackBar.open(`Code promo appliqué ! (${res.value}${res.type === 'PERCENT' ? '%' : 'DT'})`, 'OK', { panelClass: ['snack-success'], duration: 3000 });
            },
            error: (err) => {
              this.snackBar.open('Erreur lors de l\'application du code au panier.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
            }
          });
        } else {
          this.snackBar.open(res.message || 'Code promo invalide.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
        }
      },
      error: () => {
        this.snackBar.open('Erreur de validation du code promo.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
      }
    });
  }

  passCommande(): void {
    this.router.navigate(['/checkout']);
  }
}
