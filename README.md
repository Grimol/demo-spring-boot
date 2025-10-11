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

- Serialization : Permet de transformer un objet en fichier ou flux de données.
- Deserialization : Permet de transformer des données venant d'un fichier ou d'un flux en objet.

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

# **📚 Rappels — Stéréotypes & Configuration Spring**
## **🧭 Vue d’ensemble**

| Annotation        | Rôle                        | Couche typique | Particularités                                        |
| ----------------- | --------------------------- | -------------- | ----------------------------------------------------- |
| `@Configuration`  | Classe de configuration     | Config         | Déclare des beans via `@Bean` (Java config)           |
| `@Bean`           | Méthode qui déclare un bean | Config         | Contrôle fin de la création/initialisation du bean    |
| `@Component`      | Composant générique         | Tous           | Base des stéréotypes (scannée par Spring)             |
| `@Service`        | Logique métier              | Service        | Sémantique métier (mêmes capacités que `@Component`)  |
| `@Repository`     | Accès aux données           | Persistence    | Traduction des exceptions DataAccess (Spring Data)    |
| `@Controller`     | Contrôleur MVC (vues)       | Web MVC        | Renvoie des **vues** (HTML/Thymeleaf)                 |
| `@RestController` | Contrôleur REST             | API            | `@Controller` + `@ResponseBody` → renvoie du **JSON** |

## **⚙️ @Configuration**

### À quoi ça sert ?
Marque une classe comme **source de configuration**. Les méthodes annotées `@Bean` à l’intérieur **créent des beans** gérés par le conteneur Spring.
### Quand l’utiliser ?
- Pour **définir explicitement** des beans (lib externes, utilitaires, config complexe).
- Quand l’auto-configuration ne suffit pas.

**Exemple**
```java
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Notes & pièges**
- Par défaut `proxyBeanMethods = true` (création via CGLIB) garantit l’**unicité** du bean si une méthode `@Bean` en appelle une autre. Mettre `proxyBeanMethods=false` pour des configs **pures/performantes** (si les méthodes ne s’appellent pas entre elles).
- Les classes `@Configuration` sont scannées si elles sont **sous le package** de la classe `@SpringBootApplication`.

## **🧩 @Bean**
### À quoi ça sert ?
Déclare **un bean** dans le contexte Spring via une **méthode** (usine) Java. Tu contrôles **comment** et **avec quels paramètres** il est construit.
### Quand l’utiliser ?
- Pour enregistrer un bean provenant d’une **librairie** (classe non annotée).
- Pour **surcharger** un bean auto-configuré.
- Pour **paramétrer** finement la création (constructeur, setters, init/destroy).

**Exemple**
```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofSeconds(2))
        .setReadTimeout(Duration.ofSeconds(2))
        .build();
}
```

**Notes & pièges**
- Par défaut, **scope** = singleton. Possibles : `@Scope("prototype")`, `request`, `session`, etc.
- Hooks cycle de vie : `@Bean(initMethod = "...", destroyMethod = "...")` si besoin.

## **🧱 @Component**
### À quoi ça sert ?
Stéréotype **générique** : “ajoute-moi au contexte Spring”.
### Quand l’utiliser ?
- Pour tout composant **technique/utilitaire** qui ne correspond pas clairement à `@Service`/`@Repository`/`@Controller`.

**Exemple**
```java
@Component
public class Slugifier {
    public String toSlug(String input) { /* ... */ }
}
```

**Notes & pièges**
- `@Service`, `@Repository`, `@Controller` **héritent sémantiquement** de `@Component`.
- Le package scanning part (par défaut) du package de ta classe `@SpringBootApplication`.

## **🧠 @Service**
### À quoi ça sert ?
Marque une classe comme **service métier**. Sémantique claire pour la **logique applicative** (orchestration, règles, validations).
### Quand l’utiliser ?
- Pour encapsuler la **logique** qui ne relève ni du web, ni de la persistence.
- Pour centraliser des **use cases**.

**Exemple**
```java
@Service
public class PaymentService {
    private final PaymentGateway gateway;

    public PaymentService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public Receipt pay(Order order) {
        // validations, orchestration, appels externes…
        return gateway.charge(order);
    }
}
```

**Notes & pièges**
- Identique à `@Component` **fonctionnellement**, mais **plus expressif**.
- Idéal pour les **tests unitaires** (injection par constructeur recommandée).


## **🗃️ @Repository**
### À quoi ça sert ?
Marque un **DAO** / composant d’accès aux données. Ajoute la **traduction des exceptions** (ex : SQLException → DataAccessException) et l’intégration **Spring Data**.
### Quand l’utiliser ?
- Pour les classes qui **parlent à la base** (JPA/JDBC/NoSQL).
- Interfaces Spring Data (`JpaRepository`, etc.) → pas d’implémentation à écrire.

**Exemples**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

**Notes & pièges**
- Avec Spring Data, l’**implémentation est générée** automatiquement.
- En JDBC “pur”, tu écris la classe annotée `@Repository` et gères `JdbcTemplate`/`NamedParameterJdbcTemplate`.

## **🌐 @Controller (MVC — vues)**
### À quoi ça sert ?
Contrôleur **web MVC** qui retourne des **vues** (ex. Thymeleaf). Les méthodes renvoient souvent un **nom de template**, et les données via `Model`.
### Quand l’utiliser ?
- Applications **server-side rendering** (HTML).
- Pages d’admin, back-office, prototypage.

**Exemples**
```java
@Controller
public class PageController {

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Accueil");
        return "home"; // home.html (Thymeleaf)
    }
}
```

**Notes & pièges**
- Pour renvoyer du JSON depuis un `@Controller`, il faut annoter la méthode avec `@ResponseBody`.

## **🧾 @RestController (API — JSON)**
### À quoi ça sert ?
`@Controller` + `@ResponseBody` **par défaut** : le **corps** de la réponse HTTP est le **retour de la méthode**, sérialisé (JSON par Jackson).
### Quand l’utiliser ?
- Pour exposer des **API REST** (Angular / mobile / clients externes).

**Exemples**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody CreateUserCommand cmd) {
        return service.create(cmd);
    }
}
```

**Notes & pièges**
- Par défaut, les objets retournés sont **sérialisés en JSON** (Jackson).
- Pour les statuts HTTP personnalisés, utiliser `ResponseEntity<?>` ou `@ResponseStatus`.

## **🔗 Bonnes pratiques (rappel)**
- **Injection par constructeur** (pas par champ) → testable, explicite.
- Séparer **Controller** → **Service** → **Repository** (couches claires).
- Garder `@Configuration` et `@Bean` pour les cas **nécessaires** (pas de sur-configuration).
- Placer la classe `@SpringBootApplication` à la **racine du package** pour un **component scan** simple et fiable.