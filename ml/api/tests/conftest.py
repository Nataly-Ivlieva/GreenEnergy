import os
import pytest
from fastapi.testclient import TestClient

# ----------------------------
# Dummy model
# ----------------------------
class DummyModel:
    def predict(self, X):
        return [100.0] * len(X)


# ----------------------------
# Mock MODELS before import
# ----------------------------
@pytest.fixture(autouse=True)
def mock_models(monkeypatch):
    from api.inference import predict

    monkeypatch.setattr(
        predict,
        "MODELS",
        {
            "SOLAR": {
                "model": DummyModel(),
                "features": ["temperatureC"],
            },
            "WIND": {
                "model": DummyModel(),
                "features": ["temperatureC"],
            },
            "HYDRO": {
                "model": DummyModel(),
                "features": ["temperatureC"],
            },
        },
    )


# ----------------------------
# API key setup
# ----------------------------
@pytest.fixture(scope="session", autouse=True)
def set_api_key_env():
    os.environ["ENERGY_API_KEY"] = "test-key"


@pytest.fixture
def client():
    from api.ml_api import app
    return TestClient(app)


@pytest.fixture
def valid_headers():
    return {"X-API-Key": "test-key"}


@pytest.fixture
def invalid_headers():
    return {"X-API-Key": "wrong-key"}
