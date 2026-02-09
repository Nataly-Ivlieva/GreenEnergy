import os
from dotenv import load_dotenv
from fastapi import FastAPI, Depends, HTTPException, Header
from pydantic import BaseModel
from inference.predict import predict_expected_power

load_dotenv()

app = FastAPI(
    title="Energy ML Prediction API",
    version="1.0.0"
)

API_KEY = os.getenv("ENERGY_API_KEY")
API_KEY_HEADER = "X-API-Key"

if not API_KEY:
    raise RuntimeError("ENERGY_API_KEY is not set")


def verify_api_key(x_api_key: str = Header(...)):
    print("X-API-Key:", x_api_key)
    if x_api_key != API_KEY:
        raise HTTPException(status_code=401, detail="Invalid API key")


class PredictionResponse(BaseModel):
    expectedPowerKw: float


@app.get("/predict", response_model=PredictionResponse, dependencies=[Depends(verify_api_key)])
def predict(
    timestamp: int,
    generatorType: str,
    maxCapacityKw: float,
    temperatureC: float,
    windSpeedMs: float,
    solarIrradianceWm2: float,
    precipitationMm: float,
    cloudCover: float,
):
    expected = predict_expected_power(locals())
    return PredictionResponse(expectedPowerKw=expected)
