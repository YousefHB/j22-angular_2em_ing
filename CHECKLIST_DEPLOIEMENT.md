# ✅ Checklist de Déploiement ShopFlow

## 📋 Avant de Commencer

- [ ] J'ai lu `README_DEPLOIEMENT.md`
- [ ] J'ai lu `DEPLOIEMENT_RAPIDE.md`
- [ ] J'ai un compte GitHub
- [ ] J'ai un compte Render (https://render.com)
- [ ] Mon frontend est déjà déployé sur Firebase ✅
- [ ] J'ai 30 minutes devant moi

---

## 🔐 Étape 1 : Générer JWT Secret (2 min)

- [ ] Ouvrir PowerShell dans le dossier du projet
- [ ] Exécuter : `.\generate-jwt-secret.ps1`
- [ ] Copier le secret généré
- [ ] Sauvegarder le secret dans un endroit sûr (Notepad, etc.)

**✅ Secret généré et sauvegardé**

---

## 📤 Étape 2 : Pousser le Code sur GitHub (3 min)

- [ ] Ouvrir le terminal dans le dossier du projet
- [ ] Exécuter :
  ```bash
  git add .
  git commit -m "Configure backend for Render deployment"
  git push origin main
  ```
- [ ] Vérifier sur GitHub que le code est bien poussé
- [ ] Vérifier que les nouveaux fichiers sont présents :
  - [ ] `render.yaml`
  - [ ] `src/main/resources/application-prod.properties`
  - [ ] `start.sh`
  - [ ] `.env.example`

**✅ Code poussé sur GitHub**

---

## 🗄️ Étape 3 : Créer la Base de Données (5 min)

- [ ] Aller sur https://dashboard.render.com
- [ ] Se connecter ou créer un compte
- [ ] Cliquer sur **"New +"** → **"PostgreSQL"**
- [ ] Remplir le formulaire :
  - [ ] Name : `shopflow-db`
  - [ ] Database : `shopflow_db`
  - [ ] User : (généré automatiquement)
  - [ ] Region : Choisir le plus proche (ex: Frankfurt)
  - [ ] Plan : **Free**
- [ ] Cliquer sur **"Create Database"**
- [ ] Attendre que la DB soit créée (~2 min)
- [ ] Copier les informations de connexion :
  - [ ] **Internal Database URL** (commence par `postgres://...`)
  - [ ] **Username**
  - [ ] **Password**
- [ ] Sauvegarder ces informations dans un Notepad

**✅ Base de données créée et infos sauvegardées**

---

## 🚀 Étape 4 : Créer le Web Service (10 min)

### 4.1 Configuration Initiale

- [ ] Sur Render Dashboard, cliquer sur **"New +"** → **"Web Service"**
- [ ] Cliquer sur **"Connect GitHub"** (si pas déjà fait)
- [ ] Autoriser Render à accéder à vos repos
- [ ] Sélectionner le repository **shopflow**
- [ ] Cliquer sur **"Connect"**

### 4.2 Configuration du Service

- [ ] Remplir le formulaire :
  - [ ] **Name** : `shopflow-backend`
  - [ ] **Region** : Même région que la DB (ex: Frankfurt)
  - [ ] **Branch** : `main`
  - [ ] **Root Directory** : (laisser vide)
  - [ ] **Runtime** : **Java**
  - [ ] **Build Command** : `./mvnw clean package -DskipTests`
  - [ ] **Start Command** : `java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/shopflow-0.0.1-SNAPSHOT.jar`
  - [ ] **Plan** : **Free**

### 4.3 Variables d'Environnement

- [ ] Cliquer sur **"Advanced"**
- [ ] Cliquer sur **"Add Environment Variable"**
- [ ] Ajouter les variables suivantes :

| Variable | Valeur | Fait |
|----------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `prod` | [ ] |
| `DATABASE_URL` | `jdbc:postgresql://[COLLER_INTERNAL_URL]` | [ ] |
| `DB_USERNAME` | `[USERNAME_DE_LA_DB]` | [ ] |
| `DB_PASSWORD` | `[PASSWORD_DE_LA_DB]` | [ ] |
| `JWT_SECRET` | `[SECRET_GENERE_ETAPE_1]` | [ ] |
| `FRONTEND_URL` | `https://shopflow-25917.web.app` | [ ] |
| `PORT` | `8084` | [ ] |

**⚠️ Important pour DATABASE_URL :**
- [ ] Remplacer `postgres://` par `jdbc:postgresql://`
- [ ] Format final : `jdbc:postgresql://dpg-xxxxx:5432/shopflow_db`

### 4.4 Déploiement

- [ ] Vérifier que toutes les variables sont correctes
- [ ] Cliquer sur **"Create Web Service"**
- [ ] Attendre le déploiement (5-10 min)

**✅ Web Service créé**

---

## 📊 Étape 5 : Surveiller le Déploiement (5 min)

- [ ] Aller dans l'onglet **"Logs"** du service
- [ ] Surveiller les logs en temps réel
- [ ] Chercher les messages suivants :
  - [ ] `BUILD SUCCESSFUL`
  - [ ] `Started ShopFlowApplication in X seconds`
  - [ ] Pas d'erreurs rouges

**Si erreurs :**
- [ ] Vérifier les variables d'environnement
- [ ] Vérifier DATABASE_URL (format correct ?)
- [ ] Vérifier JWT_SECRET (32+ caractères ?)
- [ ] Consulter `DEPLOYMENT_GUIDE.md` section "Dépannage"

**✅ Backend déployé avec succès**

---

## 🔗 Étape 6 : Tester le Backend (3 min)

- [ ] Noter l'URL du backend : `https://shopflow-backend.onrender.com`
- [ ] Ouvrir dans le navigateur : `https://shopflow-backend.onrender.com/api/swagger-ui.html`
- [ ] Vérifier que Swagger UI s'affiche
- [ ] Tester un endpoint (ex: GET /api/products)

**✅ Backend accessible et fonctionnel**

---

## 🎨 Étape 7 : Mettre à Jour le Frontend (10 min)

### 7.1 Modifier les Services

- [ ] Ouvrir le projet frontend dans l'éditeur
- [ ] Modifier **TOUS** les services suivants :

**Services à modifier :**
- [ ] `frontend/src/services/auth.service.ts`
- [ ] `frontend/src/services/user.service.ts`
- [ ] `frontend/src/services/product.service.ts`
- [ ] `frontend/src/services/category.service.ts`
- [ ] `frontend/src/services/cart.service.ts`
- [ ] `frontend/src/services/order.service.ts`
- [ ] `frontend/src/services/address.service.ts`
- [ ] `frontend/src/services/coupon.service.ts`
- [ ] `frontend/src/services/review.service.ts`
- [ ] `frontend/src/services/dashboard.service.ts`

**Pour chaque service :**
- [ ] Remplacer `const API_URL = 'http://localhost:8084/api';`
- [ ] Par :
  ```typescript
  import { environment } from '../environments/environment';
  const API_URL = environment.apiUrl;
  ```

### 7.2 Mettre à Jour environment.prod.ts

- [ ] Ouvrir `frontend/src/environments/environment.prod.ts`
- [ ] Vérifier que l'URL est correcte :
  ```typescript
  export const environment = {
    production: true,
    apiUrl: 'https://shopflow-backend.onrender.com/api'
  };
  ```
- [ ] Remplacer `shopflow-backend` par le nom de votre service Render si différent

### 7.3 Rebuild et Redéployer

- [ ] Ouvrir le terminal dans le dossier `frontend`
- [ ] Exécuter :
  ```bash
  npm run build
  ```
- [ ] Vérifier qu'il n'y a pas d'erreurs
- [ ] Exécuter :
  ```bash
  firebase deploy
  ```
- [ ] Attendre la fin du déploiement

**✅ Frontend mis à jour et redéployé**

---

## 🧪 Étape 8 : Tester l'Application Complète (5 min)

### 8.1 Tests Fonctionnels

- [ ] Ouvrir : https://shopflow-25917.web.app
- [ ] Ouvrir la console du navigateur (F12)
- [ ] Vérifier qu'il n'y a pas d'erreurs CORS
- [ ] Vérifier que les requêtes vont vers `https://shopflow-backend.onrender.com`

### 8.2 Tests Utilisateur

- [ ] Tester l'inscription d'un nouveau compte
- [ ] Tester la connexion
- [ ] Vérifier que le token JWT est stocké (localStorage)
- [ ] Naviguer sur la page des produits
- [ ] Vérifier que les produits s'affichent
- [ ] Tester l'ajout au panier
- [ ] Tester la création d'une commande

**✅ Application complète fonctionnelle**

---

## 🎉 Étape 9 : Finalisation

### 9.1 Documentation

- [ ] Sauvegarder les URLs importantes :
  - [ ] Frontend : `https://shopflow-25917.web.app`
  - [ ] Backend : `https://shopflow-backend.onrender.com`
  - [ ] Swagger : `https://shopflow-backend.onrender.com/api/swagger-ui.html`
- [ ] Sauvegarder les identifiants Render
- [ ] Sauvegarder le JWT Secret

### 9.2 Monitoring

- [ ] Ajouter le dashboard Render aux favoris
- [ ] Configurer les notifications Render (optionnel)
- [ ] Surveiller les logs pendant les premières heures

### 9.3 Optimisations Futures

- [ ] Considérer un plan payant si beaucoup de trafic
- [ ] Ajouter un domaine personnalisé (optionnel)
- [ ] Configurer un CDN (optionnel)
- [ ] Ajouter des tests automatisés (optionnel)

**✅ Déploiement terminé avec succès !**

---

## 📊 Résumé Final

```
✅ JWT Secret généré
✅ Code poussé sur GitHub
✅ Base de données PostgreSQL créée sur Render
✅ Web Service backend déployé sur Render
✅ Backend accessible et fonctionnel
✅ Frontend mis à jour avec la bonne URL
✅ Frontend redéployé sur Firebase
✅ Application complète testée et fonctionnelle
```

---

## 🐛 En Cas de Problème

### Backend ne démarre pas
→ Consulter `DEPLOYMENT_GUIDE.md` section "Dépannage"

### Erreur CORS
→ Vérifier `FRONTEND_URL` dans les variables d'environnement Render

### Frontend ne se connecte pas au backend
→ Vérifier `environment.prod.ts` et les services modifiés

### Besoin d'aide
→ Consulter les guides :
- `DEPLOIEMENT_RAPIDE.md`
- `DEPLOYMENT_GUIDE.md`
- `MISE_A_JOUR_FRONTEND.md`

---

## 🎯 Prochaines Étapes

- [ ] Ajouter des données de test
- [ ] Inviter des utilisateurs à tester
- [ ] Surveiller les performances
- [ ] Planifier les prochaines features
- [ ] Configurer un domaine personnalisé (optionnel)

---

## 🎊 Félicitations !

Votre application ShopFlow est maintenant déployée en production ! 🚀

**URLs de votre application :**
- Frontend : https://shopflow-25917.web.app
- Backend : https://shopflow-backend.onrender.com
- API Docs : https://shopflow-backend.onrender.com/api/swagger-ui.html

---

**Date de déploiement :** _______________  
**Temps total :** _______________  
**Notes :** _______________________________________________
