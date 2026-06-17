# Elow Local Backend

Elow now runs as a local front/back separated system:

- Android client: `android/`
- Local Flask backend API: `backend/server.py`
- Admin console: `http://127.0.0.1:8080/admin`
- Database: `backend/data/elow-local.sqlite3`

The management backend uses Flask-Admin plus Flask-Login. Admin login protects
`/admin` and `/api/admin/*`; Android-facing `/api` routes remain public for the
local MVP.

## Install

```bash
cd /Users/summer/Desktop/ELOW
python3 -m pip install -r backend/requirements.txt
```

## Run

```bash
cd /Users/summer/Desktop/ELOW
python3 backend/server.py
```

Default local admin credentials:

```text
admin / admin123
```

For anything outside local development, override them before first startup:

```bash
export ELOW_SECRET_KEY="replace-with-a-long-random-secret"
export ELOW_ADMIN_USERNAME="admin"
export ELOW_ADMIN_PASSWORD="replace-this-password"
python3 backend/server.py
```

The Android emulator reaches the host backend through:

```text
http://10.0.2.2:8080/api
```

## API

- `GET /health`
- `GET /api/catalog`
- `GET /api/users/{userId}/profile`
- `PUT /api/users/{userId}/profile`
- `GET /api/users/{userId}/records`
- `POST /api/users/{userId}/records`
- `GET /api/admin/overview` (admin login required)
- `PUT /api/admin/catalog/{itemType}` (admin login required)

## Database

The local SQLite database stores:

- `catalog_items`: item image URL, serving size, sugar, alcohol, calories, and money estimate.
- `users`: local user profile, onboarding flag, and goals.
- `intake_records`: user intake records with server-calculated metrics.
- `admin_users`: management-console accounts with hashed passwords.

Android keeps only a generated local user id on device. Intake data and goals are stored in SQLite through the backend.

## Alibaba Cloud Migration Path

The current backend intentionally keeps API contracts separate from SQLite. To migrate:

1. Replace SQLite with Alibaba Cloud RDS for MySQL or PostgreSQL.
2. Move item image URLs to OSS-backed paths.
3. Put the Flask service behind ECS, SAE, or Function Compute plus API Gateway.
4. Replace the local admin bootstrap password with managed secrets and HTTPS-only cookies.
5. Add Android user authentication and map `local-*` user ids to authenticated accounts.
6. Keep Android pointed at the same `/api` contract and change only the base URL.
