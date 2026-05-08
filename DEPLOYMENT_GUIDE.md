# 🚀 Guide de Déploiement Backend sur Render

## ✅ Prérequis
- Compte GitHub (pour connecter votre code)
- Compte Render (gratuit) : https://render.com
- Base de données MySQL (vous pouvez utiliser Render ou un autre service)

---

## 📝 Étape 1 : Pousser votre code sur GitHub

Si ce n'est pas déjà fait, poussez votre code sur GitHub :

```bash
git add .
git commit -m "Prepare backend for Render deployment"
git push origin main
```

---

## 🗄️ Étape 2 : Créer une base de données MySQL

### Option A : Utiliser Render PostgreSQL (Recommandé - Gratuit)

1. Allez sur https://dashboard.render.com
2. Cliquez sur **"New +"** → **"PostgreSQL"**
3. Configurez :
   - **Name** : `shopflow-db`
   - **Database** : `shopflow_db`
   - **User** : (généré automatiquement)
   - **Region** : Choisissez le plus proche
   - **Plan** : **Free**
4. Cliquez sur **"Create Database"**
5. **Notez les informations de connexion** (Internal Database URL)

### Option B : Utiliser MySQL externe (FreeSQLDatabase, PlanetScale, etc.)

Si vous préférez MySQL, vous pouvez utiliser :
- **FreeSQLDatabase** : https://www.freesqldatabase.com
- **PlanetScale** : https://planetscale.com (gratuit)
- **Railway** : https://railway.app

---

## 🔧 Étape 3 : Modifier le projet pour PostgreSQL (si vous utilisez Render PostgreSQL)

### 3.1 Mettre à jour `pom.xml`

Ajoutez la dépendance PostgreSQL :

```xml
<!-- Dans la section <dependencies> -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 3.2 Mettre à jour `application-prod.properties`

Modifiez le dialect Hibernate :

```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.datasource.driver-class-name=org.postgresql.Driver
```

---

## 🌐 Étape 4 : Déployer sur Render

### 4.1 Créer un nouveau Web Service

1. Allez sur https://dashboard.render.com
2. Cliquez sur **"New +"** → **"Web Service"**
3. Connectez votre repository GitHub
4. Sélectionnez votre projet **shopflow**

### 4.2 Configurer le Web Service

Remplissez les informations :

- **Name** : `shopflow-backend`
- **Region** : Choisissez le plus proche (ex: Frankfurt)
- **Branch** : `main`
- **Root Directory** : (laissez vide)
- **Runtime** : **Java**
- **Build Command** : 
  ```bash
  ./mvnw clean package -DskipTests
  ```
- **Start Command** : 
  ```bash
  java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/shopflow-0.0.1-SNAPSHOT.jar
  ```
- **Plan** : **Free**

### 4.3 Configurer les Variables d'Environnement

Cliquez sur **"Advanced"** puis ajoutez ces variables :

| Key | Value | Description |
|-----|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Active le profil de production |
| `DATABASE_URL` | `jdbc:postgresql://...` | URL de votre base de données (copiez depuis Render DB) |
| `DB_USERNAME` | `shopflow_user` | Nom d'utilisateur de la DB |
| `DB_PASSWORD` | `votre_mot_de_passe` | Mot de passe de la DB |
| `JWT_SECRET` | `votre-secret-jwt-super-long-et-securise-minimum-32-caracteres` | Secret pour JWT (générez-en un sécurisé) |
| `FRONTEND_URL` | `https://shopflow-25917.web.app` | URL de votre frontend Firebase |
| `PORT` | `8084` | Port du serveur (Render utilise $PORT automatiquement) |

**⚠️ Important pour DATABASE_URL :**
- Si vous utilisez PostgreSQL de Render, copiez l'**Internal Database URL**
- Format : `jdbc:postgresql://dpg-xxxxx:5432/shopflow_db`
- Ajoutez `jdbc:` au début si ce n'est pas déjà présent

**🔐 Générer un JWT Secret sécurisé :**
```bash
# Sur Linux/Mac
openssl rand -base64 32

# Ou utilisez un générateur en ligne
# https://www.grc.com/passwords.htm
```

### 4.4 Déployer

1. Cliquez sur **"Create Web Service"**
2. Render va automatiquement :
   - Cloner votre repository
   - Construire votre application avec Maven
   - Démarrer votre backend
3. Attendez 5-10 minutes pour le premier déploiement

---

## 🔍 Étape 5 : Vérifier le déploiement

### 5.1 Vérifier les logs

Dans le dashboard Render :
- Allez dans votre service **shopflow-backend**
- Cliquez sur **"Logs"**
- Vérifiez qu'il n'y a pas d'erreurs
- Cherchez : `Started ShopFlowApplication`

### 5.2 Tester l'API

Votre backend sera disponible à : `https://shopflow-backend.onrender.com`

Testez les endpoints :
- **Health Check** : `https://shopflow-backend.onrender.com/api/auth/health`
- **Swagger UI** : `https://shopflow-backend.onrender.com/api/swagger-ui.html`
- **API Docs** : `https://shopflow-backend.onrender.com/api/api-docs`

---

## 🔗 Étape 6 : Connecter le Frontend au Backend

### 6.1 Mettre à jour l'URL de l'API dans le Frontend

Dans votre projet Angular (`frontend/src/environments/`), mettez à jour :

**environment.prod.ts** :
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://shopflow-backend.onrender.com/api'
};
```

### 6.2 Redéployer le Frontend

```bash
cd frontend
npm run build
firebase deploy
```

---

## 🎉 Étape 7 : Tester l'application complète

1. Ouvrez votre frontend : `https://shopflow-25917.web.app`
2. Essayez de vous connecter
3. Vérifiez que les données s'affichent correctement

---

## 🐛 Dépannage

### Problème : "Application failed to start"

**Solution** : Vérifiez les logs dans Render
- Erreur de connexion DB → Vérifiez `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD`
- Erreur JWT → Vérifiez que `JWT_SECRET` fait au moins 32 caractères

### Problème : CORS Error dans le navigateur

**Solution** : Vérifiez que `FRONTEND_URL` est correctement configuré dans les variables d'environnement

### Problème : "Connection timeout" à la base de données

**Solution** : 
- Utilisez l'**Internal Database URL** (pas l'External)
- Vérifiez que la DB et le Web Service sont dans la même région

### Problème : Le service s'endort (Free Plan)

**Solution** : 
- Le plan gratuit de Render met le service en veille après 15 min d'inactivité
- Le premier appel après la veille prendra 30-60 secondes
- Pour éviter cela, passez au plan payant ($7/mois)

---

## 📊 Surveillance

### Logs en temps réel
```bash
# Dans le dashboard Render, section "Logs"
```

### Métriques
- CPU, Mémoire, Requêtes : Dashboard Render → Metrics

---

## 🔄 Mises à jour automatiques

Render redéploie automatiquement à chaque push sur la branche `main` :

```bash
git add .
git commit -m "Update backend"
git push origin main
# Render détecte le push et redéploie automatiquement
```

---

## 💡 Conseils

1. **Utilisez PostgreSQL** : Plus facile avec Render (gratuit et intégré)
2. **Surveillez les logs** : Surtout lors du premier déploiement
3. **Testez localement** : Avant de déployer, testez avec le profil `prod`
4. **Sécurisez JWT_SECRET** : Utilisez un secret fort et unique
5. **Documentez vos variables** : Gardez une trace de vos configurations

---

## 📞 Support

- **Documentation Render** : https://render.com/docs
- **Community Render** : https://community.render.com
- **Spring Boot Docs** : https://spring.io/guides

---

Bon déploiement ! 🚀
