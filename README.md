# 🛒 ShopFlow - Plateforme E-commerce

## 📋 Description

ShopFlow est une plateforme e-commerce complète avec gestion des vendeurs et des clients, développée avec Spring Boot (backend) et Angular (frontend).

---

## 🚀 Déploiement

### ✅ Frontend Déployé
- **URL** : https://shopflow-25917.web.app
- **Plateforme** : Firebase Hosting

### ⏳ Backend À Déployer
- **Plateforme cible** : Render
- **Base de données** : PostgreSQL (Render)

---

## 📚 Documentation de Déploiement

### 🎯 Commencer Ici
👉 **[INDEX_DOCUMENTATION.md](INDEX_DOCUMENTATION.md)** - Index complet de la documentation

### 🚀 Guides Rapides

| Guide | Description | Temps |
|-------|-------------|-------|
| **[DEPLOIEMENT_RAPIDE.md](DEPLOIEMENT_RAPIDE.md)** | Guide express en français | 10 min |
| **[CHECKLIST_DEPLOIEMENT.md](CHECKLIST_DEPLOIEMENT.md)** | Checklist interactive | 30 min |
| **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** | Guide complet en anglais | 30 min |

### 📖 Documentation Complète

- **[README_DEPLOIEMENT.md](README_DEPLOIEMENT.md)** - Vue d'ensemble du déploiement
- **[MISE_A_JOUR_FRONTEND.md](MISE_A_JOUR_FRONTEND.md)** - Connecter le frontend au backend
- **[ARCHITECTURE_DEPLOIEMENT.md](ARCHITECTURE_DEPLOIEMENT.md)** - Architecture et flux de données
- **[CHANGEMENTS_DEPLOIEMENT.md](CHANGEMENTS_DEPLOIEMENT.md)** - Liste des modifications
- **[RESUME_FINAL.md](RESUME_FINAL.md)** - Résumé final

---

## 🏗️ Architecture

```
Frontend (Angular)          Backend (Spring Boot)       Database
Firebase Hosting     →      Render Web Service    →     Render PostgreSQL
shopflow-25917.web.app      shopflow-backend            shopflow-db
```

---

## 🛠️ Technologies

### Backend
- **Framework** : Spring Boot 3.5.13
- **Langage** : Java 17
- **Base de données** : MySQL (dev) / PostgreSQL (prod)
- **Sécurité** : Spring Security + JWT
- **Documentation API** : Swagger/OpenAPI
- **Build** : Maven

### Frontend
- **Framework** : Angular 16
- **Langage** : TypeScript
- **Styling** : SCSS
- **Build** : Angular CLI

---

## 📦 Installation Locale

### Prérequis
- Java 17+
- Node.js 16+
- MySQL 8+
- Maven 3.8+

### Backend
```bash
# Cloner le repository
git clone <repository-url>
cd shopflow

# Configurer la base de données
# Créer une base de données MySQL : shopflow_db

# Configurer application.properties
# src/main/resources/application.properties

# Lancer l'application
./mvnw spring-boot:run

# API disponible sur : http://localhost:8084/api
# Swagger UI : http://localhost:8084/api/swagger-ui.html
```

### Frontend
```bash
# Aller dans le dossier frontend
cd frontend

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm start

# Application disponible sur : http://localhost:4200
```

---

## 🔐 Configuration

### Variables d'Environnement (Production)

Voir **[.env.example](.env.example)** pour la liste complète.

Principales variables :
- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL` - URL de connexion à la base de données
- `JWT_SECRET` - Secret pour les tokens JWT (32+ caractères)
- `FRONTEND_URL` - URL du frontend pour CORS

### Générer un JWT Secret
```powershell
.\generate-jwt-secret.ps1
```

---

## 🚀 Déploiement en Production

### Étapes Rapides

1. **Générer JWT Secret**
   ```powershell
   .\generate-jwt-secret.ps1
   ```

2. **Pousser sur GitHub**
   ```bash
   git add .
   git commit -m "Configure for deployment"
   git push origin main
   ```

3. **Déployer sur Render**
   - Suivre **[DEPLOIEMENT_RAPIDE.md](DEPLOIEMENT_RAPIDE.md)**

4. **Mettre à jour le Frontend**
   - Suivre **[MISE_A_JOUR_FRONTEND.md](MISE_A_JOUR_FRONTEND.md)**

### Documentation Détaillée

Consultez **[INDEX_DOCUMENTATION.md](INDEX_DOCUMENTATION.md)** pour tous les guides disponibles.

---

## 📊 Fonctionnalités

### Utilisateurs
- ✅ Inscription et connexion (JWT)
- ✅ Gestion de profil
- ✅ Gestion d'adresses
- ✅ Historique des commandes

### Produits
- ✅ Catalogue de produits
- ✅ Catégories
- ✅ Recherche et filtres
- ✅ Avis et notes

### Panier & Commandes
- ✅ Gestion du panier
- ✅ Création de commandes
- ✅ Suivi des commandes
- ✅ Coupons de réduction

### Vendeurs
- ✅ Gestion des produits
- ✅ Gestion des commandes
- ✅ Statistiques et dashboard

### Administrateurs
- ✅ Gestion des utilisateurs
- ✅ Gestion des catégories
- ✅ Statistiques globales
- ✅ Modération

---

## 🔒 Sécurité

- **Authentification** : JWT (JSON Web Tokens)
- **Autorisation** : Role-based (CUSTOMER, SELLER, ADMIN)
- **CORS** : Configuré pour Firebase Hosting
- **Validation** : Bean Validation (JSR-380)
- **Passwords** : BCrypt hashing

---

## 📝 API Documentation

### Swagger UI
- **Local** : http://localhost:8084/api/swagger-ui.html
- **Production** : https://shopflow-backend.onrender.com/api/swagger-ui.html

### Endpoints Principaux

| Endpoint | Description | Auth |
|----------|-------------|------|
| `POST /api/auth/register` | Inscription | Public |
| `POST /api/auth/login` | Connexion | Public |
| `GET /api/products` | Liste des produits | Public |
| `POST /api/cart/add` | Ajouter au panier | User |
| `POST /api/orders` | Créer une commande | User |
| `GET /api/dashboard` | Statistiques | User |

---

## 🧪 Tests

### Backend
```bash
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

---

## 🐛 Dépannage

### Problèmes Courants

**Backend ne démarre pas**
→ Vérifier la connexion à la base de données dans `application.properties`

**Erreur CORS**
→ Vérifier la configuration CORS dans `SecurityConfig.java`

**Frontend ne se connecte pas**
→ Vérifier l'URL de l'API dans `environment.ts`

### Documentation Complète

Consultez **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** section "Dépannage"

---

## 📞 Support

### Documentation
- **[INDEX_DOCUMENTATION.md](INDEX_DOCUMENTATION.md)** - Index complet
- **[DEPLOIEMENT_RAPIDE.md](DEPLOIEMENT_RAPIDE.md)** - Guide rapide
- **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Guide complet

### Ressources Externes
- **Render** : https://render.com/docs
- **Firebase** : https://firebase.google.com/docs
- **Spring Boot** : https://spring.io/guides
- **Angular** : https://angular.io/docs

---

## 📄 Licence

Ce projet est sous licence MIT.

---

## 👥 Contributeurs

- Développeur principal : [Votre nom]

---

## 🎯 Roadmap

### Version Actuelle (v1.0)
- ✅ Authentification JWT
- ✅ Gestion des produits
- ✅ Panier et commandes
- ✅ Dashboard vendeur/admin
- ✅ Déploiement Firebase + Render

### Prochaines Versions
- 🔄 Paiement en ligne (Stripe)
- 🔄 Notifications email
- 🔄 Chat vendeur-client
- 🔄 Application mobile
- 🔄 Analytics avancés

---

## 🚀 Démarrage Rapide

### Pour Développer
```bash
# Backend
./mvnw spring-boot:run

# Frontend (nouveau terminal)
cd frontend && npm start
```

### Pour Déployer
```bash
# Lire la documentation
cat INDEX_DOCUMENTATION.md

# Suivre le guide rapide
cat DEPLOIEMENT_RAPIDE.md
```

---

## 📊 Statut du Projet

- **Frontend** : ✅ Déployé sur Firebase
- **Backend** : ⏳ Prêt pour déploiement sur Render
- **Documentation** : ✅ Complète
- **Tests** : 🔄 En cours

---

## 🎉 Remerciements

Merci d'utiliser ShopFlow ! Pour toute question, consultez la documentation ou ouvrez une issue.

---

**Prochaine étape :** Consultez **[INDEX_DOCUMENTATION.md](INDEX_DOCUMENTATION.md)** pour déployer votre application ! 🚀
