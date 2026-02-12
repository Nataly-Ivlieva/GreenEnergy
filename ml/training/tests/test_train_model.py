# tests/test_train_model.py
import pandas as pd
import pytest
from pathlib import Path
import numpy as np

import training.train.train_model as train_module


def make_fake_dataset(n=12000, gen_type="SOLAR"):
    return pd.DataFrame({
        "timestamp": pd.date_range("2024-01-01", periods=n, tz="UTC"),
        "generatorType": [gen_type] * n,
        "windSpeedMs": np.random.rand(n) * 10,
        "solarIrradianceWm2": np.random.rand(n) * 1000,
        "temperatureC": np.random.rand(n) * 30,
        "precipitationMm": np.random.rand(n),
        "cloudCover": np.random.rand(n),
        "maxCapacityKw": np.random.rand(n) * 1000,
        "actualPowerKw": np.random.rand(n) * 1000,
    })

def test_train_model_creates_artifacts(tmp_path, monkeypatch):
    
    train_path = tmp_path / "train.parquet"
    artifacts_dir = tmp_path / "artifacts"

    df = make_fake_dataset()
    df.to_parquet(train_path)

    monkeypatch.setattr(train_module, "TRAIN_PATH", train_path)
    monkeypatch.setattr(train_module, "ARTIFACTS_DIR", artifacts_dir)

    train_module.train_model()

    solar_dir = artifacts_dir / "SOLAR"

    assert (solar_dir / "model.pkl").exists()
    assert (solar_dir / "features.json").exists()
    assert (solar_dir / "meta.json").exists()
    assert (artifacts_dir / "meta.json").exists()


def test_train_model_skips_if_not_enough_data(tmp_path, monkeypatch):
    train_path = tmp_path / "train.parquet"
    artifacts_dir = tmp_path / "artifacts"

    df = make_fake_dataset(n=100)  # too small
    df.to_parquet(train_path)

    monkeypatch.setattr(train_module, "TRAIN_PATH", train_path)
    monkeypatch.setattr(train_module, "ARTIFACTS_DIR", artifacts_dir)

    train_module.train_model()

    assert not (artifacts_dir / "SOLAR").exists()
