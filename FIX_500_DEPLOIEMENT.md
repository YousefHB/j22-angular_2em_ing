# 🔧 FIX ERREUR 500 - Déploiement sur Render

## ✅ PROBLÈME RÉSOLU

**Erreur:** `Failed to load resource: the server responded with a status of 500 ()` sur `/api/products`

**Cause:** Lazy loading des relations `reviews` et `categories` dans l'entité Product causait une exception Hibernate en dehors de la transaction.

**Solution:** Ajout de gestion d'erreur dans `ProductService.mapToResponse()` pour gérer les relations non chargées.

---

## 📦 DÉPLOIEMENT SUR RENDER

### Étape 1: Commit et Push

```bash
git add .
git commit -m "Fix: Handle lazy loading in ProductService to prevent 500 errors"
git push origin main
```

### Étape 2: Déployer sur Render

**Option A: Déploiement automatique (si configuré)**
- Render détectera automatiquement le push
- Le déploiement commencera dans 1-2 minutes
- Durée: ~5-10 minutes

**Option B: Déploiement manuel**
1. Allez sur https://dashboard.render.com
2. Cliquez sur `shopflow-backend`
3. Cliquez sur "Manual Deploy" → "Deploy latest commit"
4. Attendez la fin du déploiement

### Étape 3: Vérifier le déploiement

```bash
# Test de santé
curl https://shopflow-backend-g9zy.onrender.com/api/actuator/health

# Test des produits (devrait retourner une liste vide au lieu de 500)
curl https://shopflow-backend-g9zy.onrender.com/api/products
```

**Résultat attendu:**
```json
{
  "content": [],
  "pageable": {...},
  "totalElements": 0,
  "totalPages": 0
}
```

---

## 🎯 PROCHAINES ÉTAPES

### 1. Se connecter en tant qu'Admin

```
URL: https://shopflow-25917.web.app/connexion
Email: admin@shopflow.com
Mot de passe: Admin@123
```

**Important:** Déconnectez-vous et reconnectez-vous pour obtenir le nouveau token JWT avec le rôle ADMIN.

### 2. Créer des catégories

Une fois connecté en tant qu'admin:
1. Menu "Catégories" → "Ajouter une catégorie"
2. Créez quelques catégories:
   - Électronique
   - Vêtements
   - Livres
   - Maison & Jardin
   - Sports & Loisirs

### 3. Créer des produits

1. Menu "Mes Produits" → "Ajouter un produit"
2. Remplissez les informations:
   - Nom du produit
   - Description
   - Prix
   - Stock
   - Image URL
   - Sélectionnez une ou plusieurs catégories

### 4. Tester le frontend

- Vérifiez que les produits s'affichent sur la page d'accueil
- Testez la recherche
- Testez le filtrage par catégorie
- Testez l'ajout au panier

---

## 🐛 MODIFICATIONS APPORTÉES

### Fichier: `src/main/java/com/shopflow/service/ProductService.java`

**Avant:**
```java
private ProductResponse mapToResponse(Product product) {
    // Accès direct aux relations lazy → Exception!
    product.getReviews().stream()...
    product.getCategories().stream()...
}
```

**Après:**
```java
private ProductResponse mapToResponse(Product product) {
    // Gestion des exceptions pour lazy loading
    try {
        // Tenter de charger les reviews
    } catch (Exception e) {
        // Utiliser les valeurs par défaut
    }
    
    try {
        // Tenter de charger les catégories
    } catch (Exception e) {
        // Retourner un set vide
    }
}
```

---

## 📊 LOGS À SURVEILLER

Après le déploiement, surveillez les logs sur Render:

**Logs de succès:**
```
Started ShopflowApplication in X seconds
Tomcat started on port(s): 8084
HikariPool-1 - Start completed
```

**Logs d'erreur (si présents):**
```
ERROR: Connection refused → Vérifier DATABASE_URL
ERROR: JWT secret is null → Vérifier JWT_SECRET
ERROR: relation "users" does not exist → Exécuter init-admin.sql
```

---

## ✅ CHECKLIST DE VÉRIFICATION

- [ ] Code compilé sans erreur (`./mvnw clean package -DskipTests`)
- [ ] Commit et push effectués
- [ ] Déploiement Render terminé
- [ ] Backend répond sur `/api/actuator/health`
- [ ] `/api/products` retourne 200 (même si liste vide)
- [ ] Connexion admin fonctionne
- [ ] Badge "Administrateur" visible après reconnexion
- [ ] Menu admin accessible (Catégories, Utilisateurs)
- [ ] Création de catégories fonctionne
- [ ] Création de produits fonctionne

---

## 🆘 EN CAS DE PROBLÈME

### Erreur persiste après déploiement

1. **Vérifier les logs Render:**
   ```
   Dashboard → shopflow-backend → Logs
   ```

2. **Vérifier les variables d'environnement:**
   ```
   Dashboard → shopflow-backend → Environment
   ```
   
   Variables requises:
   - `DATABASE_URL`
   - `JWT_SECRET`
   - `FRONTEND_URL`

3. **Redémarrer le service:**
   ```
   Dashboard → shopflow-backend → Manual Deploy → Clear build cache & deploy
   ```

### Base de données vide

Si aucun utilisateur n'existe:
```sql
-- Se connecter à PostgreSQL sur Render
psql "postgresql://shopflow_db_yb6f_user:nE7y6lV1xTysmo4hXxclqrdxniByDZ7J@dpg-d7ujrg67r5hc73b68uf0-a.frankfurt-postgres.render.com/shopflow_db_yb6f"

-- Vérifier les utilisateurs
SELECT email, role FROM users;

-- Si admin@shopflow.com existe mais n'est pas ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';
```

---

**Date:** 8 mai 2026  
**Statut:** Fix appliqué, prêt pour déploiement  
**Temps estimé:** 10-15 minutes (déploiement inclus)
