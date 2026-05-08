# ⚡ PROMOUVOIR LE COMPTE EN ADMIN

## 🎯 SITUATION
Vous avez un compte `admin@shopflow.com` qui est **Collectionneur** (CUSTOMER)  
Vous voulez le rendre **ADMIN**

---

## ✅ SOLUTION RAPIDE (1 MINUTE)

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

### Étape 4: Copier-coller cette commande

```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'admin@shopflow.com';
```

### Étape 5: Vérifier
```sql
SELECT id, email, first_name, last_name, role, active 
FROM users 
WHERE email = 'admin@shopflow.com';
```

**Résultat attendu:**
```
id | email              | first_name | last_name | role  | active
---+-------------------+------------+-----------+-------+--------
 X | admin@shopflow.com| Admin      | ShopFlow  | ADMIN | true
```

✅ **C'EST FAIT!**

---

## 🧪 TESTER

### 1. Déconnectez-vous du frontend
```
https://shopflow-25917.web.app
Cliquez sur votre nom → Se déconnecter
```

### 2. Reconnectez-vous
```
Email: admin@shopflow.com
Mot de passe: Admin@123
```

### 3. Vérifiez le rôle
```
Vous devriez maintenant voir:
- Menu "Tableau de bord" (admin)
- Menu "Catégories" (admin)
- Menu "Utilisateurs" (admin)
- Badge "Administrateur" au lieu de "Collectionneur"
```

---

## 📝 COMMANDE COMPLÈTE (COPIER-COLLER)

```sql
-- Promouvoir en ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';

-- Vérifier
SELECT id, email, first_name, last_name, role, active FROM users WHERE email = 'admin@shopflow.com';
```

---

## 🆘 SI ÇA NE MARCHE PAS

### Problème: "0 rows updated"
**Cause:** L'email n'existe pas ou est mal écrit

**Solution:** Vérifiez l'email exact
```sql
SELECT email, role FROM users;
```

### Problème: Le rôle ne change pas sur le frontend
**Cause:** Token JWT encore en cache

**Solution:**
1. Déconnectez-vous complètement
2. Fermez le navigateur
3. Rouvrez et reconnectez-vous
4. Ou videz le cache: Ctrl+Shift+Delete

### Problème: "relation 'users' does not exist"
**Cause:** Tables non créées

**Solution:**
```
Render Dashboard → shopflow-backend → Manual Deploy → "Clear build cache & deploy"
```

---

## 💡 AUTRES COMMANDES UTILES

### Voir tous les utilisateurs et leurs rôles
```sql
SELECT id, email, first_name, last_name, role, active 
FROM users 
ORDER BY id;
```

### Promouvoir un autre utilisateur en ADMIN
```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'autre-email@example.com';
```

### Rétrograder un admin en CUSTOMER
```sql
UPDATE users 
SET role = 'CUSTOMER' 
WHERE email = 'email@example.com';
```

### Créer un SELLER (Artiste)
```sql
UPDATE users 
SET role = 'SELLER' 
WHERE email = 'artiste@example.com';
```

---

## 📊 LES 3 RÔLES DISPONIBLES

| Rôle | Nom affiché | Permissions |
|------|-------------|-------------|
| `ADMIN` | Administrateur | Tout gérer |
| `SELLER` | Artiste | Gérer ses produits |
| `CUSTOMER` | Collectionneur | Acheter des produits |

---

## ✅ RÉSUMÉ

**Commande à exécuter:**
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';
```

**Temps:** 1 minute  
**Difficulté:** Très facile  
**Résultat:** Compte admin fonctionnel

---

**Créé le:** 8 mai 2026  
**Statut:** Prêt à utiliser
