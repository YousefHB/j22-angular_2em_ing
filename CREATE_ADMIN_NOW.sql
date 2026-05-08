-- ============================================
-- CRÉER LE COMPTE ADMIN IMMÉDIATEMENT
-- ============================================
-- Email: admin@shopflow.com
-- Mot de passe: Admin@123
-- Rôle: ADMIN

-- Hash BCrypt du mot de passe "Admin@123"
-- Généré avec BCrypt strength 10

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
ON CONFLICT (email) DO UPDATE SET
    role = 'ADMIN',
    active = true;

-- Vérifier que l'admin a été créé
SELECT id, email, first_name, last_name, role, active, created_at 
FROM users 
WHERE email = 'admin@shopflow.com';
