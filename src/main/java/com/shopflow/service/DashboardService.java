package com.shopflow.service;

import com.shopflow.dto.response.AdminDashboardResponse;
import com.shopflow.dto.response.SellerDashboardResponse;
import com.shopflow.entity.OrderStatus;
import com.shopflow.entity.Product;
import com.shopflow.entity.UserRole;
import com.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de calcul des statistiques du tableau de bord
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final OrderRepository orderRepository;
        private final CategoryRepository categoryRepository;
        private final SellerProfileRepository sellerProfileRepository;
        private final OrderItemRepository orderItemRepository;

        // ─────────────────────────────────────────────────────────────────────────
        // Dashboard ADMIN
        // ─────────────────────────────────────────────────────────────────────────

        /**
         * Statistiques globales pour l'administrateur
         */
        public AdminDashboardResponse getAdminDashboard() {

                long totalUsers = userRepository.count();
                long totalSellers = userRepository.findByRole(UserRole.SELLER).size();
                long totalCustomers = userRepository.findByRole(UserRole.CUSTOMER).size();
                long totalProducts = productRepository.count();
                long totalProductsActive = productRepository.countByActive(true);
                long totalProductsArchived = productRepository.countByActive(false);
                long totalOrders = orderRepository.count();
                long totalCategories = categoryRepository.count();

                long pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING,
                                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
                long deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED,
                                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

                // Chiffre d'affaires total (somme de tous les totalAmount)
                BigDecimal ca = orderRepository.findAll().stream()
                                .filter(o -> o.getStatus() != OrderStatus.CANCELLED
                                                && o.getStatus() != OrderStatus.REFUNDED)
                                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // ── Graphiques ──────────────────────────────────────────────────────

                // Commandes par statut
                Map<String, Long> commandesParStatut = orderRepository.findAll().stream()
                                .collect(Collectors.groupingBy(
                                                o -> o.getStatus().name(),
                                                Collectors.counting()));

                // Top vendeurs (par somme des totalAmount des commandes liées à leurs produits)
                Map<String, BigDecimal> topVendeurs = new LinkedHashMap<>();
                sellerProfileRepository.findAll().forEach(sp -> {
                        BigDecimal revenuVendeur = orderItemRepository.findAll().stream()
                                        .filter(item -> item.getProduct() != null
                                                        && sp.getUser().getId()
                                                                        .equals(item.getProduct().getSeller().getId()))
                                        .map(item -> item.getUnitPrice()
                                                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        topVendeurs.put(sp.getStoreName(), revenuVendeur);
                });
                // Garder seulement les 5 premiers triés par valeur décroissante
                Map<String, BigDecimal> top5Vendeurs = topVendeurs.entrySet().stream()
                                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                                .limit(5)
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey, Map.Entry::getValue,
                                                (e1, e2) -> e1, LinkedHashMap::new));

                // Nouveaux utilisateurs par mois (12 derniers mois)
                Map<String, Long> nouveauxParMois = buildUsersByMonth(12);

                // Avis par artiste (nombre total d'avis sur les oeuvres de chaque artiste)
                Map<String, Long> avisParArtiste = productRepository.findAll().stream()
                                .filter(p -> p.getSeller() != null)
                                .collect(Collectors.groupingBy(
                                                p -> p.getSeller().getStoreName(),
                                                Collectors.summingLong(p -> p.getReviewCount() != null
                                                                ? p.getReviewCount()
                                                                : 0L)))
                                .entrySet().stream()
                                .filter(entry -> entry.getValue() > 0)
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(5)
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey, Map.Entry::getValue,
                                                (e1, e2) -> e1, LinkedHashMap::new));

                // Repartition des utilisateurs par role
                Map<String, Long> utilisateursParRole = new LinkedHashMap<>();
                utilisateursParRole.put("Artistes", totalSellers);
                utilisateursParRole.put("Utilisateurs normaux", totalCustomers);

                // Produits par catégorie
                Map<String, Long> produitsParCategorie = new LinkedHashMap<>();
                categoryRepository.findAll().forEach(cat -> {
                        long count = productRepository.findAll().stream()
                                        .filter(p -> p.getCategories() != null
                                                        && p.getCategories().stream()
                                                                        .anyMatch(c -> c.getId().equals(cat.getId())))
                                        .count();
                        if (count > 0) {
                                produitsParCategorie.put(cat.getName(), count);
                        }
                });

                return AdminDashboardResponse.builder()
                                .totalUsers(totalUsers)
                                .totalSellers(totalSellers)
                                .totalCustomers(totalCustomers)
                                .totalProducts(totalProducts)
                                .totalProductsActive(totalProductsActive)
                                .totalProductsArchived(totalProductsArchived)
                                .totalOrders(totalOrders)
                                .totalCategories(totalCategories)
                                .pendingOrders(pendingOrders)
                                .deliveredOrders(deliveredOrders)
                                .chiffreAffairesTotal(ca)
                                .commandesParStatut(commandesParStatut)
                                .topVendeurs(top5Vendeurs)
                                .avisParArtiste(avisParArtiste)
                                .utilisateursParRole(utilisateursParRole)
                                .nouveauxUtilisateursParMois(nouveauxParMois)
                                .produitsParCategorie(produitsParCategorie)
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // Dashboard SELLER
        // ─────────────────────────────────────────────────────────────────────────

        /**
         * Statistiques pour un vendeur connecté
         *
         * @param sellerId L'identifiant du vendeur (utilisateur)
         */
        public SellerDashboardResponse getSellerDashboard(Long sellerId) {

                // Produits du vendeur
                List<Product> produits = productRepository.findBySellerId(sellerId);
                long totalProduits = produits.size();
                long produitsActifs = produits.stream().filter(p -> Boolean.TRUE.equals(p.getActive())).count();
                long alertesStock = produits.stream().filter(p -> p.getStock() != null && p.getStock() < 5).count();

                // Ids des produits du vendeur
                Set<Long> productIds = produits.stream().map(Product::getId).collect(Collectors.toSet());

                // OrderItems liés aux produits du vendeur
                var sellerItems = orderItemRepository.findAll().stream()
                                .filter(item -> item.getProduct() != null
                                                && productIds.contains(item.getProduct().getId()))
                                .collect(Collectors.toList());

                // Ordres distincts liés à ce vendeur
                Set<Long> orderIds = sellerItems.stream()
                                .filter(item -> item.getOrder() != null)
                                .map(item -> item.getOrder().getId())
                                .collect(Collectors.toSet());

                List<com.shopflow.entity.Order> orders = orderIds.isEmpty()
                                ? Collections.emptyList()
                                : orderRepository.findAll().stream()
                                                .filter(o -> orderIds.contains(o.getId()))
                                                .collect(Collectors.toList());

                long totalCommandes = orders.size();
                long commandesEnAttente = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
                long commandesExpediees = orders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count();
                long commandesLivrees = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();

                // Chiffre d'affaires (somme prixUnitaire × quantite des items du vendeur, hors
                // annulé)
                BigDecimal ca = sellerItems.stream()
                                .filter(item -> item.getOrder() != null
                                                && item.getOrder().getStatus() != OrderStatus.CANCELLED
                                                && item.getOrder().getStatus() != OrderStatus.REFUNDED)
                                .map(item -> item.getUnitPrice()
                                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // ── Graphiques ──────────────────────────────────────────────────────

                // Commandes par statut
                Map<String, Long> commandesParStatut = orders.stream()
                                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

                // Revenus par mois (6 derniers mois)
                Map<String, BigDecimal> revenusParMois = buildRevenueByMonth(sellerItems, 6);

                // Top 5 produits (nombre d'items vendus)
                Map<String, Long> topProduits = sellerItems.stream()
                                .filter(item -> item.getProduct() != null)
                                .collect(Collectors.groupingBy(
                                                item -> item.getProduct().getName(),
                                                Collectors.summingLong(item -> item.getQuantity())))
                                .entrySet().stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(5)
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey, Map.Entry::getValue,
                                                (e1, e2) -> e1, LinkedHashMap::new));

                return SellerDashboardResponse.builder()
                                .totalProduits(totalProduits)
                                .produitsActifs(produitsActifs)
                                .totalCommandes(totalCommandes)
                                .commandesEnAttente(commandesEnAttente)
                                .commandesExpediees(commandesExpediees)
                                .commandesLivrees(commandesLivrees)
                                .chiffreAffaires(ca)
                                .alertesStockFaible(alertesStock)
                                .commandesParStatut(commandesParStatut)
                                .revenusParMois(revenusParMois)
                                .topProduits(topProduits)
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // Helpers privés
        // ─────────────────────────────────────────────────────────────────────────

        private Map<String, Long> buildUsersByMonth(int nbrMois) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
                LocalDateTime limite = LocalDateTime.now().minusMonths(nbrMois);

                Map<String, Long> result = new LinkedHashMap<>();
                userRepository.findAll().stream()
                                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(limite))
                                .forEach(u -> {
                                        String mois = u.getCreatedAt().format(fmt);
                                        result.merge(mois, 1L, Long::sum);
                                });
                return result;
        }

        private Map<String, BigDecimal> buildRevenueByMonth(
                        List<com.shopflow.entity.OrderItem> items, int nbrMois) {

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
                LocalDateTime limite = LocalDateTime.now().minusMonths(nbrMois);

                Map<String, BigDecimal> result = new LinkedHashMap<>();
                items.stream()
                                .filter(item -> item.getOrder() != null
                                                && item.getOrder().getCreatedAt() != null
                                                && item.getOrder().getCreatedAt().isAfter(limite)
                                                && item.getOrder().getStatus() != OrderStatus.CANCELLED
                                                && item.getOrder().getStatus() != OrderStatus.REFUNDED)
                                .forEach(item -> {
                                        String mois = item.getOrder().getCreatedAt().format(fmt);
                                        BigDecimal rev = item.getUnitPrice()
                                                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                                        result.merge(mois, rev, BigDecimal::add);
                                });
                return result;
        }
}
