# 📱 BabyTracker - Système de Suivi du Développement des Bébés

Une application Android complète pour suivre le développement des bébés, gérer les vaccinations, monitorer la croissance (poids, taille, IMC) et faciliter la communication entre parents, nounous et médecins.

---

## 📋 Table des Matières

- [Description du Projet](#description-du-projet)
- [Architecture](#architecture)
  - [Front-End](#front-end)
  - [Back-End](#back-end)
- [Technologies Utilisées](#technologies-utilisées)
- [Fonctionnalités](#fonctionnalités)
- [Structure du Projet](#structure-du-projet)
- [Installation et Configuration](#installation-et-configuration)
- [Démonstration](#démonstration)
- [Captures d'Écran](#captures-décran)
- [Licence](#licence)

---

## 🎯 Description du Projet

**BabyTracker** est une application mobile Android destinée aux parents, nounous et professionnels de santé pour suivre le développement complet des bébés. L'application offre un système centralisé de gestion des données médicales et de croissance, avec des fonctionnalités de suivi en temps réel, de notifications et de communication entre les différents acteurs.

### Problématiques Résolues

- ✅ **Centralisation des données** : Toutes les informations sur le bébé sont centralisées en un seul endroit
- ✅ **Suivi médical facilité** : Graphiques de croissance, calcul automatique des vaccinations
- ✅ **Communication améliorée** : Chat et événements pour faciliter l'échange entre parents, nounous et médecins
- ✅ **Accès multi-rôles** : Système d'authentification avec différents niveaux d'accès
- ✅ **Mode hors-ligne** : Stockage local SQLite pour accéder aux données même sans connexion

---

## 🏗️ Architecture

L'application suit une architecture hybride combinant des services cloud (Firebase) et un stockage local (SQLite) pour optimiser les performances et la disponibilité.

### Front-End

Le front-end est développé en **Java** pour Android, utilisant les composants modernes de l'écosystème Android.

#### Composants Principaux

- **Activités (Activities)** : Point d'entrée de l'application
  - `LoginActivity.java` - Authentification utilisateur
  - `RegisterActivity.java` - Inscription de nouveaux utilisateurs
  - `SelectRole.java` - Sélection du rôle (Parent/Nounou/Docteur)
  - `MainLanding.java` - Écran principal avec navigation par fragments
  - `FirstTimeGuardian.java` - Premier enregistrement d'un bébé
  - `WeightGraph.java`, `HeightGraph.java`, `BmiGraph.java` - Visualisation des graphiques de croissance
  - `IndividualBabyVaccines.java` - Gestion des vaccinations
  - `EventManagementActivity.java` - Gestion des événements médicaux
  - `Doctor.java`, `MidWife.java` - Interfaces spécifiques aux professionnels

- **Fragments** : Composants UI réutilisables
  - `homeFragment.java` - Tableau de bord principal
  - `profileFragment.java` - Profil utilisateur
  - `VaccineFragment.java` - Liste des vaccinations
  - `MedicineFragment.java` - Gestion des médicaments
  - `extrasFragment.java` - Fonctionnalités supplémentaires

- **Adapters** : Gestion des listes et RecyclerViews
  - `BabyAdapter.java` - Adapter pour la liste des bébés
  - `BabyAdapterMidWife.java` - Adapter pour la vue nounou
  - `VaccineRecViewAdapter.java` - Adapter pour les vaccinations

#### Interface Utilisateur

- **Material Design** : Utilisation des composants Material Design pour une interface moderne
- **Graphiques** : Bibliothèque **MPAndroidChart** pour visualiser les courbes de croissance
- **Animations** : Transitions fluides entre les écrans
- **Multilingue** : Support français et anglais avec changement de langue dynamique
- **Responsive** : Adaptation à différentes tailles d'écran Android

#### Ressources

- **Layouts XML** : Tous les layouts dans `res/layout/`
- **Drawables** : Icônes vectorielles et images dans `res/drawable/`
- **Strings** : Gestion multilingue dans `res/values/` et `res/values-en/`
- **Thèmes** : Support du mode sombre dans `res/values-night/`

---

### Back-End

Le back-end utilise une architecture hybride combinant **Firebase (Cloud)** et **SQLite (Local)** pour garantir performance, synchronisation et disponibilité hors ligne.

#### Services Firebase (Cloud)

1. **Firebase Authentication**
   - Authentification par email/mot de passe (hash SHA-256)
   - Google Sign-In pour connexion rapide
   - Gestion des sessions utilisateur

2. **Cloud Firestore**
   - Collection `guardians` : Stockage des données bébé (poids, taille, informations personnelles)
   - Sous-collection `vaccines` : Vaccinations associées à chaque bébé
   - Collection `standardvaccinations` : Vaccins standards de référence
   - Collection `events` : Événements médicaux et rendez-vous
   - Synchronisation en temps réel entre appareils

3. **Firebase Cloud Messaging (FCM)**
   - Notifications push pour rappels de vaccinations
   - Alertes pour nouveaux événements médicaux
   - Service `NotificationService.java` pour la gestion des notifications

4. **Firebase Analytics**
   - Suivi de l'utilisation de l'application
   - Analyse des performances

#### Base de Données SQLite (Local)

La base de données locale `baby_tracking.db` sert de cache et permet l'accès hors ligne :

**Tables Principales :**

- `users` : Utilisateurs de l'application (parents, nounous, docteurs)
- `guardians` : Informations des bébés (nom, date de naissance, genre, poids/taille actuels)
- `vaccines` : Vaccinations individuelles avec dates et statut
- `standardvaccinations` : Vaccins standards de référence
- `medicines` : Médicaments prescrits avec posologie
- `weight_records` : Historique des mesures de poids
- `height_records` : Historique des mesures de taille
- `events` : Événements personnalisés créés par les professionnels
- `chat_messages` : Messages de chat entre parents et professionnels
- `notifications` : Notifications locales
- `DoctorLog`, `NannyLog` : Logs des professionnels

#### Classes Utilitaires Back-End

- **`FirestoreHelper.java`** : Gestion de toutes les opérations Firestore (CRUD)
- **`DatabaseHelper.java`** : Wrapper pour accès unifié Firestore/SQLite
- **`BabyDatabase.java`** : Classe principale SQLite avec schéma complet
- **`AuthHelper.java`** : Gestion de l'authentification (hash SHA-256, sessions)
- **`BabyVaccination.java`** : Calcul automatique des dates de vaccinations
- **`NotificationService.java`** : Service de notifications Firebase

#### DAO (Data Access Objects)

Classes d'accès aux données pour SQLite :
- `UserDao.java` - Gestion des utilisateurs
- `GuardianDao.java` - Gestion des bébés
- `VaccineDao.java` - Gestion des vaccinations
- `MedicineDao.java` - Gestion des médicaments
- `RecordDao.java` - Gestion des enregistrements poids/taille
- `EventDao.java` - Gestion des événements
- `ChatDao.java` - Gestion des messages de chat

---

## 🛠️ Technologies Utilisées

### Langages & Frameworks
- **Java** - Langage principal de développement
- **Android SDK 34** - Plateforme cible
- **Minimum SDK 26** (Android 8.0) - Compatibilité large

### Bibliothèques Front-End
- **Material Design Components** (`com.google.android.material:material:1.9.0`) - Composants UI modernes
- **MPAndroidChart** (`com.github.PhilJay:MPAndroidChart:v3.1.0`) - Graphiques et visualisations
- **Lottie** (`com.airbnb.android:lottie:6.0.1`) - Animations JSON
- **Glide** (`com.github.bumptech.glide:4.11.0`) - Chargement et cache d'images

### Services Back-End Firebase
- **Firebase BOM 32.1.1** - Gestion centralisée des versions Firebase
- **Firebase Authentication** - Authentification utilisateurs
- **Firebase Firestore** - Base de données NoSQL cloud
- **Firebase Cloud Messaging** - Notifications push
- **Firebase Analytics** - Analyse d'utilisation
- **Google Play Services Auth** (`20.6.0`) - Google Sign-In

### Base de Données
- **SQLite** - Base de données locale Android (Room Database pattern)
- **Firestore** - Base de données cloud Firebase

### Build Tools
- **Gradle** - Système de build
- **Android Gradle Plugin 8.13.2** - Plugin de build Android
- **Google Services Plugin 4.4.4** - Intégration Firebase

### Outils de Développement
- **Android Studio** - IDE recommandé
- **View Binding** - Liaison de vues type-safe

---

## ✨ Fonctionnalités

### 🔐 Authentification & Rôles

- **Inscription/Connexion** : Système d'authentification sécurisé avec hash SHA-256
- **Google Sign-In** : Connexion rapide via compte Google
- **Rôles Multiples** :
  - **Parent/Guardian (G)** : Accès complet aux données de ses bébés
  - **Nounou/Midwife (M)** : Accès aux bébés assignés, création d'événements
  - **Docteur (D)** : Consultation médicale, création d'événements et prescriptions

### 👶 Enregistrement Bébé

- Formulaire en 2 étapes (informations personnelles + informations bébé)
- Calcul automatique des vaccinations à partir de la date de naissance
- Synchronisation immédiate avec Firestore
- Stockage local pour accès hors ligne

### 📊 Suivi de la Croissance

- **Poids** : Enregistrement et suivi historique avec graphiques
- **Taille** : Enregistrement et suivi historique avec graphiques
- **IMC** : Calcul automatique avec visualisation graphique
- **Comparaison** : Courbes de croissance comparées aux standards médicaux
- **Graphiques interactifs** : Bibliothèque MPAndroidChart pour visualisation professionnelle

### 💉 Gestion des Vaccinations

- **Calcul automatique** : Dates de vaccination calculées selon l'âge du bébé
- **Suivi de statut** : Vaccins administrés, à venir, en retard
- **Rappels** : Notifications push pour les vaccinations à venir
- **Historique complet** : Suivi de toutes les vaccinations

### 💊 Gestion des Médicaments

- Enregistrement des médicaments prescrits
- Posologie et fréquence
- Dates de début et fin de traitement
- Notes et informations prescripteur

### 📅 Gestion des Événements

- Création d'événements médicaux par les professionnels
- Rappels et notifications
- Historique complet des événements

### 💬 Communication

- Système de chat entre parents et professionnels
- Messages stockés localement et synchronisés
- Interface de conversation intuitive

### 🔔 Notifications

- Notifications push via Firebase Cloud Messaging
- Rappels de vaccinations
- Alertes pour nouveaux événements
- Notifications locales pour actions importantes

### 🌐 Multilingue

- Support **Français** et **Anglais**
- Changement de langue dynamique dans l'application
- Traduction complète de l'interface

### 🌙 Mode Sombre

- Support du thème sombre (dark mode)
- Adaptation automatique selon les préférences système

---

## 📁 Structure du Projet

```
BabyTracker/
│
├── README.md                    # Ce fichier
├── FIREBASE_SETUP_GUIDE.md     # Guide de configuration Firebase
├── .gitignore                   # Fichiers ignorés par Git
│
└── babyOne/                     # Module principal Android
    │
    ├── app/
    │   ├── build.gradle         # Configuration Gradle du module
    │   ├── google-services.json # Configuration Firebase (à remplacer)
    │   ├── firestore.rules      # Règles de sécurité Firestore
    │   ├── proguard-rules.pro   # Règles ProGuard
    │   │
    │   └── src/
    │       └── main/
    │           ├── AndroidManifest.xml
    │           │
    │           ├── java/com/example/babyone/
    │           │   │
    │           │   ├── Activities/
    │           │   │   ├── LoginActivity.java
    │           │   │   ├── RegisterActivity.java
    │           │   │   ├── MainLanding.java
    │           │   │   ├── FirstTimeGuardian.java
    │           │   │   ├── WeightGraph.java
    │           │   │   ├── HeightGraph.java
    │           │   │   ├── BmiGraph.java
    │           │   │   └── ...
    │           │   │
    │           │   ├── Fragments/
    │           │   │   ├── homeFragment.java
    │           │   │   ├── profileFragment.java
    │           │   │   ├── VaccineFragment.java
    │           │   │   └── ...
    │           │   │
    │           │   ├── database/
    │           │   │   ├── BabyDatabase.java      # Schéma SQLite
    │           │   │   └── dao/                   # Data Access Objects
    │           │   │       ├── UserDao.java
    │           │   │       ├── GuardianDao.java
    │           │   │       ├── VaccineDao.java
    │           │   │       └── ...
    │           │   │
    │           │   ├── utils/
    │           │   │   ├── AuthHelper.java        # Authentification
    │           │   │   ├── DatabaseHelper.java    # Wrapper Firestore/SQLite
    │           │   │   └── LanguageHelper.java    # Gestion langues
    │           │   │
    │           │   ├── FirestoreHelper.java       # Opérations Firestore
    │           │   ├── BabyVaccination.java       # Calcul vaccinations
    │           │   ├── NotificationService.java   # Service notifications
    │           │   └── ...
    │           │
    │           └── res/
    │               ├── layout/                    # Layouts XML
    │               ├── drawable/                  # Images et icônes
    │               ├── values/                    # Strings, colors, themes
    │               ├── values-en/                 # Traductions anglaises
    │               └── ...
    │
    ├── build.gradle              # Configuration Gradle projet
    ├── settings.gradle           # Paramètres du projet
    ├── gradle.properties         # Propriétés Gradle
    │
    ├── gradle/
    │   └── wrapper/              # Gradle Wrapper
    │
    ├── gradlew                   # Script Gradle (Unix)
    ├── gradlew.bat               # Script Gradle (Windows)
    │
    └── cloud func/               # Fonctions Firebase Cloud (optionnel)
        └── functions/
            └── index.js
```

---

## 🚀 Installation et Configuration

### Prérequis

- **Android Studio** (dernière version recommandée)
- **Android SDK 34** installé
- **JDK 8+** configuré
- Un **projet Firebase** créé (voir guide ci-dessous)
- **Git** pour cloner le projet

### Étapes d'Installation

1. **Cloner le projet**
   ```bash
   git clone https://github.com/votre-username/BabyTracker.git
   cd BabyTracker
   ```

2. **Ouvrir dans Android Studio**
   - Ouvrir Android Studio
   - Sélectionner "Open an existing project"
   - Naviguer vers le dossier `babyOne`

3. **Configurer Firebase**
   
   **Important** : Suivez le guide complet dans `FIREBASE_SETUP_GUIDE.md`
   
   Résumé rapide :
   - Créer un projet Firebase sur [Firebase Console](https://console.firebase.google.com/)
   - Ajouter une application Android avec le package name : `com.example.babyone`
   - Télécharger `google-services.json` et le placer dans `babyOne/app/`
   - Activer les services suivants dans Firebase :
     - ✅ Authentication (avec Google Sign-In)
     - ✅ Cloud Firestore Database
     - ✅ Cloud Messaging (FCM)
   - Configurer les règles Firestore (copier depuis `babyOne/app/firestore.rules`)

4. **Synchroniser Gradle**
   - Android Studio va automatiquement synchroniser les dépendances Gradle
   - Attendre la fin du téléchargement des dépendances

5. **Construire et Exécuter**
   - Connecter un appareil Android ou lancer un émulateur (API 26+)
   - Cliquer sur "Run" ou utiliser `Shift + F10`
   - L'application sera installée et lancée

### Configuration Avancée

#### Variables d'Environnement

Si vous utilisez différents environnements (dev/prod), vous pouvez :
- Créer plusieurs fichiers `google-services.json` pour différents projets Firebase
- Utiliser des build flavors dans Gradle

#### Permissions

L'application nécessite les permissions suivantes (déjà configurées dans `AndroidManifest.xml`) :
- `INTERNET` - Pour accéder à Firebase

---

## 🎬 Démonstration

### Scénario d'Utilisation Type

1. **Première Utilisation**
   - Lancer l'application
   - S'inscrire ou se connecter (Google Sign-In ou email/mot de passe)
   - Sélectionner le rôle (Parent/Nounou/Docteur)
   - Pour un parent : Enregistrer le premier bébé avec ses informations

2. **Tableau de Bord**
   - Visualiser les informations clés : poids, taille, IMC, âge
   - Voir les prochaines vaccinations à venir
   - Accéder rapidement aux différentes sections

3. **Suivi de Croissance**
   - Ajouter une nouvelle mesure de poids/taille
   - Visualiser les graphiques de croissance
   - Comparer avec les courbes standards
   - Analyser l'évolution de l'IMC

4. **Gestion des Vaccinations**
   - Consulter la liste des vaccinations
   - Marquer une vaccination comme administrée
   - Recevoir des notifications pour les vaccinations à venir

5. **Communication (Nounou/Docteur)**
   - Consulter la liste des bébés assignés
   - Créer un événement médical
   - Communiquer avec les parents via le chat

### Fonctionnalités à Démontrer

✅ **Authentification sécurisée** - Hash SHA-256, Google Sign-In  
✅ **Enregistrement bébé** - Formulaire intuitif, calcul automatique vaccinations  
✅ **Graphiques interactifs** - Visualisation professionnelle de la croissance  
✅ **Notifications push** - Rappels automatiques  
✅ **Mode hors ligne** - Accès aux données sans connexion  
✅ **Multilingue** - Changement de langue dynamique  

---

### Exemples de sections à documenter :

- 🏠 **Écran d'accueil** - Interface de connexion
- 📊 **Tableau de bord** - Vue d'ensemble des données bébé
- 📈 **Graphiques** - Visualisation poids/taille/IMC
- 💉 **Vaccinations** - Liste et suivi des vaccinations
- 💬 **Chat** - Communication parents-professionnels
- ⚙️ **Profil** - Gestion du compte utilisateur

## 🐛 Problèmes Connus / TODO

- [ ] Améliorer la gestion des erreurs réseau
- [ ] Ajouter support de plus de langues
- [ ] Implémenter export PDF des données
- [ ] Ajouter synchronisation automatique SQLite ↔ Firestore
- [ ] Améliorer l'interface pour tablettes

---

## 📄 Licence

Copyright © 2026

Ce projet est sous licence propriétaire. Tous droits réservés.

---

## 👥 Auteurs

 **Ali TAQUAHOUT**
 **Mohammed BERNAK**
 
- **Équipe de Développement BabyTracker**

  **Ali TAQUAHOUT**
  **Mohammed BERNAK**

## 🙏 Remerciements

- **Firebase** - Pour les services cloud exceptionnels
- **MPAndroidChart** - Pour la bibliothèque de graphiques
- **Material Design** - Pour les composants UI
- **Communauté Android** - Pour le support et les ressources

---

## 📞 Support

Pour toute question ou problème :
- 📧 Ouvrir une issue sur GitHub
- 📖 Consulter le guide Firebase : `FIREBASE_SETUP_GUIDE.md`

