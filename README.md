# HellMetz Festival

## Objectif

**HellMetz Festival** est le fil rouge pluri-technologique du BTS SIO SLAM. Il permet d’aborder progressivement toutes les compétences de la formation, de l’intégration web jusqu’à l’architecture logicielle avancée avec API, mobile et intelligence artificielle. Vous allez concevoir et développer la plateforme officielle d’un grand festival de Metal à Metz : gestion des groupes, des concerts, de la logistique et des ressources humaines, communication avec les festivaliers, et assistant IA. Le projet est découpé en quatre grandes phases, qui couvrent toute la formation.

## Table des matières

- [Versions](#versions)
- [Résumé pédagogique](#resume-pedagogique)
- [Structure du projet](#structure-du-projet)
- [Travail demandé](#travail-demande)
  - [Étape 1 — Frontoffice](#etape-1)
  - [Étape 2 — Backoffice](#etape-2)
  - [Compléter le CRUD](#crud)
- [Connexion à la base de données](#connexion-base)
- [Prérequis](#prerequis)
- [Lancer l’application](#lancer)
- [URLs utiles](#urls-utiles)
- [Bonus](#bonus)
- [Règles Git](#regles-git)
- [Sécurité et keystore](#securite-keystore)
- [Besoin d’aide](#aide)

<a id="versions"></a>

## Versions

Tableau à compléter au fil du projet. Indiquez la version, la date et ce que la mise à jour apporte. Les lignes ci-dessous sont des exemples à remplacer, et non des versions réelles.

<table>
<thead>
<tr>
<th>Version</th>
<th>Date</th>
<th>Changements</th>
</tr>
</thead>
<tbody>
<tr>
<td>v2.3.1</td>
<td>24/11/2026</td>
<td>
<ul>
<li>À la modification d’un utilisateur, enregistre tous les champs (nom, prénom, e-mail, identifiant, rôle, actif) ; mot de passe conservé si le champ est vide, sinon ré-encodé (<a href="https://github.com/LK7136/SIOE/issues/16">#16</a>, <a href="https://github.com/LK7136/SIOE/pull/42">PR #42</a>)</li>
<li>Photos artistes et groupes gérées via <code>StorageService</code> dans <code>uploads/artiste_image</code> et <code>uploads/groupe_image</code> (plus de chemin local fixe) (<a href="https://github.com/LK7136/SIOE/pull/118">#118</a>)</li>
</td>
</tr>
<tr>
<td>v2.3.0</td>
<td>05/11/2026</td>
<td>
<ul>
<li>Sur le tableau de bord, barre de défilement des modules affichée seulement si le contenu déborde</li>
<li>Pagination des listes adaptée aux couleurs de la charte du site</li>
<li>Correction de l’affichage et de la sauvegarde du logo (logo par défaut si aucun n’est en base)</li>
<li>Refonte des pages de création et d’édition des utilisateurs (<code>utilisateur/edit.html</code>)</li>
<li>Ajout de <code>editUtilisateurStyle.css</code> au layout et mise à jour du menu associé</li>
</td>
</tr>
<tr>
<td>v2.2.1</td>
<td>04/11/2026</td>
<td>
<ul>
<li>Surbrillance de l’onglet actif (ActiveMenu) pour Scènes, Artistes et Utilisateurs dans la sidebar (<a href="https://github.com/LK7136/SIOE/issues/73">#73</a>)</li>
<li>Upload et personnalisation du logo dans Paramètres (sidebar + favicon) (<a href="https://github.com/LK7136/SIOE/issues/71">#71</a>)</li>
<li>Nom de l’application lu depuis les paramètres et affiché dans le titre navigateur, la sidebar et l’en-tête (<a href="https://github.com/LK7136/SIOE/issues/65">#65</a>)</li>
<li>Aperçu du plan technique réparé dans l’édition des scènes (URL fichier correcte, plus d’image cassée) (<a href="https://github.com/LK7136/SIOE/issues/67">#67</a>)</li>
<li>Dates de concert limitées à la fenêtre du festival, avec contrôles HTML et serveur (<a href="https://github.com/LK7136/SIOE/issues/12">#12</a>)</li>
<li>Persistance du logo applicatif en base et affichage cohérent dans les vues</li>
</ul>
</td>
</tr>
<tr>
<td>v2.2</td>
<td>13/10/2026</td>
<td>
<ul>
<li>Onglets Paramètres et Utilisateurs remontés en haut de la sidebar (<a href="https://github.com/LK7136/SIOE/issues/59">#59</a>)</li>
<li>Page Paramètres : ajustements d’affichage et centrage du champ « Nom de l’application »</li>
<li>Journalisation Logback (<code>logback-spring.xml</code>) : console en dev, fichiers <code>logs/sioe.log</code> / audit en prod avec rotation et exclusion du dépôt via <code>.gitignore</code>(<a href="https://github.com/LK7136/SIOE/issues/23">#23</a>)</li>
</ul>
</td>
</tr>
<tr>
<td>v2.1</td>
<td>13/10/2026</td>
<td>
<ul>
<li>Pages d’erreur HTTP 403, 405 et 500 stylées sur le modèle de la 404 (<a href="https://github.com/LK7136/SIOE/issues/20">#20</a>)</li>
<li>Nettoyage sécurité du dépôt : secrets, <code>.gitignore</code>, keystore SSL retiré du dépôt et documenté pour génération locale (<a href="https://github.com/LK7136/SIOE/issues/31">#31</a>)</li>
<li>Formulaire utilisateurs : grille des permissions affichée et pré-cochée selon le rôle (<a href="https://github.com/LK7136/SIOE/issues/15">#15</a>)</li>
<li>Enregistrement des paramètres fiabilisé : validation par type, messages flash, plus de décalage des valeurs (<a href="https://github.com/LK7136/SIOE/issues/10">#10</a>)</li>
</ul>
</td>
</tr>
<tr>
<td>v2.01</td>
<td>06/10/2026</td>
<td>
<ul>
<li>Correction du nommage des scripts SQL dans <code>db/migration</code> et alignement avec l’entité <code>Edition</code></li>
</ul>
</td>
</tr>
<tr>
<td>v2.0</td>
<td>18/09/2026</td>
<td>
<ul>
<li>Initialisation de l’application avec le framework Spring Boot</li>
</ul>
</td>
</tr>
</tbody>
</table>

## Partie technique

<a id="resume-pedagogique"></a>

### Résumé pédagogique (version courte)

| Phase | Livrable | Compétences clés |
|-------|----------|------------------|
| 1 | Site statique | HTML/CSS/JS, Docker, Git |
| 2 | Backoffice organisateurs | Java Web, MVC, PostgreSQL, Sécurité |
| 3 | API REST + App mobile | REST, Swagger, Flutter, JSON |
| 4 | HellBot IA + Optimisation | IA, Redis, DevOps |

<a id="structure-du-projet"></a>

### Structure du projet

```text
src/
├── main/
│   ├── java/
│   │   └── com.hellmetz.festival/
│   │       ├── config/              → Configuration Spring
│   │       ├── controller/          → Contrôleurs web
│   │       ├── dashboard/           → Logique dashboard
│   │       ├── model/               → Entités / classes métiers
│   │       ├── parametre/           → Gestion des paramètres
│   │       ├── repository/          → Accès base de données (JPA)
│   │       ├── security/            → Sécurité (Spring Security)
│   │       ├── service/             → Couche métier
│   │       └── HellMetzApplication  → Point d’entrée Spring Boot
│   ├── META-INF/
│   └── resources/
│       ├── assets/
│       ├── db/
│       │   └── migration/           → Scripts de migration SQL
│       ├── static/                  → Fichiers statiques (CSS, JS, images…)
│       │   ├── css/
│       │   ├── documents/
│       │   │   └── plans/
│       │   ├── fonts/
│       │   ├── images/
│       │   ├── js/
│       │   └── video/
│       ├── templates/               → Vues Thymeleaf
│       │   ├── artiste/
│       │   ├── concert/
│       │   ├── dashboard/
│       │   ├── error/
│       │   ├── groupe/
│       │   ├── parametre/
│       │   ├── scene/
│       │   ├── utilisateur/
│       │   ├── dashboard.html
│       │   ├── layout.html
│       │   ├── login.html
│       │   ├── menu.html
│       │   └── register.html
│       ├── application.properties
│       ├── hellmetz-keystore.p12
│       └── logback-spring.xml
└── WEB-INF/
```

<a id="travail-demande"></a>

### Travail demandé

<a id="etape-1"></a>

#### Étape 1 — Frontoffice : créer la landing page HellMetz

Le fichier à modifier :

```text
src/main/webapp/frontoffice/index.html
```

Travail à réaliser :

- intégrer le template Bootstrap fourni par l’enseignant ;
- ajouter un logo HellMetz ;
- créer une section line-up avec :
  - la photo du groupe ;
  - le nom du groupe ;
  - le style ;
  - un petit texte descriptif ;
- ajouter un compte à rebours JavaScript jusqu’à la date du festival ;
- ajouter un formulaire de contact (HTML uniquement) ;
- organiser la page en sections propres et lisibles.

<a id="etape-2"></a>


#### Compléter le CRUD (à rendre)

Vous devez coder les trois opérations suivantes.

##### 3.1 Formulaire groupe (affichage)

Créer une servlet :

```text
GroupeFormServlet (GET)
```

Elle affiche un formulaire (JSP) permettant d’ajouter ou de modifier un groupe.

##### 3.2 Enregistrement (POST)

Créer une servlet :

```text
GroupeFormServlet (POST)
```

Elle appelle :

- `dao.insert(groupe)` si l’ID est vide ;
- `dao.update(groupe)` si l’ID est présent.

##### 3.3 Suppression d’un groupe

Servlet :

```text
GroupeDeleteServlet
```

Elle supprime un groupe via son ID, puis redirige vers `/backoffice/groupes`.

<a id="connexion-base"></a>

### Connexion à la base de données

La configuration se trouve dans :

```text
src/main/resources/application.properties
```

Elle contient :

```properties
db.url = jdbc:postgresql://localhost:5432/hellmetz
db.user = hellmetz_user
db.password = **********
```

Ne modifiez pas ces valeurs : elles sont définies par l’enseignant.

<a id="prerequis"></a>

### Prérequis

- **Tomcat 11** (Jakarta EE 10). Téléchargement : <https://tomcat.apache.org/>
- **PostgreSQL** installé. Base : `hellmetz`
- **Java 17+**
- **IntelliJ IDEA** Ultimate (ou Community + plugin payara/tomcat)

<a id="lancer"></a>

### Lancer l’application

1. Dans IntelliJ : **Add Configuration**, puis **Tomcat**.
2. Déployer l’artifact `web-hellmetz:war exploded`.
3. Définir le context path : `/hellmetz`.
4. Lancer avec **Run**.

<a id="bonus"></a>

### Bonus

Si vous avez terminé le CRUD :

- ajouter l’upload d’image pour les groupes ;
- ajouter la recherche et le filtrage des groupes ;
- intégrer Bootstrap au backoffice ;
- ajouter une pagination (10 groupes/page) ;
- ajouter les entités Scènes, Concerts et Matériel ;
- réaliser l’API REST (plus tard dans le cursus).

<a id="regles-git"></a>

### Règles Git

Pour récupérer les mises à jour du squelette :

```bash
git pull
```

Ne faites jamais :

```bash
git push
```

Ce dépôt est en lecture seule. Pour travailler dans votre propre dépôt :

- forkez ce projet ;
- ou créez votre propre dépôt à partir de ce squelette.

<a id="securite-keystore"></a>

### Sécurité et keystore

Le fichier `keystore` n’est plus inclus dans le dépôt, pour des raisons de sécurité.

Pour générer votre propre fichier localement :

```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
```

<a id="aide"></a>

### Besoin d’aide ?

Le projet est progressif : les difficultés sont normales. Posez vos questions en cours ou sur Discord.

Bonne chance, et bon développement ! 🤘🔥

**HellMetz vous attend.**
