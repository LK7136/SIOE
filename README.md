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

| Version | Date | Changements |
|---------|------|----------------|

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
<td>v2.2.1</td>
<td>05/11/2026</td>
<td>
<ul>
<li>Afficher le scroll des modules uniquement quand il est nécessaire</li>
<li>Pagination adapter au couleur du site</li>
<li>Nettoyage du dépôt : secrets, <code>.gitignore</code> et fichiers générés</li>
<li>Correctif de nomage sql</li>
<li>Ajout keystore</li>
<li>Qualité dépôt sécurité</li>
<li>Erreur feature 20</li>
<li>Keystore</li>
<li>Page erreur</li>
<li>Qualité dépôt sécurité</li>
<li>Bug utilisateurs (permissions selon le rôle)</li>
<li>Onglets Paramètres et Utilisateurs remontés dans la sidebar</li>
<li>Aperçu du plan technique</li>
<li>Dynamisation du nom de l’application</li>
<li>ActiveMenu sur Scènes, Artistes et Utilisateurs</li>
<li>Log tech</li>
<li>Modification paramètres + centrage du champ nom de l’application</li>
<li>Correction vue et sauvegarde du logo</li>
<li>Feature concert</li>
<li>Mise à jour du menu</li>
<li>Refonte complète de la page d’édition des utilisateurs</li>
<li>Refonte complète de la page de création des utilisateurs</li>
<li>Ajout de <code>editUtilisateurStyle.css</code> au layout</li>
<li>Refonte complète de la page d’édition des utilisateurs</li>
<li>Mise à jour du menu</li>
<li>Pagination adaptée aux couleurs du site</li>
<li>Afficher le scroll des modules uniquement quand nécessaire</li>
<li>Ajout du <code>sioe.log</code> dans le <code>.gitignore</code></li>
</ul>
</td>
</tr>
<tr>
<td>v2.2</td>
<td>à compléter</td>
<td>
<ul>
<li>Update edit.html</li>
<li>maj menu</li>
<li>Refonte complète de la page d’édition des utilisateurs</li>
<li>Add editUtilisateurStyle.css to layout.html</li>
<li>refonte complète de la page de création des utilisateurs</li>
<li>Paramètre 8</li>
<li>Ajout de sioe.log dans le gitignore</li>
</ul>
</td>
</tr>
<tr>
<td>v2.1.2</td>
<td>04/11/2026</td>
<td>
<ul>
<li>Ajout du ActiveMenu sur Scènes, Artistes &amp; Utilisateur</li>
<li>Ajout du logo et de la personnalisation de ce dernier dans les paramètres</li>
<li>Fix dynamisation nom appli 65</li>
<li>Aperçu du plan technique</li>
<li>Fix du ticket numéro #12 des issues</li>
<li>refonte complète de la page d’édition des utilisateurs</li>
<li>maj menu</li>
<li>Feature concert 12</li>
<li>Ajout de la sauvegarde de l’image dans la DB</li>
<li>Correction vue et sauvegarde du logo</li>
</ul>
</td>
</tr>
<tr>
<td>v2.1.1</td>
<td>13/10/2026</td>
<td>
<ul>
<li>Onglet parametres &amp; utilisateurs à remonter dans la side bar #59</li>
<li>correction de nomage des fichiers sql présent dans db\migration</li>
<li>Modification parametres + centrage du champ nom de l’application</li>
<li>Log tech 23 v2</li>
</ul>
</td>
</tr>
<tr>
<td>v2.1</td>
<td>13/10/2026</td>
<td>
<ul>
<li>Erreur feature 20</li>
<li>Qualité dépôt sécurité 31</li>
<li>Ajout keystore</li>
<li>gitignore</li>
<li>Revert "gitignore"</li>
<li>Bug utilisateurs 15</li>
<li>Page erreur</li>
<li>keystore</li>
<li>Fiabiliser l’enregistrement du formulaire</li>
<li>Onglet parametres &amp; utilisateurs à remonter dans la side bar #59</li>
<li>correction de nomage des fichiers sql présent dans db\migration</li>
<li>Modification parametres + centrage du champ nom de l’application</li>
<li>Log tech 23 v2</li>
</ul>
</td>
</tr>
<tr>
<td>v2.01</td>
<td>06/10/2026</td>
<td>
<ul>
<li>Nettoyage du dépôt : secrets, .gitignore et fichiers générés</li>
<li>Correctif de nomage sql</li>
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

#### Étape 2 — Backoffice : gestion des groupes

URL du backoffice :

```text
/hellmetz/backoffice/groupes
```

Cette partie utilise :

- les servlets ;
- le DAO JDBC ;
- les JSP (Jakarta EE) ;
- Bootstrap (à intégrer ensuite).

Travail à réaliser : étudier le code existant.

- `Groupe` (POJO)
- `GroupeDao` (accès BDD)
- `GroupeListServlet` (affichage de la liste)
- `groupes.jsp` (affichage du tableau)

<a id="crud"></a>

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
src/main/resources/db.properties
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

<a id="urls-utiles"></a>

### URLs utiles

| Fonction | URL |
|----------|-----|
| Portail | `/hellmetz/` |
| Frontoffice | `/hellmetz/frontoffice/index.html` |
| Backoffice (groupes) | `/hellmetz/backoffice/groupes` |
| Test connexion DB | `/hellmetz/backoffice/test-db` |

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
