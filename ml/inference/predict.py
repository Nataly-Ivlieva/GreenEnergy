# predict.py
import json
from typing import Dict
from pathlib import Path

import joblib
import numpy as np
import pandas as pd

# ======================================================
# CONFIG
# ======================================================

ARTIFACTS_DIR = Path("artifacts")

SOLAR_IRRADIANCE_MIN = 20
WIND_CUT_IN = 2.5
WIND_CUT_OUT = 25.0

# ======================================================
# LOAD MODELS
# ======================================================

MODELS = {}

for gen_type in ["SOLAR", "WIND", "HYDRO"]:
    gen_dir = ARTIFACTS_DIR / gen_type

    model = joblib.load(gen_dir / "model.pkl")
    model.n_jobs = 1

    with open(gen_dir / "features.json") as f:
        features = json.load(f)

    MODELS[gen_type] = {
        "model": model,
        "features": features,
    }

# ======================================================
# FEATURE ENGINEERING (MUST MATCH TRAIN)
# ======================================================

def add_time_features(df: pd.DataFrame) -> pd.DataFrame:
    ts = pd.to_datetime(df["timestamp"], unit="s", utc=True)

    df["hour"] = ts.dt.hour
    df["dayofyear"] = ts.dt.dayofyear

    df["hour_sin"] = np.sin(2 * np.pi * df["hour"] / 24)
    df["hour_cos"] = np.cos(2 * np.pi * df["hour"] / 24)

    df["doy_sin"] = np.sin(2 * np.pi * df["dayofyear"] / 365)
    df["doy_cos"] = np.cos(2 * np.pi * df["dayofyear"] / 365)

    return df


def add_physical_features(df: pd.DataFrame, gen_type: str) -> pd.DataFrame:
    if gen_type == "SOLAR":
        df["is_night"] = (df["solarIrradianceWm2"] < SOLAR_IRRADIANCE_MIN).astype(int)
        df["irradiance_norm"] = df["solarIrradianceWm2"] / 1000.0

    elif gen_type == "WIND":
        df["below_cut_in"] = (df["windSpeedMs"] < WIND_CUT_IN).astype(int)
        df["wind_speed_sq"] = df["windSpeedMs"] ** 2

    elif gen_type == "HYDRO":
        df["is_freezing"] = (df["temperatureC"] <= -5).astype(int)
        df["is_drought"] = (df["precipitationMm"] <= 0.5).astype(int)

    return df

# ======================================================
# PREDICTION
# ======================================================

def predict_expected_power(record: Dict) -> float:
    gen_type = record.get("generatorType")

    if gen_type not in MODELS:
        raise ValueError(f"Unknown generatorType: {gen_type}")

    df = pd.DataFrame([record])

    df = add_time_features(df)
    df = add_physical_features(df, gen_type)

    # drop non-features
    df = df.drop(columns=["timestamp", "generatorType"], errors="ignore")
    df = df.fillna(0.0)

    model = MODELS[gen_type]["model"]
    features = MODELS[gen_type]["features"]

    # align columns
    for col in features:
        if col not in df.columns:
            df[col] = 0.0

    X = df[features]

    power = float(model.predict(X)[0])

    # ==================================================
    # HARD PHYSICAL RULES (POST-PROCESSING)
    # ==================================================

    max_kw = record.get("maxCapacityKw", np.inf)

    if gen_type == "SOLAR":
        if record.get("solarIrradianceWm2", 0) < SOLAR_IRRADIANCE_MIN:
            power = 0.0

    elif gen_type == "WIND":
        v = record.get("windSpeedMs", 0)
        if v < WIND_CUT_IN or v > WIND_CUT_OUT:
            power = 0.0

    power = max(0.0, min(power, max_kw))

    return round(power, 2)

# ======================================================
# LOCAL TEST
# ======================================================

if __name__ == "__main__":
    sample = {
        "timestamp": 1770287359,
        "generatorType": "SOLAR",
        "maxCapacityKw": 2506.0,
        "temperatureC": -10.5,
        "windSpeedMs": 14.2,
        "solarIrradianceWm2": 200.0,
        "precipitationMm": 0.0,
        "cloudCover": 0.0,
    }

    for irr in [0, 50, 200, 500, 800, 1000]:
        sample["solarIrradianceWm2"] = irr
        print("SOLAR",irr, predict_expected_power(sample))
    sample["generatorType"] = "WIND"
    for ws in [0, 1, 3, 10, 20, 30, 40, 50]:
        sample["windSpeedMs"] = ws
        print("WIND",ws, predict_expected_power(sample))
    sample["generatorType"] = "HYDRO"
    for temp in [-15, -5, 0, 10]:
        sample["temperatureC"] = temp
        print("HYDRO",temp, predict_expected_power(sample))