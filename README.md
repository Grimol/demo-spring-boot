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

### **Important à retenir sur Spring Boot**
- Contrat d’API : Décrit de façon formelle les règles d’interaction entre un service fournisseur et un service consommateur.
Il définit les routes disponibles, les formats de requêtes/réponses, les codes de statut, et les cas d’erreur attendus, assurant ainsi que les deux parties puissent communiquer correctement, indépendamment du langage utilisé.

**📘 Exemple de contrat d’API — Récupérer un utilisateur par ID**

***Endpoint :***
```bash
GET /users/{id}
```

***Description :***
Récupère les informations d'un utilisateur à partir de son identifiant unique (**`id`**)

**📨 Requête**
```html
GET /users/{id} HTTP/1.1
Host: api.example.com
Authorization: Bearer <token>
Content-Type: application/json
```
💬 **Note :** Aucun corps (**`Body`**) n’est requis pour cette requête.

**📦 Réponses possibles**
| Statut HTTP        | Signification                                      | Conditions                                                     |
| ------------------ | -------------------------------------------------- | -------------------------------------------------------------- |
| `200 OK`           | L'utilisateur a été trouvé et renvoyé avec succès. | L’utilisateur est **authentifié** et **autorisé**.             |
| `401 Unauthorized` | Accès refusé.                                      | L’utilisateur est **non authentifié** ou **non autorisé**.     |
| `404 Not Found`    | Aucun utilisateur trouvé avec cet ID.              | L’utilisateur est authentifié, mais la ressource n’existe pas. |

**🧾 Exemple de réponse (JSON)**
```json
{
  "id": 99,
  "username": "johndoe",
  "age": 28
}
```