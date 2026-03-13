# Data Models — Boomer ConX

## Room Entities (Local)

### CredentialEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Primary key |
| siteName | String | Website or app name |
| url | String? | Login URL |
| username | String | Username or email (plaintext) |
| encryptedPassword | ByteArray | AES-256-GCM encrypted password |
| notes | String? | User notes |
| category | String? | "banque", "courriel", "social", "autre" |
| createdAt | Long | Creation timestamp |
| updatedAt | Long | Last update timestamp |

### IceContactEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Primary key |
| name | String | Contact name |
| phone | String | Phone number |
| relationship | String | "conjoint", "enfant", "médecin" |
| priority | Int | 1 = primary contact |
| medicalNotes | String? | Allergies, conditions |
| bloodType | String? | Blood type |
| medicationList | String? | Medications (encrypted) |
| healthCardNumber | String? | RAMQ number (encrypted) |

### BackupMetadataEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Primary key |
| type | String | CONTACTS, PHOTOS, VIDEOS, FILES, APPS, LAUNCHER_LAYOUT |
| timestamp | Long | Backup timestamp |
| itemCount | Int | Number of items backed up |
| sizeBytes | Long | Encrypted backup size |
| cloudSynced | Boolean | Whether uploaded to Supabase |
| supabaseStoragePath | String? | Cloud storage path |

### LauncherItemEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Primary key |
| packageName | String | Android package name |
| label | String | Display name |
| page | Int | Home screen page |
| positionX | Int | Grid X position |
| positionY | Int | Grid Y position |
| isFolder | Boolean | Is a folder |
| folderName | String? | Folder label |

### ScamPatternEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String | Primary key |
| pattern | String | Regex or keyword pattern |
| type | String | SMS, URL, CALLER, EMAIL |
| severity | Int | 1-10 scale |
| descriptionFr | String | French description |
| descriptionEn | String? | English description |
| lastUpdated | Long | Last sync timestamp |

### RememberedNetworkEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Primary key |
| ssid | String | Network name |
| securityType | String | WPA2, WPA3, OPEN |
| notes | String? | User notes ("mot de passe sur le routeur") |
| encryptedPassword | ByteArray? | Encrypted WiFi password |

### RememberedBluetoothEntity
| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Primary key |
| deviceName | String | Bluetooth device name |
| deviceType | String | "écouteurs", "haut-parleur", "montre" |
| pairingInstructions | String? | Device-specific pairing steps |

## Supabase Tables (Cloud)

### profiles
Extends Supabase Auth. Auto-created on signup via trigger.

### backup_manifests
Cloud record of completed backups. Protected by RLS.

### scam_patterns_global
Admin-maintained scam patterns. Readable by all authenticated users.

### scam_reports
Community-submitted reports. Users can only read their own reports.

### ice_contacts_cloud
Cloud sync of ICE contacts. Protected by RLS.
