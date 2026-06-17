# Elow Local Backend Architecture

Date: 2026-05-24

## Current Gap Closed

The first Android MVP stored records and goals directly on device. That was enough for the toy-collectible flow, but it did not satisfy the product requirement for configurable food data, a management backend, or database-backed user records.

This pass changes the system to:

- Android as a client.
- Local Flask backend as the source of truth.
- SQLite as the real local database.
- Flask-Admin console for catalog and record inspection.
- Flask-Login session authentication for management routes.

## Data Ownership

Backend owns:

- Catalog item configuration.
- Serving amount.
- Sugar grams.
- Alcohol grams.
- Calories.
- Money estimate.
- Image URL.
- User onboarding state.
- User goals.
- Intake records.
- Admin login accounts for the management console.

Android owns:

- Generated local user id.
- Presentation state.
- Current unsaved Add-screen state.

## Local URLs

Host machine:

- API: `http://127.0.0.1:8080/api`
- Admin: `http://127.0.0.1:8080/admin`
- Login: `http://127.0.0.1:8080/login`

Android emulator:

- API: `http://10.0.2.2:8080/api`

## Migration Notes

Keep the Android app coupled to the API contract, not SQLite. For Alibaba Cloud, keep the same endpoint shape and move persistence behind the backend:

- SQLite tables map directly to RDS tables.
- `imageUrl` values should move to OSS.
- User ids should move from local generated ids to authenticated account ids.
- Admin console can remain same-origin with the API through Flask-Admin.
- Admin credentials should move from environment variables to managed secrets.
