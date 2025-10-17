# Script de test API complet
Write-Host "=== TEST API PRODUCTION ===" -ForegroundColor Green

# Fonction pour créer les en-têtes d'authentification
function Get-BasicAuthHeader {
    param($username, $password)
    $credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("${username}:${password}"))
    return @{Authorization = "Basic $credentials"}
}

# Test 1: Récupérer les joueurs (vide au début)
Write-Host "`n1. Test GET /players (liste vide)" -ForegroundColor Yellow
try {
    $headers = Get-BasicAuthHeader "isis" "abc123"
    $players = Invoke-RestMethod -Uri "http://localhost:8080/players" -Headers $headers -Method GET
    Write-Host "✅ Réponse: $($players | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur: $_" -ForegroundColor Red
}

# Test 2: Créer un joueur
Write-Host "`n2. Test POST /players (création)" -ForegroundColor Yellow
try {
    $headers = Get-BasicAuthHeader "isis" "abc123"
    $headers["Content-Type"] = "application/json"
    
    $newPlayer = @{
        pseudo = "IsisWarrior"
        experience = 1000
        level = 3
        gold = 500
        hp = 120.0
        atk = 15.0
        defense = 8.0
        atkSpd = 1.1
        critRate = 7.5
        critDmg = 160.0
    } | ConvertTo-Json
    
    $created = Invoke-RestMethod -Uri "http://localhost:8080/players" -Headers $headers -Method POST -Body $newPlayer
    Write-Host "✅ Joueur créé: $($created | ConvertTo-Json)" -ForegroundColor Green
    $playerId = $created.id
} catch {
    Write-Host "❌ Erreur: $_" -ForegroundColor Red
}

# Test 3: Récupérer le joueur créé
Write-Host "`n3. Test GET /players/{id}" -ForegroundColor Yellow
try {
    $headers = Get-BasicAuthHeader "isis" "abc123"
    $player = Invoke-RestMethod -Uri "http://localhost:8080/players/$playerId" -Headers $headers -Method GET
    Write-Host "✅ Joueur récupéré: $($player | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur: $_" -ForegroundColor Red
}

# Test 4: Lister tous les joueurs (maintenant 1)
Write-Host "`n4. Test GET /players (avec données)" -ForegroundColor Yellow
try {
    $headers = Get-BasicAuthHeader "isis" "abc123"
    $allPlayers = Invoke-RestMethod -Uri "http://localhost:8080/players" -Headers $headers -Method GET
    Write-Host "✅ Liste des joueurs: $($allPlayers | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur: $_" -ForegroundColor Red
}

Write-Host "`n=== TEST TERMINÉ ===" -ForegroundColor Green
Write-Host "Données SQLite persistées dans volume Docker: demo-sqlite-data"