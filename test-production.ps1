# Script de test production SQLite
Write-Host "=== TEST PRODUCTION COMPLETE ===" -ForegroundColor Green

# 1. Construire l'image Docker
Write-Host "`n1. Construction de l'image Docker..." -ForegroundColor Yellow
docker build -t demo-spring-prod .

# 2. Nettoyer les anciens conteneurs/volumes
Write-Host "`n2. Nettoyage des ressources existantes..." -ForegroundColor Yellow
docker rm -f demo-spring-container 2>$null
docker volume rm demo-sqlite-data 2>$null

# 3. Créer un volume persistant pour SQLite
Write-Host "`n3. Création du volume persistant..." -ForegroundColor Yellow
docker volume create demo-sqlite-data

# 4. Démarrer le conteneur avec profil production
Write-Host "`n4. Démarrage du conteneur en mode production..." -ForegroundColor Yellow
docker run -d `
  --name demo-spring-container `
  -p 8080:8080 `
  -v demo-sqlite-data:/app/data `
  -e SPRING_PROFILES_ACTIVE=production `
  demo-spring-prod

# 5. Attendre le démarrage
Write-Host "`n5. Attente du démarrage (15 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# 6. Vérifier que l'app est démarrée
Write-Host "`n6. Vérification du démarrage..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/" -TimeoutSec 5
    Write-Host "✅ Application démarrée avec succès!" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur de démarrage: $_" -ForegroundColor Red
    docker logs demo-spring-container
    exit 1
}

Write-Host "`n=== PRÊT POUR LES TESTS ===" -ForegroundColor Green
Write-Host "Container: demo-spring-container"
Write-Host "Port: http://localhost:8080"
Write-Host "Profile: production (SQLite)"
Write-Host "Volume: demo-sqlite-data"