import numpy as np
import pandas as pd

GENERATOR_FEATURES = {
    "SOLAR": {"solarIrradianceWm2", "cloudCover", "temperatureC"},
    "WIND": {"windSpeedMs", "temperatureC"},
    "HYDRO": {"precipitationMm", "temperatureC"},
}

def preprocess_train(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()

    # ---------- TARGET AS CAPACITY FACTOR ----------
    if "actualPowerKw" not in df.columns or "maxCapacityKw" not in df.columns:
        raise ValueError("actualPowerKw and maxCapacityKw are required")

    df["capacityFactor"] = (
        df["actualPowerKw"] / df["maxCapacityKw"]
    ).clip(0.0, 1.2)

    # ---------- TIME FEATURES ----------
    ts = pd.to_datetime(df["timestamp"], utc=True)

    df["hour_sin"] = np.sin(2 * np.pi * ts.dt.hour / 24)
    df["hour_cos"] = np.cos(2 * np.pi * ts.dt.hour / 24)

    df["day_sin"] = np.sin(2 * np.pi * ts.dt.dayofyear / 365)
    df["day_cos"] = np.cos(2 * np.pi * ts.dt.dayofyear / 365)

    # ---------- ONE-HOT GENERATOR TYPE ----------
    df = pd.get_dummies(df, columns=["generatorType"], prefix="generatorType")

    # ---------- DROP RAW ----------
    df = df.drop(
        columns=["timestamp", "actualPowerKw"],
        errors="ignore",
    )

    return df.replace([np.inf, -np.inf], np.nan).dropna()
