import schedule, time
from ingest.ingest_db import ingest_db
from build.build_train_dataset import *
from train.train_model import *

def daily_job():
    ingest_db()
    build_train_dataset()
    train_model()

schedule.every().day.at("17:00").do(daily_job)

if __name__ == "__main__":
    daily_job()
    while True:
        schedule.run_pending()
        time.sleep(60)
