import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { OrderService } from 'src/services/order.service';
import { AuthService } from 'src/services/auth.service';
import { Order } from 'src/Models/models';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {

  order: Order | null = null;
  loading = true;
  canUpdateStatus = false;
  selectedStatus = '';
  statusOptions = ['PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED'];

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.canUpdateStatus = this.authService.isAdmin() || this.authService.isSeller();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) { this.GetOrderByID(+id); }
  }

  GetOrderByID(id: number): void {
    this.orderService.GetOrderByID(id).subscribe({
      next: (o) => { this.order = o; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  getStatusClass(status: string): string {
    const map: { [key: string]: string } = {
      PENDING: 'badge-pending', PAID: 'badge-paid',
      PROCESSING: 'badge-processing', SHIPPED: 'badge-shipped',
      DELIVERED: 'badge-delivered', CANCELLED: 'badge-cancelled',
      REFUNDED: 'badge-refunded'
    };
    return 'badge ' + (map[status] || '');
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      PENDING:    'En attente',
      PAID:       'Payée',
      PROCESSING: 'En traitement',
      SHIPPED:    'Expédiée',
      DELIVERED:  'Livrée',
      CANCELLED:  'Annulée',
      REFUNDED:   'Remboursée'
    };
    return labels[status] || status;
  }

  updateStatus(): void {
    if (!this.order || !this.selectedStatus) return;
    this.orderService.UpdateOrderStatus(this.order.id, this.selectedStatus).subscribe({
      next: (updated) => {
        this.order = updated;
        this.snackBar.open('Statut mis à jour.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erreur mise à jour statut.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
      }
    });
  }
}
