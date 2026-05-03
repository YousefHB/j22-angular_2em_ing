import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddressService } from 'src/services/address.service';
import { Address } from 'src/Models/models';
import { AddressFormDialogComponent } from '../../shared/address-form-dialog/address-form-dialog.component';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-address-list',
  templateUrl: './address-list.component.html',
  styleUrls: ['./address-list.component.scss']
})
export class AddressListComponent implements OnInit {

  addresses: Address[] = [];
  loading = true;

  constructor(
    private addressService: AddressService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAddresses();
  }

  loadAddresses(): void {
    this.loading = true;
    this.addressService.getMyAddresses().subscribe({
      next: (data) => { this.addresses = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  openAddressDialog(address?: Address): void {
    const dialogRef = this.dialog.open(AddressFormDialogComponent, {
      width: '500px',
      data: { address }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadAddresses();
      }
    });
  }

  deleteAddress(address: Address): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message: `Supprimer cette adresse ?` }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.addressService.deleteAddress(address.id).subscribe({
          next: () => {
            this.snackBar.open('Adresse supprimée.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
            this.loadAddresses();
          }
        });
      }
    });
  }
}
