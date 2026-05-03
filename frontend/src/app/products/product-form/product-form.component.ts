import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductService } from 'src/services/product.service';
import { CategoryService } from 'src/services/category.service';
import { Category } from 'src/Models/models';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {

  productForm: FormGroup;
  isEdit = false;
  productId: number | null = null;
  categories: Category[] = [];
  loading = false;
  errorMessage = '';
  imagePreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.productForm = this.fb.group({
      name:             ['', Validators.required],
      description:      ['', Validators.required],
      price:            [null, [Validators.required, Validators.min(0)]],
      promotionalPrice: [null],
      stock:            [0, [Validators.required, Validators.min(0)]],
      imageUrl:         [''],
      categoryIds:      [[]]
    });
  }

  ngOnInit(): void {
    // Détection mode edit via paramètre :id
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.productId = +id;
      this.GetProductByID(this.productId);
    }

    this.loadCategories();
  }

  private GetProductByID(id: number): void {
    this.productService.GetProductByID(id).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          promotionalPrice: product.promotionalPrice,
          stock: product.stock,
          imageUrl: product.imageUrl,
          categoryIds: product.categoryIds
        });
        if (product.imageUrl) {
          this.imagePreview = product.imageUrl;
        }
      },
      error: () => { this.errorMessage = 'Produit introuvable.'; }
    });
  }

  private loadCategories(): void {
    console.log('Loading categories...');
    this.categoryService.GetAllCategories().subscribe({
      next: (cats) => { 
        console.log('Categories loaded:', cats);
        this.categories = cats; 
      },
      error: (err) => {
        console.error('Error loading categories:', err);
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
        this.productForm.patchValue({ imageUrl: this.imagePreview });
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.productForm.invalid) return;

    this.loading = true;
    this.errorMessage = '';
    const data = this.productForm.value;

    if (this.isEdit && this.productId) {
      this.productService.UpdateProduct(this.productId, data).subscribe({
        next: () => {
          this.loading = false;
          this.snackBar.open('Produit modifié avec succès.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
          this.router.navigate(['/produits']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err?.error?.message || 'Erreur lors de la modification.';
        }
      });
    } else {
      this.productService.AddProduct(data).subscribe({
        next: () => {
          this.loading = false;
          this.snackBar.open('Produit créé avec succès.', 'OK', { panelClass: ['snack-success'], duration: 3000 });
          this.router.navigate(['/produits']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err?.error?.message || 'Erreur lors de la création.';
        }
      });
    }
  }
}
