# Résolution des erreurs 500 sur Render

## Diagnostic rapide

### 1. Vérifier les logs du backend

```bash
# Sur Render Dashboard
1. Allez sur https://dashboard.render.com
2. Cliquez sur votre service "shopflow-backend"
3. Cliquez sur "Logs" dans le menu de gauche
4. Regardez les dernières erreurs
```

### 2. Erreurs communes et solutions

#### Erreur: "Connection refused" ou "Connection timeout"

**Cause:** Le backend ne peut pas se connecter à PostgreSQL

**Solution:**
1. Vérifiez que `DATABASE_URL` est configuré dans Environment
2. Format attendu: `postgresql://user:password@host:port/database`
3. Render devrait fournir cette URL automatiquement

#### Erreur: "relation 'users' does not exist"

**Cause:** Les tables n'ont pas été créées

**Solution:**
1. Vérifiez que `spring.jpa.hibernate.ddl-auto=update` dans application-prod.properties
2. Redémarrez le service: Manual Deploy → "Clear build cache & deploy"
3. Si le problème persiste, créez les tables manuellement avec le script SQL

#### Erreur: "JWT secret is null"

**Cause:** La variable d'environnement JWT_SECRET n'est pas configurée

**Solution:**
```bash
# Sur Render Dashboard → Environment
# Ajoutez:
JWT_SECRET=6UyQOhrGlLj3OOsN1qcFiC9/AtGWmYYuZgsSajL7rtE=
```

#### Erreur: "Access denied for user"

**Cause:** Problème d'authentification PostgreSQL

**Solution:**
1. Vérifiez les credentials dans DATABASE_URL
2. Assurez-vous que la base de données est active sur Render

### 3. Commandes de diagnostic

#### Tester la connexion à la base de données

```bash
# Depuis votre machine locale
# Récupérez l'External Connection String depuis Render Dashboard

psql "postgresql://shopflow_db_yb6f_user:PASSWORD@HOST/shopflow_db_yb6f"

# Une fois connecté:
\dt  # Lister les tables
\d users  # Voir la structure de la table users
```

#### Vérifier que les tables existent

```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

#### Vérifier les utilisateurs

```sql
SELECT id, email, role, active 
FROM users;
```

### 4. Initialiser la base de données

Si les tables n'existent pas, exécutez:

```bash
# Connectez-vous à PostgreSQL
psql "postgresql://shopflow_db_yb6f_user:PASSWORD@HOST/shopflow_db_yb6f"

# Exécutez le script d'initialisation
\i init-admin.sql
```

Ou copiez-collez le contenu de `init-admin.sql` directement dans le terminal psql.

### 5. Redémarrer le service proprement

```bash
# Sur Render Dashboard
1. Allez dans Settings
2. Cliquez sur "Manual Deploy"
3. Sélectionnez "Clear build cache & deploy"
4. Attendez que le déploiement soit terminé (5-10 minutes)
```

### 6. Tester les endpoints

Une fois le service redémarré:

```bash
# Test de santé
curl https://shopflow-backend-g9zy.onrender.com/api/actuator/health

# Test d'inscription (créer un compte)
curl -X POST https://shopflow-backend-g9zy.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Test de connexion
curl -X POST https://shopflow-backend-g9zy.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@shopflow.com",
    "password": "Admin@123"
  }'
```

## Checklist de vérification

- [ ] DATABASE_URL est configuré dans Environment
- [ ] JWT_SECRET est configuré dans Environment  
- [ ] FRONTEND_URL est configuré dans Environment
- [ ] Le service est démarré (pas en "Suspended")
- [ ] Les logs ne montrent pas d'erreurs de connexion DB
- [ ] Les tables existent dans PostgreSQL
- [ ] Un compte admin existe dans la table users

## Variables d'environnement requises

```bash
DATABASE_URL=postgresql://shopflow_db_yb6f_user:PASSWORD@HOST/shopflow_db_yb6f
JWT_SECRET=6UyQOhrGlLj3OOsN1qcFiC9/AtGWmYYuZgsSajL7rtE=
FRONTEND_URL=https://shopflow-25917.web.app
PORT=8084
```

## Logs à surveiller

### Démarrage réussi
```
Started ShopflowApplication in X seconds
Tomcat started on port(s): 8084
```

### Connexion DB réussie
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed
```

### Erreur à corriger
```
ERROR: Connection refused
ERROR: relation "users" does not exist
ERROR: JWT secret is null
```

## Contact support Render

Si le problème persiste:
1. Allez sur https://render.com/docs
2. Consultez la documentation PostgreSQL
3. Contactez le support via le Dashboard

## Prochaines étapes

Une fois les erreurs 500 résolues:

1. **Créer un compte admin:**
   - Exécutez `init-admin.sql` sur PostgreSQL
   - Ou inscrivez-vous via le frontend et promouvez votre compte

2. **Tester le frontend:**
   - Allez sur https://shopflow-25917.web.app
   - Connectez-vous avec admin@shopflow.com / Admin@123
   - Créez des catégories dans l'interface admin

3. **Surveiller les performances:**
   - Vérifiez les logs régulièrement
   - Surveillez l'utilisation de la base de données
   - Optimisez les requêtes si nécessaire
