/* ═══════════════════════════════════════════════════════════════
   ShopFlow — Models TypeScript
   Miroir des DTOs Spring Boot
   ═══════════════════════════════════════════════════════════════ */

// ── Auth ──────────────────────────────────────────────────────────────────

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: string; // ADMIN | SELLER | CUSTOMER
}

export interface AuthResponse {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

// ── Utilisateur ───────────────────────────────────────────────────────────

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'SELLER' | 'CUSTOMER';
  active: boolean;
  createdAt: string;
}

// ── Catégorie ─────────────────────────────────────────────────────────────

export interface Category {
  id: number;
  name: string;
  description?: string;
  parentId?: number;
}

export interface CategoryCreateRequest {
  name: string;
  description?: string;
  parentId?: number;
}

// ── Produit ───────────────────────────────────────────────────────────────

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  promotionalPrice?: number;
  discountPercentage?: number;
  stock: number;
  rating?: number;
  reviewCount?: number;
  imageUrl?: string;
  technique?: string;   // ArtShop: ex. "Huile sur toile"
  dimensions?: string;  // ArtShop: ex. "120x80 cm"
  active: boolean;
  sellerId: number;
  sellerName: string;
  categoryIds: number[];
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ProductCreateRequest {
  name: string;
  description: string;
  price: number;
  promotionalPrice?: number;
  stock: number;
  imageUrl?: string;
  technique?: string;
  dimensions?: string;
  categoryIds: number[];
}

export interface ProductUpdateRequest {
  name: string;
  description: string;
  price: number;
  promotionalPrice?: number;
  stock: number;
  imageUrl?: string;
  technique?: string;
  dimensions?: string;
  categoryIds: number[];
}

// ── Commande ──────────────────────────────────────────────────────────────

export type OrderStatus = 'PENDING' | 'PAID' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';

export interface Order {
  id: number;
  orderNumber: string;
  customerId: number;
  customerName: string;
  status: OrderStatus;
  subtotal: number;
  shippingFee: number;
  discountAmount: number;
  totalAmount: number;
  createdAt: string;
  deliveredAt?: string;
  items?: OrderItem[];
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  sellerId: number;
  sellerName: string;
  variantId?: number;
  variantInfo?: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderCreateRequest {
  deliveryAddressId: number;
  couponCode?: string;
}

// ── Panier ────────────────────────────────────────────────────────────────

export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  imageUrl?: string;
  variantId?: number;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  subtotal: number;
}

export interface Cart {
  id: number;
  items: CartItem[];
  subtotal: number;
  shippingFee: number;
  discountAmount: number;
  totalAmount: number;
  couponCode?: string;
}

export interface CartAddItemRequest {
  productId: number;
  variantId?: number;
  quantity: number;
}

// ── Avis ──────────────────────────────────────────────────────────────────

export interface Review {
  id: number;
  productId: number;
  productName: string;
  customerId: number;
  customerName: string;
  rating: number;
  comment: string;
  approved: boolean;
  createdAt: string;
}

export interface ReviewCreateRequest {
  productId: number;
  rating: number;
  comment: string;
}

// ── Coupon ────────────────────────────────────────────────────────────────

export interface Coupon {
  id: number;
  code: string;
  type: 'PERCENT' | 'FIXED';
  value: number;
  expirationDate?: string;
  maxUsages?: number;
  currentUsages: number;
  active: boolean;
}

export interface CouponCreateRequest {
  code: string;
  type: 'PERCENT' | 'FIXED';
  value: number;
  expirationDate: string;
  maxUsages?: number;
}

export interface CouponCheckResponse {
  code: string;
  exists: boolean;
  valid: boolean;
  status: string;
  message: string;
  type: 'PERCENT' | 'FIXED';
  value: number;
  active: boolean;
  expirationDate: string;
  maxUsages: number;
  currentUsages: number;
}

// ── Dashboard ─────────────────────────────────────────────────────────────

export interface AdminDashboard {
  totalUsers: number;
  totalSellers: number;
  totalCustomers: number;
  totalProducts: number;
  totalProductsActive: number;
  totalProductsArchived: number;
  totalOrders: number;
  totalCategories: number;
  pendingOrders: number;
  deliveredOrders: number;
  chiffreAffairesTotal: number;
  commandesParStatut: { [key: string]: number };
  topVendeurs: { [key: string]: number };
  avisParArtiste: { [key: string]: number };
  utilisateursParRole: { [key: string]: number };
  nouveauxUtilisateursParMois: { [key: string]: number };
  produitsParCategorie: { [key: string]: number };
}

export interface SellerDashboard {
  totalProduits: number;
  produitsActifs: number;
  totalCommandes: number;
  commandesEnAttente: number;
  commandesExpediees: number;
  commandesLivrees: number;
  chiffreAffaires: number;
  alertesStockFaible: number;
  commandesParStatut: { [key: string]: number };
  revenusParMois: { [key: string]: number };
  topProduits: { [key: string]: number };
}

// ── Adresse ───────────────────────────────────────────────────────────────

export interface Address {
  id: number;
  street: string;
  city: string;
  postalCode: string;
  country: string;
  isPrimary: boolean;
  createdAt?: string;
}

export interface AddressCreateRequest {
  street: string;
  city: string;
  postalCode: string;
  country: string;
  isPrimary?: boolean;
}
