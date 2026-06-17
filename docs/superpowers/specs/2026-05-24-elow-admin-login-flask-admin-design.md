# Elow Admin Login With Flask-Admin

Date: 2026-05-24

## Goal

Add account login capability to the Elow management backend by replacing the
current hand-written admin page with a Flask-based admin console.

The login requirement applies only to management surfaces:

- `/admin`
- `/api/admin/*`

Android-facing product APIs stay unauthenticated for this local MVP:

- `/api/catalog`
- `/api/users/{userId}/profile`
- `/api/users/{userId}/records`

## Chosen Framework

Use Flask-Admin for the management console, with Flask-Login for session-based
administrator authentication.

The existing backend is a dependency-free Python `http.server` service. That
kept the first local backend simple, but it now makes login, sessions, and
admin CRUD harder to maintain. Flask-Admin is a better fit for the next step
because it gives Elow a conventional admin foundation without changing the
Android API contract.

## Architecture

`backend/server.py` becomes a Flask app.

Core pieces:

- Flask app factory and routes for the current JSON API.
- SQLAlchemy models mapped to the existing SQLite tables.
- Flask-Admin model views for catalog items, users, and intake records.
- Flask-Login user loader and login/logout routes.
- Admin-only guard for `/admin` and `/api/admin/*`.

The SQLite database remains at `backend/data/elow-local.sqlite3` by default.
`ELOW_DB_PATH`, `ELOW_HOST`, and `ELOW_PORT` keep their current meanings.

## Data Model

Keep the existing tables:

- `catalog_items`
- `users`
- `intake_records`

Add one table:

- `admin_users`

`admin_users` fields:

- `id`
- `username`
- `password_hash`
- `created_at`
- `last_login_at`

On startup, the backend ensures the first admin exists from environment
configuration:

- `ELOW_ADMIN_USERNAME`
- `ELOW_ADMIN_PASSWORD`

For local development only, if those variables are missing, the backend creates
`admin / admin123` and documents that this must be changed before deployment.

## Routing Behavior

Public routes:

- `GET /health`
- `GET /api/catalog`
- `GET /api/users/<userId>/profile`
- `PUT /api/users/<userId>/profile`
- `GET /api/users/<userId>/records`
- `POST /api/users/<userId>/records`

Login routes:

- `GET /login`
- `POST /login`
- `GET /logout`

Protected admin routes:

- `/admin`
- `/admin/*`
- `GET /api/admin/overview`
- `PUT /api/admin/catalog/<itemType>`

Unauthenticated browser access to `/admin` redirects to `/login`.
Unauthenticated API access to `/api/admin/*` returns `401` JSON.

## Admin Console

Use Flask-Admin as the primary console and retire the custom static
`backend/admin/index.html` UI from the active route path.

Initial model views:

- Catalog Items: edit item name, family, serving, metrics, image URL, enabled.
- Users: inspect onboarding state and goals.
- Intake Records: inspect recent user records and calculated metrics.

The existing `/api/admin/overview` and `/api/admin/catalog/<itemType>` endpoints
remain available for compatibility and smoke testing, but they require login.

## Security Notes

This is a local MVP login layer, not a full production identity system.

Required safeguards in this pass:

- Passwords are stored only as Werkzeug password hashes.
- Flask session secret comes from `ELOW_SECRET_KEY`, with a development fallback.
- Admin-only routes check `current_user.is_authenticated`.
- Admin API returns `401` rather than leaking data.

Deferred production safeguards:

- Forced password rotation for the default local admin.
- CSRF protection for custom forms outside Flask-Admin.
- Rate limiting on login attempts.
- HTTPS and secure cookie settings in hosted environments.
- Role-based permissions if non-admin operator accounts are added.

## Validation

Smoke checks after implementation:

- Install backend dependencies from `backend/requirements.txt`.
- Start `python3 backend/server.py`.
- `GET /health` returns status `ok`.
- `GET /api/catalog` works without login.
- `GET /api/admin/overview` returns `401` without login.
- `GET /admin` redirects to `/login` without login.
- Login succeeds with the configured admin credentials.
- Authenticated admin session can load `/admin`.
- Authenticated admin session can call `/api/admin/overview`.
- Android debug build still compiles.
