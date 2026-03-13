# Boomer ConX

**Application Android pour les aînés du Québec** — Assistant de transition téléphonique avec coffre-fort de mots de passe, protection anti-arnaque, informations d'urgence ICE et fonctionnalités d'accessibilité avancées.

## Fonctionnalités

### Sauvegarde et restauration
- **Contacts** — Sauvegarde chiffrée (AES-256-GCM) avec synchronisation cloud
- **Photos et vidéos** — Sauvegarde sélective via MediaStore
- **Liste d'applications** — Enumération et liens Play Store pour réinstallation
- **Réseaux WiFi** — Assistant de reconnexion guidé avec instructions visuelles
- **Appareils Bluetooth** — Guide de ré-appairage par type d'appareil

### Coffre-fort de mots de passe
- Chiffrement Tink AES-256-GCM via Android Keystore
- Authentification biométrique ou NIP 6 chiffres
- Service de remplissage automatique (Autofill)
- Générateur de mots de passe sécurisés
- Catégorisation (Banque, Courriel, Réseaux sociaux)

### Urgence (ICE)
- Contacts d'urgence visibles sur l'écran de verrouillage
- Informations médicales (groupe sanguin, allergies, médicaments)
- Numéro de carte RAMQ (chiffré)
- Appel rapide en un toucher

### Protection arnaque (Scam Shield)
- Analyse SMS entrante avec patterns québécois
- Vérification d'URL via Google Safe Browsing
- Filtrage d'appels (CallScreeningService)
- Signalement communautaire
- Alertes visuelles claires

### Accessibilité
- Taille de police ajustable (0.85x à 2.0x)
- Mode contraste élevé
- Mouvement réduit
- Cibles tactiles minimum 72dp
- Descriptions de contenu sur tous les éléments interactifs
- Localisation Français québécois prioritaire

### Lanceur simplifié
- Écran d'accueil avec grandes icônes
- Arrangement personnalisable
- Sauvegarde/restauration du layout

## Architecture

Multi-module Android avec architecture MVVM + Clean Architecture :

```
app/                    → Application entry, navigation
core/
  core-common/          → Result, dispatchers, extensions
  core-security/        → Tink encryption, biometric, PIN
  core-ui/              → Theme, components (BigButton, SeniorCard)
  core-data/            → Room DB, Supabase client, DataStore
feature/
  feature-onboarding/   → Language, PIN setup, welcome
  feature-home/         → Dashboard with feature cards
  feature-backup/       → Contact, photo, app backup/restore
  feature-vault/        → Password vault, autofill service
  feature-ice/          → Emergency contacts, lock screen
  feature-scam-shield/  → SMS, call, URL protection
  feature-launcher/     → Simplified home screen
  feature-accessibility/→ Font scale, contrast, motion
supabase/               → Migrations, edge functions
```

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Hilt DI |
| Base de données | Room |
| Cloud | Supabase (Auth, Storage, PostgREST) |
| Chiffrement | Google Tink (AES-256-GCM) |
| Auth biométrique | BiometricPrompt API |
| Tâches de fond | WorkManager |
| Navigation | Navigation Compose |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

## Développement

```bash
# Clone
git clone https://github.com/your-org/boomer-conx.git

# Build
./gradlew assembleDebug

# Tests
./gradlew test

# Supabase local
supabase start
supabase db reset
```

## Licence

MIT
