# 🎯 Objectifs du Projet HellMetz Festival

Le projet **HellMetz Festival** est un fil rouge pluri-technologique permettant d’aborder progressivement toutes les compétences du **BTS SIO SLAM**, depuis l’intégration web jusqu’à l’architecture logicielle avancée avec API, mobile et IA.

Vous allez concevoir et développer la **plateforme officielle** d’un grand festival de Metal à Metz :  
gestion des groupes, concerts, logistique, ressources humaines, communication avec les festivaliers, et même un **assistant IA**.

Le projet est découpé en **4 grandes phases**, couvrant toute la formation.

---

# 🟦 Phase 1 — Le « Teaser » (Mise en jambe)

**Durée : 1 mois**  
Objectif : mettre en place les outils, la collaboration, la structure du projet et produire un premier livrable visible.

## 🎯 Compétences visées
- HTML5 / CSS3 / Bootstrap ou Tailwind  
- JavaScript simple (compte à rebours)  
- Git : commit, branche, push, pull  
- Docker : conteneurisation d’un site statique  
- Organisation d’un projet web (arborescence, assets, pages)

## 📦 Livrable
Un **site web vitrine statique (landing page)** contenant :
- Le logo *HellMetz*  
- La **line-up** (liste des groupes) avec photos + descriptions  
- Un **compte à rebours** avant le festival  
- Un **formulaire de contact** (front uniquement)  
- Le tout intégré dans un **conteneur Docker (Nginx ou Apache)** et déployé sur la VM  

---

# 🟥 Phase 2 — Le « Cœur du Réacteur » : Backoffice Organisateurs

**Durée : 5 mois**  
C’est la partie la plus importante du Semestre 1 : création d’un **backoffice métier** pour l’équipe organisatrice.

## 🎯 Compétences visées
- Architecture Java Web : Servlets / JSP / JDBC puis Spring Boot  
- MVC : Modèle – Vue – Contrôleur  
- PostgreSQL : tables, clés étrangères, requêtes SQL  
- JPA / Hibernate (avec Spring Boot)  
- Sécurité (Spring Security)  
- Conception orientée métier (ERP simplifié)

## 🧰 Fonctionnalités backoffice (ERP)
- **Gestion des concerts** : CRUD Artistes / Scènes / Horaires  
- **Logistique & matériel** : inventaire (amplis, batteries, câbles) + gestion des prêts  
- **Facility Management** : planning de nettoyage et entretien des loges/scènes  
- **Ressources humaines** :
  - contrats intermittents  
  - gestion des bénévoles  
  - planning / affectation aux postes  
- **Infrastructure** :
  - localisation GPS des WC, foodtrucks, secours  
  - préparation des données pour l’app mobile  

## 🧱 Stack technique
- Java + Servlets / JSP / JDBC (début d'année)  
- Migration progressive vers **Spring Boot + Thymeleaf**  
- PostgreSQL + JDBC puis Hibernate/JPA  
- Sécurité : login / mots de passe (Spring Security)

---

# 🟧 Phase 3 — L’Expérience Festivalier (API REST + Mobile Flutter)

**Durée : 6 mois**  
Objectif : transformer le backoffice en véritable **plateforme connectée**, exposer des données en JSON et développer l’application mobile du festival.

## 🎯 Compétences visées
- Conception d’API REST  
- Documentation OpenAPI / Swagger  
- Développement mobile Flutter  
- Requêtes HTTP + parsing JSON  
- Cartographie (Google Maps / OpenStreetMap)

## 🧱 Évolutions backend
Création de **Controllers REST** :
- `/api/concerts`
- `/api/points-interets`
- `/api/groupes`

Et aussi :
- Versionnage de l’API  
- Documentation OpenAPI  
- Filtrage / tri / pagination dans les endpoints  

## 📱 Application mobile Flutter
Fonctionnalités à implémenter :
- Liste **chronologique** des concerts  
- **Carte interactive** du festival avec POI (WC / secours / foodtrucks)  
- **Fiches scènes** (photo, capacité, prochains concerts)  
- Petit espace social :
  - mini-forum “Bourse aux billets”  
  - messages “Covoiturage”  

---

# 🟪 Phase 4 — Intelligence & Optimisation : le « HellBot »

**Durée : 6 mois**  
Objectif : ajouter de la **valeur ajoutée via l’IA**, et optimiser les performances pour un scénario réaliste (10 000 festivaliers connectés).

## 🤖 L’Agent IA (HellBot)

Utilisation de **LangChain4j** dans le backend Java.

### Fonction 1 — Recommandation intelligente  
> “Le concert de Metallica finit dans 10 minutes → le Foodtruck A va être surchargé.  
> Je te recommande le Foodtruck B derrière la scène Valley.”

Basée sur :
- horaires des concerts  
- densité de festivaliers  
- trafic observé  
- données historiques  

### Fonction 2 — Chatbot festivalier  
Répond à :
- “Où sont les toilettes les plus proches ?”  
- “Qui joue après Gojira ?”  
- “Quel est l’accès PMR le plus proche ?”  

## 🚀 Optimisations backend
- Mise en cache Redis (planning, concerts, POI)  
- Monitoring (logs, performances)  
- Déploiement final sur VM via Docker Compose  

---

# 📚 Résumé pédagogique (version courte)

| Phase | Livrable | Compétences clés |
|-------|----------|------------------|
| 🟦 1 | Site statique | HTML/CSS/JS, Docker, Git |
| 🟥 2 | Backoffice organisateurs | Java Web, MVC, PostgreSQL, Sécurité |
| 🟧 3 | API REST + App mobile | REST, Swagger, Flutter, JSON |
| 🟪 4 | HellBot IA + Optimisation | IA, Redis, DevOps |

---

# 📁 Structure du projet
```text
hellmetz/
├── frontoffice/                 → Site vitrine statique (à compléter)
│   └── index.html
├── backoffice/                  → Interfaces JSP et pages du backoffice
│   └── (jsp)
├── WEB-INF/
│   ├── web.xml
│   └── (config serveur)
└── src/main/java/
    └── com.hellmetz.festival/
        ├── backoffice/
        │   ├── dao/            → Accès base de données (JDBC)
        │   ├── model/          → Classes métiers (POJO)
        │   └── servlet/        → Contrôleurs Java
        └── frontoffice/        → (non utilisé pour l’instant)


```
---

# 🧑‍🏫 Travail demandé (Étape 1)

## 🟦 1 — Frontoffice : créer la landing page HellMetz

Rendez-vous dans :

src/main/webapp/frontoffice/index.html


### Travail à réaliser :

✔️ Intégrer le **template Bootstrap** fourni par l’enseignant  
✔️ Ajouter un **logo HellMetz**  
✔️ Créer une **section Line-up** avec :
- Photo du groupe  
- Nom du groupe  
- Style  
- Petit texte descriptif  

✔️ Ajouter un **compte à rebours JS** jusqu'à la date du festival  
✔️ Ajouter un **formulaire de contact** (HTML uniquement)  
✔️ Organiser la page en sections propres et lisibles  

---

# 🧑‍🏫 Travail demandé (Étape 2)

## 🟥 2 — Backoffice : gestion des groupes

### URL du backoffice

/hellmetz/backoffice/groupes


Cette partie utilise :

- **Servlets**  
- **DAO JDBC**  
- **JSP (Jakarta EE)**  
- **Bootstrap** (à intégrer ensuite)

### Travail à réaliser :

✔️ Étudier le code existant :  
- `Groupe` (POJO)  
- `GroupeDao` (accès BDD)  
- `GroupeListServlet` (affichage liste)  
- `groupes.jsp` (affichage tableau)

---

## 🟪 3 — Compléter le CRUD (à rendre)

Vous devez coder :

### 3.1. Formulaire groupe (affichage)
Créer une servlet :

GroupeFormServlet (GET)


→ qui affiche un formulaire (JSP) permettant d’ajouter ou modifier un groupe.

### 3.2. Enregistrement (POST)
Créer une servlet :

GroupeFormServlet (POST)


→ qui appelle :  
- `dao.insert(groupe)` si ID vide  
- `dao.update(groupe)` si ID présent  

### 3.3. Suppression d’un groupe
Servlet :

GroupeDeleteServlet


→ Supprime un groupe via son ID  
→ Redirection vers `/backoffice/groupes`

---

# 🗃️ Connexion à la base de données

La configuration se trouve dans :

src/main/resources/db.properties


Elle contient :
```text
db.url = jdbc:postgresql://localhost:5432/hellmetz
db.user = hellmetz_user
db.password = **********
```

Ne modifiez pas ces valeurs (elles sont définies par l'enseignant).

---

# 🛠 Prérequis pour lancer le projet

### ✔️ Tomcat 11 (Jakarta EE 10)  
Téléchargement : https://tomcat.apache.org/

### ✔️ PostgreSQL installé  
Base : `hellmetz`

### ✔️ Java 17+  
### ✔️ IntelliJ IDEA Ultimate (ou Community + plugin payara/tomcat)

---

# 🚀 Lancer l’application

1. Dans IntelliJ → Add Configuration → Tomcat  
2. Déployer l’artifact :  

web-hellmetz:war exploded

3. Définir le context path :  

/hellmetz

4. Run ▶️

---

# 🔍 URLs utiles

| Fonction | URL |
|---------|------|
| Portail | `/hellmetz/` |
| Frontoffice | `/hellmetz/frontoffice/index.html` |
| Backoffice (groupes) | `/hellmetz/backoffice/groupes` |
| Test connexion DB | `/hellmetz/backoffice/test-db` |

---

# 🧪 Bonus (pour aller plus loin)

Si vous avez terminé le CRUD :

- Ajouter l’upload d’image pour les groupes  
- Ajouter recherche / filtrage des groupes  
- Intégrer Bootstrap au backoffice  
- Ajouter une pagination (10 groupes/page)  
- Ajouter les entités : Scènes, Concerts, Matériel  
- Réaliser l’API REST (plus tard dans le cursus)

---

# 🎓 Tips Git (à retenir)

Pour récupérer les mises à jour du squelette :

git pull


Ne faites **jamais** :

git push


Ce dépôt est en lecture seule.  
Pour travailler dans votre propre repo :

- Forkez ce projet  
- Ou créez votre propre dépôt à partir de ce squelette

---

# 📬 Besoin d’aide ?

Le projet est progressif, les difficultés sont normales.  
Posez vos questions en cours ou sur discord.

Bonne chance, et bon développement ! 🤘🔥  
**HellMetz vous attend.**
