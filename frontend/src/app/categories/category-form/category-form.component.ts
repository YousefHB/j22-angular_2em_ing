import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CategoryService } from 'src/services/category.service';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.scss']
})
export class CategoryFormComponent implements OnInit {

  categoryForm: FormGroup;
  isEdit = false;
  categoryId: number | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.categoryForm = this.fb.group({
      name:        ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.categoryId = +id;
      this.GetCategoryByID(this.categoryId);
    }
  }

  private GetCategoryByID(id: number): void {
    this.categoryService.GetCategoryByID(id).subscribe({
      next: (cat) => {
        this.categoryForm.patchValue({ name: cat.name, description: cat.description });
      }
    });
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) return;
    this.loading = true;
    const data = this.categoryForm.value;

    const obs = this.isEdit && this.categoryId
      ? this.categoryService.UpdateCategory(this.categoryId, data)
      : this.categoryService.AddCategory(data);

    obs.subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open(
          this.isEdit ? 'Catégorie modifiée.' : 'Catégorie créée.',
          'OK', { panelClass: ['snack-success'], duration: 3000 }
        );
        this.router.navigate(['/categories']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Erreur.';
      }
    });
  }
}
