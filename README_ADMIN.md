# 🚀 CRÉER LE COMPTE ADMIN - GUIDE RAPIDE

## ⚡ EN 3 ÉTAPES

### 1️⃣ Allez sur Render
https://dashboard.render.com → `shopflow_db_yb6f` → Connect

### 2️⃣ Copiez-collez cette commande SQL

```sql
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at) VALUES ('admin@shopflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'ShopFlow', 'ADMIN', true, NOW(), NOW()) ON CONFLICT (email) DO UPDATE SET role = 'ADMIN', active = true;
```

### 3️⃣ Connectez-vous

https://shopflow-25917.web.app/connexion

- **Email:** `admin@shopflow.com`
- **Mot de passe:** `Admin@123`

---

## 📁 Fichiers disponibles

| Fichier | Description |
|---------|-------------|
| `COMMANDE_SQL_ADMIN.txt` | Juste la commande SQL à copier |
| `GUIDE_VISUEL_ADMIN.md` | Guide détaillé avec captures d'écran |
| `INSTRUCTIONS_ADMIN.md` | Instructions complètes |
| `CREATE_ADMIN_NOW.sql` | Script SQL complet |
| `TROUBLESHOOTING_500.md` | Résolution des erreurs |

---

## ✅ Vérification rapide

```sql
SELECT * FROM users WHERE email = 'admin@shopflow.com';
```

Vous devriez voir: `role = ADMIN`

---

## 🆘 Problème?

**Tables n'existent pas?**
→ Redémarrez le backend sur Render

**Email existe déjà?**
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';
```

**Erreur 500?**
→ Consultez `TROUBLESHOOTING_500.md`

---

**C'est tout! Bonne gestion! 🎉**
