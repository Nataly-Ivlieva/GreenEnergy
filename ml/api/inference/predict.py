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

ARTIFACTS_DIR = (
    Path(__file__).resolve().parents[2]
    / "artifacts"
)

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

def predict_expected_power_batch(records: list[Dict]) -> list[float]:
    if not records:
        return []
    
    results = [0.0] * len(records)

    df = pd.DataFrame(records)
    df["_idx"] = range(len(df))

    df = add_time_features(df)

    for gen_type, group in df.groupby("generatorType"):
        if gen_type not in MODELS:
            continue

        gdf = group.copy()
        gdf = add_physical_features(gdf, gen_type)

        gdf = gdf.drop(columns=["timestamp", "generatorType"], errors="ignore")
        gdf = gdf.fillna(0.0)

        model = MODELS[gen_type]["model"]
        features = MODELS[gen_type]["features"]

        for col in features:
            if col not in gdf.columns:
                gdf[col] = 0.0

        X = gdf[features]
        preds = model.predict(X)

        # post-processing
        for i, power in zip(gdf["_idx"], preds):
            rec = records[i]
            max_kw = rec.get("maxCapacityKw", np.inf)

            if gen_type == "SOLAR":
                if rec.get("solarIrradianceWm2", 0) < SOLAR_IRRADIANCE_MIN:
                    power = 0.0

            elif gen_type == "WIND":
                v = rec.get("windSpeedMs", 0)
                if v < WIND_CUT_IN or v > WIND_CUT_OUT:
                    power = 0.0

            power = max(0.0, min(power, max_kw))
            results[i] = round(float(power), 2)

    return results

# ======================================================
# LOCAL TEST
# ======================================================

if __name__ == "__main__":
    solar_batch = []
    for irr in [0, 50, 200, 500, 800, 1000]:
        solar_batch.append({
        "timestamp": 1770287359,
        "generatorType": "SOLAR",
        "maxCapacityKw": 2506.0,
        "temperatureC": 15,
        "windSpeedMs": 5,
        "solarIrradianceWm2": irr,
        "precipitationMm": 0.0,
        "cloudCover": 0.0,
    })

    print(predict_expected_power_batch(solar_batch))