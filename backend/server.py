#!/usr/bin/env python3
"""Local Elow backend.

The service exposes the Android JSON API and a Flask-Admin management console.
Only the admin console and admin API require login; Android-facing MVP routes
remain unauthenticated.
"""

from __future__ import annotations

import os
import time
import uuid
from http import HTTPStatus
from pathlib import Path
from typing import Any

from flask import Flask, jsonify, redirect, render_template_string, request, url_for
from flask_admin import Admin, AdminIndexView
from flask_admin.contrib.sqla import ModelView
from flask_admin.menu import MenuLink
from flask_login import LoginManager, UserMixin, current_user, login_required, login_user, logout_user
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text
from sqlalchemy.exc import IntegrityError
from werkzeug.security import check_password_hash, generate_password_hash


ROOT = Path(__file__).resolve().parent
DB_PATH = Path(os.environ.get("ELOW_DB_PATH", ROOT / "data" / "elow-local.sqlite3"))
HOST = os.environ.get("ELOW_HOST", "127.0.0.1")
PORT = int(os.environ.get("ELOW_PORT", "8080"))
DEFAULT_ADMIN_USERNAME = "admin"
DEFAULT_ADMIN_PASSWORD = "admin123"

db = SQLAlchemy()
login_manager = LoginManager()

DEFAULT_CATALOG = [
    {
        "type": "COLA",
        "display_name": "Cola",
        "family": "SUGAR",
        "full_serving_label": "1 bottle",
        "full_serving_ml": 500,
        "full_serving_sugar_grams": 35.0,
        "full_serving_alcohol_grams": 0.0,
        "full_serving_calories": 140.0,
        "full_serving_money": 1.50,
        "image_url": "/admin/assets/cola.png",
        "enabled": 1,
    },
    {
        "type": "MILK_TEA",
        "display_name": "Milk Tea",
        "family": "SUGAR",
        "full_serving_label": "1 cup",
        "full_serving_ml": 500,
        "full_serving_sugar_grams": 45.0,
        "full_serving_alcohol_grams": 0.0,
        "full_serving_calories": 280.0,
        "full_serving_money": 5.50,
        "image_url": "/admin/assets/milk-tea.png",
        "enabled": 1,
    },
    {
        "type": "BEER",
        "display_name": "Beer",
        "family": "ALCOHOL",
        "full_serving_label": "1 can",
        "full_serving_ml": 355,
        "full_serving_sugar_grams": 0.0,
        "full_serving_alcohol_grams": 14.0,
        "full_serving_calories": 153.0,
        "full_serving_money": 4.00,
        "image_url": "/admin/assets/beer.png",
        "enabled": 1,
    },
    {
        "type": "WINE",
        "display_name": "Wine",
        "family": "ALCOHOL",
        "full_serving_label": "1 glass",
        "full_serving_ml": 150,
        "full_serving_sugar_grams": 0.0,
        "full_serving_alcohol_grams": 14.0,
        "full_serving_calories": 125.0,
        "full_serving_money": 6.00,
        "image_url": "/admin/assets/wine.png",
        "enabled": 1,
    },
]

LOGIN_TEMPLATE = """
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Elow Admin Login</title>
    <style>
      :root {
        color-scheme: light;
        --bg: #f7f8f7;
        --surface: #ffffff;
        --ink: #20242a;
        --muted: #6c737c;
        --line: #e5e7eb;
        --blue: #2678e8;
        --blue-dark: #1761c4;
        --red: #d83a3a;
      }

      * { box-sizing: border-box; }

      body {
        align-items: center;
        background: var(--bg);
        color: var(--ink);
        display: flex;
        font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
        justify-content: center;
        margin: 0;
        min-height: 100vh;
        padding: 24px;
      }

      main {
        background: var(--surface);
        border: 1px solid var(--line);
        border-radius: 8px;
        box-shadow: 0 18px 46px rgba(20, 24, 28, 0.08);
        max-width: 420px;
        padding: 28px;
        width: 100%;
      }

      h1 { font-size: 25px; margin: 0 0 6px; }
      p { color: var(--muted); margin: 0 0 22px; }
      label { display: grid; font-weight: 700; gap: 7px; margin-bottom: 14px; }

      input {
        border: 1px solid var(--line);
        border-radius: 7px;
        color: var(--ink);
        font: inherit;
        min-height: 40px;
        padding: 8px 10px;
        width: 100%;
      }

      button {
        background: var(--blue);
        border: 0;
        border-radius: 8px;
        color: white;
        cursor: pointer;
        font: inherit;
        font-weight: 800;
        min-height: 42px;
        padding: 0 16px;
        width: 100%;
      }

      button:hover { background: var(--blue-dark); }
      .error { color: var(--red); font-weight: 700; margin: 0 0 14px; }
    </style>
  </head>
  <body>
    <main>
      <h1>Elow Admin</h1>
      <p>Sign in to manage catalog, users, and records.</p>
      {% if error %}<div class="error">{{ error }}</div>{% endif %}
      <form method="post">
        <label>
          Username
          <input name="username" autocomplete="username" required autofocus />
        </label>
        <label>
          Password
          <input name="password" type="password" autocomplete="current-password" required />
        </label>
        <input name="next" type="hidden" value="{{ next_url }}" />
        <button type="submit">Sign in</button>
      </form>
    </main>
  </body>
</html>
"""


class CatalogItem(db.Model):
    __tablename__ = "catalog_items"

    type = db.Column(db.String, primary_key=True)
    display_name = db.Column(db.String, nullable=False)
    family = db.Column(db.String, nullable=False)
    full_serving_label = db.Column(db.String, nullable=False)
    full_serving_ml = db.Column(db.Integer, nullable=False)
    full_serving_sugar_grams = db.Column(db.Float, nullable=False)
    full_serving_alcohol_grams = db.Column(db.Float, nullable=False)
    full_serving_calories = db.Column(db.Float, nullable=False)
    full_serving_money = db.Column(db.Float, nullable=False)
    image_url = db.Column(db.String, nullable=False, default="")
    enabled = db.Column(db.Integer, nullable=False, default=1)
    updated_at = db.Column(db.Integer, nullable=False)

    def __str__(self) -> str:
        return f"{self.type} - {self.display_name}"


class AppUser(db.Model):
    __tablename__ = "users"

    id = db.Column(db.String, primary_key=True)
    onboarding_complete = db.Column(db.Integer, nullable=False, default=0)
    weekly_sweet_drink_limit = db.Column(db.Integer, nullable=False, default=7)
    weekly_alcohol_occasion_limit = db.Column(db.Integer, nullable=False, default=3)
    daily_sugar_gram_target = db.Column(db.Integer, nullable=False, default=60)
    created_at = db.Column(db.Integer, nullable=False)
    updated_at = db.Column(db.Integer, nullable=False)

    def __str__(self) -> str:
        return self.id


class IntakeRecord(db.Model):
    __tablename__ = "intake_records"

    id = db.Column(db.String, primary_key=True)
    user_id = db.Column(db.String, db.ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    item_type = db.Column(db.String, db.ForeignKey("catalog_items.type"), nullable=False)
    amount_fraction = db.Column(db.Float, nullable=False)
    timestamp_epoch_millis = db.Column(db.Integer, nullable=False)
    sugar_grams = db.Column(db.Float, nullable=False)
    alcohol_grams = db.Column(db.Float, nullable=False)
    calories = db.Column(db.Float, nullable=False)
    money = db.Column(db.Float, nullable=False)
    note = db.Column(db.String, nullable=False, default="")
    created_at = db.Column(db.Integer, nullable=False)

    user = db.relationship("AppUser", backref=db.backref("records", lazy=True, cascade="all, delete-orphan"))
    item = db.relationship("CatalogItem")

    def __str__(self) -> str:
        return f"{self.item_type} {self.amount_fraction:.2f}"


class AdminUser(UserMixin, db.Model):
    __tablename__ = "admin_users"

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String, nullable=False, unique=True)
    password_hash = db.Column(db.String, nullable=False)
    created_at = db.Column(db.Integer, nullable=False)
    last_login_at = db.Column(db.Integer)

    def __str__(self) -> str:
        return self.username


def now_millis() -> int:
    return int(time.time() * 1000)


def catalog_item_json(item: CatalogItem) -> dict[str, Any]:
    return {
        "type": item.type,
        "displayName": item.display_name,
        "family": item.family,
        "fullServingLabel": item.full_serving_label,
        "fullServingMl": item.full_serving_ml,
        "fullServingSugarGrams": item.full_serving_sugar_grams,
        "fullServingAlcoholGrams": item.full_serving_alcohol_grams,
        "fullServingCalories": item.full_serving_calories,
        "fullServingMoney": item.full_serving_money,
        "imageUrl": item.image_url,
        "enabled": bool(item.enabled),
    }


def profile_json(user: AppUser) -> dict[str, Any]:
    return {
        "userId": user.id,
        "onboardingComplete": bool(user.onboarding_complete),
        "goals": {
            "weeklySweetDrinkLimit": user.weekly_sweet_drink_limit,
            "weeklyAlcoholOccasionLimit": user.weekly_alcohol_occasion_limit,
            "dailySugarGramTarget": user.daily_sugar_gram_target,
        },
    }


def record_json(record: IntakeRecord) -> dict[str, Any]:
    return {
        "id": record.id,
        "itemType": record.item_type,
        "amountFraction": record.amount_fraction,
        "timestampEpochMillis": record.timestamp_epoch_millis,
        "metrics": {
            "sugarGrams": record.sugar_grams,
            "alcoholGrams": record.alcohol_grams,
            "calories": record.calories,
            "money": record.money,
        },
        "note": record.note,
    }


def metrics_from_catalog(item: CatalogItem, amount_fraction: float) -> dict[str, float]:
    fraction = max(0.0, min(1.0, amount_fraction))
    return {
        "sugarGrams": item.full_serving_sugar_grams * fraction,
        "alcoholGrams": item.full_serving_alcohol_grams * fraction,
        "calories": item.full_serving_calories * fraction,
        "money": item.full_serving_money * fraction,
    }


def ensure_user(user_id: str) -> AppUser:
    user = db.session.get(AppUser, user_id)
    if user is None:
        timestamp = now_millis()
        user = AppUser(
            id=user_id,
            onboarding_complete=0,
            weekly_sweet_drink_limit=7,
            weekly_alcohol_occasion_limit=3,
            daily_sugar_gram_target=60,
            created_at=timestamp,
            updated_at=timestamp,
        )
        db.session.add(user)
        db.session.commit()
    return user


def bool_from_json(value: Any) -> bool:
    if isinstance(value, bool):
        return value
    return bool(value)


def read_json_object() -> dict[str, Any]:
    value = request.get_json(silent=True)
    if value is None:
        return {}
    if not isinstance(value, dict):
        raise ValueError("JSON body must be an object")
    return value


def safe_next_url(value: str | None) -> str:
    if value and value.startswith("/") and not value.startswith("//"):
        return value
    return url_for("admin.index")


@login_manager.user_loader
def load_admin_user(user_id: str) -> AdminUser | None:
    if not user_id.isdigit():
        return None
    return db.session.get(AdminUser, int(user_id))


@login_manager.unauthorized_handler
def unauthorized() -> Any:
    if request.path.startswith("/api/admin"):
        return jsonify({"error": "Authentication required"}), HTTPStatus.UNAUTHORIZED
    return redirect(url_for("login", next=request.url))


class SecureAdminIndexView(AdminIndexView):
    def is_accessible(self) -> bool:
        return current_user.is_authenticated

    def inaccessible_callback(self, name: str, **kwargs: Any) -> Any:
        return redirect(url_for("login", next=request.url))


class SecureModelView(ModelView):
    def is_accessible(self) -> bool:
        return current_user.is_authenticated

    def inaccessible_callback(self, name: str, **kwargs: Any) -> Any:
        return redirect(url_for("login", next=request.url))


class CatalogItemAdmin(SecureModelView):
    can_create = False
    can_delete = False
    column_list = (
        "type",
        "display_name",
        "family",
        "full_serving_label",
        "full_serving_ml",
        "full_serving_sugar_grams",
        "full_serving_alcohol_grams",
        "full_serving_calories",
        "full_serving_money",
        "image_url",
        "enabled",
        "updated_at",
    )
    form_columns = (
        "display_name",
        "family",
        "full_serving_label",
        "full_serving_ml",
        "full_serving_sugar_grams",
        "full_serving_alcohol_grams",
        "full_serving_calories",
        "full_serving_money",
        "image_url",
        "enabled",
    )
    form_choices = {"family": [("SUGAR", "SUGAR"), ("ALCOHOL", "ALCOHOL")]}
    column_labels = {
        "display_name": "Display Name",
        "full_serving_label": "Serving Label",
        "full_serving_ml": "Serving ml",
        "full_serving_sugar_grams": "Sugar g",
        "full_serving_alcohol_grams": "Alcohol g",
        "full_serving_calories": "Calories",
        "full_serving_money": "Money",
        "image_url": "Image URL",
        "updated_at": "Updated At",
    }

    def on_model_change(self, form: Any, model: CatalogItem, is_created: bool) -> None:
        model.family = str(model.family).upper()
        model.enabled = 1 if model.enabled else 0
        model.updated_at = now_millis()


class AppUserAdmin(SecureModelView):
    can_create = False
    can_edit = False
    can_delete = False
    column_list = (
        "id",
        "onboarding_complete",
        "weekly_sweet_drink_limit",
        "weekly_alcohol_occasion_limit",
        "daily_sugar_gram_target",
        "created_at",
        "updated_at",
    )
    column_searchable_list = ("id",)
    column_labels = {
        "onboarding_complete": "Onboarding Complete",
        "weekly_sweet_drink_limit": "Sweet Drinks / Week",
        "weekly_alcohol_occasion_limit": "Alcohol Occasions / Week",
        "daily_sugar_gram_target": "Sugar g / Day",
        "created_at": "Created At",
        "updated_at": "Updated At",
    }


class IntakeRecordAdmin(SecureModelView):
    can_create = False
    can_edit = False
    can_delete = False
    column_list = (
        "id",
        "user_id",
        "item_type",
        "amount_fraction",
        "timestamp_epoch_millis",
        "sugar_grams",
        "alcohol_grams",
        "calories",
        "money",
        "note",
        "created_at",
    )
    column_searchable_list = ("id", "user_id", "item_type")
    column_filters = ("item_type", "user_id")
    column_labels = {
        "user_id": "User ID",
        "item_type": "Item Type",
        "amount_fraction": "Amount Fraction",
        "timestamp_epoch_millis": "Timestamp",
        "sugar_grams": "Sugar g",
        "alcohol_grams": "Alcohol g",
        "created_at": "Created At",
    }


def create_app() -> Flask:
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    app = Flask(__name__)
    app.config["SECRET_KEY"] = os.environ.get("ELOW_SECRET_KEY", "elow-dev-secret-change-me")
    app.config["SQLALCHEMY_DATABASE_URI"] = f"sqlite:///{DB_PATH}"
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

    db.init_app(app)
    login_manager.init_app(app)
    login_manager.login_view = "login"

    admin = Admin(
        app,
        name="Elow Admin",
        index_view=SecureAdminIndexView(url="/admin"),
        template_mode="bootstrap4",
    )
    admin.add_view(CatalogItemAdmin(CatalogItem, db.session, name="Catalog Items", endpoint="catalog_items"))
    admin.add_view(AppUserAdmin(AppUser, db.session, name="Users", endpoint="app_users"))
    admin.add_view(IntakeRecordAdmin(IntakeRecord, db.session, name="Intake Records", endpoint="intake_records"))
    admin.add_link(MenuLink(name="Logout", category="", url="/logout"))

    register_routes(app)
    register_error_handlers(app)

    with app.app_context():
        init_db()

    return app


def init_db() -> None:
    db.create_all()
    if db.session.query(CatalogItem).count() == 0:
        timestamp = now_millis()
        db.session.add_all(CatalogItem(**item, updated_at=timestamp) for item in DEFAULT_CATALOG)
        db.session.commit()
    ensure_admin_user()


def ensure_admin_user() -> None:
    username = os.environ.get("ELOW_ADMIN_USERNAME", DEFAULT_ADMIN_USERNAME)
    password = os.environ.get("ELOW_ADMIN_PASSWORD", DEFAULT_ADMIN_PASSWORD)
    existing = AdminUser.query.filter_by(username=username).first()
    if existing is not None:
        return
    timestamp = now_millis()
    db.session.add(
        AdminUser(
            username=username,
            password_hash=generate_password_hash(password, method="pbkdf2:sha256"),
            created_at=timestamp,
            last_login_at=None,
        )
    )
    db.session.commit()


def register_routes(app: Flask) -> None:
    @app.after_request
    def add_cors_headers(response: Any) -> Any:
        if request.path == "/health" or request.path.startswith("/api/"):
            response.headers["Access-Control-Allow-Origin"] = "*"
            response.headers["Access-Control-Allow-Methods"] = "GET, POST, PUT, OPTIONS"
            response.headers["Access-Control-Allow-Headers"] = "Content-Type"
        return response

    @app.route("/", methods=["GET"])
    def root() -> Any:
        return redirect(url_for("admin.index"))

    @app.route("/favicon.ico", methods=["GET"])
    def favicon() -> Any:
        return "", HTTPStatus.NO_CONTENT

    @app.route("/health", methods=["GET"])
    def health() -> Any:
        return jsonify({"status": "ok", "dbPath": str(DB_PATH)})

    @app.route("/login", methods=["GET", "POST"])
    def login() -> Any:
        next_url = safe_next_url(request.values.get("next"))
        if current_user.is_authenticated:
            return redirect(next_url)

        error = ""
        if request.method == "POST":
            username = request.form.get("username", "")
            password = request.form.get("password", "")
            admin_user = AdminUser.query.filter_by(username=username).first()
            if admin_user is not None and check_password_hash(admin_user.password_hash, password):
                login_user(admin_user)
                admin_user.last_login_at = now_millis()
                db.session.commit()
                return redirect(next_url)
            error = "Invalid username or password."

        status = HTTPStatus.UNAUTHORIZED if error else HTTPStatus.OK
        return render_template_string(LOGIN_TEMPLATE, error=error, next_url=next_url), status

    @app.route("/logout", methods=["GET"])
    @login_required
    def logout() -> Any:
        logout_user()
        return redirect(url_for("login"))

    @app.route("/api/catalog", methods=["GET"])
    def get_catalog() -> Any:
        items = (
            CatalogItem.query.filter_by(enabled=1)
            .order_by(text("rowid"))
            .all()
        )
        return jsonify({"items": [catalog_item_json(item) for item in items]})

    @app.route("/api/users/<user_id>/profile", methods=["GET", "PUT"])
    def user_profile(user_id: str) -> Any:
        user = ensure_user(user_id)
        if request.method == "PUT":
            body = read_json_object()
            goals = body.get("goals") or {}
            if not isinstance(goals, dict):
                raise ValueError("goals must be an object")
            user.onboarding_complete = 1 if bool_from_json(body.get("onboardingComplete", user.onboarding_complete)) else 0
            user.weekly_sweet_drink_limit = int(goals.get("weeklySweetDrinkLimit", user.weekly_sweet_drink_limit))
            user.weekly_alcohol_occasion_limit = int(
                goals.get("weeklyAlcoholOccasionLimit", user.weekly_alcohol_occasion_limit)
            )
            user.daily_sugar_gram_target = int(goals.get("dailySugarGramTarget", user.daily_sugar_gram_target))
            user.updated_at = now_millis()
            db.session.commit()
        return jsonify(profile_json(user))

    @app.route("/api/users/<user_id>/records", methods=["GET", "POST"])
    def user_records(user_id: str) -> Any:
        ensure_user(user_id)
        if request.method == "GET":
            records = (
                IntakeRecord.query.filter_by(user_id=user_id)
                .order_by(IntakeRecord.timestamp_epoch_millis.desc())
                .all()
            )
            return jsonify({"records": [record_json(record) for record in records]})

        body = read_json_object()
        item_type = str(body.get("itemType", "")).upper()
        amount_fraction = max(0.0, min(1.0, float(body.get("amountFraction", 0.0))))
        note = str(body.get("note", ""))[:160]
        timestamp = int(body.get("timestampEpochMillis") or now_millis())
        record_id = str(body.get("id") or uuid.uuid4())

        item = CatalogItem.query.filter_by(type=item_type, enabled=1).first()
        if item is None:
            raise ValueError(f"Unknown or disabled item type: {item_type}")
        metrics = metrics_from_catalog(item, amount_fraction)
        record = IntakeRecord(
            id=record_id,
            user_id=user_id,
            item_type=item_type,
            amount_fraction=amount_fraction,
            timestamp_epoch_millis=timestamp,
            sugar_grams=metrics["sugarGrams"],
            alcohol_grams=metrics["alcoholGrams"],
            calories=metrics["calories"],
            money=metrics["money"],
            note=note,
            created_at=now_millis(),
        )
        db.session.add(record)
        db.session.commit()
        return jsonify(record_json(record)), HTTPStatus.CREATED

    @app.route("/api/admin/overview", methods=["GET"])
    @login_required
    def admin_overview() -> Any:
        user_id = request.args.get("userId", "")
        users = AppUser.query.order_by(AppUser.updated_at.desc()).limit(50).all()
        catalog = CatalogItem.query.order_by(text("rowid")).all()
        query = IntakeRecord.query.order_by(IntakeRecord.timestamp_epoch_millis.desc()).limit(100)
        if user_id:
            query = (
                IntakeRecord.query.filter_by(user_id=user_id)
                .order_by(IntakeRecord.timestamp_epoch_millis.desc())
                .limit(100)
            )
        records = query.all()
        return jsonify(
            {
                "users": [profile_json(user) for user in users],
                "catalog": [catalog_item_json(item) for item in catalog],
                "records": [record_json(record) | {"userId": record.user_id} for record in records],
            }
        )

    @app.route("/api/admin/catalog/<item_type>", methods=["PUT"])
    @login_required
    def update_catalog_item(item_type: str) -> Any:
        body = read_json_object()
        item = db.session.get(CatalogItem, item_type.upper())
        if item is None:
            raise ValueError(f"Unknown item type: {item_type.upper()}")

        item.display_name = str(body.get("displayName", item.display_name))
        item.family = str(body.get("family", item.family)).upper()
        item.full_serving_label = str(body.get("fullServingLabel", item.full_serving_label))
        item.full_serving_ml = int(body.get("fullServingMl", item.full_serving_ml))
        item.full_serving_sugar_grams = float(body.get("fullServingSugarGrams", item.full_serving_sugar_grams))
        item.full_serving_alcohol_grams = float(body.get("fullServingAlcoholGrams", item.full_serving_alcohol_grams))
        item.full_serving_calories = float(body.get("fullServingCalories", item.full_serving_calories))
        item.full_serving_money = float(body.get("fullServingMoney", item.full_serving_money))
        item.image_url = str(body.get("imageUrl", item.image_url))
        item.enabled = 1 if bool_from_json(body.get("enabled", item.enabled)) else 0
        item.updated_at = now_millis()
        db.session.commit()
        return jsonify(catalog_item_json(item))


def register_error_handlers(app: Flask) -> None:
    @app.errorhandler(ValueError)
    def handle_value_error(error: ValueError) -> Any:
        return jsonify({"error": str(error)}), HTTPStatus.BAD_REQUEST

    @app.errorhandler(IntegrityError)
    def handle_integrity_error(error: IntegrityError) -> Any:
        db.session.rollback()
        return jsonify({"error": f"Database constraint failed: {error.orig}"}), HTTPStatus.BAD_REQUEST

    @app.errorhandler(404)
    def handle_not_found(error: Any) -> Any:
        if request.path.startswith("/api/"):
            return jsonify({"error": "Route not found"}), HTTPStatus.NOT_FOUND
        return error

    @app.errorhandler(405)
    def handle_method_not_allowed(error: Any) -> Any:
        if request.path.startswith("/api/"):
            return jsonify({"error": "Method not allowed"}), HTTPStatus.METHOD_NOT_ALLOWED
        return error


def main() -> None:
    app = create_app()
    print(f"Elow backend running at http://{HOST}:{PORT}")
    print(f"Admin console: http://{HOST}:{PORT}/admin")
    print(f"SQLite database: {DB_PATH}")
    if os.environ.get("ELOW_ADMIN_PASSWORD") is None:
        print("Development admin: admin / admin123")
    app.run(host=HOST, port=PORT, threaded=True)


if __name__ == "__main__":
    main()
