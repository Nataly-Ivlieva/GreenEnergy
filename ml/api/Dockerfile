FROM python:3.11-slim

WORKDIR /app

COPY ml/api/requirements.txt ./ml/api/
RUN pip install --no-cache-dir -r ml/api/requirements.txt

COPY ml ./ml

WORKDIR /app/ml/api

EXPOSE 8000

CMD ["uvicorn", "ml_api:app", "--host", "0.0.0.0", "--port", "8000"]
