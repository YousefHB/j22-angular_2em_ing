# 🔄 Mise à Jour du Frontend pour le Déploiement

## 📝 Changements Nécessaires

Après avoir déployé le backend sur Render, vous devez mettre à jour le frontend pour qu'il utilise la bonne URL de l'API.

---

## ✅ Étape 1 : Fichiers d'Environnement Créés

J'ai créé deux fichiers d'environnement :

### `frontend/src/environments/environment.ts` (Développement)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8084/api'
};
```

### `frontend/src/environments/environment.prod.ts` (Production)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://shopflow-backend.onrender.com/api'
};
```

---

## 🔧 Étape 2 : Mettre à Jour les Services

Vous devez modifier **tous les services** pour utiliser `environment.apiUrl` au lieu de l'URL codée en dur.

### Services à Modifier :

1. `frontend/src/services/auth.service.ts`
2. `frontend/src/services/user.service.ts`
3. `frontend/src/services/product.service.ts`
4. `frontend/src/services/category.service.ts`
5. `frontend/src/services/cart.service.ts`
6. `frontend/src/services/order.service.ts`
7. `frontend/src/services/address.service.ts`
8. `frontend/src/services/coupon.service.ts`
9. `frontend/src/services/review.service.ts`
10. `frontend/src/services/dashboard.service.ts`

### Exemple de Modification :

**❌ AVANT :**
```typescript
const API_URL = 'http://localhost:8084/api';
```

**✅ APRÈS :**
```typescript
import { environment } from '../environments/environment';

const API_URL = environment.apiUrl;
```

---

## 🚀 Étape 3 : Mettre à Jour angular.json

Assurez-vous que `angular.json` utilise les bons fichiers d'environnement :

```json
{
  "projects": {
    "frontend": {
      "architect": {
        "build": {
          "configurations": {
            "production": {
              "fileReplacements": [
                {
                  "replace": "src/environments/environment.ts",
                  "with": "src/environments/environment.prod.ts"
                }
              ]
            }
          }
        }
      }
    }
  }
}
```

---

## 📦 Étape 4 : Mettre à Jour l'URL Backend dans environment.prod.ts

Une fois votre backend déployé sur Render, vous obtiendrez une URL comme :
```
https://shopflow-backend.onrender.com
```

Mettez à jour `frontend/src/environments/environment.prod.ts` :

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://shopflow-backend.onrender.com/api'  // ← Votre URL Render
};
```

---

## 🔨 Étape 5 : Rebuild et Redéployer

### 1. Build pour la production
```bash
cd frontend
npm run build
```

### 2. Vérifier le build
Le dossier `dist/frontend` doit contenir les fichiers compilés.

### 3. Redéployer sur Firebase
```bash
firebase deploy
```

---

## ✅ Étape 6 : Tester

1. Ouvrez votre app : `https://shopflow-25917.web.app`
2. Ouvrez la console du navigateur (F12)
3. Vérifiez que les requêtes vont vers : `https://shopflow-backend.onrender.com/api`
4. Testez la connexion et les fonctionnalités

---

## 🐛 Dépannage

### Erreur CORS
**Symptôme :** `Access to XMLHttpRequest has been blocked by CORS policy`

**Solution :**
1. Vérifiez que `FRONTEND_URL` est configuré dans Render : `https://shopflow-25917.web.app`
2. Vérifiez les logs du backend sur Render
3. Redémarrez le service backend sur Render

### Les requêtes vont toujours vers localhost
**Symptôme :** Les requêtes vont vers `http://localhost:8084`

**Solution :**
1. Vérifiez que vous avez bien modifié tous les services
2. Vérifiez que `angular.json` remplace bien les fichiers d'environnement
3. Supprimez le dossier `dist/` et rebuild :
   ```bash
   rm -rf dist/
   npm run build
   ```

### Erreur 404 sur les endpoints
**Symptôme :** `404 Not Found` sur les appels API

**Solution :**
1. Vérifiez que le backend est bien démarré sur Render
2. Testez directement l'API : `https://shopflow-backend.onrender.com/api/swagger-ui.html`
3. Vérifiez que l'URL dans `environment.prod.ts` est correcte (avec `/api` à la fin)

---

## 📋 Checklist Finale

- [ ] Fichiers d'environnement créés (`environment.ts` et `environment.prod.ts`)
- [ ] Tous les services modifiés pour utiliser `environment.apiUrl`
- [ ] `angular.json` configuré pour remplacer les fichiers d'environnement
- [ ] URL du backend Render mise à jour dans `environment.prod.ts`
- [ ] Build de production effectué (`npm run build`)
- [ ] Déployé sur Firebase (`firebase deploy`)
- [ ] Testé sur `https://shopflow-25917.web.app`
- [ ] Vérification des requêtes dans la console du navigateur
- [ ] Connexion et fonctionnalités testées

---

## 🎯 Commandes Rapides

```bash
# 1. Aller dans le dossier frontend
cd frontend

# 2. Installer les dépendances (si nécessaire)
npm install

# 3. Build pour la production
npm run build

# 4. Déployer sur Firebase
firebase deploy

# 5. Tester
# Ouvrir https://shopflow-25917.web.app dans le navigateur
```

---

## 💡 Conseil

Pour faciliter le développement, vous pouvez créer un script dans `package.json` :

```json
{
  "scripts": {
    "deploy": "ng build --configuration production && firebase deploy"
  }
}
```

Puis simplement :
```bash
npm run deploy
```

---

Bon déploiement ! 🚀
