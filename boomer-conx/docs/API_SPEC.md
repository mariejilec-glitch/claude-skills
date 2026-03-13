# API Specification — Boomer ConX

## Supabase Auto-Generated REST (PostgREST)

| Operation | Method | Endpoint | Auth |
|-----------|--------|----------|------|
| Create profile | POST | `/rest/v1/profiles` | JWT |
| Get own profile | GET | `/rest/v1/profiles?id=eq.{uid}` | JWT + RLS |
| Update profile | PATCH | `/rest/v1/profiles?id=eq.{uid}` | JWT + RLS |
| Get own backups | GET | `/rest/v1/backup_manifests?user_id=eq.{uid}` | JWT + RLS |
| Create backup record | POST | `/rest/v1/backup_manifests` | JWT + RLS |
| Upload backup file | POST | `/storage/v1/object/backups/{uid}/{filename}` | JWT |
| Download backup file | GET | `/storage/v1/object/backups/{uid}/{filename}` | JWT |
| Sync scam patterns | GET | `/rest/v1/scam_patterns_global?active=eq.true` | JWT |
| Submit scam report | POST | `/rest/v1/scam_reports` | JWT |

## Edge Functions

### POST `/functions/v1/check-url`

Checks a URL against known phishing patterns.

**Request:**
```json
{
  "url": "https://suspicious-site.com/login"
}
```

**Response (unsafe):**
```json
{
  "safe": false,
  "reason": "Possible hameçonnage Desjardins",
  "severity": 9
}
```

**Response (safe):**
```json
{
  "safe": true,
  "reason": null,
  "severity": 0
}
```

### POST `/functions/v1/scam-report`

Submits a community scam report. Requires authentication.

**Request:**
```json
{
  "phone": "+15145551234",
  "content": "Vous avez gagné un prix...",
  "type": "SMS"
}
```

**Response:**
```json
{
  "id": "uuid",
  "status": "received"
}
```

## Authentication

Supabase Auth with email/password. JWT tokens used for all API calls.

**Sign up:**
```kotlin
supabaseClient.auth.signUpWith(Email) {
    email = "user@example.com"
    password = "secure-password"
}
```

**Sign in:**
```kotlin
supabaseClient.auth.signInWith(Email) {
    email = "user@example.com"
    password = "secure-password"
}
```

## Storage Buckets

| Bucket | Access | Structure |
|--------|--------|-----------|
| `backups` | Private (RLS) | `{user_id}/{type}/{timestamp}.enc` |

All backup files are encrypted client-side before upload (AES-256-GCM via Tink).
