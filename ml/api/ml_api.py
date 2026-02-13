import os
from typing import Dict
from dotenv import load_dotenv
from fastapi import FastAPI, Depends, HTTPException, Header
from pydantic import BaseModel
from inference.predict import predict_expected_power, predict_expected_power_batch
from pathlib import Path
from fastapi import Request

MODEL_PATH = (
    Path(__file__).resolve().parents[1]
    / "artifacts"
    / "SOLAR"
    / "model.pkl"
)
print(MODEL_PATH)
class ModelStatusResponse(BaseModel):
    trained: bool

class SinglePredictionRecord(BaseModel):
    timestamp: int
    generatorType: str
    maxCapacityKw: float
    temperatureC: float
    windSpeedMs: float
    solarIrradianceWm2: float
    precipitationMm: float
    cloudCover: float
    
class PredictionRequest(BaseModel):
    records: list[SinglePredictionRecord]

class BatchPredictionResponse(BaseModel):
    expectedPowerKw: list[float]

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


@app.post("/predict/batch",response_model=BatchPredictionResponse,
    dependencies=[Depends(verify_api_key)])
def predict_batch(records: list[Dict]):
        return BatchPredictionResponse(
        expectedPowerKw=predict_expected_power_batch(records)
    )

@app.get("/model/status", response_model=ModelStatusResponse)
def model_status():
    return ModelStatusResponse(trained=MODEL_PATH.exists())

@app.post("/predict/batch/debug", dependencies=[Depends(verify_api_key)])
async def predict_batch_debug(request: Request):
    body = await request.body()
    print("RAW BODY:", body.decode())
    return {"received": True}