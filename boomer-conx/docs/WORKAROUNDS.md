# Workarounds — Fonctionnalités infaisables

Certaines fonctionnalités ne sont pas techniquement réalisables sur Android en raison de restrictions du système. Voici les contournements implémentés.

## Mots de passe WiFi

### Problème
Android ne permet pas aux applications tierces d'accéder aux mots de passe WiFi enregistrés ni de configurer automatiquement de nouveaux réseaux.

### Contournement
1. **Pré-transfert** : L'application scanne les réseaux disponibles via `WifiManager.getScanResults()` et permet à l'utilisateur d'annoter chaque réseau (ex: "maison", "chalet")
2. **L'utilisateur peut optionnellement** enregistrer le mot de passe manuellement (chiffré dans `RememberedNetworkEntity`)
3. **Post-transfert** : L'assistant affiche la liste des réseaux mémorisés avec :
   - Instructions étape par étape pour accéder aux paramètres WiFi Android
   - Bouton pour copier le mot de passe dans le presse-papier
   - Lien profond vers les paramètres WiFi du système

## Appairage Bluetooth

### Problème
Les associations Bluetooth ne peuvent pas être transférées entre appareils. Chaque appareil doit être ré-appairé manuellement.

### Contournement
1. **Pré-transfert** : Lecture des appareils appairés via `BluetoothAdapter.getBondedDevices()`
2. **Stockage** du nom et type de chaque appareil dans `RememberedBluetoothEntity`
3. **Post-transfert** : Guide de ré-appairage personnalisé par type d'appareil :
   - Écouteurs : "Maintenez le bouton d'alimentation 3 secondes..."
   - Haut-parleur : "Appuyez sur le bouton Bluetooth..."
   - Montre : "Ouvrez l'application de votre montre..."

## Réinstallation automatique d'applications

### Problème
Android ne permet pas l'installation silencieuse d'applications sans consentement explicite pour chaque app.

### Contournement
1. **Pré-transfert** : Enumération via `PackageManager.getInstalledApplications()`
2. **Stockage** de la liste avec noms et packages
3. **Post-transfert** : Liens profonds vers le Play Store : `market://details?id={packageName}`
4. **Mode batch** : Ouvre les liens en séquence pour minimiser les touches nécessaires

## Paramètres système

### Problème
Les préférences système (taille de police, luminosité, sonnerie) ne peuvent pas être restaurées programmatiquement.

### Contournement
1. **Pré-transfert** : Capture des préférences utilisateur dans l'app (taille de police, contraste, etc.)
2. **Post-transfert** : Assistant guidé qui :
   - Rappelle les paramètres précédents
   - Fournit des liens profonds vers chaque écran de paramètres
   - Montre des captures d'écran des étapes à suivre

## Extraction de mots de passe

### Problème
Les mots de passe stockés dans les navigateurs et applications ne sont pas accessibles aux applications tierces (sandbox Android).

### Contournement
1. **Coffre-fort intégré** avec saisie manuelle
2. **Service Autofill** qui capture les nouveaux mots de passe lors de la saisie
3. **Interface simple** avec grandes zones de texte pour faciliter la saisie
4. **Générateur de mots de passe** pour les nouveaux comptes
