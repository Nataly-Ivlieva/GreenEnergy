 # train_model.py
import json
import joblib
import numpy as np
import pandas as pd
from pathlib import Path
from datetime import datetime, UTC

from sklearn.ensemble import HistGradientBoostingRegressor
from sklearn.metrics import mean_absolute_error, r2_score

from preprocess_train import preprocess_train

# ---------------- CONFIG ----------------

ARTIFACTS_DIR = Path("artifacts")
TRAIN_PATH = Path("../data/train.parquet")

GENERATOR_TYPES = ["SOLAR", "WIND", "HYDRO"]

TARGET = "actualPowerKw"

SOLAR_IRRADIANCE_MIN = 20
WIND_CUT_IN = 2.5
HYDRO_FREEZE_TEMP = -5
HYDRO_DROUGHT_MM = 0.5

# ---------------- FEATURES ----------------

def add_time_features(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()

    if "timestamp" not in df.columns:
        raise ValueError("timestamp column is required")

    ts = pd.to_datetime(df["timestamp"])

    df["hour"] = ts.dt.hour
    df["dayofyear"] = ts.dt.dayofyear

    df["hour_sin"] = np.sin(2 * np.pi * df["hour"] / 24)
    df["hour_cos"] = np.cos(2 * np.pi * df["hour"] / 24)

    df["doy_sin"] = np.sin(2 * np.pi * df["dayofyear"] / 365)
    df["doy_cos"] = np.cos(2 * np.pi * df["dayofyear"] / 365)

    return df


def add_physical_features(df: pd.DataFrame, gen_type: str) -> pd.DataFrame:
    df = df.copy()

    if gen_type == "SOLAR":
        df["is_night"] = (df["solarIrradianceWm2"] < SOLAR_IRRADIANCE_MIN).astype(int)
        df["irradiance_norm"] = df["solarIrradianceWm2"] / 1000.0

    elif gen_type == "WIND":
        df["below_cut_in"] = (df["windSpeedMs"] < WIND_CUT_IN).astype(int)
        df["wind_speed_sq"] = df["windSpeedMs"] ** 2

    elif gen_type == "HYDRO":
        df["is_freezing"] = (df["temperatureC"] <= HYDRO_FREEZE_TEMP).astype(int)
        df["is_drought"] = (df["precipitationMm"] <= HYDRO_DROUGHT_MM).astype(int)

    return df


# ---------------- TRAIN ----------------

def train_model():
    df = pd.read_parquet(TRAIN_PATH)

    all_meta = {
        "rows": int(len(df)),
        "by_generator_type": {},
        "trained_at": datetime.now(UTC).isoformat(),
    }

    for gen_type in GENERATOR_TYPES:
        print(f"\n=== Training {gen_type} model ===")

        df_g = df[df["generatorType"] == gen_type].copy()

        if len(df_g) < 10_000:
            print(f"Skip {gen_type}: not enough data")
            continue

        # --- TIME SPLIT ---
        df_g["timestamp"] = pd.to_datetime(df_g["timestamp"])
        split_ts = df_g["timestamp"].quantile(0.8)

        train = df_g[df_g["timestamp"] <= split_ts].copy()
        test  = df_g[df_g["timestamp"] > split_ts].copy()

        # --- FEATURES ---
        for part in (train, test):
            part = add_time_features(part)
            part = preprocess_train(part)
            part = add_physical_features(part, gen_type)

        # --- CLEAN ---
        train = train.replace([np.inf, -np.inf], np.nan).dropna()
        test  = test.replace([np.inf, -np.inf], np.nan).dropna()

        DROP_COLS = [
            "timestamp",
            "generatorType",
        ]

        X_train = train.drop(columns=[TARGET] + DROP_COLS)
        y_train = train[TARGET]

        X_test = test.drop(columns=[TARGET] + DROP_COLS)
        y_test = test[TARGET]

        # --- MODEL ---
        model = HistGradientBoostingRegressor(
            max_depth=6,
            max_iter=800,
            learning_rate=0.03,
            min_samples_leaf=200,
            random_state=42,
        )

        model.fit(X_train, y_train)

        # --- METRICS ---
        y_pred = model.predict(X_test)

        mae = float(mean_absolute_error(y_test, y_pred))
        r2 = float(r2_score(y_test, y_pred))

        # --- BASELINE (capacity only) ---
        baseline_pred = test["maxCapacityKw"]
        baseline_r2 = float(r2_score(y_test, baseline_pred))

        print(f"Baseline R2 (mean power per generator): {baseline_r2:.4f}")

        # --- SAVE ---
        out_dir = ARTIFACTS_DIR / gen_type
        out_dir.mkdir(parents=True, exist_ok=True)

        joblib.dump(model, out_dir / "model.pkl")

        with open(out_dir / "features.json", "w") as f:
            json.dump(list(X_train.columns), f, indent=2)

        with open(out_dir / "meta.json", "w") as f:
            json.dump(
                {
                    "rows": int(len(df_g)),
                    "mae_kw": mae,
                    "r2": r2,
                    "baseline_r2": baseline_r2,
                    "trained_at": datetime.now(UTC).isoformat(),
                },
                f,
                indent=2,
            )

        all_meta["by_generator_type"][gen_type] = {
            "rows": int(len(df_g)),
            "mae_kw": mae,
            "r2": r2,
            "baseline_r2": baseline_r2,
        }

        print(
            f"{gen_type}: rows={len(df_g):,}, "
            f"MAE(kW)={mae:.2f}, R2={r2:.4f}"
        )

    with open(ARTIFACTS_DIR / "meta.json", "w") as f:
        json.dump(all_meta, f, indent=2)

    print("\nAll generator models trained")


if __name__ == "__main__":
    train_model()
