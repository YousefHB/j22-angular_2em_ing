# 📸 GUIDE VISUEL - Créer le compte admin

## 🎯 Objectif
Créer un compte admin avec:
- **Email:** admin@shopflow.com
- **Mot de passe:** Admin@123
- **Rôle:** ADMIN

---

## 📋 Méthode 1: Via Render Dashboard (LA PLUS SIMPLE)

### Étape 1: Ouvrir Render Dashboard
```
1. Allez sur: https://dashboard.render.com
2. Connectez-vous
3. Vous verrez vos services
```

### Étape 2: Accéder à la base de données
```
1. Cliquez sur "shopflow_db_yb6f" (votre base PostgreSQL)
2. Vous êtes maintenant sur la page de la base de données
```

### Étape 3: Se connecter à PostgreSQL
```
Option A - Via l'interface Render:
1. Cherchez un bouton "Shell" ou "Console" (si disponible)
2. Cliquez dessus

Option B - Via External Connection:
1. Cliquez sur "Connect" en haut à droite
2. Vous verrez "External Connection String"
3. Copiez cette chaîne de connexion
```

### Étape 4: Exécuter la commande SQL

**Copiez-collez EXACTEMENT cette commande:**

```sql
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at) VALUES ('admin@shopflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'ShopFlow', 'ADMIN', true, NOW(), NOW()) ON CONFLICT (email) DO UPDATE SET role = 'ADMIN', active = true;
```

### Étape 5: Vérifier
```sql
SELECT * FROM users WHERE email = 'admin@shopflow.com';
```

**Résultat attendu:**
```
id | email              | first_name | last_name | role  | active
---+-------------------+------------+-----------+-------+--------
 1 | admin@shopflow.com| Admin      | ShopFlow  | ADMIN | true
```

✅ **C'EST FAIT!**

---

## 📋 Méthode 2: Via psql (Ligne de commande)

### Étape 1: Récupérer la connection string
```
1. Render Dashboard → shopflow_db_yb6f
2. Cliquez sur "Connect"
3. Copiez "External Connection String"
   Format: postgresql://user:password@host:port/database
```

### Étape 2: Se connecter
```bash
# Remplacez par votre connection string
psql "postgresql://shopflow_db_yb6f_user:VOTRE_MOT_DE_PASSE@VOTRE_HOST.amazonaws.com/shopflow_db_yb6f"
```

### Étape 3: Exécuter la commande
```sql
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at) 
VALUES ('admin@shopflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'ShopFlow', 'ADMIN', true, NOW(), NOW()) 
ON CONFLICT (email) DO UPDATE SET role = 'ADMIN', active = true;
```

### Étape 4: Vérifier
```sql
SELECT * FROM users WHERE email = 'admin@shopflow.com';
```

### Étape 5: Quitter
```sql
\q
```

✅ **C'EST FAIT!**

---

## 📋 Méthode 3: Via pgAdmin ou DBeaver

### Étape 1: Installer un client PostgreSQL
- **pgAdmin:** https://www.pgadmin.org/download/
- **DBeaver:** https://dbeaver.io/download/

### Étape 2: Créer une nouvelle connexion

**Informations depuis Render Dashboard:**
```
Host: [depuis External Connection String]
Port: 5432
Database: shopflow_db_yb6f
Username: shopflow_db_yb6f_user
Password: [depuis External Connection String]
SSL Mode: Require
```

### Étape 3: Se connecter
```
1. Testez la connexion
2. Cliquez sur "Connect"
```

### Étape 4: Ouvrir Query Tool
```
1. Clic droit sur la base de données
2. Sélectionnez "Query Tool" ou "SQL Editor"
```

### Étape 5: Exécuter le script
```
1. Ouvrez le fichier "CREATE_ADMIN_NOW.sql"
2. Ou copiez-collez la commande INSERT
3. Cliquez sur "Execute" ou appuyez sur F5
```

✅ **C'EST FAIT!**

---

## 🧪 TESTER LA CONNEXION

### Test 1: Via le Frontend

```
1. Allez sur: https://shopflow-25917.web.app/connexion
2. Entrez:
   - Email: admin@shopflow.com
   - Mot de passe: Admin@123
3. Cliquez sur "Se connecter"
```

**✅ Succès si:** Vous voyez le tableau de bord avec menu admin

**❌ Échec si:** "Email ou mot de passe incorrect"
→ Le compte n'a pas été créé, recommencez

### Test 2: Via l'API

**Windows PowerShell:**
```powershell
$body = @{
    email = "admin@shopflow.com"
    password = "Admin@123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://shopflow-backend-g9zy.onrender.com/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

**Résultat attendu:**
```json
{
  "accessToken": "eyJhbGc...",
  "userId": 1,
  "email": "admin@shopflow.com",
  "role": "ADMIN"
}
```

---

## ❓ PROBLÈMES COURANTS

### Problème 1: "relation 'users' does not exist"

**Cause:** Les tables n'ont pas été créées

**Solution:**
```
1. Render Dashboard → shopflow-backend
2. Manual Deploy → "Clear build cache & deploy"
3. Attendez 5-10 minutes
4. Réessayez la commande SQL
```

### Problème 2: "duplicate key value"

**Cause:** L'email existe déjà

**Solution:** Utilisez UPDATE au lieu de INSERT:
```sql
UPDATE users 
SET role = 'ADMIN', active = true 
WHERE email = 'admin@shopflow.com';
```

### Problème 3: "password authentication failed"

**Cause:** Mauvais mot de passe PostgreSQL

**Solution:**
```
1. Render Dashboard → shopflow_db_yb6f
2. Vérifiez "External Connection String"
3. Utilisez le bon mot de passe
```

### Problème 4: Erreur 500 sur le frontend

**Cause:** Backend a des problèmes

**Solution:**
```
1. Render Dashboard → shopflow-backend → Logs
2. Cherchez les erreurs
3. Consultez TROUBLESHOOTING_500.md
```

---

## 🎉 APRÈS LA CRÉATION

### 1. Créer des catégories

**Via SQL:**
```sql
INSERT INTO categories (name, description, created_at, updated_at)
VALUES 
    ('Peinture', 'Œuvres de peinture', NOW(), NOW()),
    ('Sculpture', 'Sculptures', NOW(), NOW()),
    ('Photographie', 'Photos d''art', NOW(), NOW());
```

**Via le Frontend:**
```
1. Connectez-vous en admin
2. Menu → Catégories
3. Cliquez sur "Nouvelle catégorie"
```

### 2. Changer le mot de passe (RECOMMANDÉ)

```
1. Connectez-vous sur le frontend
2. Menu → Profil
3. Changez le mot de passe
```

Ou via SQL:
```sql
-- Générez un hash sur https://bcrypt-generator.com/
UPDATE users 
SET password = '$2a$10$VOTRE_NOUVEAU_HASH' 
WHERE email = 'admin@shopflow.com';
```

---

## 📞 BESOIN D'AIDE?

- **Logs Backend:** https://dashboard.render.com → shopflow-backend → Logs
- **Logs Database:** https://dashboard.render.com → shopflow_db_yb6f → Logs
- **Documentation:** Consultez TROUBLESHOOTING_500.md

---

**Créé le:** 8 mai 2026  
**Version:** 1.0  
**Statut:** Prêt à utiliser
