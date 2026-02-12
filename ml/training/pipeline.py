import schedule
import time
from pathlib import Path

from ingest.ingest_db import ingest_db
from build.build_train_dataset import build_train_dataset
from train.train_model import train_model
from ingest.ingest_historical import run_ingest

MODEL_PATH_SOLAR = (
    Path(__file__).resolve().parents[1]
    / "artifacts"
    / "SOLAR"
    / "model.pkl"
)


def initial_training():
    print("Running initial historical ingestion and training...")
    run_ingest()
    build_train_dataset()
    train_model()
    print("Initial training completed.")


def daily_job():
    print("Running scheduled daily training...")
    ingest_db()
    build_train_dataset()
    train_model()
    print("Daily training completed.")


schedule.every().day.at("13:18").do(daily_job)


if __name__ == "__main__":

    if not MODEL_PATH_SOLAR.exists():
        initial_training()
    else:
        print("Model already exists. Skipping initial training.")

    while True:
        schedule.run_pending()
        time.sleep(60)
