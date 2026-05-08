-- Script d'initialisation pour ShopFlow
-- À exécuter sur la base de données PostgreSQL de production

-- ============================================
-- 1. CRÉER UN COMPTE ADMINISTRATEUR
-- ============================================
-- Email: admin@shopflow.com
-- Mot de passe: Admin@123
-- Hash BCrypt du mot de passe Admin@123

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
ON CONFLICT (email) DO NOTHING;

-- ============================================
-- 2. CRÉER DES CATÉGORIES PAR DÉFAUT
-- ============================================

INSERT INTO categories (name, description, created_at, updated_at)
VALUES 
    ('Peinture', 'Œuvres de peinture originales et reproductions d''art', NOW(), NOW()),
    ('Sculpture', 'Sculptures, installations et œuvres en 3D', NOW(), NOW()),
    ('Photographie', 'Photographies d''art et tirages limités', NOW(), NOW()),
    ('Art Numérique', 'Créations numériques et NFT', NOW(), NOW()),
    ('Dessin', 'Dessins, croquis et illustrations', NOW(), NOW()),
    ('Art Abstrait', 'Œuvres abstraites et contemporaines', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- 3. VÉRIFICATION
-- ============================================

-- Afficher l'admin créé
SELECT id, email, first_name, last_name, role, active, created_at 
FROM users 
WHERE role = 'ADMIN';

-- Afficher les catégories créées
SELECT id, name, description, created_at 
FROM categories 
ORDER BY name;

-- ============================================
-- NOTES
-- ============================================
-- Pour vous connecter:
-- Email: admin@shopflow.com
-- Mot de passe: Admin@123
--
-- IMPORTANT: Changez ce mot de passe après la première connexion!
--
-- Pour changer le mot de passe via SQL:
-- UPDATE users 
-- SET password = '$2a$10$VOTRE_NOUVEAU_HASH_BCRYPT' 
-- WHERE email = 'admin@shopflow.com';
--
-- Générez un hash BCrypt sur: https://bcrypt-generator.com/
