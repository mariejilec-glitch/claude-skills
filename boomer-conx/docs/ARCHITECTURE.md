# Architecture — Boomer ConX

## Vue d'ensemble

Architecture multi-module Android suivant les principes MVVM + Clean Architecture avec injection de dépendances via Hilt.

## Graphe de dépendances

```
app
├── feature-onboarding → core-ui, core-security, core-data, core-common
├── feature-home → core-ui, core-data, core-common
├── feature-backup → core-ui, core-data, core-security, core-common
├── feature-launcher → core-ui, core-data, core-common
├── feature-vault → core-ui, core-data, core-security, core-common
├── feature-ice → core-ui, core-data, core-security, core-common
├── feature-scam-shield → core-ui, core-data, core-common
└── feature-accessibility → core-ui, core-data, core-common

core-data → core-security, core-common
core-ui → core-common
core-security → core-common
```

## Couches

### Présentation (feature modules)
- **Screen** (Composable) — UI déclarative, observe les StateFlows
- **ViewModel** — Logique de présentation, expose UiState
- Communication via StateFlow unidirectionnel

### Domaine (use cases)
- Classes `*UseCase` dans les feature modules
- Orchestrent les opérations entre repositories
- Pas de dépendance Android directe

### Données (core-data)
- **Room Database** — Persistance locale, 7 entités
- **Supabase Client** — Sync cloud (auth, storage, postgrest)
- **DataStore** — Préférences utilisateur
- **Repositories** — Abstraction des sources de données

### Sécurité (core-security)
- **CryptoManager** — Tink AES-256-GCM via Android Keystore
- **BiometricHelper** — BiometricPrompt API wrapper
- **PinManager** — SHA-256 salé via DataStore
- **KeystoreHelper** — Gestion des clés Android Keystore

## Flux de données — Sauvegarde de contacts

```
User tap "Sauvegarder"
  → BackupViewModel.backupContacts()
    → BackupContactsUseCase()
      → ContactRepository.readAllContacts()  [ContentResolver]
      → ContactRepository.serializeToVcf()   [VCF format]
      → CryptoManager.encrypt()              [AES-256-GCM]
      → SupabaseSync.uploadBackup()          [Storage]
      → BackupMetadataDao.insert()           [Room]
    ← Result<BackupMetadataEntity>
  → UiState updated with success message
```

## Flux de données — Détection d'arnaque SMS

```
SMS reçu (BroadcastReceiver)
  → SmsAnalysisService.onReceive()
    → AnalyzeSmsUseCase(message)
      → ScamRepository.getPatternsByType("SMS")  [Room cache]
      → ScamRepository.analyzeSms(message, patterns)
        → Regex matching against Quebec-specific patterns
    ← ScamAnalysisResult
  → if isScam: ScamNotificationHelper.showScamWarning()
```

## Modèle de sécurité

```
┌─────────────────────────────┐
│     Android Keystore        │ ← Hardware-backed on supported devices
│  ┌───────────────────────┐  │
│  │   Master Key          │  │
│  └───────────┬───────────┘  │
└──────────────┼──────────────┘
               │ wraps
┌──────────────┼──────────────┐
│  ┌───────────┴───────────┐  │
│  │   Tink Keyset         │  │ ← AES-256-GCM
│  └───────────┬───────────┘  │
│              │ encrypts     │
│  ┌───────────┴───────────┐  │
│  │  Vault passwords      │  │
│  │  Health card numbers   │  │
│  │  Backup data          │  │
│  │  WiFi passwords       │  │
│  └───────────────────────┘  │
│         SharedPrefs          │
└──────────────────────────────┘
```

## Navigation

```
Onboarding ──→ Home
                ├──→ Backup Dashboard
                │     ├──→ Contact Backup
                │     ├──→ Photo Backup
                │     ├──→ App List
                │     ├──→ WiFi Wizard
                │     └──→ Bluetooth Guide
                ├──→ Vault
                │     ├──→ Add Credential
                │     └──→ Credential Detail
                ├──→ ICE
                │     └──→ Edit Contact
                ├──→ Scam Shield
                ├──→ Accessibility Settings
                └──→ Launcher (separate activity)
```

## Stratégie hors-ligne

- Room est la source de vérité pour toutes les données locales
- Supabase sync est best-effort (échoue silencieusement si hors-ligne)
- BackupMetadata.cloudSynced track le statut de sync
- Les patterns anti-arnaque sont cachés localement et mis à jour périodiquement
