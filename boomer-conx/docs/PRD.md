# Boomer ConX — Product Requirements Document

## Vision

Boomer ConX est une application Android conçue pour les aînés du Québec qui simplifie la transition vers un nouveau téléphone et offre des outils essentiels de sécurité numérique dans un format accessible.

## Personas

### Monique, 72 ans, Saint-Jérôme
- Vient d'acheter un nouveau Samsung Galaxy
- A perdu ses contacts lors de la dernière transition
- Ne se souvient pas de ses mots de passe
- A déjà reçu des SMS frauduleux de "Revenu Québec"
- Parle français, comprend peu l'anglais technique

### Robert, 68 ans, Gatineau
- Bilingue français/anglais
- Veut protéger ses informations bancaires
- Son épouse a des conditions médicales — veut ICE visible
- Reçoit fréquemment des appels de numéros inconnus

## User Stories

### Sauvegarde
- En tant qu'aîné, je veux sauvegarder mes contacts d'un seul toucher pour ne pas les perdre
- En tant qu'aîné, je veux voir une confirmation claire quand la sauvegarde est terminée
- En tant qu'aîné, je veux restaurer mes contacts sur mon nouveau téléphone facilement

### Coffre-fort
- En tant qu'aîné, je veux enregistrer mes mots de passe de façon sécurisée
- En tant qu'aîné, je veux que mes mots de passe se remplissent automatiquement
- En tant qu'aîné, je veux voir mes mots de passe en gros caractères

### ICE
- En tant qu'aîné, je veux que mes contacts d'urgence soient visibles même si mon téléphone est verrouillé
- En tant qu'aîné, je veux pouvoir appeler un contact ICE en un seul toucher

### Protection arnaque
- En tant qu'aîné, je veux être alerté quand un SMS semble frauduleux
- En tant qu'aîné, je veux que les appels suspects soient filtrés automatiquement
- En tant qu'aîné, je veux comprendre POURQUOI un message est considéré comme une arnaque

### Accessibilité
- En tant qu'aîné malvoyant, je veux agrandir le texte jusqu'à 2x la taille normale
- En tant qu'aîné, je veux des boutons assez grands pour être touchés facilement
- En tant qu'aîné, je veux utiliser l'application en français québécois

## Exigences non fonctionnelles

- **Performance** : Temps de démarrage < 3 secondes
- **Sécurité** : Chiffrement AES-256-GCM pour toutes les données sensibles
- **Accessibilité** : WCAG 2.1 AA minimum, cibles tactiles 72dp
- **Hors-ligne** : Fonctionnalités critiques disponibles sans internet
- **Taille** : APK < 30MB
- **Compatibilité** : Android 8.0+ (API 26+)

## Priorités (MoSCoW)

### Must Have
- Sauvegarde/restauration de contacts
- Coffre-fort de mots de passe avec biométrie
- ICE sur écran de verrouillage
- Protection SMS anti-arnaque
- Localisation FR-CA
- Accessibilité (font scale, high contrast)

### Should Have
- Sauvegarde photos
- Service autofill
- Filtrage d'appels
- Lanceur simplifié
- Synchronisation cloud

### Could Have
- Assistant WiFi reconnexion
- Guide Bluetooth ré-appairage
- Export/import du coffre-fort
- Thème sombre

### Won't Have (v1)
- Transfert automatique d'applications
- Extraction de mots de passe d'autres apps
- Restauration de paramètres système
- Support iOS
