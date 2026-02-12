# tests/test_build_dataset.py
import pandas as pd
import pytest
import training.build.build_train_dataset as build_module


def test_build_train_dataset_success(tmp_path, monkeypatch):
    raw_dir = tmp_path / "raw"
    raw_dir.mkdir()

    train_path = tmp_path / "train.parquet"

    monkeypatch.setattr(build_module, "RAW_DIR", raw_dir)
    monkeypatch.setattr(build_module, "TRAIN_PATH", train_path)

    df = pd.DataFrame({
        "timestamp": pd.date_range("2024-01-01", periods=10, tz="UTC"),
        "generatorType": ["SOLAR"] * 10,
        "windSpeedMs": [5] * 10,
        "solarIrradianceWm2": [500] * 10,
        "temperatureC": [20] * 10,
        "precipitationMm": [0] * 10,
        "cloudCover": [0] * 10,
        "maxCapacityKw": [100] * 10,
        "actualPowerKw": [50] * 10,
    })

    df.to_parquet(raw_dir / "part-test.parquet")

    build_module.build_train_dataset()

    assert train_path.exists()


def test_build_train_dataset_no_files(tmp_path, monkeypatch):
    raw_dir = tmp_path / "raw"
    raw_dir.mkdir()

    monkeypatch.setattr(build_module, "RAW_DIR", raw_dir)

    with pytest.raises(RuntimeError):
        build_module.build_train_dataset()
