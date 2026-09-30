# 🎮 Tierlist Creator

Application de bureau interactive développée en **Java (JavaFX)** permettant de créer, personnaliser, organiser et sauvegarder des Tier-lists de manière fluide grâce au système de **Glisser-Déposer (Drag & Drop)**. L'application intègre également des fonctionnalités de recherche connectée via des **APIs externes (TMDB et RAWG)** pour importer automatiquement des films, séries et jeux vidéo avec leurs visuels officiels.

---

## ✨ Fonctionnalités principales

* **Gestion multi-vues (Architecture MVC) :**
  * **Écran d'accueil & Configuration :** Saisie, validation et persistance sécurisée des clés API (TMDB et RAWG) ainsi que gestion des sauvegardes automatiques.
  * **Éditeur dynamique (`Vue1`) :** Manipulation des items par Drag & Drop entre la zone non classée et les rangs (Tiers). Modification en temps réel des rangs (nom, couleur personnalisée via `ColorPicker`, hauteur, ordre).
  * **Tableau de bord (`Vue2`) :** Vue d'ensemble de toutes vos Tier-lists, avec options de duplication (via sérialisation profonde), de suppression et d'exportation de fichiers de sauvegarde `.ser`.
  * **Assistant d'ajout d'items (`Vue3`) :** Ajout de texte personnalisé, import d'images locales ou **recherche automatisée en ligne**.
* **Intégration d'APIs Tierces :**
  * **RAWG API :** Recherche et récupération automatique des affiches/bannières de jeux vidéo.
  * **TMDB API :** Recherche et récupération des affiches de films et séries.
* **Persistance des données :** Sauvegarde locale et sérialisation binaire (`.ser`) pour conserver l'état des configurations et des projets d'une session à l'autre sans nécessiter de base de données lourde.

---

## 🛠️ Stack technique & Technologies

* **Langage :** Java (Programmation Orientée Objet)
* **Interface graphique :** JavaFX (FXML, CSS, Contrôleurs dédiés)
* **Requêtes HTTP & Parsing :** OkHttp, Google Gson, `java.net.URLEncoder`
* **Gestion d'État & Persistance :** Pattern Singleton (`DataManager`), sérialisation Java (`Serializable`, `ObjectOutputStream` / `ObjectInputStream`)

---

## 📂 Architecture du Projet (`src/application`)

* `TiersListApplication.java` : Classe principale de lancement de l'application JavaFX.
* **Modèles :**
  * `TierList.java` : Représente une tier-list complète (nom, liste de rangs, items non classés).
  * `Tier.java` : Représente un rang individuel (nom, couleur, hauteur, items associés).
  * `Item.java` : Représente un élément (texte ou chemin d'image/URL).
  * `AppConfig.java` : Gestion des paramètres globaux et clés API.
* **Gestionnaires :**
  * `DataManager.java` : Singleton centralisant l'accès aux données et aux listes en mémoire.
  * `ConfigManager.java` : Gestion de la sauvegarde/chargement des configurations.
  * `MultiApiManager.java` : Pilotage des appels aux APIs TMDB et RAWG.
* **Contrôleurs & Vues (FXML) :**
  * `HomeController` (`home.fxml`)
  * `Vue1Controller` (`vue1.fxml`)
  * `Vue2Controller` (`vue2.fxml`)
  * `Vue3Controller` (`vue3.fxml`)

---

## 🚀 Lancement et Installation

1. Assurez-vous d'avoir un environnement de développement Java configuré avec le **SDK JavaFX** (version 21+ recommandée).
2. Clonez le dépôt et importez le projet dans votre IDE (IntelliJ IDEA, Eclipse, etc.).
3. Configurez les bibliothèques **JavaFX**, **OkHttp** et **Gson** dans le classPath/modulePath de votre projet.
4. Exécutez la classe `Launcher.java` pour démarrer l'application.

---
*Développé dans le cadre du BUT Informatique — IUT de Laval.*
