# 🏗️ Architecture de Déploiement ShopFlow

## 📊 Vue d'Ensemble

```
┌─────────────────────────────────────────────────────────────┐
│                         UTILISATEUR                          │
│                    (Navigateur Web)                          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular)                        │
│                                                              │
│  🔥 Firebase Hosting                                         │
│  📍 https://shopflow-25917.web.app                          │
│                                                              │
│  ├─ HTML/CSS/JavaScript                                     │
│  ├─ Angular Components                                      │
│  └─ Services (API Calls)                                    │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ REST API (HTTPS)
                         │ CORS Enabled
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   BACKEND (Spring Boot)                      │
│                                                              │
│  🚀 Render Web Service                                       │
│  📍 https://shopflow-backend.onrender.com                   │
│                                                              │
│  ├─ REST Controllers                                        │
│  ├─ Services (Business Logic)                               │
│  ├─ Security (JWT)                                          │
│  └─ JPA Repositories                                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ JDBC
                         │ PostgreSQL Protocol
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  BASE DE DONNÉES                             │
│                                                              │
│  🗄️ Render PostgreSQL                                        │
│  📍 Internal Database URL                                    │
│                                                              │
│  ├─ Tables (Users, Products, Orders, etc.)                  │
│  ├─ Relations                                               │
│  └─ Indexes                                                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flux de Données

### 1. Authentification
```
Utilisateur
    │
    ├─> Frontend (Login Form)
    │       │
    │       └─> POST /api/auth/login
    │               │
    │               └─> Backend (AuthController)
    │                       │
    │                       ├─> Vérification DB
    │                       └─> Génération JWT
    │                               │
    │                               └─> Token renvoyé
    │                                       │
    │                                       └─> Stocké dans localStorage
    │
    └─> Requêtes suivantes avec JWT dans Header
```

### 2. Récupération de Données
```
Utilisateur
    │
    └─> Frontend (Product List)
            │
            └─> GET /api/products
                    │ (Header: Authorization: Bearer <JWT>)
                    │
                    └─> Backend (ProductController)
                            │
                            ├─> Validation JWT
                            ├─> Query Database
                            └─> Return JSON
                                    │
                                    └─> Frontend affiche les produits
```

---

## 🔐 Sécurité

### CORS (Cross-Origin Resource Sharing)
```
Frontend (Firebase)                Backend (Render)
https://shopflow-25917.web.app --> https://shopflow-backend.onrender.com
                                    │
                                    ├─> CORS Headers:
                                    │   - Access-Control-Allow-Origin
                                    │   - Access-Control-Allow-Methods
                                    │   - Access-Control-Allow-Headers
                                    │
                                    └─> Configured in SecurityConfig.java
```

### JWT (JSON Web Token)
```
1. Login
   User → Backend: username + password
   Backend → User: JWT Token

2. Requêtes Authentifiées
   User → Backend: Request + JWT in Header
   Backend: Validate JWT → Process Request

3. Token Structure
   Header.Payload.Signature
   │      │       │
   │      │       └─> Signature (secret key)
   │      └─> User info (id, role, exp)
   └─> Algorithm (HS256)
```

---

## 🌍 Environnements

### Développement (Local)
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Frontend      │────▶│    Backend      │────▶│    Database     │
│                 │     │                 │     │                 │
│ localhost:4200  │     │ localhost:8084  │     │ localhost:3306  │
│                 │     │                 │     │                 │
│ Angular Dev     │     │ Spring Boot     │     │ MySQL           │
│ Server          │     │ (profile: dev)  │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Production (Cloud)
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Frontend      │────▶│    Backend      │────▶│    Database     │
│                 │     │                 │     │                 │
│ Firebase        │     │ Render          │     │ Render          │
│ Hosting         │     │ Web Service     │     │ PostgreSQL      │
│                 │     │                 │     │                 │
│ shopflow-       │     │ shopflow-       │     │ shopflow-db     │
│ 25917.web.app   │     │ backend         │     │                 │
│                 │     │ (profile: prod) │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## 📦 Déploiement Automatique

### Backend (Render)
```
GitHub Repository
    │
    │ git push origin main
    │
    ▼
Render Webhook
    │
    ├─> Détecte le push
    ├─> Clone le repository
    ├─> Execute: ./mvnw clean package -DskipTests
    ├─> Build le JAR
    ├─> Execute: java -jar target/shopflow-0.0.1-SNAPSHOT.jar
    │
    └─> Service déployé et accessible
```

### Frontend (Firebase)
```
Local Machine
    │
    │ npm run build
    │
    ▼
dist/frontend/
    │
    │ firebase deploy
    │
    ▼
Firebase Hosting
    │
    └─> Application déployée
```

---

## 🔧 Configuration

### Variables d'Environnement (Backend)
```
┌─────────────────────────────────────────────────────────────┐
│                    Render Dashboard                          │
│                                                              │
│  Environment Variables:                                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ SPRING_PROFILES_ACTIVE = prod                          │ │
│  │ DATABASE_URL = jdbc:postgresql://...                   │ │
│  │ DB_USERNAME = shopflow_user                            │ │
│  │ DB_PASSWORD = ********                                 │ │
│  │ JWT_SECRET = ********************************          │ │
│  │ FRONTEND_URL = https://shopflow-25917.web.app          │ │
│  │ PORT = 8084                                            │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ↓ Injectées dans l'application au démarrage                │
│                                                              │
│  application-prod.properties:                                │
│  spring.datasource.url=${DATABASE_URL}                       │
│  spring.datasource.username=${DB_USERNAME}                   │
│  app.jwt.secret=${JWT_SECRET}                                │
└─────────────────────────────────────────────────────────────┘
```

### Configuration Frontend
```
┌─────────────────────────────────────────────────────────────┐
│                  Build Configuration                         │
│                                                              │
│  Development:                                                │
│  environment.ts                                              │
│  ├─ production: false                                        │
│  └─ apiUrl: 'http://localhost:8084/api'                     │
│                                                              │
│  Production:                                                 │
│  environment.prod.ts                                         │
│  ├─ production: true                                         │
│  └─ apiUrl: 'https://shopflow-backend.onrender.com/api'     │
│                                                              │
│  angular.json remplace environment.ts par environment.prod.ts│
│  lors du build de production                                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 Monitoring & Logs

### Backend (Render)
```
Render Dashboard
    │
    ├─> Logs (Real-time)
    │   ├─ Application logs
    │   ├─ Error logs
    │   └─ Access logs
    │
    ├─> Metrics
    │   ├─ CPU Usage
    │   ├─ Memory Usage
    │   ├─ Request Count
    │   └─ Response Time
    │
    └─> Events
        ├─ Deployments
        ├─ Restarts
        └─ Errors
```

### Frontend (Firebase)
```
Firebase Console
    │
    ├─> Hosting
    │   ├─ Deployment History
    │   ├─ Usage Statistics
    │   └─ Custom Domain
    │
    └─> Analytics (optionnel)
        ├─ Page Views
        ├─ User Sessions
        └─ Performance
```

---

## 💰 Coûts

### Plan Gratuit (Free Tier)

| Service | Plan | Limitations | Coût |
|---------|------|-------------|------|
| **Firebase Hosting** | Spark | 10 GB storage, 360 MB/day transfer | **Gratuit** |
| **Render Web Service** | Free | 750h/mois, sleep après 15 min | **Gratuit** |
| **Render PostgreSQL** | Free | 1 GB storage, expire après 90 jours | **Gratuit** |

### Limitations du Plan Gratuit

**Render Web Service:**
- ⏰ Service s'endort après 15 min d'inactivité
- 🐌 Premier appel après sommeil: ~30 secondes
- 💾 512 MB RAM
- 🔄 Redémarrage automatique toutes les 24h

**Solutions:**
1. Accepter le délai (gratuit)
2. Pinger le service régulièrement (cron job)
3. Upgrade vers plan payant ($7/mois)

---

## 🚀 Performance

### Optimisations Possibles

**Frontend:**
- ✅ Build de production (minification, tree-shaking)
- ✅ Lazy loading des modules Angular
- ✅ CDN Firebase (distribution mondiale)
- 🔄 Service Worker (PWA) - à implémenter
- 🔄 Image optimization - à implémenter

**Backend:**
- ✅ Connection pooling (HikariCP)
- ✅ JPA query optimization
- 🔄 Redis cache - à implémenter
- 🔄 Database indexes - à optimiser
- 🔄 API rate limiting - à implémenter

**Base de Données:**
- ✅ Indexes sur clés étrangères
- 🔄 Query optimization
- 🔄 Database replication (plan payant)

---

## 🔄 Workflow de Développement

```
1. Développement Local
   ├─ Coder les features
   ├─ Tester localement
   └─ Commit sur Git

2. Push sur GitHub
   ├─ git push origin main
   └─ Trigger Render webhook

3. Déploiement Automatique (Backend)
   ├─ Render détecte le push
   ├─ Build l'application
   ├─ Déploie automatiquement
   └─ Service accessible en ~5 min

4. Déploiement Manuel (Frontend)
   ├─ npm run build
   ├─ firebase deploy
   └─ Application mise à jour

5. Test en Production
   ├─ Vérifier les fonctionnalités
   ├─ Surveiller les logs
   └─ Corriger si nécessaire
```

---

## 📞 Support & Documentation

### Render
- 📚 Docs: https://render.com/docs
- 💬 Community: https://community.render.com
- 📧 Support: support@render.com

### Firebase
- 📚 Docs: https://firebase.google.com/docs
- 💬 Community: https://firebase.google.com/community
- 📧 Support: Firebase Console

### Spring Boot
- 📚 Docs: https://spring.io/guides
- 💬 Community: https://stackoverflow.com/questions/tagged/spring-boot

---

Bonne compréhension de l'architecture ! 🏗️
