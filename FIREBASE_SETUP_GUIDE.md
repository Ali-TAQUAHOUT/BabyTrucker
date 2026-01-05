# Guide de Configuration Firebase Backend - BabyTracker

Ce guide vous explique comment créer et configurer votre propre projet Firebase pour l'application BabyTracker.

## Étape 1 : Créer un Projet Firebase

1. Allez sur [Firebase Console](https://console.firebase.google.com/)
2. Cliquez sur **"Ajouter un projet"** ou **"Add project"**
3. Entrez un nom pour votre projet (ex: "babytracker-app")
4. Acceptez les conditions d'utilisation
5. Choisissez si vous voulez activer Google Analytics (optionnel)
6. Cliquez sur **"Créer le projet"**

## Étape 2 : Ajouter une Application Android

1. Dans votre projet Firebase, cliquez sur l'icône Android
2. Renseignez les informations suivantes :
   - **Package name** : `com.example.babyone`
   - **App nickname** : BabyTracker (optionnel)
   - **Debug signing certificate SHA-1** : (optionnel pour l'instant)

3. Cliquez sur **"Enregistrer l'application"**

## Étape 3 : Télécharger google-services.json

1. Téléchargez le fichier `google-services.json`
2. Remplacez le fichier existant dans votre projet :
   ```
   babyOne/app/google-services.json
   ```

## Étape 4 : Activer les Services Firebase Nécessaires

### 4.1 Firebase Authentication

1. Dans la console Firebase, allez dans **Authentication**
2. Cliquez sur **"Commencer"** ou **"Get started"**
3. Activez les méthodes de connexion suivantes :
   - **Google** : Activez et configurez avec votre email
   - **Email/Password** : Activez (si nécessaire)

### 4.2 Cloud Firestore Database

1. Allez dans **Firestore Database**
2. Cliquez sur **"Créer une base de données"**
3. Choisissez le mode :
   - **Mode production** (recommandé) ou **Mode test** pour commencer
4. Choisissez une localisation (ex: `us-central` ou `europe-west`)
5. Cliquez sur **"Activer"**

### 4.3 Realtime Database (si utilisé)

1. Allez dans **Realtime Database**
2. Cliquez sur **"Créer une base de données"**
3. Choisissez une localisation
4. Configurez les règles de sécurité (mode test pour commencer)

### 4.4 Cloud Messaging (FCM)

1. Allez dans **Cloud Messaging**
2. Le service est automatiquement activé avec votre projet
3. Notez le **Server Key** (sera nécessaire pour les notifications push)

## Étape 5 : Configurer les Règles de Sécurité Firestore

Dans **Firestore Database > Règles**, configurez les règles suivantes :

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Règles pour les guardians (parents)
    match /guardians/{guardianId} {
      // Les parents peuvent lire/écrire leurs propres données
      allow read, write: if request.auth != null && 
        resource.data.email == request.auth.token.email;
      
      // Les nannies et docteurs peuvent lire les données des guardians
      allow read: if request.auth != null;
      allow write: if request.auth != null; // Ajustez selon vos besoins
      
      // Sous-collections (vaccines, medicines, etc.)
      match /{subcollection=**} {
        allow read, write: if request.auth != null;
      }
    }
    
    // Règles pour les events
    match /events/{eventId} {
      allow read: if request.auth != null;
      allow create, update, delete: if request.auth != null; // Ajustez selon les rôles
    }
    
    // Règles pour les users
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

**Note** : Ajustez ces règles selon vos besoins de sécurité spécifiques.

## Étape 6 : Configurer les Règles de Sécurité Realtime Database

Si vous utilisez Realtime Database, configurez les règles dans **Realtime Database > Règles** :

```json
{
  "rules": {
    "chats": {
      "$chatRoomId": {
        ".read": "auth != null",
        ".write": "auth != null",
        "messages": {
          "$messageId": {
            ".read": "auth != null",
            ".write": "auth != null",
            ".validate": "newData.hasChildren(['text', 'senderId', 'senderRole', 'timestamp'])"
          }
        }
      }
    },
    "events": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

## Étape 7 : Configuration dans l'Application Android

### Vérifier build.gradle (Project level)

Le fichier `babyOne/build.gradle` doit contenir :

```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.google.gms:google-services:4.4.4'
    }
}
```

### Vérifier build.gradle (App level)

Le fichier `babyOne/app/build.gradle` doit contenir :

```gradle
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.1.1')
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-firestore'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-database'
    implementation 'com.google.firebase:firebase-messaging'
    implementation 'com.google.firebase:firebase-analytics'
    // ... autres dépendances
}
```

## Étape 8 : Configuration OAuth pour Google Sign-In

1. Dans Firebase Console, allez dans **Authentication > Sign-in method > Google**
2. Activez Google Sign-In
3. Cliquez sur **"Enregistrer"**
4. Notez le **Web client ID** (sera nécessaire si vous utilisez Google Sign-In)

## Étape 9 : Tester la Configuration

1. Synchronisez votre projet Android (Sync Project with Gradle Files)
2. Compilez et exécutez l'application
3. Testez la connexion avec Google
4. Vérifiez que les données sont bien sauvegardées dans Firestore

## Structure de Données Recommandée dans Firestore

### Collection: `guardians`
Document ID: Auto-généré
```
{
  email: "parent@example.com",
  babyname: "Baby Name",
  baby_bday: "01/01/2024",
  baby_gender: "Male",
  current_weight: 3.5,
  current_height: 50.0,
  parentname: "Parent Name",
  // Sous-collections:
  // - vaccines
  // - medicines
  // - weight_records
  // - height_records
}
```

### Collection: `events`
Document ID: Auto-généré
```
{
  guardian_email: "parent@example.com",
  event_title: "Vaccination",
  event_date: "2024-02-01",
  event_type: "vaccination",
  created_by: "nanny_id",
  created_at: timestamp
}
```

### Collection: `users` (optionnel)
Document ID: user_id
```
{
  username: "username",
  email: "user@example.com",
  role: "G" | "M" | "D", // Guardian, Midwife/Nanny, Doctor
  name: "User Name"
}
```

## Notes Importantes

1. **Sécurité** : Les règles de sécurité ci-dessus sont des exemples de base. Adaptez-les selon vos besoins réels de sécurité.

2. **Quotas** : Firebase propose un plan gratuit (Spark) avec des limites. Pour la production, envisagez le plan Blaze (pay-as-you-go).

3. **Sauvegarde** : Configurez des sauvegardes régulières de vos données Firestore.

4. **Monitoring** : Utilisez Firebase Console pour monitorer l'utilisation et les erreurs.

5. **Variables d'environnement** : Ne commitez jamais de clés API ou de certificats dans votre repository public.

## Support

- [Documentation Firebase](https://firebase.google.com/docs)
- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Support](https://firebase.google.com/support)

