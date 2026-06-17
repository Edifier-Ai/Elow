# Elow Cross-Platform Data Contract

Date: 2026-05-24

## Purpose

This contract lets Android and future iOS share product logic without forcing a
cross-platform UI framework.

## Items

The first version supports:

- `COLA`
- `MILK_TEA`
- `BEER`
- `WINE`

## Record

Each intake record has:

- `id`: stable string id.
- `itemType`: one of the first-version items.
- `amountFraction`: decimal from `0.0` to `1.0`.
- `timestampEpochMillis`: local creation time in milliseconds.
- `metrics.sugarGrams`
- `metrics.alcoholGrams`
- `metrics.calories`
- `metrics.money`
- `note`: optional string, empty by default.

## Goals

First-version goals:

- `weeklySweetDrinkLimit`
- `weeklyAlcoholOccasionLimit`
- `dailySugarGramTarget`

User-defined goals are primary. Reference advice is optional and appears only
when the user asks for it.

## Metric Estimate Rules

Android first-version defaults:

- Cola full serving: 500 ml, 35 g sugar, 0 g alcohol, 140 kcal, 1.50 money units.
- Milk tea full serving: 500 ml, 45 g sugar, 0 g alcohol, 280 kcal, 5.50 money units.
- Beer full serving: 355 ml, 0 g sugar, 14 g alcohol, 153 kcal, 4.00 money units.
- Wine full serving: 150 ml, 0 g sugar, 14 g alcohol, 125 kcal, 6.00 money units.

Estimate formula:

`metric = fullServingMetric * clamp(amountFraction, 0.0, 1.0)`

## Backend API Contract

Local development uses:

- Host API: `http://127.0.0.1:8080/api`
- Android emulator API: `http://10.0.2.2:8080/api`

Endpoints:

- `GET /api/catalog`: returns configurable catalog items.
- `GET /api/users/{userId}/profile`: returns onboarding state and goals.
- `PUT /api/users/{userId}/profile`: updates onboarding state and goals.
- `GET /api/users/{userId}/records`: returns records for the user.
- `POST /api/users/{userId}/records`: creates a record and calculates metrics from the current catalog.

Admin endpoints:

- `GET /api/admin/overview`
- `PUT /api/admin/catalog/{itemType}`

SQLite is the local backing database. Future Alibaba Cloud migration should keep
this API stable while replacing SQLite with cloud database storage.

## Language Guardrails

Avoid:

- Failed
- Broke the streak
- Over limit
- Bad day

Prefer:

- Close to your target
- A little high today
- You recorded clearly
- Tomorrow can be lighter
- This week is lower than last week
