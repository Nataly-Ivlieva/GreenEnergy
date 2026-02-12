# ingest/ingest_db.py
import os
import pandas as pd
from sqlalchemy import create_engine
from datetime import datetime, timedelta, UTC
from pathlib import Path
from dotenv import load_dotenv

load_dotenv()

BASE_DIR = Path(__file__).resolve().parents[3]
DATA_DIR = BASE_DIR / "data"
RAW_DIR = DATA_DIR/ "raw"

RAW_DIR.mkdir(parents=True, exist_ok=True)

DB_HOST = os.getenv("DB_HOST")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME")
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")

if not all([DB_HOST, DB_NAME, DB_USER, DB_PASSWORD]):
    raise RuntimeError("Database environment variables are not fully set")

DB_URL = (
    f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}"
    f"@{DB_HOST}:{DB_PORT}/{DB_NAME}"
)


def ingest_db():
    engine = create_engine(DB_URL)
    since = datetime.now(UTC) - timedelta(days=1)

    df = pd.read_sql(
        f"""
        SELECT
            e.timestamp,
            g.type AS generatorType,
            g.max_capacity_kw AS maxCapacityKw,
            e.actual_power_kw AS actualPowerKw,
            e.wind_speed_ms AS windSpeedMs,
            e.solar_irradiance_wm2 AS solarIrradianceWm2,
            e.temperaturec AS temperatureC,
            e.precipitation_mm AS precipitationMm,
            e.cloud_cover AS cloudCover
        FROM energy_measurements e
        JOIN generators g ON e.generator_id = g.id
        WHERE e.timestamp >= '{since.isoformat()}'
        """,
        engine,
    )

    if df.empty:
        return

    RENAME_MAP = {
        "windspeedms": "windSpeedMs",
        "solarirradiancewm2": "solarIrradianceWm2",
        "temperaturec": "temperatureC",
        "precipitationmm": "precipitationMm",
        "cloudcover": "cloudCover",
        "maxcapacitykw": "maxCapacityKw",
        "actualpowerkw": "actualPowerKw",
        "generatortype": "generatorType",
    }

    df.columns = [c.lower() for c in df.columns]
    df = df.rename(columns=RENAME_MAP)

    df["timestamp"] = pd.to_datetime(df["timestamp"], utc=True)

    path = RAW_DIR / f"part-{datetime.now(UTC):%Y%m%d_%H%M%S}.parquet"
    df.to_parquet(path, engine="pyarrow", index=False)
    print(f"Ingested {len(df)} rows → {path}")