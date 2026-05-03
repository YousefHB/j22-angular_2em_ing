import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { OrderService } from 'src/services/order.service';
import { Order } from 'src/Models/models';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {

  orders: Order[] = [];
  loading = true;
  totalElements = 0;
  currentPage = 0;
  displayedColumns = ['orderNumber', 'customer', 'artists', 'status', 'total', 'date', 'actions'];

  constructor(
    private orderService: OrderService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void { this.GetAllOrders(); }

  GetAllOrders(): void {
    this.loading = true;
    this.orderService.GetAllOrders(this.currentPage, 10).subscribe({
      next: (page) => {
        this.orders = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.GetAllOrders();
  }

  getStatusClass(status: string): string {
    const map: { [key: string]: string } = {
      PENDING: 'badge-pending', PAID: 'badge-paid',
      PROCESSING: 'badge-processing', SHIPPED: 'badge-shipped',
      DELIVERED: 'badge-delivered', CANCELLED: 'badge-cancelled',
      REFUNDED: 'badge-refunded'
    };
    return map[status] || '';
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

  getOrderArtists(order: Order): string {
    const names = (order.items || [])
      .map(item => item.sellerName)
      .filter((name): name is string => !!name);

    return [...new Set(names)].join(', ') || 'Artiste non renseigné';
  }

  onCancel(order: Order): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message: `Annuler la commande ${order.orderNumber} ?` }
    });
    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.orderService.CancelOrder(order.id).subscribe({
          next: () => {
            this.snackBar.open('Commande annulée.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
            this.GetAllOrders();
          },
          error: () => {
            this.snackBar.open('Erreur lors de l\'annulation.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
          }
        });
      }
    });
  }
}
