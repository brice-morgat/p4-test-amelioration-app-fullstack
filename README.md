# Yoga App - Full Stack Testing

Ce dépôt contient :
- `front/` : application Angular
- `back/` : API Spring Boot

Objectif : fournir les tests unitaires, d'intégration et E2E, avec rapports de couverture.

## Prérequis

- Node.js 20+ et npm
- Java 21
- Maven 3.9+
- Docker + Docker Compose

## Installation

## 1) Cloner le projet

```bash
git clone https://github.com/brice-morgat/p4-test-amelioration-app-fullstack.git
cd P5-Full-Stack-testing
```

## 2) Installer le front

```bash
cd front
npm install
cd ..
```

## Lancer Docker (MySQL)

Le back utilise `back/compose.yaml` + `back/.env`.

Depuis `back/` :

```bash
docker compose up -d
```

Vérifier que le conteneur tourne :

```bash
docker ps
```

Le conteneur attendu est `back_mysql`.

Arrêter Docker pour le projet :

```bash
docker compose down
```

## Lancer l'application

## 1) Lancer le back

Depuis `back/` :

```bash
mvn spring-boot:run
```

- API disponible sur `http://localhost:8080`
- Le profil par défaut lit les variables de `back/.env`

## 2) Lancer le front

Depuis `front/` :

```bash
npm run start
```

Front disponible sur `http://localhost:4200`.

## Exécuter les tests

## Front - tests unitaires / intégration

Depuis `front/` :

```bash
npm run test
```

Mode watch :

```bash
npm run test:watch
```

Couverture Jest :

```bash
npm run test:coverage
```

## Front - tests E2E (Cypress)

Depuis `front/` :

```bash
npm run e2e:ci
```

Générer le rapport de couverture E2E :

```bash
npm run e2e:coverage
```

## Back - tests unitaires / intégration

Depuis `back/` :

```bash
mvn test
```

## Générer les rapports de couverture

## Back (JaCoCo)

Depuis `back/` :

```bash
mvn verify
```

Cette commande :
- exécute les tests,
- génère le rapport JaCoCo,
- applique la règle de seuil configurée dans `back/pom.xml`.

Rapports back :
- HTML : `back/target/site/jacoco/index.html`
- XML : `back/target/site/jacoco/jacoco.xml`

## Front (Jest + NYC)

```bash
cd front
npm run test:coverage
npm run e2e:ci
npm run e2e:coverage
```

Rapport front :
- `front/coverage/lcov-report/index.html`

## Vérification du seuil de couverture

## Back
Le seuil est contrôlé automatiquement lors de `mvn verify`.

## Front
Vérifier le résumé en console et le rapport HTML dans `front/coverage/`.

## Commandes rapides

```bash
# Terminal 1 : back
cd back
docker compose up -d
mvn spring-boot:run

# Terminal 2 : front
cd front
npm run start

# Tests + couverture back
cd ../back
mvn verify

# Tests + couverture front
cd ../front
npm run test:coverage
npm run e2e:ci
npm run e2e:coverage
```

## README détaillés

Pour plus de détails propres à chaque partie :
- `back/README.md`
- `front/README.md`
