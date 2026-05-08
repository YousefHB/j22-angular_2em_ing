# ⚡ SOLUTION RAPIDE - Votre compte est "Collectionneur" au lieu de "Admin"

## 🎯 PROBLÈME
Vous vous êtes connecté avec `admin@shopflow.com` mais vous voyez:
- ❌ Badge: "Collectionneur"
- ❌ Pas de menu admin
- ❌ Rôle: CUSTOMER

Vous voulez:
- ✅ Badge: "Administrateur"  
- ✅ Menu admin complet
- ✅ Rôle: ADMIN

---

## ✅ SOLUTION (2 MINUTES)

### 1️⃣ Ouvrez Render Dashboard
```
https://dashboard.render.com
```

### 2️⃣ Cliquez sur votre base de données
```
shopflow_db_yb6f
```

### 3️⃣ Connectez-vous à PostgreSQL
```
Bouton "Connect" → Copiez la connection string
Ou utilisez le Shell intégré si disponible
```

### 4️⃣ Exécutez cette commande

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';
```

### 5️⃣ Vérifiez que ça a marché

```sql
SELECT email, role FROM users WHERE email = 'admin@shopflow.com';
```

**Vous devriez voir:**
```
email              | role
-------------------+-------
admin@shopflow.com | ADMIN
```

✅ **PARFAIT!**

### 6️⃣ Reconnectez-vous sur le frontend

```
1. Allez sur: https://shopflow-25917.web.app
2. Déconnectez-vous (cliquez sur votre nom → Se déconnecter)
3. Reconnectez-vous:
   - Email: admin@shopflow.com
   - Mot de passe: Admin@123
```

### 7️⃣ Vérifiez votre nouveau rôle

Vous devriez maintenant voir:
- ✅ Badge: "Administrateur"
- ✅ Menu: "Tableau de bord"
- ✅ Menu: "Catégories"
- ✅ Menu: "Utilisateurs"

---

## 🎉 C'EST FAIT!

Vous êtes maintenant **ADMIN** et pouvez:
- ✅ Créer et gérer les catégories
- ✅ Gérer tous les utilisateurs
- ✅ Voir toutes les commandes
- ✅ Voir les statistiques
- ✅ Gérer tous les produits

---

## 🆘 SI ÇA NE MARCHE PAS

### Le rôle ne change pas sur le frontend?

**Cause:** Le token JWT est encore en cache

**Solution:**
```
1. Déconnectez-vous
2. Fermez complètement le navigateur
3. Rouvrez le navigateur
4. Allez sur https://shopflow-25917.web.app/connexion
5. Reconnectez-vous
```

**Ou videz le cache:**
```
Ctrl + Shift + Delete
→ Cochez "Cookies et données de sites"
→ Cliquez sur "Effacer les données"
```

### La commande SQL ne fonctionne pas?

**Erreur possible:** "0 rows updated"

**Vérifiez l'email exact:**
```sql
SELECT id, email, role FROM users;
```

Puis utilisez le bon email:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'LE_BON_EMAIL';
```

---

## 📝 COMMANDE COMPLÈTE (COPIER-COLLER)

```sql
-- Promouvoir en admin
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@shopflow.com';

-- Vérifier
SELECT email, first_name, last_name, role FROM users WHERE email = 'admin@shopflow.com';
```

---

## 💡 ASTUCE

Pour éviter ce problème à l'avenir, créez toujours les comptes admin directement avec le bon rôle via SQL au lieu de passer par l'inscription du frontend.

---

**Temps total:** 2 minutes  
**Difficulté:** Très facile  
**Résultat:** Compte admin fonctionnel

---

**Créé le:** 8 mai 2026  
**Statut:** Testé et fonctionnel
