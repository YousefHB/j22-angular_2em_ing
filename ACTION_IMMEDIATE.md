# ⚡ ACTION IMMÉDIATE - Créer/Promouvoir le compte admin

## 🎯 DEUX SITUATIONS POSSIBLES

### 📌 Situation A: Le compte existe déjà (Collectionneur)
**Vous voyez "Collectionneur" au lieu de "Administrateur"?**  
→ Utilisez la **COMMANDE A** ci-dessous

### 📌 Situation B: Le compte n'existe pas encore
**Première fois que vous créez le compte?**  
→ Utilisez la **COMMANDE B** ci-dessous

---

## ✅ ÉTAPES COMMUNES

### Étape 1: Aller sur Render
```
https://dashboard.render.com
```

### Étape 2: Ouvrir votre base de données
```
Cliquez sur: shopflow_db_yb6f
```

### Étape 3: Se connecter à PostgreSQL
```
Cliquez sur: "Connect" ou "Shell"
```

### Étape 4: Choisir et copier-coller la bonne commande

---

## 🔄 COMMANDE A: Promouvoir un compte existant

**Si le compte admin@shopflow.com existe déjà comme "Collectionneur":**

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';
```

---

## ➕ COMMANDE B: Créer un nouveau compte admin

**Si le compte n'existe pas encore:**

```sql
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at) VALUES ('admin@shopflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'ShopFlow', 'ADMIN', true, NOW(), NOW()) ON CONFLICT (email) DO UPDATE SET role = 'ADMIN', active = true;
```

---

## 🔍 PAS SÛR? Vérifiez d'abord!

```sql
SELECT email, role FROM users WHERE email = 'admin@shopflow.com';
```

**Résultat:**
- Si vous voyez `CUSTOMER` → Utilisez **COMMANDE A**
- Si vous voyez `no rows` → Utilisez **COMMANDE B**
- Si vous voyez `ADMIN` → C'est déjà bon! ✅

### Étape 5: Vérifier
```sql
SELECT * FROM users WHERE email = 'admin@shopflow.com';
```

Vous devriez voir: **role = ADMIN**

### Étape 6: Se connecter
```
https://shopflow-25917.web.app/connexion

Email: admin@shopflow.com
Mot de passe: Admin@123
```

---

## ✅ C'EST TOUT!

Vous êtes maintenant admin et pouvez:
- Créer des catégories
- Gérer les utilisateurs
- Voir les commandes
- Gérer les produits

---

## 🆘 Problème?

**Tables n'existent pas?**
```
Render Dashboard → shopflow-backend → Manual Deploy → "Clear build cache & deploy"
Attendez 5-10 minutes, puis réessayez
```

**Email existe déjà?**
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';
```

**Autre problème?**
```
Consultez: TROUBLESHOOTING_500.md
```

---

**Temps estimé: 5 minutes** ⏱️
