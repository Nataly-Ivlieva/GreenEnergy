from unittest.mock import patch
from training.pipeline import daily_job

def test_daily_job_calls_all_steps():
    with patch("training.pipeline.ingest_db") as mock_ingest, \
         patch("training.pipeline.build_train_dataset") as mock_build, \
         patch("training.pipeline.train_model") as mock_train:

        daily_job()

        mock_ingest.assert_called_once()
        mock_build.assert_called_once()
        mock_train.assert_called_once()
