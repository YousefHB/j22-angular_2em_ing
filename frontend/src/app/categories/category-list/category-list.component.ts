import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CategoryService } from 'src/services/category.service';
import { Category } from 'src/Models/models';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss']
})
export class CategoryListComponent implements OnInit {

  categories: Category[] = [];
  filteredCategories: Category[] = [];
  loading = true;
  
  searchTerm = '';
  sortBy: 'nameAsc' | 'nameDesc' | 'recent' = 'nameAsc';

  constructor(
    private categoryService: CategoryService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.GetAllCategories();
  }

  GetAllCategories(): void {
    this.loading = true;
    this.categoryService.GetAllCategories().subscribe({
      next: (cats) => { 
        this.categories = cats; 
        this.applyFilters();
        this.loading = false; 
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilters(): void {
    let filtered = [...this.categories];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(c => 
        c.name.toLowerCase().includes(term) || 
        (c.description && c.description.toLowerCase().includes(term))
      );
    }

    if (this.sortBy === 'nameAsc') {
      filtered.sort((a, b) => a.name.localeCompare(b.name));
    } else if (this.sortBy === 'nameDesc') {
      filtered.sort((a, b) => b.name.localeCompare(a.name));
    } else if (this.sortBy === 'recent') {
      // On trie par ID décroissant en supposant que l'ID reflète l'ordre de création
      filtered.sort((a, b) => b.id - a.id);
    }

    this.filteredCategories = filtered;
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onSortChange(sort: 'nameAsc' | 'nameDesc' | 'recent'): void {
    this.sortBy = sort;
    this.applyFilters();
  }

  onDelete(cat: Category): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message: `Êtes-vous sûr de vouloir supprimer la collection "${cat.name}" ?` }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.categoryService.DeleteCategory(cat.id).subscribe({
          next: () => {
            this.snackBar.open('Collection supprimée avec succès.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
            this.GetAllCategories();
          },
          error: () => {
            this.snackBar.open('Erreur lors de la suppression de la collection.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
          }
        });
      }
    });
  }
}
