# ingest/ingest_historical.py
from pathlib import Path
import json
import pandas as pd


def run_ingest():

    BASE_DIR = Path(__file__).resolve().parents[3]
    DATA_DIR = BASE_DIR / "data"

    JSONL_PATH = DATA_DIR / "historical_events.jsonl"
    RAW_DIR = DATA_DIR / "raw"
    RAW_DIR.mkdir(parents=True, exist_ok=True)

    CHUNK = 100_000
    part = 0
    rows = []

    with JSONL_PATH.open() as f:
        for line in f:
            rows.append(json.loads(line))
            if len(rows) >= CHUNK:
                pd.DataFrame(rows).to_parquet(
                    RAW_DIR / f"part-{part:05d}.parquet",
                    engine="pyarrow",
                    index=False,
                )
                rows.clear()
                part += 1

    if rows:
        pd.DataFrame(rows).to_parquet(
            RAW_DIR / f"part-{part:05d}.parquet",
            engine="pyarrow",
            index=False,
        )

    print("Historical ingest done")