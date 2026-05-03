import { Component, OnInit } from '@angular/core';
import { ChartDataset, ChartOptions } from 'chart.js';
import { AuthService } from 'src/services/auth.service';
import { DashboardService } from 'src/services/dashboard.service';
import { CartService } from 'src/services/cart.service';
import { ProductService } from 'src/services/product.service';
import { AdminDashboard, Product, SellerDashboard } from 'src/Models/models';

const ART_COLORS = ['#D4AF77', '#C87941', '#A8B8A0', '#91A8B8', '#E6B8A2'];
const ART_GRID = '#E8E2D9';
const ART_TEXT = '#6B5E4E';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  loading = true;
  isAdmin = false;
  isSeller = false;
  isCustomer = false;
  currentRole = '';

  adminData: AdminDashboard | null = null;
  sellerData: SellerDashboard | null = null;
  archivedProducts: Product[] = [];
  archivedProductsTotal = 0;
  loadingArchivedProducts = false;
  restoringProductId: number | null = null;

  // ── Graphiques Admin ──────────────────────────────────────────────

  // Bar — commandes par statut
  adminBarLabels: string[] = [];
  adminBarData: ChartDataset[] = [{ data: [], label: 'Commandes', backgroundColor: ART_COLORS, borderRadius: 6 }];

  // Doughnut — produits par catégorie
  adminDoughnutLabels: string[] = [];
  adminDoughnutData: ChartDataset<'doughnut', number[]>[] = [
    { data: [], backgroundColor: ART_COLORS, hoverOffset: 8, label: 'Produits', borderWidth: 2, borderColor: '#FFFFFF' }
  ];

  // Line — avis par artiste
  adminLineLabels: string[] = [];
  adminLineData: ChartDataset[] = [{
    data: [], label: 'Avis',
    borderColor: '#D4AF77', backgroundColor: 'rgba(212, 175, 119, 0.15)',
    tension: 0.4, fill: true,
    pointBackgroundColor: '#D4AF77', pointRadius: 5
  }];

  // Bar — utilisateurs par role
  adminPieLabels: string[] = [];
  adminPieData: ChartDataset<'bar', number[]>[] = [
    { data: [], backgroundColor: ['#C87941', '#D4AF77'], label: 'Utilisateurs', borderRadius: 6 }
  ];

  // ── Graphiques Seller ─────────────────────────────────────────────

  sellerBarLabels: string[] = [];
  sellerBarData: ChartDataset[] = [{ data: [], label: 'Commandes', backgroundColor: ART_COLORS, borderRadius: 6 }];

  sellerLineLabels: string[] = [];
  sellerLineData: ChartDataset[] = [{
    data: [], label: 'Revenus (DT)',
    borderColor: '#C87941', backgroundColor: 'rgba(200, 121, 65, 0.15)',
    tension: 0.4, fill: true,
    pointBackgroundColor: '#C87941', pointRadius: 5
  }];

  sellerDoughnutLabels: string[] = [];
  sellerDoughnutData: ChartDataset<'doughnut', number[]>[] = [
    { data: [], backgroundColor: ART_COLORS, hoverOffset: 8, label: 'Ventes', borderWidth: 2, borderColor: '#FFFFFF' }
  ];

  sellerPieLabels: string[] = [];
  sellerPieData: ChartDataset<'pie', number[]>[] = [
    { data: [], backgroundColor: ART_COLORS, hoverOffset: 8, label: 'Statut', borderWidth: 2, borderColor: '#FFFFFF' }
  ];

  // ── Options communes ──────────────────────────────────────────────
  chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
        labels: { color: ART_TEXT, font: { family: 'Inter', size: 12, weight: 500 }, usePointStyle: true, padding: 20 }
      }
    },
    scales: {
      x: {
        ticks: { color: ART_TEXT, font: { family: 'Inter' } },
        grid:  { color: ART_GRID }
      },
      y: {
        beginAtZero: true,
        ticks: { color: ART_TEXT, precision: 0, font: { family: 'Inter' } },
        grid:  { color: ART_GRID }
      }
    }
  };

  cartData: any = null;

  constructor(
    private authService: AuthService,
    private dashboardService: DashboardService,
    private cartService: CartService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.currentRole = this.authService.getRole() || '';
    this.isAdmin     = this.authService.isAdmin();
    this.isSeller    = this.authService.isSeller();
    this.isCustomer  = this.authService.isCustomer();

    if (this.isAdmin) {
      this.loadAdminDashboard();
    } else if (this.isSeller) {
      this.loadSellerDashboard();
    } else if (this.isCustomer) {
      this.loadCustomerCart();
    } else {
      this.loading = false;
    }
  }

  private loadCustomerCart(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cartData = cart;
      this.loading = false;
    });
  }

  get roleBadgeClass(): string {
    switch (this.currentRole) {
      case 'ADMIN':    return 'badge badge-admin';
      case 'SELLER':   return 'badge badge-seller';
      case 'CUSTOMER': return 'badge badge-customer';
      default:         return 'badge';
    }
  }

  // ── Chargement Admin ──────────────────────────────────────────────

  private loadAdminDashboard(): void {
    this.dashboardService.GetAdminDashboard().subscribe({
      next: (data) => {
        this.adminData = data;
        this.buildAdminCharts(data);
        this.loadArchivedProducts();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  private buildAdminCharts(data: AdminDashboard): void {
    // Bar — statuts commandes
    this.adminBarLabels = Object.keys(data.commandesParStatut);
    this.adminBarData = [{
      data: Object.values(data.commandesParStatut),
      label: 'Commandes',
      backgroundColor: ART_COLORS,
      borderRadius: 6
    }];

    // Doughnut — catégories
    this.adminDoughnutLabels = Object.keys(data.produitsParCategorie);
    this.adminDoughnutData = [{
      data: Object.values(data.produitsParCategorie),
      backgroundColor: ART_COLORS, hoverOffset: 8, label: 'Produits', borderWidth: 2, borderColor: '#FFFFFF'
    }];

    // Line — avis par artiste
    this.adminLineLabels = Object.keys(data.avisParArtiste);
    this.adminLineData = [{
      data: Object.values(data.avisParArtiste),
      label: 'Avis',
      borderColor: '#D4AF77', backgroundColor: 'rgba(212, 175, 119, 0.15)',
      tension: 0.4, fill: true,
      pointBackgroundColor: '#D4AF77', pointRadius: 5
    }];

    // Bar — utilisateurs par role
    this.adminPieLabels = Object.keys(data.utilisateursParRole);
    this.adminPieData = [{
      data: Object.values(data.utilisateursParRole),
      backgroundColor: ['#C87941', '#D4AF77'],
      label: 'Utilisateurs',
      borderRadius: 6
    }];
  }

  private loadArchivedProducts(): void {
    this.loadingArchivedProducts = true;
    this.productService.GetArchivedProducts(0, 10).subscribe({
      next: (page) => {
        this.archivedProducts = page.content;
        this.archivedProductsTotal = page.totalElements;
        this.loadingArchivedProducts = false;
      },
      error: () => {
        this.archivedProducts = [];
        this.archivedProductsTotal = 0;
        this.loadingArchivedProducts = false;
      }
    });
  }

  restoreProduct(product: Product): void {
    this.restoringProductId = product.id;
    this.productService.RestoreProduct(product.id).subscribe({
      next: () => {
        this.archivedProducts = this.archivedProducts.filter(p => p.id !== product.id);
        this.archivedProductsTotal = Math.max(0, this.archivedProductsTotal - 1);

        if (this.adminData) {
          this.adminData.totalProductsArchived = Math.max(0, this.adminData.totalProductsArchived - 1);
          this.adminData.totalProductsActive = (this.adminData.totalProductsActive || 0) + 1;
        }

        this.restoringProductId = null;
      },
      error: () => {
        this.restoringProductId = null;
      }
    });
  }

  // ── Chargement Seller ─────────────────────────────────────────────

  private loadSellerDashboard(): void {
    this.dashboardService.GetSellerDashboard().subscribe({
      next: (data) => {
        this.sellerData = data;
        this.buildSellerCharts(data);
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  private buildSellerCharts(data: SellerDashboard): void {
    // Bar — statuts commandes
    this.sellerBarLabels = Object.keys(data.commandesParStatut);
    this.sellerBarData = [{
      data: Object.values(data.commandesParStatut),
      label: 'Commandes', backgroundColor: ART_COLORS, borderRadius: 6
    }];

    // Line — revenus/mois
    this.sellerLineLabels = Object.keys(data.revenusParMois);
    this.sellerLineData = [{
      data: Object.values(data.revenusParMois).map(v => Number(v)),
      label: 'Revenus (DT)',
      borderColor: '#C87941', backgroundColor: 'rgba(200, 121, 65, 0.15)',
      tension: 0.4, fill: true,
      pointBackgroundColor: '#C87941', pointRadius: 5
    }];

    // Doughnut — top produits
    this.sellerDoughnutLabels = Object.keys(data.topProduits);
    this.sellerDoughnutData = [{
      data: Object.values(data.topProduits),
      backgroundColor: ART_COLORS, hoverOffset: 8, label: 'Quantité vendue', borderWidth: 2, borderColor: '#FFFFFF'
    }];

    // Pie — statut livraisons (livrés vs annulés)
    const statut = data.commandesParStatut;
    this.sellerPieLabels = ['Livrées', 'Expédiées', 'En attente', 'Annulées'];
    this.sellerPieData = [{
      data: [
        statut['DELIVERED'] || 0,
        statut['SHIPPED']   || 0,
        statut['PENDING']   || 0,
        statut['CANCELLED'] || 0
      ],
      backgroundColor: ['#A8B8A0', '#91A8B8', '#D4AF77', '#C87941'],
      hoverOffset: 8, label: 'Statut', borderWidth: 2, borderColor: '#FFFFFF'
    }];
  }
}
