# ExpensesManager

Application de gestion des dépenses développée avec JavaFX.

## Fonctionnalités

- Tableau récapitulatif des dépenses mensuelles
- Ajout et suppression de dépenses
- Catégorisation des dépenses (logement, nourriture, sorties, etc.)
- Persistance des données dans une base SQLite

## Prérequis

- Java 17 ou supérieur
- Gradle 7.0 ou supérieur (pour le développement)

## Installation

### Avec l'installateur (recommandé)

1. Téléchargez l'installateur correspondant à votre système d'exploitation depuis la section des releases.
2. Exécutez l'installateur et suivez les instructions.

### Depuis les sources

```bash
# Cloner le dépôt
git clone https://github.com/votre-utilisateur/expenses-manager.git
cd expenses-manager

# Compiler et lancer l'application
./gradlew run
```

## Développement

### Compilation

```bash
./gradlew build
```

### Création d'un installateur

```bash
./gradlew createInstaller
```

L'installateur sera généré dans le dossier `build/jpackage/`.

### Structure du projet

- `src/main/java/fr/expenses/tableau` : Code source Java
- `src/main/resources/fr/expenses/tableau` : Ressources (FXML, CSS, images)
- `src/main/resources/logback.xml` : Configuration du logging

## Configuration

Les données de l'application sont stockées dans le répertoire utilisateur :

- Windows : `C:\Users\<utilisateur>\.expenses-manager`
- Linux : `/home/<utilisateur>/.expenses-manager`
- macOS : `/Users/<utilisateur>/.expenses-manager`

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails. 
+