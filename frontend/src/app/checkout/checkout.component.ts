import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CartService } from 'src/services/cart.service';
import { OrderService } from 'src/services/order.service';
import { AddressService } from 'src/services/address.service';
import { Cart, Address, OrderCreateRequest } from 'src/Models/models';
import { AddressFormDialogComponent } from '../shared/address-form-dialog/address-form-dialog.component';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {

  cart: Cart | null = null;
  addresses: Address[] = [];
  selectedAddressId: number | null = null;
  paymentMethod = 'CASH';
  
  loading = true;
  submitting = false;

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private addressService: AddressService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    // On charge le panier et les adresses en parallèle
    Promise.all([
      this.cartService.GetCart().toPromise(),
      this.addressService.getMyAddresses().toPromise()
    ]).then(([c, addrs]) => {
      this.cart = c || null;
      this.addresses = addrs || [];
      
      // Si panier vide, on retourne aux produits
      if (!this.cart || this.cart.items.length === 0) {
        this.router.navigate(['/produits']);
        return;
      }

      // Pré-sélectionner l'adresse principale si elle existe
      const primary = this.addresses.find(a => a.isPrimary);
      if (primary) {
        this.selectedAddressId = primary.id;
      } else if (this.addresses.length > 0) {
        this.selectedAddressId = this.addresses[0].id;
      }

      this.loading = false;
    }).catch(() => {
      this.loading = false;
    });
  }

  openAddressDialog(): void {
    const dialogRef = this.dialog.open(AddressFormDialogComponent, { width: '500px' });
    dialogRef.afterClosed().subscribe((newAddr: Address) => {
      if (newAddr) {
        this.addresses.push(newAddr);
        this.selectedAddressId = newAddr.id;
      }
    });
  }

  confirmOrder(): void {
    if (!this.selectedAddressId || !this.cart) return;

    this.submitting = true;
    
    const storedCoupon = localStorage.getItem('pendingCoupon');
    const couponCode = this.cart.couponCode || ((storedCoupon && storedCoupon !== 'null') ? storedCoupon : undefined);

    const request: OrderCreateRequest = {
      deliveryAddressId: this.selectedAddressId,
      couponCode: couponCode
    };

    this.orderService.CreateOrder(request).subscribe({
      next: (order) => {
        this.submitting = false;
        localStorage.removeItem('pendingCoupon');
        
        // Vider le panier explicitement après la commande
        this.cartService.ClearCart().subscribe({
          next: () => {
            this.snackBar.open('Commande validée avec succès !', 'OK', { panelClass: ['snack-success'], duration: 4000 });
            this.router.navigate(['/commandes', order.id]);
          },
          error: () => {
            // Même si le vidage échoue (rare), on navigue quand même
            this.router.navigate(['/commandes', order.id]);
          }
        });
      },
      error: () => {
        this.submitting = false;
      }
    });
  }
}
