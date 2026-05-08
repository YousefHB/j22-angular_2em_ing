# 🚀 Déploiement Rapide - ShopFlow Backend sur Render

## ⚡ Version Express (10 minutes)

### 1️⃣ Pousser le code sur GitHub
```bash
git add .
git commit -m "Ready for Render deployment"
git push origin main
```

### 2️⃣ Créer la base de données sur Render

1. Allez sur https://dashboard.render.com
2. **New +** → **PostgreSQL**
3. Configurez :
   - Name: `shopflow-db`
   - Database: `shopflow_db`
   - Plan: **Free**
4. **Create Database**
5. ⚠️ **Copiez l'Internal Database URL** (vous en aurez besoin)

### 3️⃣ Créer le Web Service

1. **New +** → **Web Service**
2. Connectez votre repo GitHub
3. Sélectionnez le projet **shopflow**
4. Configurez :

**Build & Deploy:**
- Name: `shopflow-backend`
- Runtime: **Java**
- Build Command: `./mvnw clean package -DskipTests`
- Start Command: `java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/shopflow-0.0.1-SNAPSHOT.jar`
- Plan: **Free**

**Environment Variables** (cliquez sur "Advanced"):

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://[COLLER_ICI_INTERNAL_URL_DE_LA_DB]
DB_USERNAME=[USERNAME_DE_LA_DB]
DB_PASSWORD=[PASSWORD_DE_LA_DB]
JWT_SECRET=votre-secret-jwt-super-long-minimum-32-caracteres-securise
FRONTEND_URL=https://shopflow-25917.web.app
PORT=8084
```

5. **Create Web Service**

### 4️⃣ Attendre le déploiement (5-10 min)

Surveillez les logs pour voir :
```
Started ShopFlowApplication in X seconds
```

### 5️⃣ Tester l'API

Votre backend sera à : `https://shopflow-backend.onrender.com`

Testez :
- Swagger: `https://shopflow-backend.onrender.com/api/swagger-ui.html`
- API Docs: `https://shopflow-backend.onrender.com/api/api-docs`

### 6️⃣ Connecter le Frontend

Dans `frontend/src/environments/environment.prod.ts` :

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://shopflow-backend.onrender.com/api'
};
```

Puis redéployez :
```bash
cd frontend
npm run build
firebase deploy
```

### 7️⃣ C'est fait ! 🎉

Testez votre app : https://shopflow-25917.web.app

---

## 🔧 Commandes Utiles

### Générer un JWT Secret sécurisé
```bash
# Linux/Mac
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

### Tester localement avec le profil prod
```bash
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DATABASE_URL="jdbc:mysql://localhost:3306/shopflow_db"
$env:DB_USERNAME="root"
$env:DB_PASSWORD=""
$env:JWT_SECRET="your-local-secret-min-32-chars"
$env:FRONTEND_URL="http://localhost:4200"

./mvnw spring-boot:run
```

---

## ❌ Problèmes Courants

### "Application failed to start"
→ Vérifiez les variables d'environnement dans Render (surtout DATABASE_URL)

### "CORS Error" dans le navigateur
→ Vérifiez que FRONTEND_URL est correct : `https://shopflow-25917.web.app`

### "Connection timeout" à la DB
→ Utilisez l'**Internal Database URL** (pas External)

### Le service s'endort
→ Normal avec le plan gratuit (se réveille au premier appel, ~30 sec)

---

## 📚 Documentation Complète

Voir `DEPLOYMENT_GUIDE.md` pour plus de détails.

---

Bon déploiement ! 🚀
