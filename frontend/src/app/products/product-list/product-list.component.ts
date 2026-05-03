import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { ProductService } from 'src/services/product.service';
import { AuthService } from 'src/services/auth.service';
import { CategoryService } from 'src/services/category.service';
import { Product, Category } from 'src/Models/models';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {

  // Données
  allProducts: Product[] = []; // Tous les produits récupérés
  filteredProducts: Product[] = []; // Produits après filtres
  categories: Category[] = [];

  // État
  loading = true;
  canCreate = false;
  isCustomer = false;

  // Filtres
  searchTerm = '';
  selectedCategoryId: number | null = null; // null = "Toutes"

  // Pagination (Optionnelle si on charge tout d'un coup)
  totalElements = 0;
  currentPage = 0;
  pageSize = 50; // On charge plus d'éléments pour mieux grouper

  constructor(
    private router: Router,
    private productService: ProductService,
    private authService: AuthService,
    private categoryService: CategoryService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  goToDetail(productId: number): void {
    this.router.navigate(['/produits', productId]);
  }

  ngOnInit(): void {
    this.canCreate = this.authService.isAdmin() || this.authService.isSeller();
    this.isCustomer = this.authService.isCustomer();

    // Charger les catégories puis les produits
    this.loadCategoriesAndProducts();
  }

  loadCategoriesAndProducts(): void {
    this.loading = true;
    this.categoryService.GetAllCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        this.loadProducts();
      },
      error: () => {
        this.loadProducts(); // Charger les produits même si les catégories échouent
      }
    });
  }

  loadProducts(): void {
    let productsObservable;
    if (this.authService.isAdmin()) {
      productsObservable = this.productService.GetAdminProducts(this.currentPage, this.pageSize);
    } else if (this.authService.isSeller()) {
      productsObservable = this.productService.GetSellerProducts(this.currentPage, this.pageSize);
    } else {
      productsObservable = this.productService.GetAllProducts(this.currentPage, this.pageSize);
    }

    productsObservable.subscribe({
      next: (page) => {
        this.allProducts = page.content;
        this.totalElements = page.totalElements;
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadProducts();
  }

  // ── Logique de filtre & recherche ──────────────────────────────────

  selectCategory(categoryId: number | null): void {
    this.selectedCategoryId = categoryId;
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCategoryId = null;
    this.applyFilters();
  }

  private applyFilters(): void {
    let tempProducts = [...this.allProducts];

    // Filtre par catégorie
    if (this.selectedCategoryId !== null) {
      tempProducts = tempProducts.filter(p => p.categoryIds && p.categoryIds.includes(this.selectedCategoryId!));
    }

    // Recherche multi-critères
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase().trim();
      tempProducts = tempProducts.filter(p =>
        (p.name && p.name.toLowerCase().includes(term)) ||
        (p.sellerName && p.sellerName.toLowerCase().includes(term)) ||
        (p.technique && p.technique.toLowerCase().includes(term))
      );
    }

    this.filteredProducts = tempProducts;
  }

  // ── Groupement par catégorie ────────────────────────────────────────

  get groupedProductsByCategory(): { categoryName: string, products: Product[] }[] {
    const groups: { [key: string]: Product[] } = {};

    this.filteredProducts.forEach(product => {
      // Trouver la première catégorie (ou 'Sans catégorie')
      let catName = 'Sans catégorie';
      if (product.categoryIds && product.categoryIds.length > 0) {
        const cat = this.categories.find(c => c.id === product.categoryIds[0]);
        if (cat) { catName = cat.name; }
      }

      if (!groups[catName]) {
        groups[catName] = [];
      }
      groups[catName].push(product);
    });

    // Convertir en tableau d'objets pour l'affichage (trier alphabétiquement, 'Sans catégorie' à la fin)
    return Object.keys(groups).map(key => ({
      categoryName: key,
      products: groups[key]
    })).sort((a, b) => {
      if (a.categoryName === 'Sans catégorie') return 1;
      if (b.categoryName === 'Sans catégorie') return -1;
      return a.categoryName.localeCompare(b.categoryName);
    });
  }

  // ── Utilitaires UI ──────────────────────────────────────────────────

  getCategoryName(product: Product): string {
    if (product.categoryIds && product.categoryIds.length > 0) {
      const cat = this.categories.find(c => c.id === product.categoryIds[0]);
      return cat ? cat.name : 'ArtShop';
    }
    return 'ArtShop';
  }

  getStockClass(product: Product): string {
    return product.stock > 0 ? 'stock-in' : 'stock-out';
  }

  getStatusBadge(product: Product): { text: string, class: string } {
    if (!product.active) {
      return { text: 'BROUILLON', class: 'badge-inactive' };
    }
    if (product.stock === 0) {
      return { text: 'RUPTURE', class: 'badge-out' };
    }
    return { text: 'EXPOSÉE', class: 'badge-active' };
  }

  hasDiscount(product: Product): boolean {
    return product.promotionalPrice !== undefined && product.promotionalPrice !== null && product.promotionalPrice > 0;
  }

  getFinalPrice(product: Product): number {
    return this.hasDiscount(product) ? product.promotionalPrice! : product.price;
  }

  // ── Actions ─────────────────────────────────────────────────────────

  viewProduct(product: Product): void {
    if (!product || !product.id) return;
    this.router.navigate(['/produits', product.id]);
  }

  editProduct(product: Product): void {
    if (!product || !product.id) return;
    this.router.navigate(['/produits/modifier', product.id]);
  }

  deleteProduct(product: Product): void {
    if (!product || !product.id) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message: `Voulez-vous vraiment supprimer "${product.name}" ?` }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.productService.DeleteProduct(product.id).subscribe({
          next: () => {
            // Mise à jour locale sans recharger la page
            this.allProducts = this.allProducts.filter(p => p.id !== product.id);
            this.totalElements--;
            this.applyFilters();
            this.snackBar.open('Œuvre supprimée avec succès.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Erreur lors de la suppression de l\'œuvre.', 'OK', { panelClass: ['snack-error'], duration: 3000 });
          }
        });
      }
    });
  }
}
