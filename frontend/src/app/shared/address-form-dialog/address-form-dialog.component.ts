import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddressService } from 'src/services/address.service';
import { Address } from 'src/Models/models';

export interface AddressDialogData {
  address?: Address;
}

@Component({
  selector: 'app-address-form-dialog',
  templateUrl: './address-form-dialog.component.html',
  styleUrls: ['./address-form-dialog.component.scss']
})
export class AddressFormDialogComponent implements OnInit {

  addressForm: FormGroup;
  loading = false;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private addressService: AddressService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<AddressFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddressDialogData | null
  ) {
    this.addressForm = this.fb.group({
      street:     ['', Validators.required],
      city:       ['', Validators.required],
      postalCode: ['', Validators.required],
      country:    ['', Validators.required],
      isPrimary:  [false]
    });
  }

  ngOnInit(): void {
    const address = this.data?.address;

    if (address) {
      this.isEdit = true;
      this.addressForm.patchValue({
        street: address.street,
        city: address.city,
        postalCode: address.postalCode,
        country: address.country,
        isPrimary: address.isPrimary
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.addressForm.invalid) return;
    this.loading = true;

    const request = this.addressForm.value;

    const address = this.data?.address;

    if (this.isEdit && address) {
      this.addressService.updateAddress(address.id, request).subscribe({
        next: (addr) => {
          this.loading = false;
          this.snackBar.open('Adresse modifiée.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
          this.dialogRef.close(addr);
        },
        error: () => {
          this.loading = false;
        }
      });
    } else {
      this.addressService.createAddress(request).subscribe({
        next: (addr) => {
          this.loading = false;
          this.snackBar.open('Adresse créée.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
          this.dialogRef.close(addr);
        },
        error: () => {
          this.loading = false;
        }
      });
    }
  }
}
