# Guide complet : Construire et installer Boomer ConX

Ce guide t'explique comment transformer le code source en application Android installable sur le téléphone de ta mère.

---

## Table des matières

1. [Ce dont tu as besoin](#1-ce-dont-tu-as-besoin)
2. [Installer les outils de développement](#2-installer-les-outils-de-développement)
3. [Configurer le projet](#3-configurer-le-projet)
4. [Configurer Supabase (backend cloud)](#4-configurer-supabase-backend-cloud)
5. [Compiler l'application (créer l'APK)](#5-compiler-lapplication-créer-lapk)
6. [Signer l'APK pour la production](#6-signer-lapk-pour-la-production)
7. [Installer l'APK sur le téléphone de ta mère](#7-installer-lapk-sur-le-téléphone-de-ta-mère)
8. [Publier sur le Google Play Store (optionnel)](#8-publier-sur-le-google-play-store-optionnel)
9. [Dépannage](#9-dépannage)

---

## 1. Ce dont tu as besoin

### Matériel
- Un ordinateur (Windows, Mac ou Linux) avec au moins 8 Go de RAM
- Un câble USB pour connecter le téléphone Android de ta mère
- Le téléphone Android de ta mère (Android 8.0 ou plus récent)

### Logiciels à installer
| Logiciel | Pourquoi | Lien |
|----------|----------|------|
| Android Studio | L'outil principal pour construire l'app | https://developer.android.com/studio |
| Git | Pour télécharger le code source | https://git-scm.com/downloads |
| Un compte Google | Pour le Play Store (optionnel) | https://accounts.google.com |
| Un compte Supabase | Pour le backend cloud (gratuit) | https://supabase.com |

---

## 2. Installer les outils de développement

### Étape 2.1 : Installer Android Studio

1. Va sur https://developer.android.com/studio
2. Clique sur **Download Android Studio**
3. Installe-le comme n'importe quel programme
4. Au premier lancement, choisis **Standard Setup**
5. Laisse-le télécharger les composants (ça peut prendre 15-20 minutes)

### Étape 2.2 : Vérifier les SDK

Après l'installation d'Android Studio :

1. Ouvre Android Studio
2. Va dans **Settings** (ou **Preferences** sur Mac)
3. Cherche **SDK Manager** dans la barre de recherche
4. Sous l'onglet **SDK Platforms** :
   - Coche **Android 14 (API 34)**
   - Coche **Android 8.0 (API 26)** (version minimum)
5. Sous l'onglet **SDK Tools** :
   - Coche **Android SDK Build-Tools**
   - Coche **Android SDK Command-line Tools**
6. Clique **Apply** et attends le téléchargement

### Étape 2.3 : Installer Git

1. Va sur https://git-scm.com/downloads
2. Télécharge et installe la version pour ton système
3. Pour vérifier l'installation, ouvre un terminal et tape :

```bash
git --version
```

Tu devrais voir quelque chose comme `git version 2.x.x`.

---

## 3. Configurer le projet

### Étape 3.1 : Télécharger le code source

Ouvre un terminal et tape :

```bash
git clone https://github.com/mariejilec-glitch/claude-skills.git
cd claude-skills/boomer-conx
```

### Étape 3.2 : Ouvrir le projet dans Android Studio

1. Ouvre Android Studio
2. Clique sur **Open** (pas "New Project")
3. Navigue jusqu'au dossier `claude-skills/boomer-conx`
4. Clique **OK**
5. Attends que Gradle synchronise le projet (barre de progression en bas)
   - La première sync peut prendre 5-10 minutes
   - Si on te demande d'installer des composants manquants, clique **Install**

### Étape 3.3 : Vérifier la structure du projet

Dans le panneau de gauche d'Android Studio, tu devrais voir :

```
BoomerConX
├── app
├── core
│   ├── core-common
│   ├── core-security
│   ├── core-ui
│   └── core-data
├── feature
│   ├── feature-onboarding
│   ├── feature-home
│   ├── feature-backup
│   ├── feature-vault
│   ├── feature-ice
│   ├── feature-scam-shield
│   ├── feature-launcher
│   └── feature-accessibility
└── supabase
```

Si c'est le cas, le projet est bien configuré!

---

## 4. Configurer Supabase (backend cloud)

Supabase gère la synchronisation cloud (sauvegardes, anti-arnaques). C'est **gratuit** pour un usage personnel.

### Étape 4.1 : Créer un projet Supabase

1. Va sur https://supabase.com et crée un compte
2. Clique sur **New Project**
3. Configure :
   - **Name** : `boomer-conx`
   - **Database Password** : Choisis un mot de passe fort (note-le!)
   - **Region** : `East US` ou `Canada` (le plus proche)
4. Clique **Create new project**
5. Attends 2-3 minutes que le projet se crée

### Étape 4.2 : Exécuter les migrations SQL

1. Dans Supabase, va dans **SQL Editor** (menu de gauche)
2. Exécute chaque fichier SQL dans l'ordre :

**Migration 1 — Profils utilisateurs :**
Copie-colle le contenu de `supabase/migrations/001_auth_profiles.sql` et clique **Run**

**Migration 2 — Sauvegardes :**
Copie-colle le contenu de `supabase/migrations/002_backup_metadata.sql` et clique **Run**

**Migration 3 — Contacts d'urgence :**
Copie-colle le contenu de `supabase/migrations/003_ice_contacts.sql` et clique **Run**

**Migration 4 — Rapports d'arnaques :**
Copie-colle le contenu de `supabase/migrations/004_scam_reports.sql` et clique **Run**

### Étape 4.3 : Récupérer les clés API

1. Va dans **Settings** → **API** dans Supabase
2. Note ces deux valeurs :
   - **Project URL** : quelque chose comme `https://abcdef.supabase.co`
   - **anon public key** : une longue chaîne de caractères

### Étape 4.4 : Ajouter les clés dans l'application

Crée un fichier `local.properties` à la racine du projet `boomer-conx/` :

```properties
# SDK Android (Android Studio le génère souvent automatiquement)
sdk.dir=/Users/TON_NOM/Library/Android/sdk

# Supabase
SUPABASE_URL=https://TON-PROJET.supabase.co
SUPABASE_ANON_KEY=ta-clé-anon-ici
```

> **Important** : Ne partage JAMAIS ce fichier. Il est déjà dans le `.gitignore`.

### Étape 4.5 : Déployer les Edge Functions (optionnel)

Les Edge Functions gèrent la vérification d'URL et les signalements d'arnaques.

1. Installe le CLI Supabase :
```bash
# Mac
brew install supabase/tap/supabase

# Windows (PowerShell)
scoop install supabase

# Linux
curl -fsSL https://raw.githubusercontent.com/supabase/cli/main/install.sh | sh
```

2. Connecte-toi :
```bash
supabase login
```

3. Lie ton projet :
```bash
cd boomer-conx
supabase link --project-ref TON_PROJECT_REF
```

4. Déploie les fonctions :
```bash
supabase functions deploy check-url
supabase functions deploy scam-report
```

---

## 5. Compiler l'application (créer l'APK)

### Option A : Via Android Studio (recommandé pour débutants)

1. Dans Android Studio, va dans **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Attends que la compilation finisse (2-5 minutes)
3. Un lien **locate** apparaît en bas → clique dessus
4. L'APK est dans : `app/build/outputs/apk/debug/app-debug.apk`

### Option B : Via le terminal

```bash
cd boomer-conx

# Sur Mac/Linux
./gradlew assembleDebug

# Sur Windows
gradlew.bat assembleDebug
```

L'APK sera dans : `app/build/outputs/apk/debug/app-debug.apk`

### Vérifier que ça fonctionne

L'APK debug est parfait pour tester. Tu peux l'installer directement sur un téléphone. Mais pour une version « propre » à donner à ta mère, suis l'étape 6.

---

## 6. Signer l'APK pour la production

Android exige que chaque application soit signée numériquement. C'est comme un sceau qui garantit que l'app vient bien de toi.

### Étape 6.1 : Créer une clé de signature

Ouvre un terminal et tape :

```bash
keytool -genkey -v \
  -keystore boomerconx-release.keystore \
  -alias boomerconx \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Le programme te pose des questions :

| Question | Quoi répondre |
|----------|---------------|
| Keystore password | Choisis un mot de passe fort (NOTE-LE!) |
| First and last name | Ton nom |
| Organization | `BoomerConX` |
| City | Ta ville |
| State/Province | `Quebec` |
| Country code | `CA` |

> **CRITIQUE** : Garde le fichier `.keystore` et le mot de passe en sécurité. Si tu les perds, tu ne pourras plus jamais mettre à jour l'app sur le Play Store.

### Étape 6.2 : Configurer la signature dans Gradle

Crée un fichier `keystore.properties` à la racine du projet :

```properties
storeFile=../boomerconx-release.keystore
storePassword=ton-mot-de-passe
keyAlias=boomerconx
keyPassword=ton-mot-de-passe
```

> Ajoute ce fichier au `.gitignore` (c'est déjà fait si tu utilises le `.gitignore` du projet).

### Étape 6.3 : Compiler la version de production

```bash
# Mac/Linux
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

L'APK signé sera dans : `app/build/outputs/apk/release/app-release.apk`

---

## 7. Installer l'APK sur le téléphone de ta mère

### Méthode 1 : Par câble USB (la plus simple)

**Sur le téléphone de ta mère :**

1. Va dans **Paramètres** → **À propos du téléphone**
2. Tape 7 fois sur **Numéro de build** (ça active les options développeur)
3. Retourne dans **Paramètres** → **Options pour les développeurs**
4. Active **Débogage USB**
5. Branche le téléphone à ton ordinateur avec un câble USB
6. Sur le téléphone, accepte la connexion si demandé

**Sur ton ordinateur :**

```bash
# Vérifie que le téléphone est détecté
adb devices

# Installe l'APK
adb install app/build/outputs/apk/release/app-release.apk
```

Si `adb` n'est pas trouvé, le chemin complet est :
```bash
# Mac
~/Library/Android/sdk/platform-tools/adb install app-release.apk

# Windows
%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe install app-release.apk

# Linux
~/Android/Sdk/platform-tools/adb install app-release.apk
```

**Après l'installation :**
- Désactive les **Options pour les développeurs** (pour ne pas confondre ta mère)
- L'icône Boomer ConX apparaît sur l'écran d'accueil

### Méthode 2 : Par fichier (sans câble)

1. Envoie l'APK par courriel à ta mère, ou transfère-le via Google Drive
2. Sur le téléphone de ta mère :
   - Ouvre le fichier APK
   - Android va demander : **« Autoriser l'installation d'applications inconnues »**
   - Appuie sur **Paramètres** puis active l'option
   - Reviens et appuie sur **Installer**
3. L'app apparaît sur l'écran d'accueil

> **Note** : Après l'installation, désactive « Sources inconnues » pour plus de sécurité.

### Méthode 3 : Directement depuis Android Studio

1. Branche le téléphone par USB (avec le débogage USB activé)
2. Dans Android Studio, sélectionne le téléphone dans la barre d'outils en haut
3. Clique sur le bouton vert **Run** (▶)
4. L'application s'installe et se lance automatiquement

---

## 8. Publier sur le Google Play Store (optionnel)

Si tu veux que d'autres personnes puissent télécharger l'app.

### Étape 8.1 : Créer un compte développeur

1. Va sur https://play.google.com/console
2. Crée un compte développeur Google (frais unique de 25 $ USD)
3. Remplis les informations de ton profil

### Étape 8.2 : Préparer l'app pour la publication

Tu as besoin de :

| Élément | Spécifications |
|---------|---------------|
| Icône de l'app | 512 x 512 px, PNG |
| Captures d'écran | Au moins 2 captures, taille du téléphone |
| Description courte | Max 80 caractères |
| Description longue | Max 4000 caractères |
| Politique de confidentialité | URL vers ta politique |
| Catégorie | Outils |
| Classification du contenu | Remplis le questionnaire |

### Étape 8.3 : Créer un Android App Bundle (AAB)

Le Play Store préfère le format AAB plutôt qu'APK :

```bash
# Mac/Linux
./gradlew bundleRelease

# Windows
gradlew.bat bundleRelease
```

Le fichier sera dans : `app/build/outputs/bundle/release/app-release.aab`

### Étape 8.4 : Publier

1. Dans la Play Console, clique **Create app**
2. Remplis le nom : `Boomer ConX`
3. Va dans **Production** → **Create new release**
4. Téléverse le fichier `.aab`
5. Ajoute les notes de version
6. Soumets pour révision (peut prendre 1-7 jours)

---

## 9. Dépannage

### Erreurs courantes lors de la compilation

| Erreur | Solution |
|--------|----------|
| `SDK location not found` | Ouvre Android Studio, va dans SDK Manager et note le chemin. Ajoute-le dans `local.properties` : `sdk.dir=/chemin/vers/sdk` |
| `Could not resolve dependencies` | Vérifie ta connexion Internet. Essaie : `./gradlew --refresh-dependencies` |
| `Execution failed for task ':app:kspDebugKotlin'` | Vérifie que la version de KSP correspond à la version de Kotlin dans `libs.versions.toml` |
| `JAVA_HOME is not set` | Installe JDK 17 depuis Android Studio : Settings → Build Tools → Gradle → Gradle JDK |
| `minSdk 26 > device SDK` | Le téléphone de ta mère est trop vieux. Il faut Android 8.0 minimum (2017 ou plus récent) |

### Erreurs courantes lors de l'installation

| Erreur | Solution |
|--------|----------|
| `INSTALL_FAILED_USER_RESTRICTED` | Active « Sources inconnues » dans les paramètres du téléphone |
| `device not found` | Vérifie que le débogage USB est activé et que le câble fonctionne |
| `INSTALL_PARSE_FAILED_NO_CERTIFICATES` | L'APK n'est pas signé. Utilise `assembleDebug` ou signe l'APK (étape 6) |
| L'app plante au démarrage | Vérifie les clés Supabase dans `local.properties` |

### Vérifier la version Android du téléphone de ta mère

Sur son téléphone :
1. **Paramètres** → **À propos du téléphone** → **Version Android**
2. Il faut **8.0 ou plus** (Oreo)
3. Si c'est moins, l'application ne fonctionnera pas sur ce téléphone

### Besoin d'aide?

- Documentation Android : https://developer.android.com/docs
- Documentation Supabase : https://supabase.com/docs
- Ouvre une issue sur le dépôt GitHub du projet

---

## Résumé rapide

```
1. Installe Android Studio
2. Ouvre le projet boomer-conx/
3. Crée un projet Supabase (gratuit)
4. Ajoute les clés dans local.properties
5. Build → Build APK
6. Transfère l'APK sur le téléphone (USB, courriel, ou Drive)
7. Installe et c'est prêt!
```

Temps estimé pour tout faire la première fois : environ 1-2 heures (surtout l'attente des téléchargements).

---

*Boomer ConX — Simplifier la technologie pour ceux qu'on aime.*
