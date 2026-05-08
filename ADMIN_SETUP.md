# Configuration du compte Admin

## Créer un compte Admin sur Render

### Option 1: Via SQL direct dans Render Dashboard

1. Allez sur https://dashboard.render.com
2. Sélectionnez votre base de données PostgreSQL `shopflow_db_yb6f`
3. Cliquez sur "Connect" → "External Connection" 
4. Utilisez un client PostgreSQL (pgAdmin, DBeaver, ou psql) avec les credentials fournis
5. Exécutez le script SQL suivant:

```sql
-- Créer un compte admin
-- Mot de passe: Admin@123 (hashé avec BCrypt)
INSERT INTO users (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    'admin@shopflow.com',
    '$2a$10$xQKhF5qN8vYxGxYxGxYxGOxQKhF5qN8vYxGxYxGxYxGOxQKhF5qN8u',
    'Admin',
    'ShopFlow',
    'ADMIN',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
```

**Identifiants de connexion:**
- Email: `admin@shopflow.com`
- Mot de passe: `Admin@123`

### Option 2: Via l'API d'inscription (Recommandé)

1. Allez sur https://shopflow-25917.web.app/inscription
2. Créez un compte avec vos informations
3. Connectez-vous à la base de données PostgreSQL sur Render
4. Mettez à jour le rôle de votre compte:

```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'votre-email@example.com';
```

## Vérifier les logs du backend

Pour diagnostiquer les erreurs 500:

1. Allez sur https://dashboard.render.com
2. Sélectionnez votre service `shopflow-backend`
3. Cliquez sur "Logs" dans le menu de gauche
4. Recherchez les erreurs récentes

### Erreurs communes:

#### 1. Problème de connexion à la base de données
```
Error: Connection refused
```
**Solution:** Vérifiez que la variable d'environnement `DATABASE_URL` est correctement configurée

#### 2. Tables manquantes
```
ERROR: relation "users" does not exist
```
**Solution:** Le backend devrait créer les tables automatiquement avec `spring.jpa.hibernate.ddl-auto=update`

#### 3. JWT Secret manquant
```
Error: JWT secret is null
```
**Solution:** Vérifiez que `JWT_SECRET` est configuré dans les variables d'environnement

## Commandes utiles

### Se connecter à PostgreSQL via psql (depuis votre machine locale)

```bash
# Récupérez l'URL de connexion depuis Render Dashboard
psql "postgresql://shopflow_db_yb6f_user:PASSWORD@HOST/shopflow_db_yb6f"
```

### Vérifier les tables existantes

```sql
\dt
```

### Voir tous les utilisateurs

```sql
SELECT id, email, first_name, last_name, role, active, created_at 
FROM users;
```

### Promouvoir un utilisateur en ADMIN

```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'votre-email@example.com';
```

### Créer des catégories de test

```sql
INSERT INTO categories (name, description, created_at, updated_at)
VALUES 
    ('Peinture', 'Œuvres de peinture originales', NOW(), NOW()),
    ('Sculpture', 'Sculptures et installations', NOW(), NOW()),
    ('Photographie', 'Photographies d''art', NOW(), NOW())
ON CONFLICT DO NOTHING;
```

## Résolution des erreurs 500

### Étape 1: Vérifier les logs
```bash
# Sur Render Dashboard → Logs
# Recherchez les stack traces Java
```

### Étape 2: Vérifier la connexion DB
```sql
-- Testez la connexion
SELECT 1;
```

### Étape 3: Vérifier les variables d'environnement
Sur Render Dashboard → Environment:
- `DATABASE_URL` ✓
- `JWT_SECRET` ✓
- `FRONTEND_URL` ✓

### Étape 4: Redémarrer le service
Sur Render Dashboard → Manual Deploy → "Clear build cache & deploy"

## Générer un nouveau mot de passe BCrypt

Si vous voulez créer un admin avec un mot de passe personnalisé:

1. Allez sur https://bcrypt-generator.com/
2. Entrez votre mot de passe
3. Utilisez le hash généré dans la requête SQL

Ou utilisez ce code Java:
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "VotreMotDePasse";
        String hash = encoder.encode(password);
        System.out.println("Hash: " + hash);
    }
}
```
