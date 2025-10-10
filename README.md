# Lancer le projet
./mvnw spring-boot:run

# Lancer les tests
./mvnw -q test

# Requête avec curl
curl.exe -i "http://localhost:8080/hello"

# Utilisation du Makefile
## Build jar local
make build

## Construire l'image Docker
make docker-build

## Lancer le conteneur sur le port 8080
make docker-run

## Arrêter le conteneur
make docker-stop

# Git Workflow
Ce projet utilise une organisation simple et lisible pour la gestion du code :
- La branche principale est **`main`** → stable, prête à être déployée.
- Les développements se font sur des branches **`feat/*`** → une fonctionnalité ou un sujet par branche.

## Cycle de développement

1. **Créer une nouvelle branche**
    - git checkout -b feat/nom-fonctionnalite

2. **Coder et tester en local**
    - ./mvnw -q test

3. **Commits clairs et réguliers**
    - Un commit = une modification logique
    - Format recommandé :
        - feat: ajout CRUD projets
        - fix: correction bug sur user update
        - test: ajout tests delete project
        - chore: mise à jour Dockerfile
    
    Exemple :
        - git add .
        - git commit -m "feat: add CRUD endpoints for projects"

4. **Push des commits en fin de journée sur la branche active si ce n'est pas fini**
    - git push -u origin feat/nom-fonctionnalite

5. **Fusionner dans **`main`** après validation**
    - git checkout main
    - git pull origin main
    - git merge feat/nom-fonctionnalite
    - git push origin main

6. **Supprimer la branche feature si plus utile**
    - git branch -d feat/nom-fonctionnalite
    - git push origin --delete feat/nom-fonctionnalite

## **Bonnes pratiques**
- Toujours vérifier que les tests passent avant de merger.
- Commits petits et thématiques (éviter “fix trucs divers”).
- Utiliser des branches **`feat/`**, **`fix/`**, **`chore/`**, **`docs/`** selon le type de travail.
- La branche **`main`** reste toujours fonctionnelle et stable.