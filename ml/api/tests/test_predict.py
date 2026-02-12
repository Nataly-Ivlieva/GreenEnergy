import pytest
from api.inference.predict import (
    predict_expected_power,
    predict_expected_power_batch,
)


# ----------------------------
# Single prediction tests
# ----------------------------

def base_record(gen_type="SOLAR"):
    return {
        "timestamp": 1770287359,
        "generatorType": gen_type,
        "maxCapacityKw": 200.0,
        "temperatureC": 20,
        "windSpeedMs": 5,
        "solarIrradianceWm2": 500,
        "precipitationMm": 0,
        "cloudCover": 0,
    }


def test_unknown_generator():
    record = base_record("UNKNOWN")

    with pytest.raises(ValueError):
        predict_expected_power(record)


def test_solar_night_zero():
    record = base_record("SOLAR")
    record["solarIrradianceWm2"] = 0

    result = predict_expected_power(record)
    assert result == 0.0


def test_wind_cut_in_zero():
    record = base_record("WIND")
    record["windSpeedMs"] = 1.0  # ниже cut-in

    result = predict_expected_power(record)
    assert result == 0.0


def test_max_capacity_limit():
    record = base_record("SOLAR")
    record["maxCapacityKw"] = 50.0

    result = predict_expected_power(record)
    assert result <= 50.0


# ----------------------------
# Batch tests
# ----------------------------

def test_batch_mixed_generators():
    records = [
        base_record("SOLAR"),
        base_record("WIND"),
        base_record("HYDRO"),
    ]

    results = predict_expected_power_batch(records)

    assert isinstance(results, list)
    assert len(results) == 3
    assert all(isinstance(r, float) for r in results)
