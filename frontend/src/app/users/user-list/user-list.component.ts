import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { UserService } from 'src/services/user.service';
import { User } from 'src/Models/models';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

  users: User[] = [];
  loading = true;
  totalElements = 0;
  currentPage = 0;
  displayedColumns = ['name', 'email', 'role', 'active', 'date', 'actions'];

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void { this.GetAllUsers(); }

  GetAllUsers(): void {
    this.loading = true;
    this.userService.GetAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.totalElements = users.length;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    // Client-side pagination could be added here if needed, but for now we rely on mat-paginator's built-in client side mapping if hooked to a MatTableDataSource
  }

  onToggleActive(user: User): void {
    this.userService.ToggleUserActive(user.id).subscribe({
      next: () => {
        this.snackBar.open(
          user.active ? 'Compte désactivé.' : 'Compte activé.',
          'OK', { panelClass: ['snack-success'], duration: 3000 }
        );
        this.GetAllUsers();
      },
      error: () => {
        this.snackBar.open('Erreur.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
      }
    });
  }

  onDelete(user: User): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message: `Supprimer l'utilisateur "${user.email}" ?` }
    });
    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.userService.DeleteUser(user.id).subscribe({
          next: () => {
            this.snackBar.open('Utilisateur supprimé.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
            this.GetAllUsers();
          },
          error: () => {
            this.snackBar.open('Erreur lors de la suppression.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
          }
        });
      }
    });
  }
}
