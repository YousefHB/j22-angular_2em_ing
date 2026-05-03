import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from 'src/services/product.service';
import { CartService } from 'src/services/cart.service';
import { AuthService } from 'src/services/auth.service';
import { ReviewService } from 'src/services/review.service';
import { Product, Review, ReviewCreateRequest } from 'src/Models/models';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {

  product: Product | null = null;
  loading = true;
  isCustomer = false;

  reviews: Review[] = [];
  reviewForm: FormGroup;
  submittingReview = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private reviewService: ReviewService,
    private snackBar: MatSnackBar
  ) {
    this.reviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.maxLength(1000)]]
    });
  }

  ngOnInit(): void {
    this.isCustomer = this.authService.isCustomer();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.GetProductByID(+id);
    }
  }

  GetProductByID(id: number): void {
    this.productService.GetProductByID(id).subscribe({
      next: (p) => { 
        this.product = p; 
        this.loading = false;
        this.loadReviews(id);
      },
      error: () => { this.loading = false; }
    });
  }

  loadReviews(productId: number): void {
    this.reviewService.getReviewsByProduct(productId).subscribe({
      next: (page) => { this.reviews = page.content; },
      error: () => {}
    });
  }

  addToCart(): void {
    if (!this.product) return;
    this.cartService.AddToCart({ productId: this.product.id, quantity: 1 }).subscribe({
      next: () => {
        const snack = this.snackBar.open('Ajouté au panier !', 'Voir le panier', { 
          panelClass: ['snack-success'], 
          duration: 4000 
        });
        snack.onAction().subscribe(() => {
          this.router.navigate(['/panier']);
        });
      },
      error: () => {
        this.snackBar.open('Erreur lors de l\'ajout.', 'OK', { panelClass: ['snack-error'], duration: 2000 });
      }
    });
  }

  submitReview(): void {
    if (this.reviewForm.invalid || !this.product) return;
    this.submittingReview = true;
    const request: ReviewCreateRequest = {
      productId: this.product.id,
      rating: this.reviewForm.value.rating,
      comment: this.reviewForm.value.comment
    };

    this.reviewService.addReview(request).subscribe({
      next: (rev) => {
        this.submittingReview = false;
        this.reviews.push(rev);
        this.reviewForm.reset({ rating: 5, comment: '' });
        this.snackBar.open('Avis ajouté avec succès !', 'OK', { panelClass: ['snack-success'], duration: 3000 });
      },
      error: (err) => {
        this.submittingReview = false;
        const msg = err.error?.message || 'Erreur lors de l\'ajout de l\'avis.';
        this.snackBar.open(msg, 'OK', { panelClass: ['snack-error'], duration: 3000 });
      }
    });
  }
}
