# ingest/ingest_historical.py
import json
import pandas as pd
from pathlib import Path

JSONL_PATH = Path("../data/historical_events.jsonl")
RAW_DIR = Path("../data/raw")
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
