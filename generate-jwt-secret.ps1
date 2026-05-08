# Script PowerShell pour générer un JWT Secret sécurisé
# Usage: .\generate-jwt-secret.ps1

Write-Host "🔐 Génération d'un JWT Secret sécurisé..." -ForegroundColor Cyan
Write-Host ""

# Générer 32 bytes aléatoires et les convertir en Base64
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$secret = [Convert]::ToBase64String($bytes)

Write-Host "✅ JWT Secret généré avec succès !" -ForegroundColor Green
Write-Host ""
Write-Host "Votre JWT Secret (copiez-le) :" -ForegroundColor Yellow
Write-Host $secret -ForegroundColor White
Write-Host ""
Write-Host "📋 Longueur : $($secret.Length) caractères" -ForegroundColor Gray
Write-Host ""
Write-Host "⚠️  IMPORTANT :" -ForegroundColor Red
Write-Host "   - Gardez ce secret confidentiel" -ForegroundColor White
Write-Host "   - Ne le commitez JAMAIS dans Git" -ForegroundColor White
Write-Host "   - Utilisez-le dans les variables d'environnement Render" -ForegroundColor White
Write-Host ""
Write-Host "📝 Pour l'utiliser sur Render :" -ForegroundColor Cyan
Write-Host "   1. Allez dans votre service sur Render" -ForegroundColor White
Write-Host "   2. Environment → Add Environment Variable" -ForegroundColor White
Write-Host "   3. Key: JWT_SECRET" -ForegroundColor White
Write-Host "   4. Value: [collez le secret ci-dessus]" -ForegroundColor White
Write-Host ""

# Copier dans le presse-papier si possible
try {
    Set-Clipboard -Value $secret
    Write-Host "✅ Secret copié dans le presse-papier !" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Impossible de copier dans le presse-papier. Copiez manuellement." -ForegroundColor Yellow
}

Write-Host ""
