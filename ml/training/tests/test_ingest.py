# tests/test_ingest.py
import pandas as pd
import pytest
from pathlib import Path
from unittest.mock import patch

import training.ingest.ingest_db as ingest_module


def test_ingest_writes_parquet(tmp_path, monkeypatch):
    # redirect RAW_DIR
    monkeypatch.setattr(ingest_module, "RAW_DIR", tmp_path)

    fake_df = pd.DataFrame({
        "timestamp": pd.date_range("2024-01-01", periods=5, tz="UTC"),
        "generatorType": ["SOLAR"] * 5,
        "maxCapacityKw": [100] * 5,
        "actualPowerKw": [50] * 5,
        "windSpeedMs": [5] * 5,
        "solarIrradianceWm2": [500] * 5,
        "temperatureC": [20] * 5,
        "precipitationMm": [0] * 5,
        "cloudCover": [0] * 5,
    })

    with patch("training.ingest.ingest_db.create_engine"), \
         patch("training.ingest.ingest_db.pd.read_sql", return_value=fake_df):

        ingest_module.ingest_db()

    files = list(tmp_path.glob("part-*.parquet"))
    assert len(files) == 1


def test_ingest_empty_df(tmp_path, monkeypatch):
    monkeypatch.setattr(ingest_module, "RAW_DIR", tmp_path)

    empty_df = pd.DataFrame()

    with patch("training.ingest.ingest_db.create_engine"), \
         patch("training.ingest.ingest_db.pd.read_sql", return_value=empty_df):

        ingest_module.ingest_db()

    assert not list(tmp_path.glob("*.parquet"))
