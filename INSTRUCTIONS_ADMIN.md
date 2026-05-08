# 🚀 CRÉER LE COMPTE ADMIN - INSTRUCTIONS RAPIDES

## Identifiants du compte admin

- **Email:** `admin@shopflow.com`
- **Mot de passe:** `Admin@123`
- **Rôle:** ADMIN

## Étapes pour créer le compte

### Option 1: Via Render Dashboard (RECOMMANDÉ - 2 minutes)

1. **Allez sur Render Dashboard**
   - URL: https://dashboard.render.com
   - Connectez-vous à votre compte

2. **Accédez à votre base de données**
   - Cliquez sur `shopflow_db_yb6f` dans la liste

3. **Ouvrez la console SQL**
   - Cliquez sur l'onglet "Shell" ou "Connect"
   - Ou utilisez "External Connection" pour vous connecter

4. **Exécutez cette commande SQL:**

```sql
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    'admin@shopflow.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Admin',
    'ShopFlow',
    'ADMIN',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO UPDATE SET role = 'ADMIN', active = true;
```

5. **Vérifiez que ça a fonctionné:**

```sql
SELECT id, email, first_name, last_name, role, active 
FROM users 
WHERE email = 'admin@shopflow.com';
```

Vous devriez voir:
```
id | email                | first_name | last_name | role  | active
---+---------------------+------------+-----------+-------+--------
 1 | admin@shopflow.com  | Admin      | ShopFlow  | ADMIN | true
```

### Option 2: Via psql (ligne de commande)

1. **Récupérez la connection string**
   - Render Dashboard → shopflow_db_yb6f → Connect
   - Copiez "External Connection String"

2. **Connectez-vous:**

```bash
psql "postgresql://shopflow_db_yb6f_user:VOTRE_PASSWORD@VOTRE_HOST/shopflow_db_yb6f"
```

3. **Exécutez le script:**

```bash
\i CREATE_ADMIN_NOW.sql
```

Ou copiez-collez directement la commande INSERT ci-dessus.

### Option 3: Via un client PostgreSQL (pgAdmin, DBeaver, etc.)

1. **Configurez la connexion:**
   - Host: (depuis Render Dashboard)
   - Port: 5432
   - Database: shopflow_db_yb6f
   - User: shopflow_db_yb6f_user
   - Password: (depuis Render Dashboard)

2. **Exécutez le script `CREATE_ADMIN_NOW.sql`**

## Tester la connexion

### Via le frontend

1. Allez sur https://shopflow-25917.web.app/connexion
2. Entrez:
   - Email: `admin@shopflow.com`
   - Mot de passe: `Admin@123`
3. Cliquez sur "Se connecter"

Vous devriez voir le tableau de bord admin!

### Via l'API (curl)

```bash
curl -X POST https://shopflow-backend-g9zy.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@shopflow.com","password":"Admin@123"}'
```

Réponse attendue:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "userId": 1,
  "email": "admin@shopflow.com",
  "firstName": "Admin",
  "lastName": "ShopFlow",
  "role": "ADMIN"
}
```

## Si vous avez déjà un compte

Si vous avez déjà créé un compte via l'inscription, vous pouvez le promouvoir en admin:

```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'votre-email@example.com';
```

## Créer des catégories (après connexion admin)

Une fois connecté en tant qu'admin:

```sql
INSERT INTO categories (name, description, created_at, updated_at)
VALUES 
    ('Peinture', 'Œuvres de peinture originales', NOW(), NOW()),
    ('Sculpture', 'Sculptures et installations', NOW(), NOW()),
    ('Photographie', 'Photographies d''art', NOW(), NOW()),
    ('Art Numérique', 'Créations numériques', NOW(), NOW()),
    ('Dessin', 'Dessins et illustrations', NOW(), NOW()),
    ('Art Abstrait', 'Œuvres abstraites', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
```

Ou créez-les directement via l'interface admin sur le frontend!

## Problèmes courants

### "duplicate key value violates unique constraint"

L'email existe déjà. Utilisez plutôt:

```sql
UPDATE users 
SET role = 'ADMIN', active = true 
WHERE email = 'admin@shopflow.com';
```

### "relation 'users' does not exist"

Les tables n'ont pas été créées. Redémarrez le backend sur Render:
- Dashboard → shopflow-backend → Manual Deploy → "Clear build cache & deploy"

### "password authentication failed"

Vérifiez vos credentials de connexion PostgreSQL dans Render Dashboard.

## Sécurité

⚠️ **IMPORTANT:** Changez le mot de passe après la première connexion!

Pour générer un nouveau hash BCrypt:
1. Allez sur https://bcrypt-generator.com/
2. Entrez votre nouveau mot de passe
3. Copiez le hash généré
4. Exécutez:

```sql
UPDATE users 
SET password = '$2a$10$VOTRE_NOUVEAU_HASH' 
WHERE email = 'admin@shopflow.com';
```

---

**Besoin d'aide?** Consultez `TROUBLESHOOTING_500.md` pour plus de détails.
