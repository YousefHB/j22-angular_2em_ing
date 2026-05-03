import { Component, OnInit } from '@angular/core';
import { CouponService } from 'src/services/coupon.service';
import { CouponCheckResponse } from 'src/Models/models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-coupon-list',
  templateUrl: './coupon-list.component.html',
  styleUrls: ['./coupon-list.component.scss']
})
export class CouponListComponent implements OnInit {

  coupons: CouponCheckResponse[] = [];
  loading = true;
  displayedColumns: string[] = ['code', 'type', 'value', 'maxUsages', 'currentUsages', 'expirationDate', 'status'];

  constructor(
    private couponService: CouponService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadCoupons();
  }

  loadCoupons(): void {
    this.loading = true;
    this.couponService.getCoupons().subscribe({
      next: (data) => {
        this.coupons = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Erreur lors du chargement des coupons', 'OK', { duration: 3000 });
        this.loading = false;
      }
    });
  }
}
