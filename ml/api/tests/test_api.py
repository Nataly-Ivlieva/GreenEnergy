# ----------------------------
# /predict endpoint tests
# ----------------------------

def test_predict_success(client, valid_headers):
    response = client.get(
        "/predict",
        headers=valid_headers,
        params={
            "timestamp": 1770287359,
            "generatorType": "SOLAR",
            "maxCapacityKw": 200.0,
            "temperatureC": 20,
            "windSpeedMs": 5,
            "solarIrradianceWm2": 500,
            "precipitationMm": 0,
            "cloudCover": 0,
        },
    )

    assert response.status_code == 200
    assert "expectedPowerKw" in response.json()
    assert isinstance(response.json()["expectedPowerKw"], float)


def test_predict_invalid_api_key(client, invalid_headers):
    response = client.get(
        "/predict",
        headers=invalid_headers,
        params={
            "timestamp": 1,
            "generatorType": "SOLAR",
            "maxCapacityKw": 200,
            "temperatureC": 20,
            "windSpeedMs": 5,
            "solarIrradianceWm2": 500,
            "precipitationMm": 0,
            "cloudCover": 0,
        },
    )

    assert response.status_code == 401


def test_predict_validation_error(client, valid_headers):
    response = client.get(
        "/predict",
        headers=valid_headers,
        params={
            "timestamp": 1,
            "generatorType": "SOLAR",
        },
    )

    assert response.status_code == 422


# ----------------------------
# /predict/batch tests
# ----------------------------

def test_batch_success(client):
    response = client.post(
        "/predict/batch",
        json=[
            {
                "timestamp": 1770287359,
                "generatorType": "SOLAR",
                "maxCapacityKw": 200.0,
                "temperatureC": 20,
                "windSpeedMs": 5,
                "solarIrradianceWm2": 500,
                "precipitationMm": 0,
                "cloudCover": 0,
            }
        ],
    )

    assert response.status_code == 200
    data = response.json()
    assert "expectedPowerKw" in data
    assert isinstance(data["expectedPowerKw"], list)
    assert len(data["expectedPowerKw"]) == 1


def test_batch_empty(client):
    response = client.post("/predict/batch", json=[])

    assert response.status_code == 200
    assert response.json()["expectedPowerKw"] == []
