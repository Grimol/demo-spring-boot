# 🚀 Configuration CI/CD pour Render

## Secrets GitHub à configurer

Allez dans **Settings** > **Secrets and variables** > **Actions** de votre repo GitHub et ajoutez :

### 1. RENDER_SERVICE_ID
- Allez sur render.com → votre service
- Dans l'URL, récupérez l'ID : `https://dashboard.render.com/web/srv-XXXXXXXXX`
- La partie `srv-XXXXXXXXX` est votre SERVICE_ID

### 2. RENDER_API_KEY  
- Render Dashboard → Account Settings → API Keys
- Créez une nouvelle clé API
- Copiez la clé générée

### 3. RENDER_URL
- L'URL publique de votre service Render
- Format : `https://votre-service-name.onrender.com`

## 🔄 Fonctionnement du pipeline

1. **CI Phase** (à chaque push) :
   - ✅ Build Maven
   - ✅ Exécution des tests
   - ✅ Génération du JAR
   - ✅ Rapport de tests

2. **CD Phase** (seulement sur `main`) :
   - 🚀 Déploiement automatique sur Render
   - 🩺 Health check post-déploiement
   - 🔗 Tests d'intégration API

## 📊 Badges pour README

Ajoutez ces badges à votre README.md :

```markdown
![CI/CD](https://github.com/Grimol/demo-spring-boot/workflows/CI/CD%20Pipeline/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green)
```

## 🎯 Prochaines étapes

1. Commitez le workflow : `git add . && git commit -m "feat: add CI/CD pipeline with GitHub Actions"`
2. Configurez les secrets GitHub
3. Poussez : `git push`
4. Admirez le déploiement automatique ! 🎉