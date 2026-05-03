import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CouponService } from 'src/services/coupon.service';
import { CouponCreateRequest } from 'src/Models/models';

@Component({
  selector: 'app-coupon-form',
  templateUrl: './coupon-form.component.html',
  styleUrls: ['./coupon-form.component.scss']
})
export class CouponFormComponent {

  couponForm: FormGroup;
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private couponService: CouponService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.couponForm = this.fb.group({
      code: ['', Validators.required],
      type: ['PERCENTAGE', Validators.required],
      value: [0, [Validators.required, Validators.min(0.01)]],
      maxUsages: [null],
      expirationDate: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.couponForm.invalid) return;
    this.submitting = true;

    const request: CouponCreateRequest = this.couponForm.value;
    
    // Si maxUsages est vide, on envoie 0 pour signifier illimité (selon logique backend habituelle)
    if (!request.maxUsages) {
      request.maxUsages = 0;
    }

    this.couponService.createCoupon(request).subscribe({
      next: () => {
        this.submitting = false;
        this.snackBar.open('Coupon créé avec succès !', 'OK', { panelClass: ['snack-success'], duration: 3000 });
        this.router.navigate(['/tableau-de-bord']);
      },
      error: () => {
        this.submitting = false;
      }
    });
  }
}
